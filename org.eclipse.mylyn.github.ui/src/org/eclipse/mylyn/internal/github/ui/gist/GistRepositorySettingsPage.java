/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;

/**
 * Gist repository settings page class.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class GistRepositorySettingsPage extends AbstractRepositorySettingsPage {

	/**
	 * URL
	 */
	public static final String URL = "https://gist.github.com"; //$NON-NLS-1$

	/**
	 * @param taskRepository
	 */
	public GistRepositorySettingsPage(TaskRepository taskRepository) {
		super(Messages.GistRepositorySettingsPage_Title,
				Messages.GistRepositorySettingsPage_Description, taskRepository);
		setNeedsAnonymousLogin(false);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#getConnectorKind()
	 */
	public String getConnectorKind() {
		return GistConnector.KIND;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#createAdditionalControls(org.eclipse.swt.widgets.Composite)
	 */
	protected void createAdditionalControls(Composite parent) {
		if (repository == null) {
			setUrl(URL);
			repositoryLabelEditor
					.setStringValue(Messages.GistRepositorySettingsPage_RepositoryLabelDefault);
		}
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#isValidUrl(java.lang.String)
	 */
	protected boolean isValidUrl(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) //$NON-NLS-1$ //$NON-NLS-2$
			try {
				new URL(url);
				return true;
			} catch (IOException e) {
				return false;
			}
		return false;

	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#getValidator(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	protected Validator getValidator(final TaskRepository repository) {
		return new Validator() {

			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask(
						Messages.GistRepositorySettingsPage_TaskValidating, 100);
				try {
					monitor.subTask(Messages.GistRepositorySettingsPage_TaskContacting);
					try {
						GitHubClient client = GistConnector
								.createClient(repository);
						GistService service = new GistService(client);
						String user = repository.getCredentials(
								AuthenticationType.REPOSITORY).getUserName();
						monitor.worked(20);
						service.getGists(user);
					} catch (IOException e) {
						e = GitHubException.wrap(e);
						String message = MessageFormat
								.format(Messages.GistRepositorySettingsPage_StatusError,
										e.getLocalizedMessage());
						setStatus(GitHubUi.createErrorStatus(message));
						return;
					} finally {
						monitor.done();
					}
					setStatus(new Status(IStatus.OK, GitHubUi.BUNDLE_ID,
							Messages.GistRepositorySettingsPage_StatusSuccess));
				} finally {
					monitor.done();
				}
			}
		};
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#applyTo(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	public void applyTo(TaskRepository repository) {
		repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY,
				TaskRepository.CATEGORY_REVIEW);
		super.applyTo(repository);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#canValidate()
	 */
	public boolean canValidate() {
		return isPageComplete()
				&& (getMessage() == null || getMessageType() != IMessageProvider.ERROR);
	}

}
