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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.editors.AbstractTaskEditorInput;
import org.eclipse.mylar.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class OpenRepositoryTaskJob extends Job {

	private String serverUrl;

	private IWorkbenchPage page;

	private String repositoryKind;

	private String taskId;

	private String taskUrl;

	public OpenRepositoryTaskJob(String repositoryKind, String serverUrl, String taskId, String taskUrl,
			IWorkbenchPage page) {
		super("Opening repository task " + taskId);

		this.repositoryKind = repositoryKind;
		this.taskId = taskId;
		this.serverUrl = serverUrl;
		this.taskUrl = taskUrl;
		this.page = page;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Opening Remote Task", 10);
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryKind, serverUrl);
		if (repository == null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog
							.openError(null, "Repository Not Found", "Could not find repository configuration for "
									+ serverUrl + ". \nPlease set up repository via "
									+ TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".");
					TasksUiUtil.openUrl(taskUrl, false);
				}

			});
			return Status.OK_STATUS;
		}

		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repositoryKind);
		try {
			ITaskDataHandler offlineHandler = connector.getTaskDataHandler();
			if (offlineHandler != null) {
				// the following code was copied from SynchronizeTaskJob
				RepositoryTaskData downloadedTaskData = null;
				downloadedTaskData = offlineHandler.getTaskData(repository, taskId);
				if (downloadedTaskData != null) {
					String handle = RepositoryTaskHandleUtil.getHandle(repository.getUrl(), downloadedTaskData.getId());
					TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(handle, downloadedTaskData);
				}
				openEditor(repository, downloadedTaskData);
			} else {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						TasksUiUtil.openUrl(taskUrl, false);
					}
				});
			}
		} catch (final CoreException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MylarStatusHandler.displayStatus("Unable to open task.", e.getStatus());
				}
			});
		} finally {
			monitor.done();
		}
		return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
	}

	private void openEditor(final TaskRepository repository, final RepositoryTaskData taskData) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (taskData == null) {
					TasksUiUtil.openUrl(taskUrl, false);
				} else {
					String handle = RepositoryTaskHandleUtil.getHandle(repository.getUrl(), taskData.getId());
					AbstractTaskEditorInput editorInput = new RepositoryTaskEditorInput(repository, handle, taskUrl, taskId);
					TasksUiUtil.openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID, page);
				}
			}
		});
	}

}
