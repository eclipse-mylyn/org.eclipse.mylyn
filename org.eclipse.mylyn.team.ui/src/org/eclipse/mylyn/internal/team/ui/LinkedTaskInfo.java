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

package org.eclipse.mylyn.internal.team.ui;

import org.eclipse.mylyn.tasks.core.ILinkedTaskInfo;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.team.internal.core.subscribers.ChangeSet;

/**
 * Default implementation of {@link ILinkedTaskInfo}
 * 
 * @author Eugene Kuleshov
 */
public class LinkedTaskInfo implements ILinkedTaskInfo {

	private AbstractTask task;

	private String repositoryUrl;

	private String taskId;

	private String taskFullUrl;

	private String comment;

	private ChangeSet changeSet = null;

	public LinkedTaskInfo(AbstractTask task, ChangeSet changeSet) {
		this.task = task;
		this.changeSet = changeSet;
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

	public AbstractTask getTask() {
		return task;
	}

	public String getTaskUrl() {
		return taskFullUrl;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getComment() {
		return comment;
	}

	public ChangeSet getChangeSet() {
		return changeSet;
	}

}
