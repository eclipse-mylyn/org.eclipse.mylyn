/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      GitHub, Inc. - fixes for bug 354753      
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritSystemInfo;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Wizard page to specify Gerrit connection details.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 */
public class GerritRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private Label statusLabel;

	public GerritRepositorySettingsPage(TaskRepository taskRepository) {
		super("Gerrit Repository Settings", "Web based code review and project management for Git based projects.",
				taskRepository);
		setNeedsAnonymousLogin(true);
		setNeedsHttpAuth(false);
		setNeedsAdvanced(false);
		setNeedsEncoding(false);
		setNeedsTimeZone(false);
		setNeedsValidation(true);
	}

	@SuppressWarnings("restriction")
	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY, IRepositoryConstants.CATEGORY_REVIEW);
		repository.removeProperty(GerritConnector.KEY_REPOSITORY_ACCOUNT_ID);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		addRepositoryTemplatesToServerUrlCombo();
	}

	@Override
	protected void applyValidatorResult(Validator validator) {
		super.applyValidatorResult(validator);
		if (validator.getStatus() != null && validator.getStatus().isOK()) {
			GerritValidator gerritValidator = (GerritValidator) validator;
			statusLabel.setText(NLS.bind("Logged in as {0}.", gerritValidator.getInfo().getFullName()));
		} else {
			statusLabel.setText(" ");
		}
		statusLabel.getParent().layout();
	}

	@Override
	protected void createContributionControls(Composite parent) {
		// ignore, task editor settings are not supported
		statusLabel = new Label(parent, SWT.WRAP);
		statusLabel.setText(" "); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(0, 10).grab(true, false).span(3, SWT.DEFAULT).applyTo(statusLabel);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// ignore, advanced section is disabled 
	}

	@Override
	protected void repositoryTemplateSelected(RepositoryTemplate template) {
		repositoryLabelEditor.setStringValue(template.label);
		setUrl(template.repositoryUrl);
		getContainer().updateButtons();
	}

	@Override
	public String getConnectorKind() {
		return GerritConnector.CONNECTOR_KIND;
	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new GerritValidator(repository);
	}

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

	public class GerritValidator extends Validator {

		final TaskRepository repository;

		private GerritSystemInfo info;

		public GerritValidator(TaskRepository repository) {
			this.repository = repository;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			GerritConnector connector = (GerritConnector) getConnector();
			info = connector.validate(repository, monitor);
		}

		public GerritSystemInfo getInfo() {
			return info;
		}

	}

}
