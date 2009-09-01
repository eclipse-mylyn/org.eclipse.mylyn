/*******************************************************************************
 * Copyright (c) 2004, 2009 Nathan Hapke and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nathan Hapke - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * @author Nathan Hapke
 * @author Rob Elves
 */
public class BugzillaRepositoryConnectorStandaloneTest extends TestCase {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	@SuppressWarnings("unused")
	private AbstractTaskDataHandler handler;

	@Override
	protected void setUp() throws Exception {
		repository = BugzillaFixture.current().repository();
		connector = new BugzillaRepositoryConnector();
//		BugzillaLanguageSettings language = BugzillaCorePlugin.getDefault().getLanguageSetting(
//				IBugzillaConstants.DEFAULT_LANG);
//		BugzillaRepositoryConnector.addLanguageSetting(language);
		handler = connector.getTaskDataHandler();
	}

	/**
	 * This is the first test so that the repository credentials are correctly set for the other tests
	 */
	public void testAddCredentials() {
		AuthenticationCredentials auth = repository.getCredentials(AuthenticationType.REPOSITORY);
		assertTrue(auth != null && auth.getPassword() != null && !auth.getPassword().equals("")
				&& auth.getUserName() != null && !auth.getUserName().equals(""));
	}

	public void testGetBug() throws Exception {
		Set<String> taskIds = new HashSet<String>();
		taskIds.add("1");
		final Set<TaskData> changedTaskData = new HashSet<TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				changedTaskData.add(taskData);
			}
		};
		connector.getTaskDataHandler().getMultiTaskData(repository, taskIds, collector, new NullProgressMonitor());
		assertEquals(1, changedTaskData.size());
		for (TaskData taskData : changedTaskData) {
			String taskId = taskData.getTaskId();
			if (taskId.equals("1")) {
				assertEquals("user@mylar.eclipse.org", taskData.getRoot().getAttribute(
						BugzillaAttribute.ASSIGNED_TO.getKey()).getValue());
				assertEquals("foo", taskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue());
				// You can use the getAttributeValue to pull up the information on any
				// part of the bug
				assertEquals("P1", taskData.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue());

			} else {
				fail("Unexpected TaskData returned");
			}
		}
	}

	public void testGetBugs() throws Exception {
		Set<String> taskIds = new HashSet<String>();
		taskIds.add("1");
		taskIds.add("2");
		taskIds.add("4");
		final Set<TaskData> changedTaskData = new HashSet<TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				changedTaskData.add(taskData);
			}
		};
		connector.getTaskDataHandler().getMultiTaskData(repository, taskIds, collector, new NullProgressMonitor());
		assertEquals(3, changedTaskData.size());
		for (TaskData taskData : changedTaskData) {
			String taskId = taskData.getTaskId();
			if (taskId.equals("1")) {
				assertEquals("user@mylar.eclipse.org", taskData.getRoot().getAttribute(
						BugzillaAttribute.ASSIGNED_TO.getKey()).getValue());
				assertEquals("foo", taskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue());
				// You can use the getAttributeValue to pull up the information on any
				// part of the bug
				assertEquals("P1", taskData.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue());

			} else if (taskId.equals("2")) {
				assertEquals("nhapke@cs.ubc.ca", taskData.getRoot()
						.getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey())
						.getValue());
				assertEquals("search-match-test 1", taskData.getRoot().getAttribute(
						BugzillaAttribute.LONG_DESC.getKey()).getValue());
			} else if (taskId.equals("4")) {
				assertEquals("relves@cs.ubc.ca", taskData.getRoot()
						.getAttribute(BugzillaAttribute.REPORTER.getKey())
						.getValue());
				assertEquals("Test", taskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue());
			} else {
				fail("Unexpected TaskData returned");
			}

		}
	}

	public void testQueryViaConnector() throws Exception {
		String queryUrlString = repository.getRepositoryUrl()
				+ "/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";

		// holds onto actual hit objects
		BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();
		RepositoryQuery query = new RepositoryQuery(repository.getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);

		final Set<TaskData> changedTaskData = new HashSet<TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				changedTaskData.add(taskData);
			}
		};

		connector.performQuery(repository, query, collector, null, new NullProgressMonitor());
		assertEquals(2, changedTaskData.size());
		for (TaskData taskData : changedTaskData) {
			assertTrue(taskData.getRoot().getAttribute(BugzillaAttribute.SHORT_DESC.getKey()).getValue().contains(
					"search-match-test"));
		}
	}

}