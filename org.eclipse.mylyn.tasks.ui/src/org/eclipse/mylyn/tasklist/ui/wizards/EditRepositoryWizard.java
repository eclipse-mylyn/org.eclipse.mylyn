/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.repositories.TaskRepository;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class EditRepositoryWizard extends Wizard implements INewWizard {

	private RepositorySettingsPage repositorySettingsPage = new RepositorySettingsPage();
	
	public EditRepositoryWizard(TaskRepository repository) {
		super();
//		super.setForcePreviousAndNextButtons(true);
		repositorySettingsPage.setRepository(repository);
		repositorySettingsPage.setWizard(this);
	}

	@Override
	public boolean performFinish() {
		if (canFinish()) {
			TaskRepository repository = new TaskRepository(repositorySettingsPage.getServerUrl());
			if (repository != null) {
				repository.setAuthenticationCredentials(repositorySettingsPage.getUserName(), repositorySettingsPage.getPassword());
				MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
				return true;
			} 
		} 
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		addPage(repositorySettingsPage);
	}

	@Override
	public boolean canFinish() {
		return repositorySettingsPage.isPageComplete();
	}
}
