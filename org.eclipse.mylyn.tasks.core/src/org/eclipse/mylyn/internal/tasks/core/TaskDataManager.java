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
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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

	private static final int SAVE_INTERVAL = 30 * 1000;

	private File file;

	private OfflineDataStore dataStore;

	private TaskRepositoryManager taskRepositoryManager;

	private Job saveJob;

	private Timer saveTimer;

	private boolean saveRequested = false;

	public TaskDataManager(TaskRepositoryManager taskRepositoryManager, File file, boolean read) throws IOException,
			ClassNotFoundException {
		this.taskRepositoryManager = taskRepositoryManager;
		this.file = file;
		if (file.exists() && read) {
			readOfflineData();
		} else {
			dataStore = new OfflineDataStore();
		}

		saveTimer = new Timer();
		saveTimer.schedule(new CheckSaveRequired(), SAVE_INTERVAL, SAVE_INTERVAL);
	}

	/**
	 * Add a RepositoryTaskData to the offline reports file. Previously stored
	 * taskData is held and can be retrieved via getOldTaskData()
	 */
	public void setNewTaskData(String taskHandle, RepositoryTaskData newEntry) {
		if (taskHandle == null || newEntry == null) {
			return;
		}
		dataStore.getNewDataMap().put(taskHandle, newEntry);
		dataStateChanged();
	}

	public void setOldTaskData(String taskHandle, RepositoryTaskData oldEntry) {
		if (taskHandle == null || oldEntry == null) {
			return;
		}
		dataStore.getOldDataMap().put(taskHandle, oldEntry);
		dataStateChanged();
	}

	/**
	 * Returns the most recent copy of the task data.
	 * 
	 * @return offline task data, null if no data found
	 */
	public RepositoryTaskData getNewTaskData(String handle) {
		RepositoryTaskData data = dataStore.getNewDataMap().get(handle);
		return data;
	}

	/**
	 * Returns the old copy if exists, null otherwise.
	 */
	public RepositoryTaskData getOldTaskData(String handle) {
		return dataStore.getOldDataMap().get(handle);
	}

	public Map<String, RepositoryTaskData> getUnsubmitted() {
		return Collections.unmodifiableMap(dataStore.getUnsubmittedTaskData());
	}

	public void removeUnsubmitted(String handle) {
		dataStore.getUnsubmittedTaskData().remove(handle);
		dataStateChanged();
	}

	/**
	 * @return Get the next available temporary taskId. This taskId is given to
	 *         new unsubmitted repository tasks. Incremented each time this
	 *         method is called.
	 */
	public synchronized String getNewRepositoryTaskId() {
		dataStateChanged();
		return "" + dataStore.getNextTaskId();
	}

	private Set<RepositoryTaskAttribute> getLocalChanges(String handle) {
		Set<RepositoryTaskAttribute> localChanges;
		localChanges = dataStore.getLocalEdits().get(handle);
		if (localChanges != null) {
			return Collections.unmodifiableSet(localChanges);
		}
		return Collections.emptySet();
	}

	/**
	 * @return editable copy of task data with any edits applied
	 */
	public RepositoryTaskData getEditableCopy(String handle) {
		RepositoryTaskData data = getNewTaskData(handle);
		RepositoryTaskData clone = null;
		if (data != null) {
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
		}

		return clone;

	}

	public void saveEdits(String handle, Set<RepositoryTaskAttribute> attributes) {
		synchronized (file) {
			Set<RepositoryTaskAttribute> edits = dataStore.getLocalEdits().get(handle);
			if (edits == null) {
				edits = new HashSet<RepositoryTaskAttribute>();
				edits.addAll(attributes);
				dataStore.getLocalEdits().put(handle, edits);
			} else {
				edits.removeAll(attributes);
				edits.addAll(attributes);
			}
		}
		dataStateChanged();
	}

	public Set<RepositoryTaskAttribute> getEdits(String handle) {
		Set<RepositoryTaskAttribute> changes = dataStore.getLocalEdits().get(handle);
		if (changes == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(changes);
		}

	}

	public void discardEdits(String handle) {
		dataStore.getLocalEdits().remove(handle);
		dataStateChanged();
	}

	/**
	 * Remove some bugs from the offline reports list
	 * 
	 * @param indicesToRemove
	 *            An array of the indicies of the bugs to be removed
	 */
	public void remove(List<String> handlesToRemove) {
		for (String handle : handlesToRemove) {
			remove(handle);
		}
	}

	public void remove(String handle) {
		dataStore.getNewDataMap().remove(handle);
		dataStore.getOldDataMap().remove(handle);
		discardEdits(handle);
	}

	/**
	 * force a reset of all data maps
	 */
	public void clear() {
		synchronized (file) {
			dataStore = new OfflineDataStore();
			saveRequested = false;
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
			AbstractAttributeFactory factory = connector.getTaskDataHandler().getAttributeFactory(
					taskData.getRepositoryUrl(), taskData.getRepositoryKind(), taskData.getTaskKind());
			if (factory != null) {
				taskData.setAttributeFactory(factory);
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
				for (RepositoryTaskData taskData : dataStore.getNewDataMap().values()) {
					updateAttributeFactory(taskData);
				}
				for (RepositoryTaskData taskData : dataStore.getOldDataMap().values()) {
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

	private void dataStateChanged() {
		synchronized (saveTimer) {
			saveRequested = true;
		}
	}

	/**
	 * PUBLIC FOR TESTING ONLY Launch a save job Saving is managed by
	 * TaskDataManager
	 */
	public void save(boolean force) {

		if (!force) {
			saveJob = new TaskDataSaveJob();
			saveJob.setPriority(Job.LONG);
			saveJob.setSystem(true);
			saveJob.schedule();
		} else {
			new TaskDataSaveJob().run(new NullProgressMonitor());
		}
	}

	private class TaskDataSaveJob extends Job {

		public TaskDataSaveJob() {
			super("Saving task data");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			synchronized (file) {
				if (Platform.isRunning()) {
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
			return Status.OK_STATUS;
		}

	};

	class CheckSaveRequired extends TimerTask {

		@Override
		public void run() {
			if (!Platform.isRunning()) {
				return;
			} else {
				synchronized (saveTimer) {
					if (saveRequested) {
						save(false);
						saveRequested = false;
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

	public boolean hasOutgoing(String handleIdentifier) {
		return getLocalChanges(handleIdentifier).size() > 0;
	}
}
