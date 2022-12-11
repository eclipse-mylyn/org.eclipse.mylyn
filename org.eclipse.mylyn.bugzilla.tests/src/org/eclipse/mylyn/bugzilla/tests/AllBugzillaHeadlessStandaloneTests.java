/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
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

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaAttributeMapperTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaAttributeTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaClientTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaCustomFieldsTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaFlagsTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorStandaloneTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaTaskCompletionTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaUtilTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaVersionTest;
import org.eclipse.mylyn.bugzilla.tests.core.RepositoryConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 * @author Frank Becker
 */
public class AllBugzillaHeadlessStandaloneTests {

	public static Test suite() {
		return suite(TestConfiguration.getDefault());
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllBugzillaHeadlessStandaloneTests.class.getName());
		suite.addTestSuite(BugzillaConfigurationTest.class);
		suite.addTestSuite(BugzillaVersionTest.class);
		suite.addTestSuite(BugzillaDateTimeTests.class);
		suite.addTestSuite(BugzillaAttributeMapperTest.class);
		suite.addTestSuite(BugzillaAttributeTest.class);
		suite.addTestSuite(RepositoryConfigurationTest.class);
		if (!configuration.isLocalOnly()) {
			// network tests
			suite.addTestSuite(BugzillaTaskCompletionTest.class);
			// tests that run against all repository versions
			List<BugzillaFixture> fixtures = configuration.discover(BugzillaFixture.class, "bugzilla");
			for (BugzillaFixture fixture : fixtures) {
				addTests(suite, fixture);
			}
		}
		return suite;
	}

	protected static void addTests(TestSuite suite, BugzillaFixture fixture) {
		if (fixture.isExcluded()) {
			return;
		}

		fixture.createSuite(suite);
		// XXX: re-enable when webservice is used for retrieval of history
		// fixture.add(fixtureSuite, BugzillaTaskHistoryTest.class);
		fixture.add(BugzillaRepositoryConnectorStandaloneTest.class);
		fixture.add(BugzillaRepositoryConnectorConfigurationTest.class);
		fixture.add(BugzillaClientTest.class);
		fixture.add(BugzillaUtilTest.class);

		// Only run these tests on > 3.2 repositories
		if (!fixture.getBugzillaVersion().isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2)) {
			fixture.add(BugzillaCustomFieldsTest.class);
			fixture.add(BugzillaFlagsTest.class);
		}
		fixture.done();
	}

}
