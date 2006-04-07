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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ContextAttachWizard;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class ContextAttachAction implements IViewActionDelegate {

	ISelection selection = null;

	IViewPart view = null;

	public void init(IViewPart view) {
		this.view = view;
	}

	public void run(IAction action) {
		ITask task = TaskListView.getDefault().getSelectedTask();
		if (task instanceof AbstractRepositoryTask) {
			try {
				if (!MylarPlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
					MessageDialog.openError(null, "Attach Context", "No context exists for selected task.");
					return;
				}
				
				ContextAttachWizard wizard = new ContextAttachWizard((AbstractRepositoryTask)task);
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				if (wizard != null && shell != null && !shell.isDisposed()) {
					WizardDialog dialog = new WizardDialog(shell, wizard);
					dialog.create();
					dialog.setTitle(ContextAttachWizard.WIZARD_TITLE);
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
