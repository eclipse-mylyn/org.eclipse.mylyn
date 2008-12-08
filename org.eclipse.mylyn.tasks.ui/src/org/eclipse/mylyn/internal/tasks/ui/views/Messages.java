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

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.views.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String DisconnectRepositoryAction_Disconnected;

	public static String PriorityDropDownAction_Filter_Priority_Lower_Than;

	public static String SortyByDropDownAction_Date_Created;

	public static String SortyByDropDownAction_Descending;

	public static String SortyByDropDownAction_Priority;

	public static String SortyByDropDownAction_Sort_by;

	public static String SortyByDropDownAction_Summary;

	public static String TaskInputDialog_Clear;

	public static String TaskInputDialog_Description;

	public static String TaskInputDialog_Get_Description;

	public static String TaskInputDialog_New_Task;

	public static String TaskInputDialog_Web_Link;

	public static String TaskListDropAdapter__retrieving_from_URL_;

	public static String TaskListFilteredTree_Activate;

	public static String TaskListFilteredTree_Edit_Task_Working_Sets_;

	public static String TaskListFilteredTree_Estimated_hours;

	public static String TaskListFilteredTree__multiple_;

	public static String TaskListFilteredTree_Scheduled_tasks;

	public static String TaskListFilteredTree_Search_repository_for_key_or_summary_;

	public static String TaskListFilteredTree_Select_Active_Task;

	public static String TaskListFilteredTree_Select_Working_Set;

	public static String TaskListFilteredTree_Workweek_Progress;

	public static String TaskListTableSorter_Manual_sorting_is_disabled_in_focused_mode;

	public static String TaskListTableSorter_Task_Sorting;

	public static String TaskListToolTip_Automatic_container_for_all_local_tasks;

	public static String TaskListToolTip_Automatic_container_for_repository_tasks;

	public static String TaskListToolTip__Complete_;

	public static String TaskListToolTip_Due_;

	public static String TaskListToolTip_Elapsed_;

	public static String TaskListToolTip_Estimate_;

	public static String TaskListToolTip_hours;

	public static String TaskListToolTip_Incomplete;

	public static String TaskListToolTip_Please_synchronize_manually_for_full_error_message;

	public static String TaskListToolTip_Scheduled_;

	public static String TaskListToolTip_Some_incoming_elements_may_be_filtered;

	public static String TaskListToolTip_Synchronized;

	public static String TaskListToolTip_Total_;

	public static String TaskListView_Mylyn_context_capture_paused;

	public static String TaskListView__paused_;

	public static String TaskListView_Repository;

	public static String TaskListView_Summary;

	public static String TaskListView_Task_List;

	public static String TaskScheduleContentProvider_Future;

	public static String TaskScheduleContentProvider_Two_Weeks;

	public static String TaskScheduleContentProvider_Unscheduled;

	public static String UpdateRepositoryConfigurationAction_Error_updating_repository_configuration;

	public static String UpdateRepositoryConfigurationAction_Update_Repository_Configuration;

	public static String UpdateRepositoryConfigurationAction_Updating_repository_configuration_for_X;
}
