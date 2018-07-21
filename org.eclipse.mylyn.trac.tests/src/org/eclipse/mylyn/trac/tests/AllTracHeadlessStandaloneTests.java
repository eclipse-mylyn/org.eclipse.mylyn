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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.trac.tests.client.TracClientFactoryTest;
import org.eclipse.mylyn.trac.tests.client.TracClientProxyTest;
import org.eclipse.mylyn.trac.tests.client.TracClientTest;
import org.eclipse.mylyn.trac.tests.client.TracRepositoryInfoTest;
import org.eclipse.mylyn.trac.tests.client.TracSearchTest;
import org.eclipse.mylyn.trac.tests.client.TracTicketTest;
import org.eclipse.mylyn.trac.tests.client.TracXmlRpcClientTest;
import org.eclipse.mylyn.trac.tests.core.TracClientManagerTest;
import org.eclipse.mylyn.trac.tests.support.TracFixture;

/**
 * @author Steffen Pingel
 */
public class AllTracHeadlessStandaloneTests {

	public static Test suite() {
		return suite(TestConfiguration.getDefault());
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllTracHeadlessStandaloneTests.class.getName());
		// client tests
		suite.addTestSuite(TracSearchTest.class);
		suite.addTestSuite(TracTicketTest.class);
		suite.addTestSuite(TracRepositoryInfoTest.class);
		suite.addTestSuite(TracClientProxyTest.class);
		// network tests
		if (!configuration.isLocalOnly()) {
			List<TracFixture> fixtures = configuration.discover(TracFixture.class, "trac");
			for (TracFixture fixture : fixtures) {
				if (fixture.hasTag(TracFixture.TAG_MISC)) {
					fixture.createSuite(suite);
					fixture.add(TracClientFactoryTest.class);
					fixture.add(TracClientTest.class);
					fixture.done();
				} else if (!fixture.hasTag(TracFixture.TAG_TEST)) {
					addTests(suite, fixture);
				}
			}
		}
		return suite;
	}

	private static void addTests(TestSuite suite, TracFixture fixture) {
		fixture.createSuite(suite);
		fixture.add(TracClientManagerTest.class);
		fixture.add(TracClientFactoryTest.class);
		fixture.add(TracClientTest.class);
		if (fixture.getAccessMode() == Version.XML_RPC) {
			fixture.add(TracXmlRpcClientTest.class);
		}
		fixture.done();
	}

}