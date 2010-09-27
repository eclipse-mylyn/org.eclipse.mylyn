/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getTaskUrl() {
		return taskUrl;
	}

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
