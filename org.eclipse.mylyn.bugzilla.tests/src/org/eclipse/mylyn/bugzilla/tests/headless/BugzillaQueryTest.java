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

package org.eclipse.mylyn.bugzilla.tests.headless;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.IBugzillaTestConstants;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaLanguageSettings;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * Example use of headless API (no ui dependencies)
 * 
 * @author Rob Elves
 * @author Nathan Hapke
 */
public class BugzillaQueryTest extends TestCase {

	private TaskRepository repository;

	@SuppressWarnings("unused")
	private AbstractRepositoryConnector connectorOriginal;

	private BugzillaRepositoryConnector connector;

	@SuppressWarnings("unused")
	private AbstractTaskDataHandler handler;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		connectorOriginal = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				BugzillaCorePlugin.CONNECTOR_KIND);

		BugzillaLanguageSettings language = BugzillaCorePlugin.getDefault().getLanguageSetting(
				IBugzillaConstants.DEFAULT_LANG);

		connector = new BugzillaRepositoryConnector();
		BugzillaRepositoryConnector.addLanguageSetting(language);
		handler = connector.getTaskDataHandler();
		repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaTestConstants.TEST_BUGZILLA_222_URL);
		Credentials credentials = TestUtil.readCredentials();
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);
	}

	@Override
	protected void tearDown() throws Exception {

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

	// README
	// public void testPostBug() throws Exception {
	// RepositoryTaskData taskData = handler.getTaskData(repository, "1");
	// assertNotNull(taskData);
	// assertEquals("user@mylar.eclipse.org", taskData.getAssignedTo());
	// assertEquals("foo", taskData.getDescription());
	// taskData.setSummary("New Summary");
	// // post this modification back to the repository
	// handler.postTaskData(repository, taskData);
	//
	// // You can use the getAttributeValue to pull up the information on any
	// // part of the bug
	// // assertEquals("P1",
	// //
	// taskData.getAttributeValue(BugzillaReportElement.PRIORITY.getKeyString()));
	// }

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

// public void testValidateCredentials() throws IOException,
// BugzillaException, KeyManagementException,
// GeneralSecurityException {
// BugzillaClient.validateCredentials(null, repository.getUrl(),
// repository.getCharacterEncoding(),
// repository.getUserName(), repository.getPassword());
// }
//
// public void testValidateCredentialsInvalidProxy() throws IOException,
// BugzillaException, KeyManagementException,
// GeneralSecurityException {
// BugzillaClient.validateCredentials(new Proxy(Proxy.Type.HTTP, new
// InetSocketAddress("localhost", 12356)),
// repository.getUrl(), repository.getCharacterEncoding(),
// repository.getUserName(), repository
// .getPassword());
// }

// public void testCredentialsEncoding() throws IOException,
// BugzillaException, KeyManagementException,
// GeneralSecurityException {
// String poundSignUTF8 =
// BugzillaClient.addCredentials(IBugzillaTestConstants.TEST_BUGZILLA_222_URL,
// "UTF-8",
// "testUser", "\u00A3");
// assertTrue(poundSignUTF8.endsWith("password=%C2%A3"));
// String poundSignISO =
// BugzillaClient.addCredentials(IBugzillaTestConstants.TEST_BUGZILLA_222_URL,
// "ISO-8859-1", "testUser", "\u00A3");
// assertFalse(poundSignISO.contains("%C2%A3"));
// assertTrue(poundSignISO.endsWith("password=%A3"));
// }
