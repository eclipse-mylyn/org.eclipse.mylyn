/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.tasks.core.RepositoryTaskData;

/**
 * This object holds RepositoryTaskData and is serialized to disk for offline
 * storage.
 * 
 * @author Rob Elves
 */
public class OfflineDataStore implements Serializable {
	//private static final long serialVersionUID = -5422143577956784434L;

	private static final long serialVersionUID = 7629760132110781338L;

	/** The bug id of the most recently created offline report. */
	private Integer lastLocalTaskNumber = new Integer(0);
	
	/** The bug id of the most recently created offline report. */
	private Integer lastUnsubmittedLocalTaskNumber = new Integer(0);

	private Map<String, RepositoryTaskData> backingUnsubmittedTaskData = new HashMap<String, RepositoryTaskData>();

	private Map<String, RepositoryTaskData> backingOldTaskData = new HashMap<String, RepositoryTaskData>();

	private Map<String, RepositoryTaskData> backingNewTaskData = new HashMap<String, RepositoryTaskData>();

	/** Older version of Task Data */
	private transient Map<String, RepositoryTaskData> oldTaskDataMap;

	/** Newest version of the task data */
	private transient Map<String, RepositoryTaskData> newTaskDataMap;

	/** Unsubmitted tasks data*/
	private transient Map<String, RepositoryTaskData> unsubmittedTaskData;

	public void setNextTaskNumber(int lastNumber) {
		lastLocalTaskNumber = new Integer(lastNumber);
	}

	public int getNextTaskNumber() {
		lastLocalTaskNumber = new Integer(lastLocalTaskNumber.intValue() + 1);
		return lastLocalTaskNumber.intValue();
	}
	
	public int getNextUnsubmittedTaskNumber() {
		lastUnsubmittedLocalTaskNumber = new Integer(lastUnsubmittedLocalTaskNumber.intValue() + 1);
		return lastUnsubmittedLocalTaskNumber.intValue();
	}

	public synchronized Map<String, RepositoryTaskData> getOldDataMap() {
		if (oldTaskDataMap == null) {
			oldTaskDataMap = Collections.synchronizedMap(backingOldTaskData);
		}
		return oldTaskDataMap;
	}

	public synchronized Map<String, RepositoryTaskData> getNewDataMap() {
		if (newTaskDataMap == null) {
			newTaskDataMap = Collections.synchronizedMap(backingNewTaskData);
		}
		return newTaskDataMap;
	}

	public synchronized Map<String, RepositoryTaskData> getUnsubmittedTaskData() {
		if (unsubmittedTaskData == null) {
			unsubmittedTaskData = Collections.synchronizedMap(backingUnsubmittedTaskData);
		}
		return unsubmittedTaskData;
	}

}