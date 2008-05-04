/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class RepositoryReportFactoryTest extends TestCase {

	BugzillaAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	TaskRepository repository;

	BugzillaRepositoryConnector connector;

	private RepositoryTaskData init(String taskId) throws CoreException {
		AbstractTaskDataHandler handler = connector.getLegacyTaskDataHandler();
		RepositoryTaskData taskData = handler.getTaskData(repository, taskId, new NullProgressMonitor());
		return taskData;
	}

	private TaskRepository setRepository(String kind, String url) {
		connector = (BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				BugzillaCorePlugin.REPOSITORY_KIND);
		repository = new TaskRepository(kind, url);
		Credentials credentials = TestUtil.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		return repository;
	}

	public void testInvalidCredentials222() throws Exception {
		String bugid = "1";
		String errorMessage = "";
		try {
			setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
			repository.setAuthenticationCredentials("invalid", "invalid");
			init(bugid);
		} catch (CoreException e) {
			errorMessage = e.getStatus().getMessage();
		}
		assertTrue(errorMessage.startsWith("Unable to login"));
		repository.flushAuthenticationCredentials();
	}

	public void testBugNotFound222() {

		String bugid = "-1";
		String errorMessage = "";
		try {
			setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
			// If this isn't called the BugzillaClient will be reused (with old
			// credentials) so
			// force drop of old client connection.
			// Note that this is usually called by notification
			connector.getClientManager().repositoryAdded(repository);
			init(bugid);
		} catch (CoreException e) {
			errorMessage = e.getStatus().getMessage();
		}
		assertTrue(errorMessage.startsWith("Repository error from"));
	}

	// public void testReadingReport() throws Exception {
	// String bugid = "2";
	// setRepository(BugzillaCorePlugin.REPOSITORY_KIND,
	// IBugzillaConstants.TEST_BUGZILLA_222_URL);
	// RepositoryTaskData report = init(bugid);
	//		
	// assertNotNull(report);
	// assertEquals("search-match-test 1",
	// report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
	// .getValue());
	// assertEquals("TestProduct",
	// report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
	// assertEquals("PC",
	// report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
	// assertEquals("Windows",
	// report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
	// // first comment (#0) is the summary so this value is always 1
	// // greater
	// // than what is shown on the report ui
	// assertEquals(3, report.getComments().size());
	// assertEquals("search-match-test 1",
	// report.getComments().get(0).getAttribute(
	// BugzillaReportElement.THETEXT.getKeyString()).getValue());
	// // assertEquals(15, report.getAttachments().size());
	// // assertEquals("1",
	// //
	// report.getAttachments().get(0).getAttribute(BugzillaReportElement.ATTACHID).getValue());
	// // assertEquals("2006-03-10 14:11",
	// //
	// report.getAttachments().get(0).getAttribute(BugzillaReportElement.DATE)
	// // .getValue());
	// // assertEquals("Testing upload",
	// //
	// report.getAttachments().get(0).getAttribute(BugzillaReportElement.DESC)
	// // .getValue());
	// // assertEquals("patch130217.txt",
	// //
	// report.getAttachments().get(0).getAttribute(BugzillaReportElement.FILENAME)
	// // .getValue());
	// // assertEquals("text/plain",
	// //
	// report.getAttachments().get(0).getAttribute(BugzillaReportElement.TYPE).getValue());
	// }

	public void testReadingReport222() throws Exception {
		String bugid = "2";
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		RepositoryTaskData report = init(bugid);

		assertNotNull(report);
		assertEquals("search-match-test 1", report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
				.getValue());
		assertEquals("search-match-test 1", report.getSummary());
		assertEquals("search-match-test 1", report.getDescription());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
		assertEquals("TestProduct", report.getProduct());
		assertEquals("TestComponent", report.getAttribute(BugzillaReportElement.COMPONENT.getKeyString()).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		assertEquals("Windows", report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.VERSION.getKeyString()).getValue());
		assertEquals("P1", report.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue());
		assertEquals("" + bugid, report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
		assertEquals("NEW", report.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString()).getValue());
		assertEquals("2006-05-23 17:46", report.getAttribute(BugzillaReportElement.CREATION_TS.getKeyString())
				.getValue());
		assertEquals("2008-02-15 12:55:32", report.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
				.getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE.getKeyString()).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER.getKeyString()).getValue());
		assertEquals("nhapke@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString())
				.getValue());
		assertEquals(5, report.getComments().size());
		// assertEquals("relves@cs.ubc.ca",
		// report.getComments().get(0).getAttribute(
		// BugzillaReportElement.WHO.getKeyString()).getValue());
		// assertEquals("2006-05-23 17:46:24",
		// report.getComments().get(0).getAttribute(
		// BugzillaReportElement.BUG_WHEN.getKeyString()).getValue());
		// assertEquals("search-match-test 1",
		// report.getComments().get(0).getAttribute(
		// BugzillaReportElement.THETEXT.getKeyString()).getValue());
		// assertEquals(0, report.getAttachments().size());
	}

	public void testReadingReport2201() throws Exception {
		String bugid = "1";
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_2201_URL);
		RepositoryTaskData report = init(bugid);

		assertNotNull(report);
		assertEquals("1", report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
		assertEquals("search-match-test 1", report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
				.getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
		assertEquals("TestComponent", report.getAttribute(BugzillaReportElement.COMPONENT.getKeyString()).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		assertEquals("Windows", report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.VERSION.getKeyString()).getValue());
		assertEquals("P2", report.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue());
		assertEquals("NEW", report.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString()).getValue());
		assertEquals("2006-03-02 18:13", report.getAttribute(BugzillaReportElement.CREATION_TS.getKeyString())
				.getValue());
		assertEquals("2006-05-03 13:06:11", report.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
				.getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE.getKeyString()).getValue());
		RepositoryTaskAttribute attribute = report.getAttribute(BugzillaReportElement.BLOCKED.getKeyString());
		assertEquals("2, 9", attribute.getValue());
		attribute = report.getAttribute(BugzillaReportElement.CC.getKeyString());
		assertEquals(2, attribute.getValues().size());
		assertEquals("relves@cs.ubc.ca", attribute.getValues().get(0));
		assertEquals("relves@gmail.com", attribute.getValues().get(1));
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER.getKeyString()).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString())
				.getValue());
	}

	// public void testReadingReport2201Eclipse() throws Exception {
	// String bugid = "24448";
	// TaskRepository repository = new
	// TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
	// IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
	//
	// RepositoryTaskData report = new RepositoryTaskData(attributeFactory,
	// BugzillaCorePlugin.REPOSITORY_KIND, repository
	// .getUrl(), bugid);
	// BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(),
	// report);
	// factory.populateReport(report, repository.getUrl(), null,
	// repository.getUserName(), repository.getPassword(),
	// null);
	//
	// assertNotNull(report);
	// assertEquals("24448",
	// report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
	// assertEquals("Ant causing Out of Memory",
	// report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
	// .getValue());
	// assertEquals("Platform",
	// report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
	// assertEquals("Ant",
	// report.getAttribute(BugzillaReportElement.COMPONENT.getKeyString()).getValue());
	// assertEquals("PC",
	// report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
	// assertEquals("other",
	// report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
	// assertEquals("2.0",
	// report.getAttribute(BugzillaReportElement.VERSION.getKeyString()).getValue());
	// assertEquals("P2",
	// report.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
	// assertEquals("normal",
	// report.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue());
	// assertEquals("RESOLVED",
	// report.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString()).getValue());
	// assertEquals("WONTFIX",
	// report.getAttribute(BugzillaReportElement.RESOLUTION.getKeyString()).getValue());
	// assertEquals("2002-10-07 09:32",
	// report.getAttribute(BugzillaReportElement.CREATION_TS.getKeyString())
	// .getValue());
	// assertEquals("2006-02-03 12:03:57",
	// report.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
	// .getValue());
	// assertEquals("core, performance, ui",
	// report.getAttribute(BugzillaReportElement.KEYWORDS.getKeyString())
	// .getValue());
	// // RepositoryTaskAttribute attribute =
	// // report.getAttribute(BugzillaReportElement.CC);
	// // assertEquals(30, attribute.getValues().size());
	// // assertEquals("relves@cs.ubc.ca", attribute.getValues().get(0));
	// // assertEquals("relves@gmail.com", attribute.getValues().get(1));
	// // assertEquals("relves@cs.ubc.ca",
	// // report.getAttribute(BugzillaReportElement.REPORTER).getValue());
	// // assertEquals("relves@cs.ubc.ca",
	// // report.getAttribute(BugzillaReportElement.ASSIGNED_TO).getValue());
	// // assertEquals(1, report.getComments().size());
	// // assertEquals("relves@cs.ubc.ca",
	// //
	// report.getComments().get(0).getAttribute(BugzillaReportElement.WHO).getValue());
	// // assertEquals("2006-03-02 18:13",
	// //
	// report.getComments().get(0).getAttribute(BugzillaReportElement.BUG_WHEN)
	// // .getValue());
	// // assertEquals("search-match-test 1",
	// //
	// report.getComments().get(0).getAttribute(BugzillaReportElement.THETEXT)
	// // .getValue());
	// // assertEquals(0, report.getAttachments().size());
	// }

	// public void testReadingReport220() throws Exception {
	// String bugid = "1";
	// TaskRepository repository = new
	// TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
	// IBugzillaConstants.TEST_BUGZILLA_220_URL);
	//
	// RepositoryTaskData report = new RepositoryTaskData(attributeFactory,
	// BugzillaCorePlugin.REPOSITORY_KIND,
	// repository.getUrl(), bugid);
	// BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(),
	// report);
	// factory.populateReport(report, repository.getUrl(), null,
	// repository.getUserName(), repository.getPassword(),
	// null);
	//
	// assertNotNull(report);
	// assertEquals("1",
	// report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
	// assertEquals("search-match-test",
	// report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
	// .getValue());
	// assertEquals("TestProduct",
	// report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
	// assertEquals("TestComponent",
	// report.getAttribute(BugzillaReportElement.COMPONENT.getKeyString()).getValue());
	// assertEquals("PC",
	// report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
	// assertEquals("Windows",
	// report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
	// assertEquals("other",
	// report.getAttribute(BugzillaReportElement.VERSION.getKeyString()).getValue());
	// assertEquals("P2",
	// report.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
	// assertEquals("normal",
	// report.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue());
	// assertEquals("NEW",
	// report.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString()).getValue());
	// assertEquals("2006-03-02 17:30",
	// report.getAttribute(BugzillaReportElement.CREATION_TS.getKeyString())
	// .getValue());
	// assertEquals("2006-04-20 15:13:43",
	// report.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
	// .getValue());
	// assertEquals("---",
	// report.getAttribute(BugzillaReportElement.TARGET_MILESTONE.getKeyString()).getValue());
	// assertEquals("relves@cs.ubc.ca",
	// report.getAttribute(BugzillaReportElement.REPORTER.getKeyString()).getValue());
	// assertEquals("relves@cs.ubc.ca",
	// report.getAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString())
	// .getValue());
	// assertEquals("relves@cs.ubc.ca",
	// report.getAttribute(BugzillaReportElement.CC.getKeyString()).getValue());
	// assertEquals(3, report.getComments().size());
	// assertEquals("relves@cs.ubc.ca",
	// report.getComments().get(0).getAttribute(
	// BugzillaReportElement.WHO.getKeyString()).getValue());
	// assertEquals("2006-03-02 17:30",
	// report.getComments().get(0).getAttribute(
	// BugzillaReportElement.BUG_WHEN.getKeyString()).getValue());
	// assertEquals("search-match-test",
	// report.getComments().get(0).getAttribute(
	// BugzillaReportElement.THETEXT.getKeyString()).getValue());
	// assertEquals(0, report.getAttachments().size());
	// }

	public void testReadingReport218() throws Exception {
		String bugid = "1";
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_218_URL);
		RepositoryTaskData report = init(bugid);
		assertNotNull(report);
		assertEquals("1", report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
		assertEquals("search-match-test 1", report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
				.getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
		assertEquals("TestComponent", report.getAttribute(BugzillaReportElement.COMPONENT.getKeyString()).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		assertEquals("Windows XP", report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.VERSION.getKeyString()).getValue());
		assertEquals("P2", report.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue());
		assertEquals("NEW", report.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString()).getValue());
		assertEquals("2006-03-02 18:09", report.getAttribute(BugzillaReportElement.CREATION_TS.getKeyString())
				.getValue());
		assertEquals("2006-05-05 17:45:24", report.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
				.getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE.getKeyString()).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER.getKeyString()).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString())
				.getValue());
		assertEquals(0, report.getAttachments().size());
	}

	public void testTimeTracking222() throws Exception {
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		RepositoryTaskData report = init("11");
		assertEquals("7.50", report.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
		assertEquals("4.00", report.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString()).getValue());
		assertEquals("3.00", report.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
		assertEquals("2005-03-04", report.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()).getValue());
	}

	// public void testTimeTracking2201() throws Exception {
	// RepositoryTaskData report =
	// init(IBugzillaConstants.TEST_BUGZILLA_2201_URL, 23);
	//
	// assertEquals("7.50",
	// report.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
	// assertEquals("1.00",
	// report.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString()).getValue());
	// assertEquals("3.00",
	// report.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
	// assertEquals("2005-03-04",
	// report.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()).getValue());
	// }
	//
	// public void testTimeTracking220() throws Exception {
	// RepositoryTaskData report =
	// init(IBugzillaConstants.TEST_BUGZILLA_220_URL, 9);
	//
	// assertEquals("7.50",
	// report.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
	// assertEquals("1.00",
	// report.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString()).getValue());
	// assertEquals("3.00",
	// report.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
	// assertEquals("2005-03-04",
	// report.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()).getValue());
	// }

	public void testTimeTracking218() throws Exception {
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_218_URL);
		RepositoryTaskData report = init("19");
		assertEquals("7.50", report.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
		assertEquals("1.00", report.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString()).getValue());
		assertEquals("3.00", report.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
	}

	public void testMultipleDepensOn() throws Exception {
		String bugid = "5";
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_218_URL);
		RepositoryTaskData report = init(bugid);
		assertNotNull(report);
		assertEquals("5", report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
		assertEquals("6, 7", report.getAttribute(BugzillaReportElement.DEPENDSON.getKeyString()).getValue());
		assertEquals("13, 14", report.getAttribute(BugzillaReportElement.BLOCKED.getKeyString()).getValue());
	}

	public void testBugReportAPI() throws Exception {
		String bugid = "3";
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		RepositoryTaskData report = init(bugid);
		assertNotNull(report);
		assertTrue(report != null);
		RepositoryTaskData bugReport = report;
		assertEquals("search-match-test 2", bugReport.getSummary());
		assertEquals("search-match-test 2", bugReport.getDescription());
		assertEquals("TestProduct", bugReport.getProduct());
		assertEquals("nhapke@cs.ubc.ca", bugReport.getAssignedTo());
	}

	public void testDeltaTsTruncation() {
		String ts1 = "2006-07-06 03:22:08 0900";
		String ts1_truncated = "2006-07-06 03:22:08";
		assertEquals(ts1_truncated, BugzillaClient.stripTimeZone(ts1));

		String ts2 = "2006-07-06 03:22:08";
		String ts2_truncated = "2006-07-06 03:22:08";
		assertEquals(ts2_truncated, BugzillaClient.stripTimeZone(ts2));

		String ts3 = "2006-07-06 03:22:08 PST";
		String ts3_truncated = "2006-07-06 03:22:08";
		assertEquals(ts3_truncated, BugzillaClient.stripTimeZone(ts3));
	}

}
