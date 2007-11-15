/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.monitor.core.StatusHandler;

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
			this.taskData = (RepositoryTaskData) TaskDataManager.ObjectCloner.deepCopy(taskData);
			this.taskData.setAttributeFactory(taskData.getAttributeFactory());
		} catch (Exception e) {
			StatusHandler.fail(e, "Error creating a task dat copy", false);
			throw new RuntimeException(e);
		}
	}

	public TaskSelection(AbstractTask task) {
		RepositoryTaskData taskData = new RepositoryTaskData(new IdentityAttributeFactory(), task.getConnectorKind(), task.getRepositoryUrl(), task.getTaskId(), task.getTaskKind());
		taskData.setSummary(task.getSummary());
		taskData.setAttributeValue(RepositoryTaskAttribute.PRIORITY, task.getPriority());
		this.taskData = taskData; 
	}
	
	public TaskSelection(String summary, String description) {
		RepositoryTaskData taskData = new RepositoryTaskData(new IdentityAttributeFactory(), LocalRepositoryConnector.REPOSITORY_KIND, LocalRepositoryConnector.REPOSITORY_URL, "");
		taskData.setSummary(summary);
		taskData.setDescription(description);
		this.taskData = taskData; 		
	}
	
	public RepositoryTaskData getTaskData() {
		return taskData;
	}

	private class IdentityAttributeFactory extends AbstractAttributeFactory {

		private static final long serialVersionUID = 1L;

		@Override
		public Date getDateForAttributeType(String attributeKey, String dateString) {
			return null;
		}

		@Override
		public String getName(String key) {
			return null;
		}

		@Override
		public boolean isHidden(String key) {
			return false;
		}

		@Override
		public boolean isReadOnly(String key) {
			return false;
		}

		@Override
		public String mapCommonAttributeKey(String key) {
			return key;
		}
		
	}
	
}
