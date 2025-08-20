/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.dialogs.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String TaskCompareDialog_Ascending;

	public static String TaskCompareDialog_Descending;

	public static String TaskCompareDialog_Presentation_warning;

	public static String TaskCompareDialog_Sort_by;

	public static String TaskCompareDialog_Sorting;

	public static String TaskCompareDialog_Tasks;

	public static String TaskCompareDialog_Then_by;

	public static String TaskListSortDialog_Queries_and_Categories;

	public static String TaskListSortDialog_Grouped_by;

	public static String TaskListSortDialog_Title;

	public static String TaskRepositoryCredentialsDialog_ChooseCertificateFile;

	public static String TaskRepositoryCredentialsDialog_Enter_Credentials;

	public static String TaskRepositoryCredentialsDialog_Enter_repository_credentials;

	public static String TaskRepositoryCredentialsDialog_HTML_Open_Repository_Properties;

	public static String TaskRepositoryCredentialsDialog_Password;

	public static String TaskRepositoryCredentialsDialog_Repository_Authentication;

	public static String TaskRepositoryCredentialsDialog_Save_Password;

	public static String TaskRepositoryCredentialsDialog_Saved_passwords_are_stored_that_is_difficult;

	public static String TaskRepositoryCredentialsDialog_Task_Repository;

	public static String TaskRepositoryCredentialsDialog_User_ID;

	public static String TaskRepositoryCredentialsDialog_Filename;

	public static String UiLegendControl_Active_task;

	public static String UiLegendControl_Adjust_Colors_and_Fonts_;

	public static String UiLegendControl_Also_see_the_Getting_Started_documentation_online;

	public static String UiLegendControl_Category;

	public static String UiLegendControl_Completed;

	public static String UiLegendControl_Completed_today;

	public static String UiLegendControl_Conflicting_changes;

	public static String UiLegendControl_Date_range;

	public static String UiLegendControl__default_;

	public static String UiLegendControl_Focus_view_on_active_task;

	public static String UiLegendControl_Has_Due_date;

	public static String UiLegendControl_http_www_eclipse_org_mylyn_start;

	public static String UiLegendControl_Inactive_task_with_context;

	public static String UiLegendControl_Inactive_task_with_no_context;

	public static String UiLegendControl_Incoming_changes;

	public static String UiLegendControl_New_task;

	public static String UiLegendControl_Notes;

	public static String UiLegendControl_Open_Task_List_;

	public static String UiLegendControl_Outgoing_changes;

	public static String UiLegendControl_Past_due_date;

	public static String UiLegendControl_Past_scheduled_date;

	public static String UiLegendControl_Priorities;

	public static String UiLegendControl_Query;

	public static String UiLegendControl_Scheduled_for_today;

	public static String UiLegendControl_Synchronization;

	public static String UiLegendControl_Synchronization_failed;

	public static String UiLegendControl_Synchronization_error;

	public static String UiLegendControl_Task;

	public static String UiLegendControl_Task_Owned;

	public static String UiLegendControl_Task_Activity;

	public static String UiLegendControl_Task_Context;

	public static String UiLegendControl_Tasks;

	public static String UiLegendControl_Tasks_UI_Legend;

	public static String UiLegendControl_Unsubmitted_outgoing_changes;

	public static String UiLegendDialog_Close_Dialog;
}
