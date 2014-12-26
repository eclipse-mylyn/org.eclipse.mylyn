/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.operations;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.gerrit.ui.operations.messages"; //$NON-NLS-1$

	public static String AbandonDialog_Abandon_Change;

	public static String AbandonDialog_Enter_optional_message;

	public static String AddReviewersDialog_Add_Reviewers;

	public static String AddReviewersDialog_Enter_list_of_names_or_emails;

	public static String PublishDialog_Change_X_dash_Y;

	public static String PublishDialog_Publish_Comments;

	public static String PublishDialog_Publishes_1_draft;

	public static String PublishDialog_Publishes_X_drafts;

	public static String CherryPickDialog_Cherry_Pick;

	public static String CherryPickDialog_Change_X_Set_Y;

	public static String CherryPickDialog_Cherry_Pick_to_Branch;

	public static String CherryPickDialog_Cherry_Pick_Commit_Message;

	public static String CherryPickDialog_Cherry_Pick_Commit_Message_Template;

	public static String RebaseDialog_Rebase_Patch_Set;

	public static String RebaseDialog_Rebase_patch_set_X;

	public static String RestoreDialog_Enter_message;

	public static String RestoreDialog_Restore_Change;

	public static String SubmitDialog_Submit_Change;

	public static String SubmitDialog_Submit_change_confirmation;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
