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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.tasklist.TaskRepositoryClient;
import org.eclipse.ui.INewWizard;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryClientWizard extends Wizard implements INewWizard {

	private SelectRepositoryClientPage selectRepositoryClientPage = new SelectRepositoryClientPage(this);

	protected AbstractRepositorySettingsPage abstractRepositorySettingsPage;

	protected TaskRepositoryClient repositoryClient;

	public void setRepositoryClient(TaskRepositoryClient repository) {
		this.repositoryClient = repository;
	}

	public TaskRepositoryClient getRepositoryClient() {
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

	public void setRepositorySettingsPage(AbstractRepositorySettingsPage abstractRepositorySettingsPage) {
		this.abstractRepositorySettingsPage = abstractRepositorySettingsPage;
	}
}
