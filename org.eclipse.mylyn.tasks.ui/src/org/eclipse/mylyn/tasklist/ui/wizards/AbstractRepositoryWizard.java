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
import org.eclipse.mylar.tasklist.repositories.TaskRepository;
import org.eclipse.ui.INewWizard;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryWizard extends Wizard implements INewWizard {

	private SelectRepositoryPage selectRepositoryPage;

	protected TaskRepository repository;

	public AbstractRepositoryWizard() {
		selectRepositoryPage = new SelectRepositoryPage(this);	
	}

	public AbstractRepositoryWizard(String repositoryKind) {
		selectRepositoryPage = new SelectRepositoryPage(this, repositoryKind);	
	}
	
	public void setRepository(TaskRepository taskRepository) {
		this.repository = taskRepository;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	@Override
	public void addPages() {
		addPage(selectRepositoryPage);
	}

	@Override
	public boolean canFinish() {
		return selectRepositoryPage.isPageComplete();
	}
}
