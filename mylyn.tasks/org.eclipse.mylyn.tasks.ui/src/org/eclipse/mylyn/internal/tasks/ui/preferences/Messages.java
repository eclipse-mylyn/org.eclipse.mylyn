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

package org.eclipse.mylyn.internal.tasks.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.preferences.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String MylynPreferencePage_General_settings_Description;

	public static String MylynPreferencePage_Mylyn_Title;

	public static String NotificationsLinkPreferencesPage_LinkText;

	public static String NotificationsLinkPreferencesPage_Mylyn_Notifications;

	public static String TasksUiPreferencePage_Advanced;

	public static String TasksUiPreferencePage_Browse_;

	public static String TasksUiPreferencePage_Change_data_directory;

	public static String TasksUiPreferencePage_Confirm_Task_List_data_directory_change;

	public static String TasksUiPreferencePage_Data_directory_;

	public static String TasksUiPreferencePage_Destination_folder_cannot_be_created;

	public static String TasksUiPreferencePage_Display_notifications_for_overdue_tasks_and_incoming_changes;

	public static String TasksUiPreferencePage_Enable_inactivity_timeouts;

	public static String TasksUiPreferencePage_Enable_Time_Tracking;

	public static String TasksUiPreferencePage_Error_applying_Task_List_data_directory_changes;

	public static String TasksUiPreferencePage_Folder_Selection;

	public static String TasksUiPreferencePage_If_disabled;

	public static String TasksUiPreferencePage_minutes;

	public static String TasksUiPreferencePage_minutes_of_inactivity;

	public static String TasksUiPreferencePage_Notification_for_new_connectors_available_Label;

	public static String TasksUiPreferencePage_A_new_empty_Task_List_will_be_created_in_the_chosen_directory_if_one_does_not_already_exists;

	public static String TasksUiPreferencePage_highlight_current_line;

	public static String TasksUiPreferencePage_RelevantTasksHelp;

	public static String TasksUiPreferencePage_Rich_Editor__Recommended_;

	public static String TasksUiPreferencePage_ScheduleNewTasks;

	public static String TasksUiPreferencePage_Scheduling;

	public static String TasksUiPreferencePage_See_X_for_configuring_Task_List_colors;

	public static String TasksUiPreferencePage_Show_active_task_trim_Button_Label;

	public static String TasksUiPreferencePage_Show_tooltip_on_hover_Label;

	public static String TasksUiPreferencePage_Specify_the_folder_for_tasks;

	public static String TasksUiPreferencePage_Stop_time_accumulation_after;

	public static String TasksUiPreferencePage_Synchronize_Queries;

	public static String TasksUiPreferencePage_Synchronize_Relevant_Tasks;

	public static String TasksUiPreferencePage_Synchronize_schedule_time_must_be_GT_0;

	public static String TasksUiPreferencePage_Synchronize_schedule_time_must_be_valid_integer;

	public static String TasksUiPreferencePage_Synchronize_Task_List;

	public static String TasksUiPreferencePage_Synchronization;

	public static String TasksUiPreferencePage_Task_Data;

	public static String TasksUiPreferencePage_Task_Data_Directory_Error;

	public static String TasksUiPreferencePage_Task_Editing;

	public static String TasksUiPreferencePage_Task_List_Group;

	public static String TasksUiPreferencePage_Task_Navigation_Group_Label;

	public static String TasksUiPreferencePage_Task_Timing;

	public static String TasksUiPreferencePage_ThisWeek;

	public static String TasksUiPreferencePage_Today;

	public static String TasksUiPreferencePage_Tomorrow;

	public static String TasksUiPreferencePage_Track_Time_Spent;

	public static String TasksUiPreferencePage_Unscheduled;

	public static String TasksUiPreferencePage_Use_the_Restore_dialog_to_recover_missing_tasks;

	public static String TasksUiPreferencePage_Web_Browser;

	public static String TasksUiPreferencePage_Week_Start;

}
