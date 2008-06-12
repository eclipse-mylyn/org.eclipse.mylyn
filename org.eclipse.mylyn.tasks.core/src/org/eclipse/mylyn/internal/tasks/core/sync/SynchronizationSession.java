/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class SynchronizationSession implements ISynchronizationSession {

	private Set<ITask> changedTasks;

	private Object data;

	private boolean fullSynchronization;

	private boolean performQueries;

	private Set<ITask> staleTasks;

	private IStatus status;

	private TaskDataManager taskDataManager;

	private TaskRepository taskRepository;

	private Set<ITask> tasks;

	private boolean user;

	public SynchronizationSession() {
	}

	public SynchronizationSession(TaskDataManager taskDataManager) {
		this.taskDataManager = taskDataManager;
	}

	public Set<ITask> getChangedTasks() {
		return changedTasks;
	}

	public Object getData() {
		return data;
	}

	public Set<ITask> getStaleTasks() {
		return staleTasks;
	}

	public IStatus getStatus() {
		return status;
	}

	public TaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public Set<ITask> getTasks() {
		return tasks;
	}

	public boolean isFullSynchronization() {
		return fullSynchronization;
	}

	public boolean isUser() {
		return user;
	}

	public void markStale(ITask task) {
		if (staleTasks == null) {
			staleTasks = new HashSet<ITask>();
		}
		staleTasks.add(task);
	}

	public boolean needsPerformQueries() {
		return performQueries;
	}

	public void putTaskData(ITask task, TaskData taskData) throws CoreException {
		if (taskDataManager != null) {
			taskDataManager.putUpdatedTaskData(task, taskData, false);
		}
	}

	public void setChangedTasks(Set<ITask> changedTasks) {
		this.changedTasks = changedTasks;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setFullSynchronization(boolean fullSynchronization) {
		this.fullSynchronization = fullSynchronization;
	}

	public void setNeedsPerformQueries(boolean performQueries) {
		this.performQueries = performQueries;
	}

	public void setStatus(IStatus status) {
		this.status = status;
	}

	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public void setTasks(Set<ITask> tasks) {
		this.tasks = tasks;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

}
