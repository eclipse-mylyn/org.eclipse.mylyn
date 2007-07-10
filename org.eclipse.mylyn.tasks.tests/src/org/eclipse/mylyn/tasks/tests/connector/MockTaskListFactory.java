/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.w3c.dom.Element;

/**
 * @author Mik Kersten
 */
public class MockTaskListFactory extends AbstractTaskListFactory {
	
	private static final String QUERY_ELEMENT_NAME = "MockQuery";

	@Override
	public boolean canCreate(AbstractTask task) {
		return task instanceof MockRepositoryTask;
	}
	
	@Override
	public boolean canCreate(AbstractRepositoryQuery query) {
		return query instanceof MockRepositoryQuery;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element) {
		MockRepositoryTask task = new MockRepositoryTask(repositoryUrl, taskId, summary);
		return task;
	}

	@Override
	public String getTaskElementName() {
		return "Mock" + AbstractTaskListFactory.KEY_TASK;
	}

	@Override
	public String getQueryElementName(AbstractRepositoryQuery query) {
		return QUERY_ELEMENT_NAME;
	}
	
	@Override
	public AbstractRepositoryQuery createQuery(String repositoryUrl, String queryString, String label, Element element) {
		MockRepositoryQuery query = new MockRepositoryQuery(label, queryString);
		return query;
	}
	
	@Override
	public Set<String> getQueryElementNames() {
		Set<String> names = new HashSet<String>();
		names.add(QUERY_ELEMENT_NAME);
		return names;
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
