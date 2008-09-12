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
 * @author Steffen Pingel
 */
public class TaskTask extends AbstractTask {

	private final String connectorKind;

	public TaskTask(String connectorKind, String repositoryUrl, String taskId) {
		super(repositoryUrl, taskId, "");
		this.connectorKind = connectorKind;
		this.taskKey = taskId;
	}

	@Override
	public String getConnectorKind() {
		return connectorKind;
	}

	@Override
	public String getTaskKey() {
		return taskKey;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

}
