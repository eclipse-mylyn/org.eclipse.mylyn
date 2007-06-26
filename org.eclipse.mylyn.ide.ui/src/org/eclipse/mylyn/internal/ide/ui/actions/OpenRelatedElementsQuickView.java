/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.views.ActiveSearchQuickView;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class OpenRelatedElementsQuickView implements IWorkbenchWindowActionDelegate {

	private ActiveSearchQuickView inplaceDialog;

	public void dispose() {
		inplaceDialog = null;
	}

	public void init(IWorkbenchWindow window) {
		// don't have anything to initialize
	}

	public void run(IAction action) {
		IInteractionElement activeNode = ContextCorePlugin.getContextManager().getActiveElement();

		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		inplaceDialog = new ActiveSearchQuickView(parent);
		// inplaceDialog.setLastSelection(XRefUIUtils.getCurrentSelection());
		inplaceDialog.setWorkbenchPart(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getActivePart());
		inplaceDialog.open(activeNode);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Have selected something in the editor - therefore
		// want to close the inplace view if haven't already done so
		if (inplaceDialog != null && inplaceDialog.isOpen()) {
			inplaceDialog.dispose();
			inplaceDialog = null;
		}
	}

}
