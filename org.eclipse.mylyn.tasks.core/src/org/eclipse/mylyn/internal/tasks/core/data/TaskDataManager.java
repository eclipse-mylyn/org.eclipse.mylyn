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

package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
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

	private static final String ENCODING_UTF_8 = "UTF-8"; //$NON-NLS-1$

	private static final String EXTENSION = ".zip"; //$NON-NLS-1$

	private static final String FOLDER_TASKS = "tasks"; //$NON-NLS-1$

	private static final String FOLDER_DATA = "offline"; //$NON-NLS-1$

	private static final String FOLDER_TASKS_1_0 = "offline"; //$NON-NLS-1$

	private String dataPath;

	private final IRepositoryManager repositoryManager;

	private final TaskDataStore taskDataStore;

	private final TaskList taskList;

	private final TaskActivityManager taskActivityManager;

	private final List<ITaskDataManagerListener> listeners = new CopyOnWriteArrayList<ITaskDataManagerListener>();

	public TaskDataManager(TaskDataStore taskDataStore, IRepositoryManager repositoryManager, TaskList taskList,
			TaskActivityManager taskActivityManager) {
		this.taskDataStore = taskDataStore;
		this.repositoryManager = repositoryManager;
		this.taskList = taskList;
		this.taskActivityManager = taskActivityManager;
	}

	public void addListener(ITaskDataManagerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ITaskDataManagerListener listener) {
		listeners.remove(listener);
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
		return getWorkingCopy(itask, true);
	}

	public ITaskDataWorkingCopy getWorkingCopy(final ITask itask, final boolean markRead) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		final TaskDataState[] result = new TaskDataState[1];
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				final File file = getMigratedFile(task, kind);
				final TaskDataState state = taskDataStore.getTaskDataState(file);
				if (state == null) {
					throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Task data at \"" //$NON-NLS-1$
							+ file + "\" not found")); //$NON-NLS-1$
				}
				if (task.isMarkReadPending()) {
					state.setLastReadData(state.getRepositoryData());
				}
				state.init(TaskDataManager.this, task);
				state.revert();
				if (markRead) {
					switch (task.getSynchronizationState()) {
					case INCOMING:
					case INCOMING_NEW:
						task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
						break;
					case CONFLICT:
						task.setSynchronizationState(SynchronizationState.OUTGOING);
						break;
					}
					task.setMarkReadPending(true);
				}
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
		putUpdatedTaskData(itask, taskData, user, null);
	}

	public void putUpdatedTaskData(final ITask itask, final TaskData taskData, final boolean user, Object token)
			throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		Assert.isNotNull(taskData);
		final AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
		final TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		final boolean taskDataChanged = connector.hasTaskChanged(repository, task, taskData);
		final TaskDataManagerEvent event = new TaskDataManagerEvent(this, itask, taskData, token);
		event.setTaskDataChanged(taskDataChanged);
		final boolean[] synchronizationStateChanged = new boolean[1];
		if (taskDataChanged || user) {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					if (!taskData.isPartial()) {
						File file = getMigratedFile(task, task.getConnectorKind());
						taskDataStore.putTaskData(ensurePathExists(file), taskData, task.isMarkReadPending(), user);
						task.setMarkReadPending(false);
						event.setTaskDataUpdated(true);
					}

					boolean taskChanged = updateTaskFromTaskData(taskData, task, connector, repository);
					event.setTaskChanged(taskChanged);

					if (taskDataChanged) {
						switch (task.getSynchronizationState()) {
						case OUTGOING:
							task.setSynchronizationState(SynchronizationState.CONFLICT);
							break;
						case SYNCHRONIZED:
							task.setSynchronizationState(SynchronizationState.INCOMING);
							break;
						}
					}
					if (task.isSynchronizing()) {
						task.setSynchronizing(false);
						synchronizationStateChanged[0] = true;
					}
				}
			});
		} else {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					if (task.isSynchronizing()) {
						task.setSynchronizing(false);
						synchronizationStateChanged[0] = true;
					}
				}
			});
		}
		if (event.getTaskChanged() || event.getTaskDataChanged()) {
			taskList.notifyElementChanged(task);
			fireTaskDataUpdated(event);
		} else {
			if (synchronizationStateChanged[0]) {
				taskList.notifySynchronizationStateChanged(task);
			}
			if (event.getTaskDataUpdated()) {
				fireTaskDataUpdated(event);
			}
		}
	}

	private boolean updateTaskFromTaskData(final TaskData taskData, final AbstractTask task,
			final AbstractRepositoryConnector connector, final TaskRepository repository) {
		task.setChanged(false);
		Date oldDueDate = task.getDueDate();
		connector.updateTaskFromTaskData(repository, task, taskData);
		// XXX move this to AbstractTask or use model listener to notify task activity 
		// manager of due date changes
		Date newDueDate = task.getDueDate();
		if (oldDueDate != null && !oldDueDate.equals(newDueDate) || newDueDate != oldDueDate) {
			taskActivityManager.setDueDate(task, newDueDate);
		}
		return task.isChanged();
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
		final TaskDataManagerEvent event = new TaskDataManagerEvent(this, itask);
		fireEditsDiscarded(event);
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
		return getFile(task.getRepositoryUrl(), task, kind);
	}

	private File getFile(String repositoryUrl, ITask task, String kind) {
//			String pathName = task.getConnectorKind() + "-"
//					+ URLEncoder.encode(task.getRepositoryUrl(), ENCODING_UTF_8);
//			String fileName = kind + "-" + URLEncoder.encode(task.getTaskId(), ENCODING_UTF_8) + EXTENSION;
		String repositoryPath = task.getConnectorKind() + "-" + encode(repositoryUrl); //$NON-NLS-1$
		String fileName = encode(task.getTaskId()) + EXTENSION;
		File path = new File(dataPath + File.separator + FOLDER_TASKS + File.separator + repositoryPath
				+ File.separator + FOLDER_DATA);
		return new File(path, fileName);
	}

	private static String encode(String text) {
		StringBuffer sb = new StringBuffer(text.length());
		char[] chars = text.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.') {
				sb.append(c);
			} else {
				sb.append("%" + Integer.toHexString(c).toUpperCase()); //$NON-NLS-1$
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

	public TaskDataState getTaskDataState(ITask task) throws CoreException {
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		// TODO check that repository task data != null for returned task data state
		return taskDataStore.getTaskDataState(findFile(task, kind));
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
		return findFile(task, kind).exists();
	}

	public void putSubmittedTaskData(final ITask itask, final TaskData taskData) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		Assert.isNotNull(taskData);
		final AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
		final TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		final TaskDataManagerEvent event = new TaskDataManagerEvent(this, itask, taskData, null);
		event.setTaskDataChanged(true);
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				if (!taskData.isPartial()) {
					File file = getMigratedFile(task, task.getConnectorKind());
					taskDataStore.setTaskData(ensurePathExists(file), taskData);
					task.setMarkReadPending(false);
					event.setTaskDataUpdated(true);
				}

				boolean taskChanged = updateTaskFromTaskData(taskData, task, connector, repository);
				event.setTaskChanged(taskChanged);

				task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
				task.setSynchronizing(false);
			}
		});
		taskList.notifyElementChanged(task);
		fireTaskDataUpdated(event);
	}

	public void deleteTaskData(final ITask itask) throws CoreException {
		Assert.isTrue(itask instanceof AbstractTask);
		final AbstractTask task = (AbstractTask) itask;
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				File file = getFile(task, task.getConnectorKind());
				if (file.exists()) {
					taskDataStore.deleteTaskData(file);
					task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
				}
			}
		});
		taskList.notifyElementChanged(task);
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
					"Unexpected error while marking task read", e)); //$NON-NLS-1$
		}
		taskList.notifyElementChanged(task);
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

	private void fireTaskDataUpdated(final TaskDataManagerEvent event) {
		ITaskDataManagerListener[] array = listeners.toArray(new ITaskDataManagerListener[0]);
		if (array.length > 0) {
			for (final ITaskDataManagerListener listener : array) {
				SafeRunner.run(new ISafeRunnable() {

					public void handleException(Throwable exception) {
						// ignore
					}

					public void run() throws Exception {
						listener.taskDataUpdated(event);
					}

				});
			}
		}
	}

	private void fireEditsDiscarded(final TaskDataManagerEvent event) {
		ITaskDataManagerListener[] array = listeners.toArray(new ITaskDataManagerListener[0]);
		if (array.length > 0) {
			for (final ITaskDataManagerListener listener : array) {
				SafeRunner.run(new ISafeRunnable() {

					public void handleException(Throwable exception) {
						// ignore
					}

					public void run() throws Exception {
						listener.editsDiscarded(event);
					}

				});
			}
		}
	}

	public void refactorRepositoryUrl(final ITask itask, final String newStorageRepositoryUrl,
			final String newRepositoryUrl) throws CoreException {
		Assert.isTrue(itask instanceof AbstractTask);
		final AbstractTask task = (AbstractTask) itask;
		final String kind = task.getConnectorKind();
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				File file = getMigratedFile(task, kind);
				if (file.exists()) {
					TaskDataState oldState = taskDataStore.getTaskDataState(file);
					if (oldState != null) {
						File newFile = getFile(newStorageRepositoryUrl, task, kind);
						TaskDataState newState = new TaskDataState(oldState.getConnectorKind(), newRepositoryUrl,
								oldState.getTaskId());
						newState.merge(oldState);
						taskDataStore.putTaskData(ensurePathExists(newFile), newState);
					}
				}
			}
		});
	}
}
