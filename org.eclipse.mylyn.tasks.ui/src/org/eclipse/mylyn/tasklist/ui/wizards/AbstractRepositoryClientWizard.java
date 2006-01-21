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

package org.eclipse.mylar.tasklist.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.tasklist.ITaskRepositoryClient;
import org.eclipse.ui.INewWizard;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryClientWizard extends Wizard implements INewWizard {

	private SelectRepositoryClientPage selectRepositoryClientPage = new SelectRepositoryClientPage(this);

	protected RepositorySettingsPage repositorySettingsPage;
	
	protected ITaskRepositoryClient repositoryClient;
	
	public void setRepositoryClient(ITaskRepositoryClient repository) {
		this.repositoryClient = repository;
	}

	public ITaskRepositoryClient getRepositoryClient() {
		return repositoryClient;
	}

	@Override
	public void addPages() {
		addPage(selectRepositoryClientPage);
	}

	@Override
	public boolean canFinish() {
		return selectRepositoryClientPage.isPageComplete();
	}

	public void setRepositorySettingsPage(RepositorySettingsPage repositorySettingsPage) {
		this.repositorySettingsPage = repositorySettingsPage;
	}
}
