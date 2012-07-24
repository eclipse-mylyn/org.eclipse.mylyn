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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.SynchronizationManger;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataStore;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizationSession;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeTasksJob;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.core.data.TaskRelation.Direction;
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

	public void testGetSingleTaskData() throws Exception {
		AbstractRepositoryConnector connector = new MockRepositoryConnectorWithTaskDataHandler() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				return createTaskData("x");
			}
		};
		final ITask task = new MockTask("1");
		final AtomicBoolean putTaskData = new AtomicBoolean();
		final SynchronizationSession synchronizationSession = new SynchronizationSession();
		TaskDataManager customTaskDataManager = new TaskDataManager(taskDataStore, TasksUi.getRepositoryManager(),
				taskList, (TaskActivityManager) TasksUi.getTaskActivityManager(), new SynchronizationManger(
						(RepositoryModel) TasksUi.getRepositoryModel())) {
			@Override
			public void putUpdatedTaskData(ITask itask, TaskData taskData, boolean user, Object token,
					IProgressMonitor monitor) throws CoreException {
				putTaskData.set(true);
				assertEquals(task, itask);
				assertNotNull(taskData);
				assertFalse(user);
				assertEquals(synchronizationSession, token);
				assertNotNull(monitor);
			}
		};
		SynchronizeTasksJob job = createSyncJobWithManager(connector, Collections.singleton(task),
				customTaskDataManager);
		job.setSession(synchronizationSession);
		job.run(new NullProgressMonitor());
		assertTrue(putTaskData.get());
	}

	public void testGetSingleTaskDataPutFails() throws Exception {
		AbstractRepositoryConnector connector = new MockRepositoryConnectorWithTaskDataHandler() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				return createTaskData("x");
			}
		};
		final ITask task = new MockTask("1");
		TaskDataStore taskDataStore = new TaskDataStore(TasksUi.getRepositoryManager());
		final Status status = new Status(IStatus.ERROR, "bundle", "error");
		TaskDataManager customTaskDataManager = new TaskDataManager(taskDataStore, TasksUi.getRepositoryManager(),
				taskList, (TaskActivityManager) TasksUi.getTaskActivityManager(), new SynchronizationManger(
						(RepositoryModel) TasksUi.getRepositoryModel())) {
			@Override
			public void putUpdatedTaskData(ITask itask, TaskData taskData, boolean user, Object token,
					IProgressMonitor monitor) throws CoreException {
				throw new CoreException(status);
			}
		};
		SynchronizeTasksJob job = createSyncJobWithManager(connector, Collections.singleton(task),
				customTaskDataManager);
		job.run(new NullProgressMonitor());
		assertEquals(status, ((AbstractTask) task).getStatus());
	}

	public void testGetMultiTaskData() throws Exception {
		final AtomicBoolean multiGotCalled = new AtomicBoolean();
		DeltaCountingTaskListChangeListener listener = new DeltaCountingTaskListChangeListener();
		AbstractRepositoryConnector connector = new MockRepositoryConnectorWithTaskDataHandler() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				fail("Should use canGetMultiTaskData");
				return null;
			}

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
						multiGotCalled.set(true);
						assertEquals(3, taskIds.size());
						assertTrue(taskIds.contains("1"));
						assertTrue(taskIds.contains("2"));
						assertTrue(taskIds.contains("3"));
						assertNotNull(collector);
					}
				};
			}
		};
		final ITask task = new MockTask("1");
		final ITask task2 = new MockTask("2");
		final ITask task3 = new MockTask("3");
		((AbstractTask) task).setStatus(new Status(IStatus.WARNING, "bundle", ""));
		((AbstractTask) task2).setStatus(new Status(IStatus.WARNING, "bundle", ""));
		((AbstractTask) task3).setStatus(new Status(IStatus.WARNING, "bundle", ""));
		HashSet<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		tasks.add(task2);
		tasks.add(task3);
		SynchronizeTasksJob job = createSyncJob(connector, tasks);
		job.run(new NullProgressMonitor());
		assertEquals(3, listener.getDeltasFired());
		assertTrue(multiGotCalled.get());
		listener.tearDown();
	}

	public void testGetMultiTaskDataPutIntoManager() throws Exception {
		final MockRepositoryConnector connector = new MockRepositoryConnectorWithTaskDataHandler() {

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
						collector.accept(createTaskData("1"));
						collector.accept(createTaskData("2"));
						collector.failed("3", new Status(IStatus.ERROR, "bundle", "error"));
					}
				};
			}
		};
		final ITask task = new MockTask("1");
		final ITask task2 = new MockTask("2");
		final ITask task3 = new MockTask("3");
		HashSet<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		tasks.add(task2);
		tasks.add(task3);

		final AtomicInteger taskDataPut = new AtomicInteger();
		TaskDataManager customTaskDataManager = new TaskDataManager(taskDataStore, TasksUi.getRepositoryManager(),
				taskList, (TaskActivityManager) TasksUi.getTaskActivityManager(), new SynchronizationManger(
						(RepositoryModel) TasksUi.getRepositoryModel())) {
			@Override
			public void putUpdatedTaskData(ITask itask, TaskData taskData, boolean user, Object token,
					IProgressMonitor monitor) throws CoreException {
				taskDataPut.incrementAndGet();
			}
		};
		SynchronizeTasksJob job = createSyncJobWithManager(connector, tasks, customTaskDataManager);
		job.run(new NullProgressMonitor());
		assertEquals(2, taskDataPut.get());
		assertEquals("error", ((AbstractTask) task3).getStatus().getMessage());
	}

	public void testGetMultiTaskDataFails() throws Exception {
		final Status errorStatus = new Status(IStatus.ERROR, "bundle", "error");
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
						throw new CoreException(errorStatus);
					}
				};
			}
		};
		final ITask task = new MockTask("1");
		final ITask task2 = new MockTask("2");
		HashSet<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		tasks.add(task2);
		SynchronizeTasksJob job = createSyncJob(connector, tasks);
		job.run(new NullProgressMonitor());
		assertEquals(errorStatus, ((AbstractTask) task).getStatus());
		assertEquals(errorStatus, ((AbstractTask) task2).getStatus());
	}

	public void testGetSingleTaskDataWithRelations() throws Exception {
		final AtomicReference<List<String>> requestedTaskIds = new AtomicReference<List<String>>();
		requestedTaskIds.set(new ArrayList<String>());
		AbstractRepositoryConnector connector = new MockRepositoryConnectorWithTaskDataHandler() {
			@Override
			public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				requestedTaskIds.get().add(taskId);
				return createTaskData(taskId);
			}

			@Override
			public Collection<TaskRelation> getTaskRelations(TaskData taskData) {
				if (!taskData.getTaskId().equals("1")) {
					return null;
				}
				ArrayList<TaskRelation> relations = new ArrayList<TaskRelation>();
				relations.add(TaskRelation.dependency("1.dep.in", Direction.INWARD));
				relations.add(TaskRelation.dependency("1.dep.out", Direction.OUTWARD));
				relations.add(TaskRelation.parentTask("1.par"));
				relations.add(TaskRelation.subtask("1.sub"));
				relations.add(TaskRelation.subtask("1.sub2"));
				return relations;
			}
		};
		final ITask task = new MockTask("1");
		taskList.addTask(task);
		SynchronizeTasksJob job = createSyncJob(connector, Collections.singleton(task));
		job.run(new NullProgressMonitor());
		assertEquals(3, requestedTaskIds.get().size());
		assertTrue(requestedTaskIds.get().contains("1"));
		assertTrue(requestedTaskIds.get().contains("1.sub"));
		assertTrue(requestedTaskIds.get().contains("1.sub2"));

		ITask sub1 = taskList.getTask(MockRepositoryConnector.REPOSITORY_URL, "1.sub");
		assertNotNull(sub1);
		ITask sub2 = taskList.getTask(MockRepositoryConnector.REPOSITORY_URL, "1.sub2");
		assertNotNull(sub2);
		assertEquals(SynchronizationState.INCOMING_NEW, ((AbstractTask) sub1).getSynchronizationState());
		assertEquals(SynchronizationState.INCOMING_NEW, ((AbstractTask) sub2).getSynchronizationState());

		// same again but this time we already got them in the task list
		requestedTaskIds.get().clear();
		job = createSyncJob(connector, Collections.singleton(task));
		job.run(new NullProgressMonitor());
		assertEquals(1, requestedTaskIds.get().size());
		assertTrue(requestedTaskIds.get().contains("1"));
	}

	private SynchronizeTasksJob createSyncJob(AbstractRepositoryConnector connector, Set<ITask> tasks) {
		return new SynchronizeTasksJob(taskList, taskDataManager, tasksModel, connector, repository, tasks);
	}

	private SynchronizeTasksJob createSyncJobWithManager(AbstractRepositoryConnector connector, Set<ITask> tasks,
			TaskDataManager customTaskDataManager) {
		return new SynchronizeTasksJob(taskList, customTaskDataManager, tasksModel, connector, repository, tasks);
	}

	private TaskData createTaskData(String taskId) {
		return new TaskData(new TaskAttributeMapper(repository), MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, taskId);
	}
}