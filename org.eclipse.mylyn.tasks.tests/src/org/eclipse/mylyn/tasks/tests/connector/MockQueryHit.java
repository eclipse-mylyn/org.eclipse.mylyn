/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests.connector;

import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;

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
		if(task == null) {
			task = new MockRepositoryTask(AbstractRepositoryTask.getHandle(repositoryUrl, id));
		}
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

}
