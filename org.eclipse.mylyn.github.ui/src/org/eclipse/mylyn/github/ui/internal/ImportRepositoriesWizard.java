/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.Repository;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.internal.github.ui.gist.GistRepositorySettingsPage;
import org.eclipse.mylyn.internal.github.ui.gist.Messages;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * Import repositories wizard class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class ImportRepositoriesWizard extends Wizard implements IImportWizard {

	private CredentialsWizardPage credentialsPage;

	private RepositorySelectionWizardPage reposPage;

	/**
	 * Create import repositories wizard
	 */
	public ImportRepositoriesWizard() {
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(WorkbenchImages
				.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_IMPORT_WIZ));
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		this.credentialsPage = new CredentialsWizardPage();
		addPage(this.credentialsPage);
		this.reposPage = new RepositorySelectionWizardPage();
		addPage(this.reposPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage next = super.getNextPage(page);
		if (next == reposPage) {
			reposPage.setUser(credentialsPage.getUserName());
			reposPage.setPassword(credentialsPage.getPassword());
		}
		return next;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		String user = credentialsPage.getUserName();
		String password = credentialsPage.getPassword();
		for (Object selected : reposPage.getRepositories()) {
			Repository repo = (Repository) selected;
			AuthenticationCredentials credentials = new AuthenticationCredentials(
					user, password);
			TaskRepository repository = new TaskRepository(
					GitHub.CONNECTOR_KIND, GitHub.createGitHubUrl(
							repo.getOwner(), repo.getName()));
			repository.setProperty(IRepositoryConstants.PROPERTY_LABEL,
					repo.getId());
			repository.setCredentials(AuthenticationType.REPOSITORY,
					credentials, true);
			repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY,
					IRepositoryConstants.CATEGORY_BUGS);
			TasksUi.getRepositoryManager().addRepository(repository);
		}
		if (reposPage.createGistRepository()) {
			AuthenticationCredentials credentials = new AuthenticationCredentials(
					user, password);
			TaskRepository repository = new TaskRepository(GistConnector.KIND,
					GistRepositorySettingsPage.URL);
			repository.setProperty(IRepositoryConstants.PROPERTY_LABEL,
					Messages.GistRepositorySettingsPage_RepositoryLabelDefault);
			repository.setCredentials(AuthenticationType.REPOSITORY,
					credentials, true);
			repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY,
					IRepositoryConstants.CATEGORY_REVIEW);
			TasksUi.getRepositoryManager().addRepository(repository);
		}
		return true;
	}
}
