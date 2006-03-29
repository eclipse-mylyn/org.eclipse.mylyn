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

package org.eclipse.mylar.internal.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ContextRetrieveWizard;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class ContextRetrieveAction implements IViewActionDelegate {
	
	ISelection selection = null;
	
	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (selection.isEmpty())
			return;
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			Object obj = treeSelection.getFirstElement();
			AbstractRepositoryTask task = null;
			if (obj instanceof AbstractRepositoryTask) {
				task = (AbstractRepositoryTask) obj;
			} else if (obj instanceof AbstractQueryHit) {
				AbstractQueryHit hit = (AbstractQueryHit) obj;
				task = hit.getCorrespondingTask();
			}

			try {
				ContextRetrieveWizard wizard = new ContextRetrieveWizard(task);
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				if (wizard != null && shell != null && !shell.isDisposed()) {
					WizardDialog dialog = new WizardDialog(shell, wizard);
					dialog.create();
					dialog.setTitle(ContextRetrieveWizard.WIZARD_TITLE);
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
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
