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

import org.eclipse.mylyn.tests.integration.AllIntegrationTests;
import org.eclipse.mylyn.tests.misc.AllMiscTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
@Suite
@SelectClasses({ AllIntegrationTests.class,
//FIXME: AF: remove? we already did these tests during component build
//	AllCommonsTests.suite(configuration),,
//	AllNotificationsTests.suite(),
//	AllContextTests.suite(),
//	AllContextTasksTests.suite(),
//	AllDiscoveryTests.suite(configuration),
//	AllJavaTests.suite(),
//	AllCdtTests.suite(),
//	AllMonitorTests.suite(),
//	AllIdeTests.suite(),
//	AllTasksTests.suite(configuration),
//	AllBuildsTests.suite(),
//	AllReviewsTests.suite(),
//	AllResourcesTests.suite(),
//	AllTeamTests.suite(),
		AllMiscTests.class })
public class AllNonConnectorTests {
}
