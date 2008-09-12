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

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog to show the contents of an html page to the user
 * 
 * @author Shawn Minto
 */
public class WebBrowserDialog extends MessageDialog {

	private String data = null;

	public WebBrowserDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels, int defaultIndex, String data) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
		this.data = data;
		this.setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE);
	}

	public static int openAcceptAgreement(Shell parent, String title, String message, String data) {
		WebBrowserDialog dialog = new WebBrowserDialog(parent, title, null, // accept
				message, NONE, new String[] { IDialogConstants.OK_LABEL }, 0, data);
		// ok is the default
		return dialog.open();
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

}
