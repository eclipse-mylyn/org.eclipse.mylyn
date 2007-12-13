/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * @author Rob Elves
 */
public class LocalTask extends AbstractTask {

	public static final String SYNC_DATE_NOW = "now";

	public LocalTask(String taskId, String summary) {
		super(LocalRepositoryConnector.REPOSITORY_URL, taskId, summary);
	}

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
