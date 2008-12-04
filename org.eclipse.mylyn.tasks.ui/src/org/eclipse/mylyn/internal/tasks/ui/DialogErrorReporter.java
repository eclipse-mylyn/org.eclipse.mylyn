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
import org.eclipse.mylyn.commons.core.AbstractErrorReporter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class DialogErrorReporter extends AbstractErrorReporter {

	private boolean errorDialogOpen;

	@Override
	public int getPriority(IStatus status) {
		return AbstractErrorReporter.PRIORITY_LOW;
	}

	@Override
	public void handle(final IStatus status) {
		if (Platform.isRunning()) {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				Display display = workbench.getDisplay();
				if (display != null && !display.isDisposed()) {
					display.asyncExec(new Runnable() {
						public void run() {
							try {
								if (!errorDialogOpen) {
									errorDialogOpen = true;
									Shell shell = Display.getDefault().getActiveShell();
									ErrorDialog.openError(shell, Messages.DialogErrorReporter_Mylyn_Error, Messages.DialogErrorReporter_Please_report_the_following_error_at, status);
								}
							} finally {
								errorDialogOpen = false;
							}
						}
					});
				}
			}
		}
	}
}
