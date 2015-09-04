/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Peter Stibrany - fix for bug 247077
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.Messages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.google.common.base.Strings;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class NewRepositoryWizard extends Wizard implements INewWizard {

	private static final String PREF_ADD_QUERY = "org.eclipse.mylyn.internal.tasks.add.query"; //$NON-NLS-1$

	private AbstractRepositoryConnector connector;

	/**
	 * If not null, indicates that the wizard will initially jump to a specific connector page
	 */
	private final String connectorKind;

	private TaskRepository taskRepository;

	private SelectRepositoryConnectorPage selectConnectorPage;

	private ITaskRepositoryPage settingsPage;

	private String lastConnectorKind;

	private boolean showNewQueryPromptOnFinish;

	private String brand;

	public NewRepositoryWizard() {
		this(null);
	}

	public NewRepositoryWizard(String connectorKind) {
		this.connectorKind = connectorKind;
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		setForcePreviousAndNextButtons(connectorKind == null);
		setNeedsProgressMonitor(true);
		setWindowTitle(AddRepositoryAction.TITLE);
		setShowNewQueryPromptOnFinish(true);
	}

	@Override
	public void addPages() {
		if (connectorKind != null
				&& TasksUi.getRepositoryManager().getRepositoryConnector(connectorKind).canCreateRepository()) {
			connector = TasksUi.getRepositoryManager().getRepositoryConnector(connectorKind);
			updateSettingsPage();
			if (settingsPage != null) {
				addPage(settingsPage);
			}
		} else {
			selectConnectorPage = new SelectRepositoryConnectorPage();
			addPage(selectConnectorPage);
		}
	}

	@Override
	public boolean canFinish() {
		return (selectConnectorPage == null || selectConnectorPage.isPageComplete())
				&& !(getContainer().getCurrentPage() == selectConnectorPage) && settingsPage != null
				&& settingsPage.isPageComplete();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == selectConnectorPage) {
			connector = selectConnectorPage.getConnector();
			brand = selectConnectorPage.getBrand();
			updateSettingsPage();
			return settingsPage;
		}
		return super.getNextPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		if (canFinish()) {
			taskRepository = new TaskRepository(connector.getConnectorKind(), settingsPage.getRepositoryUrl());
			boolean finishAccepted = settingsPage.preFinish(taskRepository);
			if (finishAccepted) {
				settingsPage.performFinish(taskRepository);
				TasksUi.getRepositoryManager().addRepository(taskRepository);

				if (showNewQueryPromptOnFinish()) {
					if (connector.canQuery(taskRepository)) {
						promptToAddQuery(taskRepository);
					}
				}
				return true;
			}
		}
		return false;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	void updateSettingsPage() {
		assert connector != null;
		if (!connector.getConnectorKind().equals(lastConnectorKind)) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(connector.getConnectorKind());
			settingsPage = connectorUi.getSettingsPage(null);
			if (settingsPage == null) {
				TasksUiInternal.displayFrameworkError(NLS.bind(
						"The connector implementation is incomplete: AbstractRepositoryConnectorUi.getSettingsPage() for connector ''{0}'' returned null. Please contact the vendor of the connector to resolve the problem.", //$NON-NLS-1$
						connector.getConnectorKind()));
			}
			settingsPage.setWizard(this);
			lastConnectorKind = connector.getConnectorKind();
		}
		if (settingsPage instanceof AbstractRepositorySettingsPage) {
			((AbstractRepositorySettingsPage) settingsPage).setBrand(Strings.nullToEmpty(brand));
		}
	}

	public void promptToAddQuery(TaskRepository taskRepository) {
		IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		if (!preferenceStore.getBoolean(PREF_ADD_QUERY)) {
			MessageDialogWithToggle messageDialog = MessageDialogWithToggle.openYesNoQuestion(getShell(),
					Messages.AddRepositoryAction_Add_new_query,
					Messages.AddRepositoryAction_Add_a_query_to_the_Task_List,
					Messages.AddRepositoryAction_Do_not_show_again, false, preferenceStore, PREF_ADD_QUERY);
			preferenceStore.setValue(PREF_ADD_QUERY, messageDialog.getToggleState());
			if (messageDialog.getReturnCode() == IDialogConstants.YES_ID) {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin
						.getConnectorUi(taskRepository.getConnectorKind());
				final IWizard queryWizard = connectorUi.getQueryWizard(taskRepository, null);
				if (queryWizard instanceof Wizard) {
					((Wizard) queryWizard).setForcePreviousAndNextButtons(true);
				}

				// execute delayed to avoid stacking dialogs
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						WizardDialog queryDialog = new WizardDialog(WorkbenchUtil.getShell(), queryWizard);
						queryDialog.create();
						queryDialog.setBlockOnOpen(true);
						queryDialog.open();
					}
				});
			}
		}
	}

	public boolean showNewQueryPromptOnFinish() {
		return showNewQueryPromptOnFinish;
	}

	public void setShowNewQueryPromptOnFinish(boolean showNewQueryPromptOnFinish) {
		this.showNewQueryPromptOnFinish = showNewQueryPromptOnFinish;
	}

	@Deprecated
	public String getBrand() {
		return brand;
	}

}
