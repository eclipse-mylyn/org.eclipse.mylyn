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
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationContext;

/**
 * @author Rob Elves
 */
public class LocalRepositoryConnector extends AbstractLegacyRepositoryConnector {

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
	public AbstractTask createTask(String repositoryUrl, String id, String summary) {
		return new LocalTask(id, summary);
	}

	@Override
	public AbstractAttachmentHandler getAttachmentHandler() {
		// TODO: Implement local attachments
		return null;
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
	public AbstractTaskDataHandler getLegacyTaskDataHandler() {
		// not currently needed
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
	public boolean updateTaskFromTaskData(TaskRepository repository, ITask repositoryTask, RepositoryTaskData taskData) {
		return false;
	}

	@Override
	public boolean isUserManaged() {
		return false;
	}

	@Override
	public RepositoryTaskData getLegacyTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return null;
	}

}
