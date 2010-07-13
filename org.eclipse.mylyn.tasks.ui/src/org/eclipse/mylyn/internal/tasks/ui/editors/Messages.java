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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.editors.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AbstractReplyToCommentAction_Reply;

	public static String AttachmentSizeFormatter_0_bytes;

	public static String AttachmentSizeFormatter_0_GB;

	public static String AttachmentSizeFormatter_0_KB;

	public static String AttachmentSizeFormatter_0_MB;

	public static String AttachmentSizeFormatter_1_byte;

	public static String AttachmentTableLabelProvider_File_;

	public static String AttachmentTableLabelProvider_Patch;

	public static String AttachmentTableLabelProvider_Task_Context;

	public static String AttachmentTableLabelProvider_Type_;

	public static String BrowserPreviewViewer_Error;

	public static String BrowserPreviewViewer_Formatting_Wiki_Text;

	public static String BrowserPreviewViewer_Loading_preview_;

	public static String BrowserPreviewViewer_The_repository_does_not_support_HTML_preview;

	public static String CheckboxMultiSelectAttributeEditor_Select_X;

	public static String CommentGroupStrategy_Current;

	public static String CommentGroupStrategy_Older;

	public static String CommentGroupStrategy_Recent;

	public static String PersonalPart_Personal_Planning;

	public static String PersonAttributeEditor_Insert_My_User_Id_Tooltip;

	public static String PlanningPageFactory_Private;

	public static String PlanningPart_Active_time_in_Product_Label;

	public static String PlanningPart_Active_time_Label;

	public static String PlanningPart_Default_Product;

	public static String PlanningPart_Later;

	public static String PlanningPart_Next_Week;

	public static String PlanningPart_Personal_Notes;

	public static String PlanningPart_Reset_Active_Time;

	public static String PlanningPart_Scheduled_for_X_Tooltip;

	public static String PlanningPart_This_Week;

	public static String PlanningPart_Today;

	public static String RichTextAttributeEditor_Viewer_Source;

	public static String TaskAttachmentDropListener_Note_that_only_the_first_file_dragged_will_be_attached;

	public static String TaskEditorActionPart_Actions;

	public static String TaskEditorActionPart_Add_to_Category;

	public static String TaskEditorActionPart_Attach_Context;

	public static String TaskEditorActionPart_Submit;

	public static String TaskEditorActionPart_Submit_to_X;

	public static String TaskEditorAttachmentPart_Attach_;

	public static String TaskEditorAttachmentPart_Attach__Screenshot;

	public static String TaskEditorAttachmentPart_Attachments;

	public static String TaskEditorAttachmentPart_Created;

	public static String TaskEditorAttachmentPart_Creator;

	public static String TaskEditorAttachmentPart_Description;

	public static String TaskEditorAttachmentPart_Name;

	public static String TaskEditorAttachmentPart_No_attachments;

	public static String TaskEditorAttachmentPart_Size;

	public static String TaskEditorAttributePart_Attributes;

	public static String TaskEditorAttributePart_Refresh_Attributes;

	public static String TaskEditorAttributePart_Update_Failed;

	public static String TaskEditorAttributePart_Updating_of_repository_configuration_failed;

	public static String TaskEditorCommentPart_0;

	public static String TaskEditorCommentPart_1;

	public static String TaskEditorCommentPart_Collapse_Comments;

	public static String TaskEditorCommentPart_Comments;

	public static String TaskEditorCommentPart_Expand_Comments;

	public static String TaskEditorDescriptionPart_Description;

	public static String TaskEditorDescriptionPart_Detector;

	public static String TaskEditorDescriptionPart_Duplicate_Detection_Failed;

	public static String TaskEditorDescriptionPart_Duplicate_Detection;

	public static String TaskEditorDescriptionPart_The_duplicate_detector_did_not_return_a_valid_query;

	public static String TaskEditorDescriptionPart_Search;

	public static String TaskEditorNewCommentPart_New_Comment;

	public static String TaskEditorOutlineNode_Attachments;

	public static String TaskEditorOutlineNode_Attributes;

	public static String TaskEditorOutlineNode_Comments;

	public static String TaskEditorOutlineNode_Description;

	public static String TaskEditorOutlineNode_New_Comment;

	public static String TaskEditorOutlineNode_Related_Tasks;

	public static String TaskEditorOutlineNode_Task_;

	public static String TaskEditorOutlineNode_TaskRelation_Label;

	public static String TaskEditorOutlineNode_unknown_Label;

	public static String TaskEditorPeoplePart_People;

	public static String TaskEditorPlanningPart_0_SECOUNDS;

	public static String TaskEditorPlanningPart_Add_Private_Notes_Tooltip;

	public static String TaskEditorPlanningPart_Confirm_Activity_Time_Deletion;

	public static String TaskEditorPlanningPart_Do_you_wish_to_reset_your_activity_time_on_this_task_;

	public static String TaskEditorPlanningPart_Due;

	public static String TaskEditorPlanningPart_Estimated;

	public static String TaskEditorPlanningPart_Scheduled;

	public static String TaskEditorPlanningPart_TaskEditorPlanningPart_tooltip;

	public static String TaskEditorPlanningPart_Time_working_on_this_task;

	public static String TaskEditorRichTextPart_Browser_Preview;

	public static String TaskEditorRichTextPart_Edit_Tooltip;

	public static String TaskEditorRichTextPart_Maximize;

	public static String TaskEditorSummaryPart_Summary;

	public static String TaskPlanningEditor_Attributes;

	public static String TaskPlanningEditor_Complete;

	public static String TaskPlanningEditor_Completed;

	public static String TaskPlanningEditor_Created;

	public static String TaskPlanningEditor_Incomplete;

	public static String TaskPlanningEditor_Notes;

	public static String TaskPlanningEditor_Planning;

	public static String TaskPlanningEditor_Retrieve_task_description_from_URL;

	public static String TaskPlanningEditor_Save;

	public static String TaskPlanningEditor_Status;

	public static String TaskPlanningEditor_URL;

	public static String AbstractTaskEditorPage_Comment_required;

	public static String AbstractTaskEditorPage_Could_not_save_task;

	public static String AbstractTaskEditorPage_Error_opening_task;

	public static String AbstractTaskEditorPage_Failed_to_read_task_data_;

	public static String AbstractTaskEditorPage_History;

	public static String AbstractTaskEditorPage_Open_failed;

	public static String AbstractTaskEditorPage_Open_with_Web_Browser;

	public static String AbstractTaskEditorPage_Save_failed;

	public static String AbstractTaskEditorPage_Submit_failed;

	public static String AbstractTaskEditorPage_Submit_failed_;

	public static String AbstractTaskEditorPage_Synchronize_to_retrieve_task_data;

	public static String AbstractTaskEditorPage_Synchronize_to_update_editor_contents;

	public static String AbstractTaskEditorPage_Add_task_to_tasklist;

	public static String AbstractTaskEditorPage_Task_has_incoming_changes;

	public static String AbstractTaskEditorPage_Title;

	public static String AbstractTaskEditorPage_Unable_to_submit_at_this_time;

	public static String AttributeEditorToolkit_Content_Assist_Available__X_;

	public static String AttributePart_Category_;

	public static String SummaryPart_Section_Title;

	public static String TaskEditor_Edit_Task_Repository_ToolTip;

	public static String TaskEditor_Task;

	public static String TaskEditor_Task_added_to_the_Uncategorized_container;

	public static String TaskEditorExtensionSettingsContribution__default_;

	public static String TaskEditorExtensionSettingsContribution_Editor;

	public static String TaskEditorExtensionSettingsContribution_Plain_Text;

	public static String TaskEditorExtensionSettingsContribution_Select_the_capabilities_of_the_task_editor;

	public static String TaskUrlHyperlink_Open_URL_in_Task_Editor;
}
