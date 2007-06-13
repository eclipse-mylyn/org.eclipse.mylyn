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

package org.eclipse.mylyn.tasks.tests.connector;

import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * @author Mik Kersten
 */
public class MockRepositoryTask extends AbstractTask {
	
	private String ownerId;
	
	public MockRepositoryTask(String taskId) {
		super(MockRepositoryConnector.REPOSITORY_URL, taskId, taskId);
	}

	public MockRepositoryTask(String repositoryUrl, String taskId) {
		super(repositoryUrl, taskId, taskId);
	}
	
	public MockRepositoryTask(String repositoryUrl, String taskId, String summary) {
		super(repositoryUrl, taskId, summary);
	}
	
	@Override
	public String getRepositoryKind() {
		return "mock";
	}

	public void setOwner(String ownerId) {
		this.ownerId = ownerId;
	}
	
	@Override
	public String getOwner() {
		if(ownerId == null) {
			return super.getOwner();
		} else {
			return ownerId;
		}
	}

	@Override
	public String toString() {
		return super.getHandleIdentifier();
	}

	@Override
	public boolean isLocal() {
		// ignore
		return false;
	}

}
