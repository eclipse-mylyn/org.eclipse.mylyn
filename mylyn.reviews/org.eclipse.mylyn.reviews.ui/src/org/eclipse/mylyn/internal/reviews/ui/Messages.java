/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.reviews.ui.messages"; //$NON-NLS-1$

	public static String Reviews_AddCommentDialog_Title;

	public static String Reviews_AddCommentDialog_Message;

	public static String Reviews_GeneralCommentsText;

	public static String Reviews_NextComment;

	public static String Reviews_NextComment_Tooltip;

	public static String Reviews_PreviousComment;

	public static String Reviews_PreviousComment_Tooltip;

	public static String ReviewsLabelProvider_Artifact;

	public static String ReviewsLabelProvider_Author;

	public static String ReviewsLabelProvider_Bracket_X_bracket;

	public static String ReviewsLabelProvider_Change_X;

	public static String ReviewsLabelProvider_Comment;

	public static String ReviewsLabelProvider_Item;

	public static String ReviewsLabelProvider_Items;

	public static String ReviewsLabelProvider_Last_Change;

	public static String ReviewsLabelProvider_Question_mark;

	public static String ReviewsLabelProvider_Unknown;

	public static String ReviewsLabelProvider_X_ago;

	public static String ReviewsLabelProvider_X_comments;

	public static String ReviewsLabelProvider_X_comments_Y_drafts;

	public static String ReviewsLabelProvider_X_dot_dot_dot;

	public static String ReviewsLabelProvider_X_drafts;

	public static String ReviewsLabelProvider_X_renamed_from_Y_Z;

	public static String ReviewsLabelProvider_X_Revision_Y;

	public static String ReviewsUiPlugin_Updating_task_review_mapping;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
