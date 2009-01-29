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

import java.text.MessageFormat;
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
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants.MutexSchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
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

	private final TaskList taskList;

	private final Set<ITask> allTasks;

	private final IRepositoryManager repositoryManager;

	private TaskRepository taskRepository;

	private Map<String, TaskRelation[]> relationsByTaskId;

	private boolean updateRelations;

	private final IRepositoryModel tasksModel;

	private SynchronizationSession session;

	private final List<IStatus> statuses;

	public SynchronizeTasksJob(TaskList taskList, TaskDataManager synchronizationManager, IRepositoryModel tasksModel,
			AbstractRepositoryConnector connector, TaskRepository taskRepository, Set<ITask> tasks) {
		this(taskList, synchronizationManager, tasksModel, connector, (IRepositoryManager) null, tasks);
		this.taskRepository = taskRepository;
	}

	public SynchronizeTasksJob(TaskList taskList, TaskDataManager synchronizationManager, IRepositoryModel tasksModel,
			AbstractRepositoryConnector connector, IRepositoryManager repositoryManager, Set<ITask> tasks) {
		super("Synchronizing Tasks (" + tasks.size() + " tasks)"); //$NON-NLS-1$ //$NON-NLS-2$
		this.taskList = taskList;
		this.taskDataManager = synchronizationManager;
		this.tasksModel = tasksModel;
		this.connector = connector;
		this.repositoryManager = repositoryManager;
		this.allTasks = tasks;
		this.statuses = new ArrayList<IStatus>();
		setRule(new MutexSchedulingRule());
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			if (taskRepository == null) {
				try {
					monitor.beginTask(Messages.SynchronizeTasksJob_Processing, allTasks.size() * 100);
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
						setName(MessageFormat.format(Messages.SynchronizeTasksJob_Synchronizing_Tasks__X_,
								taskRepository.getRepositoryLabel()));
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
										"Synchronization failed", e)); //$NON-NLS-1$
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
			monitor.beginTask(Messages.SynchronizeTasksJob_Processing, tasks.size() * 100);
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
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Synchronization failed", e)); //$NON-NLS-1$
		} finally {
			monitor.done();
		}
	}

	private boolean canGetMultiTaskData(TaskRepository taskRepository) {
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		return taskDataHandler != null && taskDataHandler.canGetMultiTaskData(taskRepository);
	}

	private void synchronizeTask(IProgressMonitor monitor, ITask task) throws CoreException {
		monitor.subTask(MessageFormat.format(Messages.SynchronizeTasksJob_Receiving_task_X, task.getSummary()));
		resetStatus(task);
		if (!isUser()) {
			monitor = Policy.backgroundMonitorFor(monitor);
		}
		String taskId = task.getTaskId();
		TaskData taskData = connector.getTaskData(taskRepository, taskId, monitor);
		if (taskData != null) {
			updateFromTaskData(taskRepository, task, taskData);
			return;
		}
		throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
				"Connector failed to return task data for task \"" + task + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private ITask synchronizeTask(IProgressMonitor monitor, String taskId) throws CoreException {
		monitor.subTask(MessageFormat.format(Messages.SynchronizeTasksJob_Receiving_task_X, taskId));
		if (!isUser()) {
			monitor = Policy.backgroundMonitorFor(monitor);
		}

		TaskData taskData = connector.getTaskData(taskRepository, taskId, monitor);
		if (taskData != null) {
			return createFromTaskData(taskRepository, taskId, taskData);
		}

		throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
				"Connector failed to return task data for task \"" + taskId + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void resetStatus(ITask task) {
		((AbstractTask) task).setStatus(null);
		taskList.notifySynchronizationStateChanged(task);
	}

	private void synchronizeTasks(IProgressMonitor monitor, final TaskRepository repository, Set<ITask> tasks)
			throws CoreException {
		monitor.subTask(MessageFormat.format(Messages.SynchronizeTasksJob_Receiving_X_tasks_from_X, tasks.size(),
				repository.getRepositoryLabel()));

		final Map<String, ITask> idToTask = new HashMap<String, ITask>();
		for (ITask task : tasks) {
			idToTask.put(task.getTaskId(), task);
		}

		TaskDataCollector collector = new TaskDataCollector() {
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
		connector.getTaskDataHandler().getMultiTaskData(repository, taskIds, collector, monitor);
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
