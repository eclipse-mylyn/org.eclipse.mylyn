/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public final class TaskData {

	private final String connectorKind;

	private boolean isNew;

	private boolean partial;

	private String version;

	private final String repositoryUrl;

	private final String taskId;

	private final AbstractAttributeMapper mapper;

	private final TaskAttribute root;

	public TaskData(AbstractAttributeMapper mapper, String connectorKind, String repositoryUrl, String taskId) {
		Assert.isNotNull(mapper);
		Assert.isNotNull(connectorKind);
		Assert.isNotNull(repositoryUrl);
		Assert.isNotNull(taskId);
		this.mapper = mapper;
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.root = new TaskAttribute(this, "root");
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
		return isNew;
	}

	public boolean isPartial() {
		return partial;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public void setPartial(boolean complete) {
		this.partial = complete;
	}

	public AbstractAttributeMapper getAttributeMapper() {
		return mapper;
	}

	public TaskAttribute getMappedAttribute(String key) {
		key = getAttributeMapper().mapToRepositoryKey(getRoot(), key);
		return getRoot().getAttribute(key);
	}

}
