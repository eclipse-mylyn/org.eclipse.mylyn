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
import org.eclipse.mylar.internal.tasks.core.UnrecognizedReponseException;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.util.WebBrowserDialog;
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
 * @author Steffen Pingel
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
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				repositoryTask.setStatus(null);
				
				try {
					syncTask(monitor, repositoryTask);
				} catch (final CoreException e) {
					if (e.getStatus().getException() instanceof UnrecognizedReponseException) {
						// TODO move this handler
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								WebBrowserDialog.openAcceptAgreement(PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell(), "Unrecognized response from server", e
										.getStatus().getMessage(), e.getStatus().getException().getMessage());
								MylarStatusHandler.log(e.getStatus());
							}
						});
					} else if (e.getStatus().getException() instanceof LoginException) {
						MylarStatusHandler.fail(e, "Report download failed. Ensure proper repository configuration of "
								+ repositoryTask.getRepositoryUrl() + " in " + TaskRepositoriesView.NAME + ".", true);
						repositoryTask.setStatus(e.getStatus());
					} else if (forceSync) {
						MylarStatusHandler.log(e.getStatus().getException(), "Unable to retrieve task "
								+ AbstractRepositoryTask.getTaskId(repositoryTask.getHandleIdentifier()) + " from "
								+ repositoryTask.getRepositoryUrl());
						repositoryTask.setStatus(e.getStatus());
					}
				}

				repositoryTask.setCurrentlySynchronizing(false);
				TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
				
				monitor.worked(1);
			}
			TasksUiPlugin.getDefault().getTaskDataManager().save();			

		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not download report", false);
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}

	private void syncTask(IProgressMonitor monitor, final AbstractRepositoryTask repositoryTask) throws LoginException,
			CoreException {
		boolean canNotSynch = repositoryTask.isDirty();
		boolean hasLocalChanges = repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
				|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT;
		if (forceSync || (!canNotSynch && !hasLocalChanges) || !repositoryTask.isDownloaded()) {
			monitor.setTaskName(LABEL_SYNCHRONIZING + repositoryTask.getSummary());

			final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
			if (repository == null) {
				throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, 0,
						"Associated repository could not be found. Ensure proper repository configuration of "
								+ repositoryTask.getRepositoryUrl() + " in " + TaskRepositoriesView.NAME + ".", null));
			}

			TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
			IOfflineTaskHandler offlineHandler = connector.getOfflineTaskHandler();
			if (offlineHandler != null) {
				String taskId = AbstractRepositoryTask.getTaskId(repositoryTask.getHandleIdentifier());
				RepositoryTaskData downloadedTaskData = offlineHandler.downloadTaskData(repository, taskId);

				if (downloadedTaskData != null) {
					TasksUiPlugin.getSynchronizationManager().updateOfflineState(repositoryTask, downloadedTaskData,
							forceSync);
					refreshEditors(repositoryTask);
				} else {
					connector.updateTask(repository, repositoryTask);
				}
			} else {
				connector.updateTask(repository, repositoryTask);
			}
		}
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
											"Remote copy of task has changes. Refresh and open report?"))
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