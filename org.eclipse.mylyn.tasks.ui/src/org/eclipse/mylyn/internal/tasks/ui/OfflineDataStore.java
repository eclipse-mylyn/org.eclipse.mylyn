/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
class OfflineDataStore implements Serializable {

	private static final long serialVersionUID = 7629760132110781338L;

	/** The bug id of the most recently created offline report. */
	private transient int lastLocalTaskNumber = 0;

	/** The bug id of the most recently created offline report. */
	private transient int lastUnsubmittedTaskNumber = 0;

	private transient Map<String, RepositoryTaskData> backingUnsubmittedTaskData;

	private transient Map<String, RepositoryTaskData> backingOldTaskData;

	private transient Map<String, RepositoryTaskData> backingNewTaskData;

	/** Older version of Task Data */
	private transient Map<String, RepositoryTaskData> oldTaskDataMap;

	/** Newest version of the task data */
	private transient Map<String, RepositoryTaskData> newTaskDataMap;

	/** Unsubmitted tasks data */
	private transient Map<String, RepositoryTaskData> unsubmittedTaskData;

	public void setNextTaskNumber(int lastNumber) {
		lastLocalTaskNumber = new Integer(lastNumber);
	}

	public int getNextTaskNumber() {
		lastLocalTaskNumber++;
		return lastLocalTaskNumber;
	}

	public int getNextUnsubmittedTaskNumber() {
		lastUnsubmittedTaskNumber++;
		return lastUnsubmittedTaskNumber;
	}

	public synchronized Map<String, RepositoryTaskData> getOldDataMap() {
		if (oldTaskDataMap == null) {
			if (backingOldTaskData == null) {
				backingOldTaskData = new HashMap<String, RepositoryTaskData>();
			}
			oldTaskDataMap = Collections.synchronizedMap(backingOldTaskData);
		}
		return oldTaskDataMap;
	}

	public synchronized Map<String, RepositoryTaskData> getNewDataMap() {
		if (newTaskDataMap == null) {
			if (backingNewTaskData == null) {
				backingNewTaskData = new HashMap<String, RepositoryTaskData>();
			}
			newTaskDataMap = Collections.synchronizedMap(backingNewTaskData);
		}
		return newTaskDataMap;
	}

	public synchronized Map<String, RepositoryTaskData> getUnsubmittedTaskData() {
		if (unsubmittedTaskData == null) {
			if (backingUnsubmittedTaskData == null) {
				backingUnsubmittedTaskData = new HashMap<String, RepositoryTaskData>();
			}
			unsubmittedTaskData = Collections.synchronizedMap(backingUnsubmittedTaskData);
		}
		return unsubmittedTaskData;
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		s.writeInt(lastLocalTaskNumber);
		s.writeInt(lastUnsubmittedTaskNumber);
		writeMap(s, getNewDataMap());
		writeMap(s, getOldDataMap());
		writeMap(s, getUnsubmittedTaskData());
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		lastLocalTaskNumber = s.readInt();
		lastUnsubmittedTaskNumber = s.readInt();
		readMap(s, getNewDataMap());
		readMap(s, getOldDataMap());
		readMap(s, getUnsubmittedTaskData());
	}

	private void writeMap(ObjectOutputStream s, Map<String, RepositoryTaskData> map) throws IOException {
		s.writeInt(map.size());
		for (String key : map.keySet()) {
			s.writeObject(key);
			s.writeObject(map.get(key));
		}
	}

	private void readMap(ObjectInputStream s, Map<String, RepositoryTaskData> map) throws IOException,
			ClassNotFoundException {
		int size = s.readInt();
		for (int x = 0; x < size; x++) {
			String handle = (String) s.readObject();
			RepositoryTaskData data = (RepositoryTaskData) s.readObject();
			map.put(handle, data);
		}
	}
}