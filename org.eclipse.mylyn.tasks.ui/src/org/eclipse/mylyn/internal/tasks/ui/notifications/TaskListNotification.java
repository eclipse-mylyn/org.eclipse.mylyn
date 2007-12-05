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
 * @author Mik Kersten
 */
public class TaskListNotification extends AbstractNotification {

	protected final AbstractTask task;

	protected Date date;
	
	private String description = null;

	private String details = null;
	
	private DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(true),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

	public TaskListNotification(AbstractTask task) {
		this.task = task;
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return labelProvider.getText(task);
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
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

//
//	public synchronized void setNotified(boolean notified) {
//		task.setNotified(true);
//	}
//
//	public synchronized boolean isNotified() {
//		return task.isNotified();
//	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TaskListNotification)) {
			return false;
		}
		TaskListNotification notification = (TaskListNotification) o;
		return notification.getTask().equals(task);
	}

	protected AbstractTask getTask() {
		return task;
	}

	@Override
	public int hashCode() {
		return task.hashCode();
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
