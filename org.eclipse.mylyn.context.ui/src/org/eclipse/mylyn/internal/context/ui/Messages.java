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

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.context.ui.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ContextUiPlugin_No_local_task_context_exists;

	public static String ContextUiPlugin_Task_Activation;

	public static String TaskContextWorkingSetPage_Cannot_create_another_Active_Taskscape_Working_Set;

	public static String TaskContextWorkingSetPage_CREATE_THE_MYLYN_CONTEXT_WORKING_SET;

	public static String TaskContextWorkingSetPage_Mylyn_Task_Context_Working_Set;

	public static String TaskContextWorkingSetPage_Name;

	public static String TaskContextWorkingSetPage_NOTE_THIS_WORKING_SET_SHOULD_ONLY_BE_USED_FOR_SEARCHS;

	public static String TaskContextWorkingSetPage_TASK_CONTEXT_FOR_SEARCH;

	public static String UiUtil_Mylyn_Interest_Manipulation;

	public static String UiUtil_Not_a_valid_landmark;

	public static String AbstractFocusViewAction_Apply_Mylyn;

	public static String AbstractFocusViewAction_Empty_task_context;
}
