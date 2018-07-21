/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.RefactorRepositoryUrlOperation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class RefactorRepositoryUrlOperationTest extends TestCase {

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		taskList = TasksUiPlugin.getTaskList();
		TaskTestUtil.resetTaskList();
	}

	public void testMigrateQueryUrlHandles() throws Exception {
		RepositoryQuery query = new MockRepositoryQuery("mquery");
		query.setRepositoryUrl("http://foo.bar");
		query.setUrl("http://foo.bar/b");
		taskList.addQuery(query);
		assertTrue(taskList.getRepositoryQueries("http://foo.bar").size() > 0);
		new RefactorRepositoryUrlOperation("http://foo.bar", "http://bar.baz").run(new NullProgressMonitor());
		assertTrue(taskList.getRepositoryQueries("http://foo.bar").size() == 0);
		assertTrue(taskList.getRepositoryQueries("http://bar.baz").size() > 0);
		IRepositoryQuery changedQuery = taskList.getRepositoryQueries("http://bar.baz").iterator().next();
		assertEquals("http://bar.baz/b", changedQuery.getUrl());
	}

	public void testMigrateQueryHandles() throws Exception {
		RepositoryQuery query = new MockRepositoryQuery("mquery");
		query.setRepositoryUrl("http://a");
		taskList.addQuery(query);
		new RefactorRepositoryUrlOperation("http://a", "http://b").run(new NullProgressMonitor());
		assertFalse(taskList.getRepositoryQueries("http://b").isEmpty());
		assertTrue(taskList.getRepositoryQueries("http://a").isEmpty());
	}

	public void testMigrateTaskHandles() throws Exception {
		AbstractTask task = new MockTask("http://a", "123");
		AbstractTask task2 = new MockTask("http://other", "other");
		taskList.addTask(task);
		taskList.addTask(task2);

		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://a");
		TasksUi.getRepositoryManager().addRepository(repository);
		TaskRepository repository2 = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://other");
		TasksUi.getRepositoryManager().addRepository(repository2);

		try {
			TaskData taskData = new TaskData(new TaskAttributeMapper(repository), task.getConnectorKind(),
					task.getRepositoryUrl(), task.getTaskId());
			TasksUiPlugin.getTaskDataManager().putUpdatedTaskData(task, taskData, true);

			TaskData taskData2 = new TaskData(new TaskAttributeMapper(repository2), task2.getConnectorKind(),
					task2.getRepositoryUrl(), task2.getTaskId());
			taskData2.getRoot().createAttribute("comment").setValue("TEST");
			TasksUiPlugin.getTaskDataManager().putUpdatedTaskData(task2, taskData2, true);

			new RefactorRepositoryUrlOperation("http://a", "http://b").run(new NullProgressMonitor());
			repository.setRepositoryUrl("http://b");
			assertNull(taskList.getTask("http://a-123"));
			assertNotNull(taskList.getTask("http://b-123"));
			assertNotNull(TasksUi.getTaskDataManager().getTaskData(task));
			TaskData otherData = TasksUi.getTaskDataManager().getTaskData(task2);
			assertNotNull(otherData);
			assertEquals("TEST", otherData.getRoot().getAttribute("comment").getValue());
		} finally {
			TasksUiPlugin.getTaskDataManager().deleteTaskData(task);
			TasksUiPlugin.getTaskDataManager().deleteTaskData(task2);
		}
	}

	public void testMigrateTaskHandlesWithExplicitSet() throws Exception {
		AbstractTask task = new MockTask("http://aa", "123");
		task.setUrl("http://aa/task/123");
		taskList.addTask(task);
		new RefactorRepositoryUrlOperation("http://aa", "http://bb").run(new NullProgressMonitor());
		assertNull(taskList.getTask("http://aa-123"));
		assertNotNull(taskList.getTask("http://bb-123"));
		assertEquals("http://bb/task/123", task.getUrl());
	}

	public void testMigrateTaskHandlesUnsubmittedTask() throws Exception {
		ITask task = TasksUiUtil.createOutgoingNewTask(MockRepositoryConnector.CONNECTOR_KIND, "http://a");
		String handleIdentifier = task.getHandleIdentifier();
		taskList.addTask(task);
		assertEquals("http://a", task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL));

		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://a");
		TasksUi.getRepositoryManager().addRepository(repository);

		try {
			TaskData taskData = new TaskData(new TaskAttributeMapper(repository), repository.getConnectorKind(),
					repository.getRepositoryUrl(), "");
			ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(task, taskData);
			workingCopy.save(null, null);

			new RefactorRepositoryUrlOperation("http://a", "http://b").run(new NullProgressMonitor());
			repository.setRepositoryUrl("http://b");
			assertEquals(task, taskList.getTask(handleIdentifier));
			assertEquals("http://b", task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL));
			taskData = TasksUi.getTaskDataManager().getTaskData(task);
			assertNotNull(taskData);
			assertEquals("http://b", taskData.getRepositoryUrl());
		} finally {
			TasksUiPlugin.getTaskDataManager().deleteTaskData(task);
		}
	}

}
