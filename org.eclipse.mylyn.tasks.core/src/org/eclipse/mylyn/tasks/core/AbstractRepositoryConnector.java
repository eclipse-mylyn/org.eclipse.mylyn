/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Collection;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * Encapsulates common operations that can be performed on a task repository. Extend to connect with a Java API or WS
 * API for accessing the repository.
 * 
 * Only methods that take a progress monitor can do network I/O.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 * @since 2.0
 */
public abstract class AbstractRepositoryConnector {

	private static final long REPOSITORY_CONFIGURATION_UPDATE_INTERVAL = 24 * 60 * 60 * 1000;

	/**
	 * @since 2.0
	 */
	public abstract boolean canCreateNewTask(TaskRepository repository);

	/**
	 * @since 2.0
	 */
	public abstract boolean canCreateTaskFromKey(TaskRepository repository);

	/**
	 * @since 3.0
	 */
	public boolean canQuery(TaskRepository repository) {
		return true;
	}

	/**
	 * @return the unique kind of the repository, e.g. "bugzilla"
	 * @since 2.0
	 */
	public abstract String getConnectorKind();

	/**
	 * The connector's summary i.e. "JIRA (supports 3.3.1 and later)"
	 * 
	 * @since 2.0
	 */
	public abstract String getLabel();

	/**
	 * Can return null if URLs are not used to identify tasks.
	 */
	public abstract String getRepositoryUrlFromTaskUrl(String taskFullUrl);

	/**
	 * Returns a short label for the connector, e.g. Bugzilla.
	 * 
	 * @since 2.3
	 */
	public String getShortLabel() {
		String label = getLabel();
		if (label == null) {
			return null;
		}

		int i = label.indexOf("(");
		if (i != -1) {
			return label.substring(0, i).trim();
		}

		i = label.indexOf(" ");
		if (i != -1) {
			return label.substring(0, i).trim();
		}

		return label;
	}

	/**
	 * @since 3.0
	 */
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return null;
	}

	/**
	 * @since 3.0
	 */
	public abstract TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * @since 3.0
	 */
	public AbstractTaskDataHandler getTaskDataHandler() {
		return null;
	}

	/**
	 * @since 2.0
	 */
	public abstract String getTaskIdFromTaskUrl(String taskFullUrl);

	/**
	 * Used for referring to the task in the UI.
	 */
	public String getTaskIdPrefix() {
		return "task";
	}

	/**
	 * @since 2.0
	 */
	public String[] getTaskIdsFromComment(TaskRepository repository, String comment) {
		return null;
	}

	/**
	 * @since 3.0
	 */
	public ITaskMapping getTaskMapping(TaskData taskData) {
		return new TaskMapper(taskData);
	}

	/**
	 * Connectors can override to return other tasks associated with this task.
	 * 
	 * @since 3.0
	 */
	public Collection<TaskRelation> getTaskRelations(TaskData taskData) {
		return null;
	}

	/**
	 * @since 2.0
	 */
	public abstract String getTaskUrl(String repositoryUrl, String taskId);

	/**
	 * @since 3.0
	 */
	public abstract boolean hasChanged(TaskRepository taskRepository, ITask task, TaskData taskData);

	/**
	 * @since 3.0
	 */
	public boolean hasLocalCompletionState(TaskRepository taskRepository, ITask task) {
		return false;
	}

	/**
	 * Default implementation returns true every 24hrs.
	 * 
	 * @return true to indicate that the repository configuration is stale and requires update
	 * @since 3.0
	 */
	public boolean isRepositoryConfigurationStale(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean isStale = true;
		Date configDate = repository.getConfigurationDate();
		if (configDate != null) {
			isStale = (new Date().getTime() - configDate.getTime()) > REPOSITORY_CONFIGURATION_UPDATE_INTERVAL;
		}

		return isStale;
	}

	/**
	 * @since 2.0
	 */
	public boolean isUserManaged() {
		return true;
	}

	/**
	 * Implementors must execute query synchronously.
	 * 
	 * @since 3.0
	 */
	public abstract IStatus performQuery(TaskRepository repository, IRepositoryQuery query,
			TaskDataCollector resultCollector, ISynchronizationSession event, IProgressMonitor monitor);

	/**
	 * Hook into the synchronization process.
	 * 
	 * @since 3.0
	 */
	public void postSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
		} finally {
			monitor.done();
		}
	}

	/**
	 * Hook into the synchronization process.
	 * 
	 * @since 3.0
	 */
	public void preSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
		} finally {
			monitor.done();
		}
	}

	/**
	 * Reset and update the repository attributes from the server (e.g. products, components)
	 * 
	 * @since 3.0
	 */
	public abstract void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract void updateTaskFromTaskData(TaskRepository repository, ITask task, TaskData taskData);

}
