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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Mik Kersten
 */
public class BugzillaRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private static final String MESSAGE_FAILURE_UNKNOWN = "Unknown error occured. Check that server url and credentials are valid.";

	private static final String TITLE = "Bugzilla Repository Settings";

	private static final String DESCRIPTION = "Example: https://bugs.eclipse.org/bugs (do not include index.cgi)";

	private AbstractRepositoryConnector connector;

	protected Combo repositoryVersionCombo;

	public BugzillaRepositorySettingsPage(AbstractRepositoryConnector connector) {
		super(TITLE, DESCRIPTION);
		this.connector = connector;
	}

	protected void createAdditionalControls(Composite parent) {
		Label repositoryVersionLabel = new Label(parent, SWT.NONE);
		repositoryVersionLabel.setText("Repository Version: ");
		repositoryVersionCombo = new Combo(parent, SWT.READ_ONLY);

		for (String version : connector.getSupportedVersions()) {
			repositoryVersionCombo.add(version);
		}
		if (repository != null && repositoryVersionCombo.indexOf(repository.getVersion()) >= 0) {
			repositoryVersionCombo.select(repositoryVersionCombo.indexOf(repository.getVersion()));
		} else {
			int defaultIndex = connector.getSupportedVersions().size() - 1;
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

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete();
	}

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

	protected void validateSettings() {

		try {
			final URL serverURL = new URL(super.getServerUrl());
			final String serverUrl = getServerUrl();
			final String newUserId = getUserName();
			final String newPassword = getPassword();
			getWizard().getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Validating server settings", IProgressMonitor.UNKNOWN);
					try {
						Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
						URLConnection cntx = BugzillaPlugin.getUrlConnection(serverURL, proxySettings);
						if (cntx == null || !(cntx instanceof HttpURLConnection)) {
							throw new MalformedURLException();
						}

						HttpURLConnection serverConnection = (HttpURLConnection) cntx;
						serverConnection.connect();

						BugzillaRepositoryUtil.validateCredentials(serverUrl, newUserId, newPassword);

					} catch (Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});

			MessageDialog.openInformation(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
					"Authentication credentials are valid.");
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof MalformedURLException) {
				MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, "Server URL is invalid.");
			} else if (e.getCause() instanceof LoginException) {
				MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
						"Unable to authenticate with server. Login credentials invalid.");
			} else if (e.getCause() instanceof IOException) {
				MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
						"No Bugzilla server found at url");
			} else {
				MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, MESSAGE_FAILURE_UNKNOWN);
			}
		} catch (MalformedURLException e) {
			MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, "Server URL is invalid.");
		} catch (InterruptedException e) {
			MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, MESSAGE_FAILURE_UNKNOWN);
		}

		super.getWizard().getContainer().updateButtons();
	}
}
