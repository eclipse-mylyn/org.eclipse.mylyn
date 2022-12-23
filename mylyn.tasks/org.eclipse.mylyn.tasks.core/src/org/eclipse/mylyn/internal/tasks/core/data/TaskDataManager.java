/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.DelegatingProgressMonitor;
import org.eclipse.mylyn.commons.core.IDelegatingProgressMonitor;
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
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Encapsulates synchronization policy.
 *
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskDataManager implements ITaskDataManager {

	private final IRepositoryManager repositoryManager;

	private final TaskDataStore taskDataStore;

	private final TaskList taskList;

	private final TaskActivityManager taskActivityManager;

	private final List<ITaskDataManagerListener> listeners = new CopyOnWriteArrayList<ITaskDataManagerListener>();

	private final SynchronizationManger synchronizationManager;

	private final TaskDataFileManager fileManager = new TaskDataFileManager();

	public TaskDataManager(TaskDataStore taskDataStore, IRepositoryManager repositoryManager, TaskList taskList,
			TaskActivityManager taskActivityManager, SynchronizationManger synchronizationManager) {
		this.taskDataStore = taskDataStore;
		this.repositoryManager = repositoryManager;
		this.taskList = taskList;
		this.taskActivityManager = taskActivityManager;
		this.synchronizationManager = synchronizationManager;
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
		final boolean[] changed = new boolean[1];
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
						changed[0] = true;
						break;
					case CONFLICT:
						task.setSynchronizationState(SynchronizationState.OUTGOING);
						changed[0] = true;
						break;
					}
					task.setMarkReadPending(true);
				}
				result[0] = state;
			}
		}, null, true);
		if (changed[0]) {
			taskList.notifyElementChanged(task);
		}
		return result[0];
	}

	public void saveWorkingCopy(final ITask itask, final TaskDataState state) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		final boolean[] changed = new boolean[1];
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				final File file = fileManager.getFile(task, kind);
				taskDataStore.putTaskData(ensurePathExists(file), state);
				switch (task.getSynchronizationState()) {
				case SYNCHRONIZED:
					task.setSynchronizationState(SynchronizationState.OUTGOING);
					changed[0] = true;
					break;
				}
				taskList.addTask(task);
			}
		});
		if (changed[0]) {
			taskList.notifyElementChanged(task);
		}
	}

	public void putUpdatedTaskData(final ITask itask, final TaskData taskData, final boolean user)
			throws CoreException {
		putUpdatedTaskData(itask, taskData, user, null);
	}

	public void putUpdatedTaskData(final ITask itask, final TaskData taskData, final boolean user, Object token)
			throws CoreException {
		putUpdatedTaskData(itask, taskData, user, null, null);
	}

	public void putUpdatedTaskData(final ITask itask, final TaskData taskData, final boolean user, Object token,
			IProgressMonitor monitor) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		Assert.isNotNull(taskData);
		final AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
		final TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		final boolean taskDataChanged = connector.hasTaskChanged(repository, task, taskData);
		final TaskDataManagerEvent event = new TaskDataManagerEvent(this, itask, taskData, token);
		event.setTaskDataChanged(taskDataChanged);
		IDelegatingProgressMonitor delegatingMonitor = DelegatingProgressMonitor.getMonitorFrom(monitor);
		if (delegatingMonitor != null) {
			event.setData(delegatingMonitor.getData());
		}
		final boolean[] synchronizationStateChanged = new boolean[1];
		if (taskDataChanged || user) {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					TaskDataState state = null;
					if (!taskData.isPartial()) {
						File file = getMigratedFile(task, task.getConnectorKind());
						state = taskDataStore.putTaskData(ensurePathExists(file), taskData, task.isMarkReadPending(),
								user);
						task.setMarkReadPending(false);
						event.setTaskDataUpdated(true);
					}

					boolean taskChanged = updateTaskFromTaskData(taskData, task, connector, repository);
					event.setTaskChanged(taskChanged);

					if (taskDataChanged) {
						String suppressIncoming = null;
						// determine whether to show an incoming
						if (state == null) {
							File file = getMigratedFile(task, task.getConnectorKind());
							state = taskDataStore.getTaskDataState(ensurePathExists(file));
						}
						TaskData lastReadData = (state != null) ? state.getLastReadData() : null;
						TaskDataDiff diff = synchronizationManager.createDiff(taskData, lastReadData, monitor);
						suppressIncoming = Boolean.toString(!diff.hasChanged());

						switch (task.getSynchronizationState()) {
						case OUTGOING:
							task.setSynchronizationState(SynchronizationState.CONFLICT);
							break;
						case SYNCHRONIZED:
							task.setSynchronizationState(SynchronizationState.INCOMING);
							break;
						}

						// if an incoming was previously suppressed it may need to show now
						task.setAttribute(ITasksCoreConstants.ATTRIBUTE_TASK_SUPPRESS_INCOMING, suppressIncoming);
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
		File file = fileManager.getFile(task, kind);
		if (!file.exists()) {
			File oldFile = fileManager.getFile10(task, kind);
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
		final TaskDataManagerEvent event = new TaskDataManagerEvent(this, itask);
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				File dataFile = fileManager.getFile(task, kind);
				if (dataFile.exists()) {
					taskDataStore.discardEdits(dataFile);
				}
				switch (task.getSynchronizationState()) {
				case OUTGOING:
					task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
					event.setTaskChanged(true);
					break;
				case CONFLICT:
					task.setSynchronizationState(SynchronizationState.INCOMING);
					event.setTaskChanged(true);
					break;
				}
			}
		});
		if (event.getTaskChanged()) {
			taskList.notifyElementChanged(task);
		}
		fireEditsDiscarded(event);
	}

	private File findFile(ITask task, String kind) {
		File file = fileManager.getFile(task, kind);
		if (file.exists()) {
			return file;
		}
		return fileManager.getFile10(task, kind);
	}

	public String getDataPath() {
		return fileManager.getDataPath();
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
		TaskDataState state = taskDataStore.getTaskDataState(
				findFile(new TaskTask(taskRepository.getConnectorKind(), taskRepository.getRepositoryUrl(), taskId),
						taskRepository.getConnectorKind()));
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

	public void putSubmittedTaskData(final ITask itask, final TaskData taskData, IDelegatingProgressMonitor monitor)
			throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		Assert.isNotNull(taskData);
		final AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
		final TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		final TaskDataManagerEvent event = new TaskDataManagerEvent(this, itask, taskData, null);
		event.setTaskDataChanged(true);
		event.setData(((DelegatingProgressMonitor) monitor).getData());
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
				File file = fileManager.getFile(task, task.getConnectorKind());
				if (file.exists()) {
					taskDataStore.deleteTaskData(file);
					task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
				}
			}
		});
		taskList.notifyElementChanged(task);
	}

	public void setDataPath(String dataPath) {
		fileManager.setDataPath(dataPath);
	}

	/**
	 * @param itask
	 *            repository task to mark as read or unread
	 * @param read
	 *            true to mark as read, false to mark as unread
	 * @return true if synchronization state has been changed
	 */
	public boolean setTaskRead(final ITask itask, final boolean read) {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		final boolean changed[] = new boolean[1];
		try {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					if (read) {
						switch (task.getSynchronizationState()) {
						case INCOMING:
						case INCOMING_NEW:
							task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
							task.setMarkReadPending(true);
							changed[0] = true;
							break;
						case CONFLICT:
							task.setSynchronizationState(SynchronizationState.OUTGOING);
							task.setMarkReadPending(true);
							changed[0] = true;
							break;
						}
					} else {
						// if an incoming was previously suppressed it need to show now
						task.setAttribute(ITasksCoreConstants.ATTRIBUTE_TASK_SUPPRESS_INCOMING,
								Boolean.toString(false));
						switch (task.getSynchronizationState()) {
						case SYNCHRONIZED:
							task.setSynchronizationState(SynchronizationState.INCOMING);
							task.setMarkReadPending(false);
							changed[0] = true;
							break;
						}
					}
				}
			});
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Unexpected error while marking task read", e)); //$NON-NLS-1$
		}
		if (changed[0]) {
			taskList.notifyElementChanged(task);
		}
		return changed[0];
	}

	void putEdits(final ITask itask, final TaskData editsData) throws CoreException {
		final AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		final String kind = task.getConnectorKind();
		Assert.isNotNull(editsData);
		final boolean[] changed = new boolean[1];
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				taskDataStore.putEdits(fileManager.getFile(task, kind), editsData);
				switch (task.getSynchronizationState()) {
				case INCOMING:
				case INCOMING_NEW:
					// TODO throw exception instead?
					task.setSynchronizationState(SynchronizationState.CONFLICT);
					changed[0] = true;
					break;
				case SYNCHRONIZED:
					task.setSynchronizationState(SynchronizationState.OUTGOING);
					changed[0] = true;
					break;
				}
			}
		});
		if (changed[0]) {
			taskList.notifySynchronizationStateChanged(task);
		}
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
						File newFile = fileManager.getFile(newStorageRepositoryUrl, task, kind);
						TaskDataState newState = new TaskDataState(oldState.getConnectorKind(), newRepositoryUrl,
								oldState.getTaskId());
						newState.merge(oldState);
						taskDataStore.putTaskData(ensurePathExists(newFile), newState);
					}
				}
			}
		});
	}

	public void refactorAttributeValue(final ITask itask, final Map<TaskAttribute, Collection<String>> newValues)
			throws CoreException {
		Assert.isTrue(itask instanceof AbstractTask);
		final AbstractTask task = (AbstractTask) itask;
		final String kind = task.getConnectorKind();
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				File file = getMigratedFile(task, kind);
				if (file.exists()) {
					TaskDataState state = taskDataStore.getTaskDataState(file);
					if (state != null) {
						state.changeAttributeValues(newValues);
						taskDataStore.putTaskData(file, state);
					}
				}
			}
		});
	}

	public void refactorTaskId(final AbstractTask task, final ITask newTask) throws CoreException {
		final String kind = task.getConnectorKind();
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				File file = getMigratedFile(task, kind);
				if (file.exists()) {
					TaskDataState oldState = taskDataStore.getTaskDataState(file);
					if (oldState != null) {
						File newFile = fileManager.getFile(task.getRepositoryUrl(), newTask, kind);
						TaskDataState newState = new TaskDataState(oldState.getConnectorKind(), task.getRepositoryUrl(),
								newTask.getTaskId());
						newState.merge(oldState);
						taskDataStore.putTaskData(ensurePathExists(newFile), newState);
						taskDataStore.deleteTaskData(file);
					}
				}
			}

		});
	}

	public void refactorAttribute(ITask itask, TaskAttribute attribute) throws CoreException {
		Assert.isTrue(itask instanceof AbstractTask);
		final AbstractTask task = (AbstractTask) itask;
		final String kind = task.getConnectorKind();
		taskList.run(new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				File file = getMigratedFile(task, kind);
				if (file.exists()) {
					TaskDataState state = taskDataStore.getTaskDataState(file);
					if (state != null) {
						state.refactorAttribute(attribute);
						taskDataStore.putTaskData(file, state);
					}
				}
			}
		});

	}
}
