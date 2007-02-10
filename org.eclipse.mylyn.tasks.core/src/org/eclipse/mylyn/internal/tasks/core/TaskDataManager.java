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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
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
	private Map<String, Set<RepositoryTaskAttribute>> localChangesMap;

	/** Older version of Task Data */
	private Map<String, RepositoryTaskData> oldTaskDataMap;

	/** Newest version of the task data */
	private Map<String, RepositoryTaskData> newTaskDataMap;

	/** Unsubmitted tasks data */
	private Map<String, RepositoryTaskData> unsubmittedTaskData;

	private TaskRepositoryManager taskRepositoryManager;

	public TaskDataManager(TaskRepositoryManager taskRepositoryManager, File file, boolean read) throws IOException,
			ClassNotFoundException {
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

	private Map<String, Set<RepositoryTaskAttribute>> getLocalChangesMap() {
		if (localChangesMap == null) {
			localChangesMap = Collections.synchronizedMap(dataStore.getLocalEdits());
		}
		return localChangesMap;
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
	public void push(String taskHandle, RepositoryTaskData newEntry) {
//		String handle = AbstractRepositoryTask.getHandle(newEntry.getRepositoryUrl(), newEntry.getId());
		RepositoryTaskData moveToOld = getNewDataMap().get(taskHandle);
		synchronized (file) {
			if (moveToOld != null) {
				getOldDataMap().put(taskHandle, moveToOld);
			} else {
				getOldDataMap().put(taskHandle, newEntry);
			}
			getNewDataMap().put(taskHandle, newEntry);
		}
	}

	/**
	 * Replace the recent (new) data with this copy
	 * 
	 * @param newData
	 */
	public void replace(String handle, RepositoryTaskData newData) {
//		String handle = AbstractRepositoryTask.getHandle(newData.getRepositoryUrl(), newData.getId());
		synchronized (file) {
			getNewDataMap().put(handle, newData);
		}
	}

	/**
	 * Add an unsubmitted RepositoryTaskData to the offline reports file.
	 */
	public void putUnsubmitted(String handle, RepositoryTaskData newEntry) {
//		String handle = AbstractRepositoryTask.getHandle(newEntry.getRepositoryUrl(), newEntry.getId());
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
	 * @return Get the next available temporary taskId. This taskId is given to new
	 *         unsubmitted repository tasks. Incremented each time this method
	 *         is called.
	 */
	public String getNewRepositoryTaskId() {
		return "" + dataStore.getNextTaskId();
	}

	private Set<RepositoryTaskAttribute> getLocalChanges(String handle) {
		Set<RepositoryTaskAttribute> localChanges;
		synchronized (file) {
			localChanges = getLocalChangesMap().get(handle);
			if (localChanges != null) {
				return Collections.unmodifiableSet(localChanges);
			}
		}
		return Collections.emptySet();
	}

	/**
	 * @return editable copy of task data with any edits applied
	 */
	public RepositoryTaskData getEditableCopy(String handle) {
		RepositoryTaskData data = getRepositoryTaskData(handle);
		RepositoryTaskData clone;
		try {
			clone = (RepositoryTaskData) ObjectCloner.deepCopy(data);
			updateAttributeFactory(clone);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Error constructing modifiable task", false);
			return null;
		}
		for (RepositoryTaskAttribute attribute : getLocalChanges(handle)) {
			if (attribute == null)
				continue;
			RepositoryTaskAttribute existing = clone.getAttribute(attribute.getID());
			if (existing != null) {
				existing.clearValues();
				List<String> options = existing.getOptions();

				for (String value : attribute.getValues()) {
					if (options.size() > 0) {
						if (options.contains(value)) {
							existing.addValue(value);
						}
					} else {
						existing.addValue(value);
					}
				}

			} else {
				clone.addAttribute(attribute.getID(), attribute);
			}

		}

		return clone;

	}

//	/**
//	 * @return editable copy of task data with any edits applied
//	 */
//	public RepositoryTaskData getEditableCopy(String handle) {
//		String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
//		return getEditableCopy(handle);
//	}

	public void saveEdits(String handle, Set<RepositoryTaskAttribute> attributes) {
		synchronized (file) {
			Set<RepositoryTaskAttribute> edits = getLocalChangesMap().get(handle);
			if (edits == null) {
				edits = new HashSet<RepositoryTaskAttribute>();
				edits.addAll(attributes);
				getLocalChangesMap().put(handle, edits);
			} else {
				edits.removeAll(attributes);
				edits.addAll(attributes);
			}
		}
	}

	public Set<RepositoryTaskAttribute> getEdits(String handle) {
		Set<RepositoryTaskAttribute> changes = getLocalChangesMap().get(handle);
		if (changes == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(changes);
		}

	}

	public void discardEdits(String handle) {
		synchronized (file) {
			getLocalChangesMap().remove(handle);
		}
	}

	/**
	 * Returns the most recent copy of the task data.
	 * 
	 * @return offline task data, null if no data found
	 */
	public RepositoryTaskData getRepositoryTaskData(String handle) {
		RepositoryTaskData data = getNewDataMap().get(handle);
		if (data == null) {
			data = getOldRepositoryTaskData(handle);
		}
		return data;
	}

//	/**
//	 * Returns the most recent copy of the task data.
//	 * 
//	 * @return offline task data, null if no data found
//	 */
//	public RepositoryTaskData getRepsitoryTaskData(String repositoryUrl, String taskId) {
//		String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
//		return getRepositoryTaskData(handle);
//	}

	/**
	 * Returns the old copy if exists, null otherwise.
	 */
	public RepositoryTaskData getOldRepositoryTaskData(String handle) {
		return getOldDataMap().get(handle);
	}

//	/**
//	 * Returns the old copy if exists, null otherwise.
//	 */
//	public RepositoryTaskData getOldRepositoryTaskData(String repositoryUrl, String taskId) {
//		String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
//		return getOldRepositoryTaskData(handle);
//	}

	/**
	 * Remove some bugs from the offline reports list
	 * 
	 * @param indicesToRemove
	 *            An array of the indicies of the bugs to be removed
	 */
	public void remove(List<String> handlesToRemove) {
		synchronized (file) {
			for (String handle: handlesToRemove) {
				remove(handle);
			}
		}
	}

	public void remove(String handle) {
		synchronized (file) {
			getNewDataMap().remove(handle);
			getOldDataMap().remove(handle);
		}
	}

	/**
	 * force a reset of all data maps
	 */
	public void clear() {
		synchronized (file) {
			dataStore = new OfflineDataStore();
			oldTaskDataMap = null;
			newTaskDataMap = null;
			unsubmittedTaskData = null;
			localChangesMap = null;
		}
	}

	/**
	 * After deserialization process the attributeFactory needs to be reset on
	 * each RepositoryTaskData.
	 */
	private void updateAttributeFactory(RepositoryTaskData taskData) {
		if (taskData == null)
			return;
		AbstractRepositoryConnector connector = taskRepositoryManager.getRepositoryConnector(taskData
				.getRepositoryKind());
		if (connector != null && connector.getTaskDataHandler() != null) {
			AbstractAttributeFactory factory = connector.getTaskDataHandler().getAttributeFactory();
			if (factory != null) {
				taskData.setAttributeFactory(factory);
			}
		}
	}

	/**
	 * Make both new and old the same so that no deltas will be revealed.
	 */
	public void clearIncoming(String handle) {
		RepositoryTaskData newData = getNewDataMap().get(handle);
		if (newData != null) {
			synchronized (file) {
				getOldDataMap().put(handle, newData);
			}
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
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						MylarStatusHandler.fail(e, "Could not close stream", false);
					}
				}
			}
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

	// HACK: until we get proper offline storage....
	// Reference:
	// http://www.javaworld.com/javaworld/javatips/jw-javatip76.html?page=2
	public static class ObjectCloner {

		private ObjectCloner() {
			// can not instantiate
		}

		static public Object deepCopy(Object oldObj) throws Exception {
			ObjectOutputStream outputStream = null;
			ObjectInputStream inputStream = null;
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				outputStream = new ObjectOutputStream(byteArrayOutputStream);

				outputStream.writeObject(oldObj);
				outputStream.flush();
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream
						.toByteArray());
				inputStream = new ObjectInputStream(byteArrayInputStream);
				return inputStream.readObject();
			} catch (Exception e) {
				throw (e);
			} finally {
				outputStream.close();
				inputStream.close();
			}
		}

	}

}
