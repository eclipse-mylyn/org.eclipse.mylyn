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
 * @author Steffen Pingel
 */
public class TaskTask extends AbstractTask {

	private final String connectorKind;

	public TaskTask(String connectorKind, String repositoryUrl, String taskId) {
		super(repositoryUrl, taskId, ""); //$NON-NLS-1$
		this.connectorKind = connectorKind;
	}

	@Override
	public String getConnectorKind() {
		return connectorKind;
	}

	@Override
	public String getTaskKey() {
		return taskKey;
	}

	@Deprecated
	@Override
	public boolean isLocal() {
		return false;
	}

}
