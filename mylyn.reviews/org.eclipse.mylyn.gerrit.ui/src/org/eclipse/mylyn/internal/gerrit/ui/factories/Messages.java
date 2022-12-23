/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.gerrit.ui.factories.messages"; //$NON-NLS-1$

	public static String AbandonUiFactory_Abandon;

	public static String AbstractPatchSetUiFactory_Clone_Git_Repository;

	public static String AbstractPatchSetUiFactory_Gerrit_Fetch_Change_Error;

	public static String AbstractPatchSetUiFactory_Git_repository_not_found_in_workspace;

	public static String AbstractPatchSetUiFactory_No_Git_repository_found_for_fetching;

	public static String AbstractPatchSetUiFactory_No_remote_config_found_with_fetch_URL;

	public static String AddReviewersUiFactory_Add_Reviewers;

	public static String CherryPickUiFactory_Cherry_Pick;

	public static String CompareWithUiFactory_Base;

	public static String CompareWithUiFactory_Compare_Patch_Set_X_with_Y;

	public static String CompareWithUiFactory_Compare_With;

	public static String CompareWithUiFactory_Compare_With_Base;

	public static String CompareWithUiFactory_Compare_with_X;

	public static String FetchUiFactory_Fetch;

	public static String OpenCommitUiFactory_Open_Commit;

	public static String OpenCommitUiFactory_Opening_Commit_Viewer;

	public static String OpenFileUiFactory_File_not_available;

	public static String OpenFileUiFactory_Open_File;

	public static String PublishUiFactory_Clearing_status_failed;

	public static String PublishUiFactory_Error_while_clearing_status;

	public static String PublishUiFactory_Publish_Comments;

	public static String RebaseUiFactory_Rebase;

	public static String RemoveReviewerUiFactory_Remove_Reviewer;

	public static String RemoveReviewerUiFactory_Remove_Reviewer_Name;

	public static String RestoreUiFactory_Restore;

	public static String SubmitUiFactory_Submit;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
