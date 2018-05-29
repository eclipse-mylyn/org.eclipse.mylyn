/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Credentials wizard page class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CredentialsWizardPage extends WizardPage {

	private Text userText;

	private Text passwordText;

	/**
	 * Create credentials wizard page
	 */
	public CredentialsWizardPage() {
		super("credentialsPage", Messages.CredentialsWizardPage_Title, null); //$NON-NLS-1$
		setDescription(Messages.CredentialsWizardPage_Description);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false)
				.applyTo(displayArea);

		new Label(displayArea, SWT.NONE).setText(Messages.CredentialsWizardPage_LabelUser);

		userText = new Text(displayArea, SWT.BORDER | SWT.SINGLE);
		userText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(userText);

		new Label(displayArea, SWT.NONE).setText(Messages.CredentialsWizardPage_LabelPassword);
		passwordText = new Text(displayArea, SWT.BORDER | SWT.SINGLE
				| SWT.PASSWORD);
		passwordText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);

		setControl(displayArea);
		setPageComplete(false);
	}

	private void validatePage() {
		String message = null;
		if (message == null && userText.getText().trim().length() == 0)
			message = Messages.CredentialsWizardPage_ErrorUser;

		if (message == null && passwordText.getText().trim().length() == 0)
			message = Messages.CredentialsWizardPage_ErrorPassword;

		setErrorMessage(message);
		setPageComplete(message == null);

	}

	/**
	 * Get user name
	 * 
	 * @return user name
	 */
	public String getUserName() {
		return this.userText.getText();
	}

	/**
	 * Get password
	 * 
	 * @return password
	 */
	public String getPassword() {
		return this.passwordText.getText();
	}

}
