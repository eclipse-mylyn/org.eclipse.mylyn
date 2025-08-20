/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     BREDEX GmbH - fix for bug 295050
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
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta.Type;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.TaskRepositoryCredentialsDialog;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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
			if (!PlatformUI.getWorkbench().getDisplay().isDisposed()) {
				PlatformUI.getWorkbench().getDisplay().syncExec(runner);
				if (runner.isCanceled()) {
					throw new OperationCanceledException();
				}
				if (!runner.isChanged()) {
					throw new UnsupportedRequestException();
				}
			} else {
				throw new UnsupportedRequestException();
			}
		}
	}

	private boolean areEqual(AuthenticationCredentials oldCredentials, AuthenticationCredentials credentials) {
		if (oldCredentials == null) {
			return credentials == null;
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
			authType = credentialType;
			this.message = message;
		}

		public boolean isChanged() {
			return changed;
		}

		public boolean isCanceled() {
			return canceled;
		}

		@Override
		public void run() {
			//Shell shell = Display.getCurrent().getActiveShell();
			Shell shell = WorkbenchUtil.getShell();
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
			dialog.setFileDialog(AuthenticationType.CERTIFICATE.equals(authType));
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
				return Messages.TaskRepositoryLocationUi_Enter_repository_password;
			} else if (AuthenticationType.CERTIFICATE.equals(authType)) {
				return Messages.TaskRepositoryLocationUi_Enter_CLIENTCERTIFICATE_password;
			} else if (AuthenticationType.HTTP.equals(authType)) {
				return Messages.TaskRepositoryLocationUi_Enter_HTTP_password;
			} else if (AuthenticationType.PROXY.equals(authType)) {
				return Messages.TaskRepositoryLocationUi_Enter_proxy_password;
			}
			return null;
		}

		private void apply(TaskRepositoryCredentialsDialog dialog) {
			AuthenticationCredentials credentials = new AuthenticationCredentials(dialog.getUserName(),
					dialog.getPassword());
			taskRepository.setCredentials(authType, credentials, dialog.getSavePassword());
			TasksUiPlugin.getRepositoryManager()
					.notifyRepositorySettingsChanged(taskRepository,
							new TaskRepositoryDelta(Type.CREDENTIALS, authType));
		}
	}

}
