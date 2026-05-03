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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests;

import org.eclipse.mylyn.commons.core.DateUtilTest;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizerTest;
import org.eclipse.mylyn.commons.sdk.util.junit5.MylynTestSetup;
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
import org.eclipse.mylyn.commons.tests.xmlrpc.XmlRpcClientTest;
import org.eclipse.mylyn.commons.tests.xmlrpc.XmlRpcOperationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Mik Kersten
 */

@Suite
@SelectClasses({ //
	// org.eclipse.mylyn.commons.core
	DateUtilTest.class, HtmlStreamTokenizerTest.class,
	// org.eclipse.mylyn.commons.tests.core
	AuthenticatedProxyTest.class, CommonListenerListTest.class, CoreUtilTest.class, ExtensionPointReaderTest.class,
	Html2TextReaderTest.class, StatusHandlerTest.class,
	// org.eclipse.mylyn.commons.tests.core.storage
	CommonStoreTest.class,
	// org.eclipse.mylyn.commons.tests.net
	CommonHttpMethod3Test.class, NetUtilTest.class, SslProtocolSocketFactoryTest.class, TimeoutInputStreamTest.class,
	WebUtilTest.class,
	// org.eclipse.mylyn.commons.tests.operations
	CancellableOperationMonitorThreadTest.class, OperationUtilTest.class,
	// org.eclipse.mylyn.commons.tests.workbench.browser
	BrowserUtilTest.class, WebBrowserDialogTest.class,
	// org.eclipse.mylyn.commons.tests.xmlrpc
	XmlRpcClientTest.class, XmlRpcOperationTest.class })
@MylynTestSetup
public class AllCommonsTests {
}
