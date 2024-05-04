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
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.graphics.Point;

/**
 * Base HTTP-based task repository settings page
 */
public abstract class HttpRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private boolean syncLabel = true;

	private boolean editingUrl = false;

	/**
	 * Create repository settings page
	 *
	 * @param title
	 * @param description
	 * @param taskRepository
	 *            - Object to populate
	 */
	public HttpRepositorySettingsPage(final String title, final String description,
			final TaskRepository taskRepository) {
		super(title, description, taskRepository);
		setHttpAuth(false);
		setNeedsAdvanced(false);
		setNeedsAnonymousLogin(true);
		setNeedsTimeZone(false);
		setNeedsHttpAuth(false);
	}

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
		return isPageComplete() && (getMessage() == null || getMessageType() != IMessageProvider.ERROR);
	}

	private void syncRepositoryLabel(Function<RepositoryId, String> labelProvider) {
		if (syncLabel) {
			String url = serverUrlCombo.getText();
			RepositoryId repo = GitHub.getRepository(url);
			if (repo != null) {
				repositoryLabelEditor.setStringValue(labelProvider.apply(repo));
			}
		}
	}

	/**
	 * Set up the {@link #serverUrlCombo} to have the initial Github URL as content and to sync with the {@link #repositoryLabelEditor}.
	 *
	 * @param labelProvider
	 *            to provide a repository label
	 */
	protected void setInitialUrl(Function<RepositoryId, String> labelProvider) {
		String fullUrlText = GitHub.HTTPS_GITHUB_COM + GitHub.REPOSITORY_SEGMENTS;
		serverUrlCombo.setText(fullUrlText);
		serverUrlCombo.setFocus();
		// select the user/project part of the URL so that the user can just
		// start typing to replace the text.
		serverUrlCombo.setSelection(new Point(
				GitHub.HTTPS_GITHUB_COM.length() + 1, fullUrlText.length()));

		syncRepositoryLabel(labelProvider);

		serverUrlCombo.addModifyListener(e -> {
			editingUrl = true;
			try {
				syncRepositoryLabel(labelProvider);
			} finally {
				editingUrl = false;
			}
		});

		repositoryLabelEditor.getTextControl(compositeContainer).addModifyListener(e -> {
			if (!editingUrl) {
				syncLabel = false;
			}
		});
	}

	/**
	 * Should the 'Use Token' check box be "checked" Here to not break existing repository definitions
	 *
	 * @param taskRepository
	 */
	@Override
	protected boolean useTokenChecked(TaskRepository taskRepository) {
		return super.useTokenChecked(taskRepository) || //
				Boolean.parseBoolean(
						taskRepository.getProperty(GitHub.PROPERTY_USE_TOKEN));
	}

	/**
	 * @since 4.1
	 */
	@Override
	protected String getSettingsPageEnterTokenText() {
		return Messages.HttpRepositorySettingsPage_EnterToken;
	}

	/**
	 * @since 4.1
	 */
	@Override
	protected String getSettingsPageEnterUserAndTokenText() {
		return Messages.HttpRepositorySettingsPage_EnterUserAndToken;
	}

	/**
	 * @since 4.1
	 */
	@Override
	protected String getSettingsPageGetUseLabelUseTokenText() {
		return Messages.HttpRepositorySettingsPage_LabelUseToken;
	}

	/**
	 * @since 4.1
	 */
	@Override
	protected String getSettingsPageTooltipUseTokenText() {
		return Messages.HttpRepositorySettingsPage_TooltipUseToken;
	}

	/**
	 * @since 4.1
	 */
	@Override
	protected String getSettingsPageLabelTokenText() {
		return Messages.HttpRepositorySettingsPage_LabelToken;
	}

	/**
	 * @since 4.1
	 */
	@Override
	protected String getSettingsPageLabelSaveTokenText() {
		return Messages.HttpRepositorySettingsPage_LabelSaveToken;
	}

}
