/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Nathan Hapke
 */
public abstract class AbstractBugzillaTest extends TestCase {

	static final String DEFAULT_KIND = BugzillaCorePlugin.CONNECTOR_KIND;

	protected BugzillaRepositoryConnector connector;

	protected TaskRepositoryManager manager;

	protected TaskRepository repository;

	protected TaskList taskList;

	protected BugzillaClient client;

	public AbstractBugzillaTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		manager = TasksUiPlugin.getRepositoryManager();
		TestFixture.resetTaskListAndRepositories();
		this.client = BugzillaFixture.current().client();
		this.connector = BugzillaFixture.current().connector();
		this.repository = BugzillaFixture.current().repository();
		TasksUi.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestFixture.resetTaskList();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	protected void init34() {
		init(BugzillaTestConstants.TEST_BUGZILLA_34_URL);
	}

	protected void init323() {
		init(BugzillaTestConstants.TEST_BUGZILLA_323_URL);
	}

	protected void init322() {
		init(BugzillaTestConstants.TEST_BUGZILLA_322_URL);
	}

	protected void init32() {
		init(BugzillaTestConstants.TEST_BUGZILLA_32_URL);
	}

	protected void init30() {
		init(BugzillaTestConstants.TEST_BUGZILLA_30_URL);
	}

	protected void init222() {
		init(BugzillaTestConstants.TEST_BUGZILLA_222_URL);
	}

	protected void init2201() {
		init(BugzillaTestConstants.TEST_BUGZILLA_2201_URL);
	}

	protected void init220() {
		init(BugzillaTestConstants.TEST_BUGZILLA_220_URL);
	}

	protected void init218() {
		init(BugzillaTestConstants.TEST_BUGZILLA_218_URL);
	}

	@SuppressWarnings("deprecation")
	protected void init(String url) {
		repository = new TaskRepository(DEFAULT_KIND, url);
		Credentials credentials = TestUtil.readCredentials();
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);

		repository.setTimeZoneId("Canada/Eastern");
		assertNotNull(manager);
		manager.addRepository(repository);

		taskList = TasksUiPlugin.getTaskList();

		AbstractRepositoryConnector abstractRepositoryClient = manager.getRepositoryConnector(DEFAULT_KIND);

		assertEquals(abstractRepositoryClient.getConnectorKind(), DEFAULT_KIND);

		connector = (BugzillaRepositoryConnector) abstractRepositoryClient;
		try {
			connector.getRepositoryConfiguration(repository, false, new NullProgressMonitor());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

//	protected ITask generateLocalTaskAndDownload(String taskNumber) throws CoreException {
//		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskNumber);
//		// FIXME task.setStale(true);
//		TasksUiPlugin.getTaskList().addTask(task);
//		TasksUiInternal.synchronizeTask(connector, task, true, null);
//		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
//		return task;
//	}

	protected TaskDataModel createModel(ITask task) throws CoreException {
		ITaskDataWorkingCopy taskDataState = getWorkingCopy(task);
		return new TaskDataModel(repository, task, taskDataState);
	}

	protected ITaskDataWorkingCopy getWorkingCopy(ITask task) throws CoreException {
		return TasksUiPlugin.getTaskDataManager().getWorkingCopy(task);
	}

	protected void submit(TaskDataModel model) {
		SubmitJob submitJob = TasksUiInternal.getJobFactory().createSubmitTaskJob(connector, model.getTaskRepository(),
				model.getTask(), model.getTaskData(), model.getChangedOldAttributes());
		submitJob.schedule();
		try {
			submitJob.join();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	protected void synchAndAssertState(Set<ITask> tasks, SynchronizationState state) {
		for (ITask task : tasks) {
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
			assertEquals(task.getSynchronizationState(), state);
		}
	}

	public ITask generateLocalTaskAndDownload(String id) throws CoreException {
		TasksUi.getRepositoryManager().addRepository(repository);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, id);
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUiInternal.synchronizeTask(connector, task, true, null);
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		return task;
	}
}
