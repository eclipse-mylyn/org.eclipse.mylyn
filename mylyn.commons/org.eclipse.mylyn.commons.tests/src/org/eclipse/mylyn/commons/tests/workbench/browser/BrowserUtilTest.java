/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.workbench.browser;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.AbstractUrlHandler;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class BrowserUtilTest extends TestCase {

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

	@Override
	protected void setUp() throws Exception {
		HighPriorityHandler.handle = null;
		HighPriorityHandler.queried = false;
		LowPriorityHandler.handle = null;
		LowPriorityHandler.queried = false;
	}

	@Override
	protected void tearDown() throws Exception {
		HighPriorityHandler.handle = null;
		LowPriorityHandler.handle = null;
	}

	public void testUrlHandlerPriorityNullHandle() {
		if (CommonTestUtil.skipBrowserTests()) {
			System.err.println("Skipping BrowserUtilTest.testUrlHandlerPriorityNullHandle() to avoid browser crash");
			return;
		}
		BrowserUtil.openUrl("http://mylyn.org", 0);
		assertTrue(LowPriorityHandler.queried);
		assertTrue(HighPriorityHandler.queried);
	}

	public void testUrlHandlerPriorityLow() {
		if (CommonTestUtil.skipBrowserTests()) {
			System.err.println("Skipping BrowserUtilTest.testUrlHandlerPriorityLow() to avoid browser crash");
			return;
		}
		LowPriorityHandler.handle = new EditorHandle();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Object result = BrowserUtil.openUrl(page, "http://mylyn.org", 0);
		assertSame(result, LowPriorityHandler.handle);
		assertTrue(LowPriorityHandler.queried);
		assertTrue(HighPriorityHandler.queried);
	}

	public void testUrlHandlerPriorityHigh() {
		if (CommonTestUtil.skipBrowserTests()) {
			System.err.println("Skipping BrowserUtilTest.testUrlHandlerPriorityHigh() to avoid browser crash");
			return;
		}
		LowPriorityHandler.handle = new EditorHandle();
		HighPriorityHandler.handle = new EditorHandle();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Object result = BrowserUtil.openUrl(page, "http://mylyn.org", 0);
		assertSame(result, HighPriorityHandler.handle);
		assertFalse(LowPriorityHandler.queried);
		assertTrue(HighPriorityHandler.queried);
	}

}
