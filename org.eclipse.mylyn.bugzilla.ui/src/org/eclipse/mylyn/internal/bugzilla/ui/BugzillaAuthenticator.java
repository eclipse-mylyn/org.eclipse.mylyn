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

package org.eclipse.mylyn.internal.bugzilla.ui;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;

import org.eclipse.update.internal.ui.security.Authentication;
import org.eclipse.update.internal.ui.security.UserValidationDialog;

/**
 * Update Manager Authenticator Sadly there can only be one registered per VM
 */
public class BugzillaAuthenticator extends Authenticator {
	// private Authentication savedPasswordAuthentication;

	/*
	 * @see Authenticator#getPasswordAuthentication()
	 */
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		// String protocol = getRequestingProtocol();
		String host = getRequestingHost(); // can be null;
		InetAddress address = getRequestingSite(); // can be null;
		// int port = getRequestingPort();
		String prompt = getRequestingPrompt(); // realm or message, not
		// documented that can be null
		// String scheme = getRequestingScheme(); // not documented that can be
		// null

		String hostString = host;
		if (hostString == null && address != null) {
			address.getHostName();
		}
		if (hostString == null) {
			hostString = ""; //$NON-NLS-1$
		}
		String promptString = prompt;
		if (prompt == null) {
			promptString = ""; //$NON-NLS-1$
		}

		Authentication auth = UserValidationDialog.getAuthentication(hostString, promptString);
		if (auth != null)
			return new PasswordAuthentication(auth.getUser(), auth.getPassword().toCharArray());
		else
			return null;
	}
}
