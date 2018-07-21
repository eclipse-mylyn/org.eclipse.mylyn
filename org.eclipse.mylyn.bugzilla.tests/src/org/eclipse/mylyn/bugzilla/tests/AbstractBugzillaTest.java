/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.bugzilla.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaHarness;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TestFixture;

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

	protected BugzillaHarness harness;

	public AbstractBugzillaTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		manager = TasksUiPlugin.getRepositoryManager();
		TestFixture.resetTaskListAndRepositories();
		this.client = BugzillaFixture.current().client();
		this.connector = BugzillaFixture.current().connector();
		this.repository = BugzillaFixture.current().repository();
		TasksUi.getRepositoryManager().addRepository(repository);
		harness = BugzillaFixture.current().createHarness();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestFixture.resetTaskList();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public TaskDataModel createModel(ITask task) throws CoreException {
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
		ITask task = TasksUi.getRepositoryModel().createTask(repository, id);
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUiInternal.synchronizeTask(connector, task, true, null);
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		return task;
	}

	public BugzillaRepositoryConnector getConnector() {
		return connector;
	}

	public TaskRepositoryManager getManager() {
		return manager;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public BugzillaClient getClient() {
		return client;
	}

}
