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

import java.util.Collection;
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
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
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
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.tests.connector.AssertionProgressMonitor;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnectorWithTaskDataHandler;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.tests.connector.MockTaskDataHandler;
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

	public void testMonitorWithMultiTaskData() throws Exception {
		AbstractRepositoryConnector connector = new MockRepositoryConnectorWithTaskDataHandler() {
			@Override
			public AbstractTaskDataHandler getTaskDataHandler() {
				return new MockTaskDataHandler(this) {
					@Override
					public boolean canGetMultiTaskData(TaskRepository repository) {
						return true;
					}

					@Override
					public void getMultiTaskData(TaskRepository repository, Set<String> taskIds,
							TaskDataCollector collector, IProgressMonitor monitor) throws CoreException {
					}
				};
			}
		};
		ITask task = new MockTask("1");
		ITask task2 = new MockTask("2");
		HashSet<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		tasks.add(task2);
		SynchronizeTasksJob job = createSyncJob(connector, tasks);
		AssertionProgressMonitor monitor = new AssertionProgressMonitor();
		job.run(monitor);
		assertEquals("beginTask|subTask|done", monitor.getProgressLog());
	}

	public void testGetSingleTaskDataError() throws Exception {
		final IStatus status = new Status(IStatus.WARNING, "bundle", "error");
		AbstractRepositoryConnector connector = new MockRepositoryConnector() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				throw new CoreException(status);
			}
		};
		ITask task = new MockTask("1");
		SynchronizeTasksJob job = createSyncJob(connector, Collections.singleton(task));
		job.run(new NullProgressMonitor());
		assertEquals(status, ((AbstractTask) task).getStatus());
	}

	public void testMultipleErrors() throws Exception {
		final IStatus status = new Status(IStatus.WARNING, "bundle", "error");
		AbstractRepositoryConnector connector = new MockRepositoryConnectorWithTaskDataHandler() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				if (!taskId.equals("3")) {
					throw new CoreException(status);
				}
				return createTaskData("x");
			}
		};
		DeltaCountingTaskListChangeListener listener = new DeltaCountingTaskListChangeListener();
		ITask task = new MockTask("1");
		ITask task2 = new MockTask("2");
		ITask task3 = new MockTask("3");
		HashSet<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		tasks.add(task2);
		tasks.add(task3);
		SynchronizeTasksJob job = createSyncJob(connector, tasks);
		job.run(new NullProgressMonitor());
		assertEquals(status, ((AbstractTask) task).getStatus());
		assertFalse(((AbstractTask) task).isSynchronizing());
		assertEquals(status, ((AbstractTask) task2).getStatus());
		assertFalse(((AbstractTask) task2).isSynchronizing());
		assertNull(((AbstractTask) task3).getStatus());
		Collection<IStatus> statuses = job.getStatuses();
		assertEquals(2, statuses.size());

		try {
			statuses.add(status);
			fail("Should not be modifiable");
		} catch (Exception e) {
			// expected
		}
		assertEquals(2, listener.getDeltasFired());
		listener.tearDown();
	}

	public void testGetSingleTaskDataNull() throws Exception {
		AbstractRepositoryConnector connector = new MockRepositoryConnector() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				return null;
			}
		};
		ITask task = new MockTask("1");
		SynchronizeTasksJob job = createSyncJob(connector, Collections.singleton(task));
		job.run(new NullProgressMonitor());
		IStatus status = ((AbstractTask) task).getStatus();
		assertEquals("Connector failed to return task data for task \"Mock Task: http://mockrepository.test-1\"",
				status.getMessage());
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertEquals(ITasksCoreConstants.ID_PLUGIN, status.getPlugin());
	}

	private SynchronizeTasksJob createSyncJob(AbstractRepositoryConnector connector, Set<ITask> tasks) {
		return new SynchronizeTasksJob(taskList, taskDataManager, tasksModel, connector, repository, tasks);
	}

	private TaskData createTaskData(String taskId) {
		return new TaskData(new TaskAttributeMapper(repository), MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, taskId);
	}
}