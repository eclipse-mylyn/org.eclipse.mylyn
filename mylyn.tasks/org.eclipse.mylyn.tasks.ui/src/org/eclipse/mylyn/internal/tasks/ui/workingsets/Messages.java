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

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.workingsets.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String TaskWorkingSetPage_The_name_must_not_be_empty;

	public static String TaskWorkingSetPage_The_name_must_not_have_a_leading_or_trailing_whitespace;

	public static String TaskWorkingSetPage_No_categories_queries_selected;

	public static String TaskWorkingSetPage_Resources;

	public static String TaskWorkingSetPage_Select_Working_Set_Elements;

	public static String TaskWorkingSetPage_Tasks;

	public static String TaskWorkingSetPage_Page_Description;

	public static String TaskWorkingSetPage_A_working_set_with_the_same_name_already_exists;

	public static String TaskWorkingSetPage_Select_All;

	public static String TaskWorkingSetPage_Deselect_All;

	public static String TaskWorkingSetPage_Working_set_name;

	public static String TaskWorkingSetPage_Working_set_contents;
}
