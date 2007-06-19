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

package org.eclipse.mylyn.tasks.ui;

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
import org.eclipse.mylyn.internal.tasks.ui.TaskFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
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

				final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
						repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
				if (repository == null) {
					repositoryTask.setStatus(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0,
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
				repositoryTask.setStatus(null);
				tasks.add(repositoryTask);
				repositoryTask.setCurrentlySynchronizing(true);
				TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
			}

			if (monitor.isCanceled()) {

				for (final AbstractTask repositoryTask : repositoryTasks) {
					repositoryTask.setCurrentlySynchronizing(false);
					TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
				}

				throw new OperationCanceledException();
			}

			for (TaskRepository repository : repToTasks.keySet()) {
				Set<AbstractTask> tasksToSynch = repToTasks.get(repository);
				if (taskDataHandler != null) {
					try {
						synchronizeTasks(new SubProgressMonitor(monitor, tasksToSynch.size()), repository, tasksToSynch);
					} catch (final CoreException e) {
						for (AbstractTask task : tasksToSynch) {
							task.setStatus(e.getStatus());
						}
						if (forced) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									StatusManager.displayStatus("Task Synchronization Failed", e.getStatus());
								}
							});
						}
					}
				} else {
					updateFromRepository(repository, tasksToSynch, new SubProgressMonitor(monitor, 1));
				}
			}

		} catch (Exception e) {
			StatusManager.fail(e, "Could not synchronize task", false);
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
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
		if (taskDataHandler != null) {
			Set<RepositoryTaskData> newTaskData = taskDataHandler.getTaskData(repository, taskIds,
					new SubProgressMonitor(monitor, tasks.size()));
			if (newTaskData != null && newTaskData.size() > 0) {
				for (RepositoryTaskData taskData : newTaskData) {
					if (monitor.isCanceled())
						throw new OperationCanceledException("Synchronization cancelled by user");
					AbstractTask task = idToTask.get(taskData.getId());
					if (task != null) {
						try {
							updateTask(new SubProgressMonitor(monitor, 1), repository, task, taskData);

						} catch (final CoreException e) {
							task.setStatus(e.getStatus());
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
			} else if (newTaskData == null) {

				for (AbstractTask repositoryTask : tasks) {
					try {
						String taskId = repositoryTask.getTaskId();
						RepositoryTaskData downloadedTaskData = taskDataHandler.getTaskData(repository, taskId, monitor);

						if (downloadedTaskData != null) {
							updateTask(new SubProgressMonitor(monitor, 1), repository, repositoryTask,
									downloadedTaskData);
						} else {
							connector.updateTaskFromRepository(repository, repositoryTask, monitor);
						}

					} catch (final CoreException e) {
						repositoryTask.setStatus(e.getStatus());
						if (forced) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									StatusManager.displayStatus("Task Synchronization Failed", e.getStatus());
								}
							});
						}
					}
				}
			}
		}
	}

	private void updateFromRepository(TaskRepository repository, Set<AbstractTask> tasks, IProgressMonitor monitor) {
		for (AbstractTask task : tasks) {
			try {
				connector.updateTaskFromRepository(repository, task, new SubProgressMonitor(monitor, 1));
			} catch (CoreException ce) {
				task.setStatus(ce.getStatus());
				continue;
			}
			task.setStale(false);
			task.setCurrentlySynchronizing(false);
		}
	}

	private void updateTask(IProgressMonitor monitor, TaskRepository repository, AbstractTask repositoryTask,
			RepositoryTaskData downloadedTaskData) throws CoreException {
		if (downloadedTaskData != null) {
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

		} else {
			connector.updateTaskFromRepository(repository, repositoryTask, new SubProgressMonitor(monitor, 1));
		}

		repositoryTask.setCurrentlySynchronizing(false);
		repositoryTask.setStale(false);
		if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING
				|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, true);
		} else {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
		}

	}
}
//	private void synchronizeTask(IProgressMonitor monitor, AbstractTask repositoryTask) throws CoreException {
//	monitor.subTask(repositoryTask.getSummary());
//
//	final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
//			repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
//	if (repository == null) {
//		throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0,
//				"Associated repository could not be found. Ensure proper repository configuration of "
//						+ repositoryTask.getRepositoryUrl() + " in " + TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".",
//				null));
//	}
//
//	TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
//	AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
//	if (taskDataHandler != null) {
//		String taskId = repositoryTask.getTaskId();
//		RepositoryTaskData downloadedTaskData = taskDataHandler.getTaskData(repository, taskId, monitor);
//
//		if (downloadedTaskData != null) {
//			// HACK: part of hack below
//			Date oldDueDate = repositoryTask.getDueDate();
//			
//			TaskFactory factory = new TaskFactory(repository, true, forced);
//			repositoryTask = factory.createTask(downloadedTaskData, new SubProgressMonitor(monitor, 1));
//
//			// HACK: Remove once connectors can get access to
//			// TaskDataManager and do this themselves
//			if ((oldDueDate == null && repositoryTask.getDueDate() != null)
//					|| (oldDueDate != null && repositoryTask.getDueDate() == null)) {
//				TasksUiPlugin.getTaskListManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
//			} else if (oldDueDate != null && repositoryTask.getDueDate() != null
//					&& oldDueDate.compareTo(repositoryTask.getDueDate()) != 0) {
//				TasksUiPlugin.getTaskListManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
//			}
//
//			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING
//					|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
//				TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, true);
//			}
//		} else {
//			connector.updateTaskFromRepository(repository, repositoryTask, monitor);
//		}
//	} else {
//		connector.updateTaskFromRepository(repository, repositoryTask, monitor);
//	}
//	
//	repositoryTask.setStale(false);
//}

