/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskOpenEvent;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskOpenListener;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.osgi.util.NLS;
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

	private TaskOpenListener listener;

	private final long timestamp;

	private String taskKey;

	private TaskRepository repository;

	/**
	 * Creates a job that searches for a task with the given task <i>key</i> and opens it if found.
	 */
	public OpenRepositoryTaskJob(TaskRepository repository, String taskKey, String taskUrl, IWorkbenchPage page) {
		super(MessageFormat.format(Messages.OpenRepositoryTaskJob_Opening_repository_task_X, taskKey));
		this.repositoryKind = repository.getConnectorKind();
		this.taskId = null;
		this.repositoryUrl = repository.getRepositoryUrl();
		this.taskUrl = taskUrl;
		this.timestamp = 0;
		this.repository = repository;
		this.taskKey = taskKey;
	}

	/**
	 * Creates a job that fetches a task with the given task id and opens it.
	 */
	public OpenRepositoryTaskJob(String repositoryKind, String repositoryUrl, String taskId, String taskUrl,
			IWorkbenchPage page) {
		this(repositoryKind, repositoryUrl, taskId, taskUrl, 0, page);
	}

	/**
	 * Creates a job that fetches a task with the given task id and opens it, expanding all comments made after the
	 * given timestamp.
	 */
	public OpenRepositoryTaskJob(String repositoryKind, String repositoryUrl, String taskId, String taskUrl,
			long timestamp, IWorkbenchPage page) {
		super(MessageFormat.format(Messages.OpenRepositoryTaskJob_Opening_repository_task_X, taskId));

		this.repositoryKind = repositoryKind;
		this.taskId = taskId;
		this.repositoryUrl = repositoryUrl;
		this.taskUrl = taskUrl;
		this.timestamp = timestamp;
	}

	/**
	 * Creates a job that fetches a task with the given task id or key and opens it.
	 */
	public OpenRepositoryTaskJob(IWorkbenchPage page, String repositoryKind, String repositoryUrl, String taskIdOrKey,
			String taskUrl) {
		this(repositoryKind, repositoryUrl, taskIdOrKey, taskUrl, page);
		taskKey = taskIdOrKey;
	}

	/**
	 * Returns the task if it was created when opening
	 *
	 * @return
	 */
	public ITask getTask() {
		return task;
	}

	public void setListener(TaskOpenListener listener) {
		this.listener = listener;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(Messages.OpenRepositoryTaskJob_Opening_Remote_Task, 10);
		if (repository == null) {
			repository = TasksUi.getRepositoryManager().getRepository(repositoryKind, repositoryUrl);
		}
		if (repository == null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null, Messages.OpenRepositoryTaskJob_Repository_Not_Found,
							MessageFormat.format(
									Messages.OpenRepositoryTaskJob_Could_not_find_repository_configuration_for_X,
									repositoryUrl) + "\n" + //$NON-NLS-1$
							MessageFormat.format(Messages.OpenRepositoryTaskJob_Please_set_up_repository_via_X,
									Messages.TasksUiPlugin_Task_Repositories));
					TasksUiUtil.openUrl(taskUrl);
				}

			});
			return Status.OK_STATUS;
		}

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(repositoryKind);
		try {
			final TaskData taskData = getTaskData(connector, monitor);
			if (taskData != null) {
				task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
				TasksUiPlugin.getTaskDataManager().putUpdatedTaskData(task, taskData, true);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						TaskOpenEvent event = TasksUiInternal.openTask(task, taskId);
						if (listener != null && event != null) {
							listener.taskOpened(event);
						}
						if (timestamp != 0 && event != null) {
							List<TaskAttribute> commentAttributes = taskData.getAttributeMapper()
									.getAttributesByType(taskData, TaskAttribute.TYPE_COMMENT);
							if (commentAttributes.size() > 0) {
								for (TaskAttribute commentAttribute : commentAttributes) {
									TaskAttribute commentCreateDate = commentAttribute
											.getMappedAttribute(TaskAttribute.COMMENT_DATE);
									if (commentCreateDate != null) {
										Date dateValue = taskData.getAttributeMapper().getDateValue(commentCreateDate);
										if (dateValue.getTime() < timestamp) {
											continue;
										}
										TaskAttribute dn = commentAttribute
												.getMappedAttribute(TaskAttribute.COMMENT_NUMBER);
										TaskEditor editor = (TaskEditor) event.getEditor();
										if (dn != null) {
											editor.selectReveal(TaskAttribute.PREFIX_COMMENT + dn.getValue());
										}
										break;
									}
								}
							}
						}
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

	TaskData getTaskData(AbstractRepositoryConnector connector, IProgressMonitor monitor) throws CoreException {
		if (taskId != null && taskKey != null && connector.supportsSearchByTaskKey(repository)) {
			try {
				TaskData data = getTaskDataByKey(connector, monitor);
				if (data != null) {
					return data;
				}
			} catch (CoreException | RuntimeException e) {
			}
			try {
				return connector.getTaskData(repository, taskId, monitor);
			} catch (CoreException | RuntimeException e) {
				// do not display connector's message because it may be about using a task key as an ID which is not the real error
				throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						NLS.bind("Could not find task with ID \"{0}\" on repository {1}.", taskId, repository), e)); //$NON-NLS-1$
			}
		} else if (taskId != null) {
			return connector.getTaskData(repository, taskId, monitor);
		} else if (taskKey != null && connector.supportsSearchByTaskKey(repository)) {
			return getTaskDataByKey(connector, monitor);
		}
		return null;
	}

	private TaskData getTaskDataByKey(AbstractRepositoryConnector connector, IProgressMonitor monitor)
			throws CoreException {
		TaskData searchTaskData = connector.searchByTaskKey(repository, taskKey, monitor);
		if (searchTaskData != null && searchTaskData.isPartial()) {
			return connector.getTaskData(repository, searchTaskData.getTaskId(), monitor);
		}
		return searchTaskData;
	}

}
