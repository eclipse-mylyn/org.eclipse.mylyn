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
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskDataState implements ITaskDataWorkingCopy {

	private final String connectorKind;

	private TaskData editsTaskData;

	private TaskData lastReadTaskData;

	private TaskData localTaskData;

	private TaskData repositoryTaskData;

	private final String repositoryUrl;

	private AbstractTask task;

	private final String taskId;

	private TaskDataManager taskSynchronizationManager;

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

	public TaskData getEditsData() {
		return editsTaskData;
	}

	public TaskData getLastReadData() {
		return lastReadTaskData;
	}

	public TaskData getLocalData() {
		return localTaskData;
	}

	public TaskData getRepositoryData() {
		return repositoryTaskData;
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

	void init(TaskDataManager taskSynchronizationManager, AbstractTask task) {
		this.taskSynchronizationManager = taskSynchronizationManager;
		this.task = task;
	}

	public void refresh(IProgressMonitor monitor) throws CoreException {
		ITaskDataWorkingCopy state = taskSynchronizationManager.createWorkingCopy(task, connectorKind);
		setRepositoryData(state.getRepositoryData());
		setEditsData(state.getEditsData());
		setLastReadData(state.getLastReadData());
		revert();
	}

	public void revert() {
		localTaskData = new TaskData(repositoryTaskData.getAttributeMapper(), repositoryTaskData.getConnectorKind(),
				repositoryTaskData.getRepositoryUrl(), repositoryTaskData.getTaskId());
		localTaskData.getRoot().deepCopyFrom(repositoryTaskData.getRoot());
		if (editsTaskData != null) {
			localTaskData.getRoot().deepCopyFrom(editsTaskData.getRoot());
		} else {
			editsTaskData = new TaskData(repositoryTaskData.getAttributeMapper(),
					repositoryTaskData.getConnectorKind(), repositoryTaskData.getRepositoryUrl(),
					repositoryTaskData.getTaskId());
		}
	}

	public void save(IProgressMonitor monitor, Set<TaskAttribute> edits) throws CoreException {
		for (TaskAttribute edit : edits) {
			editsTaskData.getRoot().deepAddCopy(edit);
		}
		taskSynchronizationManager.putEdits(task, getConnectorKind(), editsTaskData);
	}

	public void setEditsData(TaskData editsTaskData) {
		this.editsTaskData = editsTaskData;
	}

	public void setLastReadData(TaskData oldTaskData) {
		this.lastReadTaskData = oldTaskData;
	}

	public void setLocalTaskData(TaskData localTaskData) {
		this.localTaskData = localTaskData;
	}

	public void setRepositoryData(TaskData newTaskData) {
		this.repositoryTaskData = newTaskData;
	}

}
