/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
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

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.commons.workbench.browser.WebBrowserDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import junit.framework.TestCase;

public class WebBrowserDialogTest extends TestCase {

	private class TestWebBrowserDialog extends WebBrowserDialog {
		private Composite parent;

		public TestWebBrowserDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
				int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
			super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
					defaultIndex);
		}

		@Override
		public Control createCustomArea(Composite parent) {
			this.parent = parent;
			return super.createCustomArea(parent);
		}

		public Composite getParent() {
			return parent;
		}
	}

	public void testCreate() {
		if (CommonTestUtil.skipBrowserTests()) {
			System.err.println("Skipping WebBrowserDialogTest.testCreate()");
			return;
		}
		TestWebBrowserDialog dialog = new TestWebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		dialog.create();
		assertEquals(3, dialog.getParent().getChildren().length);
		assertEquals(Text.class, dialog.getParent().getChildren()[0].getClass());
		assertEquals(Browser.class, dialog.getParent().getChildren()[1].getClass());
		assertEquals(Label.class, dialog.getParent().getChildren()[2].getClass());
	}

	public void testSetShow() {
		if (CommonTestUtil.skipBrowserTests()) {
			System.err.println("Skipping WebBrowserDialogTest.testSetShow()");
			return;
		}
		TestWebBrowserDialog dialog = new TestWebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		dialog.setShowLocation(false);
		dialog.setShowStatus(false);
		dialog.create();
		assertEquals(1, dialog.getParent().getChildren().length);
		assertEquals(Browser.class, dialog.getParent().getChildren()[0].getClass());
	}

	public void testSetShowAfterCreate() {
		if (CommonTestUtil.skipBrowserTests()) {
			System.err.println("Skipping WebBrowserDialogTest.testSetShowAfterCreate()");
			return;
		}
		WebBrowserDialog dialog = new WebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		dialog.create();
		try {
			dialog.setShowLocation(false);
			fail("Expected exception");
		} catch (IllegalStateException e) {
			// expected
		}
		try {
			dialog.setShowStatus(false);
			fail("Expected exception");
		} catch (IllegalStateException e) {
			// expected
		}
	}

}
