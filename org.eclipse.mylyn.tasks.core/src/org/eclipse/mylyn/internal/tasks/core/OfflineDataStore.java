/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.tasks.core.RepositoryTaskData;

/**
 * This object holds RepositoryTaskData and is serialized to disk for offline
 * storage.
 * 
 * @author Rob Elves
 */
class OfflineDataStore implements Serializable {

	private static final long serialVersionUID = -3909672088254980426L;

	/** Last new repository task id */
	private int lastNewRepositoryTaskId = 0;

	/** Older version of Task Data */
	private Map<String, RepositoryTaskData> oldTaskDataMap = new HashMap<String, RepositoryTaskData>();

	/** Newest version of the task data */
	private Map<String, RepositoryTaskData> newTaskDataMap = new HashMap<String, RepositoryTaskData>();

	/** New unsubmitted repository task data */
	private Map<String, RepositoryTaskData> unsubmittedTaskData = new HashMap<String, RepositoryTaskData>();

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
}