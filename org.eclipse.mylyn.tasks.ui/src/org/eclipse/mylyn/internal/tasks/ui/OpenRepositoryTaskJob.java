/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Rob Elves
 */
public class OpenRepositoryTaskJob extends Job {

	private final String repositoryUrl;

	private final String repositoryKind;

	private final String taskId;

	private final String taskUrl;

	public OpenRepositoryTaskJob(String repositoryKind, String repositoryUrl, String taskId, String taskUrl) {
		super("Opening repository task " + taskId);
		this.repositoryKind = repositoryKind;
		this.taskId = taskId;
		this.repositoryUrl = repositoryUrl;
		this.taskUrl = taskUrl;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Opening Remote Task", 10);
		final TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryKind, repositoryUrl);
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

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			public void run() {

				ITask task = TasksUi.getRepositoryModel().createTask(repository, taskId);
				TasksUiInternal.refreshAndOpenTaskListElement(task);
			}
		});
		return Status.OK_STATUS;
	}

}
