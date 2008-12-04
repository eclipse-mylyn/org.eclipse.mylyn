/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class OpenRepositoryTaskJob extends Job {

	private final String repositoryUrl;

	private final String repositoryKind;

	private final String taskId;

	private final String taskUrl;

	private ITask task;

	public OpenRepositoryTaskJob(String repositoryKind, String repositoryUrl, String taskId, String taskUrl,
			IWorkbenchPage page) {
		super(MessageFormat.format(Messages.OpenRepositoryTaskJob_Opening_repository_task_X, taskId));

		this.repositoryKind = repositoryKind;
		this.taskId = taskId;
		this.repositoryUrl = repositoryUrl;
		this.taskUrl = taskUrl;
	}

	/**
	 * Returns the task if it was created when openeing
	 * 
	 * @return
	 */
	public ITask getTask() {
		return task;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(Messages.OpenRepositoryTaskJob_Opening_Remote_Task, 10);
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryKind, repositoryUrl);
		if (repository == null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null, Messages.OpenRepositoryTaskJob_Repository_Not_Found,
							MessageFormat.format(
									Messages.OpenRepositoryTaskJob_Could_not_find_repository_configuration_for_X,
									repositoryUrl)
									+ "\n" + //$NON-NLS-1$
									MessageFormat.format(Messages.OpenRepositoryTaskJob_Please_set_up_repository_via_X,
											Messages.TasksUiPlugin_Task_Repositories));
					TasksUiUtil.openUrl(taskUrl);
				}

			});
			return Status.OK_STATUS;
		}

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(repositoryKind);
		try {
			TaskData taskData = connector.getTaskData(repository, taskId, monitor);
			if (taskData != null) {
				task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
				TasksUiPlugin.getTaskDataManager().putUpdatedTaskData(task, taskData, true);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						TasksUiUtil.openTask(task);
					}
				});
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
					TasksUiInternal.displayStatus(Messages.OpenRepositoryTaskJob_Unable_to_open_task, e.getStatus());
				}
			});
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

}
