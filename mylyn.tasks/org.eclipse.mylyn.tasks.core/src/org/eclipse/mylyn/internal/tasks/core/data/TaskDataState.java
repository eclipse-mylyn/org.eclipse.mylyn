/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
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
		saved = true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		TaskDataState other = (TaskDataState) obj;
		return connectorKind.equals(other.connectorKind) && repositoryUrl.equals(other.repositoryUrl)
				&& taskId.equals(other.taskId);
	}

	@Override
	public String getConnectorKind() {
		return connectorKind;
	}

	@Override
	public TaskData getEditsData() {
		return editsTaskData;
	}

	@Override
	public TaskData getLastReadData() {
		return lastReadTaskData;
	}

	@Override
	public TaskData getLocalData() {
		return localTaskData;
	}

	@Override
	public TaskData getRepositoryData() {
		return repositoryTaskData;
	}

	@Override
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	@Override
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
		taskDataManager = taskSynchronizationManager;
		this.task = task;
	}

	@Override
	public boolean isSaved() {
		return saved;
	}

	@Override
	public void refresh(IProgressMonitor monitor) throws CoreException {
		ITaskDataWorkingCopy state = taskDataManager.getWorkingCopy(task);
		setRepositoryData(state.getRepositoryData());
		setEditsData(state.getEditsData());
		setLastReadData(state.getLastReadData());
		revert();
	}

	@Override
	public void revert() {
		localTaskData = new TaskData(repositoryTaskData.getAttributeMapper(), repositoryTaskData.getConnectorKind(),
				repositoryTaskData.getRepositoryUrl(), repositoryTaskData.getTaskId());
		localTaskData.setVersion(repositoryTaskData.getVersion());
		deepCopyChildren(repositoryTaskData.getRoot(), localTaskData.getRoot());
		if (editsTaskData != null) {
			deepCopyChildren(editsTaskData.getRoot(), localTaskData.getRoot());
		} else {
			editsTaskData = new TaskData(repositoryTaskData.getAttributeMapper(), repositoryTaskData.getConnectorKind(),
					repositoryTaskData.getRepositoryUrl(), repositoryTaskData.getTaskId());
			editsTaskData.setVersion(repositoryTaskData.getVersion());
		}
	}

	private void deepCopyChildren(TaskAttribute source, TaskAttribute target) {
		for (TaskAttribute child : source.getAttributes().values()) {
			target.deepAddCopy(child);
		}
	}

	@Override
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

	/**
	 * @see #getEditsData()
	 */
	public void setEditsData(TaskData editsTaskData) {
		this.editsTaskData = editsTaskData;
	}

	/**
	 * @see #getLastReadData()
	 */
	public void setLastReadData(TaskData oldTaskData) {
		lastReadTaskData = oldTaskData;
	}

	/**
	 * @see #getLocalData()
	 */
	public void setLocalTaskData(TaskData localTaskData) {
		this.localTaskData = localTaskData;
	}

	void setSaved(boolean saved) {
		this.saved = saved;
	}

	/**
	 * @see #getRepositoryData()
	 */
	public void setRepositoryData(TaskData newTaskData) {
		repositoryTaskData = newTaskData;
	}

	public void merge(TaskDataState oldState) {
		setEditsData(createCopy(oldState.getEditsData(), getTaskId()));
		setLocalTaskData(createCopy(oldState.getLocalData(), getTaskId()));
		setRepositoryData(createCopy(oldState.getRepositoryData(), getTaskId()));
	}

	public void changeAttributeValues(Map<TaskAttribute, Collection<String>> newValues) {
		changeAttributeValues(localTaskData, newValues);
		changeAttributeValues(repositoryTaskData, newValues);
		changeAttributeValues(editsTaskData, newValues);
		changeAttributeValues(lastReadTaskData, newValues);
	}

	private void changeAttributeValues(TaskData taskData, Map<TaskAttribute, Collection<String>> newValues) {
		if (taskData != null) {
			for (TaskAttribute key : newValues.keySet()) {
				TaskAttribute attribute = taskData.getRoot().getMappedAttribute(key.getPath());
				if (attribute != null) {
					attribute.setValues(new ArrayList<>(newValues.get(key)));
				}
			}
		}
	}

	public static TaskData createCopy(TaskData oldData) {
		if (oldData == null) {
			return null;
		}
		return createCopy(oldData, oldData.getTaskId());
	}

	private static TaskData createCopy(TaskData oldData, String newTaskId) {
		if (oldData == null) {
			return null;
		}
		TaskData newData = new TaskData(oldData.getAttributeMapper(), oldData.getConnectorKind(),
				oldData.getRepositoryUrl(), newTaskId);
		newData.setVersion(oldData.getVersion());
		for (TaskAttribute child : oldData.getRoot().getAttributes().values()) {
			newData.getRoot().deepAddCopy(child);
		}
		return newData;
	}

	public void refactorAttribute(TaskAttribute attribute) throws CoreException {
		refactorAttribute(localTaskData, attribute);
		refactorAttribute(repositoryTaskData, attribute);
		refactorAttribute(editsTaskData, attribute);
		refactorAttribute(lastReadTaskData, attribute);
	}

	private void refactorAttribute(TaskData taskData, TaskAttribute attribute) throws CoreException {
		if (taskData != null) {
			String[] path = attribute.getPath();
			if (path.length == 0) {
				throw new CoreException(
						new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, Messages.TaskDataState_RefactorRoot));
			}
			String[] shortenedPath = Arrays.copyOfRange(path, 0, path.length - 1);
			TaskAttribute parent = taskData.getRoot().getMappedAttribute(shortenedPath);
			if (parent != null) {
				parent.removeAttribute(attribute.getId());
				parent.deepAddCopy(attribute);
			}
		}
	}
}
