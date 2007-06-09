/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.help.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.help.ui.dialogs.UiLegendDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ShowMylarLegendAction implements IWorkbenchWindowActionDelegate {

		public void dispose() {
		// ignore
	}

	public void init(IWorkbenchWindow window) {
		// ignore
	}

	public void run(IAction action) {
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		UiLegendDialog uiLegendDialog = new UiLegendDialog(parentShell);
		uiLegendDialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
