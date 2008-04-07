/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.SynchronizeJob;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.web.core.Policy;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class SynchronizeTasksJob extends SynchronizeJob {

	private static final String LABEL_SYNCHRONIZE_TASK = "Task Synchronization";

	private static final String LABEL_SYNCHRONIZING = "Synchronizing task ";

	private final AbstractRepositoryConnector connector;

	private boolean forced = false;

	private final RepositorySynchronizationManager synchronizationManager;

	private final AbstractTaskDataHandler taskDataHandler;

	private final TaskList taskList;

	private final TaskRepository taskRepository;

	private final Set<AbstractTask> tasks;

	public SynchronizeTasksJob(TaskList taskList, RepositorySynchronizationManager synchronizationManager,
			AbstractRepositoryConnector connector, TaskRepository taskRepository, Set<AbstractTask> tasks) {
		super(LABEL_SYNCHRONIZE_TASK + " (" + tasks.size() + " tasks)");
		this.taskList = taskList;
		this.synchronizationManager = synchronizationManager;
		this.connector = connector;
		this.taskRepository = taskRepository;
		this.tasks = tasks;
		this.taskDataHandler = connector.getTaskDataHandler();
	}

	/**
	 * Returns true, if synchronization was triggered manually and not by an automatic background job.
	 */
	public boolean isForced() {
		return forced;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(LABEL_SYNCHRONIZING, tasks.size());

			if (taskDataHandler != null && taskDataHandler.canGetMultiTaskData()) {
				try {
					synchronizeTasks(new SubProgressMonitor(monitor, tasks.size()), taskRepository, tasks);
				} catch (final CoreException e) {
					for (AbstractTask task : tasks) {
						updateStatus(taskRepository, task, e.getStatus());
					}
				}
			} else {
				for (AbstractTask task : tasks) {
					Policy.checkCanceled(monitor);
					synchronizeTask(monitor, task);
					monitor.worked(1);
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

	/**
	 * Indicates a manual synchronization. If set to true, a dialog will be displayed in case of errors.
	 */
	public void setForced(boolean forced) {
		this.forced = forced;
	}

	private void synchronizeTask(IProgressMonitor monitor, AbstractTask task) {
		monitor.subTask(task.getSummary());
		task.setSynchronizationStatus(null);
		taskList.notifyTaskChanged(task, false);
		try {
			if (taskDataHandler != null) {
				String taskId = task.getTaskId();
				RepositoryTaskData downloadedTaskData = taskDataHandler.getTaskData(taskRepository, taskId, monitor);
				if (downloadedTaskData != null) {
					updateFromTaskData(monitor, taskRepository, task, downloadedTaskData);
				} else {
					updateFromRepository(taskRepository, task, monitor);
				}
			} else {
				updateFromRepository(taskRepository, task, monitor);
			}
		} catch (final CoreException e) {
			updateStatus(taskRepository, task, e.getStatus());
		}
	}

	private void synchronizeTasks(IProgressMonitor monitor, TaskRepository repository, Set<AbstractTask> tasks)
			throws CoreException {
		monitor.subTask("Synchronizing tasks from: " + repository.getRepositoryLabel());
		Set<String> taskIds = new HashSet<String>();
		Map<String, AbstractTask> idToTask = new HashMap<String, AbstractTask>();
		for (AbstractTask task : tasks) {
			taskIds.add(task.getTaskId());
			idToTask.put(task.getTaskId(), task);
		}
		Set<RepositoryTaskData> newTaskData = taskDataHandler.getMultiTaskData(repository, taskIds,
				new SubProgressMonitor(monitor, tasks.size()));
		if (newTaskData != null && newTaskData.size() > 0) {
			for (RepositoryTaskData taskData : newTaskData) {
				if (taskData != null) {
					AbstractTask task = idToTask.remove(taskData.getId());
					if (task != null) {
						updateFromTaskData(new SubProgressMonitor(monitor, 1), repository, task, taskData);
						monitor.worked(1);
					}
				}
			}
		}

		if (newTaskData != null && newTaskData.size() < tasks.size()) {
			// set error status
		}
	}

	private void updateFromRepository(TaskRepository repository, AbstractTask task, IProgressMonitor monitor)
			throws CoreException {
		connector.updateTaskFromRepository(repository, task, new SubProgressMonitor(monitor, 1));
		updateTask(task);
	}

	private void updateFromTaskData(IProgressMonitor monitor, TaskRepository repository, AbstractTask task,
			RepositoryTaskData taskData) {
		// HACK: part of hack below
		//Date oldDueDate = repositoryTask.getDueDate();

		connector.updateTaskFromTaskData(repository, task, taskData);
		synchronizationManager.saveIncoming(task, taskData, forced);

		// HACK: Remove once connectors can get access to
		// TaskDataManager and do this themselves
//		if ((oldDueDate == null && repositoryTask.getDueDate() != null)
//				|| (oldDueDate != null && repositoryTask.getDueDate() == null)) {
//			TasksUiPlugin.getTaskActivityManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
//		} else if (oldDueDate != null && repositoryTask.getDueDate() != null
//				&& oldDueDate.compareTo(repositoryTask.getDueDate()) != 0) {
//			TasksUiPlugin.getTaskActivityManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
//		}

		updateTask(task);
	}

	/**
	 * Does not report synchronization failures if repository is offline.
	 */
	private void updateStatus(TaskRepository repository, AbstractTask task, IStatus status) {
		if (!forced && repository != null && repository.isOffline()) {
			task.setSynchronizing(false);
		} else {
			task.setSynchronizationStatus(status);
		}
	}

	private void updateTask(AbstractTask task) {
		task.setStale(false);
		task.setSynchronizing(false);
		if (task.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
				|| task.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
			taskList.notifyTaskChanged(task, true);
		} else {
			taskList.notifyTaskChanged(task, false);
		}
	}
}
