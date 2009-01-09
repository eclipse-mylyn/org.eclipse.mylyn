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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.ui.editor.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String BugzillaFlagPart_flags;

	public static String BugzillaKeywordAttributeEditor_Edit_;

	public static String BugzillaPeoplePart_People;

	public static String BugzillaPeoplePart__Select_to_remove_;

	public static String BugzillaPlanningEditorPart_Current_Estimate;

	public static String BugzillaPlanningEditorPart_Team_Planning;

	public static String BugzillaTaskEditorPage_Please_enter_a_description_before_submitting;

	public static String BugzillaTaskEditorPage_Please_enter_a_short_summary_before_submitting;

	public static String BugzillaTaskEditorPage_Please_select_a_component_before_submitting;

	public static String BugzillaTaskEditorPage_Task_Submit_Error;

	public static String BugzillaVotesEditor_Show_votes;

	public static String BugzillaVotesEditor_Vote;

	public static String KeywordsDialog_Select_Keywords;
}
