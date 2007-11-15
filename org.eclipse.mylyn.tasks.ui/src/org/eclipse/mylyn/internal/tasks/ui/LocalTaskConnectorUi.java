/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewLocalTaskWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class LocalTaskConnectorUi extends AbstractRepositoryConnectorUi {

	@Override
	public ImageDescriptor getTaskKindOverlay(AbstractTask task) {
		return super.getTaskKindOverlay(task);
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		return new NewLocalTaskWizard();
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, TaskSelection selection) {
		return new NewLocalTaskWizard(selection);
	}
	
	@Override
	public IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery queryToEdit) {
		return null;
	}

	@Override
	public String getConnectorKind() {
		return LocalRepositoryConnector.REPOSITORY_KIND;
	}

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		return null;
	}

	@Override
	public boolean hasSearchPage() {
		return false;
	}

}
