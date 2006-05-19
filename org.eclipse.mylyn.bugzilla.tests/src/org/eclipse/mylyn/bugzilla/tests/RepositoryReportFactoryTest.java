/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.tests;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryReportFactory;
import org.eclipse.mylar.provisional.bugzilla.core.AbstractRepositoryReportAttribute;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

public class RepositoryReportFactoryTest extends TestCase {

	RepositoryReportFactory factory = RepositoryReportFactory.getInstance();

	public void testBugNoFound222() throws Exception {
		int bugid = -1;
		String errorMessage = "";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		try {
			BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
			factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);
		} catch (LoginException e) {
			//
		} catch (IOException e) {
			errorMessage = e.getMessage();
		}
		assertEquals(IBugzillaConstants.ERROR_INVALID_BUG_ID, errorMessage);
	}

	public void testInvalidCredentials222() throws Exception {
		int bugid = 1;
		String errorMessage = "";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		repository.setAuthenticationCredentials("invalid", "invalid");
		try {
			BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
			factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);
		} catch (LoginException e) {
			errorMessage = e.getMessage();
		} catch (IOException e) {
			errorMessage = e.getMessage();
		}
		assertEquals(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD, errorMessage);
		repository.flushAuthenticationCredentials();
	}

	public void testReadingReport() throws Exception {
		int bugid = 4;
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);

		BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
		BugzillaRepositoryUtil.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);

		assertNotNull(report);
		assertEquals("Another Test", report.getAttribute(BugzillaReportElement.SHORT_DESC).getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		assertEquals("Other", report.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		// first comment (#0) is the description so this value is always 1 greater
		// than what is shown on the report ui
		assertEquals(38, report.getComments().size());
		assertEquals("Testing new 2.22 version capability", report.getComments().get(0).getAttribute(
				BugzillaReportElement.THETEXT).getValue());
		assertEquals(15, report.getAttachments().size());
		assertEquals("1", report.getAttachments().get(0).getAttribute(BugzillaReportElement.ATTACHID).getValue());
		assertEquals("2006-03-10 14:11", report.getAttachments().get(0).getAttribute(BugzillaReportElement.DATE)
				.getValue());
		assertEquals("Testing upload", report.getAttachments().get(0).getAttribute(BugzillaReportElement.DESC)
				.getValue());
		assertEquals("patch130217.txt", report.getAttachments().get(0).getAttribute(BugzillaReportElement.FILENAME)
				.getValue());
		assertEquals("text/plain", report.getAttachments().get(0).getAttribute(BugzillaReportElement.TYPE).getValue());
	}

	public void testReadingReport222() throws Exception {
		int bugid = 1;
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);

		BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
		BugzillaRepositoryUtil.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);

		assertNotNull(report);
		assertEquals("search-match-test 1", report.getAttribute(BugzillaReportElement.SHORT_DESC).getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT).getValue());
		assertEquals("TestComponent", report.getAttribute(BugzillaReportElement.COMPONENT).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		assertEquals("Windows", report.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.VERSION).getValue());
		assertEquals("P1", report.getAttribute(BugzillaReportElement.PRIORITY).getValue());
		assertEquals("blocker", report.getAttribute(BugzillaReportElement.BUG_SEVERITY).getValue());
		assertEquals("1", report.getAttribute(BugzillaReportElement.BUG_ID).getValue());
		assertEquals("NEW", report.getAttribute(BugzillaReportElement.BUG_STATUS).getValue());
		assertEquals("2006-03-08 19:59", report.getAttribute(BugzillaReportElement.CREATION_TS).getValue());
		assertEquals("2006-03-08 19:59:15", report.getAttribute(BugzillaReportElement.DELTA_TS).getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO).getValue());
		assertEquals(1, report.getComments().size());
		assertEquals("relves@cs.ubc.ca", report.getComments().get(0).getAttribute(BugzillaReportElement.WHO).getValue());
		assertEquals("2006-03-08 19:59:15", report.getComments().get(0).getAttribute(BugzillaReportElement.BUG_WHEN)
				.getValue());
		assertEquals("search-match-test 1", report.getComments().get(0).getAttribute(BugzillaReportElement.THETEXT)
				.getValue());
		assertEquals(0, report.getAttachments().size());
	}

	public void testReadingReport2201() throws Exception {
		int bugid = 1;
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_2201_URL);

		BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
		BugzillaRepositoryUtil.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);

		assertNotNull(report);
		assertEquals("1", report.getAttribute(BugzillaReportElement.BUG_ID).getValue());
		assertEquals("search-match-test 1", report.getAttribute(BugzillaReportElement.SHORT_DESC).getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT).getValue());
		assertEquals("TestComponent", report.getAttribute(BugzillaReportElement.COMPONENT).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		assertEquals("Windows", report.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.VERSION).getValue());
		assertEquals("P2", report.getAttribute(BugzillaReportElement.PRIORITY).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY).getValue());
		assertEquals("NEW", report.getAttribute(BugzillaReportElement.BUG_STATUS).getValue());
		assertEquals("2006-03-02 18:13", report.getAttribute(BugzillaReportElement.CREATION_TS).getValue());
		assertEquals("2006-05-03 13:06:11", report.getAttribute(BugzillaReportElement.DELTA_TS).getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE).getValue());
		AbstractRepositoryReportAttribute attribute = report.getAttribute(BugzillaReportElement.BLOCKED);
		assertEquals(2, attribute.getValues().size());
		assertEquals("2", attribute.getValues().get(0));
		assertEquals("9", attribute.getValues().get(1));
		attribute = report.getAttribute(BugzillaReportElement.CC);
		assertEquals(2, attribute.getValues().size());
		assertEquals("relves@cs.ubc.ca", attribute.getValues().get(0));
		assertEquals("relves@gmail.com", attribute.getValues().get(1));
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO).getValue());
		assertEquals(1, report.getComments().size());
		assertEquals("relves@cs.ubc.ca", report.getComments().get(0).getAttribute(BugzillaReportElement.WHO).getValue());
		assertEquals("2006-03-02 18:13", report.getComments().get(0).getAttribute(BugzillaReportElement.BUG_WHEN)
				.getValue());
		assertEquals("search-match-test 1", report.getComments().get(0).getAttribute(BugzillaReportElement.THETEXT)
				.getValue());
		assertEquals(0, report.getAttachments().size());
	}

	public void testReadingReport2201Eclipse() throws Exception {
		int bugid = 24448;
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
		BugzillaRepositoryUtil.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);

		assertNotNull(report);
		assertEquals("24448", report.getAttribute(BugzillaReportElement.BUG_ID).getValue());
		assertEquals("Ant causing Out of Memory", report.getAttribute(BugzillaReportElement.SHORT_DESC).getValue());
		assertEquals("Platform", report.getAttribute(BugzillaReportElement.PRODUCT).getValue());
		assertEquals("Ant", report.getAttribute(BugzillaReportElement.COMPONENT).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		assertEquals("2.0", report.getAttribute(BugzillaReportElement.VERSION).getValue());
		assertEquals("P2", report.getAttribute(BugzillaReportElement.PRIORITY).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY).getValue());
		assertEquals("RESOLVED", report.getAttribute(BugzillaReportElement.BUG_STATUS).getValue());
		assertEquals("WONTFIX", report.getAttribute(BugzillaReportElement.RESOLUTION).getValue());
		assertEquals("2002-10-07 09:32", report.getAttribute(BugzillaReportElement.CREATION_TS).getValue());
		assertEquals("2006-02-03 12:03:57", report.getAttribute(BugzillaReportElement.DELTA_TS).getValue());
		assertEquals("core, performance, ui", report.getAttribute(BugzillaReportElement.KEYWORDS).getValue());
		// AbstractRepositoryReportAttribute attribute =
		// report.getAttribute(BugzillaReportElement.CC);
		// assertEquals(30, attribute.getValues().size());
		// assertEquals("relves@cs.ubc.ca", attribute.getValues().get(0));
		// assertEquals("relves@gmail.com", attribute.getValues().get(1));
		// assertEquals("relves@cs.ubc.ca",
		// report.getAttribute(BugzillaReportElement.REPORTER).getValue());
		// assertEquals("relves@cs.ubc.ca",
		// report.getAttribute(BugzillaReportElement.ASSIGNED_TO).getValue());
		// assertEquals(1, report.getComments().size());
		// assertEquals("relves@cs.ubc.ca",
		// report.getComments().get(0).getAttribute(BugzillaReportElement.WHO).getValue());
		// assertEquals("2006-03-02 18:13",
		// report.getComments().get(0).getAttribute(BugzillaReportElement.BUG_WHEN)
		// .getValue());
		// assertEquals("search-match-test 1",
		// report.getComments().get(0).getAttribute(BugzillaReportElement.THETEXT)
		// .getValue());
		// assertEquals(0, report.getAttachments().size());
	}

	public void testReadingReport220() throws Exception {
		int bugid = 1;
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_220_URL);

		BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
		BugzillaRepositoryUtil.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);

		assertNotNull(report);
		assertEquals("1", report.getAttribute(BugzillaReportElement.BUG_ID).getValue());
		assertEquals("search-match-test", report.getAttribute(BugzillaReportElement.SHORT_DESC).getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT).getValue());
		assertEquals("TestComponent", report.getAttribute(BugzillaReportElement.COMPONENT).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		assertEquals("Windows", report.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.VERSION).getValue());
		assertEquals("P2", report.getAttribute(BugzillaReportElement.PRIORITY).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY).getValue());
		assertEquals("NEW", report.getAttribute(BugzillaReportElement.BUG_STATUS).getValue());
		assertEquals("2006-03-02 17:30", report.getAttribute(BugzillaReportElement.CREATION_TS).getValue());
		assertEquals("2006-04-20 15:13:43", report.getAttribute(BugzillaReportElement.DELTA_TS).getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.CC).getValue());
		assertEquals(3, report.getComments().size());
		assertEquals("relves@cs.ubc.ca", report.getComments().get(0).getAttribute(BugzillaReportElement.WHO).getValue());
		assertEquals("2006-03-02 17:30", report.getComments().get(0).getAttribute(BugzillaReportElement.BUG_WHEN)
				.getValue());
		assertEquals("search-match-test", report.getComments().get(0).getAttribute(BugzillaReportElement.THETEXT)
				.getValue());
		assertEquals(0, report.getAttachments().size());
	}

	public void testReadingReport218() throws Exception {
		int bugid = 1;
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_218_URL);

		BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
		BugzillaRepositoryUtil.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);

		assertNotNull(report);
		assertEquals("1", report.getAttribute(BugzillaReportElement.BUG_ID).getValue());
		assertEquals("search-match-test 1", report.getAttribute(BugzillaReportElement.SHORT_DESC).getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT).getValue());
		assertEquals("TestComponent", report.getAttribute(BugzillaReportElement.COMPONENT).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		assertEquals("Windows XP", report.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.VERSION).getValue());
		assertEquals("P2", report.getAttribute(BugzillaReportElement.PRIORITY).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY).getValue());
		assertEquals("NEW", report.getAttribute(BugzillaReportElement.BUG_STATUS).getValue());
		assertEquals("2006-03-02 18:09", report.getAttribute(BugzillaReportElement.CREATION_TS).getValue());
		assertEquals("2006-05-05 17:45:24", report.getAttribute(BugzillaReportElement.DELTA_TS).getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO).getValue());
		assertEquals(1, report.getComments().size());
		assertEquals("relves@cs.ubc.ca", report.getComments().get(0).getAttribute(BugzillaReportElement.WHO).getValue());
		assertEquals("2006-03-02 18:09", report.getComments().get(0).getAttribute(BugzillaReportElement.BUG_WHEN)
				.getValue());
		assertEquals("search-match-test 1", report.getComments().get(0).getAttribute(BugzillaReportElement.THETEXT)
				.getValue());
		assertEquals(0, report.getAttachments().size());
	}

	public void testBugReportAPI() throws Exception {
		int bugid = 4;
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);

		BugzillaReport report = new BugzillaReport(bugid, repository.getUrl());
		BugzillaRepositoryUtil.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(), null);

		assertNotNull(report);
		assertTrue(report instanceof BugzillaReport);
		BugzillaReport bugReport = (BugzillaReport) report;
		assertEquals("Another Test", bugReport.getSummary());
		assertEquals("Testing new 2.22 version capability", bugReport.getDescription());
		assertEquals("TestProduct", bugReport.getProduct());
		assertEquals("relves@cs.ubc.ca", bugReport.getAssignedTo());
		// assertEquals("Other",
		// report.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		// assertEquals(37, report.getComments().size());
		// assertEquals("Testing new 2.22 version capability",
		// report.getComments().get(0).getAttribute(BugzillaReportElement.THETEXT).getValue());
		// assertEquals(15, report.getAttachments().size());
		// assertEquals("1",
		// report.getAttachments().get(0).getAttribute(BugzillaReportElement.ATTACHID).getValue());
	}
}
