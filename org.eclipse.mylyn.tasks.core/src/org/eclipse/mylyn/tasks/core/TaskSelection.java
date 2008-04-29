/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager.ObjectCloner;

/**
 * @author Steffen Pingel
 * @since 2.2
 */
public class TaskSelection {

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

	public TaskSelection(AbstractTask task) {
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

	public RepositoryTaskData getTaskData() {
		return taskData;
	}

}
