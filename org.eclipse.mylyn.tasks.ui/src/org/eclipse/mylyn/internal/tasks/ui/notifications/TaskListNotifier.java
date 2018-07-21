/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.internal.tasks.core.data.ITaskDataManagerListener;
import org.eclipse.mylyn.internal.tasks.core.data.SynchronizationManger;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataDiff;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManagerEvent;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeTasksJob;
import org.eclipse.mylyn.internal.tasks.ui.ITaskListNotificationProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.PresentationFilter;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;

import com.google.common.base.Strings;

/**
 * @author Steffen Pingel
 */
public class TaskListNotifier implements ITaskDataManagerListener, ITaskListNotificationProvider {

	public final static String KEY_INCOMING_NOTIFICATION_TEXT = "org.eclipse.mylyn.tasks.ui.TaskNotificationText"; //$NON-NLS-1$

	private final TaskDataManager taskDataManager;

	private final List<TaskListNotification> notificationQueue = new ArrayList<TaskListNotification>();

	public boolean enabled;

	private final SynchronizationManger synchronizationManger;

	public TaskListNotifier(TaskDataManager taskDataManager, SynchronizationManger synchronizationManger) {
		this.taskDataManager = taskDataManager;
		this.synchronizationManger = synchronizationManger;
	}

	public TaskListNotification getNotification(ITask task, Object token) {
		if (task.getSynchronizationState() == SynchronizationState.INCOMING_NEW) {
			TaskListNotification notification = new TaskListNotification(task, token);
			notification.setDescription(Messages.TaskListNotifier_New_unread_task);
			return notification;
		} else if (task.getSynchronizationState().isIncoming()) {
			String notificationText = task.getAttribute(KEY_INCOMING_NOTIFICATION_TEXT);
			if (!Strings.isNullOrEmpty(notificationText)) {
				TaskListNotification notification = new TaskListNotification(task, token);
				notification.setDescription(notificationText);
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

	@Override
	public void taskDataUpdated(TaskDataManagerEvent event) {
		// Events from SynchronizeQueryJobs end up with incorrect diffs
		// Only notify for the subsequent SynchronizeTasksJobs
		if (event.getTaskChanged() && event.getData() instanceof SynchronizeTasksJob) {

			// Always record the notification text, it may be read outside of the notification
			// The text does not need a token to be recorded, and manually synchronized tasks will not have an associated token
			recordNotificationText(event);

			if (shouldDisplayNotification(event)) {
				queueNotification(event);
			}
		}
	}

	private void recordNotificationText(TaskDataManagerEvent event) {
		ITask task = event.getTask();
		SynchronizationState state = task.getSynchronizationState();
		String notificationText = null;
		if (state.isIncoming()) {
			notificationText = computeNotificationText(task);
		}
		task.setAttribute(KEY_INCOMING_NOTIFICATION_TEXT, notificationText);
	}

	public String computeNotificationText(ITask task) {
		TaskDataDiff diff = getDiff(task);
		if (diff != null && diff.hasChanged()) {
			return TaskDiffUtil.toString(diff, true);
		}
		return null;
	}

	private boolean shouldDisplayNotification(TaskDataManagerEvent event) {
		return event.getToken() != null && isEnabled()
				&& PresentationFilter.getInstance().isInVisibleQuery(event.getTask());
	}

	private void queueNotification(TaskDataManagerEvent event) {
		AbstractRepositoryConnectorUi connectorUi = TasksUi
				.getRepositoryConnectorUi(event.getTaskData().getConnectorKind());

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

	public Set<AbstractUiNotification> getNotifications() {
		synchronized (notificationQueue) {
			if (notificationQueue.isEmpty()) {
				return Collections.emptySet();
			}
			HashSet<AbstractUiNotification> result = new HashSet<AbstractUiNotification>(notificationQueue);
			notificationQueue.clear();
			return result;
		}
	}

	@Override
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
