/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.resources.ui.preferences.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String FocusedResourcesPreferencePage_Add_;
	public static String FocusedResourcesPreferencePage_Add__IGNORED_RESOURCE;
	public static String FocusedResourcesPreferencePage__automatic_;
	public static String FocusedResourcesPreferencePage_Enter_pattern_____any_string_;
	public static String FocusedResourcesPreferencePage_Matching_file_or_directory_names_will_not_be_added_automatically_to_the_context;
	public static String FocusedResourcesPreferencePage_Remove;
	public static String FocusedResourcesPreferencePage_Resource_Monitoring_Exclusions;
}
