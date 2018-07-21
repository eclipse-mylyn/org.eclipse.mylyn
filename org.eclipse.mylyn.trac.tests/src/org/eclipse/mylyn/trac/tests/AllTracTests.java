/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AllTracTests {

	public static Test suite() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
		TestConfiguration testConfiguration = ManagedSuite.getTestConfigurationOrCreateDefault();
		TestSuite suite = new ManagedTestSuite(AllTracTests.class.getName());
//		addTests(suite, testConfiguration);
		addEmptyTest(suite, testConfiguration);
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllTracTests.class.getName());
//		addTests(suite, configuration);
		addEmptyTest(suite, configuration);
		return suite;
	}

	private static void addEmptyTest(TestSuite suite, TestConfiguration default1) {
		suite.addTestSuite(EmptyTest.class);
	}

//	public static void addTests(TestSuite suite, TestConfiguration configuration) {
//		suite.addTest(AllTracHeadlessStandaloneTests.suite(configuration));
//		suite.addTestSuite(TracUtilTest.class);
//		suite.addTestSuite(TracHyperlinkUtilTest.class);
//
//		if (!configuration.isLocalOnly()) {
//			suite.addTestSuite(TracRepositoryQueryTest.class);
//			suite.addTestSuite(TracRepositorySettingsPageTest.class);
//			// network tests
//			List<TracFixture> fixtures = configuration.discover(TracFixture.class, "trac");
//			for (TracFixture fixture : fixtures) {
//				if (!fixture.hasTag(TracFixture.TAG_MISC)) {
//					addTests(suite, configuration, fixture);
//				}
//			}
//		}
//	}
//
//	protected static void addTests(TestSuite suite, TestConfiguration configuration, TracFixture fixture) {
//		fixture.createSuite(suite);
//		if (configuration.hasKind(TestKind.INTEGRATION) && !configuration.isLocalOnly()
//				&& CommonTestUtil.hasCredentials(PrivilegeLevel.ADMIN)) {
//			fixture.add(TracTestCleanupUtil.class);
//		}
//		fixture.add(TracRepositoryConnectorTest.class);
//		if (fixture.getAccessMode() == Version.XML_RPC) {
//			fixture.add(TracTaskDataHandlerXmlRpcTest.class);
//			fixture.add(TracAttachmentHandlerTest.class);
//		} else {
//			fixture.add(TracRepositoryConnectorWebTest.class);
//		}
//		fixture.done();
//	}

}