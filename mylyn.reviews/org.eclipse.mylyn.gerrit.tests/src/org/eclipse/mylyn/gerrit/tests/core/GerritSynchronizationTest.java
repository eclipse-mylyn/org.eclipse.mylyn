/*******************************************************************************
 * Copyright (c) 2012, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.RemoteUiFactoryProviderConfigurer;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.spi.edit.remote.AbstractRemoteEditFactoryProvider;
import org.eclipse.mylyn.reviews.spi.edit.remote.review.ReviewsRemoteEditFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.remote.RemoteUiService;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
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
import com.google.gerrit.reviewdb.Project;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class GerritSynchronizationTest extends TestCase {

	private GerritHarness harness;

	private TaskRepository repository;

	private TaskDataManager taskDataManager;

	private TaskList taskList;

	private GerritClient client;

	@Override
	@Before
	public void setUp() throws Exception {
//		RemoteFactoryProviderConfigurer configurer = new TestFactoryProviderConfigurer();
//
//		GerritCorePlugin.getDefault().getConnector().setFactoryProviderConfigurer(configurer);
		GerritUiPlugin.getDefault();
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		// cancel any parallel query synchronization jobs
		Job.getJobManager().cancel(ITasksCoreConstants.JOB_FAMILY_SYNCHRONIZATION);

		TestFixture.resetTaskListAndRepositories();

		harness = GerritFixture.current().harness();
		repository = GerritFixture.current().singleRepository();
		GerritCorePlugin.getDefault()
				.getConnector()
				.setFactoryProviderConfigurer(new RemoteUiFactoryProviderConfigurer());
		client = GerritCorePlugin.getDefault().getConnector().getClient(repository);
		AbstractRemoteEditFactoryProvider abstractRemoteEditFactoryProvider = (AbstractRemoteEditFactoryProvider) client
				.getFactoryProvider();
		GerritCorePlugin.getDefault()
				.getConnector()
				.getFactoryProviderConfigurer()
				.configure(abstractRemoteEditFactoryProvider);

		assertThat(abstractRemoteEditFactoryProvider.getService(), instanceOf(RemoteUiService.class));
		taskList = TasksUiPlugin.getTaskList();
		taskDataManager = TasksUiPlugin.getTaskDataManager();

		harness.ensureOneReviewExists();
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
		RepositoryQuery query = taskList.getQueries().iterator().next();
		synchronizeQuery(query, false);
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
		RepositoryQuery query = taskList.getQueries().iterator().next();
		synchronizeQuery(query, true);
		assertHasNewComment(task, message);
	}

	@Test
	public void testSynchronizeTaskUpdated() throws Exception {
		ITask task = createAndSynchronizeQuery(true);
		String message = addComment(task);
		synchronizeTask(task, true);
		assertHasNewComment(task, message);
	}

	@Test
	public void testGetFromChangeId() throws Exception {
		ITask task = createAndSynchronizeQuery(true);
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(repository.getConnectorKind());
		GerritClient client = ((GerritConnector) connector).getClient(repository);
		Project.NameKey project = new Project.NameKey(
				taskDataManager.getTaskData(task).getRoot().getMappedAttribute(TaskAttribute.PRODUCT).getValue());
		client.clearCachedBranches(project);
		assertNull(client.getCachedBranches(project));

		TaskData taskData = connector.getTaskData(repository, task.getTaskId(), new NullProgressMonitor());
		assertEquals(task.getTaskId(), taskData.getTaskId());
		assertNotNull(client.getCachedBranches(project));

		TaskAttribute changeIdAttribute = taskData.getRoot()
				.getAttribute(GerritTaskSchema.getDefault().CHANGE_ID.getKey());
		assertNotNull(changeIdAttribute);
		assertThat(task.getTaskId(), not(equalTo(changeIdAttribute.getValue())));
		TaskData taskDataFromChangeId = connector.getTaskData(repository, changeIdAttribute.getValue(),
				new NullProgressMonitor());
		assertEquals(task.getTaskId(), taskDataFromChangeId.getTaskId());
		assertEquals(taskData.getRoot().toString(), taskDataFromChangeId.getRoot().toString());
	}

	private String addComment(ITask task) throws GerritException {
		taskDataManager.setTaskRead(task, true);
		GerritClient client = GerritClient.create(null, harness.location());
		String message = "new comment, time: " + System.currentTimeMillis(); //$NON-NLS-1$
		client.publishComments(task.getTaskId(), 1, message, Collections.<ApprovalCategoryValue.Id> emptySet(), null);
		return message;
	}

	private void assertHasNewComment(ITask task, String message) throws CoreException {
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());

		// validate that task data was fully synchronized
		TaskData taskData = taskDataManager.getTaskData(task);
		List<TaskAttribute> comments = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_COMMENT);
		TaskCommentMapper lastComment = TaskCommentMapper.createFrom(comments.get(comments.size() - 1));
		assertEquals("Failure on " + GerritFixture.current().getRepositoryUrl() + "/" + task.getTaskId(), //$NON-NLS-1$
				"Patch Set 1:\n\n" + message, lastComment.getText());
	}

	private ITask assertTaskListHasOneTask() throws CoreException {
		assertTrue(taskList.getAllTasks().size() >= 1);
		ITask task = taskList.getAllTasks().iterator().next();
		assertEquals(repository.getUrl(), task.getRepositoryUrl());
		assertTrue(taskDataManager.hasTaskData(task));
		return task;
	}

	private ITask createAndSynchronizeQuery(boolean user) throws Exception {
		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(repository);
		query.setAttribute(GerritQuery.TYPE, GerritQuery.CUSTOM);
		query.setAttribute(GerritQuery.QUERY_STRING, harness.defaultQuery());
		taskList.addQuery((RepositoryQuery) query);

		synchronizeQuery((RepositoryQuery) query, user);

		ITask task = assertTaskListHasOneTask();
		assertEquals(SynchronizationState.INCOMING_NEW, task.getSynchronizationState());

		String filePath = client.getFactoryProvider().getDataLocator().getModelPath() + File.separator
				+ "org.eclipse.mylyn.gerrit-" + ReviewsRemoteEditFactoryProvider.asFileName(repository.getUrl())
				+ File.separator + "Review" + File.separator + task.getTaskId() + ".reviews";
		File file = new File(filePath);
		assertThat("File should exist at: " + filePath, file.exists(), is(true));

		return task;
	}

	private void synchronizeQuery(RepositoryQuery query, boolean user) throws InterruptedException {
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnector(repository.getConnectorKind());
		SynchronizationJob job = TasksUiPlugin.getTaskJobFactory()
				.createSynchronizeQueriesJob(connector, repository, Collections.singleton(query));
		job.setUser(user);
		job.schedule();
		job.join();
		// wait for any query synchronization jobs scheduled by job above
		boolean synchronizing = true;
		while (synchronizing) {
			try {
				Job.getJobManager().join(ITasksCoreConstants.JOB_FAMILY_SYNCHRONIZATION, null);
				synchronizing = false;
			} catch (InterruptedException e) {
				// ignore interrupts caused by sync exec and spin the UI loop to process them
				UiTestUtil.waitForDisplay();
			}
		}
	}

	private void synchronizeTask(ITask task, boolean user) throws InterruptedException {
		SynchronizationJob job = TasksUiPlugin.getTaskJobFactory()
				.createSynchronizeTasksJob(TasksUi.getRepositoryConnector(repository.getConnectorKind()), repository,
						Collections.singleton(task));
		job.setUser(user);
		job.schedule();
		boolean synchronizing = true;
		while (synchronizing) {
			try {
				job.join();
				synchronizing = false;
			} catch (InterruptedException e) {
				// ignore interrupts caused by sync exec and spin the UI loop to process them
				UiTestUtil.waitForDisplay();
			}
		}
	}

}
