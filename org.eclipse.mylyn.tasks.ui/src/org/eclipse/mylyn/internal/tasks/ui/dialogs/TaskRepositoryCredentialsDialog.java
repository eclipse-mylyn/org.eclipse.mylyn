/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class TaskRepositoryCredentialsDialog extends TitleAreaDialog {

	private static final String DIALOG_TITLE = Messages.TaskRepositoryCredentialsDialog_Enter_Credentials;

	private static final String IMAGE_FILE_KEYLOCK = "icons/wizban/secur_role_wiz.gif"; //$NON-NLS-1$

	public static final int TASK_REPOSITORY_CHANGED = 1000;

	private static final String MESSAGE = Messages.TaskRepositoryCredentialsDialog_Enter_repository_credentials;

	private static final String TITLE = Messages.TaskRepositoryCredentialsDialog_Repository_Authentication;

	public static TaskRepositoryCredentialsDialog createDialog(Shell shell) {
		return new TaskRepositoryCredentialsDialog(shell);
	}

	private Image keyLockImage;

	private String message;

	private String password = ""; //$NON-NLS-1$

	private boolean savePassword;

	private TaskRepository taskRepository;

	private String username = ""; //$NON-NLS-1$

	private TaskRepositoryCredentialsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public boolean close() {
		if (keyLockImage != null) {
			keyLockImage.dispose();
		}
		return super.close();
	}

	private void createLinkArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Link link = new Link(composite, SWT.WRAP);
		link.setText(Messages.TaskRepositoryCredentialsDialog_HTML_Open_Repository_Properties);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
				int returnCode = TasksUiUtil.openEditRepositoryWizard(taskRepository);
				if (returnCode == Window.OK) {
					setReturnCode(TASK_REPOSITORY_CHANGED);
				} else {
					setReturnCode(returnCode);
				}
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(
				convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT).grab(true,
				false).applyTo(link);
	}

	private void createCenterArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (taskRepository != null) {
			Composite labelComposite = new Composite(composite, SWT.NONE);
			GridLayout layout = new GridLayout(3, false);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			labelComposite.setLayout(layout);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(
					labelComposite);

			Label label = new Label(labelComposite, SWT.NONE);
			label.setImage(TasksUiPlugin.getDefault().getBrandingIcon(taskRepository.getConnectorKind()));

			label = new Label(labelComposite, SWT.NONE);
			label.setText(Messages.TaskRepositoryCredentialsDialog_Task_Repository);

			label = new Label(labelComposite, SWT.NONE);
			label.setText(taskRepository.getRepositoryLabel());
		}

		new Label(composite, SWT.NONE).setText(Messages.TaskRepositoryCredentialsDialog_User_ID);

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

		new Label(composite, SWT.NONE).setText(Messages.TaskRepositoryCredentialsDialog_Password);

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
		savePasswordButton.setText(Messages.TaskRepositoryCredentialsDialog_Save_Password);
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
		getShell().setText(DIALOG_TITLE);

		setTitle(TITLE);
		Control control = super.createContents(parent);
		if (taskRepository != null) {
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					taskRepository.getConnectorKind());
			if (connector != null) {
				setTitle(connector.getShortLabel() + " " + TITLE); //$NON-NLS-1$
			}
		}

		ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(TasksUiPlugin.ID_PLUGIN,
				IMAGE_FILE_KEYLOCK);
		if (descriptor != null) {
			keyLockImage = descriptor.createImage();
			setTitleImage(keyLockImage);
		}
		if (message != null) {
			super.setMessage(message);
		} else {
			super.setMessage(MESSAGE);
		}
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
		if (taskRepository != null) {
			createLinkArea(composite);
		}

		composite.pack();
		return parent;
	}

	private void createWarningMessage(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(composite);

		Label label = new Label(composite, SWT.NONE);
		label.setImage(Dialog.getImage(DLG_IMG_MESSAGE_WARNING));
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));

		label = new Label(composite, SWT.WRAP);
		label.setText(Messages.TaskRepositoryCredentialsDialog_Saved_passwords_are_stored_that_is_difficult);
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
