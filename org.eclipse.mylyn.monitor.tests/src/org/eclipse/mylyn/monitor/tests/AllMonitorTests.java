/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.monitor.tests;

import org.eclipse.mylyn.monitor.reports.tests.AllMonitorReportTests;
import org.eclipse.mylyn.monitor.tests.usage.tests.AllMonitorUsageTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllMonitorTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.monitor.ui.tests");

		// $JUnit-BEGIN$
		suite.addTest(AllMonitorUsageTests.suite());
		suite.addTest(AllMonitorReportTests.suite());
		// suite.addTestSuite(TaskTimerTest.class);
		suite.addTestSuite(StatisticsReportingTest.class);
		suite.addTestSuite(InteractionLoggerTest.class);
		suite.addTestSuite(ActiveTimerTest.class);
		suite.addTestSuite(StatisticsLoggingTest.class);
		suite.addTestSuite(MonitorTest.class);
		suite.addTestSuite(InteractionEventExternalizationTest.class);
		suite.addTestSuite(MonitorPackagingTest.class);
		suite.addTestSuite(MultiWindowMonitorTest.class);
		// $JUnit-END$

		return suite;
	}

}
