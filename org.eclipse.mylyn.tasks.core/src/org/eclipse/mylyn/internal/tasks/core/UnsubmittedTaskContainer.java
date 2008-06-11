/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

/**
 * @author Rob Elves
 */
public class UnsubmittedTaskContainer extends AbstractTaskCategory {

	private static final String HANDLE = "unsubmitted";

	private String repositoryUrl;

	private final String connectorKind;

	public UnsubmittedTaskContainer(String connectorKind, String repositoryUrl) {
		super(repositoryUrl + "-" + HANDLE);
		this.repositoryUrl = repositoryUrl;
		this.connectorKind = connectorKind;
	}

	/**
	 * setting will also refactor handle
	 */
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
		this.setHandleIdentifier(repositoryUrl + "-" + HANDLE);
	}

	@Override
	public String getSummary() {
		return "Unsubmitted [" + getRepositoryUrl() + "]";
	}

	@Override
	public boolean isUserManaged() {
		return false;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getConnectorKind() {
		return connectorKind;
	}
}
