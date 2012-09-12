/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core;

import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gerrit.reviewdb.ApprovalCategoryValue;

/**
 * @author Steffen Pingel
 */
public class GerritSynchronizationTest extends TestCase {

	private GerritHarness harness;

	private TaskRepository repository;

	private TaskDataManager taskDataManager;

	private TaskList taskList;

	@Override
	@Before
	public void setUp() throws Exception {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);

		harness = GerritFixture.current().harness();
		repository = GerritFixture.current().singleRepository();
		taskList = TasksUiPlugin.getTaskList();
		taskDataManager = TasksUiPlugin.getTaskDataManager();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		harness.dispose();
		TestFixture.resetTaskListAndRepositories();
		TasksUiPlugin.getRepositoryModel().clear();
	}

	@Test
	public void testSynchronizeBackgroundQueryTaskUpdated() throws Exception {
		ITask task = createAndSynchronizeQuery(false);
		String message = addComment(task);
		synchronizeAllTasks(false);
		assertHasNewComment(task, message);
	}

	@Test
	public void testSynchronizeBackgroundTaskUpdated() throws Exception {
		ITask task = createAndSynchronizeQuery(false);
		String message = addComment(task);
		synchronizeTask(task, false);
		assertHasNewComment(task, message);
	}

	@Test
	public void testSynchronizeQueryNewTask() throws Exception {
		createAndSynchronizeQuery(true);
	}

	@Test
	public void testSynchronizeQueryTaskUpdated() throws Exception {
		ITask task = createAndSynchronizeQuery(true);
		String message = addComment(task);
		synchronizeAllTasks(true);
		assertHasNewComment(task, message);
	}

	@Test
	public void testSynchronizeTaskUpdated() throws Exception {
		ITask task = createAndSynchronizeQuery(true);
		String message = addComment(task);
		synchronizeTask(task, true);
		assertHasNewComment(task, message);
	}

	private String addComment(ITask task) throws GerritException {
		taskDataManager.setTaskRead(task, true);
		GerritClient client = new GerritClient(harness.location());
		String message = "new comment, time: " + System.currentTimeMillis(); //$NON-NLS-1$
		client.publishComments(task.getTaskId(), 1, message, Collections.<ApprovalCategoryValue.Id> emptySet(), null);
		return message;
	}

	private void assertHasNewComment(ITask task, String message) throws CoreException {
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());

		// validate that task data was fully synchronized
		TaskData taskData = taskDataManager.getTaskData(task);
		List<TaskAttribute> comments = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_COMMENT);
		TaskCommentMapper lastComment = TaskCommentMapper.createFrom(comments.get(comments.size() - 1));
		assertEquals("Patch Set 1:\n\n" + message, lastComment.getText()); //$NON-NLS-1$
	}

	private ITask assertTaskListHasOneTask() throws CoreException {
		assertEquals(1, taskList.getAllTasks().size());
		ITask task = taskList.getAllTasks().iterator().next();
		assertEquals(repository.getUrl(), task.getRepositoryUrl());
		assertTrue(taskDataManager.hasTaskData(task));
		return task;
	}

	private ITask createAndSynchronizeQuery(boolean user) throws InterruptedException, CoreException, GerritException {
		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(repository);
		query.setAttribute(GerritQuery.TYPE, GerritQuery.ALL_OPEN_CHANGES);
		taskList.addQuery((RepositoryQuery) query);

		synchronizeAllTasks(user);

		ITask task = assertTaskListHasOneTask();
		assertEquals(SynchronizationState.INCOMING_NEW, task.getSynchronizationState());
		return task;
	}

	private void synchronizeAllTasks(boolean user) throws InterruptedException {
		SynchronizationJob job = TasksUiPlugin.getTaskJobFactory().createSynchronizeRepositoriesJob(
				Collections.singleton(repository));
		job.setUser(user);
		job.schedule();
		job.join();
		// wait for parallel query synchronization jobs
		Job.getJobManager().join(ITasksCoreConstants.JOB_FAMILY_SYNCHRONIZATION, null);
	}

	private void synchronizeTask(ITask task, boolean user) throws InterruptedException {
		SynchronizationJob job = TasksUiPlugin.getTaskJobFactory().createSynchronizeTasksJob(
				TasksUi.getRepositoryConnector(repository.getConnectorKind()), repository, Collections.singleton(task));
		job.setUser(user);
		job.schedule();
		job.join();
	}

}
