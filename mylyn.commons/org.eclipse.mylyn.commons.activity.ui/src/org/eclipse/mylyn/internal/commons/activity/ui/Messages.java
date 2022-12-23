/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.activity.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.commons.activity.ui.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String CheckActivityJob_Activity_Monitor_Job;

	public static String TasksUiPreferencePage_Enable_inactivity_timeouts;

	public static String TasksUiPreferencePage_Enable_Time_Tracking;

	public static String TasksUiPreferencePage_If_disabled;

	public static String TasksUiPreferencePage_minutes_of_inactivity;

	public static String TasksUiPreferencePage_Stop_time_accumulation_after;

	public static String TasksUiPreferencePage_Task_Timing;

	public static String TasksUiPreferencePage_Track_Time_Spent;

}
