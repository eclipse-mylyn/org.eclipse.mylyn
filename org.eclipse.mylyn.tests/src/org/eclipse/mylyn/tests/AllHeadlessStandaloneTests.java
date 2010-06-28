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
import org.eclipse.mylyn.tasks.tests.core.ITasksCoreConstantsTest;
import org.eclipse.mylyn.tasks.tests.core.TaskListUnmatchedContainerTest;
import org.eclipse.mylyn.tasks.tests.core.TaskRepositoryLocationTest;
import org.eclipse.mylyn.trac.tests.AllTracHeadlessStandaloneTests;

/**
 * @author Steffen Pingel
 */
public class AllHeadlessStandaloneTests {

	public static Test suite() {
		return suite(true);
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new ManagedTestSuite("Tests not requiring Eclipse Workbench");

		// commons
		suite.addTestSuite(WebUtilTest.class);
		suite.addTestSuite(SslProtocolSocketFactoryTest.class);

		// context
		// disabled due to failure: bug 257972
//		suite.addTestSuite(ContextExternalizerTest.class);
//		suite.addTestSuite(DegreeOfInterestTest.class);
//		suite.addTestSuite(ContextTest.class);

		// discovery
		//suite.addTest(AllDiscoveryTests.suite());

		// tasks
		suite.addTestSuite(TaskListTest.class);
		suite.addTestSuite(TaskListUnmatchedContainerTest.class);
		suite.addTestSuite(TasksUtilTest.class);
		suite.addTestSuite(ITasksCoreConstantsTest.class);
		suite.addTestSuite(TaskRepositoryLocationTest.class);

		// wikitext
		suite.addTest(org.eclipse.mylyn.wikitext.tests.HeadlessStandaloneTests.suite());

		// bugzilla
		suite.addTest(AllBugzillaHeadlessStandaloneTests.suite(defaultOnly));

		// trac
		suite.addTest(AllTracHeadlessStandaloneTests.suite(defaultOnly));

		return suite;
	}

}
