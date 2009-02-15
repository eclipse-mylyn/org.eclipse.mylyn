/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Frank Becker
 * @author Rob Elves
 */
public class BugzillaTaskDataHandlerTest extends TestCase {

	TaskRepository repository;

	BugzillaRepositoryConnector connector;

	private TaskData init(String taskId) throws CoreException {
		return connector.getTaskData(repository, taskId, new NullProgressMonitor());
	}

	private TaskRepository setRepository(String kind, String url) {
		connector = (BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				BugzillaCorePlugin.CONNECTOR_KIND);
		repository = new TaskRepository(kind, url);
		Credentials credentials = TestUtil.readCredentials();
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), true);
		return repository;
	}

	private void testAttributesFromCloneBug(TaskData repositoryTaskData, boolean valueFromBug9) {
		assertEquals("Clone Bug 1", repositoryTaskData.getRoot()
				.getAttribute(BugzillaAttribute.SHORT_DESC.getKey())
				.getValue());
		assertEquals("This Bug is used to test the cloneTaskData", repositoryTaskData.getRoot().getAttribute(
				BugzillaAttribute.LONG_DESC.getKey()).getValue());
		assertEquals("TestProduct", repositoryTaskData.getRoot()
				.getAttribute(BugzillaAttribute.PRODUCT.getKey())
				.getValue());
		assertEquals("TestComponent", repositoryTaskData.getRoot()
				.getAttribute(BugzillaAttribute.COMPONENT.getKey())
				.getValue());
		assertEquals("PC", repositoryTaskData.getRoot()
				.getAttribute(BugzillaAttribute.REP_PLATFORM.getKey())
				.getValue());
		assertEquals("Windows", repositoryTaskData.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		assertEquals("unspecified", repositoryTaskData.getRoot()
				.getAttribute(BugzillaAttribute.VERSION.getKey())
				.getValue());
		assertEquals("P3", repositoryTaskData.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue());
		assertEquals("enhancement", repositoryTaskData.getRoot()
				.getAttribute(BugzillaAttribute.BUG_SEVERITY.getKey())
				.getValue());
		// "---" is not contained in the repository configuration therefore not added as a valid target_milestone
		// it is however returned in the bug xml.
//		assertEquals("---", repositoryTaskData.getRoot()
//				.getAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey())
//				.getValue());
		assertEquals("Unclassified", repositoryTaskData.getRoot().getAttribute(
				BugzillaAttribute.CLASSIFICATION.getKey()).getValue());
		if (valueFromBug9) {
			assertEquals("9", repositoryTaskData.getRoot().getAttribute(BugzillaAttribute.BUG_ID.getKey()).getValue());
			assertEquals("1.00", repositoryTaskData.getRoot()
					.getAttribute(BugzillaAttribute.REMAINING_TIME.getKey())
					.getValue());
			assertEquals("1.00", repositoryTaskData.getRoot()
					.getAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey())
					.getValue());
			assertEquals("2007-12-12", repositoryTaskData.getRoot()
					.getAttribute(BugzillaAttribute.DEADLINE.getKey())
					.getValue());
			assertEquals("NEW", repositoryTaskData.getRoot()
					.getAttribute(BugzillaAttribute.BUG_STATUS.getKey())
					.getValue());
			assertEquals("2007-11-14 15:12", repositoryTaskData.getRoot().getAttribute(
					BugzillaAttribute.CREATION_TS.getKey()).getValue());
			assertEquals("2007-11-14 15:14:46", repositoryTaskData.getRoot().getAttribute(
					BugzillaAttribute.DELTA_TS.getKey()).getValue());
			assertEquals("tests@mylyn.eclipse.org", repositoryTaskData.getRoot().getAttribute(
					BugzillaAttribute.REPORTER.getKey()).getValue());
			assertEquals("tests2@mylyn.eclipse.org", repositoryTaskData.getRoot().getAttribute(
					BugzillaAttribute.ASSIGNED_TO.getKey()).getValue());
		} else {
			assertEquals("2.00", repositoryTaskData.getRoot()
					.getAttribute(BugzillaAttribute.REMAINING_TIME.getKey())
					.getValue());
			assertEquals("2.00", repositoryTaskData.getRoot()
					.getAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey())
					.getValue());
			assertEquals("2008-01-01", repositoryTaskData.getRoot()
					.getAttribute(BugzillaAttribute.DEADLINE.getKey())
					.getValue());
			assertEquals("2007-11-14 15:30", repositoryTaskData.getRoot().getAttribute(
					BugzillaAttribute.CREATION_TS.getKey()).getValue());
			assertEquals("2007-11-14 15:30:38", repositoryTaskData.getRoot().getAttribute(
					BugzillaAttribute.DELTA_TS.getKey()).getValue());
			assertEquals("tests2@mylyn.eclipse.org", repositoryTaskData.getRoot().getAttribute(
					BugzillaAttribute.REPORTER.getKey()).getValue());
			assertEquals("tests@mylyn.eclipse.org", repositoryTaskData.getRoot().getAttribute(
					BugzillaAttribute.ASSIGNED_TO.getKey()).getValue());
		}
	}

	public void testCloneTaskData() throws Exception {
		String bugid = "9";
		setRepository(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaTestConstants.TEST_BUGZILLA_30_URL);
		TaskData report1 = init(bugid);

		assertNotNull(report1);

		testAttributesFromCloneBug(report1, true);

		bugid = "10";
		setRepository(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaTestConstants.TEST_BUGZILLA_30_URL);
		TaskData report2 = init(bugid);

		assertNotNull(report2);
		assertEquals("" + bugid, report2.getRoot().getAttribute(BugzillaAttribute.BUG_ID.getKey()).getValue());
		assertEquals("2.00", report2.getRoot().getAttribute(BugzillaAttribute.REMAINING_TIME.getKey()).getValue());
		assertEquals("2.00", report2.getRoot().getAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey()).getValue());
		assertEquals("2008-01-01", report2.getRoot().getAttribute(BugzillaAttribute.DEADLINE.getKey()).getValue());

		assertEquals("Clone Bug 2", report2.getRoot().getAttribute(BugzillaAttribute.SHORT_DESC.getKey()).getValue());
		assertEquals("other Bug for cloneTaskData", report2.getRoot()
				.getAttribute(BugzillaAttribute.LONG_DESC.getKey())
				.getValue());
		assertEquals("TestProduct", report2.getRoot().getAttribute(BugzillaAttribute.PRODUCT.getKey()).getValue());
		assertEquals("TestComponent", report2.getRoot().getAttribute(BugzillaAttribute.COMPONENT.getKey()).getValue());
		assertEquals("PC", report2.getRoot().getAttribute(BugzillaAttribute.REP_PLATFORM.getKey()).getValue());
		assertEquals("Mac OS", report2.getRoot().getAttribute(BugzillaAttribute.OP_SYS.getKey()).getValue());
		assertEquals("unspecified", report2.getRoot().getAttribute(BugzillaAttribute.VERSION.getKey()).getValue());
		assertEquals("P2", report2.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue());
		assertEquals("critical", report2.getRoot().getAttribute(BugzillaAttribute.BUG_SEVERITY.getKey()).getValue());
		assertEquals("ASSIGNED", report2.getRoot().getAttribute(BugzillaAttribute.BUG_STATUS.getKey()).getValue());
		assertEquals("2007-11-14 15:30", report2.getRoot()
				.getAttribute(BugzillaAttribute.CREATION_TS.getKey())
				.getValue());
		assertEquals("2008-04-08 20:03:35", report2.getRoot()
				.getAttribute(BugzillaAttribute.DELTA_TS.getKey())
				.getValue());
		// Same discrepancy as in above test related to "---" target milestone not being in repository coonfiguration
//		assertEquals("---", report2.getRoot().getAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey()).getValue());
		assertEquals("tests2@mylyn.eclipse.org", report2.getRoot()
				.getAttribute(BugzillaAttribute.REPORTER.getKey())
				.getValue());
		assertEquals("tests@mylyn.eclipse.org", report2.getRoot()
				.getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey())
				.getValue());
	}

	public void testCharacterEscaping() throws CoreException {
		String bugid = "17";
		setRepository(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaTestConstants.TEST_BUGZILLA_30_URL);
		TaskData report1 = init(bugid);
		assertEquals("Testing! \"&@ $\" &amp;", report1.getRoot()
				.getAttribute(BugzillaAttribute.SHORT_DESC.getKey())
				.getValue());
	}

}
