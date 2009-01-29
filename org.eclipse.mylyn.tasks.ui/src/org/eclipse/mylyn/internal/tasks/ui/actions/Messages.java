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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.actions.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AbstractChangeCompletionAction_Mark_selected_local_tasks_X;

	public static String ActivateTaskDialogAction_Activate_Task;

	public static String ActivateTaskDialogAction_Select_a_task_to_activate__;

	public static String ActivateTaskHistoryDropDownAction_Activate_Previous_Task;

	public static String AddRepositoryAction_Add_new_query;

	public static String AddRepositoryAction_Add_a_query_to_the_Task_List;

	public static String AddRepositoryAction_Add_Task_Repository;

	public static String AddRepositoryAction_Do_not_show_again;

	public static String ClearOutgoingAction_Clear_outgoing;

	public static String ClearOutgoingAction_Clear_outgoing_failed;

	public static String ClearOutgoingAction_Confirm_discard;

	public static String ClearOutgoingAction_Discard_all_outgoing_changes_;

	public static String CloneTaskAction_Clone_Task_Failed;

	public static String CloneTaskAction_Clone_This_Task;

	public static String CloneTaskAction_Cloned_from_;

	public static String CollapseAllAction_Collapse_All;

	public static String CompareAttachmentsAction_Compare__;

	public static String CompareAttachmentsAction_Compare_Attachments;

	public static String CompareAttachmentsAction_Failed_to_find_attachment;

	public static String CopyCommentDetailsAction_Copy_User_ID;

	public static String CopyCommentDetailsAction_Copy_User_ID_Tooltip;

	public static String CopyTaskDetailsAction_Copy_Details;

	public static String DeleteAction_Confirm_Delete;

	public static String DeleteAction_Delete;

	public static String DeleteAction_Delete_all_of_the_unsubmitted_tasks;

	public static String DeleteAction_Delete_the_elements_listed_below;

	public static String DeleteAction_Delete_failed;

	public static String DeleteAction_Delete_the_planning_information_and_context_for_the_repository_task;

	public static String DeleteAction_Delete_the_planning_information_and_context_of_all_unmatched_tasks;

	public static String DeleteAction_Nothing_selected;

	public static String DeleteAction_Permanently_delete_the_category;

	public static String DeleteAction_Permanently_delete_the_element_listed_below;

	public static String DeleteAction_Permanently_delete_the_query;

	public static String DeleteAction_Permanently_delete_the_task_listed_below;

	public static String DeleteTaskRepositoryAction_Confirm_Delete;

	public static String DeleteTaskRepositoryAction_Delete_Repository;

	public static String DeleteTaskRepositoryAction_Delete_the_selected_task_repositories;

	public static String DeleteTaskRepositoryAction_Repository_In_Use;

	public static String DeleteTaskRepositoryAction_Repository_In_Use_MESSAGE;

	public static String EditRepositoryPropertiesAction_Properties;

	public static String ExpandAllAction_Expand_All;

	public static String ExportAction_Dialog_Title;

	public static String ExportAction_Nothing_selected;

	public static String ExportAction_Problems_encountered;

	public static String ExportAction_X_exists_Do_you_wish_to_overwrite;

	public static String FilterCompletedTasksAction_Filter_Completed_Tasks;

	public static String GoIntoAction_Go_Into;

	public static String GoUpAction_Go_Up_To_Root;

	public static String GroupSubTasksAction_Group_Subtasks;

	public static String ImportAction_Dialog_Title;

	public static String ImportAction_Problems_encountered;

	public static String LinkWithEditorAction_Link_with_Editor;

	public static String NewCategoryAction_A_category_with_this_name_already_exists;

	public static String NewCategoryAction_Enter_name;

	public static String NewCategoryAction_Enter_a_name_for_the_Category;

	public static String NewCategoryAction_New_Category;

	public static String NewCategoryAction_New_Category_;

	public static String NewCategoryAction_A_query_with_this_name_already_exists;

	public static String NewSubTaskAction_The_connector_does_not_support_creating_subtasks_for_this_task;

	public static String NewSubTaskAction_Could_not_initialize_sub_task_data_for_task_;

	public static String NewSubTaskAction_Could_not_retrieve_task_data_for_task_;

	public static String NewSubTaskAction_Create_a_new_subtask;

	public static String NewSubTaskAction_Failed_to_create_new_sub_task_;

	public static String NewSubTaskAction_Subtask;

	public static String NewSubTaskAction_Unable_to_create_subtask;

	public static String NewTaskFromSelectionAction_Comment_;

	public static String NewTaskFromSelectionAction____Created_from_Comment___;

	public static String NewTaskFromSelectionAction_New_Task_from_Selection;

	public static String NewTaskFromSelectionAction_Nothing_selected_to_create_task_from;

	public static String NewTaskFromSelectionAction_URL_;

	public static String OpenRepositoryTask_Could_not_find_matching_repository_task;

	public static String OpenRepositoryTask_Open_Repository_Task;

	public static String OpenRepositoryTask_Open_Task;

	public static String OpenTaskAction_Open_Task;

	public static String OpenTaskAction_Select_a_task_to_open__;

	public static String OpenTaskListElementAction_Open;

	public static String OpenTaskListElementAction_Open_Task_List_Element;

	public static String OpenTasksUiPreferencesAction_Preferences_;

	public static String OpenWithBrowserAction_Open_with_Browser;

	public static String PresentationDropDownSelectionAction_Task_Presentation;

	public static String QueryCloneAction_Clone_Query;

	public static String QueryCloneAction_Copy_of_X;

	public static String QueryCloneAction_No_query_selected;

	public static String RefreshRepositoryTasksAction_Refresh_All_Tasks;

	public static String RemoveFromCategoryAction_Remove_From_Category;

	public static String RenameAction_Rename;

	public static String ShowInTaskListAction_Show_In_Task_List;

	public static String SynchronizeAutomaticallyAction_Synchronize_Automatically;

	public static String SynchronizeEditorAction_Synchronize;

	public static String SynchronizeEditorAction_Synchronize_Incoming_Changes;

	public static String TaskActivateAction_Activate;

	public static String TaskDeactivateAction_Deactivate;

	public static String TaskListSortAction_Sort_;

	public static String TaskSelectionDialog__matches;

	public static String TaskSelectionDialog_Deselect_Working_Set;

	public static String TaskSelectionDialog_Edit_Active_Working_Set_;

	public static String TaskSelectionDialog_New_Task_;

	public static String TaskSelectionDialog_Open_with_Browser;

	public static String TaskSelectionDialog_Scanning_tasks;

	public static String TaskSelectionDialog_Search_for_tasks;

	public static String TaskSelectionDialog_Select_Working_Set_;

	public static String TaskSelectionDialog_Selected_item_is_not_a_task;

	public static String TaskSelectionDialog_Show_Completed_Tasks;

	public static String TaskWorkingSetAction_All;

	public static String TaskWorkingSetAction_Deselect_All;

	public static String TaskWorkingSetAction_Edit_Label;

	public static String TaskWorkingSetAction_Select_and_Edit_Working_Sets;

	public static String TaskWorkingSetAction_Sets;

	public static String ToggleAllWorkingSetsAction_Show_All;

	public static String ToggleTaskActivationAction_Activate_Task;

	public static String ToggleTaskActivationAction_Deactivate_Task;
}
