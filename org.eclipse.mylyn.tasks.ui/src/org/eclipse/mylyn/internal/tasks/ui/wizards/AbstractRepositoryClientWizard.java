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

package org.eclipse.mylar.internal.tasks.ui.wizards;

import java.util.Collection;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.INewWizard;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryClientWizard extends Wizard implements INewWizard {

	private SelectRepositoryClientPage selectRepositoryClientPage = new SelectRepositoryClientPage(this);

	protected AbstractRepositorySettingsPage abstractRepositorySettingsPage;

	protected AbstractRepositoryConnector repositoryConnector;

	public AbstractRepositoryClientWizard() {
		super();
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY);
	} 
	
	public void setRepositoryConnector(AbstractRepositoryConnector repositoryConnector) {
		this.repositoryConnector = repositoryConnector;
	}

	public AbstractRepositoryConnector getRepositoryConnector() {
		return repositoryConnector;
	}

	@Override
	public void addPages() {
		Collection<AbstractRepositoryConnector> connectors = TasksUiPlugin.getRepositoryManager().getRepositoryConnectors();
		if (connectors.size() == 1) {
			AbstractRepositoryConnector connector = connectors.toArray(new AbstractRepositoryConnector[1])[0];
			setRepositoryConnector(connector);
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryManager().getRepositoryUi(
					connector.getRepositoryType());
			AbstractRepositorySettingsPage nextPage = connectorUi.getSettingsPage();
			setRepositorySettingsPage(nextPage);
			nextPage.setWizard(this);
			addPage(nextPage);
		} else {
			addPage(selectRepositoryClientPage);			
		}		
	}

	@Override
	public boolean canFinish() {
		return selectRepositoryClientPage.isPageComplete();
	}

	public void setRepositorySettingsPage(AbstractRepositorySettingsPage abstractRepositorySettingsPage) {
		this.abstractRepositorySettingsPage = abstractRepositorySettingsPage;
	}
}
