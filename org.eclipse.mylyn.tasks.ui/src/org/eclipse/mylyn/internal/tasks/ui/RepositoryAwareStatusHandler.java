/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class RepositoryAwareStatusHandler {

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

	@Deprecated
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
