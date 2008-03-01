/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		if (shell != null && !shell.isDisposed()) {
			String param = event.getParameter("org.eclipse.mylyn.tasks.command.taskRepositoryId");
			NewRepositoryWizard repositoryWizard = new NewRepositoryWizard(param);

			WizardDialog repositoryDialog = new WizardDialog(shell, repositoryWizard);
			repositoryDialog.create();
			repositoryDialog.getShell().setText("Add Task Repository");
			repositoryDialog.setBlockOnOpen(true);
			repositoryDialog.open();
			if (repositoryDialog.getReturnCode() == Window.OK) {
				return repositoryWizard.getRepository();
			}
		}

		return null;
	}

}
