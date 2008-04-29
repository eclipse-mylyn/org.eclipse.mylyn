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
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.web.core.Policy;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class SynchronizeTasksJob extends SynchronizationJob {

	private final AbstractRepositoryConnector connector;

	private final ITaskDataManager taskDataManager;

	private final AbstractTaskDataHandler taskDataHandler;

	private final ITaskList taskList;

	private final TaskRepository taskRepository;

	private final Set<AbstractTask> tasks;

	public SynchronizeTasksJob(ITaskList taskList, ITaskDataManager synchronizationManager,
			AbstractRepositoryConnector connector, TaskRepository taskRepository, Set<AbstractTask> tasks) {
		super("Synchronizing Tasks (" + tasks.size() + " tasks)");
		this.taskList = taskList;
		this.taskDataManager = synchronizationManager;
		this.connector = connector;
		this.taskRepository = taskRepository;
		this.tasks = tasks;
		this.taskDataHandler = connector.getTaskDataHandler();
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Processing", tasks.size() * 100);

			if (taskDataHandler != null && taskDataHandler.canGetMultiTaskData()) {
				try {
					synchronizeTasks(new SubProgressMonitor(monitor, tasks.size() * 100), taskRepository, tasks);
				} catch (CoreException e) {
					for (AbstractTask task : tasks) {
						updateStatus(taskRepository, task, e.getStatus());
					}
				}
			} else {
				for (AbstractTask task : tasks) {
					Policy.checkCanceled(monitor);
					synchronizeTask(new SubProgressMonitor(monitor, 100), task);
				}
			}
		} catch (OperationCanceledException e) {
			for (AbstractTask task : tasks) {
				task.setSynchronizing(false);
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

	private void synchronizeTask(IProgressMonitor monitor, AbstractTask task) {
		monitor.subTask("Receiving task " + task.getSummary());
		task.setSynchronizationStatus(null);
		taskList.notifyTaskChanged(task, false);
		try {
			String taskId = task.getTaskId();
			if (!isUser()) {
				monitor = Policy.backgroundMonitorFor(monitor);
			}
			TaskData taskData = connector.getTaskData2(taskRepository, taskId, monitor);
			if (taskData != null) {
				updateFromTaskData(taskRepository, task, taskData);
			} else {
				RepositoryTaskData downloadedTaskData = connector.getTaskData(taskRepository, taskId, monitor);
				if (downloadedTaskData != null) {
					updateFromTaskData(taskRepository, task, downloadedTaskData);
				} else {
					// FIXME log/set error
				}
			}
		} catch (final CoreException e) {
			updateStatus(taskRepository, task, e.getStatus());
		}
	}

	private void synchronizeTasks(IProgressMonitor monitor, final TaskRepository repository, Set<AbstractTask> tasks)
			throws CoreException {
		monitor.subTask("Receiving " + tasks.size() + " tasks from " + repository.getRepositoryLabel());

		final Map<String, AbstractTask> idToTask = new HashMap<String, AbstractTask>();
		for (AbstractTask task : tasks) {
			idToTask.put(task.getTaskId(), task);
		}

		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(RepositoryTaskData taskData) {
				AbstractTask task = idToTask.remove(taskData.getTaskId());
				if (task != null) {
					updateFromTaskData(repository, task, taskData);
				}
			}

			@Override
			public void accept(TaskData taskData) {
				AbstractTask task = idToTask.remove(taskData.getTaskId());
				if (task != null) {
					updateFromTaskData(repository, task, taskData);
				}
			}
		};

		if (!isUser()) {
			monitor = Policy.backgroundMonitorFor(monitor);
		}
		taskDataHandler.getMultiTaskData(repository, Collections.unmodifiableSet(idToTask.keySet()), collector, monitor);
	}

	private void updateFromTaskData(TaskRepository repository, AbstractTask task, RepositoryTaskData taskData) {
		// HACK: part of hack below
		//Date oldDueDate = repositoryTask.getDueDate();

		boolean changed = connector.updateTaskFromTaskData(repository, task, taskData);
		if (!taskData.isPartial()) {
			taskDataManager.saveIncoming(task, taskData, isUser());
		} else if (changed && !task.isStale() && task.getSynchronizationState() == RepositoryTaskSyncState.SYNCHRONIZED) {
			// TODO move to synchronizationManager
			// set incoming marker for web tasks 
			task.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
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
		task.setSynchronizing(false);
		if (task.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
				|| task.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
			taskList.notifyTaskChanged(task, true);
		} else {
			taskList.notifyTaskChanged(task, false);
		}
	}

	private void updateFromTaskData(TaskRepository taskRepository, AbstractTask task, TaskData taskData) {
		try {
			taskDataManager.putUpdatedTaskData(task, taskData, isUser());
		} catch (CoreException e) {
			updateStatus(taskRepository, task, e.getStatus());
		}
	}

	private void updateStatus(TaskRepository repository, AbstractTask task, IStatus status) {
		task.setSynchronizationStatus(status);
		if (!isUser()) {
			task.setSynchronizing(false);
		}
		taskList.notifyTaskChanged(task, false);
	}

}
