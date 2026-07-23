/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
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

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task,
			TaskData taskData) {
		Date dataDate = getTaskMapping(taskData).getModificationDate();
		Date taskDate = task.getModificationDate();
		return dataDate == null || !dataDate.equals(taskDate);
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository taskRepository,
			IProgressMonitor monitor) throws CoreException {
		// empty
	}

	@Override
	public String getTaskIdFromTaskUrl(final String taskFullUrl) {
		if (taskFullUrl == null || taskFullUrl.length() == 0)
			return null;
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
