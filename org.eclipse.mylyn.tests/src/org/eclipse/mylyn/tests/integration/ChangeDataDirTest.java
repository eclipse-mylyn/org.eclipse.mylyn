/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tests.integration;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.Task;
import org.eclipse.mylar.internal.tasklist.TaskListManager;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;

/**
 * Tests changes to the main mylar data directory location.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (rewrites)
 */
public class ChangeDataDirTest extends TestCase {

	private String newDataDir = null;

	private final String defaultDir = MylarPlugin.getDefault().getDefaultDataDirectory();

	private TaskListManager manager = MylarTaskListPlugin.getTaskListManager();

	protected void setUp() throws Exception {
		super.setUp();

		newDataDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ChangeDataDirTest.class.getSimpleName();
		File dir = new File(newDataDir);
		dir.mkdir();
		dir.deleteOnExit();
		MylarTaskListPlugin.getTaskListManager().createNewTaskList();
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		MylarPlugin.getDefault().setDataDirectory(defaultDir);
	}

	public void testMonitorFileMove() {
		MylarMonitorPlugin.getDefault().startMonitoring();
		MylarMonitorPlugin.getDefault().getInteractionLogger().interactionObserved(
				InteractionEvent.makeCommand("id", "delta"));
		String oldPath = MylarMonitorPlugin.getDefault().getInteractionLogger().getOutputFile().getAbsolutePath();
		assertTrue(new File(oldPath).exists());

		MylarPlugin.getDefault().setDataDirectory(newDataDir);

		assertFalse(new File(oldPath).exists());
		String newPath = MylarMonitorPlugin.getDefault().getInteractionLogger().getOutputFile().getAbsolutePath();
		assertTrue(new File(newPath).exists());

		assertTrue(MylarMonitorPlugin.getDefault().getInteractionLogger().getOutputFile().exists());
		String monitorFileName = MylarMonitorPlugin.MONITOR_LOG_NAME + MylarContextManager.CONTEXT_FILE_EXTENSION;
		List<String> newFiles = Arrays.asList(new File(newDataDir).list());
		assertTrue(newFiles.toString(), newFiles.contains(monitorFileName));

		List<String> filesLeft = Arrays.asList(new File(defaultDir).list());
		assertFalse(filesLeft.toString(), filesLeft.contains(monitorFileName));
		MylarMonitorPlugin.getDefault().stopMonitoring();
	}

	public void testDefaultDataDirectoryMove() {
		String workspaceRelativeDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ".mylar";
		assertTrue(defaultDir.equals(workspaceRelativeDir));

		MylarPlugin.getDefault().setDataDirectory(newDataDir);
		assertTrue(MylarPlugin.getDefault().getDataDirectory().equals(newDataDir));
	}

	public void testTaskMove() {
		String handle = "task-1";
		ITask task = new Task(handle, "label", true);
		manager.moveToRoot(task);

		ITask readTaskBeforeMove = manager.getTaskForHandle(handle, true);
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().copyDataDirContentsTo(newDataDir);
		MylarPlugin.getDefault().setDataDirectory(newDataDir);

		ITask readTaskAfterMove = manager.getTaskForHandle(handle, true);
		assertNotNull(readTaskAfterMove);
		assertEquals(readTaskBeforeMove.getCreationDate(), readTaskAfterMove.getCreationDate());
	}

	public void testBugzillaTaskMove() {
		String handle = TaskRepositoryManager.getHandle("server", 1);
		BugzillaTask bugzillaTask = new BugzillaTask(handle, "bug1", true);
		addBugzillaTask(bugzillaTask);
		Date refreshDate = new Date();
		bugzillaTask.setLastRefresh(refreshDate);

		BugzillaTask readTaskBeforeMove = (BugzillaTask) manager.getTaskForHandle(handle, true);
		assertNotNull(readTaskBeforeMove);
		assertEquals(refreshDate, readTaskBeforeMove.getLastRefresh());

		MylarTaskListPlugin.getDefault().getTaskListSaveManager().copyDataDirContentsTo(newDataDir);
		MylarPlugin.getDefault().setDataDirectory(newDataDir);

		BugzillaTask readTaskAfterMove = (BugzillaTask) manager.getTaskForHandle(handle, true);
		assertNotNull(readTaskAfterMove);
		// HACK: shoudl be checking date equality, but millis seem to differ?
		assertEquals(refreshDate.toString(), readTaskAfterMove.getLastRefresh().toString());
	}

	private void addBugzillaTask(BugzillaTask newTask) {
//		AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
//		client.addTaskToArchive(newTask);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTaskToArchive(newTask);
//		BugzillaTaskHandler handler = new BugzillaTaskHandler();
//		handler.addTaskToArchive(newTask);
		MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);
	}

	// /**
	// * Tests moving the main mylar data directory to another location (Without
	// * copying existing data to the new directory)
	// */
	// public void testChangeMainDataDir() {
	//
	// ITask mainDataDirTask = createAndSaveTask("Main Task", false);
	//
	// // MylarPlugin.getDefault().setDataDirectory(newDataDir);
	// assertEquals(0, manager.getTaskList().getRootTasks().size());
	//
	// // Check that the main data dir task isn't in the list or the folder
	// File taskFile = new File(MylarPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertFalse(taskFile.exists());
	// assertNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	//
	// // Check that a newly created task appears in the right place (method
	// // will check)
	// ITask newDataDirTask = createAndSaveTask("New Data Dir", false);
	// taskFile = new File(MylarPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + newDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertTrue(taskFile.exists());
	//
	// // Check for other the tasklist file in the new dir
	// File destTaskListFile = new
	// File(MylarPlugin.getDefault().getDataDirectory() + File.separator
	// + MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);
	// assertTrue(destTaskListFile.exists());
	//
	// // Switch back to the main task directory
	// MylarPlugin.getDefault().setDataDirectory(MylarPlugin.getDefault().getDefaultDataDirectory());
	//		
	// // Check that the previously created main dir task is in the task list
	// and its file exists
	// assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	// taskFile = new File(MylarPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertTrue(taskFile.exists());
	//
	// // Check that the task created in the "New Data Dir" isn't there now
	// // that we're back to the main dir
	// assertNull(manager.getTaskForHandle(newDataDirTask.getHandleIdentifier(),
	// false));
	//
	// }
	//
	// /**
	// * Creates a task with an interaction event and checks that it has been
	// * properly saved in the currently active data directory
	// */
	// protected ITask createAndSaveTask(String taskName, boolean
	// createBugzillaTask) {
	//
	// // Create the task and add it to the root of the task list
	// BugzillaTask newTask = null;
	// if (!createBugzillaTask) {
	// String handle =
	// MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle();
	// newTask = new BugzillaTask(handle, "bug1", true, true);//new
	// Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(),
	// taskName, true);
	// manager.moveToRoot(newTask);
	// } else {
	// newTask = new
	// BugzillaTask(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(),
	// taskName, true,
	// true);
	// addBugzillaTask(newTask);
	// }
	//
	// MylarContext mockContext =
	// MylarPlugin.getContextManager().loadContext(newTask.getHandleIdentifier(),
	// newTask.getContextPath());
	// InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT,
	// "structureKind", "handle", "originId");
	// mockContext.parseEvent(event);
	// MylarPlugin.getContextManager().contextActivated(mockContext);
	//
	// // Save the context file and check that it exists
	// MylarPlugin.getContextManager().saveContext(mockContext.getId(),
	// newTask.getContextPath());
	// File taskFile = new File(MylarPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + newTask.getContextPath() + MylarContextManager.CONTEXT_FILE_EXTENSION);
	// assertTrue(MylarPlugin.getContextManager().hasContext(newTask.getContextPath()));
	// assertTrue(taskFile.exists());
	//
	// return newTask;
	// }
	//	
	// /**
	// * Same as above but using bugzilla tasks Tests moving the main mylar data
	// * directory to another location (Without copying existing data to the new
	// * directory)
	// */
	// public void testChangeMainDataDirBugzilla() {
	//
	// // Create a task in the main dir and context with an interaction event
	// // to be saved
	// ITask mainDataDirTask = createAndSaveTask("Main Task", true);
	//
	// // Set time to see if the right task data is returned by the registry
	// // mechanism
	// mainDataDirTask.setElapsedTime(ELAPSED_TIME1);
	//
	// // Save tasklist
	// MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
	//
	// // Temp check that the task is there
	// assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	//
	// // Switch task directory
	// MylarPlugin.getDefault().setDataDirectory(newDataDir);
	//
	// // Check that there are no tasks in the tasklist after switching to the
	// // empty dir
	// assertTrue(manager.getTaskList().getRootTasks().size() == 0);
	//
	// // Check that the main data dir task isn't in the list or the folder
	// File taskFile = new File(MylarPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertFalse(taskFile.exists());
	// assertNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	//
	// // Check that a newly created task appears in the right place (method
	// // will check)
	// ITask newDataDirTask = createAndSaveTask("New Data Dir", true);
	// taskFile = new File(MylarPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + newDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertTrue(taskFile.exists());
	//
	// // Check for tasklist file in the new dir
	// File destTaskListFile = new
	// File(MylarPlugin.getDefault().getDataDirectory() + File.separator
	// + MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);
	// assertTrue(destTaskListFile.exists());
	//
	// MylarPlugin.getDefault().setDataDirectory(MylarPlugin.getDefault().getDefaultDataDirectory());
	//
	// // Check that the previously created main dir task is in the task list
	// // and its file exists
	// assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	// taskFile = new File(MylarPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertTrue(taskFile.exists());
	//
	// // Check that the elapsed time is still right
	// assertTrue(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false).getElapsedTime() == ELAPSED_TIME1);
	//
	// // Check that the task created in the "New Data Dir" isn't there now
	// // that we're back to the main dir
	// assertNull(manager.getTaskForHandle(newDataDirTask.getHandleIdentifier(),
	// false));
	// }
}
