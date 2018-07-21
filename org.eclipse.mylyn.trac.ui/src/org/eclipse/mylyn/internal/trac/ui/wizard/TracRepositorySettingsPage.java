/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *     BREDEX GmbH - fix for bug 295050
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.wizard;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.model.TracRepositoryInfo;
import org.eclipse.mylyn.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Steffen Pingel
 */
public class TracRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private static final String TITLE = Messages.TracRepositorySettingsPage_Trac_Repository_Settings;

	private static final String DESCRIPTION = Messages.TracRepositorySettingsPage_EXAMPLE_HTTP_TRAC_EDGEWALL_ORG;

	private Combo accessTypeCombo;

	/** Supported access types. */
	private Version[] versions;

	public TracRepositorySettingsPage(TaskRepository taskRepository) {
		super(TITLE, DESCRIPTION, taskRepository);
		setNeedsCertAuth(true);
		setNeedsAnonymousLogin(true);
		setNeedsEncoding(false);
		setNeedsTimeZone(false);
	}

	@Override
	protected void repositoryTemplateSelected(RepositoryTemplate template) {
		repositoryLabelEditor.setStringValue(template.label);
		setUrl(template.repositoryUrl);
		setAnonymous(template.anonymous);

		try {
			Version version = Version.valueOf(template.version);
			setTracVersion(version);
		} catch (RuntimeException ex) {
			setTracVersion(Version.TRAC_0_9);
		}

		getContainer().updateButtons();
	}

	@Override
	protected void createAdditionalControls(final Composite parent) {
		addRepositoryTemplatesToServerUrlCombo();

		Label accessTypeLabel = new Label(parent, SWT.NONE);
		accessTypeLabel.setText(Messages.TracRepositorySettingsPage_Access_Type_);
		accessTypeCombo = new Combo(parent, SWT.READ_ONLY);

		accessTypeCombo.add(Messages.TracRepositorySettingsPage_Automatic__Use_Validate_Settings_);
		versions = Version.values();
		for (Version version : versions) {
			accessTypeCombo.add(version.toString());
		}
		if (repository != null) {
			setTracVersion(Version.fromVersion(repository.getVersion()));
		} else {
			setTracVersion(null);
		}
		accessTypeCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (accessTypeCombo.getSelectionIndex() > 0) {
					setVersion(versions[accessTypeCombo.getSelectionIndex() - 1].name());
				}
				getWizard().getContainer().updateButtons();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
	}

	@Override
	public boolean isPageComplete() {
		// make sure "Automatic" is not selected as a version
		return super.isPageComplete() && accessTypeCombo != null && accessTypeCombo.getSelectionIndex() != 0;
	}

	@Override
	protected boolean isValidUrl(String url) {
		boolean isValid = super.isValidUrl(url);
		return isValid && !url.endsWith("/"); //$NON-NLS-1$
	}

	public Version getTracVersion() {
		if (accessTypeCombo.getSelectionIndex() == 0) {
			return null;
		} else {
			return versions[accessTypeCombo.getSelectionIndex() - 1];
		}
	}

	public void setTracVersion(Version version) {
		if (version == null) {
			// select "Automatic"
			accessTypeCombo.select(0);
		} else {
			int i = accessTypeCombo.indexOf(version.toString());
			if (i != -1) {
				accessTypeCombo.select(i);
			}
			setVersion(version.name());
		}
	}

	@Override
	protected void applyValidatorResult(Validator validator) {
		if (((TracValidator) validator).getResult() != null) {
			setTracVersion(((TracValidator) validator).getResult());
			getContainer().updateButtons();
		}
		super.applyValidatorResult(validator);
	}

	// public for testing
	public class TracValidator extends Validator {

		private final String repositoryUrl;

		private final TaskRepository taskRepository;

		private Version version;

		private Version result;

		public TracValidator(TaskRepository taskRepository, Version version) {
			this.repositoryUrl = taskRepository.getRepositoryUrl();
			this.taskRepository = taskRepository;
			this.version = version;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				//validate(Provider.of(monitor));
				validate(monitor);
			} catch (MalformedURLException e) {
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.ID_PLUGIN, INVALID_REPOSITORY_URL));
			} catch (TracLoginException e) {
				if (e.isNtlmAuthRequested()) {
					AuthenticationCredentials credentials = taskRepository.getCredentials(AuthenticationType.REPOSITORY);
					if (credentials == null || credentials.getUserName() == null || credentials.getPassword() == null) {
						throw new CoreException(new RepositoryStatus(IStatus.ERROR, TracUiPlugin.ID_PLUGIN,
								RepositoryStatus.ERROR_EMPTY_PASSWORD,
								Messages.TracRepositorySettingsPage_auth_failed_missing_credentials, e));
					}
					if (!credentials.getUserName().contains("\\")) { //$NON-NLS-1$
						throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
								TracUiPlugin.ID_PLUGIN,
								Messages.TracRepositorySettingsPage_NTLM_authentication_requested_Error));
					}
				}
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.ID_PLUGIN, INVALID_LOGIN));
			} catch (TracPermissionDeniedException e) {
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.ID_PLUGIN, "Insufficient permissions for selected access type.")); //$NON-NLS-1$
			} catch (TracException e) {
				String message = Messages.TracRepositorySettingsPage_No_Trac_repository_found_at_url;
				if (e.getMessage() != null) {
					message += ": " + e.getMessage(); //$NON-NLS-1$
				}
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.ID_PLUGIN, message));
			}
		}

		public void validate(IProgressMonitor monitor) throws MalformedURLException, TracException {
			AbstractWebLocation location = new TaskRepositoryLocationFactory().createWebLocation(taskRepository);

			TracRepositoryInfo info;
			if (version != null) {
				ITracClient client = TracClientFactory.createClient(location, version);
				info = client.validate(monitor);
			} else {
				// probe version: XML-RPC access first, then web
				// access
				try {
					version = Version.XML_RPC;
					ITracClient client = TracClientFactory.createClient(location, version);
					info = client.validate(monitor);
				} catch (TracException e) {
					try {
						version = Version.TRAC_0_9;
						ITracClient client = TracClientFactory.createClient(location, version);
						info = client.validate(monitor);

						if (e instanceof TracPermissionDeniedException) {
							setStatus(RepositoryStatus.createStatus(repositoryUrl, IStatus.INFO,
									TracUiPlugin.ID_PLUGIN,
									Messages.TracRepositorySettingsPage_Authentication_credentials_are_valid));
						}
					} catch (TracLoginException e2) {
						throw e;
					} catch (TracException e2) {
						throw new TracException();
					}
				}
				result = version;
			}

			if (version == Version.XML_RPC //
					&& (info.isApiVersion(1, 0, 0) //
							|| (info.isApiVersionOrHigher(1, 0, 3) && info.isApiVersionOrSmaller(1, 0, 5)))) {
				setStatus(RepositoryStatus.createStatus(
						repositoryUrl,
						IStatus.INFO,
						TracUiPlugin.ID_PLUGIN,
						Messages.TracRepositorySettingsPage_Authentication_credentials_valid_Update_to_latest_XmlRpcPlugin_Warning));
			}

			MultiStatus status = new MultiStatus(TracUiPlugin.ID_PLUGIN, 0, NLS.bind("Validation results for {0}", //$NON-NLS-1$
					taskRepository.getRepositoryLabel()), null);
			status.add(new Status(IStatus.INFO, TracUiPlugin.ID_PLUGIN, NLS.bind("Version: {0}", info.toString()))); //$NON-NLS-1$
			status.add(new Status(IStatus.INFO, TracUiPlugin.ID_PLUGIN,
					NLS.bind("Access Type: {0}", version.toString()))); //$NON-NLS-1$
			StatusHandler.log(status);
		}

		public Version getResult() {
			return result;
		}

	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new TracValidator(repository, getTracVersion());
	}

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.CONNECTOR_KIND;
	}

}
