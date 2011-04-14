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
package org.eclipse.mylyn.internal.github.ui.gist;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
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
			setUrl("https://gist.github.com"); //$NON-NLS-1$
			repositoryLabelEditor.setStringValue("Gists"); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#isValidUrl(java.lang.String)
	 */
	protected boolean isValidUrl(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) { //$NON-NLS-1$ //$NON-NLS-2$
			try {
				new URL(url);
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		return false;

	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#getValidator(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	protected Validator getValidator(TaskRepository repository) {
		return new Validator() {

			public void run(IProgressMonitor monitor) throws CoreException {

			}
		};
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#applyTo(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	public void applyTo(TaskRepository repository) {
		repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY,
				IRepositoryConstants.CATEGORY_REVIEW);
		super.applyTo(repository);
	}

}
