/*******************************************************************************
 * Copyright (c) 2011, 2020 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.issue;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.NoSuchPageException;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.mylyn.internal.github.core.issue.IssueConnector;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.github.ui.HttpRepositorySettingsPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Composite;

/**
 * GitHub connector specific extensions.
 */
public class IssueRepositorySettingsPage extends HttpRepositorySettingsPage {

	/**
	 * Populate taskRepository with repository settings.
	 *
	 * @param taskRepository
	 *            - Object to populate
	 */
	public IssueRepositorySettingsPage(final TaskRepository taskRepository) {
		super(Messages.IssueRepositorySettingsPage_Title, Messages.IssueRepositorySettingsPage_Description,
				taskRepository);
		setUseTokenForAuthentication(true);

	}

	@Override
	public String getConnectorKind() {
		return GitHub.CONNECTOR_KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		if (getRepository() == null) {
			setAnonymous(false);
		}
		// Set the URL now, because serverURL is definitely instantiated.
		if (serverUrlCombo.getText().isEmpty()) {
			setInitialUrl(IssueConnector::getRepositoryLabel);
		}
	}

	@Override
	protected Validator getValidator(final TaskRepository taskRepository) {
		Validator validator = new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask(
						Messages.IssueRepositorySettingsPage_TaskValidating, 100);
				try {
					monitor.subTask(Messages.IssueRepositorySettingsPage_TaskContactingServer);
					try {
						GitHubClient client = IssueConnector.createClient(taskRepository);
						IssueService service = new IssueService(client);
						RepositoryId repo = GitHub.getRepository(taskRepository.getRepositoryUrl());
						monitor.worked(50);
						service.pageIssues(repo.getOwner(), repo.getName(), null, 1).next();
					} catch (NoSuchPageException e) {
						String message = MessageFormat.format(Messages.IssueRepositorySettingsPage_StatusError,
								GitHubException.wrap(e.getCause()).getLocalizedMessage());
						setStatus(GitHubUi.createErrorStatus(message));
						return;
					} finally {
						monitor.done();
					}

					setStatus(new Status(IStatus.OK, GitHubUi.BUNDLE_ID,
							Messages.IssueRepositorySettingsPage_StatusSuccess));
				} finally {
					monitor.done();
				}
			}
		};
		return validator;
	}

	@Override
	public void applyTo(TaskRepository taskRepository) {
		taskRepository.setCategory(TaskRepository.CATEGORY_BUGS);
		super.applyTo(taskRepository);
	}

}
