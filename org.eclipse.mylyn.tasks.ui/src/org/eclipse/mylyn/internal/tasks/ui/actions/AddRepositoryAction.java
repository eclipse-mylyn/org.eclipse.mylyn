/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Balazs Brinkus - bug 174473
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewRepositoryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.TaskRepositoryWizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author David Green
 */
public class AddRepositoryAction extends Action {

	@Deprecated
	private static final String PREF_ADD_QUERY = "org.eclipse.mylyn.internal.tasks.add.query"; //$NON-NLS-1$

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.add"; //$NON-NLS-1$

	public static final String TITLE = Messages.AddRepositoryAction_Add_Task_Repository;

	private boolean promptToAddQuery = true;

	public AddRepositoryAction() {
		setImageDescriptor(TasksUiImages.REPOSITORY_NEW);
		setText(TITLE);
		setId(ID);
		boolean enabled = TasksUiPlugin.getRepositoryManager().hasUserManagedRepositoryConnectors();
		if (!enabled) {
			// bug 279054 enable the action if connector discovery is present/enabled
			Command command = TasksUiInternal.getConfiguredDiscoveryWizardCommand();
			enabled = command != null && command.isEnabled();
		}
		setEnabled(enabled);
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
		return showWizard(WorkbenchUtil.getShell(), null);
	}

	public TaskRepository showWizard(Shell shell, String connectorKind) {
		NewRepositoryWizard repositoryWizard = new NewRepositoryWizard(connectorKind);
		repositoryWizard.setShowNewQueryPromptOnFinish(getPromptToAddQuery());

		WizardDialog dialog = new TaskRepositoryWizardDialog(shell, repositoryWizard);
		dialog.create();
		dialog.setBlockOnOpen(true);
		dialog.open();

		if (dialog.getReturnCode() == Window.OK) {
			return repositoryWizard.getTaskRepository();
		}
		return null;
	}

	@Deprecated
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
				if (queryWizard instanceof Wizard) {
					((Wizard) queryWizard).setForcePreviousAndNextButtons(true);
				}

				WizardDialog queryDialog = new WizardDialog(shell, queryWizard);
				queryDialog.create();
				queryDialog.setBlockOnOpen(true);
				queryDialog.open();
			}
		}

	}

}
