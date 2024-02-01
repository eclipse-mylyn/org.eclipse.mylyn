/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class RepositoryPerson implements IRepositoryPerson {

	private String name;

	private final String personId;

	private final TaskRepository taskRepository;

	private final Map<String, String> attributes = new HashMap<>();

	public RepositoryPerson(TaskRepository taskRepository, String personId) {
		this.taskRepository = taskRepository;
		this.personId = personId;
	}

	@Override
	public String getConnectorKind() {
		return taskRepository.getConnectorKind();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPersonId() {
		return personId;
	}

	@Override
	public String getRepositoryUrl() {
		return taskRepository.getRepositoryUrl();
	}

	@Override
	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	@Override
	public String getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public Map<String, String> getAttributes() {
		return Map.copyOf(attributes);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	@Override
	public boolean matchesUsername(String username) {
		String thisUsername = attributes.get(TaskAttribute.PERSON_USERNAME);
		if (thisUsername != null) {
			return thisUsername.equals(username);
		}
		return getPersonId().equals(username);
	}

	@Override
	public String toString() {

		if (getName() == null) {
			return getPersonId();
		} else if (getPersonId() == null) {
			return getName();
		} else if (getName().equals(getPersonId())) {
			return getName();
		} else {
			return getName() + " <" + getPersonId() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}
