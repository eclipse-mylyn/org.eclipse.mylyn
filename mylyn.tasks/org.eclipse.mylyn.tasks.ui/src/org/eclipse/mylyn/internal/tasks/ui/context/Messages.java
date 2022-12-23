/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.context;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.context.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ContextAttachWizard_Attach_Context;

	public static String ContextAttachWizardPage_Enter_comment;

	public static String ContextAttachWizardPage_Attaches_local_context_to_repository_task;

	public static String ContextAttachWizardPage_Comment_;

	public static String ContextAttachWizardPage_Repository_;

	public static String ContextAttachWizardPage_Task;

	public static String ContextRetrieveWizard_Retrieve_Context;

	public static String ContextRetrieveWizardPage_Author;

	public static String ContextRetrieveWizardPage_Date;

	public static String ContextRetrieveWizardPage_Description;

	public static String ContextRetrieveWizardPage_Select_context;

	public static String ContextRetrieveWizardPage_SELECT_A_CONTEXT_TO_RETTRIEVE_FROM_TABLE_BELOW;

	public static String ClearContextHandler_CLEAR_THE_CONTEXT_THE_FOR_SELECTED_TASKS;

	public static String ContextRetrieveWizardPage_Task;

	public static String RetrieveLatestContextDialog_Dialog_Title;

	public static String RetrieveLatestContextDialog_No_local_context_exists;

	public static String RetrieveLatestContextDialog_Show_All_Contexts_Label;

	public static String RetrieveLatestContextDialog_Unknown;

	public static String ClearContextHandler_CLEAR_THE_CONTEXT_THE_FOR_SELECTED_TASK;

	public static String ClearContextHandler_Confirm_clear_context;

	public static String CopyContextHandler_Merge;

	public static String CopyContextHandler_Copy_Context;

	public static String CopyContextHandler_No_source_task_selected;

	public static String CopyContextHandler_No_target_task_selected;

	public static String CopyContextHandler_Replace;

	public static String CopyContextHandler_Select_Target_Task;

	public static String CopyContextHandler_Select_the_target_task__;

	public static String CopyContextHandler_SELECTED_TASK_ALREADY_HAS_CONTEXT;

	public static String CopyContextHandler_SOURCE_TASK_DOES_HAVE_A_CONTEXT;

	public static String CopyContextHandler_TARGET_TASK_CON_NOT_BE_THE_SAME_AS_SOURCE_TASK;

}
