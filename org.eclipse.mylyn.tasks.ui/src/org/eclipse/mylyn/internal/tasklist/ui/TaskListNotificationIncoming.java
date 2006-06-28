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

package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskElementLabelProvider;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class TaskListNotificationIncoming implements ITaskListNotification {

	private final AbstractRepositoryTask task;
	
	private String description = null;

	private DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

	public TaskListNotificationIncoming(AbstractRepositoryTask task) {
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
				TaskUiUtil.refreshAndOpenTaskListElement(task);
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

	public boolean equals(Object o) {
		if (!(o instanceof TaskListNotificationIncoming)) {
			return false;
		}
		TaskListNotificationIncoming notification = (TaskListNotificationIncoming) o;
		return notification.getTask().equals(task);
	}

	private ITask getTask() {
		return task;
	}

	public int hashCode() {
		return task.hashCode();
	}

	public Image getOverlayIcon() {
		return TaskListImages.getImage(TaskListImages.OVERLAY_INCOMMING);
	}

}
