/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tests.integration;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;

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
		TasksUiPlugin.getExternalizationManager().requestSave();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.resetTaskList();
		TasksUiPlugin.getDefault().setDataDirectory(defaultDir, new NullProgressMonitor());
	}

	public void testDefaultDataDirectoryMove() throws CoreException {
		String workspaceRelativeDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ".metadata" + '/' + ".mylyn";
		assertEquals(defaultDir, workspaceRelativeDir);

		TasksUiPlugin.getDefault().setDataDirectory(newDataDir, new NullProgressMonitor());
		assertEquals(TasksUiPlugin.getDefault().getDataDirectory(), newDataDir);
	}

	public void testTaskMove() throws CoreException {
		AbstractTask task = manager.createNewLocalTask("label");
		String handle = task.getHandleIdentifier();
		manager.getTaskList().addTask(task,
				manager.getTaskList().getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));

		AbstractTask readTaskBeforeMove = manager.getTaskList().getTask(handle);
		assertNotNull(readTaskBeforeMove);
		assertTrue(manager.getTaskList().getAllTasks().size() > 0);
		TasksUiPlugin.getTaskListManager().copyDataDirContentsTo(newDataDir);
		TasksUiPlugin.getDefault().setDataDirectory(newDataDir, new NullProgressMonitor());
		assertTrue(manager.getTaskList().getAllTasks().size() > 0);
		AbstractTask readTaskAfterMove = manager.getTaskList().getTask(handle);

		assertNotNull(readTaskAfterMove);
		assertEquals(readTaskBeforeMove.getCreationDate(), readTaskAfterMove.getCreationDate());
	}

}
