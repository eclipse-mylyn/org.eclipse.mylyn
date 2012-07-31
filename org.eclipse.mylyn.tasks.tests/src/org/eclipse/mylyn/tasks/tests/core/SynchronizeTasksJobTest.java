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

package org.eclipse.mylyn.tasks.tests.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants.MutexSchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataStore;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeTasksJob;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.AssertionProgressMonitor;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TestFixture;

/**
 * @author Benjamin Muskalla
 */
public class SynchronizeTasksJobTest extends TestCase {

	private final class DeltaCountingTaskListChangeListener implements ITaskListChangeListener {

		private int deltasFired = 0;

		public DeltaCountingTaskListChangeListener() {
			taskList.addChangeListener(this);
		}

		public void containersChanged(Set<TaskContainerDelta> containers) {
			deltasFired++;
		}

		public int getDeltasFired() {
			return deltasFired;
		}

		public void tearDown() {
			taskList.removeChangeListener(this);
		}
	}

	private IRepositoryModel tasksModel;

	private TaskDataManager taskDataManager;

	private TaskRepository repository;

	private TaskList taskList;

	private TaskDataStore taskDataStore;

	@Override
	protected void setUp() throws Exception {
		tasksModel = TasksUi.getRepositoryModel();
		taskDataManager = (TaskDataManager) TasksUi.getTaskDataManager();
		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		taskList = (TaskList) TasksUiInternal.getTaskList();
		taskDataStore = new TaskDataStore(TasksUi.getRepositoryManager());
	}

	@Override
	protected void tearDown() throws Exception {
		TestFixture.resetTaskList();
	}

	public void testRunsExclusivly() throws Exception {
		SynchronizeTasksJob job = createSyncJob(null, Collections.<ITask> emptySet());
		assertTrue(job.getRule() instanceof MutexSchedulingRule);
	}

	public void testSyncWithSingleTaskDataCanceled() throws Exception {
		DeltaCountingTaskListChangeListener listener = new DeltaCountingTaskListChangeListener();

		AbstractRepositoryConnector connector = new MockRepositoryConnector() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				throw new OperationCanceledException();
			}
		};
		ITask firstTask = new MockTask("1");
		ITask secondTask = new MockTask("2");
		HashSet<ITask> tasks = new HashSet<ITask>();
		tasks.add(firstTask);
		tasks.add(secondTask);
		SynchronizeTasksJob job = createSyncJob(connector, tasks);
		IStatus status = job.run(new NullProgressMonitor());
		assertEquals(Status.CANCEL_STATUS, status);
		assertEquals(2, listener.getDeltasFired());
		listener.tearDown();
	}

	public void testSyncWithSingleTaskDataRandomException() throws Exception {
		AbstractRepositoryConnector connector = new MockRepositoryConnector() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				throw new NullPointerException("Should just be logged");
			}
		};
		ITask firstTask = new MockTask("1");
		ITask secondTask = new MockTask("2");
		HashSet<ITask> tasks = new HashSet<ITask>();
		tasks.add(firstTask);
		tasks.add(secondTask);
		SynchronizeTasksJob job = createSyncJob(connector, tasks);
		job.run(new NullProgressMonitor());
	}

	public void testMonitorWithSingleTaskData() throws Exception {
		AbstractRepositoryConnector connector = new MockRepositoryConnector();
		ITask task = new MockTask("1");
		ITask task2 = new MockTask("2");
		HashSet<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		tasks.add(task2);
		SynchronizeTasksJob job = createSyncJob(connector, tasks);
		AssertionProgressMonitor monitor = new AssertionProgressMonitor();
		job.run(monitor);
		assertEquals("beginTask|subTask|subTask|done", monitor.getProgressLog());
	}

	public void testResetTaskStatusBeforeSync() throws Exception {
		DeltaCountingTaskListChangeListener listener = new DeltaCountingTaskListChangeListener();
		AbstractRepositoryConnector connector = new MockRepositoryConnector();
		ITask firstTask = new MockTask("1");
		ITask secondTask = new MockTask("2");
		((AbstractTask) firstTask).setStatus(new Status(IStatus.WARNING, "bundle", ""));
		((AbstractTask) secondTask).setStatus(new Status(IStatus.ERROR, "bundle", ""));
		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(firstTask);
		tasks.add(secondTask);
		SynchronizeTasksJob job = createSyncJob(connector, tasks);
		job.run(new NullProgressMonitor());
		assertEquals(4, listener.getDeltasFired());
		listener.tearDown();
	}

	private SynchronizeTasksJob createSyncJob(AbstractRepositoryConnector connector, Set<ITask> tasks) {
		return new SynchronizeTasksJob(taskList, taskDataManager, tasksModel, connector, repository, tasks);
	}

}