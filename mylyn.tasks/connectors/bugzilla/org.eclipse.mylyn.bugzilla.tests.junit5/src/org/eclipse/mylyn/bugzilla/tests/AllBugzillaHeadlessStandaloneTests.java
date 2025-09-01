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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaAttributeMapperTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaAttributeTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaClientTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaCustomFieldsTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaFlagsTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorStandaloneTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaUtilTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaVersionTest;
import org.eclipse.mylyn.bugzilla.tests.core.RepositoryConfigurationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;


/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 * @author Frank Becker
 */
@Suite
@SelectClasses({
	BugzillaConfigurationTest.class, BugzillaVersionTest.class, BugzillaDateTimeTests.class,
	BugzillaAttributeMapperTest.class, BugzillaAttributeTest.class, RepositoryConfigurationTest.class,
		// needs fixture
	BugzillaRepositoryConnectorStandaloneTest.class,
	BugzillaRepositoryConnectorConfigurationTest.class, BugzillaClientTest.class, BugzillaUtilTest.class,
	BugzillaCustomFieldsTest.class, BugzillaFlagsTest.class
})

public class AllBugzillaHeadlessStandaloneTests {
//	public static Test suite() {
//		return suite(TestConfiguration.getDefault());
//	}
//
//	public static Test suite(TestConfiguration configuration) {
//		TestSuite suite = new TestSuite(AllBugzillaHeadlessStandaloneTests.class.getName());
//		suite.addTestSuite(BugzillaConfigurationTest.class);
//		suite.addTestSuite(BugzillaVersionTest.class);
//		suite.addTestSuite(BugzillaDateTimeTests.class);
//		suite.addTestSuite(BugzillaAttributeMapperTest.class);
//		suite.addTestSuite(BugzillaAttributeTest.class);
//		suite.addTestSuite(RepositoryConfigurationTest.class);
//		if (!configuration.isLocalOnly()) {
//			// network tests
//			suite.addTestSuite(BugzillaTaskCompletionTest.class);
//			// tests that run against all repository versions
//			List<BugzillaFixture> fixtures = configuration.discover(BugzillaFixture.class, "bugzilla", false);
//			for (BugzillaFixture fixture : fixtures) {
//				addTests(suite, fixture);
//			}
//		}
//		return suite;
//	}
//
//	protected static void addTests(TestSuite suite, BugzillaFixture fixture) {
//		if (fixture.isExcluded()) {
//			return;
//		}
//
//		fixture.createSuite(suite);
//		// XXX: re-enable when webservice is used for retrieval of history
//		// fixture.add(fixtureSuite, BugzillaTaskHistoryTest.class);
//		fixture.add(BugzillaRepositoryConnectorStandaloneTest.class);
//		fixture.add(BugzillaRepositoryConnectorConfigurationTest.class);
//		fixture.add(BugzillaClientTest.class);
//		fixture.add(BugzillaUtilTest.class);
//
//		// Only run these tests on > 3.2 repositories
//		if (!fixture.getBugzillaVersion().isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2)) {
//			fixture.add(BugzillaCustomFieldsTest.class);
//			fixture.add(BugzillaFlagsTest.class);
//		}
//		fixture.done();
//	}

}
