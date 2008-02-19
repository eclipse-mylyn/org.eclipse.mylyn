/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.core.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

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
			String repositoryKind = attributes.get(IRepositoryConstants.REPOSITORY_KIND);
			if (repositoryKind != null) {
				taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryUrl);
			} else {
				taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryUrl, repositoryKind);
			}
		}
		return taskRepository;
	}

	public TaskSelection createTaskSelection() {
		TaskSelection selection = new TaskSelection("", "");
		applyTo(selection.getTaskData());
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
