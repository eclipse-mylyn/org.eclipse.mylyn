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

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Mik Kersten
 */
public abstract class AutomaticRepositoryTaskContainer extends AbstractTaskCategory {

	protected String repositoryUrl;

	private final String connectorKind;

	public AutomaticRepositoryTaskContainer(String handleAndDescription, String connectorKind, String repositoryUrl) {
		super(handleAndDescription);
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
	}

	public abstract String getSummaryLabel();

	protected abstract String getHandleSuffix();

	@Override
	public String getSummary() {
		return getSummaryLabel() + " [" + getRepositoryUrl() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
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
		this.setHandleIdentifier(repositoryUrl + "-" + getHandleSuffix()); //$NON-NLS-1$
	}

}