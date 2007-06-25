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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class EditRepositoryWizard extends Wizard implements INewWizard {

	private static final String TITLE = "Task Repository Settings";

	private AbstractRepositorySettingsPage abstractRepositorySettingsPage;

	private TaskRepository repository;

	public EditRepositoryWizard(TaskRepository repository) {
		super();
		this.repository = repository;
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
		abstractRepositorySettingsPage = connectorUi.getSettingsPage();
		abstractRepositorySettingsPage.setRepository(repository);
		abstractRepositorySettingsPage.setVersion(repository.getVersion());
		abstractRepositorySettingsPage.setWizard(this);
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY_SETTINGS);
		setWindowTitle(TITLE);
	}

	/**
	 * Custom properties should be set on the repository object to ensure they are saved.
	 */
	@Override
	public boolean performFinish() {
		if (canFinish()) {
			String oldUrl = repository.getUrl();
			String newUrl = abstractRepositorySettingsPage.getServerUrl();
			TasksUiPlugin.getTaskListManager().refactorRepositoryUrl(oldUrl, newUrl);

			repository.flushAuthenticationCredentials();
			repository.setUrl(newUrl);
			repository.setVersion(abstractRepositorySettingsPage.getVersion());
			repository.setCharacterEncoding(abstractRepositorySettingsPage.getCharacterEncoding());
			repository.setAuthenticationCredentials(abstractRepositorySettingsPage.getUserName(),
					abstractRepositorySettingsPage.getPassword());
			repository.setRepositoryLabel(abstractRepositorySettingsPage.getRepositoryLabel());
			repository.setAnonymous(abstractRepositorySettingsPage.isAnonymousAccess());
			repository.setHttpAuthenticationCredentials(abstractRepositorySettingsPage.getHttpAuthUserId(),
					abstractRepositorySettingsPage.getHttpAuthPassword());

			repository.setProperty(TaskRepository.PROXY_USEDEFAULT,
					String.valueOf(abstractRepositorySettingsPage.getUseDefaultProxy()));
			repository.setProperty(TaskRepository.PROXY_HOSTNAME, abstractRepositorySettingsPage.getProxyHostname());
			repository.setProperty(TaskRepository.PROXY_PORT, abstractRepositorySettingsPage.getProxyPort());
			repository.setProxyAuthenticationCredentials(abstractRepositorySettingsPage.getProxyUserName(),
					abstractRepositorySettingsPage.getProxyPassword());

			abstractRepositorySettingsPage.updateProperties(repository);
			TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(repository);
			TasksUiPlugin.getRepositoryManager().saveRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
			return true;
		}
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		addPage(abstractRepositorySettingsPage);
	}

	@Override
	public boolean canFinish() {
		return abstractRepositorySettingsPage.isPageComplete();
	}

	/** public for testing */
	public AbstractRepositorySettingsPage getSettingsPage() {
		return abstractRepositorySettingsPage;
	}

	public TaskRepository getRepository() {
		return repository;
	}
}
