/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager2;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.IRepositorySynchronizationManager;

/**
 * Encapsulates synchronization policy.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public final class RepositorySynchronizationManager implements IRepositorySynchronizationManager {

	private final TaskDataManager taskDataManager;

	private final ITaskList taskList;

	private final ITaskDataManager2 taskDataManager2;

	private final ITaskRepositoryManager repositoryManager;

	public RepositorySynchronizationManager(TaskDataManager taskDataManager, ITaskDataManager2 taskDataManager2,
			ITaskRepositoryManager repositoryManager, ITaskList taskList) {
		this.taskDataManager = taskDataManager;
		this.taskDataManager2 = taskDataManager2;
		this.repositoryManager = repositoryManager;
		this.taskList = taskList;
	}

	/**
	 * @param repositoryTask
	 *            task that changed
	 * @param modifiedAttributes
	 *            attributes that have changed during edit session
	 */
	public synchronized void saveOutgoing(AbstractTask repositoryTask, Set<RepositoryTaskAttribute> modifiedAttributes) {
		repositoryTask.setSynchronizationState(RepositoryTaskSyncState.OUTGOING);
		taskDataManager.saveEdits(repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId(),
				Collections.unmodifiableSet(modifiedAttributes));
		taskList.notifyTaskChanged(repositoryTask, false);
	}

	/**
	 * Saves incoming data and updates task sync state appropriately
	 * 
	 * @return true if call results in change of sync state
	 */
	public synchronized boolean saveIncoming(final AbstractTask repositoryTask, final RepositoryTaskData newTaskData,
			boolean forceSync) {
		Assert.isNotNull(newTaskData);
		final RepositoryTaskSyncState startState = repositoryTask.getSynchronizationState();
		RepositoryTaskSyncState status = repositoryTask.getSynchronizationState();

		RepositoryTaskData previousTaskData = taskDataManager.getNewTaskData(repositoryTask.getRepositoryUrl(),
				repositoryTask.getTaskId());

		if (repositoryTask.isSubmitting()) {
			status = RepositoryTaskSyncState.SYNCHRONIZED;
			repositoryTask.setSubmitting(false);
			TaskDataManager dataManager = taskDataManager;
			dataManager.discardEdits(repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId());

			taskDataManager.setNewTaskData(newTaskData);
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
				if (checkHasIncoming(repositoryTask, newTaskData)) {
					status = RepositoryTaskSyncState.CONFLICT;
				}
				taskDataManager.setNewTaskData(newTaskData);
				break;

			case CONFLICT:
				// fall through to INCOMING (conflict implies incoming)
			case INCOMING:
				// only most recent incoming will be displayed if two
				// sequential incoming's /conflicts happen

				taskDataManager.setNewTaskData(newTaskData);
				break;
			case SYNCHRONIZED:
				boolean hasIncoming = checkHasIncoming(repositoryTask, newTaskData);
				if (hasIncoming) {
					status = RepositoryTaskSyncState.INCOMING;
					repositoryTask.setNotified(false);
				}
				if (hasIncoming || previousTaskData == null || forceSync) {
					taskDataManager.setNewTaskData(newTaskData);
				}
				break;
			}
		}
		repositoryTask.setSynchronizationState(status);
		return startState != repositoryTask.getSynchronizationState();
	}

	public void saveOffline(AbstractTask task, RepositoryTaskData taskData) {
		taskDataManager.setNewTaskData(taskData);
	}

	/** public for testing purposes */
	public boolean checkHasIncoming(AbstractTask repositoryTask, RepositoryTaskData newData) {
		if (repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.INCOMING) {
			return true;
		}

		String lastModified = repositoryTask.getLastReadTimeStamp();
		RepositoryTaskAttribute modifiedDateAttribute = newData.getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		if (lastModified != null && modifiedDateAttribute != null && modifiedDateAttribute.getValue() != null) {
			if (lastModified.trim().compareTo(modifiedDateAttribute.getValue().trim()) == 0) {
				// Only set to synchronized state if not in incoming state.
				// Case of incoming->sync handled by markRead upon opening
				// or a forced synchronization on the task only.
				return false;
			}

			Date modifiedDate = newData.getAttributeFactory().getDateForAttributeType(
					RepositoryTaskAttribute.DATE_MODIFIED, modifiedDateAttribute.getValue());
			Date lastModifiedDate = newData.getAttributeFactory().getDateForAttributeType(
					RepositoryTaskAttribute.DATE_MODIFIED, lastModified);
			if (modifiedDate != null && lastModifiedDate != null && modifiedDate.equals(lastModifiedDate)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @param repositoryTask -
	 *            repository task to mark as read or unread
	 * @param read -
	 *            true to mark as read, false to mark as unread
	 */
	public void setTaskRead(AbstractTask repositoryTask, boolean read) {
		RepositoryTaskData taskData = taskDataManager.getNewTaskData(repositoryTask.getRepositoryUrl(),
				repositoryTask.getTaskId());

		if (read && repositoryTask.getSynchronizationState().equals(RepositoryTaskSyncState.INCOMING)) {
			if (taskData != null && taskData.getLastModified() != null) {
				repositoryTask.setLastReadTimeStamp(taskData.getLastModified());
				taskDataManager.setOldTaskData(taskData);
			}
			repositoryTask.setSynchronizationState(RepositoryTaskSyncState.SYNCHRONIZED);
			taskList.notifyTaskChanged(repositoryTask, false);
		} else if (read && repositoryTask.getSynchronizationState().equals(RepositoryTaskSyncState.CONFLICT)) {
			if (taskData != null && taskData.getLastModified() != null) {
				repositoryTask.setLastReadTimeStamp(taskData.getLastModified());
			}
			repositoryTask.setSynchronizationState(RepositoryTaskSyncState.OUTGOING);
			taskList.notifyTaskChanged(repositoryTask, false);
		} else if (read && repositoryTask.getSynchronizationState().equals(RepositoryTaskSyncState.SYNCHRONIZED)) {
			if (taskData != null && taskData.getLastModified() != null) {
				repositoryTask.setLastReadTimeStamp(taskData.getLastModified());
				// By setting old every time (and not setting upon submission)
				// we see our changes
				// If condition is enabled and we save old in OUTGOING handler
				// our own changes
				// will not be displayed after submission.
				// if
				// (dataManager.getOldTaskData(repositoryTask.getHandleIdentifier())
				// == null) {
				taskDataManager.setOldTaskData(taskData);
				// }
			}
//			else if (repositoryTask.getLastReadTimeStamp() == null && repositoryTask.isLocal()) {
//				// fall back for cases where the stamp is missing, set bogus date
//				repositoryTask.setLastReadTimeStamp(LocalTask.SYNC_DATE_NOW);
//			}

		} else if (!read && repositoryTask.getSynchronizationState().equals(RepositoryTaskSyncState.SYNCHRONIZED)) {
			repositoryTask.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
			taskList.notifyTaskChanged(repositoryTask, false);
		}

		// for connectors that don't support task data set read date to now (bug#204741)
		if (read && taskData == null && repositoryTask.isLocal()) {
			repositoryTask.setLastReadTimeStamp((new Date()).toString());
		}
	}

	public void discardOutgoing(AbstractTask repositoryTask) {
		taskDataManager.discardEdits(repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId());
		repositoryTask.setSynchronizationState(RepositoryTaskSyncState.SYNCHRONIZED);
		taskList.notifyTaskChanged(repositoryTask, true);
	}

	public void putTaskData(AbstractTask task, TaskData taskData, boolean user) throws CoreException {
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
		TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(), task.getRepositoryUrl());

		boolean changed = connector.hasChanged(task, taskData);
		if (changed || user) {
			connector.updateTaskFromTaskData(repository, task, taskData);

			if (!taskData.isPartial()) {
				taskDataManager2.setTaskData(task, task.getConnectorKind(), taskData);
			}
		}

		if (changed) {
			RepositoryTaskSyncState state = task.getSynchronizationState();
			switch (state) {
			case OUTGOING:
				state = RepositoryTaskSyncState.CONFLICT;
				break;
			case SYNCHRONIZED:
				state = RepositoryTaskSyncState.INCOMING;
				break;
			}
			task.setSynchronizationState(state);

			task.setStale(false);
			task.setSynchronizing(false);

			taskList.notifyTaskChanged(task, false);
		}
	}

}
