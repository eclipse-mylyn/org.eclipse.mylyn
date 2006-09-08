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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
class SynchronizeTaskJob extends Job {

	private static final String LABEL_SYNCHRONIZING = "Synchronizing ";

	private static final String LABEL_SYNCHRONIZE_TASK = "Task Synchronization";

	private final AbstractRepositoryConnector connector;

	// private final RepositorySynchronizationManager synchronizationManager;

	private Set<AbstractRepositoryTask> repositoryTasks;

	private boolean forceSync = false;

	public SynchronizeTaskJob(AbstractRepositoryConnector connector, Set<AbstractRepositoryTask> repositoryTasks) {
		super(LABEL_SYNCHRONIZE_TASK + " (" + repositoryTasks.size() + " tasks)");
		this.connector = connector;
		this.repositoryTasks = repositoryTasks;
		// this.synchronizationManager = synchronizationManager;
	}

	public void setForceSynch(boolean forceUpdate) {
		this.forceSync = forceUpdate;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(LABEL_SYNCHRONIZE_TASK, repositoryTasks.size());
			setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);

			for (final AbstractRepositoryTask repositoryTask : repositoryTasks) {
				if (monitor.isCanceled())
					throw new OperationCanceledException();

				boolean canNotSynch = repositoryTask.isDirty();
				boolean hasLocalChanges = repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
						|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT;
				if (forceSync || (!canNotSynch && !hasLocalChanges) || !repositoryTask.isDownloaded()) {
					monitor.setTaskName(LABEL_SYNCHRONIZING + repositoryTask.getDescription());
					// repositoryTask.setCurrentlySynchronizing(true);
					TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
					IOfflineTaskHandler offlineHandler = connector.getOfflineTaskHandler();
					if (offlineHandler != null) {
						RepositoryTaskData downloadedTaskData = null;
						try {
							TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
									repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
							if (repository == null) {
								throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0,
										"Associated repository could not be found. Ensure proper repository configuration of "
												+ repositoryTask.getRepositoryUrl() + " in " + TaskRepositoriesView.NAME
												+ ".", null));
							} else {
								downloadedTaskData = offlineHandler.downloadTaskData(repositoryTask, repository,
										TasksUiPlugin.getDefault().getProxySettings());
							}
						} catch (final LoginException e) {
							throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0, "Report download failed. Ensure proper repository configuration of " + repositoryTask.getRepositoryUrl() + " in "
									+ TaskRepositoriesView.NAME + ".", e ));
						} catch (final CoreException e) {
							if (!(e.getStatus().getException() instanceof IOException)) {
								MylarStatusHandler.log(e.getStatus());
							} else if (e.getStatus().getException() instanceof FileNotFoundException) {
								// can be caused by empty urlbase parameter on bugzilla server
								MylarStatusHandler.log(e.getStatus());
							} else {
								// ignore, assume working offline
							}
							continue;
						}

						if (downloadedTaskData != null) {
							TasksUiPlugin.getSynchronizationManager().updateOfflineState(connector, repositoryTask,
									downloadedTaskData, forceSync);
							connector.updateTaskState(repositoryTask);
							refreshEditors(repositoryTask);
						}
					}
					repositoryTask.setCurrentlySynchronizing(false);
					TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);

				} else {
					repositoryTask.setCurrentlySynchronizing(false);
				}

				monitor.worked(1);
			}

			// TasksUiPlugin.getDefault().getTaskListNotificationManager().startNotification(1);

		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not download report", false);
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}

	private void refreshEditors(final AbstractRepositoryTask repositoryTask) {
		// TODO: move out of SynchronizeTaskJob (but beware of race conditions)
		if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING
				|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
			List<MylarTaskEditor> editors = TaskUiUtil.getActiveRepositoryTaskEditors();
			for (final MylarTaskEditor editor : editors) {
				final TaskEditorInput input = (TaskEditorInput) editor.getEditorInput();
				if (input.getTask().getHandleIdentifier().equals(repositoryTask.getHandleIdentifier())) {

					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if ((repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING && MessageDialog
									.openConfirm(null, "Stale Editor",
											"Remote copy of report has changes. Refresh and open report?"))
									|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
								TaskUiUtil.closeEditorInActivePage(input.getTask());
								TaskUiUtil.refreshAndOpenTaskListElement(input.getTask());
							}
						}
					});
				}
			}
		}
	}

}