/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationContext;

/**
 * @author Rob Elves
 */
public class LocalRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_LABEL = "Local";

	public static final String CONNECTOR_KIND = "local";

	public static final String REPOSITORY_URL = "local";

	public static final String REPOSITORY_VERSION = "1";

	public static final String DEFAULT_SUMMARY = "New Task";

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return false;
	}

	@Override
	public String getLabel() {
		return "Local Task Repository";
	}

	@Override
	public String getConnectorKind() {
		return CONNECTOR_KIND;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		// ignore
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		// ignore
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		// ignore
		return null;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector resultCollector,
			ISynchronizationContext event, IProgressMonitor monitor) {
		// ignore
		return null;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore
	}

	@Override
	public boolean isUserManaged() {
		return false;
	}

	@Override
	public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		// ignore
		return null;
	}

	@Override
	public boolean hasChanged(ITask task, TaskData taskData) {
		// ignore
		return false;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository, ITask task, TaskData taskData) {
		// ignore
	}

}
