/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.ui.tasklist.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String BugzillaCustomQueryDialog_Bugzilla_Query_Category_Name;

	public static String BugzillaCustomQueryDialog_Max_Hits_Returned__1_means_all_;

	public static String BugzillaCustomQueryDialog_Query_URL;

	public static String BugzillaCustomQueryWizardPage_Create_query_from_URL;

	public static String BugzillaCustomQueryWizardPage_Enter_the_title_and_URL_for_the_query;

	public static String BugzillaCustomQueryWizardPage_No_Valid_Buglist_URL;

	public static String BugzillaCustomQueryWizardPage_Please_specify_Query_URL;

	public static String BugzillaCustomQueryWizardPage_Query_Title;

	public static String BugzillaCustomQueryWizardPage_Query_URL;

	public static String BugzillaQueryTypeWizardPage_Choose_query_type;

	public static String BugzillaQueryTypeWizardPage_Create_query_from_existing_URL;

	public static String BugzillaQueryTypeWizardPage_Create_query_using_form;

	public static String BugzillaQueryTypeWizardPage_Select_from_the_available_query_types;

	public static String BugzillaRepositorySettingsPage_All;

	public static String BugzillaRepositorySettingsPage_AUTOTETECT_PLATFORM_AND_OS;

	public static String BugzillaRepositorySettingsPage_admin_parameter;

	public static String BugzillaRepositorySettingsPage_available_once_repository_created;

	public static String BugzillaRepositorySettingsPage_Browse_descriptor;

	public static String BugzillaRepositorySettingsPage_browseToolTip;

	public static String BugzillaRepositorySettingsPage_bugzilla_repository_settings;

	public static String BugzillaRepositorySettingsPage_Custom_Workflow;

	public static String BugzillaRepositorySettingsPage_example_do_not_include;

	public static String BugzillaRepositorySettingsPage_Language_;

	public static String BugzillaRepositorySettingsPage_descriptor_file;

	public static String BugzillaRepositorySettingsPage_DescriptorFileNotExists;

	public static String BugzillaRepositorySettingsPage_DownloadText;

	public static String BugzillaRepositorySettingsPage_DownloadToolTip;

	public static String BugzillaRepositorySettingsPage_insiderGroup;

	public static String BugzillaRepositorySettingsPage_insiderGroup_ToolTip;

	public static String BugzillaRepositorySettingsPage_local_users_enabled;

	public static String BugzillaRepositorySettingsPage_override_auto_detection_of_platform;

	public static String BugzillaRepositorySettingsPage_RequiresBugzilla3_6;

	public static String BugzillaRepositorySettingsPage_Retrieving_repository_configuration;

	public static String BugzillaRepositorySettingsPage_SelectDescriptorFile;

	public static String BugzillaRepositorySettingsPage_Server_URL_is_invalid;

	public static String BugzillaRepositorySettingsPage_suppressToolTip;

	public static String BugzillaRepositorySettingsPage_use_see_also;

	public static String BugzillaRepositorySettingsPage_usebugaliases;

	public static String BugzillaRepositorySettingsPage_useclassification;

	public static String BugzillaRepositorySettingsPage_useqacontact;

	public static String BugzillaRepositorySettingsPage_usestatuswhiteboard;

	public static String BugzillaRepositorySettingsPage_usetargetmilestone;

	public static String BugzillaRepositorySettingsPage_UseXmlRpc;

	public static String BugzillaRepositorySettingsPage_Validating_server_settings;

	public static String BugzillaTaskAttachmentPage_Advanced;

}
