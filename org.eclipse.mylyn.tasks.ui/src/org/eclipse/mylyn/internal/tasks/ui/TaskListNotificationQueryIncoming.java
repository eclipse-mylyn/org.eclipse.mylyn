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
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class TaskListNotificationQueryIncoming implements ITaskListNotification {

	private final AbstractTask hit;

	private DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(true),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

	private Date date;

	public TaskListNotificationQueryIncoming(AbstractTask hit) {
		this.hit = hit;
	}

	public String getDescription() {
		return hit.getSummary();
	}

	public String getLabel() {
		if (labelProvider.getText(hit).length() > 40) {
			String truncated = labelProvider.getText(hit).substring(0, 35);
			return truncated + "...";
		}
		return labelProvider.getText(hit);
	}

	public void openTask() {

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				TasksUiUtil.refreshAndOpenTaskListElement(hit);
			}
		});

	}

	public Image getNotificationIcon() {
		return labelProvider.getImage(hit);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TaskListNotificationQueryIncoming)) {
			return false;
		}
		TaskListNotificationQueryIncoming notification = (TaskListNotificationQueryIncoming) o;
		return notification.getDescription().equals(hit.getSummary());
	}

	@Override
	public int hashCode() {
		return hit.getSummary().hashCode();
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
