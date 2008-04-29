/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.core.IStatusHandler;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.WebBrowserDialog;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class RepositoryAwareStatusHandler implements IStatusHandler {

	protected static final String ERROR_MESSAGE = "Please report the following error at:\n"
			+ "http://bugs.eclipse.org/bugs/enter_bug.cgi?product=Mylyn\n\n"
			+ "Or via the popup menu in the Error Log view (see Window -> Show View)";

	/**
	 * Used to ensure that only one dialog is open.
	 */
	private static boolean errorDialogOpen = false;

	private static RepositoryAwareStatusHandler instance;

	public synchronized static RepositoryAwareStatusHandler getInstance() {
		if (instance == null) {
			new RepositoryAwareStatusHandler();
		}
		return instance;
	}

	public RepositoryAwareStatusHandler() {
		instance = this;
	}

	private MessageDialog createDialog(Shell shell, String title, String message, int type) {
		return new MessageDialog(shell, title, null, message, type, new String[] { IDialogConstants.OK_LABEL }, 0);
	}

	public void displayStatus(final String title, final IStatus status) {

		if (status.getCode() == RepositoryStatus.ERROR_INTERNAL) {
			StatusHandler.log(status);
			fail(status, true);
			return;
		}

		if (Platform.isRunning()) {
			try {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						Shell shell = null;
						if (PlatformUI.getWorkbench() != null
								&& PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
							shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						}

						if (status instanceof RepositoryStatus && ((RepositoryStatus) status).isHtmlMessage()) {
							WebBrowserDialog.openAcceptAgreement(shell, title, status.getMessage(),
									((RepositoryStatus) status).getHtmlMessage());
							return;
						}

						switch (status.getSeverity()) {
						case IStatus.CANCEL:
						case IStatus.INFO:
							createDialog(shell, title, status.getMessage(), MessageDialog.INFORMATION).open();
							break;
						case IStatus.WARNING:
							createDialog(shell, title, status.getMessage(), MessageDialog.WARNING).open();
							break;
						case IStatus.ERROR:
						default:
							createDialog(shell, title, status.getMessage(), MessageDialog.ERROR).open();
							break;
						}
					}
				});
			} catch (Throwable t) {
				status.getException().printStackTrace();
			}
		}
	}

	public void fail(final IStatus status, boolean informUser) {
		if (informUser && Platform.isRunning()) {
			try {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						Shell shell = null;
						if (PlatformUI.getWorkbench() != null
								&& PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
							shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						}

						// ensure that only one dialog can be open at a time
						synchronized (shell) {
							try {
								if (!errorDialogOpen) {
									errorDialogOpen = true;
									ErrorDialog.openError(shell, "Mylyn Error", ERROR_MESSAGE, status);
								}
							} finally {
								errorDialogOpen = false;
							}
						}
					}
				});
			} catch (Throwable t) {
				status.getException().printStackTrace();
			}
		}
	}

}
