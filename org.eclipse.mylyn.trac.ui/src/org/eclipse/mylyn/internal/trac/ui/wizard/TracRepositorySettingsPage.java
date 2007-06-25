/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.wizard;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Steffen Pingel
 */
public class TracRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private static final String TITLE = "Trac Repository Settings";

	private static final String DESCRIPTION = "Example: http://trac.edgewall.org";

	private Combo accessTypeCombo;

	/** Supported access types. */
	private Version[] versions;

	public TracRepositorySettingsPage(AbstractRepositoryConnectorUi repositoryUi) {
		super(TITLE, DESCRIPTION, repositoryUi);

		setNeedsAnonymousLogin(true);
		setNeedsEncoding(false);
		setNeedsTimeZone(false);
	}

	@Override
	protected void createAdditionalControls(final Composite parent) {

		for (RepositoryTemplate template : connector.getTemplates()) {
			serverUrlCombo.add(template.label);
		}
		serverUrlCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = serverUrlCombo.getText();
				RepositoryTemplate template = connector.getTemplate(text);
				if (template != null) {
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
					return;
				}
			}
		});

		Label accessTypeLabel = new Label(parent, SWT.NONE);
		accessTypeLabel.setText("Access Type: ");
		accessTypeCombo = new Combo(parent, SWT.READ_ONLY);

		accessTypeCombo.add("Automatic (Use Validate Settings)");
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
	protected boolean isValidUrl(String name) {
		if ((name.startsWith(URL_PREFIX_HTTPS) || name.startsWith(URL_PREFIX_HTTP)) && !name.endsWith("/")) {
			try {
				new URL(name);
				return true;
			} catch (MalformedURLException e) {
			}
		}
		return false;
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
		super.applyValidatorResult(validator);

		if (((TracValidator) validator).getResult() != null) {
			setTracVersion(((TracValidator) validator).getResult());
		}
	}

	// public for testing
	public class TracValidator extends Validator {

		final String repositoryUrl;

		final Version version;

		final String username;

		final String password;

		final Proxy proxy;

		private Version result;

		public TracValidator(TaskRepository repository, Version version) {
			this.repositoryUrl = repository.getUrl();
			this.username = repository.getUserName();
			this.password = repository.getPassword();
			this.proxy = repository.getProxy();
			this.version = version;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				validate();
			} catch (MalformedURLException e) {
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.PLUGIN_ID, INVALID_REPOSITORY_URL));
			} catch (TracLoginException e) {
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.PLUGIN_ID, INVALID_LOGIN));
			} catch (TracPermissionDeniedException e) {
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.PLUGIN_ID, "Insufficient permissions for selected access type."));
			} catch (TracException e) {
				String message = "No Trac repository found at url";
				if (e.getMessage() != null) {
					message += ": " + e.getMessage();
				}
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.PLUGIN_ID, message));
			}
		}

		public void validate() throws MalformedURLException, TracException {
			if (version != null) {
				ITracClient client = TracClientFactory.createClient(repositoryUrl, version, username, password, proxy);
				client.validate();
			} else {
				// probe version: XML-RPC access first, then web
				// access
				try {
					ITracClient client = TracClientFactory.createClient(repositoryUrl, Version.XML_RPC, username,
							password, proxy);
					client.validate();
					result = Version.XML_RPC;
				} catch (TracException e) {
					try {
						ITracClient client = TracClientFactory.createClient(repositoryUrl, Version.TRAC_0_9, username,
								password, proxy);
						client.validate();
						result = Version.TRAC_0_9;

						if (e instanceof TracPermissionDeniedException) {
							setStatus(RepositoryStatus.createStatus(repositoryUrl, IStatus.INFO,
									TracUiPlugin.PLUGIN_ID,
									"Authentication credentials are valid. Note: Insufficient permissions for XML-RPC access, falling back to web access."));
						}
					} catch (TracLoginException e2) {
						throw e;
					} catch (TracException e2) {
						throw new TracException();
					}
				}
			}
		}

		public Version getResult() {
			return result;
		}

	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new TracValidator(repository, getTracVersion());
	}

}
