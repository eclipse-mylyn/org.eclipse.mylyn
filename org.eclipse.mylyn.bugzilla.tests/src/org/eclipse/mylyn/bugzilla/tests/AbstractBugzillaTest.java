/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Nathan Hapke
 */
public abstract class AbstractBugzillaTest extends TestCase {

	static final String DEFAULT_KIND = BugzillaCorePlugin.REPOSITORY_KIND;

	protected BugzillaRepositoryConnector connector;

	protected TaskRepositoryManager manager;

	protected TaskRepository repository;

	protected TaskList taskList;

	public AbstractBugzillaTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		TasksUiPlugin.getTaskDataStorageManager().clear();
		manager = TasksUiPlugin.getRepositoryManager();
		TasksUiPlugin.getTaskListManager().resetTaskList();//getTaskList().reset();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	protected void init31() {
		init(IBugzillaConstants.TEST_BUGZILLA_31_URL);
	}

	protected void init30() {
		init(IBugzillaConstants.TEST_BUGZILLA_30_URL);
	}

	protected void init222() {
		init(IBugzillaConstants.TEST_BUGZILLA_222_URL);
	}

	protected void init2201() {
		init(IBugzillaConstants.TEST_BUGZILLA_2201_URL);
	}

	protected void init220() {
		init(IBugzillaConstants.TEST_BUGZILLA_220_URL);
	}

	protected void init218() {
		init(IBugzillaConstants.TEST_BUGZILLA_218_URL);
	}

	@SuppressWarnings("deprecation")
	protected void init(String url) {
		repository = new TaskRepository(DEFAULT_KIND, url);
		Credentials credentials = TestUtil.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);

		repository.setTimeZoneId("Canada/Eastern");
		assertNotNull(manager);
		manager.addRepository(repository);

		taskList = TasksUiPlugin.getTaskList();

		AbstractRepositoryConnector abstractRepositoryClient = manager.getRepositoryConnector(DEFAULT_KIND);

		assertEquals(abstractRepositoryClient.getConnectorKind(), DEFAULT_KIND);

		connector = (BugzillaRepositoryConnector) abstractRepositoryClient;
	}

	protected BugzillaTask generateLocalTaskAndDownload(String taskNumber) throws CoreException {
		BugzillaTask task = (BugzillaTask) TasksUiUtil.createTask(repository, taskNumber, new NullProgressMonitor());
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		assertNotNull(task);
		TasksUiPlugin.getTaskList().addTask(task, TasksUiPlugin.getTaskList().getDefaultCategory());

		return task;
	}

	protected void submit(ITask task, RepositoryTaskData taskData) throws CoreException {
		connector.getLegacyTaskDataHandler().postTaskData(repository, taskData, new NullProgressMonitor());
		((AbstractTask) task).setSubmitting(true);
	}

	// protected BugzillaReportSubmitForm makeExistingBugPost(RepositoryTaskData
	// taskData)
	// throws UnsupportedEncodingException {
	// return BugzillaReportSubmitForm.makeExistingBugPost(taskData,
	// repository.getUrl(), repository.getUserName(),
	// repository.getPassword(), repository.getCharacterEncoding());
	// }

	protected void synchAndAssertState(Set<AbstractTask> tasks, SynchronizationState state) {
		for (AbstractTask task : tasks) {
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			assertEquals(task.getSynchronizationState(), state);
		}
	}

	// class MockBugzillaReportSubmitForm extends BugzillaReportSubmitForm {
	//
	// public MockBugzillaReportSubmitForm() {
	// super();
	// }
	//
	// @Override
	// public String submitReportToRepository(BugzillaClient client) throws
	// BugzillaException, LoginException,
	// PossibleBugzillaFailureException {
	// return "test-submit";
	// }
	//
	// }
}
