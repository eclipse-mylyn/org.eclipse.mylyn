/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
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

	private boolean saved;

	private TaskData repositoryTaskData;

	private final String repositoryUrl;

	private ITask task;

	private final String taskId;

	private TaskDataManager taskDataManager;

	public TaskDataState(String connectorKind, String repositoryUrl, String taskId) {
		Assert.isNotNull(connectorKind);
		Assert.isNotNull(repositoryUrl);
		Assert.isNotNull(taskId);
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.saved = true;
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

	void init(TaskDataManager taskSynchronizationManager, ITask task) {
		this.taskDataManager = taskSynchronizationManager;
		this.task = task;
	}

	public boolean isSaved() {
		return saved;
	}

	public void refresh(IProgressMonitor monitor) throws CoreException {
		ITaskDataWorkingCopy state = taskDataManager.getWorkingCopy(task);
		setRepositoryData(state.getRepositoryData());
		setEditsData(state.getEditsData());
		setLastReadData(state.getLastReadData());
		revert();
	}

	public void revert() {
		localTaskData = new TaskData(repositoryTaskData.getAttributeMapper(), repositoryTaskData.getConnectorKind(),
				repositoryTaskData.getRepositoryUrl(), repositoryTaskData.getTaskId());
		localTaskData.setVersion(repositoryTaskData.getVersion());
		deepCopyChildren(repositoryTaskData.getRoot(), localTaskData.getRoot());
		if (editsTaskData != null) {
			deepCopyChildren(editsTaskData.getRoot(), localTaskData.getRoot());
		} else {
			editsTaskData = new TaskData(repositoryTaskData.getAttributeMapper(),
					repositoryTaskData.getConnectorKind(), repositoryTaskData.getRepositoryUrl(),
					repositoryTaskData.getTaskId());
			editsTaskData.setVersion(repositoryTaskData.getVersion());
		}
	}

	private void deepCopyChildren(TaskAttribute source, TaskAttribute target) {
		for (TaskAttribute child : source.getAttributes().values()) {
			target.deepAddCopy(child);
		}
	}

	public void save(Set<TaskAttribute> edits, IProgressMonitor monitor) throws CoreException {
		if (edits != null) {
			for (TaskAttribute edit : edits) {
				editsTaskData.getRoot().deepAddCopy(edit);
			}
		}
		if (saved) {
			taskDataManager.putEdits(task, editsTaskData);
		} else {
			taskDataManager.saveWorkingCopy(task, this);
			setSaved(true);
		}
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

	void setSaved(boolean saved) {
		this.saved = saved;
	}

	public void setRepositoryData(TaskData newTaskData) {
		this.repositoryTaskData = newTaskData;
	}

	public void merge(TaskDataState oldState) {
		setEditsData(createCopy(oldState.getEditsData()));
		setLocalTaskData(createCopy(oldState.getLocalData()));
		setRepositoryData(createCopy(oldState.getRepositoryData()));
	}

	private TaskData createCopy(TaskData oldData) {
		if (oldData == null) {
			return null;
		}
		TaskData newData = new TaskData(oldData.getAttributeMapper(), getConnectorKind(), getRepositoryUrl(),
				getTaskId());
		newData.setVersion(oldData.getVersion());
		for (TaskAttribute child : oldData.getRoot().getAttributes().values()) {
			newData.getRoot().deepAddCopy(child);
		}
		return newData;
	}

}
