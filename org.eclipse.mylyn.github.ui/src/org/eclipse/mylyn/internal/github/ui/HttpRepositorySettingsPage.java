/******************************************************************************
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
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;

/**
 * Base HTTP-based task repository settings page
 */
public abstract class HttpRepositorySettingsPage extends
		AbstractRepositorySettingsPage {

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
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#canValidate()
	 */
	public boolean canValidate() {
		return isPageComplete()
				&& (getMessage() == null || getMessageType() != IMessageProvider.ERROR);
	}
}
