/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ActiveContextExternalizationParticipant_Active_Task_Context;

	public static String ActivityExternalizationParticipant_Activity_Context;

	public static String ChangeActivityHandleOperation_Activity_migration;

	public static String DialogErrorReporter_Mylyn_Error;

	public static String DialogErrorReporter_Please_report_the_following_error_at;

	public static String DownloadAndOpenTaskAttachmentJob_cannotOpenEditor;

	public static String DownloadAndOpenTaskAttachmentJob_editorTooltip;

	public static String DownloadAndOpenTaskAttachmentJob_failedToDownloadAttachment;

	public static String MoveToCategoryMenuContributor_Set_Category_Menu_Item;

	public static String OpenRepositoryTaskJob_Could_not_find_repository_configuration_for_X;

	public static String OpenRepositoryTaskJob_Opening_Remote_Task;

	public static String OpenRepositoryTaskJob_Opening_repository_task_X;

	public static String OpenRepositoryTaskJob_Please_set_up_repository_via_X;

	public static String OpenRepositoryTaskJob_Repository_Not_Found;

	public static String OpenRepositoryTaskJob_Unable_to_open_task;

	public static String RefactorRepositoryUrlOperation_Repository_URL_update;

	public static String ScheduleDatePicker_Clear;

	public static String ScheduleTaskMenuContributor_Cannot_schedule_completed_tasks;

	public static String ScheduleTaskMenuContributor_Choose_Date_;

	public static String ScheduleTaskMenuContributor_Future;

	public static String ScheduleTaskMenuContributor_Not_Scheduled;

	public static String ScheduleTaskMenuContributor_Schedule_for;

	public static String SynchronizeRelevantTasksJob_SynchronizingRelevantTasks;

	public static String TaskAttachmentEditorViewer_Do_not_warn_me_again;

	public static String TaskAttachmentEditorViewer_Open_Attachment;

	public static String TaskAttachmentEditorViewer_openingAttachment;

	public static String TaskAttachmentEditorViewer_Some_files_can_harm_your_computer;

	public static String TaskAttachmentViewerBrowser_browser;

	public static String TaskHistoryDropDown_Activate_Task_;

	public static String TaskHistoryDropDown_Deactivate_Task;

	public static String TaskHistoryDropDown_Open_Active_Task;

	public static String TaskListBackupManager_Error_occured_during_scheduled_tasklist_backup;

	public static String TaskListBackupManager_Scheduled_task_data_backup;

	public static String TaskListNotificationManager_Open_Notification_Job;

	public static String TaskRepositoryLocationUi_Enter_CLIENTCERTIFICATE_password;

	public static String TaskRepositoryLocationUi_Enter_HTTP_password;

	public static String TaskRepositoryLocationUi_Enter_proxy_password;

	public static String TaskRepositoryLocationUi_Enter_repository_password;

	public static String TaskSearchPage_ERROR_Unable_to_present_query_page;

	public static String TaskSearchPage_no_available_repositories;

	public static String TaskSearchPage_no_searchable_repositories;

	public static String TaskSearchPage_No_task_found_matching_key_;

	public static String TaskSearchPage_Repository_Search;

	public static String TaskSearchPage_Repository;

	public static String TaskSearchPage_Task_Key_ID;

	public static String TaskSearchPage_Task_Search;

	public static String TasksReminderDialog_Description;

	public static String TasksReminderDialog_Dismiss_All;

	public static String TasksReminderDialog_Dismiss_Selected;

	public static String TasksReminderDialog_Priority;

	public static String TasksReminderDialog_Remind_tommorrow;

	public static String TasksReminderDialog_Reminder_Day;

	public static String TasksReminderDialog_Reminders;

	public static String TasksUiPlugin_Activate_Task;

	public static String TasksUiPlugin_Hide_Irrelevant_Subtasks;

	public static String TasksUiPlugin_Hide_Irrelevant_Subtasks_Message;

	public static String TasksUiPlugin_Initializing_Task_List;

	public static String TasksUiPlugin_Task_Repositories;

	public static String TasksUiPlugin_Load_Data_Directory;

	public static String TasksUiPlugin_New_Task;

	public static String TaskTrimWidget__no_active_task_;

	public static String TaskTrimWidget__no_task_active_;

	public static String AbstractRepositoryConnectorUi_InReplyToComment;

	public static String AbstractRepositoryConnectorUi_InReplyToDescription;

	public static String AbstractRepositoryConnectorUi_InReplyToTaskAndComment;

	public static String AbstractRepositoryConnectorUi_Task;

	public static String TaskElementLabelProvider__no_summary_available_;

	public static String TaskHyperlink_Could_not_determine_repository_for_report;

	public static String TaskHyperlink_Open_Task_X_in_X;

	public static String AbstractRetrieveTitleFromUrlJob_Retrieving_summary_from_URL;

	public static String FileStorage_unableToReadAttachmentFile;

}
