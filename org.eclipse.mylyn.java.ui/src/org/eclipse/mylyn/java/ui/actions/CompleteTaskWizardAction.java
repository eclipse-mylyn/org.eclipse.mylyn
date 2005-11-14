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

package org.eclipse.mylar.java.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.mylar.java.ui.wizards.MylarCommitWizard;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class CompleteTaskWizardAction implements IViewActionDelegate {

	private static final String WIZARD_LABEL = "Commit Resources in Task Context";

	public void init(IViewPart view) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {
		ITask task = TaskListView.getDefault().getSelectedTask();
		if (!task.isActive()) {
			MessageDialog.openInformation(null, 
					"Mylar Information", 
					"This action can only be run on an active task.");
			return;
		}
		
		IResource[] resources = MylarJavaPlugin.getDefault().getChangeSetManager().getResources(task);
		if (resources == null || resources.length == 0){
			MessageDialog.openInformation(null, 
					"Mylar Information", 
					"There are no interesting resources in the active task context.");
			return;
		}
		
		try {
			MylarCommitWizard wizard = new MylarCommitWizard(resources, task);
			
			Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				wizard.loadSize();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.setMinimumPageSize(wizard.loadSize());
				dialog.create();
				dialog.setTitle(WIZARD_LABEL);
				dialog.setBlockOnOpen(true);
				if(dialog.open() == Dialog.CANCEL){
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarPlugin.fail(e, e.getMessage(), true);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
