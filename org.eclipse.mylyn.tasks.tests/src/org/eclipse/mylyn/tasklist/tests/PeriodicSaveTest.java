package org.eclipse.mylar.tasklist.tests;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.TaskListManager;
import org.eclipse.mylar.tasklist.internal.SaveTimer;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;

/**
 * Tests the mechanism for saving the task data periodically.
 * 
 * If this test fails unexpectedly, try adjusting the timing.
 * 
 * @author Wesley Coelho
 */
public class PeriodicSaveTest extends TestCase {

	private ITask task1 = null;

	private TaskListManager manager = MylarTasklistPlugin.getTaskListManager();

	private SaveTimer saveTimer = null;

	private int saveTimerInterval = -1;

	protected void setUp() throws Exception {
		super.setUp();

		// Create a task and context with an interaction event to be saved
		task1 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "Periodic Save Task", true);
		manager.addRootTask(task1);
		MylarContext mockContext = MylarPlugin.getContextManager().loadContext(task1.getHandleIdentifier(),
				task1.getPath());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT, "structureKind", "handle", "originId");
		mockContext.parseEvent(event);
		MylarPlugin.getContextManager().contextActivated(mockContext);

		// Save the context file and check that it exists
		MylarPlugin.getContextManager().saveContext(mockContext.getId(), task1.getPath());
		File taskFile = new File(MylarPlugin.getDefault().getMylarDataDirectory() + File.separator + task1.getPath()
				+ MylarContextManager.FILE_EXTENSION);
		assertTrue(MylarPlugin.getContextManager().hasContext(task1.getPath()));
		assertTrue(taskFile.exists());

		// Make the periodic save run by setting its interval very small and
		// waiting
		saveTimer = MylarTasklistPlugin.getDefault().getSaveTimer();
		saveTimerInterval = saveTimer.getSaveIntervalMillis();
	}

	public void testPeriodicSave() {

		// Activate the task
		(new TaskActivateAction()).run(task1);

		// Get the task from disk and read it's time
		ITask diskTask = getTaskFromDisk(task1);
		assertTrue(diskTask != null);

		long elapsedTimeBeforeSave = diskTask.getElapsedMillis();

		saveTimer.setSaveIntervalMillis(500);

		try {
			Thread.sleep(900); // Wait long enough that the save request should
								// have fired
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		saveTimer.setSaveIntervalMillis(600000); // Prevent more save
													// requests by setting long
													// interval

		try {
			Thread.sleep(1000); // Fudge time to allow disk write to finish
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Read the task from disk and check that it has a greater time
		diskTask = getTaskFromDisk(task1);
		assertTrue(diskTask.getElapsedMillis() > elapsedTimeBeforeSave);

	}

	public ITask getTaskFromDisk(ITask requestedTask) {

		File taskListFile = new File(MylarPlugin.getDefault().getMylarDataDirectory() + File.separator
				+ MylarTasklistPlugin.DEFAULT_TASK_LIST_FILE);

		assertTrue(taskListFile.exists());

		TaskList tasklist = new TaskList();
		MylarTasklistPlugin.getDefault().getTaskListExternalizer().readTaskList(tasklist, taskListFile);

		return tasklist.getTaskForHandle(requestedTask.getHandleIdentifier(), false);

	}

	protected void tearDown() throws Exception {

		saveTimer.setSaveIntervalMillis(saveTimerInterval);

		super.tearDown();
	}

}
