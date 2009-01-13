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

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class AttributeTaskMapper {

	private final Map<String, String> attributes;

	public AttributeTaskMapper(Map<String, String> attributes) {
		Assert.isNotNull(attributes);
		this.attributes = attributes;
	}

	public boolean isMappingComplete() {
		return getTaskRepository() != null && attributes.get(IRepositoryConstants.PRODUCT) != null;
	}

	public TaskRepository getTaskRepository() {
		TaskRepository taskRepository = null;
		String repositoryUrl = attributes.get(IRepositoryConstants.REPOSITORY_URL);
		if (repositoryUrl != null) {
			String connectorKind = attributes.get(IRepositoryConstants.CONNECTOR_KIND);
			if (connectorKind != null) {
				taskRepository = TasksUi.getRepositoryManager().getRepository(connectorKind, repositoryUrl);
			}
		}
		return taskRepository;
	}

	public TaskData createTaskData(IProgressMonitor monitor) throws CoreException {
		ITaskMapping taskMapping = getTaskMapping();
		return TasksUiInternal.createTaskData(getTaskRepository(), taskMapping, taskMapping, monitor);
	}

	public ITaskMapping getTaskMapping() {
		return new KeyValueMapping(attributes);
	}

	private static class KeyValueMapping extends TaskMapping {

		private final Map<String, String> attributes;

		public KeyValueMapping(Map<String, String> attributes) {
			Assert.isNotNull(attributes);
			this.attributes = attributes;
		}

		@Override
		public void merge(ITaskMapping source) {
		}

		@Override
		public Date getCompletionDate() {
			// ignore
			return null;
		}

		@Override
		public String getComponent() {
			return attributes.get(IRepositoryConstants.COMPONENT);
		}

		@Override
		public Date getCreationDate() {
			// ignore
			return null;
		}

		@Override
		public String getDescription() {
			return attributes.get(IRepositoryConstants.DESCRIPTION);
		}

		@Override
		public Date getDueDate() {
			// ignore
			return null;
		}

		@Override
		public Date getModificationDate() {
			// ignore
			return null;
		}

		@Override
		public String getOwner() {
			// ignore
			return null;
		}

		@Override
		public PriorityLevel getPriorityLevel() {
			// ignore
			return null;
		}

		@Override
		public String getProduct() {
			return attributes.get(IRepositoryConstants.PRODUCT);
		}

		@Override
		public String getSummary() {
			// ignore
			return null;
		}

		@Override
		public TaskData getTaskData() {
			// ignore
			return null;
		}

		@Override
		public String getTaskKey() {
			// ignore
			return null;
		}

		@Override
		public String getTaskKind() {
			// ignore
			return null;
		}

		@Override
		public String getTaskUrl() {
			// ignore
			return null;
		}

		@Override
		public List<String> getCc() {
			// ignore
			return null;
		}

		@Override
		public List<String> getKeywords() {
			// ignore
			return null;
		}

		@Override
		public String getReporter() {
			// ignore
			return null;
		}

		@Override
		public String getResolution() {
			// ignore
			return null;
		}

		@Override
		public String getTaskStatus() {
			// ignore
			return null;
		}

	}

}
