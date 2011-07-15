/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core;

import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

/**
 * Base repository connector
 */
public abstract class RepositoryConnector extends AbstractRepositoryConnector {

	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task,
			TaskData taskData) {
		Date dataDate = getTaskMapping(taskData).getModificationDate();
		Date taskDate = task.getModificationDate();
		return dataDate == null || !dataDate.equals(taskDate);
	}

	public void updateRepositoryConfiguration(TaskRepository taskRepository,
			IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		int lastSlash = taskFullUrl.lastIndexOf('/');
		if (lastSlash != -1 && lastSlash + 1 < taskFullUrl.length())
			return taskFullUrl.substring(lastSlash + 1);
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + '/' + taskId;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository,
			ITask task, TaskData taskData) {
		if (!taskData.isNew())
			task.setUrl(getTaskUrl(taskRepository.getUrl(),
					taskData.getTaskId()));
		new TaskMapper(taskData).applyTo(task);
	}
}
