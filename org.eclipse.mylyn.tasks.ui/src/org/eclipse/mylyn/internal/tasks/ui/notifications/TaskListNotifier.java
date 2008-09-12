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

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotification;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.data.ITaskDataManagerListener;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManagerEvent;
import org.eclipse.mylyn.internal.tasks.ui.ITaskListNotificationProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class TaskListNotifier implements ITaskDataManagerListener, ITaskListNotificationProvider {

	private final TaskDataManager taskDataManager;

	private final List<TaskListNotification> notificationQueue = new ArrayList<TaskListNotification>();

	private final RepositoryModel repositoryModel;

	public TaskListNotifier(RepositoryModel repositoryModel, TaskDataManager taskDataManager) {
		this.repositoryModel = repositoryModel;
		this.taskDataManager = taskDataManager;
		this.taskDataManager.addListener(this);
	}

	public TaskListNotification getNotification(ITask task, Object token) {
		if (task.getSynchronizationState() == SynchronizationState.INCOMING_NEW) {
			TaskListNotification notification = new TaskListNotification(task, token);
			notification.setDescription("New unread task");
			return notification;
		} else if (task.getSynchronizationState() == SynchronizationState.INCOMING) {
			TaskDataDiff diff = getDiff(task);
			if (diff != null) {
				TaskListNotification notification = new TaskListNotification(task, token);
				notification.setDescription(diff.toString());
				return notification;
			}
		}
		return null;
	}

	public TaskDataDiff getDiff(ITask task) {
		ITaskDataWorkingCopy workingCopy;
		try {
			workingCopy = taskDataManager.getTaskDataState(task);
			if (workingCopy != null) {
				TaskDataDiff diff = new TaskDataDiff(repositoryModel, workingCopy.getRepositoryData(),
						workingCopy.getLastReadData());
				return diff;
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to get task data for task: \""
					+ task + "\"", e));
		}
		return null;
	}

	public void taskDataUpdated(TaskDataManagerEvent event) {
		if (event.getToken() != null && event.getTaskDataChanged()) {
			AbstractRepositoryConnectorUi connectorUi = TasksUi.getRepositoryConnectorUi(event.getTaskData()
					.getConnectorKind());
			if (!connectorUi.hasCustomNotifications()) {
				TaskListNotification notification = getNotification(event.getTask(), event.getToken());
				if (notification != null) {
					synchronized (notificationQueue) {
						notificationQueue.add(notification);
					}
				}
			}
		}
	}

	public Set<AbstractNotification> getNotifications() {
		synchronized (notificationQueue) {
			if (notificationQueue.isEmpty()) {
				return Collections.emptySet();
			}
			HashSet<AbstractNotification> result = new HashSet<AbstractNotification>(notificationQueue);
			notificationQueue.clear();
			return result;
		}
	}

	public void editsDiscarded(TaskDataManagerEvent event) {
		// ignore		
	}

}
