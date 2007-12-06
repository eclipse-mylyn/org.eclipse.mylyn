/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.util.Date;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class TaskListNotificationQueryIncoming extends TaskListNotification {

	private DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(true),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

	public TaskListNotificationQueryIncoming(AbstractTask task) {
		super(task);
	}

	public String getDescription() {
		return task.getSummary();
	}

	public void open() {

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				TasksUiUtil.refreshAndOpenTaskListElement(task);
			}
		});

	}

	public Image getNotificationImage() {
		return labelProvider.getImage(task);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TaskListNotificationQueryIncoming)) {
			return false;
		}
		TaskListNotificationQueryIncoming notification = (TaskListNotificationQueryIncoming) o;
		return notification.getDescription().equals(task.getSummary());
	}

	@Override
	public int hashCode() {
		return task.getSummary().hashCode();
	}

	public Image getNotificationKindImage() {
		return TasksUiImages.getImage(TasksUiImages.OVERLAY_INCOMMING);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
