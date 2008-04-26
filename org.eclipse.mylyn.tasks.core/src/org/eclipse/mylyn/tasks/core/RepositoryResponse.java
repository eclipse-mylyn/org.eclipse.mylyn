/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * @author Steffen Pingel
 */
public class RepositoryResponse {

	public enum ResponseKind {
		TASK_CREATED
	};

	private final String taskId;

	private final ResponseKind reposonseKind;

	public RepositoryResponse(ResponseKind reposonseKind, String taskId) {
		this.reposonseKind = reposonseKind;
		this.taskId = taskId;
	}

	public RepositoryResponse() {
		this(null, null);
	}

	public String getTaskId() {
		return taskId;
	}

	public ResponseKind getReposonseKind() {
		return reposonseKind;
	}

}
