/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

public class TaskListExternalizationTest extends TestCase {

	private TaskList taskList;

	private TaskRepository repository;

	private void reloadTaskList() throws CoreException {
		taskList.notifyElementsChanged(null);
		TasksUiPlugin.getExternalizationManager().requestSave();
		TasksUiPlugin.getDefault().reloadDataDirectory();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		TasksUiPlugin.getTaskListManager().resetTaskList();

		repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		taskList = TasksUiPlugin.getTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		TasksUiPlugin.getTaskListManager().resetTaskList();
	}

	public void testTaskAttributes() throws CoreException {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		task1.setAttribute("key", "value");
		assertEquals(1, task1.getAttributes().size());

		reloadTaskList();

		task1 = taskList.getTask(task1.getHandleIdentifier());
		assertNotNull(task1);
		assertEquals(1, task1.getAttributes().size());
		assertEquals("value", task1.getAttribute("key"));
	}

	public void testTaskAttributeDelete() throws CoreException {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		task1.setAttribute("key", "value");
		task1.setAttribute("key", null);
		assertEquals(0, task1.getAttributes().size());
		assertEquals(null, task1.getAttribute("key"));

		reloadTaskList();

		task1 = taskList.getTask(task1.getHandleIdentifier());
		assertNotNull(task1);
		assertEquals(0, task1.getAttributes().size());
		assertEquals(null, task1.getAttribute("key"));
	}

}
