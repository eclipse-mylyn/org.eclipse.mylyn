/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.ui.wizards.GerritCustomQueryPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskSearchPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * Connector specific UI for Gerrit.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 */
public class GerritConnectorUi extends AbstractRepositoryConnectorUi {

	@Override
	public String getConnectorKind() {
		return GerritConnector.CONNECTOR_KIND;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping taskSelection) {
		return new NewTaskWizard(taskRepository, taskSelection);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		wizard.addPage(new GerritCustomQueryPage(repository, "GerritQueryPage", query)); //$NON-NLS-1$
		return wizard;
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new GerritRepositorySettingsPage(taskRepository);
	}

	@Override
	public ITaskSearchPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return new GerritCustomQueryPage(repository, "GerritQueryPage", null);
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public String getTaskKindLabel(ITask task) {
		return "Change";
	}

}
