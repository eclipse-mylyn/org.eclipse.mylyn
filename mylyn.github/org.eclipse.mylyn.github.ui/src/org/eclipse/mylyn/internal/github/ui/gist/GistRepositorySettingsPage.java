/*******************************************************************************
 *  Copyright (c) 2011, 2020 GitHub Inc. and others
 *
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
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.github.ui.HttpRepositorySettingsPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Composite;

/**
 * Gist repository settings.
 */
public class GistRepositorySettingsPage extends HttpRepositorySettingsPage {

	/** URL for gists. */
	public static final String URL = "https://gist.github.com"; //$NON-NLS-1$

	/**
	 * @param taskRepository
	 */
	public GistRepositorySettingsPage(TaskRepository taskRepository) {
		super(Messages.GistRepositorySettingsPage_Title,
				Messages.GistRepositorySettingsPage_Description, taskRepository);
		setNeedsAnonymousLogin(false);
		setUseToken(true, false);
	}

	@Override
	public String getConnectorKind() {
		return GistConnector.KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		if (repository == null) {
			setUrl(URL);
			repositoryLabelEditor
					.setStringValue(Messages.GistRepositorySettingsPage_RepositoryLabelDefault);
		}
		// For gists we still need a user name.
	}

	@Override
	protected Validator getValidator(final TaskRepository taskRepository) {
		return new Validator() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask(
						Messages.GistRepositorySettingsPage_TaskValidating, 100);
				try {
					monitor.subTask(Messages.GistRepositorySettingsPage_TaskContacting);
					try {
						GitHubClient client = GistConnector
								.createClient(taskRepository);
						GistService service = new GistService(client);
						String user = taskRepository.getCredentials(
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

	@Override
	public void applyTo(TaskRepository taskRepository) {
		taskRepository.setCategory(TaskRepository.CATEGORY_REVIEW);
		super.applyTo(taskRepository);
	}

}
