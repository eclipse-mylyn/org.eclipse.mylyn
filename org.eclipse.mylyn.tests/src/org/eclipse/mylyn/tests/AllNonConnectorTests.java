/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.junit4.ManagedTestSuite;
import org.eclipse.mylyn.tests.integration.AllIntegrationTests;
import org.eclipse.mylyn.tests.misc.AllMiscTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class AllNonConnectorTests {

	public static Test suite() {
		TestSuite suite = new ManagedTestSuite(AllNonConnectorTests.class.getName());
		addTests(suite, TestConfiguration.getDefault());
		return suite;
	}

	static void addTests(TestSuite suite, TestConfiguration configuration) {
		suite.addTest(AllIntegrationTests.suite());
		//FIXME: AF: remove? we already did these tests during component build
//		suite.addTest(AllCommonsTests.suite(configuration));
//		suite.addTest(AllNotificationsTests.suite());
//		suite.addTest(AllContextTests.suite());
//		suite.addTest(AllContextTasksTests.suite());
//		suite.addTest(AllDiscoveryTests.suite(configuration));
//		suite.addTest(AllJavaTests.suite());
//		suite.addTest(AllCdtTests.suite());
//		suite.addTest(AllMonitorTests.suite());
//		suite.addTest(AllIdeTests.suite());
//		suite.addTest(AllTasksTests.suite(configuration));
//		suite.addTest(AllBuildsTests.suite());
//		suite.addTest(AllReviewsTests.suite());
//		suite.addTest(AllResourcesTests.suite());
//		suite.addTest(AllTeamTests.suite());
		suite.addTest(AllMiscTests.suite());
	}

}
