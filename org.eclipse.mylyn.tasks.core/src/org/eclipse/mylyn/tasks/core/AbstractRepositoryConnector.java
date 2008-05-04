/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationContext;

/**
 * Encapsulates common operations that can be performed on a task repository. Extend to connect with a Java API or WS
 * API for accessing the repository.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 * @since 2.0
 */
public abstract class AbstractRepositoryConnector {

	private static final long REPOSITORY_CONFIGURATION_UPDATE_INTERVAL = 24 * 60 * 60 * 1000;

	private boolean userManaged = true;

	private ITaskDataManager taskDataManager;

	/**
	 * Set upon construction
	 * 
	 * @since 3.0
	 */
	public void init(ITaskDataManager taskDataManager) {
		this.taskDataManager = taskDataManager;
	}

	/**
	 * @since 3.0
	 */
	public AbstractTaskDataHandler getTaskDataHandler2() {
		return null;
	}

	public abstract String getRepositoryUrlFromTaskUrl(String taskFullUrl);

	public abstract String getTaskIdFromTaskUrl(String taskFullUrl);

	// API 3.0 change type of taskId to AbstractTask
	public abstract String getTaskUrl(String repositoryUrl, String taskId);

	public String[] getTaskIdsFromComment(TaskRepository repository, String comment) {
		return null;
	}

	// API 3.0 rename to canCreateTaskFromId?
	public abstract boolean canCreateTaskFromKey(TaskRepository repository);

	public abstract boolean canCreateNewTask(TaskRepository repository);

	/**
	 * @since 3.0
	 */
	public boolean canQuery(TaskRepository repository) {
		return true;
	}

	/**
	 * Utility method for construction of connector specific task object.
	 * 
	 * @return instance of AbstractTask
	 */
	public abstract AbstractTask createTask(String repositoryUrl, String id, String summary);

	/**
	 * Implementors must execute query synchronously.
	 * 
	 * @since 3.0
	 */
	public abstract IStatus performQuery(TaskRepository repository, AbstractRepositoryQuery query,
			TaskDataCollector resultCollector, SynchronizationContext event, IProgressMonitor monitor);

	/**
	 * The connector's summary i.e. "JIRA (supports 3.3.1 and later)"
	 */
	public abstract String getLabel();

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
	 * @return the unique kind of the repository, e.g. "bugzilla"
	 */
	public abstract String getConnectorKind();

	/**
	 * Used for referring to the task in the UI.
	 * 
	 * @return
	 */
	// API 3.0 move to RepositoryConnectorUi?
	public String getTaskIdPrefix() {
		return "task";
	}

	/**
	 * Reset and update the repository attributes from the server (e.g. products, components)
	 * 
	 * @since 3.0
	 */
	public abstract void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Default implementation returns true every 24hrs
	 * 
	 * @param monitor
	 * 		TODO
	 * 
	 * @return true to indicate that the repository configuration is stale and requires update
	 * @throws CoreException
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

	// API 3.0 remove and let connectors override isUserManaged? Can this property change in the life cycle of the connector?
	public void setUserManaged(boolean userManaged) {
		this.userManaged = userManaged;
	}

	/**
	 * If false, user is unable to manipulate (i.e. rename/delete), no preferences are available.
	 */
	public boolean isUserManaged() {
		return userManaged;
	}

	/**
	 * @since 3.0
	 */
	protected ITaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

	/**
	 * @since 3.0
	 */
	public void preSynchronization(SynchronizationContext event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
		} finally {
			monitor.done();
		}
	}

	/**
	 * @since 3.0
	 */
	public void postSynchronization(SynchronizationContext event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
		} finally {
			monitor.done();
		}
	}

	/**
	 * @since 3.0
	 */
	public boolean hasChanged(AbstractTask task, TaskData taskData) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 3.0
	 */
	public void updateTaskFromTaskData(TaskRepository repository, AbstractTask task, TaskData taskData) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 3.0
	 */
	public TaskData getTaskData2(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return null;
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
	public TaskMapper getTaskScheme(TaskData taskData) {
		return new TaskMapper(taskData);
	}

}
