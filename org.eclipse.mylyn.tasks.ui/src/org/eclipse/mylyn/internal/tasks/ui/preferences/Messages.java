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

	public static String TasksUiPreferencePage_Advanced;

	public static String TasksUiPreferencePage_Browse_;

	public static String TasksUiPreferencePage_Change_data_directory;

	public static String TasksUiPreferencePage_Confirm_Task_List_data_directory_change;

	public static String TasksUiPreferencePage_Data_directory_;

	public static String TasksUiPreferencePage_Destination_folder_does_not_exist;

	public static String TasksUiPreferencePage_Display_notifications_for_overdue_tasks_and_incoming_changes;

	public static String TasksUiPreferencePage_Enable_inactivity_timeouts;

	public static String TasksUiPreferencePage_Error_applying_Task_List_data_directory_changes;

	public static String TasksUiPreferencePage_Folder_Selection;

	public static String TasksUiPreferencePage_If_disabled;

	public static String TasksUiPreferencePage_minutes;

	public static String TasksUiPreferencePage_minutes_of_inactivity;

	public static String TasksUiPreferencePage_A_new_empty_Task_List_will_be_created_in_the_chosen_directory_if_one_does_not_already_exists;

	public static String TasksUiPreferencePage_Rich_Editor__Recommended_;

	public static String TasksUiPreferencePage_Scheduling;

	public static String TasksUiPreferencePage_See_X_for_configuring_Task_List_colors;

	public static String TasksUiPreferencePage_Specify_the_folder_for_tasks;

	public static String TasksUiPreferencePage_Stop_time_accumulation_after;

	public static String TasksUiPreferencePage_Synchronize_schedule_time_must_be_GT_0;

	public static String TasksUiPreferencePage_Synchronize_schedule_time_must_be_valid_integer;

	public static String TasksUiPreferencePage_Synchronize_with_repositories_every;

	public static String TasksUiPreferencePage_Synchronization;

	public static String TasksUiPreferencePage_Task_Data;

	public static String TasksUiPreferencePage_Task_Data_Directory_Error;

	public static String TasksUiPreferencePage_Task_Editing;

	public static String TasksUiPreferencePage_Task_Timing;

	public static String TasksUiPreferencePage_Use_the_Restore_dialog_to_recover_missing_tasks;

	public static String TasksUiPreferencePage_Web_Browser;

	public static String TasksUiPreferencePage_Week_Start;
}
