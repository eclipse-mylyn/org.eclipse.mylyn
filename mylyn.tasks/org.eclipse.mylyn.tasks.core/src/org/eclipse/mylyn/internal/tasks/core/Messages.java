/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.core.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String DayDateRange___Today;

	public static String LocalRepositoryConnector_Local;

	public static String LocalRepositoryConnector_Local_Task_Repository;

	public static String LocalRepositoryConnector_New_Task;

	public static String RepositoryExternalizationParticipant_Task_Repositories;

	public static String UncategorizedTaskContainer_Uncategorized;

	public static String UnmatchedTaskContainer_Unmatched;

	public static String UnsubmittedTaskContainer_Unsubmitted;

	public static String WeekDateRange_Next_Week;

	public static String WeekDateRange_Previous_Week;

	public static String WeekDateRange_This_Week;

	public static String WeekDateRange_Two_Weeks;

	public static String PriorityLevel_High;

	public static String PriorityLevel_Low;

	public static String PriorityLevel_Normal;

	public static String PriorityLevel_Very_High;

	public static String PriorityLevel_Very_Low;

	public static String ScheduledTaskContainer_Next_Day;

	public static String TaskJobFactory_Refreshing_repository_configuration;

	public static String TaskRelation_Dependency;

	public static String TaskRelation_Parent;

	public static String TaskRelation_Subtask;

}
