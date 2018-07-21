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

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Mik Kersten
 */
public abstract class AutomaticRepositoryTaskContainer extends AbstractTaskCategory implements ITaskRepositoryElement {

	protected String repositoryUrl;

	private final String connectorKind;

	private final String handleSuffix;

	public AutomaticRepositoryTaskContainer(String handleSuffix, String connectorKind, String repositoryUrl) {
		super(repositoryUrl + "-" + handleSuffix); //$NON-NLS-1$
		this.handleSuffix = handleSuffix;
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
	}

	@Override
	public boolean isUserManaged() {
		return false;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	@Override
	public String getPriority() {
		return PriorityLevel.P1.toString();
	}

	/**
	 * setting will also refactor handle
	 */
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
		setHandleIdentifier(repositoryUrl + "-" + handleSuffix); //$NON-NLS-1$
	}

}