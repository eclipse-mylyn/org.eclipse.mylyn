/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.TaskRepositoryCredentialsDialog;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TaskRepositoryLocationUi extends TaskRepositoryLocation {

	private static Object lock = new Object();

	public TaskRepositoryLocationUi(TaskRepository taskRepository) {
		super(taskRepository);
	}

	@Override
	public void requestCredentials(AuthenticationType authType, String message, IProgressMonitor monitor)
			throws UnsupportedRequestException {
		if (CoreUtil.TEST_MODE) {
			throw new UnsupportedRequestException();
		}

		AuthenticationCredentials oldCredentials = taskRepository.getCredentials(authType);
		// synchronize on a static lock to ensure that only one password dialog is displayed at a time
		synchronized (lock) {
			// check if the credentials changed while the thread was waiting for the lock
			if (!areEqual(oldCredentials, taskRepository.getCredentials(authType))) {
				return;
			}

			if (Policy.isBackgroundMonitor(monitor)) {
				throw new UnsupportedRequestException();
			}

			PasswordRunner runner = new PasswordRunner(authType, message);
			PlatformUI.getWorkbench().getDisplay().syncExec(runner);
			if (runner.isCanceled()) {
				throw new OperationCanceledException();
			}
			if (!runner.isChanged()) {
				throw new UnsupportedRequestException();
			}
		}
	}

	private boolean areEqual(AuthenticationCredentials oldCredentials, AuthenticationCredentials credentials) {
		if (oldCredentials == null) {
			return (credentials == null);
		} else {
			return oldCredentials.equals(credentials);
		}
	}

	private class PasswordRunner implements Runnable {

		private final AuthenticationType authType;

		private boolean changed;

		private final String message;

		private boolean canceled;

		public PasswordRunner(AuthenticationType credentialType, String message) {
			this.authType = credentialType;
			this.message = message;
		}

		public boolean isChanged() {
			return changed;
		}

		public boolean isCanceled() {
			return canceled;
		}

		public void run() {
			Shell shell = Display.getCurrent().getActiveShell();
			if (shell != null && !shell.isDisposed()) {
				TaskRepositoryCredentialsDialog dialog = TaskRepositoryCredentialsDialog.createDialog(shell);
				initializeDialog(dialog);
				int resultCode = dialog.open();
				if (resultCode == Window.OK) {
					apply(dialog);
					changed = true;
				} else if (resultCode == TaskRepositoryCredentialsDialog.TASK_REPOSITORY_CHANGED) {
					changed = true;
				} else {
					canceled = true;
				}
			}
		}

		private void initializeDialog(TaskRepositoryCredentialsDialog dialog) {
			dialog.setTaskRepository(taskRepository);

			AuthenticationCredentials credentials = taskRepository.getCredentials(authType);
			if (credentials != null) {
				dialog.setUsername(credentials.getUserName());
				dialog.setPassword(credentials.getPassword());
			}

			// caller provided message takes precedence
			if (message != null) {
				dialog.setMessage(message);
			} else {
				dialog.setMessage(getDefaultMessage());
			}
		}

		private String getDefaultMessage() {
			if (AuthenticationType.REPOSITORY.equals(authType)) {
				return "Enter repository password";
			} else if (AuthenticationType.HTTP.equals(authType)) {
				return "Enter HTTP password";
			} else if (AuthenticationType.PROXY.equals(authType)) {
				return "Enter proxy password";
			}
			return null;
		}

		private void apply(TaskRepositoryCredentialsDialog dialog) {
			AuthenticationCredentials credentials = new AuthenticationCredentials(dialog.getUserName(),
					dialog.getPassword());
			taskRepository.setCredentials(authType, credentials, dialog.getSavePassword());
			TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(taskRepository);
		}
	}

}
