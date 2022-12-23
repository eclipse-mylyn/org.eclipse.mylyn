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
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.LocalRepositoryConnectorUi;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewQueryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class NewQueryAction extends Action implements IViewActionDelegate, IExecutableExtension {

	private final String ID = "org.eclipse.mylyn.tasks.ui.new.query"; //$NON-NLS-1$

	private final String LABEL_NEW_QUERY = Messages.NewQueryAction_new_query_;

	private boolean skipRepositoryPage;

	public NewQueryAction() {
		setText(LABEL_NEW_QUERY);
		setToolTipText(LABEL_NEW_QUERY);
		setId(ID);
		setImageDescriptor(TasksUiImages.QUERY_NEW);
	}

	public void run(IAction action) {
		run();
	}

	@Override
	public void run() {
		IWizard wizard = null;
		/* Disabled for bug 275204 to make it more simple to discover ui for installing additional connectors
		List<TaskRepository> repositories = TasksUi.getRepositoryManager().getAllRepositories();
		if (repositories.size() == 2) {
			// NOTE: this click-saving should be generalized
			for (TaskRepository taskRepository : repositories) {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
				if (!(connectorUi instanceof LocalRepositoryConnectorUi)) {
					wizard = connectorUi.getQueryWizard(taskRepository, null);
					if (wizard == null) {
						continue;
					}
					((Wizard) wizard).setForcePreviousAndNextButtons(true);
				}
			}
		} else */
		if (skipRepositoryPage) {
			TaskRepository taskRepository = TasksUiUtil.getSelectedRepository();
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
			wizard = connectorUi.getQueryWizard(taskRepository, null);
			if (wizard instanceof Wizard) {
				((Wizard) wizard).setForcePreviousAndNextButtons(true);
			}
			if (connectorUi instanceof LocalRepositoryConnectorUi) {
				wizard.performFinish();
				return;
			}
		} else {
			wizard = new NewQueryWizard();
		}

		WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
		dialog.setHelpAvailable(false);
		dialog.create();
		dialog.setBlockOnOpen(true);
		dialog.open();
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		if ("skipFirstPage".equals(data)) { //$NON-NLS-1$
			this.skipRepositoryPage = true;
		}
	}

	public void init(IViewPart view) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
