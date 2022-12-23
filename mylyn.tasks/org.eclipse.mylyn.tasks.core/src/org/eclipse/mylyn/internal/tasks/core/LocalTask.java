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
public class LocalTask extends AbstractTask {

	public static final String SYNC_DATE_NOW = "now"; //$NON-NLS-1$

	public LocalTask(String taskId, String summary) {
		super(LocalRepositoryConnector.REPOSITORY_URL, taskId, summary);
	}

	@Deprecated
	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public String getConnectorKind() {
		return LocalRepositoryConnector.CONNECTOR_KIND;
	}

	@Override
	public boolean isNotified() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getLastReadTimeStamp() {
		return SYNC_DATE_NOW;
	}

	@Override
	public String getOwner() {
		return LocalRepositoryConnector.CONNECTOR_KIND;
	}

	@Override
	public String getTaskKey() {
		return null;
	}

}
