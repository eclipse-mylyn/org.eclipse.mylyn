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

package org.eclipse.mylyn.internal.bugzilla.ui.search;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.ui.search.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String BugzillaSearchPage_all;

	public static String BugzillaSearchPage_all_words;

	public static String BugzillaSearchPage_any;

	public static String BugzillaSearchPage_any_word;

	public static String BugzillaSearchPage_Bugzilla_Query;

	public static String BugzillaSearchPage_Bugzilla_Search_Page;

	public static String BugzillaSearchPage_cc;

	public static String BugzillaSearchPage_Changed_in;

	public static String BugzillaSearchPage_Comment;

	public static String BugzillaSearchPage_commenter;

	public static String BugzillaSearchPage_Component;

	public static String BugzillaSearchPage_days;

	public static String BugzillaSearchPage_Email;

	public static String BugzillaSearchPage_Email_2;

	public static String BugzillaSearchPage_Error_updating_search_options;

	public static String BugzillaSearchPage_Error_was_X;

	public static String BugzillaSearchPage_exact;

	public static String BugzillaSearchPage_Hardware;

	public static String BugzillaSearchPage_Keywords;

	public static String BugzillaSearchPage_Milestone;

	public static String BugzillaSearchPage_No_repository_available;

	public static String BugzillaSearchPage_none;

	public static String BugzillaSearchPage_notregexp;

	public static String BugzillaSearchPage_Number_of_days_must_be_a_positive_integer;

	public static String BugzillaSearchPage_Operating_System;

	public static String BugzillaSearchPage_owner;

	public static String BugzillaSearchPage_Product;

	public static String BugzillaSearchPage_PROORITY;

	public static String BugzillaSearchPage_Query_Title;

	public static String BugzillaSearchPage_regexp;

	public static String BugzillaSearchPage_reporter;

	public static String BugzillaSearchPage_Resolution;

	public static String BugzillaSearchPage_Select_;

	public static String BugzillaSearchPage_Select_the_Bugzilla_query_parameters;

	public static String BugzillaSearchPage_Severity;

	public static String BugzillaSearchPage_Status;

	public static String BugzillaSearchPage_substring;

	public static String BugzillaSearchPage_Summary;

	public static String BugzillaSearchPage_Task_Repositories;

	public static String BugzillaSearchPage_Unable_to_get_configuration_X;

	public static String BugzillaSearchPage_Update_Attributes_from_Repository;

	public static String BugzillaSearchPage_Updating_search_options_;

	public static String BugzillaSearchPage_Version;
}
