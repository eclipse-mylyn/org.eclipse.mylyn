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

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Date;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class TaskListNotificationIncoming implements ITaskListNotification {

	private final AbstractTask task;
	
	private String description = null;

	private DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

	private Date date;

	public TaskListNotificationIncoming(AbstractTask task) {
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

	public void openTask() {

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				TasksUiUtil.refreshAndOpenTaskListElement(task);
			}
		});

	}

	public Image getNotificationIcon() {
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
		if (!(o instanceof TaskListNotificationIncoming)) {
			return false;
		}
		TaskListNotificationIncoming notification = (TaskListNotificationIncoming) o;
		return notification.getTask().equals(task);
	}

	private AbstractTask getTask() {
		return task;
	}

	@Override
	public int hashCode() {
		return task.hashCode();
	}

	public Image getOverlayIcon() {
		return TasksUiImages.getImage(TasksUiImages.OVERLAY_INCOMMING);
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {	
		this.date = date;
	}
	
	public int compareTo(ITaskListNotification anotherNotification) throws ClassCastException {
	    if (!(anotherNotification instanceof ITaskListNotification))
	      throw new ClassCastException("A ITaskListNotification object expected.");
	    Date anotherDate = (anotherNotification).getDate();
	    if(date != null && anotherDate != null) {
	    	return date.compareTo(anotherDate);
	    } else if(date == null) {
	    	return -1;
	    } else {
	    	return 1;
	    }    
	  }
}
