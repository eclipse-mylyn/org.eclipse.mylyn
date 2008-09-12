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

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.LegacyTaskDataCollector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.core.data.TaskRelation.Direction;
import org.eclipse.mylyn.tasks.core.data.TaskRelation.Kind;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class SynchronizeTasksJob extends SynchronizationJob {

	private final AbstractRepositoryConnector connector;

	private final TaskDataManager taskDataManager;

	private final ITaskList taskList;

	private final Set<ITask> allTasks;

	private final IRepositoryManager repositoryManager;

	private TaskRepository taskRepository;

	private Map<String, TaskRelation[]> relationsByTaskId;

	private boolean updateRelations;

	private final IRepositoryModel tasksModel;

	private SynchronizationSession session;

	private final List<IStatus> statuses;

	public SynchronizeTasksJob(ITaskList taskList, TaskDataManager synchronizationManager, IRepositoryModel tasksModel,
			AbstractRepositoryConnector connector, TaskRepository taskRepository, Set<ITask> tasks) {
		super("Synchronizing Tasks (" + tasks.size() + " tasks)");
		this.taskList = taskList;
		this.taskDataManager = synchronizationManager;
		this.tasksModel = tasksModel;
		this.connector = connector;
		this.taskRepository = taskRepository;
		this.allTasks = tasks;
		this.repositoryManager = null;
		this.statuses = new ArrayList<IStatus>();
	}

	public SynchronizeTasksJob(ITaskList taskList, TaskDataManager synchronizationManager, IRepositoryModel tasksModel,
			AbstractRepositoryConnector connector, IRepositoryManager repositoryManager, Set<ITask> tasks) {
		super("Synchronizing Tasks (" + tasks.size() + " tasks)");
		this.taskList = taskList;
		this.taskDataManager = synchronizationManager;
		this.tasksModel = tasksModel;
		this.connector = connector;
		this.repositoryManager = repositoryManager;
		this.allTasks = tasks;
		this.statuses = new ArrayList<IStatus>();
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			if (taskRepository == null) {
				try {
					monitor.beginTask("Processing", allTasks.size() * 100);
					// group tasks by repository
					Map<TaskRepository, Set<ITask>> tasksByRepository = new HashMap<TaskRepository, Set<ITask>>();
					for (ITask task : allTasks) {
						TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
								task.getRepositoryUrl());
						Set<ITask> tasks = tasksByRepository.get(repository);
						if (tasks == null) {
							tasks = new HashSet<ITask>();
							tasksByRepository.put(repository, tasks);
						}
						tasks.add(task);
					}
					// synchronize tasks for each repositories
					for (TaskRepository taskRepository : tasksByRepository.keySet()) {
						setName("Synchronizing Tasks (" + taskRepository.getRepositoryLabel() + ")");
						this.taskRepository = taskRepository;
						Set<ITask> repositoryTasks = tasksByRepository.get(taskRepository);
						run(repositoryTasks, new SubProgressMonitor(monitor, repositoryTasks.size() * 100));
					}
				} finally {
					monitor.done();
				}
			} else {
				run(allTasks, monitor);
			}
		} catch (OperationCanceledException e) {
			for (ITask task : allTasks) {
				((AbstractTask) task).setSynchronizing(false);
				taskList.notifyElementChanged(task);
			}
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	private void run(Set<ITask> tasks, IProgressMonitor monitor) {
		relationsByTaskId = new HashMap<String, TaskRelation[]>();
		updateRelations = true;
		runInternal(tasks, monitor);
		synchronizedTaskRelations(monitor, relationsByTaskId);
	}

	public void synchronizedTaskRelations(IProgressMonitor monitor, Map<String, TaskRelation[]> relationsByTaskId) {
		updateRelations = false;
		for (String taskId : relationsByTaskId.keySet()) {
			ITask parentTask = taskList.getTask(taskRepository.getRepositoryUrl(), taskId);
			if (parentTask instanceof ITaskContainer) {
				Set<ITask> removedChildTasks = new HashSet<ITask>(((ITaskContainer) parentTask).getChildren());

				TaskRelation[] relations = relationsByTaskId.get(taskId);
				for (TaskRelation relation : relations) {
					if (relation.getDirection() == Direction.OUTWARD && relation.getKind() == Kind.CONTAINMENT) {
						ITask task = taskList.getTask(taskRepository.getRepositoryUrl(), relation.getTaskId());
						if (task == null) {
							try {
								task = synchronizeTask(monitor, relation.getTaskId());
							} catch (CoreException e) {
								StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
										"Synchronization failed", e));
							}
						} else {
							removedChildTasks.remove(task);
						}

						if (task != null) {
							taskList.addTask(task, (AbstractTaskContainer) parentTask);
						}
					}
				}

				for (ITask task : removedChildTasks) {
					taskList.removeFromContainer((AbstractTaskContainer) parentTask, task);
				}
			}
		}
	}

	private void runInternal(Set<ITask> tasks, IProgressMonitor monitor) {
		try {
			monitor.beginTask("Processing", tasks.size() * 100);
			if (canGetMultiTaskData(taskRepository)) {
				try {
					for (ITask task : tasks) {
						resetStatus(task);
					}
					synchronizeTasks(new SubProgressMonitor(monitor, tasks.size() * 100), taskRepository, tasks);
				} catch (CoreException e) {
					for (ITask task : tasks) {
						updateStatus(taskRepository, task, e.getStatus());
					}
				}
			} else {
				for (ITask task : tasks) {
					Policy.checkCanceled(monitor);
					resetStatus(task);
					try {
						synchronizeTask(new SubProgressMonitor(monitor, 100), task);
					} catch (CoreException e) {
						updateStatus(taskRepository, task, e.getStatus());
					}
				}
			}
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Synchronization failed", e));
		} finally {
			monitor.done();
		}
	}

	@SuppressWarnings("deprecation")
	private boolean canGetMultiTaskData(TaskRepository taskRepository) {
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			AbstractTaskDataHandler taskDataHandler = ((AbstractLegacyRepositoryConnector) connector).getLegacyTaskDataHandler();
			return taskDataHandler != null && taskDataHandler.canGetMultiTaskData();
		} else {
			org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
			return taskDataHandler != null && taskDataHandler.canGetMultiTaskData(taskRepository);
		}
	}

	@SuppressWarnings("deprecation")
	private void synchronizeTask(IProgressMonitor monitor, ITask task) throws CoreException {
		monitor.subTask("Receiving task " + task.getSummary());
		resetStatus(task);
		if (!isUser()) {
			monitor = Policy.backgroundMonitorFor(monitor);
		}
		String taskId = task.getTaskId();
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			RepositoryTaskData downloadedTaskData = ((AbstractLegacyRepositoryConnector) connector).getLegacyTaskData(
					taskRepository, taskId, monitor);
			if (downloadedTaskData != null) {
				try {
					updateFromTaskData(taskRepository, task, downloadedTaskData);
				} catch (CoreException e) {
					updateStatus(taskRepository, task, e.getStatus());
				}
				return;
			}
		} else {
			TaskData taskData = connector.getTaskData(taskRepository, taskId, monitor);
			if (taskData != null) {
				updateFromTaskData(taskRepository, task, taskData);
				return;
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
				"Connector failed to return task data for task \"" + task + "\""));
	}

	private ITask synchronizeTask(IProgressMonitor monitor, String taskId) throws CoreException {
		monitor.subTask("Receiving task " + taskId);
		if (!isUser()) {
			monitor = Policy.backgroundMonitorFor(monitor);
		}
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			RepositoryTaskData downloadedTaskData = ((AbstractLegacyRepositoryConnector) connector).getLegacyTaskData(
					taskRepository, taskId, monitor);
			if (downloadedTaskData != null) {
				return updateFromTaskData(taskRepository, null, downloadedTaskData);
			}
		} else {
			TaskData taskData = connector.getTaskData(taskRepository, taskId, monitor);
			if (taskData != null) {
				return createFromTaskData(taskRepository, taskId, taskData);
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
				"Connector failed to return task data for task \"" + taskId + "\""));
	}

	private void resetStatus(ITask task) {
		((AbstractTask) task).setStatus(null);
		taskList.notifySynchronizationStateChanged(task);
	}

	@SuppressWarnings("deprecation")
	private void synchronizeTasks(IProgressMonitor monitor, final TaskRepository repository, Set<ITask> tasks)
			throws CoreException {
		monitor.subTask("Receiving " + tasks.size() + " tasks from " + repository.getRepositoryLabel());

		final Map<String, ITask> idToTask = new HashMap<String, ITask>();
		for (ITask task : tasks) {
			idToTask.put(task.getTaskId(), task);
		}

		LegacyTaskDataCollector collector = new LegacyTaskDataCollector() {
			@Override
			public void accept(RepositoryTaskData taskData) {
				ITask task = idToTask.remove(taskData.getTaskId());
				if (task != null) {
					try {
						updateFromTaskData(repository, task, taskData);
					} catch (CoreException e) {
						StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
								"Synchronization failed", e));
					}
				}
			}

			@Override
			public void accept(TaskData taskData) {
				ITask task = idToTask.remove(taskData.getTaskId());
				if (task != null) {
					updateFromTaskData(repository, task, taskData);
				}
			}
		};

		if (!isUser()) {
			monitor = Policy.backgroundMonitorFor(monitor);
		}
		Set<String> taskIds = Collections.unmodifiableSet(new HashSet<String>(idToTask.keySet()));
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			((AbstractLegacyRepositoryConnector) connector).getLegacyTaskDataHandler().getMultiTaskData(repository,
					taskIds, collector, monitor);
		} else {
			connector.getTaskDataHandler().getMultiTaskData(repository, taskIds, collector, monitor);
		}

	}

	@SuppressWarnings("deprecation")
	private ITask updateFromTaskData(TaskRepository repository, ITask task, RepositoryTaskData taskData)
			throws CoreException {
		// HACK: part of hack below
		//Date oldDueDate = repositoryTask.getDueDate();

//		boolean changed = ((AbstractLegacyRepositoryConnector) connector).updateTaskFromTaskData(repository, task,
//				taskData);
//		if (!taskData.isPartial()) {
//			((TaskDataManager) taskDataManager).saveIncoming(task, taskData, isUser());
//		} else if (changed && !task.isStale() && task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED) {
//			// TODO move to synchronizationManager
//			// set incoming marker for web tasks 
//			((AbstractTask) task).setSynchronizationState(SynchronizationState.INCOMING);
//		}

		TaskFactory factory = new TaskFactory(repository, true, isUser(),
				(AbstractLegacyRepositoryConnector) connector, taskDataManager, taskList);
		task = factory.createTask(taskData, new NullProgressMonitor());

		// HACK: Remove once connectors can get access to
		// TaskDataManager and do this themselves
//		if ((oldDueDate == null && repositoryTask.getDueDate() != null)
//				|| (oldDueDate != null && repositoryTask.getDueDate() == null)) {
//			TasksUiPlugin.getTaskActivityManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
//		} else if (oldDueDate != null && repositoryTask.getDueDate() != null
//				&& oldDueDate.compareTo(repositoryTask.getDueDate()) != 0) {
//			TasksUiPlugin.getTaskActivityManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
//		}

		((AbstractTask) task).setSynchronizing(false);
		taskList.notifySynchronizationStateChanged(task);

		return task;
	}

	private void updateFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		try {
			taskDataManager.putUpdatedTaskData(task, taskData, isUser(), getSession());
			if (updateRelations) {
				Collection<TaskRelation> relations = connector.getTaskRelations(taskData);
				if (relations != null) {
					relationsByTaskId.put(task.getTaskId(), relations.toArray(new TaskRelation[0]));
				}
			}
		} catch (CoreException e) {
			updateStatus(taskRepository, task, e.getStatus());
		}
	}

	private ITask createFromTaskData(TaskRepository taskRepository, String taskId, TaskData taskData)
			throws CoreException {
		ITask task = tasksModel.createTask(taskRepository, taskData.getTaskId());
		((AbstractTask) task).setSynchronizationState(SynchronizationState.INCOMING_NEW);
		taskDataManager.putUpdatedTaskData(task, taskData, isUser(), getSession());
		return task;
	}

	private void updateStatus(TaskRepository repository, ITask task, IStatus status) {
		statuses.add(status);
		((AbstractTask) task).setStatus(status);
		if (!isUser()) {
			((AbstractTask) task).setSynchronizing(false);
		}
		taskList.notifyElementChanged(task);
	}

	public SynchronizationSession getSession() {
		return session;
	}

	public void setSession(SynchronizationSession session) {
		this.session = session;
	}

	public Collection<IStatus> getStatuses() {
		return Collections.unmodifiableCollection(statuses);
	}

}
