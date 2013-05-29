/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

	public static String BugzillaSearchPage_AND_Button;

	public static String BugzillaSearchPage_any;

	public static String BugzillaSearchPage_Bugzilla_Query;

	public static String BugzillaSearchPage_cc;

	public static String BugzillaSearchPage_Changed_in;

	public static String BugzillaSearchPage_Comment;

	public static String BugzillaSearchPage_commenter;

	public static String BugzillaSearchPage_Component;

	public static String BugzillaSearchPage_days;

	public static String BugzillaSearchPage_days_must_be_an_positve_integer_value_but_is;

	public static String BugzillaSearchPage_Email;

	public static String BugzillaSearchPage_Email_2;

	public static String BugzillaSearchPage_EmailOperation_exact;

	public static String BugzillaSearchPage_EmailOperation_notequals;

	public static String BugzillaSearchPage_EmailOperation_notregexp;

	public static String BugzillaSearchPage_EmailOperation_regexp;

	public static String BugzillaSearchPage_EmailOperation_substring;

	public static String BugzillaSearchPage_Enter_search_option;

	public static String BugzillaSearchPage_Hardware;

	public static String BugzillaSearchPage_Keywords;

	public static String BugzillaSearchPage_Milestone;

	public static String BugzillaSearchPage_Negate_Button;

	public static String BugzillaSearchPage_Add_Chart_Button;

	public static String BugzillaSearchPage_none;

	public static String BugzillaSearchPage_Number_of_days_is_invalid;

	public static String BugzillaSearchPage_Number_of_days_must_be_a_positive_integer;

	public static String BugzillaSearchPage_Operating_System;

	public static String BugzillaSearchPage_Operation_changed_after;

	public static String BugzillaSearchPage_Operation_changed_before;

	public static String BugzillaSearchPage_Operation_changed_by;

	public static String BugzillaSearchPage_Operation_changed_from;

	public static String BugzillaSearchPage_Operation_changed_to;

	public static String BugzillaSearchPage_Operation_contains_all_of_the_strings;

	public static String BugzillaSearchPage_Operation_contains_all_of_the_words;

	public static String BugzillaSearchPage_Operation_contains_any_of_he_words;

	public static String BugzillaSearchPage_Operation_contains_any_of_the_strings;

	public static String BugzillaSearchPage_Operation_contains_none_of_the_strings;

	public static String BugzillaSearchPage_Operation_contains_none_of_the_words;

	public static String BugzillaSearchPage_Operation_contains_regexp;

	public static String BugzillaSearchPage_Operation_contains_the_string;

	public static String BugzillaSearchPage_Operation_contains_the_string_exact_case;

	public static String BugzillaSearchPage_Operation_does_not_contain_regexp;

	public static String BugzillaSearchPage_Operation_does_not_contain_the_string;

	public static String BugzillaSearchPage_Operation_is_equal_to;

	public static String BugzillaSearchPage_Operation_is_equal_to_any_of_the_strings;

	public static String BugzillaSearchPage_Operation_is_greater_than;

	public static String BugzillaSearchPage_Operation_is_less_than;

	public static String BugzillaSearchPage_Operation_is_not_equal_to;

	public static String BugzillaSearchPage_Operation_matches;

	public static String BugzillaSearchPage_Operation_Noop;

	public static String BugzillaSearchPage_OperationText_allwords;

	public static String BugzillaSearchPage_OperationText_allwordssubstr;

	public static String BugzillaSearchPage_OperationText_anywords;

	public static String BugzillaSearchPage_OperationText_anywordssubstr;

	public static String BugzillaSearchPage_OperationText_casesubstring;

	public static String BugzillaSearchPage_OperationText_notregexp;

	public static String BugzillaSearchPage_OperationText_regexp;

	public static String BugzillaSearchPage_OperationText_substring;

	public static String BugzillaSearchPage_OR_Button;

	public static String BugzillaSearchPage_owner;

	public static String BugzillaSearchPage_Product;

	public static String BugzillaSearchPage_PROORITY;

	public static String BugzillaSearchPage_qacontact;

	public static String BugzillaSearchPage_reporter;

	public static String BugzillaSearchPage_Resolution;

	public static String BugzillaSearchPage_Select_;

	public static String BugzillaSearchPage_Select_the_Bugzilla_query_parameters;

	public static String BugzillaSearchPage_Severity;

	public static String BugzillaSearchPage_Status;

	public static String BugzillaSearchPage_Summary;

	public static String BugzillaSearchPage_ValidationMessage;

	public static String BugzillaSearchPage_ValidationTitle;

	public static String BugzillaSearchPage_Version;

	public static String BugzillaSearchPage_BooleanChart;

	public static String BugzillaSearchPage_Field_Alias;

	public static String BugzillaSearchPage_Field_AssignedTo;

	public static String BugzillaSearchPage_Field_Attachment_creator;

	public static String BugzillaSearchPage_Field_Attachment_data;

	public static String BugzillaSearchPage_Field_Attachment_description;

	public static String BugzillaSearchPage_Field_Attachment_filename;

	public static String BugzillaSearchPage_Field_Attachment_is_a_URL;

	public static String BugzillaSearchPage_Field_Attachment_is_obsolete;

	public static String BugzillaSearchPage_Field_Attachment_is_patch;

	public static String BugzillaSearchPage_Field_Attachment_is_private;

	public static String BugzillaSearchPage_Field_Attachment_mime_type;

	public static String BugzillaSearchPage_Field_Blocks;

	public static String BugzillaSearchPage_Field_Bug;

	public static String BugzillaSearchPage_Field_CC;

	public static String BugzillaSearchPage_Field_CC_Accessible;

	public static String BugzillaSearchPage_Field_Classification;

	public static String BugzillaSearchPage_Field_Comment;

	public static String BugzillaSearchPage_Field_Comment_is_private;

	public static String BugzillaSearchPage_Field_Commenter;

	public static String BugzillaSearchPage_Field_Component;

	public static String BugzillaSearchPage_Field_Content;

	public static String BugzillaSearchPage_Field_Creation_date;

	public static String BugzillaSearchPage_Field_Days_since_bug_changed;

	public static String BugzillaSearchPage_Field_Depends_on;

	public static String BugzillaSearchPage_Field_drop_down_custom_field;

	public static String BugzillaSearchPage_Field_Ever_Confirmed;

	public static String BugzillaSearchPage_Field_Flag;

	public static String BugzillaSearchPage_Field_Flag_Requestee;

	public static String BugzillaSearchPage_Field_Flag_Setter;

	public static String BugzillaSearchPage_Field_free_text_custom_field;

	public static String BugzillaSearchPage_Field_Group;

	public static String BugzillaSearchPage_Field_Keywords;

	public static String BugzillaSearchPage_Field_Last_changed_date;

	public static String BugzillaSearchPage_Field_Noop;

	public static String BugzillaSearchPage_Field_OS_Version;

	public static String BugzillaSearchPage_Field_Platform;

	public static String BugzillaSearchPage_Field_Priority;

	public static String BugzillaSearchPage_Field_Product;

	public static String BugzillaSearchPage_Field_QAContact;

	public static String BugzillaSearchPage_Field_ReportedBy;

	public static String BugzillaSearchPage_Field_Reporter_Accessible;

	public static String BugzillaSearchPage_Field_Resolution;

	public static String BugzillaSearchPage_Field_Severity;

	public static String BugzillaSearchPage_Field_Status;

	public static String BugzillaSearchPage_Field_Status_Whiteboard;

	public static String BugzillaSearchPage_Field_Summary;

	public static String BugzillaSearchPage_Field_Target_Milestone;

	public static String BugzillaSearchPage_Field_Time_Since_Assignee_Touched;

	public static String BugzillaSearchPage_Field_URL;

	public static String BugzillaSearchPage_Field_Version;

	public static String BugzillaSearchPage_Field_Votes;

	public static String BugzillaSearchPage_More_Options;

	public static String BugzillaSearchPage_Tooltip_Custom_fields_at_end;

	public static String BugzillaSearchPage_Tooltip_remove_row;

	public static String BugzillaSearchPage_Whiteboard;
}
