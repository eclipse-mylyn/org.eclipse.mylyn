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
package org.eclipse.mylyn.internal.github.ui;

import java.io.IOException;
import java.net.URL;
import java.util.function.Function;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;

/**
 * Base HTTP-based task repository settings page
 */
public abstract class HttpRepositorySettingsPage extends
		AbstractRepositorySettingsPage {

	private boolean syncLabel = true;

	private boolean editingUrl = false;

	private boolean needsUser = true;

	private Button useToken;

	/**
	 * Create repository settings page
	 *
	 * @param title
	 * @param description
	 * @param taskRepository
	 *            - Object to populate
	 */
	public HttpRepositorySettingsPage(final String title,
			final String description, final TaskRepository taskRepository) {
		super(title, description, taskRepository);
		setHttpAuth(false);
		setNeedsAdvanced(false);
		setNeedsAnonymousLogin(true);
		setNeedsTimeZone(false);
		setNeedsHttpAuth(false);
	}

	@SuppressWarnings("unused")
	@Override
	protected boolean isValidUrl(final String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) { //$NON-NLS-1$ //$NON-NLS-2$
			try {
				new URL(url);
				return GitHub.getRepository(url) != null;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean canValidate() {
		return isPageComplete()
				&& (getMessage() == null || getMessageType() != IMessageProvider.ERROR);
	}

	private void syncRepositoryLabel(
			Function<RepositoryId, String> labelProvider) {
		if (syncLabel) {
			String url = serverUrlCombo.getText();
			RepositoryId repo = GitHub.getRepository(url);
			if (repo != null) {
				repositoryLabelEditor.setStringValue(labelProvider.apply(repo));
			}
		}
	}

	/**
	 * Set up the {@link #serverUrlCombo} to have the initial Github URL as
	 * content and to sync with the {@link #repositoryLabelEditor}.
	 *
	 * @param labelProvider
	 *            to provide a repository label
	 */
	protected void setInitialUrl(Function<RepositoryId, String> labelProvider) {
		String fullUrlText = GitHub.HTTP_GITHUB_COM
				+ GitHub.REPOSITORY_SEGMENTS;
		serverUrlCombo.setText(fullUrlText);
		serverUrlCombo.setFocus();
		// select the user/project part of the URL so that the user can just
		// start typing to replace the text.
		serverUrlCombo.setSelection(new Point(
				GitHub.HTTP_GITHUB_COM.length() + 1, fullUrlText.length()));

		syncRepositoryLabel(labelProvider);

		serverUrlCombo.addModifyListener(e -> {
			editingUrl = true;
			try {
				syncRepositoryLabel(labelProvider);
			} finally {
				editingUrl = false;
			}
		});

		repositoryLabelEditor.getTextControl(compositeContainer)
				.addModifyListener(e -> {
					if (!editingUrl) {
						syncLabel = false;
					}
				});
	}

	/**
	 * Inserts a checkbox into the page where the user can specify that token
	 * authentication shall be used for the task repository.
	 *
	 * @param userOptional
	 *            whether or not a user name is optional
	 */
	protected void addTokenCheckbox(boolean userOptional) {
		needsUser = !userOptional;
		useToken = new Button(compositeContainer, SWT.CHECK);
		useToken.setText(Messages.HttpRepositorySettingsPage_LabelUseToken);
		useToken.setToolTipText(
				Messages.HttpRepositorySettingsPage_TooltipUseToken);
		useToken.moveBelow(savePasswordButton);
		GridDataFactory.defaultsFor(useToken).span(3, 1).applyTo(useToken);
		String savePasswordText = savePasswordButton.getText();
		boolean[] allowAnon = { isAnonymousAccess() };
		useToken.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isChecked = useToken.getSelection();
				if (isChecked) {
					repositoryPasswordEditor.setLabelText(
							Messages.HttpRepositorySettingsPage_LabelToken);
					savePasswordButton.setText(
							Messages.HttpRepositorySettingsPage_LabelSaveToken);
					if (anonymousButton != null) {
						allowAnon[0] = isAnonymousAccess();
						setAnonymous(false);
						anonymousButton.setEnabled(false);
					}
				} else {
					repositoryPasswordEditor.setLabelText(LABEL_PASSWORD);
					savePasswordButton.setText(savePasswordText);
					if (anonymousButton != null) {
						anonymousButton.setEnabled(true);
						setAnonymous(allowAnon[0]);
					}
				}
				if (userOptional) {
					repositoryUserNameEditor.getTextControl(compositeContainer)
							.setEnabled(!isChecked);
					repositoryUserNameEditor.setEmptyStringAllowed(isChecked);
				}
				repositoryPasswordEditor.getLabelControl(compositeContainer)
						.requestLayout();
				// Trigger page validation if needed
				if (userOptional && getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		TaskRepository taskRepository = getRepository();
		if (taskRepository != null) {
			useToken.setSelection(Boolean.parseBoolean(
					taskRepository.getProperty(GitHub.PROPERTY_USE_TOKEN)));
		}
	}

	/**
	 * Tells whether the task repository uses token authentication.
	 *
	 * @return {@code true} if token authentication shall be used; {@code false}
	 *         otherwise
	 */
	protected boolean useTokenAuth() {
		return useToken != null && useToken.getSelection();
	}

	@Override
	protected boolean isMissingCredentials() {
		if (!needsUser && useTokenAuth()) {
			return repositoryPasswordEditor.getStringValue().trim().isEmpty();
		} else {
			return super.isMissingCredentials();
		}
	}

	@SuppressWarnings("restriction")
	@Override
	public void setMessage(String newMessage, int newType) {
		// This is a bit hacky since it relies on an internal message and the
		// way it is used in the super class. But it beats re-implementing
		// isPageComplete().
		if (useTokenAuth()
				&& org.eclipse.mylyn.internal.tasks.ui.wizards.Messages.AbstractRepositorySettingsPage_Enter_a_user_id_Message0
						.equals(newMessage)) {
			if (needsUser) {
				super.setMessage(
						Messages.HttpRepositorySettingsPage_EnterUserAndToken,
						newType);
			} else {
				super.setMessage(Messages.HttpRepositorySettingsPage_EnterToken,
						newType);
			}
		} else {
			super.setMessage(newMessage, newType);
		}
	}

	@Override
	public void applyTo(TaskRepository taskRepository) {
		taskRepository.setProperty(GitHub.PROPERTY_USE_TOKEN,
				Boolean.toString(useToken != null && useToken.getSelection()));
		super.applyTo(taskRepository);
	}
}
