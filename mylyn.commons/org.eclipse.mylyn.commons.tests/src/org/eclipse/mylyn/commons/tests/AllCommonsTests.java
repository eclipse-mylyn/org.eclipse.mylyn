/*******************************************************************************
 * Copyright (c) 2000, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.core.HtmlStreamTokenizerTest;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.tests.core.AuthenticatedProxyTest;
import org.eclipse.mylyn.commons.tests.core.CommonListenerListTest;
import org.eclipse.mylyn.commons.tests.core.CoreUtilTest;
import org.eclipse.mylyn.commons.tests.core.ExtensionPointReaderTest;
import org.eclipse.mylyn.commons.tests.core.Html2TextReaderTest;
import org.eclipse.mylyn.commons.tests.core.StatusHandlerTest;
import org.eclipse.mylyn.commons.tests.core.storage.CommonStoreTest;
import org.eclipse.mylyn.commons.tests.net.CommonHttpMethod3Test;
import org.eclipse.mylyn.commons.tests.net.NetUtilTest;
import org.eclipse.mylyn.commons.tests.net.SslProtocolSocketFactoryTest;
import org.eclipse.mylyn.commons.tests.net.TimeoutInputStreamTest;
import org.eclipse.mylyn.commons.tests.net.WebUtilTest;
import org.eclipse.mylyn.commons.tests.operations.CancellableOperationMonitorThreadTest;
import org.eclipse.mylyn.commons.tests.operations.OperationUtilTest;
import org.eclipse.mylyn.commons.tests.workbench.browser.BrowserUtilTest;
import org.eclipse.mylyn.commons.tests.workbench.browser.WebBrowserDialogTest;

/**
 * @author Mik Kersten
 */
public class AllCommonsTests {

	public static Test suite() {
		TestSuite suite = new ManagedTestSuite(AllCommonsTests.class.getName());
		addTests(suite);
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllCommonsTests.class.getName());
		addTests(suite);
		return suite;
	}

	private static void addTests(TestSuite suite) {
		suite.addTestSuite(TimeoutInputStreamTest.class);
		suite.addTestSuite(CoreUtilTest.class);
		suite.addTestSuite(AuthenticatedProxyTest.class);
		suite.addTestSuite(OperationUtilTest.class);
		suite.addTestSuite(NetUtilTest.class);
		suite.addTestSuite(SslProtocolSocketFactoryTest.class);
		suite.addTestSuite(WebUtilTest.class);
		suite.addTestSuite(BrowserUtilTest.class);
		suite.addTestSuite(WebBrowserDialogTest.class);
		suite.addTestSuite(ExtensionPointReaderTest.class);
		suite.addTestSuite(CommonListenerListTest.class);
		suite.addTestSuite(CommonStoreTest.class);
		suite.addTestSuite(Html2TextReaderTest.class);
		suite.addTestSuite(CommonHttpMethod3Test.class);
		suite.addTestSuite(HtmlStreamTokenizerTest.class);
		suite.addTestSuite(CancellableOperationMonitorThreadTest.class);
		suite.addTestSuite(StatusHandlerTest.class);
	}

}
