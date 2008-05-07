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
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Frank Becker
 */
public class BugzillaTaskDataHandlerTest extends TestCase {

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

	private void testAttributesFromCloneBug(RepositoryTaskData repositoryTaskData, boolean valueFromBug9) {
		assertEquals("Clone Bug 1", repositoryTaskData.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
				.getValue());
		assertEquals("Clone Bug 1", repositoryTaskData.getSummary());
		assertEquals("This Bug is used to test the cloneTaskData", repositoryTaskData.getDescription());
		assertEquals("TestProduct", repositoryTaskData.getAttribute(BugzillaReportElement.PRODUCT.getKeyString())
				.getValue());
		assertEquals("TestProduct", repositoryTaskData.getProduct());
		assertEquals("TestComponent", repositoryTaskData.getAttribute(BugzillaReportElement.COMPONENT.getKeyString())
				.getValue());
		assertEquals("PC", repositoryTaskData.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString())
				.getValue());
		assertEquals("Windows", repositoryTaskData.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		assertEquals("unspecified", repositoryTaskData.getAttribute(BugzillaReportElement.VERSION.getKeyString())
				.getValue());
		assertEquals("P3", repositoryTaskData.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
		assertEquals("enhancement", repositoryTaskData.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString())
				.getValue());
		assertEquals("---", repositoryTaskData.getAttribute(BugzillaReportElement.TARGET_MILESTONE.getKeyString())
				.getValue());
		assertEquals("Unclassified", repositoryTaskData.getAttribute(
				BugzillaReportElement.CLASSIFICATION.getKeyString()).getValue());
		if (valueFromBug9) {
			assertEquals("9", repositoryTaskData.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
			assertEquals("1.00", repositoryTaskData.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString())
					.getValue());
			assertEquals("1.00", repositoryTaskData.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString())
					.getValue());
			assertEquals("2007-12-12", repositoryTaskData.getAttribute(BugzillaReportElement.DEADLINE.getKeyString())
					.getValue());
			assertEquals("NEW", repositoryTaskData.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString())
					.getValue());
			assertEquals("2007-11-14 15:12", repositoryTaskData.getAttribute(
					BugzillaReportElement.CREATION_TS.getKeyString()).getValue());
			assertEquals("2007-11-14 15:14:46", repositoryTaskData.getAttribute(
					BugzillaReportElement.DELTA_TS.getKeyString()).getValue());
			assertEquals("tests@mylyn.eclipse.org", repositoryTaskData.getAttribute(
					BugzillaReportElement.REPORTER.getKeyString()).getValue());
			assertEquals("tests2@mylyn.eclipse.org", repositoryTaskData.getAttribute(
					BugzillaReportElement.ASSIGNED_TO.getKeyString()).getValue());
		} else {
			assertEquals("2.00", repositoryTaskData.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString())
					.getValue());
			assertEquals("2.00", repositoryTaskData.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString())
					.getValue());
			assertEquals("2008-01-01", repositoryTaskData.getAttribute(BugzillaReportElement.DEADLINE.getKeyString())
					.getValue());
			assertEquals("2007-11-14 15:30", repositoryTaskData.getAttribute(
					BugzillaReportElement.CREATION_TS.getKeyString()).getValue());
			assertEquals("2007-11-14 15:30:38", repositoryTaskData.getAttribute(
					BugzillaReportElement.DELTA_TS.getKeyString()).getValue());
			assertEquals("tests2@mylyn.eclipse.org", repositoryTaskData.getAttribute(
					BugzillaReportElement.REPORTER.getKeyString()).getValue());
			assertEquals("tests@mylyn.eclipse.org", repositoryTaskData.getAttribute(
					BugzillaReportElement.ASSIGNED_TO.getKeyString()).getValue());
		}
	}

	public void testCloneTaskData() throws Exception {
		String bugid = "9";
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_30_URL);
		RepositoryTaskData report1 = init(bugid);

		assertNotNull(report1);

		testAttributesFromCloneBug(report1, true);

		bugid = "10";
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_30_URL);
		RepositoryTaskData report2 = init(bugid);

		assertNotNull(report2);
		assertEquals("" + bugid, report2.getAttribute(BugzillaReportElement.BUG_ID.getKeyString()).getValue());
		assertEquals("2.00", report2.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString()).getValue());
		assertEquals("2.00", report2.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()).getValue());
		assertEquals("2008-01-01", report2.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()).getValue());

		assertEquals("Clone Bug 2", report2.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString()).getValue());
		assertEquals("Clone Bug 2", report2.getSummary());
		assertEquals("other Bug for cloneTaskData", report2.getDescription());
		assertEquals("TestProduct", report2.getAttribute(BugzillaReportElement.PRODUCT.getKeyString()).getValue());
		assertEquals("TestProduct", report2.getProduct());
		assertEquals("TestComponent", report2.getAttribute(BugzillaReportElement.COMPONENT.getKeyString()).getValue());
		assertEquals("PC", report2.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		assertEquals("Mac OS", report2.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		assertEquals("unspecified", report2.getAttribute(BugzillaReportElement.VERSION.getKeyString()).getValue());
		assertEquals("P2", report2.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue());
		assertEquals("critical", report2.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue());
		assertEquals("ASSIGNED", report2.getAttribute(BugzillaReportElement.BUG_STATUS.getKeyString()).getValue());
		assertEquals("2007-11-14 15:30", report2.getAttribute(BugzillaReportElement.CREATION_TS.getKeyString())
				.getValue());
		assertEquals("2007-11-14 15:30:38", report2.getAttribute(BugzillaReportElement.DELTA_TS.getKeyString())
				.getValue());
		assertEquals("---", report2.getAttribute(BugzillaReportElement.TARGET_MILESTONE.getKeyString()).getValue());
		assertEquals("tests2@mylyn.eclipse.org", report2.getAttribute(BugzillaReportElement.REPORTER.getKeyString())
				.getValue());
		assertEquals("tests@mylyn.eclipse.org", report2.getAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString())
				.getValue());

		AbstractTaskDataHandler handler = connector.getLegacyTaskDataHandler();
		handler.cloneTaskData(report1, report2);
		testAttributesFromCloneBug(report2, false);

	}

	public void testCharacterEscaping() throws CoreException {
		String bugid = "17";
		setRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_30_URL);
		RepositoryTaskData report1 = init(bugid);
		assertEquals("Testing! \"&@ $\" &amp;", report1.getSummary());
	}

}
