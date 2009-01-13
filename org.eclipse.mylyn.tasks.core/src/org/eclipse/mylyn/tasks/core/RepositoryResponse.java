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

package org.eclipse.mylyn.tasks.core;

/**
 * Clients may subclass.
 * 
 * @author Steffen Pingel
 * @since 3.0
 */
public class RepositoryResponse {

	public enum ResponseKind {
		TASK_CREATED, TASK_UPDATED;
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
