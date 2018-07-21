/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.List;

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaXmlRpcClientTest;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaHyperlinkDetectorTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaRepositorySettingsPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaSearchPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaTaskEditorTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaTaskHyperlinkDetectorTest;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Frank Becker
 */
public class AllBugzillaTests {

	public static Test suite() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}

		TestSuite suite = new ManagedTestSuite(AllBugzillaTests.class.getName());
		TestConfiguration testConfiguration = ManagedSuite.getTestConfigurationOrCreateDefault();
		addTests(suite, testConfiguration);
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllBugzillaTests.class.getName());
		addTests(suite, configuration);
		return suite;
	}

	public static void addTests(TestSuite suite, TestConfiguration configuration) {
		// Standalone tests (Don't require an instance of Eclipse)
		suite.addTest(AllBugzillaHeadlessStandaloneTests.suite(configuration));

		// Tests that only need to run once (i.e. no network io so doesn't matter which repository)
		suite.addTestSuite(BugzillaTaskHyperlinkDetectorTest.class);
		suite.addTestSuite(BugzillaHyperlinkDetectorTest.class);

		// network tests
		if (!configuration.isLocalOnly()) {
			suite.addTestSuite(BugzillaTaskEditorTest.class);
			suite.addTestSuite(BugzillaSearchPageTest.class);
			suite.addTestSuite(BugzillaRepositorySettingsPageTest.class);
			List<BugzillaFixture> fixtures = configuration.discover(BugzillaFixture.class, "bugzilla");
			for (BugzillaFixture fixture : fixtures) {
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

		fixture.done();
	}

}
