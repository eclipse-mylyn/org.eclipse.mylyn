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

package org.eclipse.mylar.tasks.tests.connector;

import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskExternalizationException;
import org.eclipse.mylar.tasks.core.TaskList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mik Kersten
 */
public class MockRepositoryExternalizer extends DelegatingTaskExternalizer {

	private static final String KEY_MOCK = "Mock";
	
	@Override
	public boolean canCreateElementFor(AbstractRepositoryQuery query) {
		return query instanceof MockRepositoryQuery;
	}

	@Override
	public boolean canCreateElementFor(ITask task) {
		return task instanceof MockRepositoryTask;
	}

//	@Override
//	public boolean canCreateElementFor(AbstractQueryHit queryHit) {
//		return queryHit instanceof AbstractQueryHit;
//	}
	
	@Override
	public boolean canReadCategory(Node node) {
		return false;
	}

	@Override
	public boolean canReadQuery(Node node) {
		return false;
	}

	@Override
	public boolean canReadQueryHit(Node node) {
		return false;
	}

	@Override
	public String getTaskTagName() {
		return KEY_MOCK;
	}
	
	@Override
	public ITask createTask(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList, AbstractTaskContainer category, ITask parent)
			throws TaskExternalizationException {
		MockRepositoryTask task = new MockRepositoryTask(repositoryUrl, taskId, summary);
		return task;
	}

}
