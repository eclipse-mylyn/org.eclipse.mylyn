/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITasksModel;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TasksModel implements ITasksModel {

	private final TaskList taskList;

	private final ITaskRepositoryManager repositoryManager;

	private int queryCount;

	private final Map<String, ITask> taskByHandle = new WeakHashMap<String, ITask>();

	public TasksModel(TaskList taskList, ITaskRepositoryManager repositoryManager) {
		this.taskList = taskList;
		this.repositoryManager = repositoryManager;
	}

	public void addQuery(IRepositoryQuery query) {
		taskList.addQuery((RepositoryQuery) query);
	}

	public void addTask(ITask task) {
		taskList.addTask(task);
	}

	public synchronized ITask createTask(TaskRepository taskRepository, String taskId) {
		String handle = getTaskHandle(taskRepository, taskId);
		ITask task = taskByHandle.get(handle);
		if (task == null) {
			task = new TaskTask(taskRepository.getConnectorKind(), taskRepository.getRepositoryUrl(), taskId);
			taskByHandle.put(handle, task);
		}
		return task;
	}

	public IRepositoryQuery createQuery(TaskRepository taskRepository) {
		String handle = "query-" + ++queryCount;
		RepositoryQuery query = new RepositoryQuery(taskRepository.getConnectorKind(), handle);
		query.setRepositoryUrl(taskRepository.getRepositoryUrl());
		return query;
	}

	public void deleteQuery(IRepositoryQuery query) {
		taskList.deleteQuery((RepositoryQuery) query);
	}

	public void deleteTask(ITask task) {
		taskList.deleteTask(task);
	}

	public synchronized ITask getTask(TaskRepository taskRepository, String taskId) {
		return taskByHandle.get(getTaskHandle(taskRepository, taskId));
	}

	private String getTaskHandle(TaskRepository taskRepository, String taskId) {
		return taskRepository.getConnectorKind() + "-" + taskRepository.getRepositoryUrl() + "-" + taskId;
	}

}
