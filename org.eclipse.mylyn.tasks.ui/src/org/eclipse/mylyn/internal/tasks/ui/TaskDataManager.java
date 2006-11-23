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
package org.eclipse.mylar.internal.tasks.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Manager for persisting RepositoryTaskData
 * 
 * @author Rob Elves
 */
public class TaskDataManager {

	private File file;

	private OfflineDataStore dataStore;

	public TaskDataManager(File file, boolean read) throws IOException, ClassNotFoundException {
		this.file = file;
		if (file.exists() && read) {
			readFile();
		} else {
			dataStore = new OfflineDataStore();
		}
	}

	/**
	 * Add a RepositoryTaskData to the offline reports file. Previously stored
	 * taskData is held and can be retrieved via getOldTaskData()
	 */
	public void put(RepositoryTaskData newEntry) {
		synchronized (file) {
			String handle = AbstractRepositoryTask.getHandle(newEntry.getRepositoryUrl(), newEntry.getId());
			RepositoryTaskData moveToOld = dataStore.getNewDataMap().get(handle);
			if (moveToOld != null) {
				dataStore.getOldDataMap().put(handle, moveToOld);
			}
			dataStore.getNewDataMap().put(handle, newEntry);
		}
	}

	/**
	 * Add a unsubmitted RepositoryTaskData to the offline reports file.
	 */
	public void putUnsubmitted(RepositoryTaskData newEntry) {
		String handle = AbstractRepositoryTask.getHandle(newEntry.getRepositoryUrl(), newEntry.getId());
		synchronized (file) {
			dataStore.getUnsubmittedTaskData().put(handle, newEntry);
		}
	}

	public Map<String, RepositoryTaskData> getUnsubmitted() {
		return dataStore.getUnsubmittedTaskData();
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

	/**
	 * @return The id that the next new local task should use. Incremented each
	 *         time this method is called.
	 */
	public String getNextLocalTaskId() {
		return "" + dataStore.getNextTaskNumber();
	}

	/**
	 * @return The id of the next new unsubmitted task. Incremented each time
	 *         this method is called.
	 */
	public String getNextUnsubmittedTaskId() {
		return "" + dataStore.getNextUnsubmittedTaskNumber();
	}

	/**
	 * Returns the most recent copy of the task data.
	 */
	public RepositoryTaskData getTaskData(String handle) {
		RepositoryTaskData data = dataStore.getNewDataMap().get(handle);
		if (data == null) {
			data = getOldTaskData(handle);
			if (data != null) {
				dataStore.getNewDataMap().put(handle, data);
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
		return dataStore.getOldDataMap().get(handle);
	}

	/**
	 * Returns the old copy if exists, null otherwise.
	 */
	public RepositoryTaskData getOldTaskData(String repositoryUrl, String taskId) {
		String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
		return getOldTaskData(handle);
	}

	/**
	 * Read the offline reports in from the file on disk
	 * 
	 * @throws IOException
	 *             Error opening or closing the offline reports file
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 *             Error deserializing objects from the offline reports file
	 */
	private void readFile() throws IOException, ClassNotFoundException {

		synchronized (file) {
			ObjectInputStream in = null;
			try {
				in = new ObjectInputStream(new FileInputStream(file));
				dataStore = (OfflineDataStore) in.readObject();
				for (RepositoryTaskData taskData : dataStore.getNewDataMap().values()) {
					updateAttributeFactory(taskData);
				}
				for (RepositoryTaskData taskData : dataStore.getOldDataMap().values()) {
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
			dataStore.setNextTaskNumber(in.readInt());
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
			MylarStatusHandler.fail(ex, "Migration of offline task data failed.", false);
		} finally {
			in.close();
		}
	}

	private void updateAttributeFactory(RepositoryTaskData taskData) {
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				taskData.getRepositoryKind());
		if (connector != null && connector.getOfflineTaskHandler() != null) {
			AbstractAttributeFactory factory = connector.getOfflineTaskHandler().getAttributeFactory();
			if (factory != null) {
				taskData.setAttributeFactory(factory);
			}
		}
	}

	/**
	 * Remove some bugs from the offline reports list
	 * 
	 * @param indicesToRemove
	 *            An array of the indicies of the bugs to be removed
	 */
	public void remove(List<RepositoryTaskData> dataToRemove) {
		for (RepositoryTaskData repositoryTaskData : dataToRemove) {
			remove(repositoryTaskData);
		}
	}

	public void remove(RepositoryTaskData taskData) {
		String handle = AbstractRepositoryTask.getHandle(taskData.getRepositoryUrl(), taskData.getId());
		dataStore.getNewDataMap().remove(handle);
		dataStore.getOldDataMap().remove(handle);
	}

	/**
	 * FOR TESTING ONLY.
	 */
	public void clear() {
		dataStore = new OfflineDataStore();
	}

	/**
	 * FOR TESTING ONLY.
	 */
	public void reloadFromFile() throws IOException, ClassNotFoundException {
		readFile();
	}

}
