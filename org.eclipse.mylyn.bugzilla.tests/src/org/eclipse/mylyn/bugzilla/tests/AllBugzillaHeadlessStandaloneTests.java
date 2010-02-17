/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
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

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaClientTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaCustomFieldsTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaFlagsTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorStandaloneTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaTaskCompletionTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaVersionTest;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 */
public class AllBugzillaHeadlessStandaloneTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Headless Standalone Tests for org.eclipse.mylyn.bugzilla.tests");
		suite.addTestSuite(BugzillaConfigurationTest.class);
		suite.addTestSuite(BugzillaVersionTest.class);
		suite.addTestSuite(BugzillaTaskCompletionTest.class);

		for (BugzillaFixture fixture : BugzillaFixture.ALL) {
			fixture.createSuite(suite);
			// XXX: re-enable when webservice is used for retrieval of history
			// fixture.add(fixtureSuite, BugzillaTaskHistoryTest.class); 
			fixture.add(BugzillaRepositoryConnectorStandaloneTest.class);
			fixture.add(BugzillaRepositoryConnectorConfigurationTest.class);

			// Move any tests here that are resulting in spurious failures
			// due to recent changes in Bugzilla Server head.
			if (fixture != BugzillaFixture.BUGS_HEAD) {
				fixture.add(BugzillaClientTest.class);

				// Only run these tests on > 3.2 repositories
				if (!fixture.getBugzillaVersion().isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2)) {
					fixture.add(BugzillaCustomFieldsTest.class);
					fixture.add(BugzillaFlagsTest.class);
				}

				fixture.add(BugzillaClientTest.class);
			}
			fixture.done();
		}
		return suite;
	}

}
