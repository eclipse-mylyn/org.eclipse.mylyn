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

import org.eclipse.mylyn.commons.core.HtmlStreamTokenizerTest;
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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Mik Kersten
 */
@RunWith(Suite.class)
@SuiteClasses({ TimeoutInputStreamTest.class, CoreUtilTest.class, AuthenticatedProxyTest.class, OperationUtilTest.class,
	NetUtilTest.class, SslProtocolSocketFactoryTest.class, WebUtilTest.class, BrowserUtilTest.class,
	WebBrowserDialogTest.class, ExtensionPointReaderTest.class, CommonListenerListTest.class, CommonStoreTest.class,
	Html2TextReaderTest.class, CommonHttpMethod3Test.class, HtmlStreamTokenizerTest.class,
	CancellableOperationMonitorThreadTest.class, StatusHandlerTest.class })
public class AllCommonsTests {
}
