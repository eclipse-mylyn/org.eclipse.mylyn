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
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskDataHandler;
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

	public SynchronizeTaskJob(AbstractRepositoryConnector connector, Set<AbstractTask> repositoryTasks) {
		super(LABEL_SYNCHRONIZE_TASK + " (" + repositoryTasks.size() + " tasks)");
		this.connector = connector;
		this.repositoryTasks = repositoryTasks;
	}

	/**
	 * Returns true, if synchronization was triggered manually and not by an
	 * automatic background job.
	 */
	public boolean isForced() {
		return forced;
	}

	/**
	 * Indicates a manual synchronization. If set to true, a dialog will be
	 * displayed in case of errors.
	 */
	public void setForced(boolean forced) {
		this.forced = forced;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(LABEL_SYNCHRONIZING, repositoryTasks.size());
			setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);

			for (final AbstractTask repositoryTask : repositoryTasks) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				repositoryTask.setStatus(null);

				try {
					synchronizeTask(monitor, repositoryTask);
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

				// TODO: Set in connector.updateTask
				repositoryTask.setCurrentlySynchronizing(false);

				TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
				// TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);

				monitor.worked(1);
			}
			// TasksUiPlugin.getDefault().getTaskDataManager().save();

		} catch (Exception e) {
			StatusManager.fail(e, "Could not download report", false);
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}

	private void synchronizeTask(IProgressMonitor monitor, AbstractTask repositoryTask) throws CoreException {
		monitor.subTask(repositoryTask.getSummary());

		final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
		if (repository == null) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0,
					"Associated repository could not be found. Ensure proper repository configuration of "
							+ repositoryTask.getRepositoryUrl() + " in " + TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".",
					null));
		}

		TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
		ITaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		if (taskDataHandler != null) {
			String taskId = repositoryTask.getTaskId();
			RepositoryTaskData downloadedTaskData = taskDataHandler.getTaskData(repository, taskId, monitor);

			if (downloadedTaskData != null) {
				// HACK: part of hack below
				Date oldDueDate = repositoryTask.getDueDate();
				
				TaskFactory factory = new TaskFactory(repository, true, forced);
				repositoryTask = factory.createTask(downloadedTaskData, new SubProgressMonitor(monitor, 1));

//				TasksUiPlugin.getSynchronizationManager().saveIncoming(repositoryTask, downloadedTaskData, forced);
//				connector.updateTaskFromTaskData(repository, repositoryTask, downloadedTaskData);
//				repositoryTask.dropSubTasks();
//				for (String subId : taskDataHandler.getSubTaskIds(downloadedTaskData)) {
//					if (subId == null || subId.trim().equals("")) {
//						continue;
//					}
//					AbstractTask subTask = factory.createTaskFromExistingId(repository, subId, false,
//							new SubProgressMonitor(monitor, 1));
//					if (subTask != null) {
//						repositoryTask.addSubTask(subTask);
//					}
//				}

				// HACK: Remove once connectors can get access to
				// TaskDataManager and do this themselves
				if ((oldDueDate == null && repositoryTask.getDueDate() != null)
						|| (oldDueDate != null && repositoryTask.getDueDate() == null)) {
					TasksUiPlugin.getTaskListManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
				} else if (oldDueDate != null && repositoryTask.getDueDate() != null
						&& oldDueDate.compareTo(repositoryTask.getDueDate()) != 0) {
					TasksUiPlugin.getTaskListManager().setDueDate(repositoryTask, repositoryTask.getDueDate());
				}

				if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING
						|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
					TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, true);
				}
			} else {
				connector.updateTaskFromRepository(repository, repositoryTask, monitor);
			}
		} else {
			connector.updateTaskFromRepository(repository, repositoryTask, monitor);
		}
		
		repositoryTask.setStale(false);
	}
}