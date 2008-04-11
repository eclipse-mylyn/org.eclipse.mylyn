/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.Collection;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.ui.INewWizard;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryClientWizard extends Wizard implements INewWizard {

	/**
	 * If not null, indicates that the wizard will initially jump to a specific connector page
	 */
	private final String repositoryType;

	protected TaskRepository repository;

	private final SelectRepositoryClientPage selectRepositoryClientPage = new SelectRepositoryClientPage(this);

	protected AbstractRepositorySettingsPage abstractRepositorySettingsPage;

	protected AbstractRepositoryConnector repositoryConnector;

	protected AbstractRepositoryClientWizard() {
		this(null);
	}

	protected AbstractRepositoryClientWizard(String repositoryType) {
		this.repositoryType = repositoryType;
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public void setRepositoryConnector(AbstractRepositoryConnector repositoryConnector) {
		this.repositoryConnector = repositoryConnector;
	}

	public AbstractRepositoryConnector getRepositoryConnector() {
		return repositoryConnector;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	@Override
	public void addPages() {
		Collection<AbstractRepositoryConnector> connectors = TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnectors();
		if (repositoryType != null || connectors.size() == 1) {
			AbstractRepositoryConnector connector = null;
			if (repositoryType != null) {
				connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repositoryType);
			} else {
				connector = connectors.toArray(new AbstractRepositoryConnector[1])[0];
			}
			setRepositoryConnector(connector);
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(connector.getConnectorKind());
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
