/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaXmlRpcClientTest;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaHyperlinkDetectorTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaRepositorySettingsPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaSearchPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaTaskHyperlinkDetectorTest;
import org.eclipse.mylyn.bugzilla.tests.ui.TaskEditorTest;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.tests.util.TestUtil;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Frank Becker
 */
public class AllBugzillaTests {

	public static Test suite() {
		TestSuite suite = new ManagedTestSuite(AllBugzillaTests.class.getName());
		addTests(TestUtil.runHeartbeatTestsOnly(), suite);
		return suite;
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new TestSuite(AllBugzillaTests.class.getName());
		addTests(defaultOnly, suite);
		return suite;
	}

	private static void addTests(boolean defaultOnly, TestSuite suite) {
		// Standalone tests (Don't require an instance of Eclipse)
		suite.addTest(AllBugzillaHeadlessStandaloneTests.suite(defaultOnly));

		// Tests that only need to run once (i.e. no network io so doesn't matter which repository)
		suite.addTestSuite(TaskEditorTest.class);
		suite.addTestSuite(BugzillaRepositorySettingsPageTest.class);
		suite.addTestSuite(BugzillaSearchPageTest.class);
		suite.addTestSuite(BugzillaDateTimeTests.class);
		suite.addTestSuite(BugzillaTaskHyperlinkDetectorTest.class);
		suite.addTestSuite(BugzillaHyperlinkDetectorTest.class);

		// Each of these tests gets executed against every repo in BugzillaFixture.ALL
		// unless otherwise excluded
		if (defaultOnly) {
			addTests(suite, BugzillaFixture.DEFAULT);
		} else {
			for (BugzillaFixture fixture : BugzillaFixture.ALL) {
				addTests(suite, fixture);
			}
		}
	}

	private static void addTests(TestSuite suite, BugzillaFixture fixture) {
		if (fixture.isExcluded()) {
			return;
		}

		fixture.createSuite(suite);
		fixture.add(RepositoryReportFactoryTest.class);
		fixture.add(BugzillaTaskDataHandlerTest.class);
		fixture.add(BugzillaSearchTest.class);
		fixture.add(EncodingTest.class);
		fixture.add(BugzillaXmlRpcClientTest.class);
		fixture.add(BugzillaRepositoryConnectorTest.class);
		fixture.add(BugzillaAttachmentHandlerTest.class);

		// Move any tests here that are resulting in spurious failures
		// due to recent changes in Bugzilla Server head.
		if (fixture != BugzillaFixture.BUGS_HEAD) {
		}

		// Only run this if we have custom status and Workflow
		if (fixture.equals(BugzillaFixture.BUGS_3_6_CUSTOM_WF_AND_STATUS)) {
			fixture.add(BugzillaCustomRepositoryTest.class);
		}

		fixture.done();
	}

}
