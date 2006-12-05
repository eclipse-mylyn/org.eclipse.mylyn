/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.team;

import org.eclipse.mylar.tasks.core.ILinkedTaskInfo;
import org.eclipse.mylar.tasks.core.ITask;

/**
 * Default implementation of {@link ILinkedTaskInfo}
 * 
 * @author Eugene Kuleshov
 */
public class LinkedTaskInfo implements ILinkedTaskInfo {

	private ITask task;
	
	private String repositoryUrl;
	
	private String taskId;

	private String taskFullUrl;

	private String comment;
	

	public LinkedTaskInfo(ITask task) {
		this.task = task;
	}

	public LinkedTaskInfo(String taskFullUrl) {
		this.taskFullUrl = taskFullUrl;
	}

	public LinkedTaskInfo(String repositoryUrl, String taskId, String taskFullUrl, String comment) {
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.taskFullUrl = taskFullUrl;
		this.comment = comment;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public ITask getTask() {
		return task;
	}

	public String getTaskFullUrl() {
		return taskFullUrl;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getComment() {
		return comment;
	}

}

