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
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * @author Steffen Pingel
 */
public class TracQueryHit extends AbstractQueryHit {

	private boolean completed;

	public TracQueryHit(TaskList taskList, String repositoryUrl, String description, String id) {
		super(taskList, repositoryUrl, description, id);
	}

	public TracQueryHit(TaskList taskList, String handle) {
		super(taskList, AbstractRepositoryTask.getRepositoryUrl(handle), "", AbstractRepositoryTask.getTaskId(handle));
	}

	protected AbstractRepositoryTask createTask() {
		TracTask newTask = new TracTask(getHandleIdentifier(), getDescription(), true);
		newTask.setCompleted(completed);
		newTask.setPriority(priority);
		return newTask;
	}

	@Override
	public boolean isCompleted() {
		return (task != null) ? task.isCompleted() : completed;
	}

	public String getUrl() {
		return getRepositoryUrl() + ITracClient.TICKET_URL + getId();
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}
