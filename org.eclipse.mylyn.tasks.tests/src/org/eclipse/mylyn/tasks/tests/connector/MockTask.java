/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;

/**
 * @author Mik Kersten
 */
public class MockTask extends AbstractTask {

	private String ownerId;

	public MockTask(String taskId) {
		this(MockRepositoryConnector.REPOSITORY_URL, taskId, taskId);
	}

	public MockTask(String repositoryUrl, String taskId) {
		this(repositoryUrl, taskId, taskId);
	}

	public MockTask(String repositoryUrl, String taskId, String summary) {
		super(repositoryUrl, taskId, summary);
		setTaskKey(taskId);
	}

	@Override
	public String getConnectorKind() {
		return "mock";
	}

	@Override
	public void setOwner(String ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public String getOwner() {
		if (ownerId == null) {
			return super.getOwner();
		} else {
			return ownerId;
		}
	}

	@Override
	public String toString() {
		return "Mock Task: " + super.getHandleIdentifier();
	}

	@Deprecated
	@Override
	public boolean isLocal() {
		// ignore
		return false;
	}

	@Override
	public String getTaskKey() {
		return taskKey;
	}

}
