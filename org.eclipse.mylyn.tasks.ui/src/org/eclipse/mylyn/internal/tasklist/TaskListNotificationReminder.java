/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist;

import org.eclipse.mylar.internal.tasklist.ui.TaskListUiUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

public class TaskListNotificationReminder implements ITaskListNotification {

	private final ITask task;

	private boolean notified = false;

	public TaskListNotificationReminder(ITask task) {
		this.task = task;
	}

	private Image getIcon() {
		return task.getIcon();
	}

	public String getDescription() {
		if (task.getDescription().length() > 40) {
			String truncated = task.getDescription().substring(0, 35);
			return truncated + "...";
		}
		return task.getDescription();

	}

	public String getToolTip() {
		return null;
	}

	public void openResource() {
		
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				TaskListUiUtil.openEditor(task);
			}
		});

	}

	public Image getNotificationIcon() {
		Image taskImage = getIcon();
		// TODO: overlay with REMINDER decorator
		return taskImage;
	}

	public void setNotified(boolean notified) {
		task.setReminded(true);
		this.notified = notified;

	}

	public boolean isNotified() {
		return notified;
	}

	/**
	 * equality based on tasks' equality (handle)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof TaskListNotificationReminder)) {
			return false;
		}
		TaskListNotificationReminder notification = (TaskListNotificationReminder) o;
		return notification.getTask().equals(task);		
	}

	private ITask getTask() {
		return task;
	}

	public int hashCode() {
		return task.hashCode();
	}

}
