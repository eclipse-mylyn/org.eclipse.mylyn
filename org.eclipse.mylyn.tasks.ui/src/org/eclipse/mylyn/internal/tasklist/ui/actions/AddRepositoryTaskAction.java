/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AddExistingTaskWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class AddRepositoryTaskAction extends Action implements IViewActionDelegate {
    
	private static final String WIZARD_LABEL = "Add an existing repository task/issue";

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		try {
			AddExistingTaskWizard wizard = new AddExistingTaskWizard();
			Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle(WIZARD_LABEL);
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Dialog.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
