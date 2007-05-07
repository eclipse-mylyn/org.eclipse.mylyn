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

package org.eclipse.mylar.tasks.ui;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
class SynchronizeTaskJob extends Job {

	private static final String LABEL_SYNCHRONIZING = "Synchronizing ";

	private static final String LABEL_SYNCHRONIZE_TASK = "Task Synchronization";

	private final AbstractRepositoryConnector connector;

	private Set<AbstractRepositoryTask> repositoryTasks;

	private boolean forceSync = false;

	public SynchronizeTaskJob(AbstractRepositoryConnector connector, Set<AbstractRepositoryTask> repositoryTasks) {
		super(LABEL_SYNCHRONIZE_TASK + " (" + repositoryTasks.size() + " tasks)");
		this.connector = connector;
		this.repositoryTasks = repositoryTasks;
	}

	public void setForceSynch(boolean forceUpdate) {
		this.forceSync = forceUpdate;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(LABEL_SYNCHRONIZE_TASK, repositoryTasks.size());
			setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);

			for (final AbstractRepositoryTask repositoryTask : repositoryTasks) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				repositoryTask.setStatus(null);

				try {
					syncTask(monitor, repositoryTask);
				} catch (final CoreException e) {
					repositoryTask.setStatus(e.getStatus());
					if (forceSync) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MylarStatusHandler.displayStatus("Task synchronization failed", e.getStatus());
							}
						});
					}
				}

				// TODO: Set in connector.updateTask
				repositoryTask.setCurrentlySynchronizing(false);
				
				TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(repositoryTask);
				// TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);

				monitor.worked(1);
			}
			// TasksUiPlugin.getDefault().getTaskDataManager().save();

		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not download report", false);
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}

	private void syncTask(IProgressMonitor monitor, final AbstractRepositoryTask repositoryTask) throws CoreException {
		monitor.setTaskName(LABEL_SYNCHRONIZING + repositoryTask.getSummary());

		final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
		if (repository == null) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0,
					"Associated repository could not be found. Ensure proper repository configuration of "
							+ repositoryTask.getRepositoryUrl() + " in " + TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".",
					null));
		}

		TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(repositoryTask);
		ITaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		if (taskDataHandler != null) {
			String taskId = repositoryTask.getTaskId();
			RepositoryTaskData downloadedTaskData = taskDataHandler.getTaskData(repository, taskId);

			if (downloadedTaskData != null) {
				TasksUiPlugin.getSynchronizationManager().saveIncoming(repositoryTask, downloadedTaskData, forceSync);
				connector.updateTaskFromTaskData(repository, repositoryTask, downloadedTaskData);
				if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING
						|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
					TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
				}
			} else {
				connector.updateTaskFromRepository(repository, repositoryTask);
			}
		} else {
			connector.updateTaskFromRepository(repository, repositoryTask);
		}
	}
}