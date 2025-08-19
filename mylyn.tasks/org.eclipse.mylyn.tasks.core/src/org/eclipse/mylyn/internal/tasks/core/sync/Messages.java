/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.sync;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.core.sync.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String DeleteTasksJob_Deleting_tasks;

	public static String GetTaskHistoryJob_Retrieving_History;

	public static String GetTaskHistoryJob_Retrieving_Task_History;

	public static String SubmitTaskAttachmentJob_Sending_data;

	public static String SubmitTaskAttachmentJob_Submitting_attachment;

	public static String SubmitTaskAttachmentJob_Updating_task;

	public static String SubmitTaskJob_Receiving_data;

	public static String SubmitTaskJob_Sending_data;

	public static String SubmitTaskJob_Submitting_task;

	public static String SynchronizeQueriesJob_Processing;

	public static String SynchronizeQueriesJob_Querying_repository;

	public static String SynchronizeQueriesJob_Receiving_related_tasks;

	public static String SynchronizeQueriesJob_Synchronizing_Queries;

	public static String SynchronizeQueriesJob_Synchronizing_query_X;

	public static String SynchronizeQueriesJob_Updating_repository_state;

	public static String SynchronizeRepositoriesJob_Processing;

	public static String SynchronizeRepositoriesJob_Processing_;

	public static String SynchronizeRepositoriesJob_Synchronizing_Task_List;

	public static String SynchronizeRepositoriesJob_Updating_repository_configuration_for_X;

	public static String SynchronizeTasksJob_Processing;

	public static String SynchronizeTasksJob_Receiving_task_X;

	public static String SynchronizeTasksJob_Synchronization_of_task_ID_REPOSITORY_failed;

	public static String SynchronizeTasksJob_Synchronizing_Tasks__X_;

	public static String SynchronizeTasksJob_Receiving_X_tasks_from_X;

	public static String UpdateRepositoryConfigurationJob_Receiving_configuration;
}
