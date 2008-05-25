/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Encapsulates synchronization policy.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskDataManager implements ITaskDataManager {

	private static final String ENCODING_UTF_8 = "UTF-8";

	private static final String EXTENSION = ".zip";

	private static final String FOLDER_TASKS = "tasks";

	private static final String FOLDER_DATA = "offline";

	private static final String FOLDER_TASKS_1_0 = "offline";

	private String dataPath;

	private final ITaskRepositoryManager repositoryManager;

	@Deprecated
	private final TaskDataStorageManager taskDataStorageManager;

	private final TaskDataStore taskDataStore;

	private final TaskList taskList;

	public TaskDataManager(TaskDataStorageManager taskDataManager, TaskDataStore taskDataStore,
			ITaskRepositoryManager repositoryManager, TaskList taskList) {
		this.taskDataStorageManager = taskDataManager;
		this.taskDataStore = taskDataStore;
		this.repositoryManager = repositoryManager;
		this.taskList = taskList;
	}

	/** public for testing purposes */
	@Deprecated
	public boolean checkHasIncoming(ITask repositoryTask,
			org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData newData) {
		if (repositoryTask.getSynchronizationState() == SynchronizationState.INCOMING) {
			return true;
		}

		String lastModified = repositoryTask.getLastReadTimeStamp();
		org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute modifiedDateAttribute = newData.getAttribute(org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute.DATE_MODIFIED);
		if (lastModified != null && modifiedDateAttribute != null && modifiedDateAttribute.getValue() != null) {
			if (lastModified.trim().compareTo(modifiedDateAttribute.getValue().trim()) == 0) {
				// Only set to synchronized state if not in incoming state.
				// Case of incoming->sync handled by markRead upon opening
				// or a forced synchronization on the task only.
				return false;
			}

			Date modifiedDate = newData.getAttributeFactory().getDateForAttributeType(
					org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute.DATE_MODIFIED,
					modifiedDateAttribute.getValue());
			Date lastModifiedDate = newData.getAttributeFactory().getDateForAttributeType(
					org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute.DATE_MODIFIED,
					lastModified);
			if (modifiedDate != null && lastModifiedDate != null && modifiedDate.equals(lastModifiedDate)) {
				return false;
			}
		}

		return true;
	}

	public ITaskDataWorkingCopy createWorkingCopy(final ITask task, final TaskData taskData) {
		Assert.isNotNull(task);
		final TaskDataState state = new TaskDataState(taskData.getConnectorKind(), taskData.getRepositoryUrl(),
				taskData.getTaskId());
		state.setRepositoryData(taskData);
		state.setLastReadData(taskData);
		state.init(TaskDataManager.this, task);
		state.setSaved(false);
		state.revert();
		return state;
	}

	public ITaskDataWorkingCopy getWorkingCopy(final ITask itask) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		final TaskDataState[] result = new TaskDataState[1];
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				final File file = getMigratedFile(task, kind);
				final TaskDataState state = taskDataStore.getTaskDataState(file);
				if (state == null) {
					throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Task data at \""
							+ file + "\" not found"));
				}
				if (task.isMarkReadPending()) {
					state.setLastReadData(state.getRepositoryData());
				}
				state.init(TaskDataManager.this, task);
				state.revert();
				switch (task.getSynchronizationState()) {
				case INCOMING:
				case INCOMING_NEW:
					task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
					// XXX legacy support for showing correct synchronization decoration in task list
					task.setLastReadTimeStamp(new Date().toString());
					break;
				case CONFLICT:
					task.setSynchronizationState(SynchronizationState.OUTGOING);
					break;
				}
				task.setMarkReadPending(true);
				result[0] = state;
			}
		});
		taskList.notifyElementChanged(task);
		return result[0];
	}

	public void saveWorkingCopy(final ITask itask, final TaskDataState state) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				final File file = getFile(task, kind);
				taskDataStore.putTaskData(ensurePathExists(file), state);
				switch (task.getSynchronizationState()) {
				case SYNCHRONIZED:
					task.setSynchronizationState(SynchronizationState.OUTGOING);
				}
				taskList.addTask(task);
			}
		});
		taskList.notifyElementChanged(task);
	}

	public void putUpdatedTaskData(final ITask itask, final TaskData taskData, final boolean user) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		Assert.isNotNull(taskData);
		final AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
		final TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		final boolean changed = connector.hasChanged(task, taskData);
		if (changed || user) {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					boolean newTask = false;
					if (!taskData.isPartial()) {
						File file = getMigratedFile(task, task.getConnectorKind());
						newTask = !file.exists();
						taskDataStore.putTaskData(ensurePathExists(file), taskData, task.isMarkReadPending(), user);
						task.setMarkReadPending(false);
					}

					connector.updateTaskFromTaskData(repository, task, taskData);

					if (changed) {
						switch (task.getSynchronizationState()) {
						case OUTGOING:
							task.setSynchronizationState(SynchronizationState.CONFLICT);
							break;
						case SYNCHRONIZED:
							if (newTask) {
								// FIXME this won't work for tasks that have partial task data 
								task.setSynchronizationState(SynchronizationState.INCOMING_NEW);
								// XXX legacy support for showing correct synchronization decoration in task list
								task.setLastReadTimeStamp(null);
							} else {
								task.setSynchronizationState(SynchronizationState.INCOMING);
							}
							break;
						}
					}
					task.setStale(false);
					task.setSynchronizing(false);
				}
			});
			taskList.notifyElementChanged(task);
		} else {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					task.setStale(false);
					task.setSynchronizing(false);
				}
			});
			taskList.notifySynchronizationStateChanged(task);
		}
	}

	private File ensurePathExists(File file) {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}

	private File getMigratedFile(ITask task, String kind) throws CoreException {
		Assert.isNotNull(task);
		Assert.isNotNull(kind);
		File file = getFile(task, kind);
		if (!file.exists()) {
			File oldFile = getFile10(task, kind);
			if (oldFile.exists()) {
				TaskDataState state = taskDataStore.getTaskDataState(oldFile);
				// save migrated task data right away
				taskDataStore.putTaskData(ensurePathExists(file), state);
			}
		}
		return file;
	}

	public void discardEdits(final ITask itask) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				File dataFile = getFile(task, kind);
				if (dataFile.exists()) {
					taskDataStore.discardEdits(dataFile);
				}
				switch (task.getSynchronizationState()) {
				case OUTGOING:
					task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
					break;
				case CONFLICT:
					task.setSynchronizationState(SynchronizationState.INCOMING);
					break;
				}
			}
		});
		taskList.notifyElementChanged(task);
	}

	@Deprecated
	public void discardOutgoing(AbstractTask repositoryTask) {
		taskDataStorageManager.discardEdits(repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId());
		repositoryTask.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
		//taskList.notifyElementChanged(repositoryTask);
	}

	private File findFile(ITask task, String kind) {
		File file = getFile(task, kind);
		if (file.exists()) {
			return file;
		}
		return getFile10(task, kind);
	}

	public String getDataPath() {
		return dataPath;
	}

	private File getFile(ITask task, String kind) {
		try {
//			String pathName = task.getConnectorKind() + "-"
//					+ URLEncoder.encode(task.getRepositoryUrl(), ENCODING_UTF_8);
//			String fileName = kind + "-" + URLEncoder.encode(task.getTaskId(), ENCODING_UTF_8) + EXTENSION;
			String repositoryPath = task.getConnectorKind() + "-" + encode(task.getRepositoryUrl());
			String fileName = encode(task.getTaskId()) + EXTENSION;
			File path = new File(dataPath + File.separator + FOLDER_TASKS + File.separator + repositoryPath
					+ File.separator + FOLDER_DATA);
			return new File(path, fileName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String encode(String text) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer(text.length());
		char[] chars = text.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.') {
				sb.append(c);
			} else {
				sb.append("%" + Integer.toHexString(c).toUpperCase());
			}
		}
		return sb.toString();
	}

	private File getFile10(ITask task, String kind) {
		try {
			String pathName = URLEncoder.encode(task.getRepositoryUrl(), ENCODING_UTF_8);
			String fileName = task.getTaskId() + EXTENSION;
			File path = new File(dataPath + File.separator + FOLDER_TASKS_1_0, pathName);
			return new File(path, fileName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

	public TaskData getTaskData(ITask task) throws CoreException {
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		TaskDataState state = taskDataStore.getTaskDataState(findFile(task, kind));
		if (state == null) {
			return null;
		}
		return state.getRepositoryData();
	}

	public TaskData getTaskData(TaskRepository taskRepository, String taskId) throws CoreException {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(taskId);
		TaskDataState state = taskDataStore.getTaskDataState(findFile(new TaskTask(taskRepository.getConnectorKind(),
				taskRepository.getRepositoryUrl(), taskId), taskRepository.getConnectorKind()));
		if (state == null) {
			return null;
		}
		return state.getRepositoryData();
	}

	public boolean hasTaskData(ITask task) {
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		return getFile(task, kind).exists();
	}

	public void putSubmittedTaskData(final ITask itask, final TaskData taskData) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		Assert.isNotNull(taskData);
		final AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
		final TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				if (!taskData.isPartial()) {
					File file = getMigratedFile(task, task.getConnectorKind());
					taskDataStore.setTaskData(ensurePathExists(file), taskData);
					task.setMarkReadPending(false);
				}

				connector.updateTaskFromTaskData(repository, task, taskData);

				task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
				task.setStale(false);
				task.setSynchronizing(false);
				task.setSubmitting(false);
			}
		});
		taskList.notifySynchronizationStateChanged(task);
	}

	/**
	 * Saves incoming data and updates task sync state appropriately
	 * 
	 * @return true if call results in change of sync state
	 */
	@Deprecated
	public synchronized boolean saveIncoming(final ITask itask,
			final org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData newTaskData, boolean forceSync) {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(newTaskData);
		final SynchronizationState startState = task.getSynchronizationState();
		SynchronizationState status = task.getSynchronizationState();

		org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData previousTaskData = taskDataStorageManager.getNewTaskData(
				task.getRepositoryUrl(), task.getTaskId());

		if (task.isSubmitting()) {
			status = SynchronizationState.SYNCHRONIZED;
			task.setSubmitting(false);
			TaskDataStorageManager dataManager = taskDataStorageManager;
			dataManager.discardEdits(task.getRepositoryUrl(), task.getTaskId());

			taskDataStorageManager.setNewTaskData(newTaskData);
			/**
			 * If we set both so we don't see our own changes
			 * 
			 * @see RepositorySynchronizationManager.setTaskRead(AbstractTask, boolean)
			 */
			// taskDataManager.setOldTaskData(repositoryTask.getHandleIdentifier(),
			// newTaskData);
		} else {

			switch (status) {
			case OUTGOING:
				if (checkHasIncoming(task, newTaskData)) {
					status = SynchronizationState.CONFLICT;
				}
				taskDataStorageManager.setNewTaskData(newTaskData);
				break;

			case CONFLICT:
				// fall through to INCOMING (conflict implies incoming)
			case INCOMING:
				// only most recent incoming will be displayed if two
				// sequential incoming's /conflicts happen

				taskDataStorageManager.setNewTaskData(newTaskData);
				break;
			case SYNCHRONIZED:
				boolean hasIncoming = checkHasIncoming(task, newTaskData);
				if (hasIncoming) {
					status = SynchronizationState.INCOMING;
					task.setNotified(false);
				}
				if (hasIncoming || previousTaskData == null || forceSync) {
					taskDataStorageManager.setNewTaskData(newTaskData);
				}
				break;
			}
		}
		task.setSynchronizationState(status);
		return startState != task.getSynchronizationState();
	}

	@Deprecated
	public void saveOffline(ITask task, org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData taskData) {
		taskDataStorageManager.setNewTaskData(taskData);
	}

	/**
	 * @param repositoryTask
	 *            task that changed
	 * @param modifiedAttributes
	 *            attributes that have changed during edit session
	 */
	@Deprecated
	public synchronized void saveOutgoing(AbstractTask repositoryTask,
			Set<org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute> modifiedAttributes) {
		repositoryTask.setSynchronizationState(SynchronizationState.OUTGOING);
		taskDataStorageManager.saveEdits(repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId(),
				Collections.unmodifiableSet(modifiedAttributes));
		taskList.notifyElementChanged(repositoryTask);
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	/**
	 * @param task
	 *            repository task to mark as read or unread
	 * @param read
	 *            true to mark as read, false to mark as unread
	 */
	public void setTaskRead(final ITask itask, final boolean read) {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		// legacy support
		if (!getFile(task, task.getConnectorKind()).exists()) {
			setTaskReadDeprecated(task, read);
			return;
		}
		// current api
		try {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					if (read) {
						switch (task.getSynchronizationState()) {
						case INCOMING:
						case INCOMING_NEW:
							task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
							task.setMarkReadPending(true);
							break;
						case CONFLICT:
							task.setSynchronizationState(SynchronizationState.OUTGOING);
							task.setMarkReadPending(true);
							break;
						}
					} else {
						switch (task.getSynchronizationState()) {
						case SYNCHRONIZED:
							task.setSynchronizationState(SynchronizationState.INCOMING);
							task.setMarkReadPending(false);
							break;
						}
					}
				}
			});
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Unexpected error while marking task read", e));
		}
		taskList.notifyElementChanged(task);
	}

	@Deprecated
	private void setTaskReadDeprecated(ITask itask, boolean read) {
		final AbstractTask task = (AbstractTask) itask;
		org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData taskData = taskDataStorageManager.getNewTaskData(
				task.getRepositoryUrl(), task.getTaskId());
		if (read && task.getSynchronizationState().equals(SynchronizationState.INCOMING)) {
			if (taskData != null && taskData.getLastModified() != null) {
				task.setLastReadTimeStamp(taskData.getLastModified());
				taskDataStorageManager.setOldTaskData(taskData);
			}
			task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
			taskList.notifyElementChanged(task);
		} else if (read && task.getSynchronizationState().equals(SynchronizationState.CONFLICT)) {
			if (taskData != null && taskData.getLastModified() != null) {
				task.setLastReadTimeStamp(taskData.getLastModified());
			}
			task.setSynchronizationState(SynchronizationState.OUTGOING);
			taskList.notifyElementChanged(task);
		} else if (read && task.getSynchronizationState().equals(SynchronizationState.SYNCHRONIZED)) {
			if (taskData != null && taskData.getLastModified() != null) {
				task.setLastReadTimeStamp(taskData.getLastModified());
				// By setting old every time (and not setting upon submission)
				// we see our changes
				// If condition is enabled and we save old in OUTGOING handler
				// our own changes
				// will not be displayed after submission.
				// if
				// (dataManager.getOldTaskData(repositoryTask.getHandleIdentifier())
				// == null) {
				taskDataStorageManager.setOldTaskData(taskData);
				// }
			}
//			else if (repositoryTask.getLastReadTimeStamp() == null && repositoryTask.isLocal()) {
//				// fall back for cases where the stamp is missing, set bogus date
//				repositoryTask.setLastReadTimeStamp(LocalTask.SYNC_DATE_NOW);
//			}

		} else if (!read && task.getSynchronizationState().equals(SynchronizationState.SYNCHRONIZED)) {
			task.setSynchronizationState(SynchronizationState.INCOMING);
			taskList.notifyElementChanged(task);
		}

		// for connectors that don't support task data set read date to now (bug#204741)
		if (read && taskData == null && task.isLocal()) {
			task.setLastReadTimeStamp((new Date()).toString());
		}
	}

	void putEdits(final ITask itask, final TaskData editsData) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		Assert.isNotNull(editsData);
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				taskDataStore.putEdits(getFile(task, kind), editsData);
				switch (task.getSynchronizationState()) {
				case INCOMING:
				case INCOMING_NEW:
					// TODO throw exception instead?
					task.setSynchronizationState(SynchronizationState.CONFLICT);
					break;
				case SYNCHRONIZED:
					task.setSynchronizationState(SynchronizationState.OUTGOING);
					break;
				}
			}
		});
		taskList.notifySynchronizationStateChanged(task);
	}

	@Deprecated
	public org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData getNewTaskData(String repositoryUrl,
			String taskId) {
		return taskDataStorageManager.getNewTaskData(repositoryUrl, taskId);
	}

	@Deprecated
	public void setNewTaskData(org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData taskData) {
		taskDataStorageManager.setNewTaskData(taskData);
	}

	@Deprecated
	public TaskDataStorageManager getTaskDataStorageManager() {
		return taskDataStorageManager;
	}

}
