/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.TaskFactory;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylyn.trac.tests.support.TestFixture;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * @author Steffen Pingel
 */
public class RepositorySearchQueryTest extends TestCase {

	private TestData data;

	private TaskRepositoryManager manager;

//	private TracRepositoryConnector connector;

	private TaskRepository repository;

	public RepositorySearchQueryTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		data = TestFixture.init010();
		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

//		connector = (TracRepositoryConnector) manager.getRepositoryConnector(TracUiPlugin.REPOSITORY_KIND);
		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(true);
	}

	protected void init(String url, Version version) {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, url);
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void testSearch() {
		init(TracTestConstants.TEST_TRAC_096_URL, Version.TRAC_0_9);

		TracSearch search = new TracSearch();
		String queryUrl = repository.getUrl() + ITracClient.QUERY_URL + search.toUrl();
		TracRepositoryQuery query = new TracRepositoryQuery(repository.getUrl(), queryUrl, "description");
		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager()
				.getTaskList(), repository, query, new TaskFactory(repository, false, false));
				
		collector.run(new NullProgressMonitor());
		for (AbstractTask task : collector.getTaskHits()) {
			assertEquals(TracTestConstants.TEST_TRAC_096_URL, task.getRepositoryUrl());		
		}
		assertEquals(data.tickets.size(), collector.getTaskHits().size());
	}

}
