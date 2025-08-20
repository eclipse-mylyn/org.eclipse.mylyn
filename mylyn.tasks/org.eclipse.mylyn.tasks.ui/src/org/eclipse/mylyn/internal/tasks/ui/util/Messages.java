/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.util.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AttachmentUtil_The_context_is_empty;

	public static String AttachmentUtil_Downloading_attachment;

	public static String AttachmentUtil_Mylyn_Information;

	public static String CopyAttachmentToClipboardJob_Copy_Attachment_to_Clipboard;

	public static String CopyAttachmentToClipboardJob_Copying_Attachment_to_Clipboard;

	public static String DownloadAttachmentJob_Copy_Attachment_to_Clipboard;

	public static String DownloadAttachmentJob_Downloading_Attachment;

	public static String ImportExportUtil_Tasks_and_queries_Filter0;

	public static String SortCriterion_Modification_Date;

	public static String SortCriterion_Scheduled_Date;

	public static String SortCriterion_Type;

	public static String SortKindEntry_Date_Created;

	public static String SortKindEntry_None;

	public static String SortKindEntry_Priority;

	public static String SortKindEntry_Rank;

	public static String SortKindEntry_Summary;

	public static String SortKindEntry_Task_ID;

	public static String SortKindEntry_Due_Date;

	public static String SaveAttachmentsAction_directoryDoesntExist;

	public static String SaveAttachmentsAction_directoryDoesntExist0;

	public static String SaveAttachmentsAction_fileExists_doYouWantToOverwrite0;

	public static String SaveAttachmentsAction_overwriteFile0;

	public static String SaveAttachmentsAction_selectDirectory;

	public static String SaveAttachmentsAction_selectDirectoryHint;

	public static String TaskDataExportOperation_exporting_task_data;

	public static String TasksUiInternal_Configuration_Refresh_Failed;

	public static String TasksUiInternal_Create_Task;

	public static String TasksUiInternal_The_new_task_will_be_added_to_the_X_container;

	public static String TasksUiInternal_Query_Synchronization_Failed;

	public static String TasksUiInternal_Task_Synchronization_Failed;

	public static String TasksUiInternal__hour_;

	public static String TasksUiInternal__hours_;

	public static String TasksUiInternal__minute_;

	public static String TasksUiInternal__minutes_;

	public static String TasksUiInternal__second;

	public static String TasksUiInternal__seconds;

	public static String TasksUiInternal_Activate_Task;

	public static String TasksUiInternal_An_unknown_error_occurred;

	public static String TasksUiInternal_Failed_to_open_task;

	public static String TasksUiInternal_No_repository_found;

	public static String TasksUiInternal_Rename_Category_Message;

	public static String TasksUiInternal_Rename_Category_Name_already_exists_Error;

	public static String TasksUiInternal_Rename_Category_Title;

	public static String TasksUiInternal_See_error_log_for_details;

	public static String TasksUiMenus_Copy_Contents;

	public static String TasksUiMenus_Copy_URL;

	public static String TasksUiMenus_File_exists_;

	public static String TasksUiMenus_Overwrite_existing_file_;

	public static String TasksUiMenus_Save_;
}
