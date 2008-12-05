/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Peter Stibrany - fix for parameter name (bug 247077)
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewRepositoryWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Displays a wizard dialog for adding a new task repository.
 * 
 * @author Willian Mitsuda
 * @author Steffen Pingel
 */
public class AddTaskRepositoryHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		if (shell != null && !shell.isDisposed()) {
			String connectorKind = event.getParameter("connectorKind"); //$NON-NLS-1$
			NewRepositoryWizard repositoryWizard = new NewRepositoryWizard(connectorKind);

			WizardDialog repositoryDialog = new WizardDialog(shell, repositoryWizard);
			repositoryDialog.create();
			repositoryDialog.getShell().setText(Messages.AddTaskRepositoryHandler_Add_Task_Repository);
			repositoryDialog.setBlockOnOpen(true);
			repositoryDialog.open();
			if (repositoryDialog.getReturnCode() == Window.OK) {
				return repositoryWizard.getTaskRepository();
			}
		}

		return null;
	}

}
