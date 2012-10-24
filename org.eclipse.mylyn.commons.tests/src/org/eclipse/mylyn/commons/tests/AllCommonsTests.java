/*******************************************************************************
 * Copyright (c) 2000, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.tests.core.AuthenticatedProxyTest;
import org.eclipse.mylyn.commons.tests.core.CommonListenerListTest;
import org.eclipse.mylyn.commons.tests.core.CoreUtilTest;
import org.eclipse.mylyn.commons.tests.core.ExtensionPointReaderTest;
import org.eclipse.mylyn.commons.tests.core.Html2TextReaderTest;
import org.eclipse.mylyn.commons.tests.core.storage.CommonStoreTest;
import org.eclipse.mylyn.commons.tests.net.NetUtilTest;
import org.eclipse.mylyn.commons.tests.net.SslProtocolSocketFactoryTest;
import org.eclipse.mylyn.commons.tests.net.TimeoutInputStreamTest;
import org.eclipse.mylyn.commons.tests.net.WebUtilTest;
import org.eclipse.mylyn.commons.tests.operations.OperationUtilTest;
import org.eclipse.mylyn.commons.tests.workbench.browser.BrowserUtilTest;

/**
 * @author Mik Kersten
 */
public class AllCommonsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllCommonsTests.class.getName());
		suite.addTestSuite(TimeoutInputStreamTest.class);
		suite.addTestSuite(CoreUtilTest.class);
		suite.addTestSuite(AuthenticatedProxyTest.class);
		suite.addTestSuite(OperationUtilTest.class);
		suite.addTestSuite(NetUtilTest.class);
		suite.addTestSuite(SslProtocolSocketFactoryTest.class);
		suite.addTestSuite(WebUtilTest.class);
		suite.addTestSuite(BrowserUtilTest.class);
		suite.addTestSuite(ExtensionPointReaderTest.class);
		suite.addTestSuite(CommonListenerListTest.class);
		suite.addTestSuite(CommonStoreTest.class);
		suite.addTestSuite(Html2TextReaderTest.class);
		return suite;
	}

}
