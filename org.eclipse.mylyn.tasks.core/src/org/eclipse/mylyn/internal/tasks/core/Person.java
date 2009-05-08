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

/**
 * @author Rob Elves
 */
public class Person extends AbstractTaskContainer implements ITaskRepositoryElement {

	private final String connectorKind;

	private final String repositoryUrl;

	public Person(String email, String connectorKind, String repositoryUrl) {
		super(email);
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}
}
