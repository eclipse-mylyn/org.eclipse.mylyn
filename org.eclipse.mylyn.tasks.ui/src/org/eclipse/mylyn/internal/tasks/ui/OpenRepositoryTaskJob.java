/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class OpenRepositoryTaskJob extends Job {

	private final String repositoryUrl;

	private final IWorkbenchPage page;

	private final String repositoryKind;

	private final String taskId;

	private final String taskUrl;

	public OpenRepositoryTaskJob(String repositoryKind, String repositoryUrl, String taskId, String taskUrl,
			IWorkbenchPage page) {
		super("Opening repository task " + taskId);

		this.repositoryKind = repositoryKind;
		this.taskId = taskId;
		this.repositoryUrl = repositoryUrl;
		this.taskUrl = taskUrl;
		this.page = page;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Opening Remote Task", 10);
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryKind, repositoryUrl);
		if (repository == null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null, "Repository Not Found",
							"Could not find repository configuration for " + repositoryUrl
									+ ". \nPlease set up repository via " + TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".");
					TasksUiUtil.openUrl(taskUrl);
				}

			});
			return Status.OK_STATUS;
		}

		AbstractLegacyRepositoryConnector connector = (AbstractLegacyRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(repositoryKind);
		try {

			AbstractTaskDataHandler offlineHandler = connector.getLegacyTaskDataHandler();
			if (offlineHandler != null) {
				// the following code was copied from SynchronizeTaskJob
				RepositoryTaskData downloadedTaskData = null;
				downloadedTaskData = offlineHandler.getTaskData(repository, taskId, monitor);
				if (downloadedTaskData != null) {
					TasksUiPlugin.getTaskDataStorageManager().setNewTaskData(downloadedTaskData);
				}
				openEditor(repository, connector, repository, downloadedTaskData);
			} else {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						TasksUiUtil.openUrl(taskUrl);
					}
				});
			}
		} catch (final CoreException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					TasksUiInternal.displayStatus("Unable to open task", e.getStatus());
				}
			});
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

	private void openEditor(final TaskRepository repository, final AbstractLegacyRepositoryConnector connector,
			final TaskRepository taskRepository, final RepositoryTaskData taskData) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (taskData == null) {
					TasksUiUtil.openUrl(taskUrl);
				} else {
					AbstractTask task = connector.createTask(taskData.getRepositoryUrl(), taskData.getTaskId(),
							taskData.getSummary());
					connector.updateTaskFromTaskData(taskRepository, task, taskData);
					TaskEditorInput editorInput = new TaskEditorInput(repository, task);
					TasksUiUtil.openEditor(editorInput, TaskEditor.ID_EDITOR, page);
				}
			}
		});
	}

}
