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
package org.eclipse.mylyn.internal.github.ui.issue;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
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
public class IssueRepositorySettingsPage extends AbstractRepositorySettingsPage {

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
	public IssueRepositorySettingsPage(final TaskRepository taskRepository) {
		super(Messages.IssueRepositorySettingsPage_Title,
				Messages.IssueRepositorySettingsPage_Description,
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
			Repository repo = GitHub.getRepository(url);
			if (repo != null)
				repositoryLabelEditor.setStringValue(repo.getOwner() + '/'
						+ repo.getName());
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
						Messages.IssueRepositorySettingsPage_TaskValidating,
						100);
				try {
					monitor.subTask(Messages.IssueRepositorySettingsPage_TaskContactingServer);
					try {
						AuthenticationCredentials auth = repository
								.getCredentials(AuthenticationType.REPOSITORY);
						GitHubClient client = new GitHubClient();
						if (auth != null)
							client.setCredentials(auth.getUserName(),
									auth.getPassword());
						IssueService service = new IssueService(client);
						Repository repo = GitHub.getRepository(repository
								.getRepositoryUrl());
						monitor.worked(50);
						service.getIssues(repo.getOwner(), repo.getName(), null);
					} catch (IOException e) {
						e = GitHubException.wrap(e);
						String message = MessageFormat
								.format(Messages.IssueRepositorySettingsPage_StatusError,
										e.getLocalizedMessage());
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
	protected boolean isValidUrl(final String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) //$NON-NLS-1$ //$NON-NLS-2$
			try {
				new URL(url);
				return GitHub.getRepository(url) != null;
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

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#canValidate()
	 */
	public boolean canValidate() {
		return isPageComplete()
				&& (getMessage() == null || getMessageType() != IMessageProvider.ERROR);
	}

}
