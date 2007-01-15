/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.IStatusHandler;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.util.WebBrowserDialog;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class RepositoryAwareStatusHandler implements IStatusHandler {

	protected static final String ERROR_MESSAGE = "Please report the following error by following the bugs link at:\n"
			+ "http://eclipse.org/mylar\n\n" + "For details please use Window -> Show View -> Error Log";

	// TODO: implement option to report bug
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
						ErrorDialog.openError(shell, "Mylar Error", ERROR_MESSAGE, status);
					}
				});
			} catch (Throwable t) {
				status.getException().printStackTrace();
			}
		}
	}

	public void displayStatus(final String title, final IStatus status) {
		if (Platform.isRunning()) {
			try {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						Shell shell = null;
						if (PlatformUI.getWorkbench() != null
								&& PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
							shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						}

						if (status.getCode() == IMylarStatusConstants.REPOSITORY_ERROR_HTML) {
							WebBrowserDialog.openAcceptAgreement(shell, title, "", status.getMessage());
							MylarStatusHandler.log(status);
							return;
						}
						switch (status.getSeverity()) {
						case IStatus.CANCEL:
						case IStatus.INFO:
							MessageDialog.openInformation(shell, title, status.getMessage());
							break;
						case IStatus.WARNING:
							MessageDialog.openWarning(shell, title, status.getMessage());
							break;
						case IStatus.ERROR:
						default:
							MessageDialog.openError(shell, title, status.getMessage());
							break;
						}
					}
				});
			} catch (Throwable t) {
				status.getException().printStackTrace();
			}
		}
	}
}
