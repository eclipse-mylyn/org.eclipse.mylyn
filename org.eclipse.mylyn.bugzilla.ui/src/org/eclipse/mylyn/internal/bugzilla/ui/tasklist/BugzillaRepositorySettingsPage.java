/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaLanguageSettings;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaStatus;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private static final String TOOLTIP_AUTODETECTION_ENABLED = "Override auto detection of Platform and OS for new bug reports.";

	private static final String TOOLTIP_AUTODETECTION_DISABLED = "Available once repository has been created.";

	private static final String LABEL_SHORT_LOGINS = "Local users enabled:";

	private static final String LABEL_VERSION_NUMBER = "2.18 - 3.0";

	private static final String TITLE = "Bugzilla Repository Settings";

	private static final String DESCRIPTION = "Supports Bugzilla " + LABEL_VERSION_NUMBER
			+ "\nExample: https://bugs.eclipse.org/bugs (do not include index.cgi)";

	protected Button autodetectPlatformOS;

	protected Combo defaultPlatformCombo;

	protected Combo defaultOSCombo;

	private Button cleanQAContact;

	private RepositoryConfiguration repositoryConfiguration = null;

	private String platform = null;

	private String os = null;

	private Combo languageSettingCombo;

	public BugzillaRepositorySettingsPage(TaskRepository taskRepository) {
		super(TITLE, DESCRIPTION, taskRepository);
		setNeedsAnonymousLogin(true);
		setNeedsEncoding(true);
		setNeedsTimeZone(false);
		setNeedsHttpAuth(true);
	}

	@Override
	protected void repositoryTemplateSelected(RepositoryTemplate template) {
		repositoryLabelEditor.setStringValue(template.label);
		setUrl(template.repositoryUrl);
		// setAnonymous(info.anonymous);
		if (template.characterEncoding != null) {
			setEncoding(template.characterEncoding);
		}
		getContainer().updateButtons();

	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		addRepositoryTemplatesToServerUrlCombo();

		Label shortLoginLabel = new Label(parent, SWT.NONE);
		shortLoginLabel.setText(LABEL_SHORT_LOGINS);
		cleanQAContact = new Button(parent, SWT.CHECK | SWT.LEFT);
		if (repository != null) {
			boolean shortLogin = Boolean.parseBoolean(repository.getProperty(IBugzillaConstants.REPOSITORY_SETTING_SHORT_LOGIN));
			cleanQAContact.setSelection(shortLogin);
		}

		if (null != repository) {
			repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository.getRepositoryUrl());
			platform = repository.getProperty(IBugzillaConstants.BUGZILLA_DEF_PLATFORM);
			os = repository.getProperty(IBugzillaConstants.BUGZILLA_DEF_OS);
		}

		Label defaultPlatformLabel = new Label(parent, SWT.NONE);
		defaultPlatformLabel.setText("Autodetect platform and os");
		if (null == repository) {
			defaultPlatformLabel.setToolTipText(TOOLTIP_AUTODETECTION_DISABLED);
		} else {
			defaultPlatformLabel.setToolTipText(TOOLTIP_AUTODETECTION_ENABLED);
		}

		Composite platformOSContainer = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		platformOSContainer.setLayout(gridLayout);

		autodetectPlatformOS = new Button(platformOSContainer, SWT.CHECK);
		autodetectPlatformOS.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (autodetectPlatformOS.isEnabled() && repositoryConfiguration == null
						&& !autodetectPlatformOS.getSelection()) {
					try {
						getWizard().getContainer().run(true, false, new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor) throws InvocationTargetException,
									InterruptedException {
								try {
									monitor.beginTask("Retrieving repository configuration", IProgressMonitor.UNKNOWN);
									repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository,
											false, monitor);
									if (repositoryConfiguration != null) {
										platform = repository.getProperty(IBugzillaConstants.BUGZILLA_DEF_PLATFORM);
										os = repository.getProperty(IBugzillaConstants.BUGZILLA_DEF_OS);
										PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

											public void run() {
												populatePlatformCombo();
												populateOsCombo();
											}
										});
									}
								} catch (CoreException e) {
									throw new InvocationTargetException(e);
								} finally {
									monitor.done();
								}

							}

						});
					} catch (InvocationTargetException e1) {
						if (e1.getCause() != null) {
							setErrorMessage(e1.getCause().getMessage());
						}
					} catch (InterruptedException e1) {
						// ignore
					}
				}
				defaultPlatformCombo.setEnabled(!autodetectPlatformOS.getSelection());
				defaultOSCombo.setEnabled(!autodetectPlatformOS.getSelection());
			}

		});
		autodetectPlatformOS.setEnabled(null != repository);
		if (null == repository) {
			autodetectPlatformOS.setToolTipText(TOOLTIP_AUTODETECTION_DISABLED);
		} else {
			autodetectPlatformOS.setToolTipText(TOOLTIP_AUTODETECTION_ENABLED);
		}
		autodetectPlatformOS.setSelection(null == platform && null == os);

		defaultPlatformCombo = new Combo(platformOSContainer, SWT.READ_ONLY);
		populatePlatformCombo();

		defaultOSCombo = new Combo(platformOSContainer, SWT.READ_ONLY);
		populateOsCombo();

		new Label(parent, SWT.NONE).setText("Language: ");
		languageSettingCombo = new Combo(parent, SWT.DROP_DOWN);

		for (BugzillaLanguageSettings bugzillaLanguageSettings : BugzillaCorePlugin.getDefault().getLanguageSettings()) {
			languageSettingCombo.add(bugzillaLanguageSettings.getLanguageName());
		}
		if (repository != null) {
			String language = repository.getProperty(IBugzillaConstants.BUGZILLA_LANGUAGE_SETTING);
			if (language != null && !language.equals("") && languageSettingCombo.indexOf(language) >= 0) {
				languageSettingCombo.select(languageSettingCombo.indexOf(language));
			}
		}
		if (languageSettingCombo.getSelectionIndex() == -1) {
			if (languageSettingCombo.indexOf(IBugzillaConstants.DEFAULT_LANG) >= 0) {
				languageSettingCombo.select(languageSettingCombo.indexOf(IBugzillaConstants.DEFAULT_LANG));
			}
		}
	}

	private void populateOsCombo() {
		if (null != repositoryConfiguration && defaultOSCombo != null) {
			defaultOSCombo.removeAll();
			List<String> optionValues = repositoryConfiguration.getOSs();
			for (String option : optionValues) {
				defaultOSCombo.add(option.toString());
			}
			if (null != os && defaultOSCombo.indexOf(os) >= 0) {
				defaultOSCombo.select(defaultOSCombo.indexOf(os));
			} else {
				// remove value if no longer exists and set to All!
				repository.removeProperty(IBugzillaConstants.BUGZILLA_DEF_OS);
				defaultOSCombo.select(0);
			}
		} else {
			defaultOSCombo.add("All");
			defaultOSCombo.select(0);
		}
		defaultOSCombo.getParent().pack(true);
		defaultOSCombo.setEnabled(!autodetectPlatformOS.getSelection());
	}

	private void populatePlatformCombo() {
		if (null != repositoryConfiguration && defaultPlatformCombo != null) {
			defaultPlatformCombo.removeAll();
			List<String> optionValues = repositoryConfiguration.getPlatforms();
			for (String option : optionValues) {
				defaultPlatformCombo.add(option.toString());
			}
			if (null != platform && defaultPlatformCombo.indexOf(platform) >= 0) {
				defaultPlatformCombo.select(defaultPlatformCombo.indexOf(platform));
			} else {
				// remove value if no longer exists and set to All!
				repository.removeProperty(IBugzillaConstants.BUGZILLA_DEF_PLATFORM);
				defaultPlatformCombo.select(0);
			}
		} else {
			defaultPlatformCombo.add("All");
			defaultPlatformCombo.select(0);
		}
		defaultPlatformCombo.getParent().pack(true);
		defaultPlatformCombo.setEnabled(!autodetectPlatformOS.getSelection());
	}

	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		repository.setProperty(IBugzillaConstants.REPOSITORY_SETTING_SHORT_LOGIN,
				String.valueOf(cleanQAContact.getSelection()));
		repository.setProperty(IBugzillaConstants.BUGZILLA_LANGUAGE_SETTING, languageSettingCombo.getText());

		if (!autodetectPlatformOS.getSelection()) {
			repository.setProperty(IBugzillaConstants.BUGZILLA_DEF_PLATFORM,
					String.valueOf(defaultPlatformCombo.getItem(defaultPlatformCombo.getSelectionIndex())));
			repository.setProperty(IBugzillaConstants.BUGZILLA_DEF_OS,
					String.valueOf(defaultOSCombo.getItem(defaultOSCombo.getSelectionIndex())));
		} else {
			repository.removeProperty(IBugzillaConstants.BUGZILLA_DEF_PLATFORM);
			repository.removeProperty(IBugzillaConstants.BUGZILLA_DEF_OS);
		}
	}

	@Override
	protected boolean isValidUrl(String name) {
		if (name.startsWith(URL_PREFIX_HTTPS) || name.startsWith(URL_PREFIX_HTTP)) {
			try {
				new URL(name);
				return true;
			} catch (MalformedURLException e) {
			}
		}
		return false;
	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new BugzillaValidator(repository);
	}

	public class BugzillaValidator extends Validator {

		final TaskRepository repository;

		public BugzillaValidator(TaskRepository repository) {
			this.repository = repository;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				validate(monitor);
			} catch (Exception e) {
				displayError(repository.getRepositoryUrl(), e);
			}
		}

		private void displayError(final String serverUrl, Throwable e) {
			IStatus status;
			if (e instanceof MalformedURLException) {
				status = new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, "Server URL is invalid.");
			} else if (e instanceof CoreException) {
				status = ((CoreException) e).getStatus();
			} else if (e instanceof IOException) {
				status = new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN, RepositoryStatus.ERROR_IO,
						serverUrl, e.getMessage());
			} else {
				status = new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, serverUrl, e.getMessage());
			}
			setStatus(status);
		}

		public void validate(IProgressMonitor monitor) throws IOException, CoreException {

			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			try {
				monitor.beginTask("Validating server settings", IProgressMonitor.UNKNOWN);
				BugzillaClient client = null;

				client = BugzillaClientFactory.createClient(repository);
				client.validate(monitor);
			} finally {
				monitor.done();
			}
		}

	}

	@Override
	public String getConnectorKind() {
		return BugzillaCorePlugin.CONNECTOR_KIND;
	}

}
