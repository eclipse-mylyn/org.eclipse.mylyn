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

package org.eclipse.mylyn.internal.tasks.core;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
class OfflineDataStore implements Serializable {

	private static final long serialVersionUID = -3909632088254980426L;

	/** Last new repository task taskId */
	private int lastNewRepositoryTaskId = 0;

	// Local changes to existing reports
	private final Map<String, Set<RepositoryTaskAttribute>> localEdits = new ConcurrentHashMap<String, Set<RepositoryTaskAttribute>>();

	/** Older version of Task Data */
	private final Map<String, RepositoryTaskData> oldTaskDataMap = new ConcurrentHashMap<String, RepositoryTaskData>();

	/** Newest version of the task data */
	private final Map<String, RepositoryTaskData> newTaskDataMap = new ConcurrentHashMap<String, RepositoryTaskData>();

	/** New unsubmitted repository task data */
	private final Map<String, RepositoryTaskData> unsubmittedTaskData = new ConcurrentHashMap<String, RepositoryTaskData>();

	public void setLastNewTaskId(int lastNumber) {
		lastNewRepositoryTaskId = new Integer(lastNumber);
	}

	public int getNextTaskId() {
		lastNewRepositoryTaskId++;
		return lastNewRepositoryTaskId;
	}

	public Map<String, RepositoryTaskData> getOldDataMap() {
		return oldTaskDataMap;
	}

	public Map<String, RepositoryTaskData> getNewDataMap() {
		return newTaskDataMap;
	}

	public Map<String, RepositoryTaskData> getUnsubmittedTaskData() {
		return unsubmittedTaskData;
	}

	public Map<String, Set<RepositoryTaskAttribute>> getLocalEdits() {
		return localEdits;
	}
}