/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.mylyn.team.ui.AbstractTaskReference;

/**
 * @author Steffen Pingel
 */
public class TaskReference extends AbstractTaskReference {

	private String repositoryUrl;

	private String taskId;

	private String taskUrl;

	private String text;

	@Override
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public String getTaskUrl() {
		return taskUrl;
	}

	@Override
	public String getText() {
		return text;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	public void setText(String text) {
		this.text = text;
	}

}
