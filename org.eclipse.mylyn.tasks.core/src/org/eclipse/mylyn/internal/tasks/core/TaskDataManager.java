/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;

/**
 * Manager for persisting RepositoryTaskData offline
 * 
 * @author Rob Elves
 */
public class TaskDataManager {

	private final TaskRepositoryManager taskRepositoryManager;

	private final ITaskDataStorage storage;

	private int nextNewId = 1;

	public TaskDataManager(TaskRepositoryManager taskRepositoryManager, ITaskDataStorage storage) {
		this.taskRepositoryManager = taskRepositoryManager;
		this.storage = storage;
	}

	/**
	 * Add a RepositoryTaskData to the offline reports file.
	 */
	public void setNewTaskData(RepositoryTaskData data) {
		if (data == null || data.getRepositoryUrl() == null || data.getId() == null) {
			return;
		}

		TaskDataState state = retrieveState(data);
		if (state != null) {
			state.setNewTaskData(data);
		} else {
			state = new TaskDataState(data.getRepositoryUrl(), data.getId());
			state.setNewTaskData(data);
		}
		saveState(state);
	}

	/**
	 * @since 2.3
	 */
	public void setNewTaskData(String repositoryUrl, String taskId, RepositoryTaskData data) {
		TaskDataState state = retrieveState(repositoryUrl, taskId);
		if (state != null) {
			state.setNewTaskData(data);
		} else {
			state = new TaskDataState(repositoryUrl, taskId);
			state.setNewTaskData(data);
		}
		saveState(state);
	}

	public void setOldTaskData(RepositoryTaskData data) {
		if (data == null || data.getRepositoryUrl() == null || data.getId() == null) {
			return;
		}
		TaskDataState state = retrieveState(data);
		if (state != null) {
			state.setOldTaskData(data);
		} else {
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
					"Attempt to save old data when no new data exists", new Exception()));
		}
		saveState(state);
	}

	/**
	 * Returns the most recent copy of the task data.
	 * 
	 * @return offline task data, null if no data found
	 */
	public RepositoryTaskData getNewTaskData(String repositoryUrl, String id) {
		if (repositoryUrl == null || id == null) {
			return null;
		}
		TaskDataState state = retrieveState(repositoryUrl, id);
		if (state != null) {
			return state.getNewTaskData();
		}
		return null;
	}

	/**
	 * Returns the old copy if exists, null otherwise.
	 */
	public RepositoryTaskData getOldTaskData(String repositoryUrl, String id) {
		if (repositoryUrl == null || id == null) {
			return null;
		}
		TaskDataState state = retrieveState(repositoryUrl, id);
		if (state != null) {
			return state.getOldTaskData();
		}
		return null;
	}

	/**
	 * @return Get the next available temporary taskId. This taskId is given to new unsubmitted repository tasks.
	 *         Incremented each time this method is called.
	 */
	public synchronized String getNewRepositoryTaskId() {
		// TODO: generate based on values of unsubmitted offline report ids
		nextNewId++;
		if (nextNewId == Integer.MAX_VALUE) {
			nextNewId = 1;
		}
		return "" + nextNewId;
	}

	/**
	 * 
	 * @return editable copy of task data with any edits applied
	 */
	public RepositoryTaskData getEditableCopy(String repositoryUrl, String id) {
		if (repositoryUrl == null || id == null) {
			return null;
		}
		TaskDataState state = retrieveState(repositoryUrl, id);
		RepositoryTaskData clone = null;
		if (state != null) {
			if (state.getNewTaskData() != null) {
				try {
					clone = (RepositoryTaskData) ObjectCloner.deepCopy(state.getNewTaskData());
					updateAttributeFactory(clone);
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Error constructing modifiable task", e));
					return null;
				}
			}
			if (clone != null) {
				for (RepositoryTaskAttribute attribute : state.getEdits()) {
					if (attribute == null) {
						continue;
					}
					RepositoryTaskAttribute existing = clone.getAttribute(attribute.getId());
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
						clone.addAttribute(attribute.getId(), attribute);
					}
				}
			}
		}
		return clone;

	}

	// API 3.0 review: the state of the elements of changedAttribues could change between this call and the time state is written to disk, might need to make a full copy  
	public void saveEdits(String repositoryUrl, String id, Set<RepositoryTaskAttribute> changedAttributes) {
		TaskDataState state = retrieveState(repositoryUrl, id);
		if (state != null) {
			Set<RepositoryTaskAttribute> edits = state.getEdits();
			if (edits == null) {
				// Copy here?
				state.setEdits(changedAttributes);
			} else {
				edits.removeAll(changedAttributes);
				edits.addAll(changedAttributes);
			}
			try {
				storage.put(state);
			} catch (Exception e) {
				// FIXME what exception is caught here?
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error saving edits", e));
			}
		}

	}

	public Set<RepositoryTaskAttribute> getEdits(String repositoryUrl, String id) {
		if (repositoryUrl == null || id == null) {
			return Collections.emptySet();
		}
		TaskDataState state = retrieveState(repositoryUrl, id);
		if (state != null) {
			if (state.getEdits() != null) {
				return Collections.unmodifiableSet(state.getEdits());
			}
		}
		return Collections.emptySet();
	}

	public void discardEdits(String repositoryUrl, String id) {
		if (repositoryUrl == null || id == null) {
			return;
		}
		TaskDataState state = retrieveState(repositoryUrl, id);
		if (state != null) {
			state.discardEdits();
			try {
				storage.put(state);
			} catch (Exception e) {
				// FIXME what exception is caught here?
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error discarding edits", e));
			}
		}
	}

	public void remove(String repositoryUrl, String id) {
		if (repositoryUrl == null || id == null) {
			return;
		}
		storage.remove(repositoryUrl, id);
	}

	/**
	 * DESTROY ALL OFFLINE TASK DATA Public for testing only Forces a reset of all data maps Does not signal data
	 * changed (doesn't request save)
	 */
	public void clear() {
		nextNewId = 0;
		storage.clear();
	}

	/**
	 * After deserialization process the attributeFactory needs to be reset on each RepositoryTaskData.
	 */
	private void updateAttributeFactory(RepositoryTaskData taskData) {
		if (taskData == null) {
			return;
		}
		taskData.refresh();
		AbstractRepositoryConnector connector = taskRepositoryManager.getRepositoryConnector(taskData.getRepositoryKind());
		if (connector != null && connector.getTaskDataHandler() != null) {
			AbstractAttributeFactory factory = connector.getTaskDataHandler().getAttributeFactory(taskData);
			if (factory != null) {
				taskData.setAttributeFactory(factory);
			}
		}
	}

	// XXX: review if task data cloning can be done without using serialization
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
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
						byteArrayOutputStream.toByteArray());
				inputStream = new ObjectInputStream(byteArrayInputStream);
				return inputStream.readObject();
			} catch (Exception e) {
				throw (e);
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}

	}

	public void start() {
		try {
			storage.start();
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Offline storage start failed",
					e));
		}
	}

	public void stop() {
		try {
			storage.stop();
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Offline storage stop failed", e));
		}
	}

	public void saveNow() {
		storage.flush();
	}

	private TaskDataState retrieveState(RepositoryTaskData data) {
		return retrieveState(data.getRepositoryUrl(), data.getId());
	}

	private TaskDataState retrieveState(String repositoryUrl, String id) {
		TaskDataState state = null;
		try {
			state = storage.get(repositoryUrl, id);
			if (state != null) {
				// TODO: Get rid of attribute factory on containers!!!
				if (state.getNewTaskData() != null) {
					updateAttributeFactory(state.getNewTaskData());
				}
				if (state.getOldTaskData() != null) {
					updateAttributeFactory(state.getOldTaskData());
				}
			}
		} catch (Exception e) {
			// FIXME what Exception is caught here?
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error saving offline data", e));
		}
		return state;
	}

	private void saveState(TaskDataState state) {
		storage.put(state);
	}

}
