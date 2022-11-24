/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.manual;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.workbench.browser.WebBrowserDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Steffen Pingel
 */
public class TestWebBrowserDialog {

	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Test Web Browser Dialog");
		shell.setLayout(new RowLayout());

		final Text locationText = new Text(shell, SWT.BORDER);
		locationText.setLayoutData(new RowData(200, SWT.DEFAULT));
		locationText.setText("https://localhost");

		Button userButton = new Button(shell, SWT.PUSH);
		userButton.setText("Open");
		userButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WebBrowserDialog dialog = new WebBrowserDialog(shell, "Browse " + locationText.getText(), null,
						"Web Browser Dialog Test", MessageDialog.NONE, new String[] { IDialogConstants.CANCEL_LABEL },
						0);
				dialog.create();
				dialog.setUrl(locationText.getText(), null, null);
				dialog.open();
			}
		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
