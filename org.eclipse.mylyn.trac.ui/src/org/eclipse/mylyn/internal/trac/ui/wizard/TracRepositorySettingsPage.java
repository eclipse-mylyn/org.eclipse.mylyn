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

package org.eclipse.mylar.internal.trac.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.trac.MylarTracPlugin;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracClientFactory;
import org.eclipse.mylar.internal.trac.core.TracException;
import org.eclipse.mylar.internal.trac.core.TracLoginException;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
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

	private static final String MESSAGE_FAILURE_UNKNOWN = "Unknown error occured. Check that server url and credentials are valid.";

	private static final String TITLE = "Trac Repository Settings";

	private static final String DESCRIPTION = "Example: http://projects.edgewall.com/trac";

	private Combo accessTypeCombo;

	/** Supported access types. */
	private Version[] versions;

	public TracRepositorySettingsPage(AbstractRepositoryConnector connector) {
		super(TITLE, DESCRIPTION, connector);

		setNeedsAnonymousLogin(true);
		setNeedsEncoding(false);
		setNeedsTimeZone(false);
	}

	protected void createAdditionalControls(final Composite parent) {
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
					setVersion(versions[accessTypeCombo.getSelectionIndex()].name());
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

	private Version getTracVersion() {
		if (accessTypeCombo.getSelectionIndex() == 0) {
			return null;
		} else {
			return versions[accessTypeCombo.getSelectionIndex() - 1];
		}
	}

	private void setTracVersion(Version version) {
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

	protected void validateSettings() {

		try {
			final String serverUrl = getServerUrl();
			final Version version = getTracVersion();
			final String username = getUserName();
			final String password = getPassword();

			final Version[] result = new Version[1];
			getWizard().getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Validating server settings", IProgressMonitor.UNKNOWN);
					try {
						if (version != null) {
							ITracClient client = TracClientFactory.createClient(serverUrl, version, username, password);
							client.validate();
						} else {
							result[0] = TracClientFactory.probeClient(serverUrl, username, password);
						}

					} catch (Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});

			MessageDialog.openInformation(null, MylarTracPlugin.TITLE_MESSAGE_DIALOG,
					"Authentication credentials are valid.");

			if (result[0] != null) {
				setTracVersion(result[0]);
			}
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof MalformedURLException) {
				MessageDialog.openWarning(null, MylarTracPlugin.TITLE_MESSAGE_DIALOG, "Repository url is invalid.");
			} else if (e.getCause() instanceof TracLoginException) {
				MessageDialog.openWarning(null, MylarTracPlugin.TITLE_MESSAGE_DIALOG,
						"Unable to authenticate with repository. Login credentials invalid.");
			} else if (e.getCause() instanceof TracException) {
				MessageDialog
						.openWarning(null, MylarTracPlugin.TITLE_MESSAGE_DIALOG, "No Trac repository found at url");
			} else {
				MessageDialog.openWarning(null, MylarTracPlugin.TITLE_MESSAGE_DIALOG, MESSAGE_FAILURE_UNKNOWN);
			}
		} catch (InterruptedException e) {
			MessageDialog.openWarning(null, MylarTracPlugin.TITLE_MESSAGE_DIALOG, MESSAGE_FAILURE_UNKNOWN);
		}

		super.getWizard().getContainer().updateButtons();
	}

}
