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

package org.eclipse.mylyn.internal.trac.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.trac.ui.wizard.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String TracFilterQueryPage_Add_search_filters_to_define_query;

	public static String TracFilterQueryPage_CC;

	public static String TracFilterQueryPage_Component;

	public static String TracFilterQueryPage_Keywords;

	public static String TracFilterQueryPage_Milestone;

	public static String TracFilterQueryPage_New_Trac_Query;

	public static String TracFilterQueryPage_or;

	public static String TracFilterQueryPage_Owner;

	public static String TracFilterQueryPage_Priority;

	public static String TracFilterQueryPage_Query_Title;

	public static String TracFilterQueryPage_Reporter;

	public static String TracFilterQueryPage_Resolution;

	public static String TracFilterQueryPage_Select_to_add_filter;

	public static String TracFilterQueryPage_Status;

	public static String TracFilterQueryPage_Summary;

	public static String TracFilterQueryPage_Type;

	public static String TracFilterQueryPage_Version;

	public static String TracQueryPage_CC;

	public static String TracQueryPage_Component;

	public static String TracQueryPage_Description;

	public static String TracQueryPage_Enter_query_parameters;

	public static String TracQueryPage_If_attributes_are_blank_or_stale_press_the_Update_button;

	public static String TracQueryPage_Keywords;

	public static String TracQueryPage_Milestone;

	public static String TracQueryPage_No_repository_available;

	public static String TracQueryPage_Owner;

	public static String TracQueryPage_Priority;

	public static String TracQueryPage_Query_Title;

	public static String TracQueryPage_Reporter;

	public static String TracQueryPage_Resolution;

	public static String TracQueryPage_Status;

	public static String TracQueryPage_Summary;

	public static String TracQueryPage_Type;

	public static String TracQueryPage_Update_Attributes_Failed;

	public static String TracQueryPage_Update_Attributes_from_Repository;

	public static String TracQueryPage_Version;

	public static String TracRepositorySettingsPage_Access_Type_;

	public static String TracRepositorySettingsPage_Authentication_credentials_are_valid;

	public static String TracRepositorySettingsPage_Automatic__Use_Validate_Settings_;

	public static String TracRepositorySettingsPage_EXAMPLE_HTTP_TRAC_EDGEWALL_ORG;

	public static String TracRepositorySettingsPage_No_Trac_repository_found_at_url;

	public static String TracRepositorySettingsPage_Trac_Repository_Settings;
}
