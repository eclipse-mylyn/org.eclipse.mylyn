/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.gerrit.ui.messages"; //$NON-NLS-1$

	public static String GerritConnectorUi_Change;

	public static String GerritRepositoryLocationUi_Login;

	public static String GerritRepositoryLocationUi_Login_to_OpenID_Provider;

	public static String GerritRepositorySettingsPage_Gerrit_may_not_be_supported;

	public static String GerritRepositorySettingsPage_Gerrit_Repository_Settings;

	public static String GerritRepositorySettingsPage_Gerrit_Repository_Settings_description;

	public static String GerritRepositorySettingsPage_Google_Account;

	public static String GerritRepositorySettingsPage_OpenID_Authentication;

	public static String GerritRepositorySettingsPage_Provider;

	public static String GerritRepositorySettingsPage_X_Logged_in_as_Y_dot_Z;

	public static String GerritRepositorySettingsPage_Yahoo_Account;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
