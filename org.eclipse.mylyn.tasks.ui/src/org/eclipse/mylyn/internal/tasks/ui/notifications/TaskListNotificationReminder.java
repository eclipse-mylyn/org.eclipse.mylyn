/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.util.Date;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.swt.graphics.Image;

/**
 * @author Rob Elves
 */
public class TaskListNotificationReminder extends TaskListNotification {

	private Date date;

	public TaskListNotificationReminder(AbstractTask task) {
		super(task);
	}

	public Image getNotificationKindImage() {
		return TasksUiImages.getImage(TasksUiImages.OVERLAY_HAS_DUE);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TaskListNotificationReminder)) {
			return false;
		}
		TaskListNotificationReminder notification = (TaskListNotificationReminder) o;
		return notification.getTask().equals(task);
	}

	@Override
	public int hashCode() {
		return task.hashCode();
	}

	public int compareTo(AbstractNotification anotherNotification) throws ClassCastException {
		if (!(anotherNotification != null))
			throw new ClassCastException("A ITaskListNotification object expected.");
		Date anotherDate = (anotherNotification).getDate();
		if (date != null && anotherDate != null) {
			return date.compareTo(anotherDate);
		} else if (date == null) {
			return -1;
		} else {
			return 1;
		}
	}
}
