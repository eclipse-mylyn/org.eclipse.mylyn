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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.ui.wizards.NewRepositoryTaskWizard;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class NewRepositoryTaskAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylar.tasklist.ui.repositories.actions.create";
			
	@Override
	public void run() {

		boolean offline = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
		if (offline) {
			MessageDialog.openInformation(null, "Unable to create bug report",
					"Unable to create a new bug report since you are currently offline");
			return;
		}
		 
//		IWizard wizard = new MultiRepositoryAwareWizard(new NewRepositoryTaskPage(connectorKinds), TITLE);
		IWizard wizard = new NewRepositoryTaskWizard();
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (wizard != null && shell != null && !shell.isDisposed()) {

			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.setBlockOnOpen(true);
			dialog.open();

		} else {
			// ignore
		}
	}

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
