/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.team.ui.AbstractTaskReference;
import org.eclipse.team.internal.core.subscribers.ChangeSet;

/**
 * Default implementation of {@link AbstractTaskReference}
 *
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class LinkedTaskInfo extends AbstractTaskReference {

	private ITask task;

	private String repositoryUrl;

	private String taskId;

	private String taskFullUrl;

	private String comment;

	private ChangeSet changeSet = null;

	private long timestamp = 0;

	public LinkedTaskInfo(String repositoryUrl, String taskId, String taskFullUrl, String comment, long timestamp,
			ITask task) {
		this(repositoryUrl, taskId, taskFullUrl, comment, timestamp);
		this.task = task;
	}

	public LinkedTaskInfo(String repositoryUrl, String taskId, String taskFullUrl, String comment, long timestamp) {
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.taskFullUrl = taskFullUrl;
		this.comment = comment;
		this.timestamp = timestamp;
	}

	public LinkedTaskInfo(ITask task, ChangeSet changeSet) {
		this.task = task;
		this.changeSet = changeSet;
	}

	public LinkedTaskInfo(ITask task, ChangeSet changeSet, long timestamp) {
		this.task = task;
		this.changeSet = changeSet;
		this.timestamp = timestamp;
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

	@Override
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public ITask getTask() {
		return task;
	}

	@Override
	public String getTaskUrl() {
		return taskFullUrl;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public String getText() {
		return comment;
	}

	public ChangeSet getChangeSet() {
		return changeSet;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
