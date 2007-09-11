/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;

/**
 * @author Rob Elves
 */
public class Person extends AbstractTaskContainer {

	private String connectorKind;

	private String repositoryUrl;

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
