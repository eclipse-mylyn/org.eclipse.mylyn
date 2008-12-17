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

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public final class TaskData {

	private final String connectorKind;

	private boolean partial;

	private String version;

	private final String repositoryUrl;

	private final String taskId;

	private final TaskAttributeMapper mapper;

	private final TaskAttribute root;

	public TaskData(TaskAttributeMapper mapper, String connectorKind, String repositoryUrl, String taskId) {
		Assert.isNotNull(mapper);
		Assert.isNotNull(connectorKind);
		Assert.isNotNull(repositoryUrl);
		Assert.isNotNull(taskId);
		this.mapper = mapper;
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.root = new TaskAttribute(this);
	}

	public TaskAttribute getRoot() {
		return root;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Returns true if this is a new, unsubmitted task; false otherwise.
	 */
	public boolean isNew() {
		return getTaskId().length() == 0;
	}

	/**
	 * Returns true, if this task data does not have all task attributes.
	 */
	public boolean isPartial() {
		return partial;
	}

	/**
	 * Set <code>partial</code> to true to indicate that this task data does not have all task attributes.
	 * 
	 * @see #isPartial()
	 * @see AbstractRepositoryConnector#performQuery(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.IRepositoryQuery, TaskDataCollector,
	 *      org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession, org.eclipse.core.runtime.IProgressMonitor)
	 * @see AbstractRepositoryConnector#getTaskData(org.eclipse.mylyn.tasks.core.TaskRepository, String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 * @see #isPartial()
	 */
	public void setPartial(boolean partial) {
		this.partial = partial;
	}

	public TaskAttributeMapper getAttributeMapper() {
		return mapper;
	}

}
