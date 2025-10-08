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
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.manual;

import org.eclipse.mylyn.commons.ui.dialogs.CredentialsDialog;
import org.eclipse.mylyn.commons.ui.dialogs.CredentialsDialog.Mode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class TestCredentialsDialog {

	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Test Credentials Dialog");
		shell.setLayout(new RowLayout());

		Button userButton = new Button(shell, SWT.PUSH);
		userButton.setText("Username/Password");
		userButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CredentialsDialog dialog = new CredentialsDialog(shell, Mode.USER);
				dialog.create();
				dialog.setMessage("Enter password");
				dialog.open();
				System.err.println("User name: " + dialog.getUserName());
				System.err.println("Password: " + dialog.getPassword());
				System.err.println("Save password: " + dialog.getSavePassword());
			}
		});

		Button domainButton = new Button(shell, SWT.PUSH);
		domainButton.setText("Domain");
		domainButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CredentialsDialog dialog = new CredentialsDialog(shell, Mode.USER);
				dialog.setNeedsDomain(true);
				dialog.create();
				dialog.setMessage("Enter password");
				dialog.open();
				System.err.println("User name: " + dialog.getUserName());
				System.err.println("Password: " + dialog.getPassword());
				System.err.println("Domain: " + dialog.getDomain());
				System.err.println("Save password: " + dialog.getSavePassword());
			}
		});

		Button keyStoreButton = new Button(shell, SWT.PUSH);
		keyStoreButton.setText("Key Store");
		keyStoreButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CredentialsDialog dialog = new CredentialsDialog(shell, Mode.KEY_STORE);
				dialog.create();
				dialog.setMessage("Enter keystore location");
				dialog.open();
				System.err.println("Key store filename: " + dialog.getKeyStoreFileName());
				System.err.println("Password: " + dialog.getPassword());
				System.err.println("Save password: " + dialog.getSavePassword());
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
