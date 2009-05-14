/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import org.eclipse.mylyn.commons.tests.net.SslProtocolSocketFactoryTest;
import org.eclipse.mylyn.commons.tests.net.WebUtilTest;
import org.eclipse.mylyn.discovery.tests.AllDiscoveryTests;
import org.eclipse.mylyn.jira.tests.AllJiraHeadlessStandaloneTests;
import org.eclipse.mylyn.tasks.tests.TaskListTest;
import org.eclipse.mylyn.tasks.tests.TasksUtilTest;
import org.eclipse.mylyn.tasks.tests.core.ITasksCoreConstantsTest;
import org.eclipse.mylyn.tasks.tests.core.TaskListUnmatchedContainerTest;
import org.eclipse.mylyn.tasks.tests.core.TaskRepositoryLocationTest;
import org.eclipse.mylyn.trac.tests.AllTracHeadlessStandaloneTests;

/**
 * @author Mik Kersten
 */
public class AllHeadlessStandaloneTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests not requiring Eclipse Workbench");

		// commons
		suite.addTestSuite(WebUtilTest.class);
		suite.addTestSuite(SslProtocolSocketFactoryTest.class);

		// context
		// disabled due to failure: bug 257972
//		suite.addTestSuite(ContextExternalizerTest.class);
//		suite.addTestSuite(DegreeOfInterestTest.class);
//		suite.addTestSuite(ContextTest.class);

		// discovery
		suite.addTest(AllDiscoveryTests.suite());

		// tasks
		suite.addTestSuite(TaskListTest.class);
		suite.addTestSuite(TaskListUnmatchedContainerTest.class);
		suite.addTestSuite(TasksUtilTest.class);
		suite.addTestSuite(ITasksCoreConstantsTest.class);
		suite.addTestSuite(TaskRepositoryLocationTest.class);

		// wikitext
		suite.addTest(org.eclipse.mylyn.wikitext.tests.HeadlessStandaloneTests.suite());

		// trac
		suite.addTest(AllTracHeadlessStandaloneTests.suite());

		// jira
		suite.addTest(AllJiraHeadlessStandaloneTests.suite());

		return suite;
	}

}
