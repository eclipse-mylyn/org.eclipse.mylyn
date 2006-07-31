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

import org.eclipse.mylar.core.core.tests.support.MylarTestUtils;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryReportFactory;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class RepositoryReportFactoryTest extends TestCase {

	RepositoryReportFactory factory = new RepositoryReportFactory();

	BugzillaAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	private RepositoryTaskData init(String URL, int bugid) throws Exception {
		TaskRepository repository = getRepository(BugzillaPlugin.REPOSITORY_KIND, URL);

		RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND, repository
				.getUrl(), ""+bugid);
		BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(),
				null);

		return report;
	}

	private TaskRepository getRepository(String kind, String url) {
		TaskRepository repository = new TaskRepository(kind, url);
		Credentials credentials = MylarTestUtils.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		return repository;
	}

	public void testBugNotFound222() throws Exception {
		String bugid = "-1";
		String errorMessage = "";
		TaskRepository repository = getRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		try {
			RepositoryTaskData report = new RepositoryTaskData(attributeFactory, repository.getKind(), repository
					.getUrl(), bugid);
			factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository
					.getPassword(), null);
		} catch (LoginException e) {
			//
		} catch (IOException e) {
			errorMessage = e.getMessage();
		}
		assertEquals(IBugzillaConstants.ERROR_INVALID_BUG_ID, errorMessage);
	}

	public void testInvalidCredentials222() throws Exception {
		String bugid = "1";
		String errorMessage = "";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		repository.setAuthenticationCredentials("invalid", "invalid");
		try {
			RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND,
					repository.getUrl(), bugid);
			factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository
					.getPassword(), null);
		} catch (LoginException e) {
			errorMessage = e.getMessage();
		} catch (IOException e) {
			errorMessage = e.getMessage();
		}
		assertEquals(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD, errorMessage);
		repository.flushAuthenticationCredentials();
	}

	public void testReadingReport() throws Exception {
		String bugid = "2";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);

		RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND, repository
				.getUrl(), bugid);
		BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(),
				null);

		assertNotNull(report);
		assertEquals("search-match-test 1", report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
				.getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		assertEquals("Windows", report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		// first comment (#0) is the description so this value is always 1
		// greater
		// than what is shown on the report ui
		assertEquals(3, report.getComments().size());
		assertEquals("search-match-test 1", report.getComments().get(0).getAttribute(
				BugzillaReportElement.THETEXT.getKeyString()).getValue());
		// assertEquals(15, report.getAttachments().size());
		// assertEquals("1",
		// report.getAttachments().get(0).getAttribute(BugzillaReportElement.ATTACHID).getValue());
		// assertEquals("2006-03-10 14:11",
		// report.getAttachments().get(0).getAttribute(BugzillaReportElement.DATE)
		// .getValue());
		// assertEquals("Testing upload",
		// report.getAttachments().get(0).getAttribute(BugzillaReportElement.DESC)
		// .getValue());
		// assertEquals("patch130217.txt",
		// report.getAttachments().get(0).getAttribute(BugzillaReportElement.FILENAME)
		// .getValue());
		// assertEquals("text/plain",
		// report.getAttachments().get(0).getAttribute(BugzillaReportElement.TYPE).getValue());
	}

	public void testReadingReport222() throws Exception {
		int bugid = 2;
		// TaskRepository repository = new
		// TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
		// IBugzillaConstants.TEST_BUGZILLA_222_URL);
		//
		// RepositoryTaskData report = new RepositoryTaskData(attributeFactory,
		// BugzillaPlugin.REPOSITORY_KIND, repository.getUrl(), bugid);
		// BugzillaRepositoryUtil.setupExistingBugAttributes(repository.getUrl(),
		// report);
		// factory.populateReport(report, repository.getUrl(), null,
		// repository.getUserName(), repository.getPassword(), null);
		RepositoryTaskData report = init(IBugzillaConstants.TEST_BUGZILLA_222_URL, bugid);

		assertNotNull(report);
		assertEquals("search-match-test 1", report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
				.getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
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
		assertEquals("2006-06-15 12:55:43", report.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
				.getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE.getKeyString()).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER.getKeyString()).getValue());
		assertEquals("nhapke@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString())
				.getValue());
		assertEquals(3, report.getComments().size());
		assertEquals("relves@cs.ubc.ca", report.getComments().get(0).getAttribute(
				BugzillaReportElement.WHO.getKeyString()).getValue());
		assertEquals("2006-05-23 17:46:24", report.getComments().get(0).getAttribute(
				BugzillaReportElement.BUG_WHEN.getKeyString()).getValue());
		assertEquals("search-match-test 1", report.getComments().get(0).getAttribute(
				BugzillaReportElement.THETEXT.getKeyString()).getValue());
		assertEquals(0, report.getAttachments().size());
	}

	public void testTimeTracking222() throws Exception {
		RepositoryTaskData report = init(IBugzillaConstants.TEST_BUGZILLA_222_URL, 11);

		assertEquals("7.50", report.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
		assertEquals("4.00", report.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString()).getValue());
		assertEquals("3.00", report.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
		assertEquals("2005-03-04", report.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()).getValue());
	}

	public void testTimeTracking2201() throws Exception {
		RepositoryTaskData report = init(IBugzillaConstants.TEST_BUGZILLA_2201_URL, 23);

		assertEquals("7.50", report.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
		assertEquals("1.00", report.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString()).getValue());
		assertEquals("3.00", report.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
		assertEquals("2005-03-04", report.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()).getValue());
	}

	public void testTimeTracking220() throws Exception {
		RepositoryTaskData report = init(IBugzillaConstants.TEST_BUGZILLA_220_URL, 9);

		assertEquals("7.50", report.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
		assertEquals("1.00", report.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString()).getValue());
		assertEquals("3.00", report.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
		assertEquals("2005-03-04", report.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()).getValue());
	}

	public void testTimeTracking218() throws Exception {
		RepositoryTaskData report = init(IBugzillaConstants.TEST_BUGZILLA_218_URL, 19);

		assertEquals("7.50", report.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
		assertEquals("1.00", report.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString()).getValue());
		assertEquals("3.00", report.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
	}

	public void testReadingReport2201() throws Exception {
		String bugid = "1";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_2201_URL);

		RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND, repository
				.getUrl(), bugid);
		BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(),
				null);

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
		assertEquals(1, report.getComments().size());
		assertEquals("relves@cs.ubc.ca", report.getComments().get(0).getAttribute(
				BugzillaReportElement.WHO.getKeyString()).getValue());
		assertEquals("2006-03-02 18:13", report.getComments().get(0).getAttribute(
				BugzillaReportElement.BUG_WHEN.getKeyString()).getValue());
		assertEquals("search-match-test 1", report.getComments().get(0).getAttribute(
				BugzillaReportElement.THETEXT.getKeyString()).getValue());
		assertEquals(0, report.getAttachments().size());
	}

	public void testReadingReport2201Eclipse() throws Exception {
		String bugid = "24448";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND, repository
				.getUrl(), bugid);
		BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(),
				null);

		assertNotNull(report);
		assertEquals("24448", report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
		assertEquals("Ant causing Out of Memory", report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
				.getValue());
		assertEquals("Platform", report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
		assertEquals("Ant", report.getAttribute(BugzillaReportElement.COMPONENT.getKeyString()).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		assertEquals("2.0", report.getAttribute(BugzillaReportElement.VERSION.getKeyString()).getValue());
		assertEquals("P2", report.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue());
		assertEquals("RESOLVED", report.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString()).getValue());
		assertEquals("WONTFIX", report.getAttribute(BugzillaReportElement.RESOLUTION.getKeyString()).getValue());
		assertEquals("2002-10-07 09:32", report.getAttribute(BugzillaReportElement.CREATION_TS.getKeyString())
				.getValue());
		assertEquals("2006-02-03 12:03:57", report.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
				.getValue());
		assertEquals("core, performance, ui", report.getAttribute(BugzillaReportElement.KEYWORDS.getKeyString())
				.getValue());
		// RepositoryTaskAttribute attribute =
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
		String bugid = "1";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_220_URL);

		RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND, repository
				.getUrl(), bugid);
		BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(),
				null);

		assertNotNull(report);
		assertEquals("1", report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
		assertEquals("search-match-test", report.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
				.getValue());
		assertEquals("TestProduct", report.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
		assertEquals("TestComponent", report.getAttribute(BugzillaReportElement.COMPONENT.getKeyString()).getValue());
		assertEquals("PC", report.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		assertEquals("Windows", report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		assertEquals("other", report.getAttribute(BugzillaReportElement.VERSION.getKeyString()).getValue());
		assertEquals("P2", report.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
		assertEquals("normal", report.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue());
		assertEquals("NEW", report.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString()).getValue());
		assertEquals("2006-03-02 17:30", report.getAttribute(BugzillaReportElement.CREATION_TS.getKeyString())
				.getValue());
		assertEquals("2006-04-20 15:13:43", report.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
				.getValue());
		assertEquals("---", report.getAttribute(BugzillaReportElement.TARGET_MILESTONE.getKeyString()).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.REPORTER.getKeyString()).getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString())
				.getValue());
		assertEquals("relves@cs.ubc.ca", report.getAttribute(BugzillaReportElement.CC.getKeyString()).getValue());
		assertEquals(3, report.getComments().size());
		assertEquals("relves@cs.ubc.ca", report.getComments().get(0).getAttribute(
				BugzillaReportElement.WHO.getKeyString()).getValue());
		assertEquals("2006-03-02 17:30", report.getComments().get(0).getAttribute(
				BugzillaReportElement.BUG_WHEN.getKeyString()).getValue());
		assertEquals("search-match-test", report.getComments().get(0).getAttribute(
				BugzillaReportElement.THETEXT.getKeyString()).getValue());
		assertEquals(0, report.getAttachments().size());
	}

	public void testReadingReport218() throws Exception {
		String bugid = "1";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_218_URL);

		RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND, repository
				.getUrl(), bugid);
		BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(),
				null);

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
		assertEquals(1, report.getComments().size());
		assertEquals("relves@cs.ubc.ca", report.getComments().get(0).getAttribute(
				BugzillaReportElement.WHO.getKeyString()).getValue());
		assertEquals("2006-03-02 18:09", report.getComments().get(0).getAttribute(
				BugzillaReportElement.BUG_WHEN.getKeyString()).getValue());
		assertEquals("search-match-test 1", report.getComments().get(0).getAttribute(
				BugzillaReportElement.THETEXT.getKeyString()).getValue());
		assertEquals(0, report.getAttachments().size());
	}
	
	public void testMultipleDepensOn() throws Exception {
		String bugid = "5";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_218_URL);

		RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND, repository
				.getUrl(), bugid);
		BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(),
				null);

		assertNotNull(report);
		assertEquals("5", report.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
		assertEquals("6, 7", report.getAttribute(BugzillaReportElement.DEPENDSON.getKeyString()).getValue());
		assertEquals("13, 14", report.getAttribute(BugzillaReportElement.BLOCKED.getKeyString()).getValue());
	}


	public void testBugReportAPI() throws Exception {
		String bugid = "3";
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);

		RepositoryTaskData report = new RepositoryTaskData(attributeFactory, BugzillaPlugin.REPOSITORY_KIND, repository
				.getUrl(), bugid);
		BugzillaServerFacade.setupExistingBugAttributes(repository.getUrl(), report);
		factory.populateReport(report, repository.getUrl(), null, repository.getUserName(), repository.getPassword(),
				null);

		assertNotNull(report);
		assertTrue(report instanceof RepositoryTaskData);
		RepositoryTaskData bugReport = (RepositoryTaskData) report;
		assertEquals("search-match-test 2", bugReport.getSummary());
		assertEquals("search-match-test 2", bugReport.getDescription());
		assertEquals("TestProduct", bugReport.getProduct());
		assertEquals("nhapke@cs.ubc.ca", bugReport.getAssignedTo());
		// assertEquals("Other",
		// report.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		// assertEquals(37, report.getComments().size());
		// assertEquals("Testing new 2.22 version capability",
		// report.getComments().get(0).getAttribute(BugzillaReportElement.THETEXT.getKeyString()).getValue());
		// assertEquals(15, report.getAttachments().size());
		// assertEquals("1",
		// report.getAttachments().get(0).getAttribute(BugzillaReportElement.ATTACHID.getKeyString()).getValue());
	}
	
	
	public void testDeltaTsTruncation() {
		String ts1 = "2006-07-06 03:22:08 0900";
		String ts1_truncated = "2006-07-06 03:22:08";
		assertEquals(ts1_truncated, BugzillaReportSubmitForm.stripTimeZone(ts1));
		
		String ts2 = "2006-07-06 03:22:08";
		String ts2_truncated = "2006-07-06 03:22:08";
		assertEquals(ts2_truncated, BugzillaReportSubmitForm.stripTimeZone(ts2));
		
		String ts3 = "2006-07-06 03:22:08 PST";
		String ts3_truncated = "2006-07-06 03:22:08";
		assertEquals(ts3_truncated, BugzillaReportSubmitForm.stripTimeZone(ts3));
	}
	
}
