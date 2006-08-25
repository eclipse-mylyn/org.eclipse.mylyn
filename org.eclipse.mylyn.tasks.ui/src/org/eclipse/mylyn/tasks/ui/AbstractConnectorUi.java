/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasks.ui.wizards.CommonAddExistingTaskWizard;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public abstract class AbstractConnectorUi {
	
	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getRepositoryType();
		
	public abstract AbstractRepositorySettingsPage getSettingsPage();

	public abstract IWizard getNewQueryWizard(TaskRepository repository, IStructuredSelection selection);

	public abstract void openEditQueryDialog(AbstractRepositoryQuery query);

	public abstract boolean hasRichEditor();
	
	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection);
	
	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		return new CommonAddExistingTaskWizard(repository);
	}

	public boolean hasSearchPage() {
		return false;
	}

	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return null;
	}

	public void openRemoteTask(String repositoryUrl, String idString) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				TasksUiPlugin.TITLE_DIALOG, "Not supported by connector: " + this.getClass().getSimpleName());
	}
}
