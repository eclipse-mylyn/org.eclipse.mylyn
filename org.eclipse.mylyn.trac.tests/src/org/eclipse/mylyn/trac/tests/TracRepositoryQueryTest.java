/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.RefactorRepositoryUrlOperation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.trac.tests.support.TracTestConstants;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryQueryTest extends TestCase {

	public void testChangeRepositoryUrl() throws Exception {
		TaskRepository repository = TracTestUtil.init(TracTestConstants.TEST_TRAC_096_URL, Version.XML_RPC);

		TracSearch search = new TracSearch();
		String queryUrl = repository.getRepositoryUrl() + ITracClient.QUERY_URL + search.toUrl();
		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(repository);
		query.setUrl(queryUrl);
		TasksUiPlugin.getTaskList().addQuery((RepositoryQuery) query);

		String taskId = "123";
		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskId);
		task.setUrl(repository.getRepositoryUrl() + ITracClient.TICKET_URL + taskId);
		TasksUiPlugin.getTaskList().addTask(task);

		String oldUrl = repository.getRepositoryUrl();
		String newUrl = TracTestConstants.TEST_TRAC_010_URL;
		new RefactorRepositoryUrlOperation(oldUrl, newUrl).run(new NullProgressMonitor());
		repository.setRepositoryUrl(newUrl);

		assertEquals(newUrl, query.getRepositoryUrl());
		assertEquals(newUrl + ITracClient.QUERY_URL + search.toUrl(), query.getUrl());
		assertEquals(newUrl + ITracClient.TICKET_URL + taskId, task.getUrl());
	}
}
