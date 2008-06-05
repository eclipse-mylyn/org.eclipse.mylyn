/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.IdentityAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager.ObjectCloner;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 * @since 2.2
 */
@Deprecated
public class TaskSelection implements ITaskMapping {

	private final RepositoryTaskData taskData;

	public TaskSelection(RepositoryTaskData taskData) {
		if (taskData == null) {
			throw new IllegalArgumentException();
		}

		try {
			this.taskData = (RepositoryTaskData) ObjectCloner.deepCopy(taskData);
			this.taskData.setAttributeFactory(taskData.getAttributeFactory());
			this.taskData.refresh();
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Error creating a task data copy", e));
			throw new RuntimeException(e);
		}
	}

	public TaskSelection(ITask task) {
		RepositoryTaskData taskData = new RepositoryTaskData(IdentityAttributeFactory.getInstance(),
				task.getConnectorKind(), task.getRepositoryUrl(), task.getTaskId(), task.getTaskKind());
		taskData.setSummary(task.getSummary());
		taskData.setAttributeValue(RepositoryTaskAttribute.PRIORITY, task.getPriority());
		this.taskData = taskData;
	}

	public TaskSelection(String summary, String description) {
		RepositoryTaskData taskData = new RepositoryTaskData(IdentityAttributeFactory.getInstance(),
				LocalRepositoryConnector.CONNECTOR_KIND, LocalRepositoryConnector.REPOSITORY_URL, "");
		taskData.setSummary(summary);
		taskData.setDescription(description);
		this.taskData = taskData;
	}

	public RepositoryTaskData getLegacyTaskData() {
		return taskData;
	}

	public Date getCompletionDate() {
		// ignore
		return null;
	}

	public String getComponent() {
		// ignore
		return null;
	}

	public Date getCreationDate() {
		// ignore
		return null;
	}

	public String getDescription() {
		// ignore
		return null;
	}

	public Date getDueDate() {
		// ignore
		return null;
	}

	public Date getModificationDate() {
		// ignore
		return null;
	}

	public String getOwner() {
		// ignore
		return null;
	}

	public PriorityLevel getPriority() {
		// ignore
		return null;
	}

	public String getProduct() {
		// ignore
		return null;
	}

	public String getSummary() {
		// ignore
		return null;
	}

	public TaskData getTaskData() {
		// ignore
		return null;
	}

	public String getTaskKey() {
		// ignore
		return null;
	}

	public String getTaskKind() {
		// ignore
		return null;
	}

	public String getTaskUrl() {
		// ignore
		return null;
	}

	public void setCompletionDate(Date dateCompleted) {
		// ignore

	}

	public void setComponent(String component) {
		// ignore

	}

	public void setCreationDate(Date dateCreated) {
		// ignore

	}

	public void setDescription(String description) {
		// ignore

	}

	public void setDueDate(Date value) {
		// ignore

	}

	public void setModificationDate(Date dateModified) {
		// ignore

	}

	public void setOwner(String owner) {
		// ignore

	}

	public void setPriority(PriorityLevel priority) {
		// ignore

	}

	public void setProduct(String product) {
		// ignore

	}

	public void setReporter(String reporter) {
		// ignore

	}

	public void setSummary(String summary) {
		// ignore

	}

	public void setTaskKind(String taskKind) {
		// ignore

	}

	public void setTaskUrl(String taskUrl) {
		// ignore

	}

	public void merge(ITaskMapping source) {
		// ignore

	}

	public List<String> getCc() {
		// ignore
		return null;
	}

	public List<String> getKeywords() {
		// ignore
		return null;
	}

	public String getReporter() {
		// ignore
		return null;
	}

	public String getResolution() {
		// ignore
		return null;
	}

	public String getTaskStatus() {
		// ignore
		return null;
	}

}
