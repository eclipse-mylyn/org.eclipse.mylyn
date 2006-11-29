/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.team;

import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Default implementation of {@link ILinkedTaskInfo}
 * 
 * @author Eugene Kuleshov
 */
public class LinkedTaskInfo implements ILinkedTaskInfo {

	private String taskId;

	private String taskFullUrl;

	private String repositoryUrl;

	private ITask task;

	public LinkedTaskInfo(ITask task) {
		init(task, //
				AbstractRepositoryTask.getRepositoryUrl(task.getHandleIdentifier()), //
				AbstractRepositoryTask.getTaskId(task.getHandleIdentifier()), //
				task.getUrl());
	}

	public LinkedTaskInfo(String taskFullUrl) {
		init(null, null, null, taskFullUrl);
	}

	public LinkedTaskInfo(String repositoryUrl, String taskId, String taskFullUrl) {
		init(null, repositoryUrl, taskId, taskFullUrl);
	}

	private void init(ITask task, String repositoryUrl, String taskId, String taskFullUrl) {
		// TODO should this even be here?

		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		AbstractRepositoryConnector connector = repositoryManager.getConnectorForRepositoryTaskUrl(taskFullUrl);
		if (connector == null && repositoryUrl != null) {
			TaskRepository repository = repositoryManager.getRepository(repositoryUrl);
			if (repository != null) {
				connector = repositoryManager.getRepositoryConnector(repository.getKind());
			}
		}

		if (repositoryUrl == null && connector != null) {
			repositoryUrl = connector.getRepositoryUrlFromTaskUrl(taskFullUrl);
		}

		if (taskId == null && connector != null) {
			taskId = connector.getTaskIdFromTaskUrl(taskFullUrl);
		}

		if (taskFullUrl == null && repositoryUrl != null && taskId != null && connector != null) {
			taskFullUrl = connector.getTaskWebUrl(repositoryUrl, taskId);
		}
		
		if (task == null) {
			if (taskId != null && repositoryUrl != null) {
				String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
				task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
			}
			if (task == null && taskFullUrl != null) {
				// search by fullUrl
				for (ITask currTask : TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks()) {
					if (currTask instanceof AbstractRepositoryTask) {
						String currUrl = ((AbstractRepositoryTask) currTask).getUrl();
						if (taskFullUrl.equals(currUrl)) {
							task = currTask;
							break;
						}
					}
				}
			}
		}

		if (taskFullUrl == null && task != null) {
			taskFullUrl = task.getUrl();
		}

		this.task = task;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.taskFullUrl = taskFullUrl;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public ITask getTask() {
		return task;
	}

	public String getTaskFullUrl() {
		return taskFullUrl;
	}

	public String getTaskId() {
		return taskId;
	}

}
