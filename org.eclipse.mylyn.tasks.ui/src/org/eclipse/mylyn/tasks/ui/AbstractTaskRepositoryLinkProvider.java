/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;

/**
 * Extend to provide linking between repositories and tasks.
 * 
 * @author Eugene Kuleshov
 * @since 2.0
 */
public abstract class AbstractTaskRepositoryLinkProvider implements IExecutableExtension {

	private static final int DEFAULT_ORDER = 1000;

	private String id;

	private String name;

	private int order;

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) {
		id = config.getAttribute("id");
		name = config.getAttribute("name");
		try {
			order = Integer.parseInt(config.getAttribute("order"));
		} catch (NumberFormatException ex) {
			order = DEFAULT_ORDER;
		}
	}

	public abstract TaskRepository getTaskRepository(IResource resource, TaskRepositoryManager repositoryManager);

	public boolean canSetTaskRepository(IResource resource) {
		return false;
	}

	public boolean setTaskRepository(IResource resource, TaskRepository repository) {
		return false;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getOrder() {
		return order;
	}
}