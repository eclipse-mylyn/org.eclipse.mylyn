/*******************************************************************************
 * Copyright (c) 2009, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.core.data.messages"; //$NON-NLS-1$

	public static String DefaultTaskSchema_Add_Self_to_CC_Label;

	public static String DefaultTaskSchema_Attachment_ID_Label;

	public static String DefaultTaskSchema_Attachment_Label;

	public static String DefaultTaskSchema_Author_Label;

	public static String DefaultTaskSchema_Completion_Label;

	public static String DefaultTaskSchema_Component_Label;

	public static String DefaultTaskSchema_Content_Type_Label;

	public static String DefaultTaskSchema_Created_Label;

	public static String DefaultTaskSchema_Deprecated_Label;

	public static String DefaultTaskSchema_Description_Label;

	public static String DefaultTaskSchema_Due_Label;

	public static String DefaultTaskSchema_Filename_Label;

	public static String DefaultTaskSchema_ID_Label;

	public static String DefaultTaskSchema_Key_Label;

	public static String DefaultTaskSchema_Keywords_Label;

	public static String DefaultTaskSchema_Kind_Label;

	public static String DefaultTaskSchema_Modified_Label;

	public static String DefaultTaskSchema_Number_Label;

	public static String DefaultTaskSchema_Owner_Label;

	public static String DefaultTaskSchema_Patch_Label;

	public static String DefaultTaskSchema_Priority_Label;

	public static String DefaultTaskSchema_Private_Label;

	public static String DefaultTaskSchema_Product_Label;

	public static String DefaultTaskSchema_Rank_Label;

	public static String DefaultTaskSchema_Replace_existing_attachment;

	public static String DefaultTaskSchema_Reporter_Label;

	public static String DefaultTaskSchema_Resolution_Label;

	public static String DefaultTaskSchema_Severity_Label;

	public static String DefaultTaskSchema_Size_Label;

	public static String DefaultTaskSchema_Status_Label;

	public static String DefaultTaskSchema_Summary_Label;

	public static String DefaultTaskSchema_URL_Label;

	public static String TaskDataState_RefactorRoot;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
