/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.core.deprecated.DefaultTaskSchema;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskSelection;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("deprecation")
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

	public TaskSelection createTaskSelection() {
		TaskSelection selection = new TaskSelection("", "");
		applyTo(selection.getLegacyTaskData());
		return selection;
	}

	public void applyTo(RepositoryTaskData taskData) {
		DefaultTaskSchema schema = new DefaultTaskSchema(taskData);
		for (Entry<String, String> entry : attributes.entrySet()) {
			if (IRepositoryConstants.PRODUCT.equals(entry.getKey())) {
				schema.setProduct(entry.getValue());
			} else if (IRepositoryConstants.COMPONENT.equals(entry.getKey())) {
				schema.setComponent(entry.getValue());
			}
		}
	}

}
