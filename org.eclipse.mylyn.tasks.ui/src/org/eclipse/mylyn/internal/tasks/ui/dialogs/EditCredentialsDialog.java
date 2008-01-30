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
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class EditCredentialsDialog extends TitleAreaDialog {

	private static final String MESSAGE = "Please enter the repository credentials";

	private static final String TITLE = "Authentication Failed";

	public static EditCredentialsDialog createDialog(Shell shell) {
		return new EditCredentialsDialog(shell);
	}

	private Image keyLockImage;

	private String message;

	private String password = "";

	private boolean savePassword;

	private TaskRepository taskRepository;

	private String username = "";

	private EditCredentialsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public boolean close() {
		if (keyLockImage != null) {
			keyLockImage.dispose();
		}
		return super.close();
	}

	private void createButtonArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// spacer
		new Label(composite, SWT.NONE);

		Label label = new Label(composite, SWT.WRAP);
		label.setText("To disable background synchronization put the repository in disconnected mode.");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(label);

		Button disconnectButton = new Button(composite, SWT.PUSH);
		disconnectButton.setText("Disconnect Repository");
		disconnectButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				taskRepository.setOffline(true);
				setReturnCode(Window.CANCEL);
				close();
			}
		});
	}

	private void createCenterArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (taskRepository != null) {
			Label label = new Label(composite, SWT.NONE);
			label.setText("Task Repository:");

			ImageHyperlink repositoryHyperlink = new ImageHyperlink(composite, SWT.NONE);
			repositoryHyperlink.setText(taskRepository.getRepositoryLabel());
			repositoryHyperlink.setToolTipText("Open Repository Properties");
			repositoryHyperlink.setImage(TasksUiPlugin.getDefault().getBrandingIcon(taskRepository.getConnectorKind()));
			repositoryHyperlink.setBackground(composite.getBackground());
			repositoryHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					close();
					int returnCode = TasksUiUtil.openEditRepositoryWizard(taskRepository);
					setReturnCode(returnCode);
				}
			});
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

	@Override
	protected Control createContents(Composite parent) {
		getShell().setText("Enter Credentials");

		Control control = super.createContents(parent);
		setTitle(TITLE);
		keyLockImage = AbstractUIPlugin.imageDescriptorFromPlugin(TasksUiPlugin.ID_PLUGIN, "icons/wizban/keylock.gif")
				.createImage();
		setTitleImage(keyLockImage);
		if (message != null) {
			super.setMessage(message);
		} else {
			super.setMessage(MESSAGE);
		}
		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parent2 = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parent2, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(composite);

		createCenterArea(composite);

		if (taskRepository != null) {
			createButtonArea(composite);
		}

		composite.pack();
		return parent;

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
		label.setImage(Dialog.getImage(DLG_IMG_MESSAGE_WARNING));
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));

		label = new Label(composite, SWT.WRAP);
		label.setText("Saved passwords are stored on your computer in a file that is difficult, but not impossible, for an intruder to read.");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(
				convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT).grab(true,
				false).applyTo(label);
	}

	public String getMessage() {
		return message;
	}

	public String getPassword() {
		return password;
	}

	public boolean getSavePassword() {
		return savePassword;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public String getUserName() {
		return username;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	public void setPassword(String password) {
		if (password == null) {
			throw new IllegalArgumentException();
		}
		this.password = password;
	}

	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}

	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public void setUsername(String username) {
		if (username == null) {
			throw new IllegalArgumentException();
		}
		this.username = username;
	}

}
