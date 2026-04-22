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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

@SuppressWarnings("nls")
public class WebBrowserDialogTest {

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

	@Test
	@DisabledOnOs(value = OS.LINUX, disabledReason = "missing GTK 3.x/4.x bindings") // TODO Is this still the case
	public void testCreate() {
		assumeFalse(CommonTestUtil.skipBrowserTests(), "Browser crashes");

		TestWebBrowserDialog dialog = new TestWebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		dialog.create();
		assertEquals(3, dialog.getParent().getChildren().length);
		assertEquals(Text.class, dialog.getParent().getChildren()[0].getClass());
		assertEquals(Browser.class, dialog.getParent().getChildren()[1].getClass());
		assertEquals(Label.class, dialog.getParent().getChildren()[2].getClass());
	}

	@Test
	@DisabledOnOs(value = OS.LINUX, disabledReason = "missing GTK 3.x/4.x bindings") // TODO Is this still the case
	public void testSetShow() {
		assumeFalse(CommonTestUtil.skipBrowserTests(), "Browser crashes");

		TestWebBrowserDialog dialog = new TestWebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		dialog.setShowLocation(false);
		dialog.setShowStatus(false);
		dialog.create();
		assertEquals(1, dialog.getParent().getChildren().length);
		assertEquals(Browser.class, dialog.getParent().getChildren()[0].getClass());
	}

	@Test
	@DisabledOnOs(value = OS.LINUX, disabledReason = "missing GTK 3.x/4.x bindings") // TODO Is this still the case
	public void testSetShowAfterCreate() {
		assumeFalse(CommonTestUtil.skipBrowserTests(), "Browser crashes");

		WebBrowserDialog dialog = new WebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		dialog.create();
		assertThrows(IllegalStateException.class, () -> dialog.setShowLocation(false));

		assertThrows(IllegalStateException.class, () -> dialog.setShowStatus(false));
	}

}
