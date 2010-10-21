/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.gerrit.core.GerritConnector;
import org.eclipse.mylyn.gerrit.ui.wizards.GerritCustomQueryPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;


/**
 * Connector specific UI for Gerrit.
 * @author Mikael Kober, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 *
 */
public class GerritConnectorUi extends AbstractRepositoryConnectorUi {

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getConnectorKind()
	 */
	@Override
	public String getConnectorKind() {
		return GerritConnector.CONNECTOR_KIND;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getNewTaskWizard(org.eclipse.mylyn.tasks.core.TaskRepository, org.eclipse.mylyn.tasks.core.ITaskMapping)
	 */
	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping taskSelection) {
		return new NewTaskWizard(taskRepository, taskSelection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getQueryWizard(org.eclipse.mylyn.tasks.core.TaskRepository, org.eclipse.mylyn.tasks.core.IRepositoryQuery)
	 */
	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		wizard.addPage(new GerritCustomQueryPage(repository, "Gerrit query", query));
		return wizard;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getSettingsPage(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new GerritRepositorySettingsPage(taskRepository);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#hasSearchPage()
	 */
	@Override
	public boolean hasSearchPage() {
		return false;
	}

}
