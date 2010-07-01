/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

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
		return suite(false);
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new TestSuite("Headless Standalone Tests for org.eclipse.mylyn.trac.tests");
		// client tests
		suite.addTestSuite(TracSearchTest.class);
		suite.addTestSuite(TracTicketTest.class);
		suite.addTestSuite(TracRepositoryInfoTest.class);
		suite.addTestSuite(TracClientProxyTest.class);
		// core tests
		suite.addTestSuite(TracClientManagerTest.class);
		if (defaultOnly) {
			addTests(suite, TracFixture.DEFAULT);
		} else {
			// network tests
			for (TracFixture fixture : TracFixture.ALL) {
				addTests(suite, fixture);
			}
			// validation tests
			for (TracFixture fixture : TracFixture.MISC) {
				fixture.createSuite(suite);
				fixture.add(TracClientFactoryTest.class);
				fixture.add(TracClientTest.class);
				fixture.done();
			}
		}
		return suite;
	}

	private static void addTests(TestSuite suite, TracFixture fixture) {
		fixture.createSuite(suite);
		fixture.add(TracClientFactoryTest.class);
		fixture.add(TracClientTest.class);
		if (fixture.getAccessMode() == Version.XML_RPC) {
			fixture.add(TracXmlRpcClientTest.class);
		}
		fixture.done();
	}

}