/*******************************************************************************
 * Copyright (c) 2004, 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *     BREDEX GmbH - fix for bug 295050
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog that prompts for credentials. The dialog support two modes:
 * <ul>
 * <li>Username/password authentication</li>
 * <li>Key store authentication</li>
 * </ul>
 * 
 * @author Frank Becker
 * @author Steffen Pingel
 * @author Torsten Kalix
 * @since 3.7
 */
public class CredentialsDialog extends TitleAreaDialog {

	public enum Mode {
		KEY_STORE, USER,
	}

	private String domain;

	private Image keyLockImage;

	private String keyStoreFileName;

	private final Mode mode;

	private boolean needsDomain;

	private String password;

	private boolean savePassword;

	private String username;

	public CredentialsDialog(Shell parentShell, Mode mode) {
		super(parentShell);
		this.mode = mode;
		domain = ""; //$NON-NLS-1$
		username = ""; //$NON-NLS-1$
		password = ""; //$NON-NLS-1$
		keyStoreFileName = ""; //$NON-NLS-1$
	}

	@Override
	public boolean close() {
		if (keyLockImage != null) {
			keyLockImage.dispose();
		}
		return super.close();
	}

	public String getDomain() {
		return domain;
	}

	public String getKeyStoreFileName() {
		return keyStoreFileName;
	}

	public Mode getMode() {
		return mode;
	}

	public String getPassword() {
		return password;
	}

	public boolean getSavePassword() {
		return savePassword;
	}

	public String getUserName() {
		return username;
	}

	public boolean needsDomain() {
		return needsDomain;
	}

	public void setDomain(String domain) {
		Assert.isNotNull(domain);
		this.domain = domain;
	}

	public void setKeyStoreFileName(String keyStoreFileName) {
		Assert.isNotNull(this.keyStoreFileName);
		this.keyStoreFileName = keyStoreFileName;
	}

	public void setNeedsDomain(boolean needsDomain) {
		this.needsDomain = needsDomain;
	}

	public void setPassword(String password) {
		Assert.isNotNull(password);
		this.password = password;
	}

	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}

	public void setUsername(String username) {
		Assert.isNotNull(username);
		this.username = username;
	}

	private void createCenterArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (mode == Mode.USER) {
			createUserControls(composite);
		} else if (mode == Mode.KEY_STORE) {
			createKeyStoreControls(composite);
		}

		createPasswordControls(composite);
	}

	private void createKeyStoreControls(Composite composite) {
		new Label(composite, SWT.NONE).setText(Messages.CredentialsDialog_KeyStore);

		final Text keyStoreField = new Text(composite, SWT.BORDER);
		keyStoreField.addModifyListener(e -> keyStoreFileName = keyStoreField.getText());
		keyStoreField.setText(keyStoreFileName);
		if (keyStoreFileName.length() == 0) {
			keyStoreField.setFocus();
		}
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false)
				.applyTo(keyStoreField);

		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText(Messages.CredentialsDialog_Browse);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
				if (keyStoreFileName != null) {
					fileDialog.setFilterPath(System.getProperty("user.home", ".")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				String returnFile = fileDialog.open();
				if (returnFile != null) {
					username = returnFile;
					keyStoreField.setText(returnFile);
				}
			}
		});
	}

	private void createPasswordControls(Composite composite) {
		new Label(composite, SWT.NONE).setText(Messages.CredentialsDialog_Password);

		final Text passwordField = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordField.addModifyListener(e -> password = passwordField.getText());
		passwordField.setText(password);
		if (username.length() > 0) {
			passwordField.setFocus();
		}
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.span(2, 1)
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false)
				.applyTo(passwordField);

		final Button savePasswordButton = new Button(composite, SWT.CHECK);
		savePasswordButton.setText(Messages.CredentialsDialog_SavePassword);
		savePasswordButton.setSelection(savePassword);
		savePasswordButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				savePassword = savePasswordButton.getSelection();
			}
		});
		GridDataFactory.fillDefaults().span(3, 1).applyTo(savePasswordButton);
	}

	private void createUserControls(Composite composite) {
		new Label(composite, SWT.NONE).setText(Messages.CredentialsDialog_Username);

		final Text usernameText = new Text(composite, SWT.BORDER);
		usernameText.setText(username);
		usernameText.addModifyListener(e -> username = usernameText.getText());
		if (username.length() == 0) {
			usernameText.setFocus();
		}
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.span(2, 1)
				.grab(true, false)
				.applyTo(usernameText);

		if (needsDomain()) {
			new Label(composite, SWT.NONE).setText(Messages.CredentialsDialog_Domain);

			final Text domainText = new Text(composite, SWT.BORDER);
			domainText.setText(domain);
			domainText.addModifyListener(e -> domain = domainText.getText());
			GridDataFactory.fillDefaults()
					.align(SWT.FILL, SWT.CENTER)
					.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
					.span(2, 1)
					.grab(true, false)
					.applyTo(domainText);
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setText(Messages.CredentialsDialog_Enter_credentials);
		setTitle(Messages.CredentialsDialog_Authentication);

		Control control = super.createContents(parent);

		keyLockImage = CommonImages.BANNER_SECURE_ROLE.createImage();
		setTitleImage(keyLockImage);

		applyDialogFont(control);
		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parent2 = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parent2, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(composite);

		createCenterArea(composite);

		composite.pack();
		return parent;
	}

}
