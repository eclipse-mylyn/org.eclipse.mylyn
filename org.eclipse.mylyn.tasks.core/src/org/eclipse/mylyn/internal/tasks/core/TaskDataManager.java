/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasks.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;

/**
 * Manager for persisting RepositoryTaskData offline
 * 
 * @author Rob Elves
 */
public class TaskDataManager {

	private File file;

	private OfflineDataStore dataStore;

	/** Older version of Task Data */
	private Map<String, RepositoryTaskData> oldTaskDataMap;

	/** Newest version of the task data */
	private Map<String, RepositoryTaskData> newTaskDataMap;

	/** Unsubmitted tasks data */
	private Map<String, RepositoryTaskData> unsubmittedTaskData;

	private TaskRepositoryManager taskRepositoryManager;
	
	public TaskDataManager(TaskRepositoryManager taskRepositoryManager, File file, boolean read) throws IOException, ClassNotFoundException {
		this.taskRepositoryManager = taskRepositoryManager;
		this.file = file;
		if (file.exists() && read) {
			readOfflineData();
		} else {
			dataStore = new OfflineDataStore();
		}
	}

	private Map<String, RepositoryTaskData> getOldDataMap() {
		if (oldTaskDataMap == null) {
			oldTaskDataMap = Collections.synchronizedMap(dataStore.getOldDataMap());
		}
		return oldTaskDataMap;
	}

	private synchronized Map<String, RepositoryTaskData> getNewDataMap() {
		if (newTaskDataMap == null) {
			newTaskDataMap = Collections.synchronizedMap(dataStore.getNewDataMap());
		}
		return newTaskDataMap;
	}

	private synchronized Map<String, RepositoryTaskData> getUnsubmittedTaskData() {
		if (unsubmittedTaskData == null) {
			unsubmittedTaskData = Collections.synchronizedMap(dataStore.getUnsubmittedTaskData());
		}
		return unsubmittedTaskData;
	}

	/**
	 * Add a RepositoryTaskData to the offline reports file. Previously stored
	 * taskData is held and can be retrieved via getOldTaskData()
	 */
	public void put(RepositoryTaskData newEntry) {
		synchronized (file) {
			String handle = AbstractRepositoryTask.getHandle(newEntry.getRepositoryUrl(), newEntry.getId());
			RepositoryTaskData moveToOld = getNewDataMap().get(handle);
			if (moveToOld != null) {
				getOldDataMap().put(handle, moveToOld);
			}
			getNewDataMap().put(handle, newEntry);
		}
	}

	/**
	 * Add an unsubmitted RepositoryTaskData to the offline reports file.
	 */
	public void putUnsubmitted(RepositoryTaskData newEntry) {
		String handle = AbstractRepositoryTask.getHandle(newEntry.getRepositoryUrl(), newEntry.getId());
		synchronized (file) {
			getUnsubmittedTaskData().put(handle, newEntry);
		}
	}

	public Map<String, RepositoryTaskData> getUnsubmitted() {
		return Collections.unmodifiableMap(getUnsubmittedTaskData());
	}

	public void removeUnsubmitted(String handle) {
		synchronized (file) {
			getUnsubmittedTaskData().remove(handle);
		}
	}

	public void clearUnsubmitted() {
		synchronized (file) {
			getUnsubmittedTaskData().clear();
		}
	}

	/**
	 * @return Get the next available temporary id. This id is given to new
	 *         unsubmitted repository tasks. Incremented each time this method
	 *         is called.
	 */
	public String getNewRepositoryTaskId() {
		return "" + dataStore.getNextTaskId();
	}

	/**
	 * Returns the most recent copy of the task data.
	 */
	public RepositoryTaskData getTaskData(String handle) {
		RepositoryTaskData data = getNewDataMap().get(handle);
		if (data == null) {
			data = getOldTaskData(handle);
			if (data != null) {
				getNewDataMap().put(handle, data);
			}
		}
		return data;
	}

	/**
	 * Returns the most recent copy of the task data.
	 */
	public RepositoryTaskData getTaskData(String repositoryUrl, String taskId) {
		String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
		return getTaskData(handle);
	}

	/**
	 * Returns the old copy if exists, null otherwise.
	 */
	public RepositoryTaskData getOldTaskData(String handle) {
		return getOldDataMap().get(handle);
	}

	/**
	 * Returns the old copy if exists, null otherwise.
	 */
	public RepositoryTaskData getOldTaskData(String repositoryUrl, String taskId) {
		String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
		return getOldTaskData(handle);
	}

	/**
	 * Remove some bugs from the offline reports list
	 * 
	 * @param indicesToRemove
	 *            An array of the indicies of the bugs to be removed
	 */
	public void remove(List<RepositoryTaskData> dataToRemove) {
		synchronized (file) {
			for (RepositoryTaskData repositoryTaskData : dataToRemove) {
				remove(repositoryTaskData);
			}
		}
	}

	public void remove(RepositoryTaskData taskData) {
		synchronized (file) {
			String handle = AbstractRepositoryTask.getHandle(taskData.getRepositoryUrl(), taskData.getId());
			getNewDataMap().remove(handle);
			getOldDataMap().remove(handle);
		}
	}

	/**
	 * Public for testing
	 */
	public void clear() {
		synchronized (file) {
			dataStore = new OfflineDataStore();
			oldTaskDataMap = null;
			newTaskDataMap = null;
			unsubmittedTaskData = null;
		}
	}

	/**
	 * Public for testing
	 */
	public void readOfflineData() throws IOException, ClassNotFoundException {
		clear();
		synchronized (file) {
			ObjectInputStream in = null;
			try {
				in = new ObjectInputStream(new FileInputStream(file));
				dataStore = (OfflineDataStore) in.readObject();
				for (RepositoryTaskData taskData : getNewDataMap().values()) {
					updateAttributeFactory(taskData);
				}
				for (RepositoryTaskData taskData : getOldDataMap().values()) {
					updateAttributeFactory(taskData);
				}
			} catch (OptionalDataException e) {
				in.close();
				readOldOfflineFile();
			} catch (ClassCastException e) {
				in.close();
				readOldOfflineFile();
			} finally {
				in.close();
			}
		}
	}

	/**
	 * Migrate from old offline task data format (pre 1.0)
	 */
	private void readOldOfflineFile() throws IOException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			// read in each of the offline reports in the file

			dataStore = new OfflineDataStore();

			// get the number of offline reports in the file
			int size = in.readInt();
			dataStore.setLastNewTaskId(in.readInt());
			for (int nX = 0; nX < size; nX++) {
				RepositoryTaskData taskData = null;

				taskData = (RepositoryTaskData) in.readObject();

				if (taskData != null) {
					updateAttributeFactory(taskData);
					put(taskData);
				}
			}
		} catch (Exception ex) {
			dataStore = new OfflineDataStore();
			MylarStatusHandler.log(ex, "Could not migrate old offline data file, created new.");
		} finally {
			in.close();
		}
	}

	/** save task data to offline file */
	public void save() {
		synchronized (file) {

			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(new FileOutputStream(file));
				out.writeObject(dataStore);
				out.close();
			} catch (IOException e) {
				MylarStatusHandler.fail(e, "Could not write to offline reports file.", false);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

	private void updateAttributeFactory(RepositoryTaskData taskData) {
		AbstractRepositoryConnector connector = taskRepositoryManager.getRepositoryConnector(
				taskData.getRepositoryKind());
//		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
//				taskData.getRepositoryKind());
		if (connector != null && connector.getTaskDataHandler() != null) {
			AbstractAttributeFactory factory = connector.getTaskDataHandler().getAttributeFactory();
			if (factory != null) {
				taskData.setAttributeFactory(factory);
			}
		}
	}

}
