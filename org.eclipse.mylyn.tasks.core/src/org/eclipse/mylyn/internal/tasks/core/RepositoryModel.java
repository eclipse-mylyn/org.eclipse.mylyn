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

import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class RepositoryModel implements IRepositoryModel {

	private int handleCount;

	private final IRepositoryManager repositoryManager;

	private final Map<String, ITask> taskByHandle = new WeakHashMap<String, ITask>();

	private final TaskList taskList;

	public RepositoryModel(TaskList taskList, IRepositoryManager repositoryManager) {
		this.taskList = taskList;
		this.repositoryManager = repositoryManager;
		initialize();
	}

	private void initialize() {
		repositoryManager.addListener(new TaskRepositoryAdapter() {
			@Override
			public void repositoryAdded(TaskRepository repository) {
				taskList.addUnmatchedContainer(new UnmatchedTaskContainer(repository.getConnectorKind(),
						repository.getRepositoryUrl()));
			}

			@Override
			public void repositoryRemoved(TaskRepository repository) {
				// TODO 
				//taskList.removeUnmatchedContainer(taskList.getUnmatchedContainer(repository.getRepositoryUrl()));
			}
		});
	}

	public IRepositoryQuery createQuery(TaskRepository taskRepository) {
		String handle = taskList.getUniqueHandleIdentifier();
		RepositoryQuery query = new RepositoryQuery(taskRepository.getConnectorKind(), handle);
		query.setRepositoryUrl(taskRepository.getRepositoryUrl());
		return query;
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

	public ITaskAttachment createTaskAttachment(TaskAttribute taskAttribute) {
		TaskData taskData = taskAttribute.getTaskData();
		TaskRepository taskRepository = repositoryManager.getRepository(taskData.getConnectorKind(),
				taskData.getRepositoryUrl());
		ITask task = getTask(taskRepository, taskData.getTaskId());
		TaskAttachment taskAttachment = new TaskAttachment(taskRepository, task, taskAttribute);
		taskData.getAttributeMapper().updateTaskAttachment(taskAttachment, taskAttribute);
		return taskAttachment;
	}

	public ITaskComment createTaskComment(TaskAttribute taskAttribute) {
		TaskData taskData = taskAttribute.getTaskData();
		TaskRepository taskRepository = repositoryManager.getRepository(taskData.getConnectorKind(),
				taskData.getRepositoryUrl());
		ITask task = getTask(taskRepository, taskData.getTaskId());
		TaskComment taskComment = new TaskComment(taskRepository, task, taskAttribute);
		taskData.getAttributeMapper().updateTaskComment(taskComment, taskAttribute);
		return taskComment;
	}

	public synchronized ITask getTask(String handleIdentifier) {
		ITask task = taskByHandle.get(handleIdentifier);
		if (task == null) {
			task = taskList.getTask(handleIdentifier);
		}
		return task;
	}

	public synchronized ITask getTask(TaskRepository taskRepository, String taskId) {
		return getTask(getTaskHandle(taskRepository, taskId));
	}

	private String getTaskHandle(TaskRepository taskRepository, String taskId) {
		return RepositoryTaskHandleUtil.getHandle(taskRepository.getRepositoryUrl(), taskId);
	}

	public TaskRepository getTaskRepository(String connectorKind, String repositoryUrl) {
		TaskRepository taskRepository = repositoryManager.getRepository(connectorKind, repositoryUrl);
		if (taskRepository == null) {
			taskRepository = new TaskRepository(connectorKind, repositoryUrl);
			repositoryManager.addRepository(taskRepository);
		}
		return taskRepository;
	}

	public synchronized void clear() {
		taskByHandle.clear();
	}

}
