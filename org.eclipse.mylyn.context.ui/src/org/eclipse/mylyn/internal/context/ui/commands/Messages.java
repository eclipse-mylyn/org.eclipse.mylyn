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

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.context.ui.commands.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ClearContextHandler_CLEAR_THE_CONTEXT_THE_FOR_SELECTED_TASK;

	public static String ClearContextHandler_Confirm_clear_context;

	public static String CopyContextHandler_Copy_Context;

	public static String CopyContextHandler_No_source_task_selected;

	public static String CopyContextHandler_No_target_task_selected;

	public static String CopyContextHandler_Select_Target_Task;

	public static String CopyContextHandler_Select_the_target_task__;

	public static String CopyContextHandler_SOURCE_TASK_DOES_HAVE_A_CONTEXT;

	public static String CopyContextHandler_TARGET_TASK_CON_NOT_BE_THE_SAME_AS_SOURCE_TASK;

}
