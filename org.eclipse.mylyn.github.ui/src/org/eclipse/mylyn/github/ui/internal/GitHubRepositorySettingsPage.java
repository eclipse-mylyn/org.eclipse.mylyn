/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubClient;
import org.eclipse.mylyn.github.internal.IssueService;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * GitHub connector specific extensions.
 */
public class GitHubRepositorySettingsPage extends
		AbstractRepositorySettingsPage {

	/**
	 * URL
	 */
	public static final String URL = "http://github.com"; //$NON-NLS-1$

	/**
	 * DEFAULT_REPOSITORY
	 */
	public static final String DEFAULT_REPOSITORY = "/user/repository"; //$NON-NLS-1$

	private boolean syncLabel = true;
	private boolean editingUrl = false;

	/**
	 * Populate taskRepository with repository settings.
	 * 
	 * @param taskRepository
	 *            - Object to populate
	 */
	public GitHubRepositorySettingsPage(final TaskRepository taskRepository) {
		super(Messages.GitHubRepositorySettingsPage_Title,
				Messages.GitHubRepositorySettingsPage_Description,
				taskRepository);
		this.setHttpAuth(false);
		this.setNeedsAdvanced(false);
		this.setNeedsAnonymousLogin(true);
		this.setNeedsTimeZone(false);
		this.setNeedsHttpAuth(false);
	}

	@Override
	public String getConnectorKind() {
		return GitHub.CONNECTOR_KIND;
	}

	/**
	 * Sync server url combo with repository label editor base on default label
	 * format
	 */
	protected void syncRepositoryLabel() {
		if (syncLabel) {
			String url = serverUrlCombo.getText();
			String user = GitHub.computeTaskRepositoryUser(url);
			String repo = GitHub.computeTaskRepositoryProject(url);
			if (user != null && repo != null)
				repositoryLabelEditor.setStringValue(user + '/' + repo);
		}
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// Set the URL now, because serverURL is definitely instantiated .
		if (serverUrlCombo.getText().length() == 0) {
			String fullUrlText = URL + DEFAULT_REPOSITORY;
			serverUrlCombo.setText(fullUrlText);
			serverUrlCombo.setFocus();
			// select the user/project part of the URL so that the user can just
			// start typing to replace the text.
			serverUrlCombo.setSelection(new Point(URL.length() + 1, fullUrlText
					.length()));

			syncRepositoryLabel();

			serverUrlCombo.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					editingUrl = true;
					try {
						syncRepositoryLabel();
					} finally {
						editingUrl = false;
					}
				}
			});

			repositoryLabelEditor.getTextControl(compositeContainer)
					.addModifyListener(new ModifyListener() {

						public void modifyText(ModifyEvent e) {
							if (!editingUrl)
								syncLabel = false;
						}
					});
		}

		if (getRepository() == null)
			setAnonymous(false);
	}

	@Override
	protected Validator getValidator(final TaskRepository repository) {
		Validator validator = new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask(
						Messages.GitHubRepositorySettingsPage_TaskValidating,
						100);
				try {
					String urlText = repository.getUrl();
					Matcher urlMatcher = GitHub.URL_PATTERN
							.matcher(urlText == null ? "" : urlText); //$NON-NLS-1$
					if (!urlMatcher.matches()) {
						setStatus(GitHubUi
								.createErrorStatus(Messages.GitHubRepositorySettingsPage_ErrorMalformedUrl));
						return;
					}
					monitor.worked(20);

					monitor.subTask(Messages.GitHubRepositorySettingsPage_TaskContactingServer);
					try {
						AuthenticationCredentials auth = repository
								.getCredentials(AuthenticationType.REPOSITORY);
						GitHubClient client = new GitHubClient();
						if (auth != null)
							client.setCredentials(auth.getUserName(),
									auth.getPassword());
						IssueService service = new IssueService(client);
						String user = GitHub
								.computeTaskRepositoryUser(repository.getUrl());
						String project = GitHub
								.computeTaskRepositoryProject(repository
										.getUrl());
						monitor.worked(20);
						service.getIssues(user, project, null);
					} catch (IOException e) {
						setStatus(GitHubUi
								.createErrorStatus(Messages.GitHubRepositorySettingsPage_ErrorInvalidCredentials));
						return;
					} finally {
						monitor.done();
					}

					setStatus(new Status(IStatus.OK, GitHubUi.BUNDLE_ID,
							Messages.GitHubRepositorySettingsPage_StatusSuccess));
				} finally {
					monitor.done();
				}
			}
		};
		return validator;
	}

	@Override
	protected boolean isValidUrl(final String url) {
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
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#applyTo(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	public void applyTo(TaskRepository repository) {
		repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY,
				IRepositoryConstants.CATEGORY_BUGS);
		super.applyTo(repository);
	}

}
