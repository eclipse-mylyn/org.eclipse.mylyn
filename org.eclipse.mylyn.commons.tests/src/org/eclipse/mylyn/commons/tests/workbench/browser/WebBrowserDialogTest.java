/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.workbench.browser;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.commons.workbench.browser.WebBrowserDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class WebBrowserDialogTest extends TestCase {

	private class TestWebBrowserDialog extends WebBrowserDialog {
		private Composite parent;

		public TestWebBrowserDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
				String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
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
		TestWebBrowserDialog dialog = new TestWebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		dialog.create();
		assertEquals(3, dialog.getParent().getChildren().length);
		assertEquals(Text.class, dialog.getParent().getChildren()[0].getClass());
		assertEquals(Browser.class, dialog.getParent().getChildren()[1].getClass());
		assertEquals(Label.class, dialog.getParent().getChildren()[2].getClass());
	}

	public void testGetUrl() throws InterruptedException {
		TestWebBrowserDialog dialog = new TestWebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		final AtomicBoolean pageLoaded = new AtomicBoolean();
		dialog.create();
		dialog.getBrowser().addLocationListener(new LocationListener() {
			public void changing(LocationEvent event) {
				// ignore			
			}

			public void changed(LocationEvent event) {
				if (!event.top) {
					// ignore nested frames
					return;
				}
				pageLoaded.set(true);
			}
		});
		dialog.setUrl("http://mylyn.org/", null, null);
		while (!pageLoaded.get()) {
			Display.getCurrent().readAndDispatch();
		}
		Thread.sleep(50);// wait for other listener to set url
		assertEquals("http://mylyn.org/", ((Text) dialog.getParent().getChildren()[0]).getText());
	}

	public void testSetShow() {
		TestWebBrowserDialog dialog = new TestWebBrowserDialog(WorkbenchUtil.getShell(), "title", null, "message", 0,
				new String[0], 0);
		dialog.setShowLocation(false);
		dialog.setShowStatus(false);
		dialog.create();
		assertEquals(1, dialog.getParent().getChildren().length);
		assertEquals(Browser.class, dialog.getParent().getChildren()[0].getClass());
	}

	public void testSetShowAfterCreate() {
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
