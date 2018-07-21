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

	public static String NewLocalTaskHandler_Could_not_create_local_task;

	public static String OpenTaskAttachmentHandler_failedToOpenViewer;

	public static String OpenTaskAttachmentHandler_noAttachmentViewerFound;

	public static String RemoteTaskSelectionDialog_Add_;

	public static String RemoteTaskSelectionDialog_Add_to_Task_List_category;

	public static String RemoteTaskSelectionDialog_Enter_Key_ID__use_comma_for_multiple_;

	public static String RemoteTaskSelectionDialog_Enter_a_valid_task_ID;

	public static String RemoteTaskSelectionDialog_Matching_tasks;

	public static String RemoteTaskSelectionDialog_Select_a_task_or_repository;

	public static String RemoteTaskSelectionDialog_Select_a_task_repository;

	public static String ShowTasksConnectorDiscoveryWizardCommandHandler_Install_Connectors;

	public static String ShowTasksConnectorDiscoveryWizardCommandHandler_Notify_when_updates_are_available_Text;

	public static String ShowTasksConnectorDiscoveryWizardCommandHandler_Unable_to_launch_connector_install;

	public static String MarkTaskHandler_MarkTasksReadOperation;

	public static String MarkTaskHandler_MarkTasksUnreadOperation;
}
