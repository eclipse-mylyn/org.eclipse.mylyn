/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class EditCredentialsDialog extends Dialog {

	private static final String TITLE = "Enter Password";

	private boolean savePassword;

	private Image keyLockImage;

	private String message;

	private String username = "";

	private String password = "";

	private String url;

	public EditCredentialsDialog(Shell parentShell) {
		super(parentShell);

		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public String getUserName() {
		return username;
	}

	public void setUsername(String username) {
		if (username == null) {
			throw new IllegalArgumentException();
		}
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password == null) {
			throw new IllegalArgumentException();
		}
		this.password = password;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public boolean getSavePassword() {
		return savePassword;
	}

	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(TITLE);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(composite, SWT.NONE);
		keyLockImage = TasksUiPlugin.imageDescriptorFromPlugin(TasksUiPlugin.ID_PLUGIN, "icons/wizban/keylock.gif")
				.createImage();
		label.setImage(keyLockImage);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(label);

		createCenterArea(composite);

		Dialog.applyDialogFont(parent);

		return composite;
	}

	private void createCenterArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (message != null) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText(message);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(
					convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT).grab(true,
					false).span(2, 1).applyTo(label);

			// spacer
			label = new Label(composite, SWT.NONE);
			GridDataFactory.fillDefaults().span(2, 1).applyTo(label);
		}

		if (url != null) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText("Repository:");

			label = new Label(composite, SWT.NONE);
			label.setText(url);
			GridDataFactory.fillDefaults().applyTo(label);
		}

		new Label(composite, SWT.NONE).setText("&User name:");

		final Text usernameField = new Text(composite, SWT.BORDER);
		usernameField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				username = usernameField.getText();
			}
		});
		usernameField.setText(username);
		if (username.length() == 0) {
			usernameField.setFocus();
		}
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false)
				.applyTo(usernameField);

		new Label(composite, SWT.NONE).setText("&Password:");

		final Text passwordField = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				password = passwordField.getText();
			}
		});
		passwordField.setText(password);
		if (username.length() > 0) {
			passwordField.setFocus();
		}
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false)
				.applyTo(passwordField);

		final Button savePasswordButton = new Button(composite, SWT.CHECK);
		savePasswordButton.setText("&Save password");
		savePasswordButton.setSelection(savePassword);
		savePasswordButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				savePassword = savePasswordButton.getSelection();
			}
		});
		GridDataFactory.fillDefaults().span(2, 1).applyTo(savePasswordButton);

		createWarningMessage(composite);
	}

	private void createWarningMessage(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(composite);

		Label label = new Label(composite, SWT.NONE);
		label.setImage(getImage(DLG_IMG_MESSAGE_WARNING));
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));

		label = new Label(composite, SWT.WRAP);
		label.setText("Saved passwords are stored on your computer in a file that is difficult, but not impossible, for an intruder to read.");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(
				convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT).grab(true,
				false).applyTo(label);
	}

	@Override
	public boolean close() {
		if (keyLockImage != null) {
			keyLockImage.dispose();
		}
		return super.close();
	}

}
