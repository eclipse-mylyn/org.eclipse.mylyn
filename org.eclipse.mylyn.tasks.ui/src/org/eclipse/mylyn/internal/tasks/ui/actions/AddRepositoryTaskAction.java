/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.AddExistingTaskWizard;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class AddRepositoryTaskAction extends Action implements IViewActionDelegate {

	private static final String WIZARD_LABEL = "Add an existing repository task/issue";

	public void run(IAction action) {
		try {
			AddExistingTaskWizard wizard = new AddExistingTaskWizard();
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle(WIZARD_LABEL);
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
		}
	}

	public void init(IViewPart view) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
