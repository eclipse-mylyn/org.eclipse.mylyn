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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.search.BugzillaQueryPageParser;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 */
public class BugzillaRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private static final String TITLE = "Bugzilla Repository Settings";

	private static final String DESCRIPTION = "Example: https://bugs.eclipse.org/bugs (do not include index.cgi)";

	public BugzillaRepositorySettingsPage() {
		super(TITLE, DESCRIPTION);
	}

	protected void createAdditionalControls(Composite parent) {
		// ignore
	}

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete();
	}

	protected void validateSettings() {
		try {
			URL serverURL = new URL(super.serverUrlEditor.getStringValue());
			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(serverURL);
			if (cntx == null || !(cntx instanceof HttpURLConnection)) {
				MessageDialog.openInformation(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, "Could not connect.");
			}

			HttpURLConnection serverConnection = (HttpURLConnection) cntx;
			serverConnection.connect();
			TaskRepository tempRepository = new TaskRepository(repository.getKind(), getServerUrl());
			tempRepository.setAuthenticationCredentials(getUserName(), getPassword());
			new BugzillaQueryPageParser(tempRepository, new NullProgressMonitor());
		} catch (MalformedURLException e) {
			MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, "Server URL is invalid.");
			return;
		} catch (LoginException e) {
			MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
					"Unable to authenticate with server. Login credentials invalid.");
			return;
		} catch (IOException e) {
			MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, "No Bugzilla server found at url");
			return;
		} catch (Throwable t) {
			MessageDialog.openWarning(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
					"Unknown error occured. Check that server url and credentials are valid.");
			return;
		}

		MessageDialog.openInformation(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
				"Authentication credentials are valid.");

		super.getWizard().getContainer().updateButtons();
	}
}
