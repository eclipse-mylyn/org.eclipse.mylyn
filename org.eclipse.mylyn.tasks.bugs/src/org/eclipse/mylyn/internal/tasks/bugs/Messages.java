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

package org.eclipse.mylyn.internal.tasks.bugs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.bugs.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String DefaultSupportHandler_Configuration_Details;

	public static String DefaultSupportHandler_Date_X;

	public static String DefaultSupportHandler_Installed_Features;

	public static String DefaultSupportHandler_Message_X;

	public static String DefaultSupportHandler_Plugin_X;

	public static String DefaultSupportHandler_Product_X;

	public static String DefaultSupportHandler_Severity_X;

	public static String DefaultSupportHandler_Step_1;

	public static String DefaultSupportHandler_Step_2;

	public static String DefaultSupportHandler_Step_3;

	public static String DefaultSupportHandler_What_steps_message;

	public static String DefaultTaskContributor_Error;

	public static String DefaultTaskContributor_Error_Details;

	public static String DefaultTaskContributor_EXCEPTION_STACK_TRACE;

	public static String DefaultTaskContributor_Info;

	public static String DefaultTaskContributor_OK;

	public static String DefaultTaskContributor_SESSION_DATA;

	public static String DefaultTaskContributor_Warning;

	public static String TaskErrorReporter_Create_Task_Error_Message;

	public static String TaskErrorReporter_Create_Task_Error_Title;

	public static String TaskErrorReporter_Job_Progress_Process_support_request;
}
