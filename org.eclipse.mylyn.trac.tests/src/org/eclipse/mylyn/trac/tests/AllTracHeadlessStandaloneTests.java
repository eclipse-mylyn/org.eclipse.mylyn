/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.trac.tests.client.TracClientFactoryTest;
import org.eclipse.mylyn.trac.tests.client.TracClientProxyTest;
import org.eclipse.mylyn.trac.tests.client.TracSearchTest;
import org.eclipse.mylyn.trac.tests.client.TracTicketTest;
import org.eclipse.mylyn.trac.tests.client.TracWebClientSearchTest;
import org.eclipse.mylyn.trac.tests.client.TracWebClientTest;
import org.eclipse.mylyn.trac.tests.client.TracXmlRpcClientSearchTest;
import org.eclipse.mylyn.trac.tests.client.TracXmlRpcClientTest;
import org.eclipse.mylyn.trac.tests.core.TracClientManagerTest;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AllTracHeadlessStandaloneTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Headless Standalone Tests for org.eclipse.mylyn.trac.tests");
		// other
		suite.addTestSuite(TracClientManagerTest.class);
		// client
		suite.addTestSuite(TracSearchTest.class);
		suite.addTestSuite(TracTicketTest.class);
		suite.addTestSuite(TracXmlRpcClientTest.class);
		suite.addTestSuite(TracXmlRpcClientSearchTest.class);
		suite.addTestSuite(TracWebClientTest.class);
		suite.addTestSuite(TracWebClientSearchTest.class);
		suite.addTestSuite(TracClientFactoryTest.class);
		suite.addTestSuite(TracClientProxyTest.class);
		return suite;
	}

}