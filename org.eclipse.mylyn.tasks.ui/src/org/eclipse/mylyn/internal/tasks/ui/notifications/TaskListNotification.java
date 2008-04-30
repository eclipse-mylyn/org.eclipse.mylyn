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
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotification;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
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

	private final DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(
			new TaskElementLabelProvider(true), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

	public TaskListNotification(AbstractTask task) {
		this.task = task;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getLabel() {
		return labelProvider.getText(task);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void open() {

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				TasksUiUtil.refreshAndOpenTaskListElement(task);
			}
		});
	}

	@Override
	public Image getNotificationImage() {
		return labelProvider.getImage(task);
	}

	protected AbstractTask getTask() {
		return task;
	}

	@Override
	public Image getNotificationKindImage() {
		if (task != null && task.getLastReadTimeStamp() == null) {
			return CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING_NEW);
		} else {
			return CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING);
		}
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	public int compareTo(AbstractNotification anotherNotification) throws ClassCastException {
		if (!(anotherNotification != null)) {
			throw new ClassCastException("A ITaskListNotification object expected.");
		}
		Date anotherDate = (anotherNotification).getDate();
		if (date != null && anotherDate != null) {
			return date.compareTo(anotherDate);
		} else if (date == null) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaskListNotification other = (TaskListNotification) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (task == null) {
			if (other.task != null) {
				return false;
			}
		} else if (!task.equals(other.task)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == AbstractTask.class) {
			return task;
		}
		return null;
	}
}
