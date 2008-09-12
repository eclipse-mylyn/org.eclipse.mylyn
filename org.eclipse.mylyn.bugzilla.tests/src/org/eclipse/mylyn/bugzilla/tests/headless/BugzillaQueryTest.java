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

import junit.framework.TestCase;

import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaLanguageSettings;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;

/**
 * Example use of headless API (no ui dependencies)
 * 
 * @author Rob Elves
 * @author Nathan Hapke
 */
public class BugzillaQueryTest extends TestCase {

	private TaskRepository repository;

	private AbstractRepositoryConnector connectorOriginal;

	private BugzillaRepositoryConnector connector;

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
		repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		Credentials credentials = TestUtil.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
	}

	@Override
	protected void tearDown() throws Exception {

	}

	/**
	 * This is the first test so that the repository credentials are correctly set for the other tests
	 */
	public void testAddCredentials() {
		if (!repository.hasCredentials()) {
			Credentials credentials = TestUtil.readCredentials();
			repository.setAuthenticationCredentials(credentials.username, credentials.password);

			assertTrue(repository.hasCredentials());
		}
	}

	// XXX: refactor 3.0
//	public void testGetBug() throws Exception {
//		TaskData taskData = handler.getTaskData(repository, "1", new NullProgressMonitor());
//		assertNotNull(taskData);
//		assertEquals("user@mylar.eclipse.org", taskData.getAssignedTo());
//		assertEquals("foo", taskData.getDescription());
//
//		// You can use the getAttributeValue to pull up the information on any
//		// part of the bug
//		assertEquals("P1", taskData.getAttributeValue(BugzillaReportElement.PRIORITY.getKey()));
//	}
//	

	// TODO: Uncomment when bug#176513 completed
//	public void testGetBugs() throws Exception {
//		HashSet<String> taskIds = new HashSet<String>();
//		taskIds.add("1");
//		taskIds.add("2");
//		taskIds.add("4");
//		Map<String, RepositoryTaskData> taskDataMap = handler.getTaskData(repository, taskIds);
//		assertNotNull(taskDataMap);
//		RepositoryTaskData taskData = taskDataMap.get("1");
//		assertEquals("user@mylar.eclipse.org", taskData.getAssignedTo());
//		assertEquals("foo", taskData.getDescription());
//		// You can use the getAttributeValue to pull up the information on any
//		// part of the bug
//		assertEquals("P1", taskData.getAttributeValue(BugzillaReportElement.PRIORITY.getKeyString()));
//
//		taskData = taskDataMap.get("2");
//		assertEquals("nhapke@cs.ubc.ca", taskData.getAssignedTo());
//		assertEquals("search-match-test 1", taskData.getDescription());
//
//		taskData = taskDataMap.get("4");
//		assertEquals("relves@cs.ubc.ca", taskData.getReporter());
//		assertEquals("Test", taskData.getDescription());
//	}

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

	// XXX: refactpr
//	@SuppressWarnings("deprecation")
//	public void testQueryViaConnector() throws Exception {
//		String queryUrlString = repository.getRepositoryUrl()
//				+ "/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";
//
//		// holds onto actual hit objects
//		ITaskList taskList = new TaskList();
//		QueryHitCollector collector = new QueryHitCollector(new TaskFactory(repository));
//		BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();
//		connector.init(taskList);
//		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(repository.getRepositoryUrl(), queryUrlString,
//				"summary");
//		connector.performQuery(repository, query, collector, null, new NullProgressMonitor());
//		assertEquals(2, collector.getTasks().size());
//		for (ITask hit : collector.getTasks()) {
//			assertTrue(hit.getSummary().contains("search-match-test"));
//		}
//	}
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
// BugzillaClient.addCredentials(IBugzillaConstants.TEST_BUGZILLA_222_URL,
// "UTF-8",
// "testUser", "\u00A3");
// assertTrue(poundSignUTF8.endsWith("password=%C2%A3"));
// String poundSignISO =
// BugzillaClient.addCredentials(IBugzillaConstants.TEST_BUGZILLA_222_URL,
// "ISO-8859-1", "testUser", "\u00A3");
// assertFalse(poundSignISO.contains("%C2%A3"));
// assertTrue(poundSignISO.endsWith("password=%A3"));
// }
