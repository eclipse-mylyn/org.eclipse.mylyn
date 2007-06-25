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
package org.eclipse.mylyn.tests.integration;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Tests changes to the main mylar data directory location.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (rewrites)
 */
public class ChangeDataDirTest extends TestCase {

	private String newDataDir = null;

	private final String defaultDir = TasksUiPlugin.getDefault().getDefaultDataDirectory();

	private TaskListManager manager = TasksUiPlugin.getTaskListManager();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		newDataDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ChangeDataDirTest.class.getSimpleName();
		File dir = new File(newDataDir);
		dir.mkdir();
		dir.deleteOnExit();
		manager.resetTaskList();
		assertTrue(manager.getTaskList().isEmpty());
		TasksUiPlugin.getTaskListManager().saveTaskList();
//		TasksUiPlugin.getDefault().getTaskListSaveManager().saveTaskList(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.resetTaskList();
		TasksUiPlugin.getDefault().setDataDirectory(defaultDir);
	}

	public void testMonitorFileMove() {
		UiUsageMonitorPlugin.getDefault().startMonitoring();
		UiUsageMonitorPlugin.getDefault().getInteractionLogger().interactionObserved(
				InteractionEvent.makeCommand("id", "delta"));
		String oldPath = UiUsageMonitorPlugin.getDefault().getInteractionLogger().getOutputFile().getAbsolutePath();
		assertTrue(new File(oldPath).exists());

		TasksUiPlugin.getDefault().setDataDirectory(newDataDir);

		assertFalse(new File(oldPath).exists());
		String newPath = UiUsageMonitorPlugin.getDefault().getInteractionLogger().getOutputFile().getAbsolutePath();
		assertTrue(new File(newPath).exists());

		assertTrue(UiUsageMonitorPlugin.getDefault().getInteractionLogger().getOutputFile().exists());
		String monitorFileName = UiUsageMonitorPlugin.MONITOR_LOG_NAME
				+ InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD;
		List<String> newFiles = Arrays.asList(new File(newDataDir).list());
		assertTrue(newFiles.toString(), newFiles.contains(monitorFileName));

		List<String> filesLeft = Arrays.asList(new File(defaultDir).list());
		assertFalse(filesLeft.toString(), filesLeft.contains(monitorFileName));
		UiUsageMonitorPlugin.getDefault().stopMonitoring();
	}

	public void testDefaultDataDirectoryMove() {
		String workspaceRelativeDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ".metadata" + '/' + ".mylyn";
		assertEquals(defaultDir, workspaceRelativeDir);

		TasksUiPlugin.getDefault().setDataDirectory(newDataDir);
		assertEquals(TasksUiPlugin.getDefault().getDataDirectory(), newDataDir);
	}

	public void testTaskMove() {
		AbstractTask task = manager.createNewLocalTask("label");
		String handle = task.getHandleIdentifier();
		manager.getTaskList().moveToContainer(task, manager.getTaskList().getDefaultCategory());

		AbstractTask readTaskBeforeMove = manager.getTaskList().getTask(handle);
		TasksUiPlugin.getTaskListManager().copyDataDirContentsTo(newDataDir);
		TasksUiPlugin.getDefault().setDataDirectory(newDataDir);
		AbstractTask readTaskAfterMove = manager.getTaskList().getTask(handle);

		assertNotNull(readTaskAfterMove);
		assertEquals(readTaskBeforeMove.getCreationDate(), readTaskAfterMove.getCreationDate());
	}

	// TODO: delete? using lastOpened date wrong
	public void testBugzillaTaskMove() {
//		String handle = AbstractTask.getHandle("server", 1);
		BugzillaTask bugzillaTask = new BugzillaTask("server", "1", "bug1");
		String refreshDate = (new Date()).toString();
		bugzillaTask.setLastReadTimeStamp(refreshDate);
		addBugzillaTask(bugzillaTask);
		BugzillaTask readTaskBeforeMove = (BugzillaTask) manager.getTaskList().getTask("server", "1");
		assertNotNull(readTaskBeforeMove);
		assertEquals(refreshDate, readTaskBeforeMove.getLastReadTimeStamp());

		TasksUiPlugin.getTaskListManager().copyDataDirContentsTo(newDataDir);
		TasksUiPlugin.getDefault().setDataDirectory(newDataDir);

		BugzillaTask readTaskAfterMove = (BugzillaTask) manager.getTaskList().getTask("server", "1");
		assertNotNull(readTaskAfterMove);
		assertEquals("bug1", readTaskAfterMove.getSummary());
		assertEquals(refreshDate, readTaskAfterMove.getLastReadTimeStamp());
	}

	private void addBugzillaTask(BugzillaTask newTask) {
		// AbstractRepositoryClient client =
		// MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
		// client.addTaskToArchive(newTask);

		// TODO: put back?
		// MylarTaskListPlugin.getTaskListManager().getTaskList().internalAddTask(newTask);

		// BugzillaTaskHandler handler = new BugzillaTaskHandler();
		// handler.addTaskToArchive(newTask);
		TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(newTask,
				TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory());
	}

	// /**
	// * Tests moving the main mylar data directory to another location (Without
	// * copying existing data to the new directory)
	// */
	// public void testChangeMainDataDir() {
	//
	// ITask mainDataDirTask = createAndSaveTask("Main Task", false);
	//
	// // ContextCorePlugin.getDefault().setDataDirectory(newDataDir);
	// assertEquals(0, manager.getTaskList().getRootTasks().size());
	//
	// // Check that the main data dir task isn't in the list or the folder
	// File taskFile = new File(ContextCorePlugin.getDefault().getDataDirectory() +
	// File.separator
	// + mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertFalse(taskFile.exists());
	// assertNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	//
	// // Check that a newly created task appears in the right place (method
	// // will check)
	// ITask newDataDirTask = createAndSaveTask("New Data Dir", false);
	// taskFile = new File(ContextCorePlugin.getDefault().getDataDirectory() +
	// File.separator
	// + newDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertTrue(taskFile.exists());
	//
	// // Check for other the tasklist file in the new dir
	// File destTaskListFile = new
	// File(ContextCorePlugin.getDefault().getDataDirectory() + File.separator
	// + MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);
	// assertTrue(destTaskListFile.exists());
	//
	// // Switch back to the main task directory
	// ContextCorePlugin.getDefault().setDataDirectory(ContextCorePlugin.getDefault().getDefaultDataDirectory());
	//		
	// // Check that the previously created main dir task is in the task list
	// and its file exists
	// assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	// taskFile = new File(ContextCorePlugin.getDefault().getDataDirectory() +
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
	// ContextCorePlugin.getContextManager().loadContext(newTask.getHandleIdentifier(),
	// newTask.getContextPath());
	// InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT,
	// "structureKind", "handle", "originId");
	// mockContext.parseEvent(event);
	// ContextCorePlugin.getContextManager().contextActivated(mockContext);
	//
	// // Save the context file and check that it exists
	// ContextCorePlugin.getContextManager().saveContext(mockContext.getId(),
	// newTask.getContextPath());
	// File taskFile = new File(ContextCorePlugin.getDefault().getDataDirectory() +
	// File.separator
	// + newTask.getContextPath() + MylarContextManager.CONTEXT_FILE_EXTENSION);
	// assertTrue(ContextCorePlugin.getContextManager().hasContext(newTask.getContextPath()));
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
	// ContextCorePlugin.getDefault().setDataDirectory(newDataDir);
	//
	// // Check that there are no tasks in the tasklist after switching to the
	// // empty dir
	// assertTrue(manager.getTaskList().getRootTasks().size() == 0);
	//
	// // Check that the main data dir task isn't in the list or the folder
	// File taskFile = new File(ContextCorePlugin.getDefault().getDataDirectory() +
	// File.separator
	// + mainDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertFalse(taskFile.exists());
	// assertNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	//
	// // Check that a newly created task appears in the right place (method
	// // will check)
	// ITask newDataDirTask = createAndSaveTask("New Data Dir", true);
	// taskFile = new File(ContextCorePlugin.getDefault().getDataDirectory() +
	// File.separator
	// + newDataDirTask.getContextPath() + MylarTaskListPlugin.FILE_EXTENSION);
	// assertTrue(taskFile.exists());
	//
	// // Check for tasklist file in the new dir
	// File destTaskListFile = new
	// File(ContextCorePlugin.getDefault().getDataDirectory() + File.separator
	// + MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);
	// assertTrue(destTaskListFile.exists());
	//
	// ContextCorePlugin.getDefault().setDataDirectory(ContextCorePlugin.getDefault().getDefaultDataDirectory());
	//
	// // Check that the previously created main dir task is in the task list
	// // and its file exists
	// assertNotNull(manager.getTaskForHandle(mainDataDirTask.getHandleIdentifier(),
	// false));
	// taskFile = new File(ContextCorePlugin.getDefault().getDataDirectory() +
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
