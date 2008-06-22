/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import org.eclipse.mylyn.trac.tests.client.TracClientFactoryTest;
import org.eclipse.mylyn.trac.tests.client.TracClientProxyTest;
import org.eclipse.mylyn.trac.tests.client.TracSearchTest;
import org.eclipse.mylyn.trac.tests.client.TracTicketTest;
import org.eclipse.mylyn.trac.tests.client.TracWebClientSearchTest;
import org.eclipse.mylyn.trac.tests.client.TracWebClientTest;
import org.eclipse.mylyn.trac.tests.client.TracXmlRpcClientSearchTest;
import org.eclipse.mylyn.trac.tests.client.TracXmlRpcClientTest;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AllTracTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.trac.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(TracSearchTest.class);
		suite.addTestSuite(TracTicketTest.class);
		suite.addTestSuite(TracXmlRpcClientTest.class);
		suite.addTestSuite(TracXmlRpcClientSearchTest.class);
		suite.addTestSuite(TracWebClientTest.class);
		suite.addTestSuite(TracWebClientSearchTest.class);
		suite.addTestSuite(TracClientFactoryTest.class);
		suite.addTestSuite(TracRepositoryConnectorTest.class);
		suite.addTestSuite(TracQueryTest.class);
		suite.addTestSuite(TracRepositoryQueryTest.class);
		suite.addTestSuite(TracClientManagerTest.class);
		suite.addTestSuite(TracAttachmentHandlerTest.class);
		suite.addTestSuite(RepositorySearchQueryTest.class);
		suite.addTestSuite(TracTaskDataHandlerTest.class);
		suite.addTestSuite(TracTaskTest.class);
		suite.addTestSuite(TracRepositorySettingsPageTest.class);
		suite.addTestSuite(TracClientProxyTest.class);
		suite.addTestSuite(TracHyperlinkUtilTest.class);
		// $JUnit-END$
		return suite;
	}

}