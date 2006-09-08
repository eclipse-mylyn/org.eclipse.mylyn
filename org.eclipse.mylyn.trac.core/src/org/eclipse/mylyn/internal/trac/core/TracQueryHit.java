/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * @author Steffen Pingel
 */
public class TracQueryHit extends AbstractQueryHit {

	private TracTask task;

	private boolean completed;

	private TaskList taskList;

	public TracQueryHit(TaskList taskList, String repositoryUrl, String description, String id) {
		super(repositoryUrl, description, id);

		this.taskList = taskList;
	}

	public TracQueryHit(TaskList taskList, String handle) {
		super(AbstractRepositoryTask.getRepositoryUrl(handle), "", AbstractRepositoryTask.getTaskId(handle));

		this.taskList = taskList;
	}

	@Override
	public AbstractRepositoryTask getCorrespondingTask() {
		return task;
	}

	@Override
	public AbstractRepositoryTask getOrCreateCorrespondingTask() {
		ITask existingTask = taskList.getTask(getHandleIdentifier());
		if (existingTask instanceof TracTask) {
			this.task = (TracTask) existingTask;
		} else {
			this.task = new TracTask(getHandleIdentifier(), getDescription(), true);
			task.setCompleted(completed);
			task.setPriority(priority);
			taskList.addTask(task);
		}
		return task;
	}

	@Override
	public boolean isCompleted() {
		return (task != null) ? task.isCompleted() : completed;
	}

	@Override
	public void setCorrespondingTask(AbstractRepositoryTask task) {
		if (task instanceof TracTask) {
			this.task = (TracTask) task;
		}
	}

	public String getUrl() {
		return getRepositoryUrl() + ITracClient.TICKET_URL + getId();
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}
