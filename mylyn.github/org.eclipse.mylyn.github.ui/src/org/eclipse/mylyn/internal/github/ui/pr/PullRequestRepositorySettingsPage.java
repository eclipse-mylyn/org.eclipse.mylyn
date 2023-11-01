/******************************************************************************
 *  Copyright (c) 2011, 2020 GitHub Inc. and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.NoSuchPageException;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.mylyn.internal.github.core.issue.IssueConnector;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestConnector;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.github.ui.HttpRepositorySettingsPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Composite;

/**
 * Pull request task repository settings page.
 */
public class PullRequestRepositorySettingsPage extends
		HttpRepositorySettingsPage {

	/**
	 * Create pull request repository settings page
	 *
	 * @param taskRepository
	 */
	public PullRequestRepositorySettingsPage(final TaskRepository taskRepository) {
		super(Messages.PullRequestRepositorySettingsPage_Title,
				Messages.PullRequestRepositorySettingsPage_Description,
				taskRepository);
		setUseTokenForAuthentication(true);

	}

	@Override
	public String getConnectorKind() {
		return PullRequestConnector.KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// Set the URL now, because serverURL is definitely instantiated .
		if (serverUrlCombo.getText().length() == 0) {
			setInitialUrl(PullRequestConnector::getRepositoryLabel);
		} else {
			serverUrlCombo.setText(PullRequestConnector.stripPulls(repository
					.getRepositoryUrl()));
		}
		if (getRepository() == null) {
			setAnonymous(false);
		}
	}

	@Override
	protected Validator getValidator(final TaskRepository taskRepository) {
		Validator validator = new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask(
						Messages.PullRequestRepositorySettingsPage_TaskValidating,
						100);
				monitor.subTask(Messages.PullRequestRepositorySettingsPage_TaskContacting);
				try {
					GitHubClient client = IssueConnector
							.createClient(taskRepository);
					PullRequestService service = new PullRequestService(client);
					RepositoryId repo = GitHub.getRepository(taskRepository
							.getRepositoryUrl());
					monitor.worked(50);
					service.pagePullRequests(repo, IssueService.STATE_OPEN, 1)
							.next();
				} catch (NoSuchPageException e) {
					String message = MessageFormat
							.format(Messages.PullRequestRepositorySettingsPage_ValidateError,
									GitHubException.wrap(e.getCause())
											.getLocalizedMessage());
					setStatus(GitHubUi.createErrorStatus(message));
					return;
				} finally {
					monitor.done();
				}

				setStatus(new Status(
						IStatus.OK,
						GitHubUi.BUNDLE_ID,
						Messages.PullRequestRepositorySettingsPage_ValidateSuccess));
			}
		};
		return validator;
	}

	@Override
	public void applyTo(TaskRepository taskRepository) {
		taskRepository.setCategory(TaskRepository.CATEGORY_REVIEW);
		super.applyTo(taskRepository);
	}

	@Override
	public String getRepositoryUrl() {
		return PullRequestConnector.appendPulls(super.getRepositoryUrl());
	}
}
