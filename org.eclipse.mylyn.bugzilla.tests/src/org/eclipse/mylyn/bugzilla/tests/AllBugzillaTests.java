/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaXMLRPCTest;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaRepositorySettingsPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaSearchPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaTaskHyperlinkDetectorTest;
import org.eclipse.mylyn.bugzilla.tests.ui.TaskEditorTest;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;

/**
 * @author Mik Kersten
 */
public class AllBugzillaTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.eclipse.mylyn.bugzilla.tests");

		// Standalone tests (Don't require an instance of Eclipse)
		suite.addTest(AllBugzillaHeadlessStandaloneTests.suite());

		// Tests that only need to run once (i.e. no network io so doesn't matter which repository)
		suite.addTestSuite(TaskEditorTest.class);
		suite.addTestSuite(BugzillaRepositorySettingsPageTest.class);
		suite.addTestSuite(BugzillaSearchPageTest.class);
		suite.addTestSuite(BugzillaDateTimeTests.class);
		suite.addTestSuite(BugzillaTaskHyperlinkDetectorTest.class);

		// Each of these tests gets executed against every repo in BugzillaFixture.ALL
		// unless otherwise excluded
		for (BugzillaFixture fixture : BugzillaFixture.ALL) {
			fixture.createSuite(suite);
			fixture.add(RepositoryReportFactoryTest.class);
			fixture.add(BugzillaTaskDataHandlerTest.class);
			fixture.add(BugzillaSearchTest.class);
			fixture.add(EncodingTest.class);

			// Move any tests here that are resulting in spurious failures
			// due to recent changes in Bugzilla Server head.
			if (fixture != BugzillaFixture.BUGS_HEAD) {
			}

			// Only run these tests on > 3.2 repositories
			if (!fixture.getBugzillaVersion().isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2)) {
				if (fixture != BugzillaFixture.BUGS_HEAD) {
					fixture.add(BugzillaRepositoryConnectorTest.class);
				}
				fixture.add(BugzillaAttachmentHandlerTest.class);
			}
			if (!fixture.getBugzillaVersion().isSmaller(BugzillaVersion.BUGZILLA_3_6)) {
				fixture.add(BugzillaXMLRPCTest.class);
			}

			fixture.done();
		}

		return suite;
	}

}
