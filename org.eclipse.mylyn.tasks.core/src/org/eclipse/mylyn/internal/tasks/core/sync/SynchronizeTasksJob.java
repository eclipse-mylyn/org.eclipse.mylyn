/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Collections;
import java.util.HashMap;
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
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.LegacyTaskDataCollector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class SynchronizeTasksJob extends SynchronizationJob {

	private final AbstractRepositoryConnector connector;

	private final ITaskDataManager taskDataManager;

	private final ITaskList taskList;

	private final TaskRepository taskRepository;

	private final Set<ITask> tasks;

	public SynchronizeTasksJob(ITaskList taskList, ITaskDataManager synchronizationManager,
			AbstractRepositoryConnector connector, TaskRepository taskRepository, Set<ITask> tasks) {
		super("Synchronizing Tasks (" + tasks.size() + " tasks)");
		this.taskList = taskList;
		this.taskDataManager = synchronizationManager;
		this.connector = connector;
		this.taskRepository = taskRepository;
		this.tasks = tasks;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Processing", tasks.size() * 100);

			if (canGetMultiTaskData()) {
				try {
					synchronizeTasks(new SubProgressMonitor(monitor, tasks.size() * 100), taskRepository, tasks);
				} catch (CoreException e) {
					for (ITask task : tasks) {
						updateStatus(taskRepository, task, e.getStatus());
					}
				}
			} else {
				for (ITask task : tasks) {
					Policy.checkCanceled(monitor);
					synchronizeTask(new SubProgressMonitor(monitor, 100), task);
				}
			}
		} catch (OperationCanceledException e) {
			for (ITask task : tasks) {
				((AbstractTask) task).setSynchronizing(false);
				taskList.notifyTaskChanged(task, false);
			}
			return Status.CANCEL_STATUS;
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Synchronization failed", e));
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}

	@SuppressWarnings("deprecation")
	private boolean canGetMultiTaskData() {
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			AbstractTaskDataHandler taskDataHandler = ((AbstractLegacyRepositoryConnector) connector).getLegacyTaskDataHandler();
			return taskDataHandler != null && taskDataHandler.canGetMultiTaskData();
		} else {
			org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
			return taskDataHandler != null && taskDataHandler.canGetMultiTaskData();
		}
	}

	@SuppressWarnings("deprecation")
	private void synchronizeTask(IProgressMonitor monitor, ITask task) {
		monitor.subTask("Receiving task " + task.getSummary());
		((AbstractTask) task).setSynchronizationStatus(null);
		taskList.notifyTaskChanged(task, false);
		try {
			String taskId = task.getTaskId();
			if (!isUser()) {
				monitor = Policy.backgroundMonitorFor(monitor);
			}
			if (connector instanceof AbstractLegacyRepositoryConnector) {
				RepositoryTaskData downloadedTaskData = ((AbstractLegacyRepositoryConnector) connector).getLegacyTaskData(
						taskRepository, taskId, monitor);
				if (downloadedTaskData != null) {
					updateFromTaskData(taskRepository, task, downloadedTaskData);
				}
			} else {
				TaskData taskData = connector.getTaskData(taskRepository, taskId, monitor);
				if (taskData != null) {
					updateFromTaskData(taskRepository, task, taskData);
				} else {
					throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Connector failed to return task data for task \"" + task + "\""));
				}
			}
		} catch (final CoreException e) {
			updateStatus(taskRepository, task, e.getStatus());
		}
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
					updateFromTaskData(repository, task, taskData);
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
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			((AbstractLegacyRepositoryConnector) connector).getLegacyTaskDataHandler().getMultiTaskData(repository,
					Collections.unmodifiableSet(idToTask.keySet()), collector, monitor);
		} else {
			connector.getTaskDataHandler().getMultiTaskData(repository,
					Collections.unmodifiableSet(idToTask.keySet()), collector, monitor);
		}

	}

	@SuppressWarnings("deprecation")
	private void updateFromTaskData(TaskRepository repository, ITask task, RepositoryTaskData taskData) {
		// HACK: part of hack below
		//Date oldDueDate = repositoryTask.getDueDate();

		boolean changed = ((AbstractLegacyRepositoryConnector) connector).updateTaskFromTaskData(repository, task,
				taskData);
		if (!taskData.isPartial()) {
			((TaskDataManager) taskDataManager).saveIncoming(task, taskData, isUser());
		} else if (changed && !task.isStale() && task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED) {
			// TODO move to synchronizationManager
			// set incoming marker for web tasks 
			((AbstractTask) task).setSynchronizationState(SynchronizationState.INCOMING);
		}

		// HACK: Remove once connectors can get access to
		// TaskDataManager and do this themselves
//		if ((oldDueDate == null && repositoryTask.getDueDate() != null)
//				|| (oldDueDate != null && repositoryTask.getDueDate() == null)) {
//			TasksUiPlugin.getTaskActivityManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
//		} else if (oldDueDate != null && repositoryTask.getDueDate() != null
//				&& oldDueDate.compareTo(repositoryTask.getDueDate()) != 0) {
//			TasksUiPlugin.getTaskActivityManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
//		}

		task.setStale(false);
		((AbstractTask) task).setSynchronizing(false);
		if (task.getSynchronizationState() == SynchronizationState.INCOMING
				|| task.getSynchronizationState() == SynchronizationState.CONFLICT) {
			taskList.notifyTaskChanged(task, true);
		} else {
			taskList.notifyTaskChanged(task, false);
		}
	}

	private void updateFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		try {
			taskDataManager.putUpdatedTaskData(task, taskData, isUser());
		} catch (CoreException e) {
			updateStatus(taskRepository, task, e.getStatus());
		}
	}

	private void updateStatus(TaskRepository repository, ITask task, IStatus status) {
		((AbstractTask) task).setSynchronizationStatus(status);
		if (!isUser()) {
			((AbstractTask) task).setSynchronizing(false);
		}
		taskList.notifyTaskChanged(task, false);
	}

}
