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
import org.eclipse.mylyn.commons.tests.net.SslProtocolSocketFactoryTest;
import org.eclipse.mylyn.commons.tests.net.WebUtilTest;
import org.eclipse.mylyn.commons.tests.support.ManagedTestSuite;
import org.eclipse.mylyn.tasks.tests.TaskListTest;
import org.eclipse.mylyn.tasks.tests.TasksUtilTest;
import org.eclipse.mylyn.tasks.tests.core.DefaultTaskSchemaTest;
import org.eclipse.mylyn.tasks.tests.core.EnvironmentTest;
import org.eclipse.mylyn.tasks.tests.core.FeedReaderTest;
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
@RunWith(AllTests.class)
public class AllHeadlessStandaloneTest {

	public static Test suite() {
		return suite(true);
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new ManagedTestSuite(AllHeadlessStandaloneTest.class.getName());

		// commons
		suite.addTestSuite(WebUtilTest.class);
		suite.addTestSuite(SslProtocolSocketFactoryTest.class);
		suite.addTestSuite(FeedReaderTest.class);
		suite.addTestSuite(EnvironmentTest.class);

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
		suite.addTestSuite(TasksUtilTest.class);
		suite.addTestSuite(ITasksCoreConstantsTest.class);
		suite.addTestSuite(TaskRepositoryLocationTest.class);
		suite.addTestSuite(TaskDataExternalizerTest.class);
		suite.addTestSuite(Xml11InputStreamTest.class);

		// bugzilla
		suite.addTest(AllBugzillaHeadlessStandaloneTests.suite(defaultOnly));

		// trac
		suite.addTest(AllTracHeadlessStandaloneTests.suite(defaultOnly));

		// hudson
		//suite.addTest(AllHudsonTests.suite(defaultOnly));

		return suite;
	}

}
