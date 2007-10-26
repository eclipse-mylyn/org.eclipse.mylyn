/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.EditCredentialsDialog;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TaskRepositoryLocationUi extends TaskRepositoryLocation {

	public TaskRepositoryLocationUi(TaskRepository taskRepository) {
		super(taskRepository);
	}

	public ResultType requestCredentials(final String authType, String message) {
		PasswordRunner runner = new PasswordRunner(authType, message);
		PlatformUI.getWorkbench().getDisplay().syncExec(runner);
		if (runner.isCancelled()) {
			throw new OperationCanceledException();
		}
		return runner.getResult();
	}

	private class PasswordRunner implements Runnable {

		private final String authType;

		private boolean cancelled;

		private ResultType result;

		private final String message;

		public PasswordRunner(String credentialType, String message) {
			this.authType = credentialType;
			this.message = message;
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public ResultType getResult() {
			return result;
		}

		public void run() {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (shell != null && !shell.isDisposed()) {
				EditCredentialsDialog dialog = new EditCredentialsDialog(shell);
				initializeDialog(dialog);
				if (dialog.open() == Dialog.OK) {
					saveDialog(dialog);
					result = ResultType.CREDENTIALS_CHANGED;
					cancelled = false;
				} else {
					cancelled = true;
				}
			}
		}

		private void initializeDialog(EditCredentialsDialog dialog) {
			dialog.setUrl(taskRepository.getRepositoryLabel());

			String username = taskRepository.getUserName(authType);
			if (username != null) {
				dialog.setUsername(taskRepository.getUserName());
			}

			String password = taskRepository.getPassword(authType);
			if (password != null) {
				dialog.setPassword(password);
			}

			// caller provided message takes precedence
			if (message != null) {
				dialog.setMessage(message);
			} else {
				dialog.setMessage(getDefaultMessage());
			}
		}

		private String getDefaultMessage() {
			if (TaskRepository.AUTH_DEFAULT.equals(authType)) {
				return "Please enter repository password";
			} else if (TaskRepository.AUTH_HTTP.equals(authType)) {
				return "Please enter HTTP password";
			} else if (TaskRepository.AUTH_PROXY.equals(authType)) {
				return "Please enter proxy password";
			}
			return null;
		}

		private void saveDialog(EditCredentialsDialog dialog) {
			if (TaskRepository.AUTH_DEFAULT.equals(authType)) {
				taskRepository.setAnonymous(false);
			}

			taskRepository.setSavePassword(authType, dialog.getSavePassword());
			taskRepository.setCredentials(authType, dialog.getUserName(), dialog.getPassword());

			TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(taskRepository);
			TasksUiPlugin.getRepositoryManager().saveRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		}
	}

}
