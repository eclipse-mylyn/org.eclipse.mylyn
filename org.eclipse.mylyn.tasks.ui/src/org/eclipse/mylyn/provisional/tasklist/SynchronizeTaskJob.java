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

package org.eclipse.mylar.provisional.tasklist;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.TaskEditorInput;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
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
			setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);
			for (final AbstractRepositoryTask repositoryTask : repositoryTasks) {
				if (monitor.isCanceled())
					throw new OperationCanceledException();

				// TODO: refactor conditions
				boolean canNotSynch = repositoryTask.isDirty() || repositoryTask.isSynchronizing();
				boolean hasLocalChanges = repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
						|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT;
				if (forceSync || (!canNotSynch && !hasLocalChanges) || !repositoryTask.isDownloaded()) {
					monitor.setTaskName(LABEL_SYNCHRONIZING + repositoryTask.getDescription());
					repositoryTask.setCurrentlyDownloading(true);
					MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
					IOfflineTaskHandler offlineHandler = connector.getOfflineTaskHandler();
					if (offlineHandler != null) {
						RepositoryTaskData downloadedTaskData = null;
						try {
							downloadedTaskData = offlineHandler.downloadTaskData(repositoryTask);
						} catch (final CoreException e) {
							MylarStatusHandler.log(e.getStatus());
						}

						if (downloadedTaskData != null) {
							boolean changed = connector.updateOfflineState(repositoryTask, downloadedTaskData,
									forceSync);
							connector.updateTaskState(repositoryTask);
							if (changed) {
								refreshEditors(repositoryTask);
							}
						}
					}
					// else {
					// MylarStatusHandler.log("No offline content handler
					// available for "
					// + connector.getRepositoryType() + " connector.", this);
					// }
					repositoryTask.setCurrentlyDownloading(false);
					MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);

				}

				monitor.worked(1);
			}

			MylarTaskListPlugin.getDefault().getTaskListNotificationManager().startNotification(1);

		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not download report", false);
		} finally {
			monitor.done();
		}
		// this.connector.removeRefreshingTask(repositoryTask);
		return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "", null);
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

	// private boolean isDirty(AbstractRepositoryTask task) {
	// // TODO: move out of SynchronizeTaskJob
	// List<MylarTaskEditor> editors =
	// TaskUiUtil.getActiveRepositoryTaskEditors();
	// for (final MylarTaskEditor editor : editors) {
	// TaskEditorInput input = (TaskEditorInput) editor.getEditorInput();
	// if (input.getTask().equals(task) && editor.isDirty()) {
	// return true;
	// }
	// }
	// return false;
	// }

}