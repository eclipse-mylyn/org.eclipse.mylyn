/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * @since 3.9
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.commons.ui.dialogs.messages"; //$NON-NLS-1$

	public static String CredentialsDialog_Authentication;

	public static String CredentialsDialog_Browse;

	public static String CredentialsDialog_Domain;

	public static String CredentialsDialog_Enter_credentials;

	public static String CredentialsDialog_KeyStore;

	public static String CredentialsDialog_Password;

	public static String CredentialsDialog_SavePassword;

	public static String CredentialsDialog_Username;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
