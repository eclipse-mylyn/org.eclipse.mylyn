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
import org.eclipse.mylar.tasklist.repositories.ITaskRepositoryClient;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class AddRepositoryWizard extends Wizard implements INewWizard {

	private SelectRepositoryWizardPage repositoryWizardPage;

	private ITaskRepositoryClient repositoryClient;

	public AddRepositoryWizard() {
		super();
		init();
	}

	@Override
	public boolean performFinish() {
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private void init() {
		repositoryWizardPage = new SelectRepositoryWizardPage(this);
		super.setForcePreviousAndNextButtons(true);
	}

	@Override
	public void addPages() {
		addPage(repositoryWizardPage);
	}

	public void setRepositoryClient(ITaskRepositoryClient repository) {
		this.repositoryClient = repository;
	}

	public ITaskRepositoryClient getRepositoryClient() {
		return repositoryClient;
	}
}
