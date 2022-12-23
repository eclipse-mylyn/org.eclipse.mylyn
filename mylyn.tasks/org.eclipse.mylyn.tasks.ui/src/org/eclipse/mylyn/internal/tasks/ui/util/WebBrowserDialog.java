/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * Dialog to show the contents of an html page to the user
 * 
 * @author Shawn Minto
 * @deprecated use {@link org.eclipse.mylyn.commons.workbench.browser.WebBrowserDialog} instead
 */
@Deprecated
public class WebBrowserDialog extends MessageDialog {

	private String data = null;

	private static Boolean browserAvailable;

	public WebBrowserDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels, int defaultIndex, String data) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
		this.data = data;
		this.setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE);
	}

	public static int openAcceptAgreement(Shell parent, String title, String message, String data) {
		if (isInternalBrowserAvailable(parent)) {
			WebBrowserDialog dialog = new WebBrowserDialog(parent, title, null, // accept
					message, NONE, new String[] { IDialogConstants.OK_LABEL }, 0, data);
			// ok is the default
			return dialog.open();
		} else {
			File file = null;
			try {
				file = File.createTempFile("mylyn-error", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
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
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Unexpected error while displaying error", e)); //$NON-NLS-1$
				return Window.CANCEL;
			}
			BrowserUtil.openUrl(file.toURI().toString(), IWorkbenchBrowserSupport.AS_EXTERNAL);
			return Window.OK;
		}
	}

	@Override
	public Control createCustomArea(Composite parent) {
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);
		layout.numColumns = 1;

		Browser b = new Browser(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 50;
		b.setLayoutData(gd);
		b.setText(data);

		return parent;
	}

	private static synchronized boolean isInternalBrowserAvailable(Composite composite) {
		if (browserAvailable == null) {
			try {
				Browser browser = new Browser(composite, SWT.NULL);
				browser.dispose();
				browserAvailable = true;
			} catch (SWTError e) {
				browserAvailable = false;
			}
		}
		return browserAvailable;
	}

}
