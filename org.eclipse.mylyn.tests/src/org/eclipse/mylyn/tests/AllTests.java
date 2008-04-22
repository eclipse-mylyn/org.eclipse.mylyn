/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.AllBugzillaTests;
import org.eclipse.mylyn.context.tests.AllContextTests;
import org.eclipse.mylyn.ide.tests.AllIdeTests;
import org.eclipse.mylyn.java.tests.AllJavaTests;
import org.eclipse.mylyn.jira.tests.AllJiraTests;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.monitor.tests.AllMonitorTests;
import org.eclipse.mylyn.resources.tests.AllResourcesTests;
import org.eclipse.mylyn.tasks.tests.AllTasksTests;
import org.eclipse.mylyn.team.tests.AllTeamTests;
import org.eclipse.mylyn.tests.integration.AllIntegrationTests;
import org.eclipse.mylyn.tests.integration.TestingStatusNotifier;
import org.eclipse.mylyn.tests.misc.AllMiscTests;
import org.eclipse.mylyn.trac.tests.AllTracTests;
import org.eclipse.mylyn.xplanner.tests.AllXPlannerTests;

/**
 * @author Mik Kersten
 */
public class AllTests {

	public static Test suite() {
		StatusHandler.addStatusHandler(new TestingStatusNotifier());
//		ResourcesUiBridgePlugin.getDefault().setResourceMonitoringEnabled(false);

		// TODO: the order of these tests might still matter, but shouldn't

		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.tests");
		// $JUnit-BEGIN$
		suite.addTest(AllContextTests.suite());
		suite.addTest(AllJavaTests.suite());
		suite.addTest(AllMonitorTests.suite());
		suite.addTest(AllIntegrationTests.suite());
		suite.addTest(AllIdeTests.suite());
		suite.addTest(AllTasksTests.suite());
		suite.addTest(AllResourcesTests.suite());
		suite.addTest(AllTeamTests.suite());
		suite.addTest(AllCoreTests.suite());
		suite.addTest(AllMiscTests.suite());
		suite.addTest(AllBugzillaTests.suite());
		suite.addTest(AllJiraTests.suite());
		suite.addTest(AllTracTests.suite());
		suite.addTest(AllXPlannerTests.suite());
		// $JUnit-END$
		return suite;
	}
}
