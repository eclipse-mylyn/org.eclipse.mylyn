/*******************************************************************************
 * Copyright (c) 2008, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.AllBugzillaHeadlessStandaloneTests;
import org.eclipse.mylyn.commons.notifications.tests.core.NotificationEnvironmentTest;
import org.eclipse.mylyn.commons.notifications.tests.feed.FeedReaderTest;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration.TestKind;
import org.eclipse.mylyn.commons.tests.core.CoreUtilTest;
import org.eclipse.mylyn.commons.tests.net.SslProtocolSocketFactoryTest;
import org.eclipse.mylyn.commons.tests.net.WebUtilTest;
import org.eclipse.mylyn.hudson.tests.AllHudsonTests;
import org.eclipse.mylyn.tasks.tests.TaskListTest;
import org.eclipse.mylyn.tasks.tests.core.DefaultTaskSchemaTest;
import org.eclipse.mylyn.tasks.tests.core.ITasksCoreConstantsTest;
import org.eclipse.mylyn.tasks.tests.core.TaskListUnmatchedContainerTest;
import org.eclipse.mylyn.tasks.tests.core.TaskRepositoryLocationTest;
import org.eclipse.mylyn.tasks.tests.data.TaskDataExternalizerTest;
import org.eclipse.mylyn.tasks.tests.data.Xml11InputStreamTest;
import org.eclipse.mylyn.trac.tests.AllTracHeadlessStandaloneTests;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

/**
 * @author Steffen Pingel
 */
// required for Maven which runs tests using JUnit 4 
@RunWith(AllTests.class)
public class AllHeadlessStandaloneTest {

	public static Test suite() {
		TestConfiguration configuration = new TestConfiguration(TestKind.INTEGRATION);
		configuration.setLocalOnly(true);

		return suite(TestConfiguration.getDefault());
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new ManagedTestSuite(AllHeadlessStandaloneTest.class.getName());

		// commons
		suite.addTestSuite(CoreUtilTest.class);
		suite.addTestSuite(WebUtilTest.class);
		suite.addTestSuite(SslProtocolSocketFactoryTest.class);
		suite.addTestSuite(FeedReaderTest.class);
		suite.addTestSuite(NotificationEnvironmentTest.class);

		// context
		// disabled due to failure: bug 257972
//		suite.addTestSuite(ContextExternalizerTest.class);
//		suite.addTestSuite(DegreeOfInterestTest.class);
//		suite.addTestSuite(ContextTest.class);

		// discovery
		//suite.addTest(AllDiscoveryTests.suite());

		// tasks
		suite.addTestSuite(DefaultTaskSchemaTest.class);
		suite.addTestSuite(TaskListTest.class);
		suite.addTestSuite(TaskListUnmatchedContainerTest.class);
		suite.addTestSuite(ITasksCoreConstantsTest.class);
		suite.addTestSuite(TaskRepositoryLocationTest.class);
		suite.addTestSuite(TaskDataExternalizerTest.class);
		suite.addTestSuite(Xml11InputStreamTest.class);

		// bugzilla
		suite.addTest(AllBugzillaHeadlessStandaloneTests.suite(configuration));

		// trac
		suite.addTest(AllTracHeadlessStandaloneTests.suite(configuration));

		// hudson
		suite.addTest(AllHudsonTests.suite(configuration));

		return suite;
	}

}
