/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Balazs Brinkus - bug 174473
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
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.ITaskCommandIds;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AddRepositoryAction extends Action {

	private static final String PREF_ADD_QUERY = "org.eclipse.mylyn.internal.tasks.add.query"; //$NON-NLS-1$

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.add"; //$NON-NLS-1$

	public static final String TITLE = Messages.AddRepositoryAction_Add_Task_Repository;

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
					TaskRepository repository = (TaskRepository) result;
					AbstractRepositoryConnector connector = TasksUiPlugin.getConnector(repository.getConnectorKind());
					if (connector != null && connector.canQuery(repository)) {
						promptToAddQuery(repository);
					}
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
			MessageDialogWithToggle messageDialog = MessageDialogWithToggle.openYesNoQuestion(shell,
					Messages.AddRepositoryAction_Add_new_query,
					Messages.AddRepositoryAction_Add_a_query_to_the_Task_List,
					Messages.AddRepositoryAction_Do_not_show_again, false, preferenceStore, PREF_ADD_QUERY);
			preferenceStore.setValue(PREF_ADD_QUERY, messageDialog.getToggleState());
			if (messageDialog.getReturnCode() == IDialogConstants.YES_ID) {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
				IWizard queryWizard = connectorUi.getQueryWizard(taskRepository, null);
				((Wizard) queryWizard).setForcePreviousAndNextButtons(true);

				WizardDialog queryDialog = new WizardDialog(shell, queryWizard);
				queryDialog.create();
				queryDialog.setBlockOnOpen(true);
				queryDialog.open();
			}
		}

	}

}
