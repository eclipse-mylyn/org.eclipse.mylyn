/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.history;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.team.ui.history.messages"; //$NON-NLS-1$

	public static String TaskHistoryPage_Added_Column_Label;

	public static String TaskHistoryPage_Author_Column_Label;

	public static String TaskHistoryPage_Field_Column_Label;

	public static String TaskHistoryPage_New_Value_Label;

	public static String TaskHistoryPage_Old_Value_Label;

	public static String TaskHistoryPage_Removed_Column_Label;

	public static String TaskHistoryPage_Task_history_for_X_Desscription_Label;

	public static String TaskHistoryPage_Time_Column_Label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
