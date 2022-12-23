/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

public class TaskListExternalizationConstants {

	public static final String DEFAULT_PRIORITY = PriorityLevel.P3.toString();

	public static final String OUT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S Z"; //$NON-NLS-1$

	public static final String IN_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S z"; //$NON-NLS-1$

	public static final String KEY_NOTIFIED_INCOMING = "NotifiedIncoming"; //$NON-NLS-1$

	public static final String KEY_NAME = "Name"; //$NON-NLS-1$

	public static final String KEY_LABEL = "Label"; //$NON-NLS-1$

	public static final String KEY_QUERY = "Query"; //$NON-NLS-1$

	public static final String KEY_QUERY_STRING = "QueryString"; //$NON-NLS-1$

	public static final String KEY_HANDLE = "Handle"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_URL = "RepositoryUrl"; //$NON-NLS-1$

	public static final String KEY_KIND = "Kind"; //$NON-NLS-1$

	public static final String KEY_TIME_ESTIMATED = "Estimated"; //$NON-NLS-1$

	public static final String KEY_ISSUEURL = "IssueURL"; //$NON-NLS-1$

	public static final String KEY_NOTES = "Notes"; //$NON-NLS-1$

	public static final String KEY_ACTIVE = "Active"; //$NON-NLS-1$

	public static final String KEY_PRIORITY = "Priority"; //$NON-NLS-1$

	public static final String VAL_FALSE = "false"; //$NON-NLS-1$

	public static final String VAL_TRUE = "true"; //$NON-NLS-1$

	public static final String KEY_DATE_END = "EndDate"; //$NON-NLS-1$

	public static final String KEY_DATE_CREATION = "CreationDate"; //$NON-NLS-1$

	public static final String KEY_DATE_SCHEDULED_START = "ScheduledStartDate"; //$NON-NLS-1$

	public static final String KEY_DATE_SCHEDULED_END = "ScheduledEndDate"; //$NON-NLS-1$

	public static final String KEY_DATE_MODIFICATION = "ModificationDate"; //$NON-NLS-1$

	public static final String KEY_DATE_DUE = "DueDate"; //$NON-NLS-1$

	public static final String KEY_REMINDED = "Reminded"; //$NON-NLS-1$

	public static final String KEY_SYNC_STATE = "offlineSyncState"; //$NON-NLS-1$

	public static final String KEY_OWNER = "Owner"; //$NON-NLS-1$

	public static final String KEY_OWNER_ID = "OwnerId"; //$NON-NLS-1$

	public static final String KEY_MARK_READ_PENDING = "MarkReadPending"; //$NON-NLS-1$

	public static final String KEY_CONNECTOR_KIND = "ConnectorKind"; //$NON-NLS-1$

	public static final String KEY_TASK_ID = "TaskId"; //$NON-NLS-1$

	public static final String KEY_LAST_REFRESH = "LastRefreshTimeStamp"; //$NON-NLS-1$

	public static final String KEY_KEY = "Key"; //$NON-NLS-1$

	public static final String NODE_TASK_LIST = "TaskList"; //$NON-NLS-1$

	public static final String NODE_TASK = "Task"; //$NON-NLS-1$

	public static final String NODE_SUB_TASK = "SubTask"; //$NON-NLS-1$

	public static final String NODE_QUERY = "Query"; //$NON-NLS-1$

	public static final String NODE_QUERY_HIT = "QueryHit"; //$NON-NLS-1$

	public static final String NODE_CATEGORY = "TaskCategory"; //$NON-NLS-1$

	public static final String NODE_TASK_REFERENCE = "TaskReference"; //$NON-NLS-1$

	public static final String NODE_ATTRIBUTE = "Attribute"; //$NON-NLS-1$
}
