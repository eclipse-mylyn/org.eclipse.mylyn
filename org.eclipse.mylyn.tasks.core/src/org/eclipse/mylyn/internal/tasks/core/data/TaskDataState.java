/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.data.ITaskDataState;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskDataState implements ITaskDataState {

	private final String connectorKind;

	private TaskData editsTaskData;

	private TaskData newTaskData;

	private TaskData oldTaskData;

	private final String repositoryUrl;

	private final String taskId;

	private TaskDataManager2 taskDataManager;

	private AbstractTask task;

	public TaskDataState(String connectorKind, String repositoryUrl, String taskId) {
		Assert.isNotNull(connectorKind);
		Assert.isNotNull(repositoryUrl);
		Assert.isNotNull(taskId);
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaskDataState other = (TaskDataState) obj;
		return connectorKind.equals(other.connectorKind) && repositoryUrl.equals(other.repositoryUrl)
				&& taskId.equals(other.taskId);
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public TaskData getLocalData() {
		return editsTaskData;
	}

	public TaskData getRepositoryData() {
		return newTaskData;
	}

	public TaskData getLastReadData() {
		return oldTaskData;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getTaskId() {
		return taskId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + connectorKind.hashCode();
		result = prime * result + taskId.hashCode();
		result = prime * result + repositoryUrl.hashCode();
		return result;
	}

	public void setEditsTaskData(TaskData editsTaskData) {
		this.editsTaskData = editsTaskData;
	}

	public void setNewTaskData(TaskData newTaskData) {
		this.newTaskData = newTaskData;
	}

	public void setOldTaskData(TaskData oldTaskData) {
		this.oldTaskData = oldTaskData;
	}

	public void addEdit(TaskAttribute attribute) {
		// ignore

	}

	public Set<TaskAttribute> getEditedAttributes() {
		// ignore
		return null;
	}

	public void refresh(IProgressMonitor monitor) throws CoreException {
		// ignore

	}

	public void save(IProgressMonitor monitor) throws CoreException {
		taskDataManager.writeState(task, getConnectorKind(), this);
	}

	void init(TaskDataManager2 taskDataManager, AbstractTask task) {
		this.taskDataManager = taskDataManager;
		this.task = task;
	}

	TaskDataManager2 getTaskDataManager() {
		return taskDataManager;
	}

	AbstractTask getTask() {
		return task;
	}

}
