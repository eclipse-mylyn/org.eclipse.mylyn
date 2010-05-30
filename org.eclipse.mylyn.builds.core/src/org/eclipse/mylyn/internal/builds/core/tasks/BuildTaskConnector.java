/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.core.tasks;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * @author Steffen Pingel
 */
public class BuildTaskConnector extends AbstractRepositoryConnector {

	public static final String CONNECTOR_KIND = "org.eclipse.mylyn.builds";

	public static final String TASK_REPOSITORY_KEY_BUILD_CONNECTOR_KIND = "org.eclipse.mylyn.builds";

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return false;
	}

	@Override
	public boolean canQuery(TaskRepository repository) {
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return false;
	}

	@Override
	public String getConnectorKind() {
		return CONNECTOR_KIND;
	}

	@Override
	public String getLabel() {
		return "Continuous Integration";
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		return null;
	}

	@Override
	public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return null;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		return false;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		return null;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository taskRepository, IProgressMonitor monitor)
			throws CoreException {
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
	}

}
