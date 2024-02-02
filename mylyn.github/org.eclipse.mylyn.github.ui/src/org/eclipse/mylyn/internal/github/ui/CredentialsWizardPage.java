/*******************************************************************************
 *  Copyright (c) 2011, 2020 GitHub Inc. and others
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
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

	private Button useToken;

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
	@Override
	public void createControl(Composite parent) {
		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(displayArea);

		new Label(displayArea, SWT.NONE).setText(Messages.CredentialsWizardPage_LabelUser);

		userText = new Text(displayArea, SWT.BORDER | SWT.SINGLE);
		userText.addModifyListener(e -> validatePage());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(userText);

		Label passwordLabel = new Label(displayArea, SWT.NONE);
		passwordLabel.setText(Messages.CredentialsWizardPage_LabelPassword);
		passwordText = new Text(displayArea, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		passwordText.addModifyListener(e -> validatePage());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);
		useToken = new Button(displayArea, SWT.CHECK);
		useToken.setText(Messages.HttpRepositorySettingsPage_LabelUseToken);
		useToken.setToolTipText(
				Messages.HttpRepositorySettingsPage_TooltipUseToken);
		useToken.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isChecked = useToken.getSelection();
				// Don't disable the userText; if the user want to create a
				// Gists Mylyn repository, we need a user name even with token
				// auth.
				if (isChecked) {
					passwordLabel.setText(
							Messages.HttpRepositorySettingsPage_LabelToken);
				} else {
					passwordLabel.setText(
							Messages.CredentialsWizardPage_LabelPassword);
				}
				passwordLabel.requestLayout();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		setControl(displayArea);
		setPageComplete(false);
	}

	private void validatePage() {
		String message = null;
		if (!useToken.getSelection() && userText.getText().trim().isEmpty()) {
			message = Messages.CredentialsWizardPage_ErrorUser;
		} else if (passwordText.getText().trim().isEmpty()) {
			if (useToken.getSelection()) {
				message = Messages.HttpRepositorySettingsPage_EnterToken;
			} else {
				message = Messages.CredentialsWizardPage_ErrorPassword;
			}
		}

		setErrorMessage(message);
		setPageComplete(message == null);

	}

	/**
	 * Retrieves the user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return userText.getText();
	}

	/**
	 * Retrieves the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return passwordText.getText();
	}

	/**
	 * Tells whether the {@link #getPassword() password} is a token.
	 *
	 * @return {@code true} if the password is a token; {@code false} otherwise
	 */
	public boolean isToken() {
		return useToken.getSelection();
	}
}
