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

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.commands.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AddTaskRepositoryHandler_Add_Task_Repository;

	public static String NewLocalTaskHandler_Could_not_create_local_task;

	public static String OpenTaskAttachmentInDefaultEditorHandler_Failed_to_open_editor;

	public static String OpenTaskAttachmentInDefaultEditorHandler_No_default_editor_for_X_found;

	public static String OpenTaskAttachmentInDefaultEditorHandler_Open_Attachment_Failed;

	public static String RemoteTaskSelectionDialog_Add_;

	public static String RemoteTaskSelectionDialog_Add_to_Task_List_category;

	public static String RemoteTaskSelectionDialog_Enter_Key_ID__use_comma_for_multiple_;

	public static String RemoteTaskSelectionDialog_Enter_a_valid_task_ID;

	public static String RemoteTaskSelectionDialog_Matching_tasks;

	public static String RemoteTaskSelectionDialog_Select_a_task_or_repository;

	public static String RemoteTaskSelectionDialog_Select_a_task_repository;
}
