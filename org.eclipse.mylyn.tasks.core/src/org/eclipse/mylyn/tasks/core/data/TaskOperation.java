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

/**
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskOperation {

	/**
	 * @since 3.0
	 */
	public static void applyTo(TaskAttribute taskAttribute, String operationId, String label) {
		TaskData taskData = taskAttribute.getTaskData();
		taskData.getAttributeMapper().setValue(taskAttribute, operationId);
		taskAttribute.getMetaData().defaults().setType(TaskAttribute.TYPE_OPERATION).setLabel(label);
	}

	/**
	 * @since 3.0
	 */
	public static TaskOperation createFrom(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		TaskData taskData = taskAttribute.getTaskData();
		TaskOperation operation = new TaskOperation(taskData.getConnectorKind(), taskData.getRepositoryUrl(),
				taskData.getTaskId(), taskAttribute.getValue());
		operation.setLabel(taskAttribute.getMetaData().getLabel());
		operation.setTaskAttribute(taskAttribute);
		return operation;
	}

	private final String connectorKind;

	private String label;

	private final String operationId;

	private final String repositoryUrl;

	private TaskAttribute taskAttribute;

	private final String taskId;

	/**
	 * @since 3.0
	 */
	public TaskOperation(String connectorKind, String repositoryUrl, String taskId, String operationId) {
		Assert.isNotNull(connectorKind);
		Assert.isNotNull(repositoryUrl);
		Assert.isNotNull(taskId);
		Assert.isNotNull(operationId);
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.operationId = operationId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaskOperation other = (TaskOperation) obj;
		if (!connectorKind.equals(other.connectorKind)) {
			return false;
		}
		if (!operationId.equals(other.operationId)) {
			return false;
		}
		if (!repositoryUrl.equals(other.repositoryUrl)) {
			return false;
		}
		if (!taskId.equals(other.taskId)) {
			return false;
		}
		return true;
	}

	/**
	 * @since 3.0
	 */
	public String getConnectorKind() {
		return connectorKind;
	}

	/**
	 * @since 3.0
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @since 3.0
	 */
	public String getOperationId() {
		return operationId;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	/**
	 * @since 3.0
	 */
	public TaskAttribute getTaskAttribute() {
		return taskAttribute;
	}

	/**
	 * @since 3.0
	 */
	public String getTaskId() {
		return taskId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + connectorKind.hashCode();
		result = prime * result + operationId.hashCode();
		result = prime * result + repositoryUrl.hashCode();
		result = prime * result + taskId.hashCode();
		return result;
	}

	/**
	 * @since 3.0
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @since 3.0
	 */
	public void setTaskAttribute(TaskAttribute taskAttribute) {
		this.taskAttribute = taskAttribute;
	}

}
