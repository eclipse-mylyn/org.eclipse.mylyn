/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui.dialogs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.commons.ui.CommonsUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.PlatformUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Dialog that show the contents of an HTML page or the content of a URL in a dialog.
 * 
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class WebBrowserDialog extends MessageDialog {

	private String text;

	private Browser browser;

	public WebBrowserDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
		this.setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE);
	}

	public void setText(String text) {
		this.text = text;
		if (browser != null) {
			browser.setText(text);
		}
	}

	public String getText() {
		return text;
	}

	public static int openText(Shell parent, String title, String message, String text) {
		if (PlatformUiUtil.hasInternalBrowser()) {
			WebBrowserDialog dialog = new WebBrowserDialog(parent, title, null, message, NONE,
					new String[] { IDialogConstants.OK_LABEL }, 0);
			dialog.setText(text);
			return dialog.open();
		} else {
			File file = null;
			try {
				file = File.createTempFile("temp", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
				file.deleteOnExit();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				try {
					writer.write(message);
				} finally {
					writer.close();
				}
			} catch (IOException e) {
				if (file != null) {
					file.delete();
				}
				StatusManager.getManager().handle(
						new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN,
								"Unexpected error while displaying page", e), StatusManager.LOG); //$NON-NLS-1$
				return Window.CANCEL;
			}
			WorkbenchUtil.openUrl(file.toURI().toString(), IWorkbenchBrowserSupport.AS_EXTERNAL);
			return Window.OK;
		}
	}

	@Override
	public Control createCustomArea(Composite parent) {
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);
		layout.numColumns = 1;

		browser = new Browser(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 50;
		browser.setLayoutData(gd);

		if (text != null) {
			browser.setText(text);
		}

		return parent;
	}

	public Browser getBrowser() {
		return browser;
	}

}
