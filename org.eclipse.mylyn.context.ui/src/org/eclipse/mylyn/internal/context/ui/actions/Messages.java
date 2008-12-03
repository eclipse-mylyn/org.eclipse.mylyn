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

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.context.ui.actions.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AbstractInterestManipulationAction_Interest_Manipulation;

	public static String AbstractInterestManipulationAction_No_task_context_is_active;

	public static String ContextAttachAction_Attach_;

	public static String ContextAttachAction_Attach_Task_Context;

	public static String ContextAttachAction_Context_Attachment;

	public static String ContextAttachAction_Task_must_be_synchronized_before_attaching_context;

	public static String ContextClearAction_Clear;

	public static String ContextClearAction_Clear_the_context_for_the_selected_task;

	public static String ContextClearAction_Confirm_clear_context;

	public static String ContextCopyAction_Copy_Context;

	public static String ContextCopyAction_Copy_Task_Context_to_;

	public static String ContextCopyAction_Copy_to_;

	public static String ContextCopyAction_No_source_task_selected;

	public static String ContextCopyAction_No_target_task_selected;

	public static String ContextCopyAction_Select_Target_Task;

	public static String ContextCopyAction_Select_the_target_task__;

	public static String ContextCopyAction_Source_task_does_not_have_a_context;

	public static String ContextCopyAction_Target_task_can_not_be_the_same_as_source_task;

	public static String ContextRetrieveAction_CAN_NOT_RETRIEVE_CONTEXT_FOR_LOCAL_TASKS;

	public static String ContextRetrieveAction_Retrieve_;

	public static String ContextRetrieveAction_Retrieve_Task_Context;

	public static String FocusTaskListAction_No_tasks_scheduled_for_this_week;

	public static String ToggleDecorateInterestLevelAction_Decorate_Interest;

	public static String ToggleDecorateInterestLevelAction_Toggle_Interest_Level_Decorator;
}
