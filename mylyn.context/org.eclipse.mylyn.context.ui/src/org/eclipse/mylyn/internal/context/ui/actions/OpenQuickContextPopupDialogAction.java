/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.context.ui.views.QuickContextPopupDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Class to activate the inplace Cross Reference view, via the key binding defined in the plugin.xml.
 * 
 * @author Mik Kersten
 */
public class OpenQuickContextPopupDialogAction implements IWorkbenchWindowActionDelegate {

	private QuickContextPopupDialog inplaceDialog;

	@Override
	public void dispose() {
		inplaceDialog = null;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// ignore
	}

	@Override
	public void run(IAction action) {
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		inplaceDialog = new QuickContextPopupDialog(parent);
		inplaceDialog.open();
		inplaceDialog.setFocus();
		// inplaceDialog.setLastSelection(XRefUIUtils.getCurrentSelection());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
