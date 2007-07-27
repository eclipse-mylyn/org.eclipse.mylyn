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
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewRepositoryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Handles the "add task repository" command
 * 
 * @author Willian Mitsuda
 * @author Balazs Brinkus (bug 174473)
 */
public class AddTaskRepositoryHandler extends AbstractHandler {

	private static final String PREF_ADD_QUERY = "org.eclipse.mylyn.internal.tasks.add.query";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String param = event.getParameter("org.eclipse.mylyn.tasks.command.taskRepositoryId");

		NewRepositoryWizard repositoryWizard = new NewRepositoryWizard(param);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			WizardDialog repositoryDialog = new WizardDialog(shell, repositoryWizard);
			repositoryDialog.create();
			repositoryDialog.getShell().setText("Add Task Repository");
			repositoryDialog.setBlockOnOpen(true);
			repositoryDialog.open();

			if (repositoryDialog.getReturnCode() == Window.OK) {

				IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();

				if (!preferenceStore.getBoolean(PREF_ADD_QUERY)) {
					MessageDialogWithToggle messageDialog = MessageDialogWithToggle.openYesNoQuestion(
							PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Add new query",
							"Would you like to add a query to the Task List for this repository?", "Do not show again",
							false, preferenceStore, PREF_ADD_QUERY);
					preferenceStore.setValue(PREF_ADD_QUERY, messageDialog.getToggleState());

					if (messageDialog.getReturnCode() == 2) {
						TaskRepository taskRepository = repositoryWizard.getRepository();

						AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
						IWizard queryWizard = connectorUi.getQueryWizard(taskRepository, null);
						((Wizard) queryWizard).setForcePreviousAndNextButtons(true);

						WizardDialog queryDialog = new WizardDialog(shell, queryWizard);
						queryDialog.create();
						queryDialog.setTitle("Add Repository Query");
						queryDialog.setBlockOnOpen(true);
						queryDialog.open();
					}
				}
			}

		}

		return null;
	}
}
