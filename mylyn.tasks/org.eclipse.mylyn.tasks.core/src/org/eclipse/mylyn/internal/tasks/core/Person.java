/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
