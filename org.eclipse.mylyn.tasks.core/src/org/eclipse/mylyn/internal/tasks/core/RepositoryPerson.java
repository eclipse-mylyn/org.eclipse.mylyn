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

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class RepositoryPerson implements IRepositoryPerson {

	private String name;

	private final String personId;

	private final TaskRepository taskRepository;

	public RepositoryPerson(TaskRepository taskRepository, String personId) {
		this.taskRepository = taskRepository;
		this.personId = personId;
	}

	public String getConnectorKind() {
		return taskRepository.getConnectorKind();
	}

	public String getName() {
		return name;
	}

	public String getPersonId() {
		return personId;
	}

	public String getRepositoryUrl() {
		return taskRepository.getRepositoryUrl();
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if (getName() == null) {
			return getPersonId();
		} else {
			return getName() + " <" + getPersonId() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}
