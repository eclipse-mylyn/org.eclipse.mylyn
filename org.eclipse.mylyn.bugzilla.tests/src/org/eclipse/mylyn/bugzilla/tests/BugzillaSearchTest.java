/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Rob Elves
 */
public class BugzillaSearchTest extends TestCase {

	private static final String QUERY_NAME = "Query Page Name";

	private static final String BUG_DESC_SUBSTRING_SEARCH = "/buglist.cgi?short_desc_type=allwordssubstr&short_desc=";// search-match-test&amp";

	private static final String SEARCH_DESCRIPTION = "search-match-test";

	private static final int NUM_EXPECTED_HITS = 2;

	private static final int NUM_REPOSITORIES = 0;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		assertEquals(NUM_REPOSITORIES, TasksUiPlugin.getRepositoryManager().getRepositories(
				BugzillaCorePlugin.CONNECTOR_KIND).size());
	}

	@Override
	protected void tearDown() throws Exception {
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		super.tearDown();
	}

	public void testSearching218() throws Exception {
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.TEST_BUGZILLA_218_URL);
		repository.setVersion(IBugzillaConstants.BugzillaServerVersion.SERVER_218.toString());
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		assertEquals(NUM_EXPECTED_HITS, runQuery(IBugzillaConstants.TEST_BUGZILLA_218_URL, SEARCH_DESCRIPTION).size());
	}

	public void testSearching220() throws Exception {
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.TEST_BUGZILLA_220_URL);
		repository.setVersion(IBugzillaConstants.BugzillaServerVersion.SERVER_220.toString());
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		assertEquals(NUM_EXPECTED_HITS, runQuery(IBugzillaConstants.TEST_BUGZILLA_220_URL, SEARCH_DESCRIPTION).size());
	}

	public void testSearching2201() throws Exception {
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.TEST_BUGZILLA_2201_URL);
		repository.setVersion(IBugzillaConstants.BugzillaServerVersion.SERVER_220.toString());
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		assertEquals(NUM_EXPECTED_HITS, runQuery(IBugzillaConstants.TEST_BUGZILLA_2201_URL, SEARCH_DESCRIPTION).size());
	}

	public void testSearching222() throws Exception {
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		repository.setVersion(IBugzillaConstants.BugzillaServerVersion.SERVER_222.toString());
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		Set<TaskData> hits = runQuery(IBugzillaConstants.TEST_BUGZILLA_222_URL, SEARCH_DESCRIPTION);
		assertEquals(NUM_EXPECTED_HITS, hits.size());
	}

	private Set<TaskData> runQuery(String repositoryURL, String SearchString) throws Exception {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				BugzillaCorePlugin.CONNECTOR_KIND, repositoryURL);
		assertNotNull(repository);
		IRepositoryQuery repositoryQuery = TasksUi.getRepositoryModel().createRepositoryQuery(repository);
		repositoryQuery.setUrl(repository.getRepositoryUrl() + BUG_DESC_SUBSTRING_SEARCH + SearchString);
		repositoryQuery.setSummary(QUERY_NAME);

		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				BugzillaCorePlugin.CONNECTOR_KIND);
		final Set<TaskData> changedTaskData = new HashSet<TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				changedTaskData.add(taskData);
			}
		};

		connector.performQuery(repository, repositoryQuery, collector, null, new NullProgressMonitor());

		return changedTaskData;
	}

}
