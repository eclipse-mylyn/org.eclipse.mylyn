/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasklist.tests.mockconnector;

import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;

/**
 * @author Rob Elves
 */
public class MockQueryHit extends AbstractQueryHit {

	AbstractRepositoryTask task = null;
	
	public MockQueryHit(String repositoryUrl, String description, String id) {
		super(repositoryUrl, description, id);		
	}

	@Override
	public AbstractRepositoryTask getCorrespondingTask() {
		return task;
	}

	@Override
	public AbstractRepositoryTask getOrCreateCorrespondingTask() {
		return task;
	}

	@Override
	public boolean isCompleted() {
		return task.isCompleted();
	}

	@Override
	public void setCorrespondingTask(AbstractRepositoryTask task) {
		this.task = task;
	}

	public String getDescription() {
		return task.getDescription();
	}

	public String getPriority() {
		return task.getPriority();
	}

	public void setHandleIdentifier(String id) {
		// ignore
	}

}
