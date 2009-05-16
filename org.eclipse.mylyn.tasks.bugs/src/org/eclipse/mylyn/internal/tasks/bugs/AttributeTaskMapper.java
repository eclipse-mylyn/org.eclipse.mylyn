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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProduct;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.ISupportResponse;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.ITaskContribution;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class AttributeTaskMapper implements ITaskContribution, ISupportResponse {

	private final Map<String, String> attributes;

	private final IProduct product;

	private final IStatus status;

	private TaskData taskData;

	public AttributeTaskMapper(IStatus status, IProduct product) {
		Assert.isNotNull(status);
		Assert.isNotNull(product);
		this.status = status;
		this.product = product;
		this.attributes = new HashMap<String, String>();
	}

	public void appendToDescription(String text) {
		String description = getAttribute(IRepositoryConstants.DESCRIPTION);
		setAttribute(IRepositoryConstants.DESCRIPTION, (description != null) ? description + text : text);
	}

	public TaskData createTaskData(IProgressMonitor monitor) throws CoreException {
		ITaskMapping taskMapping = getTaskMapping();
		return TasksUiInternal.createTaskData(getTaskRepository(), taskMapping, taskMapping, monitor);
	}

	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public IProduct getProduct() {
		return product;
	}

	public IStatus getStatus() {
		return status;
	}

	public TaskData getTaskData() {
		return taskData;
	}

	public ITaskMapping getTaskMapping() {
		return new KeyValueMapping(attributes);
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

	public boolean isMappingComplete() {
		return getTaskRepository() != null;
	}

	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}

	void setTaskData(TaskData taskData) {
		this.taskData = taskData;
	}

}
