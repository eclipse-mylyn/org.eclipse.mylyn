/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;

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

	/**
	 * This operation is invoked frequently by hyperlink detectors and needs to be fast (i.e. cannot do network access
	 * or invoke long-running refreshes). Return null if the repository cannot be resolved without excessive file
	 * I/O.@since 3.0
	 * 
	 * @since 3.0
	 */
	public abstract TaskRepository getTaskRepository(IResource resource, IRepositoryManager repositoryManager);

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