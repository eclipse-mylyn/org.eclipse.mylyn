/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class RepositoryPerson {

	private final String personId;

	private final String connectorKind;

	private final String repositoryUrl;

	private String name;

	public RepositoryPerson(String connectorKind, String repositoryUrl, String taskId, String personId) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.personId = personId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPersonId() {
		return personId;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	@Override
	public String toString() {
		if (getName() == null) {
			return getPersonId();
		} else {
			return getName() + " <" + getPersonId() + ">";
		}
	}
}
