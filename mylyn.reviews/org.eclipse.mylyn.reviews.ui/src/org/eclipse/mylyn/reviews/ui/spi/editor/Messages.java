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

package org.eclipse.mylyn.reviews.ui.spi.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.reviews.ui.spi.editor.messages"; //$NON-NLS-1$

	public static String AbstractReviewTaskEditorPage_Show_Review_Navigator;

	public static String ReviewDetailSection_Bracket_X_bracket;

	public static String ReviewDetailSection_Depends_On;

	public static String ReviewDetailSection_Link_W_X_Y_by_Z;

	public static String ReviewDetailSection_Needed_By;

	public static String ReviewDetailSection_Needs_X;

	public static String ReviewDetailSection_Review;

	public static String ReviewDetailSection_Reviewers;

	public static String Reviews_RetrievingContents;

	public static String Reviews_UpdateFailure_X;

	public static String ReviewSetContentSection_Author;

	public static String ReviewSetContentSection_Commit;

	public static String ReviewSetContentSection_Committer;

	public static String ReviewSetContentSection_Parents;

	public static String ReviewSetContentSection_Ref;

	public static String ReviewSetContentSection_Unspecified;

	public static String ReviewSetContentSection_X_comma_Y_Comments;

	public static String ReviewSetSection_Patch_Sets;

	public static String ReviewSet_BuildHeader;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
