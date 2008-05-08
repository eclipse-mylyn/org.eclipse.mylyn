/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Set;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationContext;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class SynchronizationContext implements ISynchronizationContext {

	private boolean fullSynchronization;

	private boolean performQueries;

	private Set<ITask> tasks;

	private Set<ITask> changedTasks;

	private TaskRepository taskRepository;

	private Object data;

	private ITaskDataManager taskDataManager;

	public SynchronizationContext(ITaskDataManager taskDataManager) {
		this.taskDataManager = taskDataManager;
	}

	public SynchronizationContext() {
	}

	public void setFullSynchronization(boolean fullSynchronization) {
		this.fullSynchronization = fullSynchronization;
	}

	public boolean isFullSynchronization() {
		return fullSynchronization;
	}

	public void setNeedsPerformQueries(boolean performQueries) {
		this.performQueries = performQueries;
	}

	public boolean needsPerformQueries() {
		return performQueries;
	}

	public void setTasks(Set<ITask> tasks) {
		this.tasks = tasks;
	}

	public Set<ITask> getTasks() {
		return tasks;
	}

	public void setChangedTasks(Set<ITask> changedTasks) {
		this.changedTasks = changedTasks;
	}

	public Set<ITask> getChangedTasks() {
		return changedTasks;
	}

	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public ITaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

}
