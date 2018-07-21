/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;

/**
 * @author Rob Elves
 */
public class BugzillaSearchTest extends TestCase {

	private static final String QUERY_NAME = "Query Page Name";

	private static final String BUG_SUMMARY_SUBSTRING_SEARCH = "/buglist.cgi?short_desc_type=allwordssubstr&short_desc=";

	private static final String BUG_COMMENT_SUBSTRING_SEARCH = "/buglist.cgi?long_desc_type=allwordssubstr&long_desc=";

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BugzillaFixture.current().client();
		this.connector = BugzillaFixture.current().connector();
		this.repository = BugzillaFixture.current().repository();
	}

	public void testSummarySearching() throws Exception {
		long now = System.currentTimeMillis();
		TaskData newTaskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, now + "", null);
		assertNotNull(newTaskData);
		assertEquals(1, runQuery(BUG_SUMMARY_SUBSTRING_SEARCH, now + "").size());
	}

	public void testCommentSearching() throws Exception {
		long now = System.currentTimeMillis();
		TaskData newTaskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, now + "");
		assertNotNull(newTaskData);
		assertEquals(1, runQuery(BUG_COMMENT_SUBSTRING_SEARCH, now + "").size());
	}

	private Set<TaskData> runQuery(String requestUrl, String queryString) throws Exception {
		IRepositoryQuery repositoryQuery = TasksUi.getRepositoryModel().createRepositoryQuery(repository);
		repositoryQuery.setUrl(repository.getRepositoryUrl() + requestUrl + queryString);
		repositoryQuery.setSummary(QUERY_NAME);

		final Set<TaskData> changedTaskData = new HashSet<TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				changedTaskData.add(taskData);
			}
		};

		IStatus status = connector.performQuery(repository, repositoryQuery, collector, null, new NullProgressMonitor());
		assertEquals(IStatus.OK, status.getCode());
		return changedTaskData;
	}

}
