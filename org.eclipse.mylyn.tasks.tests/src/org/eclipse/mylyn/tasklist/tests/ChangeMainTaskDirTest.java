package org.eclipse.mylar.tasklist.tests;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask.BugTaskState;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.TaskListManager;

/**
 * Tests changes to the main mylar data directory location.
 * 
 * @author Wesley Coelho
 */
public class ChangeMainTaskDirTest extends TestCase {

	private static final int ELAPSED_TIME1 = 123456;

	private static final int ELAPSED_TIME2 = 999999;

	private File newMainDataDir = null;

	private String originalMainDataDir = null;

	private TaskListManager manager = MylarTaskListPlugin.getTaskListManager();

	private String defaultDirectory = MylarPlugin.getDefault().getDataDirectory();

	protected void setUp() throws Exception {
		super.setUp();

		// Get the original main data directory so that it can be reset later
		originalMainDataDir = MylarPlugin.getDefault().getDataDirectory();

		// Create test main data directories to use
		newMainDataDir = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator + "TestMainDir1");
		newMainDataDir.mkdir();
		assertTrue(newMainDataDir.exists());

	}

	public void testDefaultDataDirectoryLocation() {

		String newDirectory = defaultDirectory + File.separator + "TestDir";

		// Check that default directory is correct
		assertTrue("Wrong default directory returned", defaultDirectory.equals(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ File.separator + ".mylar"));

		// Set the directory to something else
		MylarPlugin.getDefault().setDataDirectory(newDirectory);

		// Check that the change was made
		assertTrue("Wrong mylar directory returned", MylarPlugin.getDefault().getDataDirectory().equals(newDirectory));

	}

	/**
	 * Tests moving the main mylar data directory to another location (Without
	 * copying existing data to the new directory)
	 */
	public void testChangeMainDataDir() {

		// Create a task in the main dir and context with an interaction event
		// to be saved
		ITask mainDataDirTask = createAndSaveTask("Main Task", false);

		// Switch task directory
		switchMainTaskDirectory(newMainDataDir.getPath());

		// Check that there are no tasks in the tasklist after switching to the
		// empty dir
		assertTrue(manager.getTaskList().getRootTasks().size() == 0);

		// Check that the main data dir task isn't in the list or the folder
		File taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertFalse(taskFile.exists());
		assertNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(), false));

		// Check that a newly created task appears in the right place (method
		// will check)
		ITask newDataDirTask = createAndSaveTask("New Data Dir", false);
		taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ newDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertTrue(taskFile.exists());

		// Check for other the tasklist file in the new dir
		File destTaskListFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);
		assertTrue(destTaskListFile.exists());

		// Switch back to the main task directory
		switchMainTaskDirectory(originalMainDataDir);

		// Check that the previously created main dir task is in the task list
		// and its file exists
		assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(), false));
		taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertTrue(taskFile.exists());

		// Check that the task created in the "New Data Dir" isn't there now
		// that we're back to the main dir
		assertNull(manager.getTaskForHandle(newDataDirTask.getHandleIdentifier(), false));

	}

	/**
	 * Same as above but using bugzilla tasks Tests moving the main mylar data
	 * directory to another location (Without copying existing data to the new
	 * directory)
	 */
	public void testChangeMainDataDirBugzilla() {

		// Create a task in the main dir and context with an interaction event
		// to be saved
		ITask mainDataDirTask = createAndSaveTask("Main Task", true);

		// Set time to see if the right task data is returned by the registry
		// mechanism
		mainDataDirTask.setElapsedTime(ELAPSED_TIME1);

		// Save tasklist
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();

		// Temp check that the task is there
		assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(), false));

		// Switch task directory
		switchMainTaskDirectory(newMainDataDir.getPath());

		// Check that there are no tasks in the tasklist after switching to the
		// empty dir
		assertTrue(manager.getTaskList().getRootTasks().size() == 0);

		// Check that the main data dir task isn't in the list or the folder
		File taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertFalse(taskFile.exists());
		assertNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(), false));

		// Check that a newly created task appears in the right place (method
		// will check)
		ITask newDataDirTask = createAndSaveTask("New Data Dir", true);
		taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ newDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertTrue(taskFile.exists());

		// Save tasklist
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();

		// Check for tasklist file in the new dir
		File destTaskListFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);
		assertTrue(destTaskListFile.exists());

		// Switch back to the main task directory
		switchMainTaskDirectory(originalMainDataDir);

		// Check that the previously created main dir task is in the task list
		// and its file exists
		assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(), false));
		taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
		assertTrue(taskFile.exists());

		// Check that the elapsed time is still right
		assertTrue(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(), false).getElapsedTime() == ELAPSED_TIME1);

		// Check that the task created in the "New Data Dir" isn't there now
		// that we're back to the main dir
		assertNull(manager.getTaskForHandle(newDataDirTask.getHandleIdentifier(), false));

	}

	/**
	 * Tests for bugzilla registry problem where the registry entry has outdated
	 * fields that don't match the corresponding task
	 */
	public void testBugzillaRegistry() {

		int commonBugID = 123;

		// Create a task in the main dir and context with an interaction event
		// to be saved
		ITask bugzillaTask1 = new BugzillaTask(BugzillaUiPlugin.getDefault().createBugHandleIdentifier(commonBugID),
				"BugZTask", true, true);
		addBugzillaTask(bugzillaTask1);

		// Set time to see if the right task data is returned by the registry
		// mechanism
		bugzillaTask1.setElapsedTime(ELAPSED_TIME1);

		// Switch task directory
		switchMainTaskDirectory(newMainDataDir.getPath());

		// Create a new bugzilla task with the same bug id
		ITask bugzillaTask2 = new BugzillaTask(BugzillaUiPlugin.getDefault().createBugHandleIdentifier(commonBugID),
				"BugZTask", true, true);
		addBugzillaTask(bugzillaTask1);

		// Set a time that's different from the other task
		bugzillaTask2.setElapsedTime(ELAPSED_TIME2);

		// Switch back to the main task directory
		switchMainTaskDirectory(originalMainDataDir);

		// Check that the elapsed time is still right
		assertTrue(manager.getTaskForHandle(bugzillaTask1.getHandleIdentifier(), false).getElapsedTime() == ELAPSED_TIME1);

		// Switch back to the other task directory
		switchMainTaskDirectory(newMainDataDir.getPath());

		// Check that the elapsed time is still right
		assertTrue("Task has wrong time", manager.getTaskForHandle(bugzillaTask2.getHandleIdentifier(), false)
				.getElapsedTime() == ELAPSED_TIME2);

	}

	/**
	 * Creates a task with an interaction event and checks that it has been
	 * properly saved in the currently active data directory
	 */
	protected ITask createAndSaveTask(String taskName, boolean createBugzillaTask) {

		// Create the task and add it to the root of the task list
		ITask newTask = null;
		if (!createBugzillaTask) {
			newTask = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), taskName, true);
			manager.moveToRoot(newTask);
		} else {
			newTask = new BugzillaTask(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), taskName, true,
					true);
			addBugzillaTask(newTask);
		}

		MylarContext mockContext = MylarPlugin.getContextManager().loadContext(newTask.getHandleIdentifier(),
				newTask.getContextPath());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT, "structureKind", "handle", "originId");
		mockContext.parseEvent(event);
		MylarPlugin.getContextManager().contextActivated(mockContext);

		// Save the context file and check that it exists
		MylarPlugin.getContextManager().saveContext(mockContext.getId(), newTask.getContextPath());
		File taskFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ newTask.getContextPath() + MylarContextManager.FILE_EXTENSION);
		assertTrue(MylarPlugin.getContextManager().hasContext(newTask.getContextPath()));
		assertTrue(taskFile.exists());

		return newTask;
	}

	private void addBugzillaTask(ITask newTask) {

		ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(newTask);
		if (taskHandler != null) {
			ITask addedTask = taskHandler.taskAdded(newTask);
			if (addedTask instanceof BugzillaTask) {
				BugzillaTask newTask2 = (BugzillaTask) addedTask;
				if (newTask2 == newTask) {
					((BugzillaTask) newTask).scheduleDownloadReport();
				} else {
					newTask = newTask2;
					((BugzillaTask) newTask).updateTaskDetails();
				}
			}
		} else {
			((BugzillaTask) newTask).scheduleDownloadReport();
		}

		MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);

		BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask) newTask);

		try {
			while (((BugzillaTask) newTask).getState() != BugTaskState.FREE) {
				Thread.sleep(250);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RuntimeException re) {
			re.printStackTrace();
		}
	}

	/**
	 * Copy (almost) of code that changes the task directory
	 * (MylarTaskListPreferencePage)
	 */
	protected void switchMainTaskDirectory(String newDir) {

		// Order matters:
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
		MylarPlugin.getDefault().getPreferenceStore().setValue(MylarPlugin.PREF_DATA_DIR, newDir);
		MylarTaskListPlugin.getDefault().setDataDirectory(MylarPlugin.getDefault().getDataDirectory());
	}

	protected void tearDown() throws Exception {

		MylarPlugin.getDefault().setDataDirectory(defaultDirectory);

		// Delete the test destination folder

		File[] files = newMainDataDir.listFiles();
		for (File file : files) {
			file.delete();
		}

		newMainDataDir.delete();
		assertFalse(newMainDataDir.exists());

		super.tearDown();
	}
}
