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
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.TaskExternalizationException;
import org.w3c.dom.Element;

/**
 * @author Mik Kersten
 */
public class MockTaskListFactory extends AbstractTaskListFactory {

	@Override
	public boolean canCreate(AbstractTask task) {
		return task instanceof MockRepositoryTask;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element) throws TaskExternalizationException {
		MockRepositoryTask task = new MockRepositoryTask(repositoryUrl, taskId, summary);
		return task;
	}
 
	
	@Override
	public String getTaskElementName() {
		return "Mock" + AbstractTaskListFactory.KEY_TASK;
	}
	
//	private static final String KEY_MOCK = "Mock";
//	
//	@Override
//	public boolean canCreateElementFor(AbstractRepositoryQuery query) {
//		return query instanceof MockRepositoryQuery;
//	}
//
//	@Override
//	public boolean canCreateElementFor(AbstractTask task) {
//		return task instanceof MockRepositoryTask;
//	}

//	@Override
//	public boolean canCreateElementFor(AbstractQueryHit queryHit) {
//		return queryHit instanceof AbstractQueryHit;
//	}
	
//	@Override
//	public boolean canReadCategory(Node node) {
//		return false;
//	}
//
//	@Override
//	public boolean canReadQuery(Node node) {
//		return false;
//	}
//
//	@Override
//	public boolean canReadQueryHit(Node node) {
//		return false;
//	}

//	@Override
//	public String getTaskTagName() {
//		return KEY_MOCK;
//	}
//	
//	@Override
//	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList, AbstractTaskContainer category, AbstractTask parent)
//			throws TaskExternalizationException {
//		MockRepositoryTask task = new MockRepositoryTask(repositoryUrl, taskId, summary);
//		return task;
//	}

}
