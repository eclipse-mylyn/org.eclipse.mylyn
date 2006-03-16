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
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.ui.INewWizard;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryClientWizard extends Wizard implements INewWizard {

	private SelectRepositoryClientPage selectRepositoryClientPage = new SelectRepositoryClientPage(this);

	protected AbstractRepositorySettingsPage abstractRepositorySettingsPage;

	protected AbstractRepositoryConnector repositoryClient;

	public AbstractRepositoryClientWizard() {
		super();
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY);
	} 
	
	public void setRepositoryClient(AbstractRepositoryConnector repository) {
		this.repositoryClient = repository;
	}

	public AbstractRepositoryConnector getRepositoryClient() {
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
