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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.wizards.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String EditRepositoryWizard_Failed_to_refactor_repository_urls;

	public static String EditRepositoryWizard_Properties_for_Task_Repository;

	public static String AttachmentSourcePage__Clipboard_;

	public static String AttachmentSourcePage__Screenshot_;

	public static String AttachmentSourcePage_Browse_;

	public static String AttachmentSourcePage_Cannot_locate_attachment_file;

	public static String AttachmentSourcePage_Clipboard;

	public static String AttachmentSourcePage_Clipboard_contains_an_unsupported_data;

	public static String AttachmentSourcePage_Clipboard_supports_text_and_image_attachments_only;

	public static String AttachmentSourcePage_File;

	public static String AttachmentSourcePage_No_file_name;

	public static String AttachmentSourcePage_Select_attachment_source;

	public static String AttachmentSourcePage_Select_the_location_of_the_attachment;

	public static String AttachmentSourcePage_Workspace;

	public static String NewQueryWizard_New_Repository_Query;

	public static String NewTaskWizard_New_Task;

	public static String NewWebTaskPage_Create_via_Web_Browser;

	public static String NewWebTaskPage_New_Task;

	public static String NewWebTaskPage_Once_submitted_synchronize_queries_or_add_the_task_to_a_category;

	public static String NewWebTaskPage_This_will_open_a_web_browser_that_can_be_used_to_create_a_new_task;

	public static String AttachmentPreviewPage_A_preview_the_type_X_is_currently_not_available;

	public static String AttachmentPreviewPage_Attachment_Preview;

	public static String AttachmentPreviewPage_Could_not_create_preview;

	public static String AttachmentPreviewPage_Preparing_preview;

	public static String AttachmentPreviewPage_Review_the_attachment_before_submitting;

	public static String AttachmentPreviewPage_Run_in_background;

	public static String SelectRepositoryConnectorPage_discoveryProblemMessage;

	public static String SelectRepositoryConnectorPage_discoveryProblemTitle;

	public static String SelectRepositoryConnectorPage_downloadLink;

	public static String SelectRepositoryConnectorPage_Select_a_task_repository_type;

	public static String SelectRepositoryConnectorPage_You_can_connect_to_an_existing_account_using_one_of_the_installed_connectors;

	public static String SelectRepositoryPage_Add_new_repositories_using_the_X_view;

	public static String SelectRepositoryPage_Select_a_repository;

	public static String TaskAttachmentWizard_Add_Attachment;

	public static String TaskAttachmentWizard_Attach_Screenshot;

	public static String TaskAttachmentWizard_Attaching_context;

	public static String TaskAttachmentWizard_Attachment_Failed;

	public static String TaskAttachmentWizard_Screenshot;

	public static String TaskDataExportWizard_Export;

	public static String TaskDataExportWizard_export_failed;

	public static String TaskDataExportWizardPage_Browse_;

	public static String TaskDataExportWizardPage_Export_Mylyn_Task_Data;

	public static String TaskDataExportWizardPage_File;

	public static String TaskDataExportWizardPage_Folder;

	public static String TaskDataExportWizardPage_Folder_Selection;

	public static String TaskDataExportWizardPage_Please_choose_an_export_destination;

	public static String TaskDataExportWizardPage_Specify_the_destination_folder_for_task_data;

	public static String TaskDataImportWizard_confirm_overwrite;

	public static String TaskDataImportWizard_could_not_be_found;

	public static String TaskDataImportWizard_existing_task_data_about_to_be_erased_proceed;

	public static String TaskDataImportWizard_File_not_found;

	public static String TaskDataImportWizard_Import;

	public static String TaskDataImportWizard_Import_Error;

	public static String TaskDataImportWizard_Importing_Data;

	public static String TaskDataImportWizard_task_data_import_failed;

	public static String TaskDataImportWizardPage_Restore_tasks_from_history;

	public static String TaskDataImportWizardPage_Browse_;

	public static String TaskDataImportWizardPage_From_snapshot;

	public static String TaskDataImportWizardPage_From_zip_file;

	public static String TaskDataImportWizardPage_Import_method_backup;

	public static String TaskDataImportWizardPage_Import_method_zip;

	public static String TaskDataImportWizardPage_Import_Settings_saved;

	public static String TaskDataImportWizardPage_Import_Source_zip_file_setting;

	public static String TaskDataImportWizardPage_Importing_overwrites_current_tasks_and_repositories;

	public static String TaskDataImportWizardPage__unspecified_;

	public static String TaskDataImportWizardPage_Zip_File_Selection;

	public static String AbstractRepositoryQueryPage_A_category_with_this_name_already_exists;

	public static String AbstractRepositoryQueryPage_Enter_query_parameters;

	public static String AbstractRepositoryQueryPage_If_attributes_are_blank_or_stale_press_the_Update_button;

	public static String AbstractRepositoryQueryPage_Please_specify_a_title_for_the_query;

	public static String AbstractRepositoryQueryPage_A_query_with_this_name_already_exists;

	public static String AbstractRepositorySettingsPage_Additional_Settings;

	public static String AbstractRepositorySettingsPage_Anonymous_Access;

	public static String AbstractRepositorySettingsPage_Authentication_credentials_are_valid;

	public static String AbstractRepositorySettingsPage_Change_account_settings;

	public static String AbstractRepositorySettingsPage_Change_Settings;

	public static String AbstractRepositorySettingsPage_Character_encoding;

	public static String AbstractRepositorySettingsPage_Create_new_account;

	public static String AbstractRepositorySettingsPage_Default__;

	public static String AbstractRepositorySettingsPage_Disconnected;

	public static String AbstractRepositorySettingsPage_Enable_http_authentication;

	public static String AbstractRepositorySettingsPage_Enable_proxy_authentication;

	public static String AbstractRepositorySettingsPage_Enter_a_valid_server_url;

	public static String AbstractRepositorySettingsPage_Http_Authentication;

	public static String AbstractRepositorySettingsPage_Internal_error_validating_repository;

	public static String AbstractRepositorySettingsPage_Label_;

	public static String AbstractRepositorySettingsPage_Other;

	public static String AbstractRepositorySettingsPage_Password_;

	public static String AbstractRepositorySettingsPage_Problems_encountered_determining_available_charsets;

	public static String AbstractRepositorySettingsPage_Proxy_host_address_;

	public static String AbstractRepositorySettingsPage_Proxy_host_port_;

	public static String AbstractRepositorySettingsPage_Proxy_Server_Configuration;

	public static String AbstractRepositorySettingsPage_Repository_already_exists;

	public static String AbstractRepositorySettingsPage_Repository_is_valid;

	public static String AbstractRepositorySettingsPage_Repository_url_is_invalid;

	public static String AbstractRepositorySettingsPage_Repository_user_name_and_password_must_not_be_blank;

	public static String AbstractRepositorySettingsPage_Save_Password;

	public static String AbstractRepositorySettingsPage_Server_;

	public static String AbstractRepositorySettingsPage_Unable_to_authenticate_with_repository;

	public static String AbstractRepositorySettingsPage_Use_global_Network_Connections_preferences;

	public static String AbstractRepositorySettingsPage_User_ID_;

	public static String AbstractRepositorySettingsPage_Validate_Settings;

	public static String AbstractRepositorySettingsPage_Validating_server_settings;

	public static String NewTaskWizard_Create_Task;

	public static String NewTaskWizard_Error_creating_new_task;

	public static String NewTaskWizard_Failed_to_create_new_task_;

	public static String NewWebTaskWizard_New_Task;

	public static String NewWebTaskWizard_This_connector_does_not_provide_a_rich_task_editor_for_creating_tasks;

	public static String RepositoryQueryWizard_Edit_Repository_Query;

	public static String TaskAttachmentPage_ATTACHE_CONTEXT;

	public static String TaskAttachmentPage_Attachment_Details;

	public static String TaskAttachmentPage_Comment;

	public static String TaskAttachmentPage_Content_Type;

	public static String TaskAttachmentPage_Description;

	public static String TaskAttachmentPage_Enter_a_description;

	public static String TaskAttachmentPage_Enter_a_file_name;

	public static String TaskAttachmentPage_File;

	public static String TaskAttachmentPage_Patch;

	public static String TaskAttachmentPage_Verify_the_content_type_of_the_attachment;

	public static String AbstractTaskRepositoryPage_Validation_failed;

	public static String LocalRepositorySettingsPage_Configure_the_local_repository;

	public static String LocalRepositorySettingsPage_Local_Repository_Settings;
}
