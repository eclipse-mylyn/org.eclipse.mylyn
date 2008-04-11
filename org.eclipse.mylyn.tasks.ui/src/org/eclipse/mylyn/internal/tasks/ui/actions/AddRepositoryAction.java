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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.ITaskCommandIds;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Mik Kersten
 * @author Balazs Brinkus (bug 174473)
 * @author Steffen Pingel
 */
// API 3.0 rename to AddTaskRepositoryAction
public class AddRepositoryAction extends Action {

	private static final String PREF_ADD_QUERY = "org.eclipse.mylyn.internal.tasks.add.query";

	// TODO externalize and move to messages class
	public static final String TITLE = "Add Task Repository";

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.add";

	private boolean promptToAddQuery = true;

	public AddRepositoryAction() {
		setImageDescriptor(TasksUiImages.REPOSITORY_NEW);
		setText(TITLE);
		setId(ID);
		setEnabled(TasksUiPlugin.getRepositoryManager().hasUserManagedRepositoryConnectors());
	}

	public boolean getPromptToAddQuery() {
		return promptToAddQuery;
	}

	public void setPromptToAddQuery(boolean promptToAddQuery) {
		this.promptToAddQuery = promptToAddQuery;
	}

	@Override
	public void run() {
		showWizard();
	}

	public TaskRepository showWizard() {
		IHandlerService handlerSvc = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		try {
			Object result = handlerSvc.executeCommand(ITaskCommandIds.ADD_TASK_REPOSITORY, null);
			if (result instanceof TaskRepository) {
				if (getPromptToAddQuery()) {
					promptToAddQuery((TaskRepository) result);
				}
				return (TaskRepository) result;
			}
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
		}
		return null;
	}

	public void promptToAddQuery(TaskRepository taskRepository) {
		IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		if (!preferenceStore.getBoolean(PREF_ADD_QUERY)) {
			Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
			MessageDialogWithToggle messageDialog = MessageDialogWithToggle.openYesNoQuestion(shell, "Add new query",
					"Would you like to add a query to the Task List for this repository?", "Do not show again", false,
					preferenceStore, PREF_ADD_QUERY);
			preferenceStore.setValue(PREF_ADD_QUERY, messageDialog.getToggleState());
			if (messageDialog.getReturnCode() == IDialogConstants.YES_ID) {
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
