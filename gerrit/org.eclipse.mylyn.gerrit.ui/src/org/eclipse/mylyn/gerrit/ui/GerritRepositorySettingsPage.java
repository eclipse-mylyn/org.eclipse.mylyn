/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;


/**
 * Wizard page to specify URL to Gerrit server, authentication type, and more.
 * @author Mikael Kober, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 */
public class GerritRepositorySettingsPage extends AbstractRepositorySettingsPage {
	
	private static final String TITLE = "Gerrit Repository Settings";

	private static final String DESCRIPTION_LABEL = "Web based code review and project management for Git based projects.";
	
	/**
	 * Constructor.
	 * @param taskRepository
	 */
	public GerritRepositorySettingsPage(TaskRepository taskRepository) {
		super(TITLE, DESCRIPTION_LABEL, taskRepository);
		setNeedsAnonymousLogin(true);
		setNeedsHttpAuth(false);
		setNeedsProxy(false);
		setNeedsAdvanced(false);  // might need additional controls later on
		setNeedsEncoding(false);  
		setNeedsTimeZone(false);
		setNeedsValidation(false); // ??
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		addRepositoryTemplatesToServerUrlCombo();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#applyTo(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		//repository.setRepositoryLabel(selected);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#createAdditionalControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createAdditionalControls(Composite parent) {
		// not called now: setNeedsAdvanced(true) when needed.
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#repositoryTemplateSelected(org.eclipse.mylyn.tasks.core.RepositoryTemplate)
	 */
	@Override
	protected void repositoryTemplateSelected(RepositoryTemplate template) {
		repositoryLabelEditor.setStringValue(template.label);
		setUrl(template.repositoryUrl);
		getContainer().updateButtons();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#getConnectorKind()
	 */
	@Override
	public String getConnectorKind() {
		return GerritConnector.CONNECTOR_KIND;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#getValidator(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new GerritValidator(repository);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#isValidUrl(java.lang.String)
	 */
	@Override
	protected boolean isValidUrl(String url) {
		if (url.startsWith(URL_PREFIX_HTTPS) || url.startsWith(URL_PREFIX_HTTP)) {
			try {
				new URL(url);
				return true;
			} catch (MalformedURLException e) {
			}
		}
		return false;
	}
	
	/**
	 * Validator for the repository.
	 * @author 23059115
	 *
	 */
	public class GerritValidator extends Validator {

		final TaskRepository repository;

		/**
		 * Constructor.
		 * @param repository
		 */
		public GerritValidator(TaskRepository repository) {
			this.repository = repository;
		}

		/**
		 * @return repository url
		 */
		public String getRepositoryUrl() {
			return repository.getRepositoryUrl();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage
		 * .Validator#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				new URL(repository.getRepositoryUrl());
			} catch (MalformedURLException ex) {
				throw new CoreException(new Status(IStatus.ERROR,
						Activator.PLUGIN_ID, IStatus.OK,
						"Invalid repository URL", null));
			}

			try {
				// TODO: validate connection
			} catch (Exception e) {
				// TODO: handle
			}
		}
	}

}
