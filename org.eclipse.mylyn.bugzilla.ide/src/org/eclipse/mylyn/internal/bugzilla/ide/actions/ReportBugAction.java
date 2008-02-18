/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.bugzilla.ide.wizards.ReportBugWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 */
public class ReportBugAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		WizardDialog dialog = new WizardDialog(window.getShell(), new ReportBugWizard());
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore		
	}
}
