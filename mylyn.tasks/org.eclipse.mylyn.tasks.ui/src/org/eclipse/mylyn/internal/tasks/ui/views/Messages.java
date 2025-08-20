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

	public static String RepositoryElementActionGroup_Copy_Detail_Menu_Label;

	public static String RepositoryElementActionGroup_New;

	public static String TaskListDropAdapter__retrieving_from_URL_;

	public static String TaskListFilteredTree_Activate;

	public static String TaskListFilteredTree_Edit_Task_Working_Sets_;

	public static String TaskListFilteredTree_Estimated_hours;

	public static String TaskListFilteredTree__multiple_;

	public static String TaskListFilteredTree_Scheduled_tasks;

	public static String TaskListFilteredTree_Search_repository_for_key_or_summary_;

	public static String TaskListFilteredTree_Select_Active_Task;

	public static String TaskListFilteredTree_Select_Working_Set;

	public static String TaskListFilteredTree_Show_Tasks_UI_Legend;

	public static String TaskListFilteredTree_Workweek_Progress;

	public static String TaskListSorter_Catagory_and_Query;

	public static String TaskListSorter_Catagory_and_Repository;

	public static String TaskListSorter_No_Grouping;

	public static String TaskListToolTip_Active_X;

	public static String TaskListToolTip_Assigned_to_X;

	public static String TaskListToolTip_Automatic_container_for_all_local_tasks;

	public static String TaskListToolTip_Automatic_container_for_repository_tasks;

	public static String TaskListToolTip_Due;

	public static String TaskListToolTip_Estimate;

	public static String TaskListToolTip_Please_synchronize_manually_for_full_error_message;

	public static String TaskListToolTip_Scheduled;

	public static String TaskListToolTip_Some_incoming_elements_may_be_filtered;

	public static String TaskListToolTip_Synchronized;

	public static String TaskListToolTip_Total_Complete_Incomplete;

	public static String TaskListToolTip_Incoming_Outgoing;

	public static String TaskListToolTip_Unassigned;

	public static String TaskListView_Mylyn_context_capture_paused;

	public static String TaskListView__paused_;

	public static String TaskListView_Advanced_Filters_Label;

	public static String TaskListView_Repository;

	public static String TaskListView_Summary;

	public static String TaskListView_Task_List;

	public static String TaskListView_Welcome_Message;

	public static String TaskListView_Welcome_Message_Title;

	public static String TaskScheduleContentProvider_Completed;

	public static String TaskScheduleContentProvider_Future;

	public static String TaskScheduleContentProvider_Incoming;

	public static String TaskScheduleContentProvider_Outgoing;

	public static String TaskScheduleContentProvider_Two_Weeks;

	public static String TaskScheduleContentProvider_Unscheduled;

	public static String UpdateRepositoryConfigurationAction_Update_Repository_Configuration;

}
