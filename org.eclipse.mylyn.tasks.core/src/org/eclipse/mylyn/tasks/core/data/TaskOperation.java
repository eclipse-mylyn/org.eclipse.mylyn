/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskOperation {

	private String associatedAttributeId;

	private final String connectorKind;

	private String label;

	private final String operationId;

	private final String repositoryUrl;

	private final String taskId;

	public TaskOperation(String connectorKind, String repositoryUrl, String taskId, String operationId) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.operationId = operationId;
	}

	public String getAssociatedAttributeId() {
		return associatedAttributeId;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getLabel() {
		return label;
	}

	public String getOperationId() {
		return operationId;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setAssociatedAttributeId(String associatedAttributeId) {
		this.associatedAttributeId = associatedAttributeId;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
