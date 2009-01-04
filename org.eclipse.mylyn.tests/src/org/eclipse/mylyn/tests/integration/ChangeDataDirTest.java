/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.commons.tests.support.CommonsTestUtil;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

/**
 * Tests changes to the main data directory location.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (rewrites)
 */
public class ChangeDataDirTest extends TestCase {

	private String newDataDir;

	private String defaultDir;

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		defaultDir = TasksUiPlugin.getDefault().getDefaultDataDirectory();

		newDataDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ChangeDataDirTest.class.getSimpleName();
		File dir = new File(newDataDir);

		dir.mkdir();
		dir.deleteOnExit();

		taskList = TasksUiPlugin.getTaskList();

		TaskTestUtil.resetTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskList();
		TasksUiPlugin.getDefault().setDataDirectory(defaultDir);
	}

	public void testDefaultDataDirectoryMove() throws Exception {
		String workspaceRelativeDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ".metadata" + '/' + ".mylyn";
		assertEquals(defaultDir, workspaceRelativeDir);

		TasksUiPlugin.getDefault().setDataDirectory(newDataDir);
		assertEquals(newDataDir, TasksUiPlugin.getDefault().getDataDirectory());
		assertEquals(newDataDir, TasksUiPlugin.getTaskDataManager().getDataPath());
		assertEquals(new File(newDataDir, "contexts"), ContextCorePlugin.getContextStore().getContextDirectory());

	}

	public void testTaskMove() throws Exception {
		AbstractTask task = TasksUiInternal.createNewLocalTask("label");
		String handle = task.getHandleIdentifier();
		taskList.addTask(task, taskList.getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));

		ITask readTaskBeforeMove = taskList.getTask(handle);
		assertNotNull(readTaskBeforeMove);
		assertTrue(taskList.getAllTasks().size() > 0);
		CommonsTestUtil.copyFolder(new File(TasksUiPlugin.getDefault().getDataDirectory()), new File(newDataDir));
		TasksUiPlugin.getDefault().setDataDirectory(newDataDir);
		assertTrue(taskList.getAllTasks().size() > 0);
		ITask readTaskAfterMove = taskList.getTask(handle);

		assertNotNull(readTaskAfterMove);
		assertEquals(readTaskBeforeMove.getCreationDate(), readTaskAfterMove.getCreationDate());
	}

}
