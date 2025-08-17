/*********************************************************************
 * Copyright (c) 2010, 2014 Sony Ericsson/ST Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      GitHub, Inc. - fixes for bug 354753
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritSystemInfo;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * Wizard page to specify Gerrit connection details.
 *
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 */
public class GerritRepositorySettingsPage extends AbstractRepositorySettingsPage {

	public class GerritValidator extends Validator {

		private GerritSystemInfo info;

		final TaskRepository repository;

		public GerritValidator(TaskRepository repository) {
			this.repository = repository;
		}

		public GerritSystemInfo getInfo() {
			return info;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			GerritConnector connector = (GerritConnector) getConnector();
			info = connector.validate(repository, monitor);
		}

		private boolean isSupportedVersion() {
			return info.getCapabilities().isSupported();
		}

	}

	private static class OpenIdProvider {

		private final String name;

		private final String url;

		public OpenIdProvider(String name, String url) {
			this.name = name;
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}

	}

	private Button openIdButton;

	private Combo openIdCombo;

	private final List<OpenIdProvider> openIdProviders = new ArrayList<>();

	public GerritRepositorySettingsPage(TaskRepository taskRepository) {
		super(Messages.GerritRepositorySettingsPage_Gerrit_Repository_Settings,
				Messages.GerritRepositorySettingsPage_Gerrit_Repository_Settings_description, taskRepository);
		setNeedsAnonymousLogin(true);
		setNeedsHttpAuth(true);
		setNeedsAdvanced(true);
		setNeedsEncoding(false);
		setNeedsTimeZone(false);
		setNeedsValidation(true);

		openIdProviders.add(new OpenIdProvider(Messages.GerritRepositorySettingsPage_Google_Account,
				"https://www.google.com/accounts/o8/id")); //$NON-NLS-1$
		openIdProviders
				.add(new OpenIdProvider(Messages.GerritRepositorySettingsPage_Yahoo_Account, "https://me.yahoo.com")); //$NON-NLS-1$
	}

	@SuppressWarnings("restriction")
	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		repository.setProperty(GerritConnector.KEY_REPOSITORY_OPEN_ID_ENABLED,
				Boolean.toString(openIdButton.getSelection()));
		repository.setProperty(GerritConnector.KEY_REPOSITORY_OPEN_ID_PROVIDER, openIdCombo.getText());
		repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY, TaskRepository.CATEGORY_REVIEW);
		repository.removeProperty(GerritConnector.KEY_REPOSITORY_ACCOUNT_ID);
		repository.removeProperty(GerritConnector.KEY_REPOSITORY_AUTH);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		addRepositoryTemplatesToServerUrlCombo();
	}

	@Override
	public String getConnectorKind() {
		return GerritConnector.CONNECTOR_KIND;
	}

	private void updateButtons() {
		openIdCombo.setEnabled(openIdButton.getSelection());
	}

	@Override
	protected void applyValidatorResult(Validator validator) {
		super.applyValidatorResult(validator);
		if (validator.getStatus() != null && validator.getStatus().isOK()) {
			GerritValidator gerritValidator = (GerritValidator) validator;

			String warning = ""; //$NON-NLS-1$
			if (!gerritValidator.isSupportedVersion()) {
				warning = NLS.bind(Messages.GerritRepositorySettingsPage_Gerrit_may_not_be_supported,
						gerritValidator.getInfo().getVersion());
			}

			setMessage(
					NLS.bind(Messages.GerritRepositorySettingsPage_X_Logged_in_as_Y_dot_Z,
							getMessage(), gerritValidator.getInfo().getFullName(), warning),
					warning.isEmpty() ? IMessageProvider.INFORMATION : IMessageProvider.WARNING);
		}
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		openIdButton = new Button(parent, SWT.CHECK);
		openIdButton.setText(Messages.GerritRepositorySettingsPage_OpenID_Authentication);
		openIdButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
		GridDataFactory.fillDefaults().span(2, 1).applyTo(openIdButton);

		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText(Messages.GerritRepositorySettingsPage_Provider);

		openIdCombo = new Combo(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(openIdCombo);
		for (OpenIdProvider provider : openIdProviders) {
			openIdCombo.add(provider.getName());
		}
		openIdCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (openIdCombo.getSelectionIndex() >= 0) {
					openIdCombo.setText(openIdProviders.get(openIdCombo.getSelectionIndex()).getUrl());
				}
			}
		});

		if (repository != null) {
			openIdButton.setSelection(
					Boolean.parseBoolean(repository.getProperty(GerritConnector.KEY_REPOSITORY_OPEN_ID_ENABLED)));
			String value = repository.getProperty(GerritConnector.KEY_REPOSITORY_OPEN_ID_PROVIDER);
			openIdCombo.setText(value != null ? value : ""); //$NON-NLS-1$

			if (openIdButton.getSelection()) {
				if (parent.getParent() instanceof ExpandableComposite) {
					CommonFormUtil.setExpanded((ExpandableComposite) parent.getParent(), true);
				}
			}
		}

		updateButtons();
	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new GerritValidator(repository);
	}

	@Override
	protected void repositoryTemplateSelected(RepositoryTemplate template) {
		repositoryLabelEditor.setStringValue(template.label);
		setUrl(template.repositoryUrl);
		getContainer().updateButtons();
	}

	@Override
	public String getRepositoryUrl() {
		// remove the fragment part of a copy/paste gerrit URL to avoid errors afterwards
		String url = super.getRepositoryUrl();
		int hashIndex = url.indexOf('#');
		if (hashIndex >= 0) {
			url = TaskRepositoryManager.stripSlashes(url.substring(0, hashIndex));
		}
		return url;
	}
}
