/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotification;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.data.ITaskDataManagerListener;
import org.eclipse.mylyn.internal.tasks.core.data.SynchronizationManger;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataDiff;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManagerEvent;
import org.eclipse.mylyn.internal.tasks.ui.ITaskListNotificationProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.PresentationFilter;
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

	public boolean enabled;

	private final SynchronizationManger synchronizationManger;

	public TaskListNotifier(RepositoryModel repositoryModel, TaskDataManager taskDataManager,
			SynchronizationManger synchronizationManger) {
		this.repositoryModel = repositoryModel;
		this.taskDataManager = taskDataManager;
		this.synchronizationManger = synchronizationManger;
	}

	public TaskListNotification getNotification(ITask task, Object token) {
		if (task.getSynchronizationState() == SynchronizationState.INCOMING_NEW) {
			TaskListNotification notification = new TaskListNotification(task, token);
			notification.setDescription(Messages.TaskListNotifier_New_unread_task);
			return notification;
		} else if (task.getSynchronizationState() == SynchronizationState.INCOMING) {
			TaskDataDiff diff = getDiff(task);
			if (diff != null && diff.hasChanged()) {
				TaskListNotification notification = new TaskListNotification(task, token);
				notification.setDescription(TaskDiffUtil.toString(diff, 60, true));
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
				return synchronizationManger.createDiff(workingCopy.getRepositoryData(), workingCopy.getLastReadData(),
						new NullProgressMonitor());
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to get task data for task: \"" //$NON-NLS-1$
					+ task + "\"", e)); //$NON-NLS-1$
		}
		return null;
	}

	public void taskDataUpdated(TaskDataManagerEvent event) {
		synchronized (notificationQueue) {
			if (!enabled) {
				// skip expensive processing
				return;
			}
		}
		if (event.getToken() != null && event.getTaskDataChanged()) {
			if (PresentationFilter.getInstance().isInVisibleQuery(event.getTask())) {
				AbstractRepositoryConnectorUi connectorUi = TasksUi.getRepositoryConnectorUi(event.getTaskData()
						.getConnectorKind());
				if (!connectorUi.hasCustomNotifications()) {
					TaskListNotification notification = getNotification(event.getTask(), event.getToken());
					if (notification != null) {
						synchronized (notificationQueue) {
							if (enabled) {
								notificationQueue.add(notification);
							}
						}
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

	public void setEnabled(boolean enabled) {
		synchronized (notificationQueue) {
			if (!enabled) {
				notificationQueue.clear();
			}
			this.enabled = enabled;
		}
	}

	public boolean isEnabled() {
		synchronized (notificationQueue) {
			return enabled;
		}
	}

}
