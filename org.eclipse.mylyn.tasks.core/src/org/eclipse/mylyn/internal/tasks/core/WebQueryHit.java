/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.core;

import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * Represents issue returned by <code>WebQuery</code> 
 * 
 * @author Eugene Kuleshov
 */
public class WebQueryHit extends AbstractQueryHit {
	
	private final String taskPrefix;

	public WebQueryHit(TaskList taskList, String repositoryUrl, String description, String id, String taskPrefix) {
		super(taskList, repositoryUrl, description, id);
		this.taskPrefix = taskPrefix;
	}
 
	public String getPriority() {
		return "?";
	}

	public boolean isCompleted() {
		return false;
	}

	protected AbstractRepositoryTask createTask() {
		return new WebTask(id, description, taskPrefix, repositoryUrl, WebTask.REPOSITORY_TYPE);
	}
	
	@Override
	public String getHandleIdentifier() {
		return taskPrefix + getId();
	}

	public String getTaskPrefix() {
		return this.taskPrefix;
	}
	
}

