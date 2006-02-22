/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class AddRepositoryWizard extends AbstractRepositoryClientWizard {

	// private AbstractRepositorySettingsPage abstractRepositorySettingsPage;//
	// = new AbstractRepositorySettingsPage();

	public AddRepositoryWizard() {
		super();
		super.setForcePreviousAndNextButtons(true);
	}

	@Override
	public boolean performFinish() {
		if (canFinish()) {
			TaskRepository repository = new TaskRepository(repositoryClient.getRepositoryType(),
					super.abstractRepositorySettingsPage.getServerUrl());
			if (repository != null) {
				repository.setAuthenticationCredentials(abstractRepositorySettingsPage.getUserName(),
						abstractRepositorySettingsPage.getPassword());
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
		super.addPages();
		// addPage(abstractRepositorySettingsPage);
	}

	public AbstractRepositorySettingsPage getRepositorySettingsPage() {
		return abstractRepositorySettingsPage;
	}

	public void setRepositorySettingsPage(AbstractRepositorySettingsPage abstractRepositorySettingsPage) {
		this.abstractRepositorySettingsPage = abstractRepositorySettingsPage;
	}

	@Override
	public boolean canFinish() {
		return super.canFinish() && abstractRepositorySettingsPage != null
				&& abstractRepositorySettingsPage.isPageComplete();
	}
}
