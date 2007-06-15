/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
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
	public String getRepositoryKind() {
		return LocalRepositoryConnector.REPOSITORY_KIND;
	}

	public boolean isNotified() {
		return true;
	}
	
	public String getLastSyncDateStamp() {
		return SYNC_DATE_NOW;
	}
	
	public String getOwner() {
		return LocalRepositoryConnector.REPOSITORY_KIND;
	}
	
	@Override
	public String getTaskKey() {
		return null;
	}

}
