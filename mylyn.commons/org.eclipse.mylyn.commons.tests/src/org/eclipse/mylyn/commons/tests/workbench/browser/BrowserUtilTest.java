/*******************************************************************************
 * Copyright (c) 2012, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.workbench.browser;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.TestUrl;
import org.eclipse.mylyn.commons.sdk.util.junit5.EnabledIfCI;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.AbstractUrlHandler;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class BrowserUtilTest {

	public static class LowPriorityHandler extends AbstractUrlHandler {

		static EditorHandle handle;

		static boolean queried;

		@Override
		public EditorHandle openUrl(IWorkbenchPage page, String location, int customFlags) {
			queried = true;
			return handle;
		}

		@Override
		public int getPriority() {
			return 1;
		}
	}

	public static class HighPriorityHandler extends AbstractUrlHandler {

		static EditorHandle handle;

		static boolean queried;

		@Override
		public EditorHandle openUrl(IWorkbenchPage page, String location, int customFlags) {
			queried = true;
			return handle;
		}

		@Override
		public int getPriority() {
			return 1000;
		}

	}

	@BeforeEach
	void setUp() throws Exception {
		HighPriorityHandler.handle = null;
		HighPriorityHandler.queried = false;
		LowPriorityHandler.handle = null;
		LowPriorityHandler.queried = false;
	}

	@AfterEach
	void tearDown() throws Exception {
		HighPriorityHandler.handle = null;
		LowPriorityHandler.handle = null;
	}

	@Test
	@EnabledIfCI
	public void testUrlHandlerPriorityNullHandle() {
		assumeFalse(CommonTestUtil.skipBrowserTests(), "Browser crashes");

		BrowserUtil.openUrl(TestUrl.DEFAULT.getHttpsOk().toString(), 0);
		assertTrue(LowPriorityHandler.queried);
		assertTrue(HighPriorityHandler.queried);
	}

	@Test
	@EnabledIfCI
	public void testUrlHandlerPriorityLow() {
		assumeFalse(CommonTestUtil.skipBrowserTests(), "Browser crashes");

		LowPriorityHandler.handle = new EditorHandle();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Object result = BrowserUtil.openUrl(page, TestUrl.DEFAULT.getHttpsOk().toString(), 0);
		assertSame(result, LowPriorityHandler.handle);
		assertTrue(LowPriorityHandler.queried);
		assertTrue(HighPriorityHandler.queried);
	}

	@Test
	@EnabledIfCI
	public void testUrlHandlerPriorityHigh() {
		assumeFalse(CommonTestUtil.skipBrowserTests(), "Browser crashes");

		LowPriorityHandler.handle = new EditorHandle();
		HighPriorityHandler.handle = new EditorHandle();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Object result = BrowserUtil.openUrl(page, TestUrl.DEFAULT.getHttpsOk().toString(), 0);
		assertSame(result, HighPriorityHandler.handle);
		assertFalse(LowPriorityHandler.queried);
		assertTrue(HighPriorityHandler.queried);
	}

}
