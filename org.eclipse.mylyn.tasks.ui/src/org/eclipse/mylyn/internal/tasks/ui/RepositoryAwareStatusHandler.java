/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.mylar.context.core.IStatusHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class RepositoryAwareStatusHandler implements IStatusHandler {

	protected static final String ERROR_MESSAGE = "Please report the following error by following the bugs link at:\n"
		+ "http://eclipse.org/mylar\n\n"
		+ "For details please use Window -> Show View -> Error Log";
	
	// TODO: implement option to report bug
	public void notify(final Status status, boolean informUser) {
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
}
