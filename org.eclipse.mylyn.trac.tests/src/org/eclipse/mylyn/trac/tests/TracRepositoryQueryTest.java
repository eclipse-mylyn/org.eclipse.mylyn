/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.RefactorRepositoryUrlOperation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryQueryTest extends TestCase {

	public void testChangeRepositoryUrl() throws Exception {
		TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		TaskRepository repository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND,
				TracTestConstants.TEST_TRAC_096_URL);
		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());

		TracSearch search = new TracSearch();
		String queryUrl = repository.getRepositoryUrl() + ITracClient.QUERY_URL + search.toUrl();
		TracRepositoryQuery query = new TracRepositoryQuery(repository.getRepositoryUrl(), queryUrl, "description");
		TasksUiPlugin.getTaskList().addQuery(query);

		TracTask task = new TracTask(TracTestConstants.TEST_TRAC_096_URL, "" + 123, "desc");
		TasksUiPlugin.getTaskList().addTask(task);

		String oldUrl = repository.getRepositoryUrl();
		String newUrl = TracTestConstants.TEST_TRAC_010_URL;
		new RefactorRepositoryUrlOperation(oldUrl, newUrl).run(new NullProgressMonitor());
		repository.setRepositoryUrl(newUrl);

		assertEquals(newUrl, query.getRepositoryUrl());
		assertEquals(newUrl + ITracClient.QUERY_URL + search.toUrl(), query.getUrl());
		assertEquals(newUrl + ITracClient.TICKET_URL + 123, task.getUrl());
	}

	public void testGetFilterList() {
		String repositoryUrl = "https://foo.bar/repo";
		String parameterUrl = "&status=new&status=assigned&status=reopened&milestone=0.1";
		String queryUrl = repositoryUrl + ITracClient.QUERY_URL + parameterUrl;
		TracRepositoryQuery query = new TracRepositoryQuery(repositoryUrl, queryUrl, "description");

		TracSearch filterList = query.getTracSearch();

		assertEquals(parameterUrl, filterList.toUrl());
		assertEquals("&status=new|assigned|reopened&milestone=0.1", filterList.toQuery());

		List<TracSearchFilter> list = filterList.getFilters();
		TracSearchFilter filter = list.get(0);
		assertEquals("status", filter.getFieldName());
		assertEquals(Arrays.asList("new", "assigned", "reopened"), filter.getValues());
		filter = list.get(1);
		assertEquals("milestone", filter.getFieldName());
		assertEquals(Arrays.asList("0.1"), filter.getValues());
	}

}
