/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewRepositoryWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Handles the "add task repository" command
 * 
 * @author Willian Mitsuda
 */
public class AddTaskRepositoryHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String param = event.getParameter("org.eclipse.mylyn.tasks.command.taskRepositoryId");
		NewRepositoryWizard wizard = new NewRepositoryWizard(param);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.create();
			dialog.getShell().setText("Add Task Repository");
			dialog.setBlockOnOpen(true);
			dialog.open();
		}
		return null;
	}

}
