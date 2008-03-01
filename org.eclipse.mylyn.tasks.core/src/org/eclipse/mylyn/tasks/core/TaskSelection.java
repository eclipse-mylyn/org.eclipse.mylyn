/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.AbstractAttributeMapper;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
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
			this.taskData.refresh();
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Error creating a task data copy", e));
			throw new RuntimeException(e);
		}
	}

	public TaskSelection(AbstractTask task) {
		RepositoryTaskData taskData = new RepositoryTaskData(new IdentityAttributeFactory(), task.getConnectorKind(),
				task.getRepositoryUrl(), task.getTaskId(), task.getTaskKind());
		taskData.setSummary(task.getSummary());
		taskData.setAttributeValue(RepositoryTaskAttribute.PRIORITY, task.getPriority());
		this.taskData = taskData;
	}

	public TaskSelection(String summary, String description) {
		RepositoryTaskData taskData = new RepositoryTaskData(new IdentityAttributeFactory(),
				LocalRepositoryConnector.CONNECTOR_KIND, LocalRepositoryConnector.REPOSITORY_URL, "");
		taskData.setSummary(summary);
		taskData.setDescription(description);
		this.taskData = taskData;
	}

	public RepositoryTaskData getTaskData() {
		return taskData;
	}

	private class IdentityAttributeFactory extends AbstractAttributeFactory {

		private static final long serialVersionUID = 1L;

		private final AbstractAttributeMapper attributeMapper = new AbstractAttributeMapper(this) {
			@Override
			public String getType(RepositoryTaskAttribute taskAttribute) {
				return RepositoryTaskAttribute.TYPE_SHORT_TEXT;
			}
		};

		@Override
		public AbstractAttributeMapper getAttributeMapper() {
			return attributeMapper;
		}

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
