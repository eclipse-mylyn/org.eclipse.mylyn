/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.ui.TracHyperlinkUtilTest;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AllTracTests {

	public static Test suite() {
//		UrlConnectionUtil.initCommonsLoggingSettings();

		TracCorePlugin.getDefault()
				.getConnector()
				.setTaskRepositoryLocationFactory(new TaskRepositoryLocationFactory());

		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.trac.tests");
		// $JUnit-BEGIN$
		// suite.addTestSuite(TracXmlRpcTest.class);
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