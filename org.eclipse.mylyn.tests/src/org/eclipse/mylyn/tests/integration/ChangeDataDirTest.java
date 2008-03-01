/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Tests changes to the main data directory location.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (rewrites)
 */
public class ChangeDataDirTest extends TestCase {

	private String newDataDir = null;

	private final String defaultDir = TasksUiPlugin.getDefault().getDefaultDataDirectory();

	private final TaskListManager manager = TasksUiPlugin.getTaskListManager();

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
		manager.getTaskList().moveTask(task,
				manager.getTaskList().getOrphanContainer(LocalRepositoryConnector.REPOSITORY_URL));

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
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask);
	}
}
