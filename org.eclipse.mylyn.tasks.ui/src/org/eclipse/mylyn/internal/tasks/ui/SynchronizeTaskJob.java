/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Date;
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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
class SynchronizeTaskJob extends Job {

	private static final String LABEL_SYNCHRONIZING = "Synchronizing task ";

	private static final String LABEL_SYNCHRONIZE_TASK = "Task Synchronization";

	private final AbstractRepositoryConnector connector;

	private Set<AbstractTask> repositoryTasks;

	private boolean forced = false;

	private AbstractTaskDataHandler taskDataHandler;

	private Map<TaskRepository, Set<AbstractTask>> repToTasks;

	public SynchronizeTaskJob(AbstractRepositoryConnector connector, Set<AbstractTask> repositoryTasks) {
		super(LABEL_SYNCHRONIZE_TASK + " (" + repositoryTasks.size() + " tasks)");
		this.connector = connector;
		this.repositoryTasks = repositoryTasks;
		this.taskDataHandler = connector.getTaskDataHandler();
	}

	/**
	 * Returns true, if synchronization was triggered manually and not by an automatic background job.
	 */
	public boolean isForced() {
		return forced;
	}

	/**
	 * Indicates a manual synchronization. If set to true, a dialog will be displayed in case of errors.
	 */
	public void setForced(boolean forced) {
		this.forced = forced;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			repToTasks = new HashMap<TaskRepository, Set<AbstractTask>>();
			monitor.beginTask(LABEL_SYNCHRONIZING, repositoryTasks.size());
			setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);

			for (final AbstractTask repositoryTask : repositoryTasks) {

				if (monitor.isCanceled()) {
					break;
				}

				if (taskDataHandler != null && taskDataHandler.canGetMultiTaskData()) {
					// Multi synch supported...
					TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
							repositoryTask.getConnectorKind(), repositoryTask.getRepositoryUrl());

					if (repository == null) {
						repositoryTask.setSynchronizationStatus(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0,
								"Associated repository could not be found. Ensure proper repository configuration of "
										+ repositoryTask.getRepositoryUrl() + " in "
										+ TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".", null));
						continue;
					}

					Set<AbstractTask> tasks = repToTasks.get(repository);
					if (tasks == null) {
						tasks = new HashSet<AbstractTask>();
						repToTasks.put(repository, tasks);
					}
					repositoryTask.setSynchronizationStatus(null);
					tasks.add(repositoryTask);
					repositoryTask.setSynchronizing(true);
					TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
				} else {
					// Single synch supported...
					synchronizeTask(monitor, repositoryTask);
				}
			}

			if (monitor.isCanceled()) {

				for (final AbstractTask repositoryTask : repositoryTasks) {
					repositoryTask.setSynchronizing(false);
					TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
				}

				return Status.CANCEL_STATUS;
			}

			for (TaskRepository repository : repToTasks.keySet()) {
				Set<AbstractTask> tasksToSynch = repToTasks.get(repository);
				try {
					synchronizeTasks(new SubProgressMonitor(monitor, tasksToSynch.size()), repository, tasksToSynch);
				} catch (OperationCanceledException e) {
					return Status.CANCEL_STATUS;
				} catch (final CoreException e) {
					for (AbstractTask task : tasksToSynch) {
						updateStatus(repository, task, e.getStatus());
					}
					if (forced) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								StatusManager.displayStatus("Task Synchronization Failed", e.getStatus());
							}
						});
					}
				}
			}

		} catch (Exception e) {
			StatusManager.fail(e, "Synchronization failed", false);
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}

	private void synchronizeTask(IProgressMonitor monitor, AbstractTask task) {
		monitor.subTask(task.getSummary());

		task.setSynchronizationStatus(null);

		final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				task.getConnectorKind(), task.getRepositoryUrl());
		try {
			if (repository == null) {
				throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0,
						"Associated repository could not be found. Ensure proper repository configuration of "
								+ task.getRepositoryUrl() + " in " + TasksUiPlugin.LABEL_VIEW_REPOSITORIES
								+ ".", null));
			}

			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task, false);
			if (taskDataHandler != null) {
				String taskId = task.getTaskId();
				RepositoryTaskData downloadedTaskData = taskDataHandler.getTaskData(repository, taskId, monitor);

				if (downloadedTaskData != null) {
					updateTask(monitor, repository, task, downloadedTaskData);
				} else {
					updateFromRepository(repository, task, monitor);
				}
			} else {
				updateFromRepository(repository, task, monitor);
			}
		} catch (final CoreException e) {
			updateStatus(repository, task, e.getStatus());
			if (forced) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						StatusManager.displayStatus("Task Synchronization Failed", e.getStatus());
					}
				});
			}
		}

		monitor.worked(1);
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
				if (monitor.isCanceled())
					throw new OperationCanceledException("Synchronization cancelled by user");
				if (taskData != null) {
					AbstractTask task = idToTask.remove(taskData.getId());
					if (task != null) {
						try {
							updateTask(new SubProgressMonitor(monitor, 1), repository, task, taskData);
						} catch (final CoreException e) {
							updateStatus(repository, task, e.getStatus());
							if (forced) {
								PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
									public void run() {
										StatusManager.displayStatus("Task Synchronization Failed", e.getStatus());
									}
								});
							}
						}
						monitor.worked(1);
					}
				}
			}
		}

		if (newTaskData != null && newTaskData.size() < tasks.size()) {
			//set error status
		}
	}

	private void updateFromRepository(TaskRepository repository, AbstractTask task, IProgressMonitor monitor)
			throws CoreException {
		connector.updateTaskFromRepository(repository, task, new SubProgressMonitor(monitor, 1));
		task.setStale(false);
		task.setSynchronizing(false);
		if (task.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
				|| task.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task, true);
		} else {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task, false);
		}
	}

	private void updateTask(IProgressMonitor monitor, TaskRepository repository, AbstractTask repositoryTask,
			RepositoryTaskData downloadedTaskData) throws CoreException {

		if (downloadedTaskData == null)
			return;

		// HACK: part of hack below
		Date oldDueDate = repositoryTask.getDueDate();

		TaskFactory factory = new TaskFactory(repository, true, forced);
		repositoryTask = factory.createTask(downloadedTaskData, new SubProgressMonitor(monitor, 1));

		// HACK: Remove once connectors can get access to
		// TaskDataManager and do this themselves
		if ((oldDueDate == null && repositoryTask.getDueDate() != null)
				|| (oldDueDate != null && repositoryTask.getDueDate() == null)) {
			TasksUiPlugin.getTaskListManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
		} else if (oldDueDate != null && repositoryTask.getDueDate() != null
				&& oldDueDate.compareTo(repositoryTask.getDueDate()) != 0) {
			TasksUiPlugin.getTaskListManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
		}

		repositoryTask.setSynchronizing(false);
		repositoryTask.setStale(false);
		if (repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
				|| repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, true);
		} else {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
		}
	}
}
