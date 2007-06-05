/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasks.core;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.mylar.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylar.tasks.core.Task.PriorityLevel;

/**
 * @author Mik Kersten
 */
/*public*/ abstract class AbstractQueryHit extends PlatformObject implements ITaskListElement {

	protected TaskList taskList;

	protected AbstractRepositoryTask task;

	protected String repositoryUrl;

	protected String summary;

	protected String priority = PriorityLevel.getDefault().toString();

	protected String taskId;

	private boolean completed = false;

	private boolean isNotified = false;

	private AbstractRepositoryQuery parent;

	protected AbstractQueryHit(TaskList taskList, String repositoryUrl, String description, String taskId) {
		this.taskList = taskList;
		this.repositoryUrl = repositoryUrl;
		this.summary = description;
		this.taskId = taskId;
	}

	public AbstractRepositoryQuery getParent() {
		return parent;
	}

	public void setParent(AbstractRepositoryQuery parent) {
		this.parent = parent;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public String getSummary() {
		if (task != null) {
			return task.getSummary();
		} else {
			return summary;
		}
	}

	public AbstractRepositoryTask getOrCreateCorrespondingTask() {
		if (taskList == null) {
			return null;
		}

		ITask existingTask = taskList.getTask(getHandleIdentifier());
		if (existingTask instanceof AbstractRepositoryTask) {
			this.task = (AbstractRepositoryTask) existingTask;
		} else {			
			task = createTask();			
			task.setCompleted(completed);
			taskList.addTask(task);
		}
		return task;
	}

	//@Deprecated
	protected abstract AbstractRepositoryTask createTask();

	/**
	 * @return null if there is no corresponding task
	 */
	public AbstractRepositoryTask getCorrespondingTask() {
		return task;
	}

	public void setCorrespondingTask(AbstractRepositoryTask task) {
		this.task = task;
	}

	public boolean isCompleted() {
		if (task != null) {
			return task.isCompleted();
		} else {
			return completed;
		}
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public final String getHandleIdentifier() {
		if (task != null) {
			return task.getHandleIdentifier();
		}
		return RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId);
	}

	/**
	 * @return Unique identifier for this task on the corresponding server, must
	 *         be robust to changing attributes on the task.
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @return An ID that can be presented to the user for identifying the task,
	 *         override to return null if no such ID exists.
	 */
	public String getIdentifyingLabel() {
		return getTaskId();
	}

	public boolean isNotified() {
		return isNotified;
	}

	public void setNotified(boolean notified) {
		isNotified = notified;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractQueryHit)) {
			return false;
		}
		AbstractQueryHit hit = (AbstractQueryHit) obj;
		return hit.getHandleIdentifier().equals(this.getHandleIdentifier());
	}

	@Override
	public int hashCode() {
		return this.getHandleIdentifier().hashCode();
	}

	/**
	 * @return the url of the hit without any additional login information etc.
	 */
	public String getUrl() {
		return "";
	}

	public String getPriority() {
		if (task != null) {
			return task.getPriority();
		} else {
			return priority;
		}
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setSummary(String description) {
		this.summary = description;
	}

	public void setHandleIdentifier(String id) {
		// ignore
	}

	public int compareTo(ITaskListElement taskListElement) {
		return this.taskId.compareTo(((AbstractQueryHit) taskListElement).taskId);
	}

	@Deprecated
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
