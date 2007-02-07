/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.core.net.WebClientUtil;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaClientFactory;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.MylarStatus;
import org.eclipse.mylar.tasks.core.RepositoryTemplate;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositorySettingsPage extends AbstractRepositorySettingsPage {

	public static final String LABEL_AUTOMATIC_VERSION = "Automatic (Use Validate Settings)";

	private static final String TITLE = "Bugzilla Repository Settings";

	private static final String DESCRIPTION = "Example: https://bugs.eclipse.org/bugs (do not include index.cgi)";

	protected Combo repositoryVersionCombo;

	public BugzillaRepositorySettingsPage(AbstractRepositoryConnectorUi repositoryUi) {
		super(TITLE, DESCRIPTION, repositoryUi);
		setNeedsAnonymousLogin(true);
		setNeedsEncoding(true);
		setNeedsTimeZone(false);
		setNeedsHttpAuth(true);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {

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
					// setAnonymous(info.anonymous);
					setBugzillaVersion(template.version);
					if (template.characterEncoding != null) {
						setEncoding(template.characterEncoding);
					}
					getContainer().updateButtons();
					return;
				}
			}
		});

		Label repositoryVersionLabel = new Label(parent, SWT.NONE);
		repositoryVersionLabel.setText("Repository Version: ");
		repositoryVersionCombo = new Combo(parent, SWT.READ_ONLY);

		repositoryVersionCombo.add(LABEL_AUTOMATIC_VERSION);

		for (String version : getConnector().getSupportedVersions()) {
			repositoryVersionCombo.add(version);
		}
		if (repository != null && repositoryVersionCombo.indexOf(repository.getVersion()) >= 0) {
			repositoryVersionCombo.select(repositoryVersionCombo.indexOf(repository.getVersion()));
		} else {
			int defaultIndex = getConnector().getSupportedVersions().size() - 1;
			repositoryVersionCombo.select(defaultIndex);
			setVersion(repositoryVersionCombo.getItem(defaultIndex));
		}

		repositoryVersionCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (repositoryVersionCombo.getSelectionIndex() >= 0) {
					setVersion(repositoryVersionCombo.getItem(repositoryVersionCombo.getSelectionIndex()));
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
	}

	public void setBugzillaVersion(String version) {
		if (version == null) {
			repositoryVersionCombo.select(0);
		} else {
			int i = repositoryVersionCombo.indexOf(version.toString());
			if (i != -1) {
				repositoryVersionCombo.select(i);
				setVersion(version);
			} else {
				for (IBugzillaConstants.BugzillaServerVersion serverVersion : IBugzillaConstants.BugzillaServerVersion
						.values()) {
					if (version.startsWith(serverVersion.toString())) {
						i = repositoryVersionCombo.indexOf(serverVersion.toString());
						if (i != -1) {
							repositoryVersionCombo.select(i);
							setVersion(serverVersion.toString());
							break;
						}
					}
				}
				if (i == -1) {
					MylarStatusHandler.log("Could not resolve repository version: " + version, this);
					setVersion(IBugzillaConstants.BugzillaServerVersion.SERVER_218.toString());
				}
			}
		}
	}

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete();
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

	/* public for testing */
	@Override
	public void validateSettings() {

		final String serverUrl = getServerUrl();
		final String newUserId = getUserName();
		final String newPassword = getPassword();
		final boolean isAnonymous = isAnonymousAccess();
		final String newEncoding = getCharacterEncoding();
		final String httpAuthUser = getHttpAuthUserId();
		final String httpAuthPass = getHttpAuthPassword();
		final Proxy tempProxy;
		try {
			setMessage("Validating server settings...");
			setErrorMessage(null);
			if (getUseDefaultProxy()) {
				tempProxy = TaskRepository.getSystemProxy();
			} else {
				tempProxy = WebClientUtil.getProxy(getProxyHostname(), getProxyPort(), getProxyUsername(),
						getProxyPassword());
			}
			final boolean checkVersion = repositoryVersionCombo.getSelectionIndex() == 0;
			final String[] version = new String[1];
			getWizard().getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (monitor == null) {
						monitor = new NullProgressMonitor();
					}
					try {
						monitor.beginTask("Validating server settings", IProgressMonitor.UNKNOWN);
						BugzillaClient client = null;
						if (isAnonymous) {
							client = BugzillaClientFactory.createClient(serverUrl, newUserId, newPassword,
									httpAuthUser, httpAuthPass, tempProxy, newEncoding);
							client.logout();
						} else if (version != null) {
							client = BugzillaClientFactory.createClient(serverUrl, newUserId, newPassword,
									httpAuthUser, httpAuthPass, tempProxy, newEncoding);
							client.validate();
						}
						if (checkVersion && client != null) {
							RepositoryConfiguration config = client.getRepositoryConfiguration();
							if (config != null) {
								version[0] = config.getInstallVersion();
							}
						}

					} catch (Exception ex) {
						throw new InvocationTargetException(ex);

					} finally {
						monitor.done();
					}
				}
			});

			if (version[0] != null) {
				setBugzillaVersion(version[0]);
			}

			if (!isAnonymous) {
				setMessage("Valid Bugzilla server found and your login was accepted");
			} else {
				setMessage("Valid Bugzilla server found");
			}
		} catch (InvocationTargetException e) {
			setMessage(null);
			displayError(serverUrl, e.getTargetException());

		} catch (InterruptedException e) {
			setErrorMessage("Could not connect to Bugzilla server or authentication failed");
		}
	}

	private void displayError(final String serverUrl, Throwable e) {
		IStatus status;
		if (e instanceof MalformedURLException) {
			status = new MylarStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID, IMylarStatusConstants.NETWORK_ERROR,
					"Server URL is invalid.");
		} else if (e instanceof CoreException) {
			status = ((CoreException) e).getStatus();
		} else if (e instanceof IOException) {
			status = new MylarStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID, IMylarStatusConstants.IO_ERROR,
					serverUrl, e.getMessage());
		} else {
			status = new MylarStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID, IMylarStatusConstants.NETWORK_ERROR,
					serverUrl, e.getMessage());
		}
		MylarStatusHandler.displayStatus("Validation failed", status);
	}
}