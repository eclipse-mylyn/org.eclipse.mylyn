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

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.util.Calendar;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.RefactorRepositoryUrlOperation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
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

	public void testMigrateTaskContextFiles() throws Exception {
		File fileA = ContextCorePlugin.getContextStore().getFileForContext("http://a-1");
		fileA.createNewFile();
		fileA.deleteOnExit();
		assertTrue(fileA.exists());
		new RefactorRepositoryUrlOperation("http://a", "http://b").run(new NullProgressMonitor());
		File fileB = ContextCorePlugin.getContextStore().getFileForContext("http://b-1");
		assertTrue(fileB.exists());
		assertFalse(fileA.exists());
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

		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, "http://a");
		TasksUi.getRepositoryManager().addRepository(repository);
		TaskRepository repository2 = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, "http://other");
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

	public void testRefactorMetaContextHandles() throws Exception {
		String firstUrl = "http://repository1.com/bugs";
		String secondUrl = "http://repository2.com/bugs";
		AbstractTask task1 = new MockTask(firstUrl, "1");
		AbstractTask task2 = new MockTask(firstUrl, "2");
		taskList.addTask(task1);
		taskList.addTask(task2);
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.MINUTE, 5);

		Calendar startDate2 = Calendar.getInstance();
		startDate2.add(Calendar.MINUTE, 15);
		Calendar endDate2 = Calendar.getInstance();
		endDate2.add(Calendar.MINUTE, 25);

		ContextCorePlugin.getContextManager().resetActivityMetaContext();
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertEquals(0, metaContext.getInteractionHistory().size());

		ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.ATTENTION,
						InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task1.getHandleIdentifier(), "origin",
						null, InteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, startDate.getTime(),
						endDate.getTime()));

		ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.ATTENTION,
						InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task2.getHandleIdentifier(), "origin",
						null, InteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, startDate2.getTime(),
						endDate2.getTime()));

		assertEquals(2, metaContext.getInteractionHistory().size());
		assertEquals(60 * 1000 * 5, TasksUiPlugin.getTaskActivityManager().getElapsedTime(task1));
		assertEquals(2 * 60 * 1000 * 5, TasksUiPlugin.getTaskActivityManager().getElapsedTime(task2));
		new RefactorRepositoryUrlOperation(firstUrl, secondUrl).run(new NullProgressMonitor());
		metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertEquals(2, metaContext.getInteractionHistory().size());
		assertEquals(60 * 1000 * 5, TasksUiPlugin.getTaskActivityManager().getElapsedTime(new MockTask(secondUrl, "1")));
		assertEquals(2 * 60 * 1000 * 5, TasksUiPlugin.getTaskActivityManager().getElapsedTime(
				new MockTask(secondUrl, "2")));
		assertEquals(secondUrl + "-1", metaContext.getInteractionHistory().get(0).getStructureHandle());
	}

	public void testMigrateTaskHandlesUnsubmittedTask() throws Exception {
		ITask task = TasksUiUtil.createOutgoingNewTask(MockRepositoryConnector.REPOSITORY_KIND, "http://a");
		String handleIdentifier = task.getHandleIdentifier();
		taskList.addTask(task);
		assertEquals("http://a", task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL));

		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, "http://a");
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
