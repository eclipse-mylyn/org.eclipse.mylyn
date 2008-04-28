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
		this.root = new TaskAttribute(this, null, "root");
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

	public boolean isPartial() {
		return partial;
	}

	public void setPartial(boolean complete) {
		this.partial = complete;
	}

	public TaskAttributeMapper getAttributeMapper() {
		return mapper;
	}

	public TaskAttribute getMappedAttribute(String key) {
		key = getAttributeMapper().mapToRepositoryKey(getRoot(), key);
		return getRoot().getAttribute(key);
	}

	public TaskAttribute getMappedAttribute(String[] path) {
		TaskAttribute attribute = getRoot();
		for (String id : path) {
			attribute = attribute.getMappedAttribute(id);
			if (attribute == null) {
				break;
			}
		}
		return attribute;
	}

}
