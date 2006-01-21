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

package org.eclipse.mylar.bugzilla.core;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.tasklist.ui.wizards.RepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 */
public class BugzillaRepositorySettingsPage extends RepositorySettingsPage {

	private static final String TITLE = "Bugzilla Repository Settings";
	
	private static final String DESCRIPTION = "Example: https://bugs.eclipse.org/bugs (do not include index.cgi)";

	private Button validateServerButton;
	
	public BugzillaRepositorySettingsPage() {
		super(TITLE, DESCRIPTION);
	}

	protected void createAdditionalControls(Composite parent) {
		validateServerButton = new Button(parent, SWT.PUSH);
		validateServerButton.setText("Validate Server URL");
		validateServerButton.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
				
			}

			public void mouseDown(MouseEvent e) {
				// ignore
				
			}

			public void mouseUp(MouseEvent e) {
				validateServer();
			}
			
		});
		// ignore
	}
	
	@Override
	public boolean isPageComplete() {
		return super.isPageComplete();
	}
	
	private void validateServer() {
		try {
			URL serverURL = new URL(super.serverUrlEditor.getStringValue());
			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(serverURL);
			if (cntx == null || !(cntx instanceof HttpURLConnection)) {
				MessageDialog.openInformation(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, 
						"Could not connect.");
			}
			
			HttpURLConnection serverConnection = (HttpURLConnection) cntx;

			serverConnection.connect();

			int responseCode = serverConnection.getResponseCode();

			if (responseCode != HttpURLConnection.HTTP_OK) {
				MessageDialog.openInformation(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, 
						"No Bugzilla server found at: " + serverURL);
			} else {
				MessageDialog.openInformation(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, 
					"Valid Bugzilla server found at: " + serverURL);
				super.getWizard().getContainer().updateButtons();
			}
			getWizard().getContainer().updateButtons();
		} catch (Exception e) {
			if (!MessageDialog.openQuestion(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, 
					"Could not connect: " + e.getMessage())) {
			}
		}
	}
}
