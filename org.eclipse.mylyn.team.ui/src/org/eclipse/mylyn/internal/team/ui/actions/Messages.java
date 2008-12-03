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

package org.eclipse.mylyn.internal.team.ui.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.team.ui.actions.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AddToTaskContextAction_ACTIVATE_TASK_TO_ADD_RESOURCES;
	public static String AddToTaskContextAction_Add_to_Task_Context;
	public static String AddToTaskContextAction_No_resources_to_add;

	public static String ApplyPatchAction_Apply_Patch;
	public static String ApplyPatchAction_Error_Retrieving_Context;

	public static String OpenCorrespondingTaskAction_Completed;
	public static String OpenCorrespondingTaskAction_Open_Corresponding_Task;
	public static String OpenCorrespondingTaskAction_Open_Task;
	public static String OpenCorrespondingTaskAction_Opening_Corresponding_Task;
	public static String OpenCorrespondingTaskAction_Progress_on;
	public static String OpenCorrespondingTaskAction_Unable_to_match_task;
}
