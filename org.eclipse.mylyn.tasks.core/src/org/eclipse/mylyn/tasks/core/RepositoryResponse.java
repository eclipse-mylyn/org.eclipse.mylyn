/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
