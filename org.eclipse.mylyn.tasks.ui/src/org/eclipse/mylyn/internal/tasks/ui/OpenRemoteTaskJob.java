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

package org.eclipse.mylar.internal.tasks.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.core.UnrecognizedReponseException;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractTaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.util.WebBrowserDialog;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class OpenRemoteTaskJob extends Job {

	private String serverUrl;

	private IWorkbenchPage page;

	private String repositoryKind;

	private String taskId;

	private String taskUrl;

	public OpenRemoteTaskJob(String repositoryKind, String serverUrl, String taskId, String taskUrl, IWorkbenchPage page) {
		super("Opening remote task: " + taskId);
		
		this.repositoryKind = repositoryKind;
		this.taskId = taskId;
		this.serverUrl = serverUrl;
		this.taskUrl = taskUrl;
		this.page = page;
	}

	public IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Opening Remote Task", 10);
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					repositoryKind, serverUrl);
			if (repository == null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, "Repository Not Found",
								"Could not find repository configuration for " + serverUrl
								+ ". \nPlease set up repository via " + TaskRepositoriesView.NAME + ".");
						TaskUiUtil.openUrl(taskUrl);
					}

				});
				return Status.OK_STATUS;
			}
			
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repositoryKind);
			try {
				IOfflineTaskHandler offlineHandler = connector.getOfflineTaskHandler();
				if (offlineHandler != null) {
					// the following code was copied from SynchronizeTaskJob
					RepositoryTaskData downloadedTaskData = null;
					try {
						downloadedTaskData = offlineHandler.downloadTaskData(repository, taskId, TasksUiPlugin.getDefault().getProxySettings());
						openEditor(repository, downloadedTaskData);										
					} catch (final CoreException e) {
						// TODO generalize exception handling
						if (e.getStatus().getException() instanceof UnrecognizedReponseException) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									WebBrowserDialog.openAcceptAgreement(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Unrecognized response from server", e.getStatus().getMessage(), e.getStatus().getException()
											.getMessage());
									MylarStatusHandler.log(e.getStatus());
								}
							});
						} else if (e.getStatus().getException() instanceof LoginException) {
							MylarStatusHandler.log(e.getStatus().getException(), "Login credentials are invalid for " + repository.getUrl());
						} else if (!(e.getStatus().getException() instanceof IOException)) {
							MylarStatusHandler.log(e.getStatus());
						} else if (e.getStatus().getException() instanceof FileNotFoundException) {
							// can be caused by empty urlbase parameter on bugzilla server
							MylarStatusHandler.log(e.getStatus());
						} else {
							// bug 154729
							// MylarStatusHandler.log(e.getStatus());
						}
					}
				} else {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							TaskUiUtil.openUrl(taskUrl);
						}
					});
				}
			} finally {
				monitor.done();
			}
		return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
	}
	
	private void openEditor(final TaskRepository repository, final RepositoryTaskData taskData) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (taskData == null) {
					TaskUiUtil.openUrl(taskUrl);
				} else {
					AbstractTaskEditorInput editorInput = new RepositoryTaskEditorInput(taskUrl, repository, taskData);
					TaskUiUtil.openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID, page);
				}
			}
		});
	}
	
}
