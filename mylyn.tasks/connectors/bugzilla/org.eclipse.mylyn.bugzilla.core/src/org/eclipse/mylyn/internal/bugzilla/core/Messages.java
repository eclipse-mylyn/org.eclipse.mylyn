/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.core.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String BugzillaAttribute_Add;

	public static String BugzillaAttribute_Add_CC;

	public static String BugzillaAttribute_Add_self_to_CC;

	public static String BugzillaAttribute_Additional_Comments;

	public static String BugzillaAttribute_AddSeeAlso;

	public static String BugzillaAttribute_Alias;

	public static String BugzillaAttribute_Assigned_to;

	public static String BugzillaAttribute_Assigned_to_NAME;

	public static String BugzillaAttribute_ATTACH_ID;

	public static String BugzillaAttribute_attachment;

	public static String BugzillaAttribute_Blocks;

	public static String BugzillaAttribute_bug;

	public static String BugzillaAttribute_Bug_ID;

	public static String BugzillaAttribute_bug_when;

	public static String BugzillaAttribute_bugzilla;

	public static String BugzillaAttribute_CC;

	public static String BugzillaAttribute_CC_List;

	public static String BugzillaAttribute_Classification;

	public static String BugzillaAttribute_Classification_ID;

	public static String BugzillaAttribute_Comment_ID;

	public static String BugzillaAttribute_Component;

	public static String BugzillaAttribute_Content_Type;

	public static String BugzillaAttribute_data;

	public static String BugzillaAttribute_Date;

	public static String BugzillaAttribute_Depends_on__Subtasks_;

	public static String BugzillaAttribute_desc;

	public static String BugzillaAttribute_Description;

	public static String BugzillaAttribute_Due;

	public static String BugzillaAttribute_Duplicate_of;

	public static String BugzillaAttribute_Estimated_Time;

	public static String BugzillaAttribute_everconfirmed;

	public static String BugzillaAttribute_Exporter;

	public static String BugzillaAttribute_filename;

	public static String BugzillaAttribute_flag;

	public static String BugzillaAttribute_Group;

	public static String BugzillaAttribute_Keywords;

	public static String BugzillaAttribute_Modified;

	public static String BugzillaAttribute_new_comment;

	public static String BugzillaAttribute_Number_of_comments;

	public static String BugzillaAttribute_Obsolete;

	public static String BugzillaAttribute_open_status_values;

	public static String BugzillaAttribute_Opened;

	public static String BugzillaAttribute_OS;

	public static String BugzillaAttribute_Patch;

	public static String BugzillaAttribute_Platform;

	public static String BugzillaAttribute_Priority;

	public static String BugzillaAttribute_Private;

	public static String BugzillaAttribute_Product;

	public static String BugzillaAttribute_QA_Contact;

	public static String BugzillaAttribute_QA_Contact_NAME;

	public static String BugzillaAttribute_Reassign_to_default_assignee;

	public static String BugzillaAttribute_Remaining;

	public static String BugzillaAttribute_Remove_CC;

	public static String BugzillaAttribute_REPORT_ACCESSIBLE;

	public static String BugzillaAttribute_REPORT_NAME;

	public static String BugzillaAttribute_Reporter;

	public static String BugzillaAttribute_Resolution;

	public static String BugzillaAttribute_See_Also;

	public static String BugzillaAttribute_Severity;

	public static String BugzillaAttribute_Size;

	public static String BugzillaAttribute_Status;

	public static String BugzillaAttribute_Status_Whiteboard;

	public static String BugzillaAttribute_Summary;

	public static String BugzillaAttribute_Target_milestone;

	public static String BugzillaAttribute_thetext;

	public static String BugzillaAttribute_type;

	public static String BugzillaAttribute_UNKNOWN;

	public static String BugzillaAttribute_URL;

	public static String BugzillaAttribute_Query_Timestamp;

	public static String BugzillaAttribute_used_by_search_engine_bugs;

	public static String BugzillaAttribute_used_by_search_engine_desc;

	public static String BugzillaAttribute_used_by_search_engine_id;

	public static String BugzillaAttribute_used_by_search_engine_installation;

	public static String BugzillaAttribute_used_by_search_engine_li;

	public static String BugzillaAttribute_used_by_search_engine_rdf;

	public static String BugzillaAttribute_used_by_search_engine_result;

	public static String BugzillaAttribute_used_by_search_engine_seq;

	public static String BugzillaAttribute_Version;

	public static String BugzillaAttribute_version_of_bugzilla_installed;

	public static String BugzillaAttribute_Votes;

	public static String BugzillaAttribute_who;

	public static String BugzillaAttribute_who_name;

	public static String BugzillaAttribute_Worked;

	public static String BugzillaAttribute_Attacher;

	public static String BugzillaAttachmentMapper_Author;

	public static String BugzillaAttachmentMapper_Content_Type;

	public static String BugzillaAttachmentMapper_Creation_Date;

	public static String BugzillaAttachmentMapper_DELTA_TS;

	public static String BugzillaAttachmentMapper_Description;

	public static String BugzillaAttachmentMapper_Filename;

	public static String BugzillaAttachmentMapper_obsolete;

	public static String BugzillaAttachmentMapper_patch;

	public static String BugzillaAttachmentMapper_Size;

	public static String BugzillaAttachmentMapper_Token;

	public static String BugzillaAttachmentMapper_URL;

	public static String BugzillaClient_anonymous_user_not_allowed;

	public static String BugzillaClient_could_not_post_form_null_returned;

	public static String BugzillaClient_description_required_when_submitting_attachments;

	public static String BugzillaClient_Unable_to_retrieve_new_task;

	public static String BugzillaOperation_Accept_to_ASSIGNED;

	public static String BugzillaOperation_confirmed;

	public static String BugzillaOperation_default;

	public static String BugzillaOperation_Duplicate_of;

	public static String BugzillaOperation_in_progress;

	public static String BugzillaOperation_Leave_as_X_X;

	public static String BugzillaOperation_Mark_as_NEW;

	public static String BugzillaOperation_Mark_as_CLOSED;

	public static String BugzillaOperation_Mark_as_VERIFIED;

	public static String BugzillaOperation_Reassign_to;

	public static String BugzillaOperation_Reopen_bug;

	public static String BugzillaOperation_Resolve_as;

	public static String BugzillaOperation_Reassign_to_default_assignee;

	public static String BugzillaOperation_unconfirmed;

	public static String BugzillaOperation_verified;

	public static String BugzillaRepositoryConnector_BUGZILLA_CONNECTOR_LABEL;

	public static String BugzillaRepositoryConnector_Check_repository_configuration;

	public static String BugzillaRepositoryConnector_checking_for_changed_tasks;

	public static String BugzillaRepositoryConnector_Query_for_changed_tasks;

	public static String BugzillaRepositoryConnector_running_query;

	public static String BugzillaRepositoryConnector_Unrecognized_response_from_server;

	public static String BugzillaTaskAttachmentHandler_Getting_attachment;

	public static String BugzillaTaskAttachmentHandler_Sending_attachment;

	public static String BugzillaTaskAttachmentHandler_unable_to_submit_attachment;

	public static String BugzillaTaskDataHandler_Receiving_tasks;

	public static String BugzillaTaskDataHandler_Submitting_task;

	public static String IBugzillaConstants_Bug_id_not_found;

	public static String BugzillaTaskDataHandler_updating_attachment;

	public static String IBugzillaConstants_Bugzilla_login_information_or_repository_version_incorrect;

	public static String IBugzillaConstants_invalid_bug_id_requested_bug_id_does_not_exist;

	public static String IBugzillaConstants_invalid_repository_credentials;

	public static String IBugzillaConstants_Mylyn_Bugzilla_Connector;

	public static String IBugzillaConstants_NO_DATA_RETRIEVED_FOR_TASK;

	public static String IBugzillaConstants_requested_operation_not_permitted;

	public static String SaxMultiBugReportContentHandler_id_not_found;

	public static String BugzillaStatus_repositoryLoginFailure;

	public static String BugzillaStatus_repositoryNotFound;

	public static String BugzillaStatus_repositoryCommentRequired;

	public static String BugzillaStatus_repositoryCollision;

	public static String BugzillaStatus_suspiciousAction;

	public static String BugzillaStatus_operationCancelled;

	public static String BugzillaStatus_emptyPassword;

	public static String BugzillaStatus_errorIo;

	public static String BugzillaStatus_errorInternal;

	public static String BugzillaStatus_errorRepository;

	public static String CustomTransitionManager_InvalidBugzillaOption;

	public static String CustomTransitionManager_InvalidBugzillaTransition;

	public static String CustomTransitionManager_UnexpectedResponse;

	public static String RepositoryConfiguration_Operation_Tooltip_close;

	public static String RepositoryConfiguration_Operation_Tooltip_confirmed;

	public static String RepositoryConfiguration_Operation_Tooltip_duplicate;

	public static String RepositoryConfiguration_Operation_Tooltip_in_progress;

	public static String RepositoryConfiguration_Operation_Tooltip_new_default;

	public static String RepositoryConfiguration_Operation_Tooltip_none;

	public static String RepositoryConfiguration_Operation_Tooltip_reopen;

	public static String RepositoryConfiguration_Operation_Tooltip_resolve;

	public static String RepositoryConfiguration_Operation_Tooltip_unconfirmed;

	public static String RepositoryConfiguration_Operation_Tooltip_veryfy;

}
