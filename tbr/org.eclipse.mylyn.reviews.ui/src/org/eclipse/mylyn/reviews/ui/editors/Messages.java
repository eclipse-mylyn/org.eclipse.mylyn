/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui.editors;

import org.eclipse.osgi.util.NLS;

/*
 * @author Kilian Matt
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.reviews.ui.editors.messages"; //$NON-NLS-1$
	public static String CreateReviewTaskEditorPageFactory_Reviews;
	public static String CreateReviewTaskEditorPart_Patches;
	public static String CreateReviewTaskEditorPart_Create_Review;
	public static String CreateReviewTaskEditorPart_Header_Author;
	public static String CreateReviewTaskEditorPart_Header_Date;
	public static String CreateReviewTaskEditorPart_Header_Filename;
	public static String EditorSupport_Original;
	public static String EditorSupport_Patched;
	public static String NewReviewTaskEditorInput_ReviewPrefix;
	public static String NewReviewTaskEditorInput_Tooltip;
	public static String ReviewEditor_Assigned_to;
	public static String ReviewEditor_Comment;
	public static String ReviewEditor_Create_delegated_Review;
	public static String ReviewEditor_Diff;
	public static String ReviewEditor_Files;
	public static String ReviewEditor_New_Patch_based_Review;
	public static String ReviewEditor_Rating;
	public static String ReviewEditor_Review;
	public static String ReviewEditor_Submit;
	public static String ReviewSummaryTaskEditorPart_Header_Author;
	public static String ReviewSummaryTaskEditorPart_Header_Comment;
	public static String ReviewSummaryTaskEditorPart_Header_Scope;
	public static String ReviewSummaryTaskEditorPart_Header_Result;
	public static String ReviewSummaryTaskEditorPart_Header_Reviewer;
	public static String ReviewSummaryTaskEditorPart_Header_ReviewId;
	public static String ReviewSummaryTaskEditorPart_Partname;
	public static String ReviewTaskEditorInput_New_Review;
	public static String TaskEditorPatchReviewPart_Diff;
	public static String TaskEditorPatchReviewPart_Files;
	public static String TaskEditorPatchReviewPart_Name;
	public static String TaskEditorPatchReviewPart_Patches;
	public static String TaskEditorPatchReviewPart_Rating;
	public static String TaskEditorPatchReviewPart_Review;
	public static String UpdateReviewTask_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
