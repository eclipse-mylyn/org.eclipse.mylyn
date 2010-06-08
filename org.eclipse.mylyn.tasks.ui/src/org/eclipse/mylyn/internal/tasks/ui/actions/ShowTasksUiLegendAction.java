/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.UiLegendDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class ShowTasksUiLegendAction implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

	public void dispose() {
		// ignore
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		UiLegendDialog uiLegendDialog = new UiLegendDialog(WorkbenchUtil.getShell());
		uiLegendDialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	public void init(IViewPart view) {
	}
}
