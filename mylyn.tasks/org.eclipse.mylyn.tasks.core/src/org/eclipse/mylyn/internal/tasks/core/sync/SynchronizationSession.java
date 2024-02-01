/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Collections;
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

	@Override
	public Set<ITask> getChangedTasks() {
		return changedTasks;
	}

	@Override
	public Object getData() {
		return data;
	}

	public Set<ITask> getStaleTasks() {
		if (staleTasks == null) {
			return Collections.emptySet();
		} else {
			return staleTasks;
		}
	}

	@Override
	public IStatus getStatus() {
		return status;
	}

	@Override
	public TaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

	@Override
	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	@Override
	public Set<ITask> getTasks() {
		return tasks;
	}

	@Override
	public boolean isFullSynchronization() {
		return fullSynchronization;
	}

	@Override
	public boolean isUser() {
		return user;
	}

	@Override
	public void markStale(ITask task) {
		if (staleTasks == null) {
			staleTasks = new HashSet<>();
		}
		staleTasks.add(task);
	}

	@Override
	public boolean needsPerformQueries() {
		return performQueries;
	}

	@Override
	public void putTaskData(ITask task, TaskData taskData) throws CoreException {
		if (taskDataManager != null) {
			taskDataManager.putUpdatedTaskData(task, taskData, false);
		}
	}

	public void setChangedTasks(Set<ITask> changedTasks) {
		this.changedTasks = changedTasks;
	}

	@Override
	public void setData(Object data) {
		this.data = data;
	}

	public void setFullSynchronization(boolean fullSynchronization) {
		this.fullSynchronization = fullSynchronization;
	}

	@Override
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
