/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.rest.ui.messages"; //$NON-NLS-1$

	public static String BugzillaKeywordAttributeEditor_Select_X;

	public static String BugzillaRestRepositorySettingsPage_api_key;

	public static String BugzillaRestRepositorySettingsPage_Please_create_or_copy_the_API_Key_from;

	public static String BugzillaRestRepositorySettingsPage_RestRepositorySetting;

	public static String BugzillaRestRepositorySettingsPage_SupportsVersionMessage;

	public static String BugzillaRestRepositorySettingsPage_use_api_key;

	public static String BugzillaRestRepositorySettingsPage_View_your_apikey_settings;

	public static String BugzillaRestSearchQueryPage_PropertiesForNewQuery;

	public static String BugzillaRestSearchQueryPage_PropertiesForQuery;

	public static String BugzillaRestTaskEditorPageFactory_Bugzilla;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
