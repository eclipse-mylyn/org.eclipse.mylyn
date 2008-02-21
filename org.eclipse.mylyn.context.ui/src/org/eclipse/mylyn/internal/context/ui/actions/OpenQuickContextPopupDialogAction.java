/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public void dispose() {
		inplaceDialog = null;
	}

	public void init(IWorkbenchWindow window) {
		// ignore
	}

	public void run(IAction action) {
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		inplaceDialog = new QuickContextPopupDialog(parent);
		inplaceDialog.open();
		inplaceDialog.setFocus();
		// inplaceDialog.setLastSelection(XRefUIUtils.getCurrentSelection());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
