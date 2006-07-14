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

package org.eclipse.mylar.internal.tasks.ui.ui;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.mylar.internal.tasks.ui.ui.views.TaskElementLabelProvider;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class TaskListNotificationQueryIncoming implements ITaskListNotification {

	private final AbstractQueryHit hit;

	private DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

	public TaskListNotificationQueryIncoming(AbstractQueryHit hit) {
		this.hit = hit;
	}

	public String getDescription() {
		return hit.getDescription();
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
				TaskUiUtil.refreshAndOpenTaskListElement(hit);
			}
		});

	}

	public Image getNotificationIcon() {
		return labelProvider.getImage(hit);
	}

	public boolean equals(Object o) {
		if (!(o instanceof TaskListNotificationQueryIncoming)) {
			return false;
		}
		TaskListNotificationQueryIncoming notification = (TaskListNotificationQueryIncoming) o;
		return notification.getDescription().equals(hit.getDescription());
	}

	public int hashCode() {
		return hit.getDescription().hashCode();
	}

	public Image getOverlayIcon() {
		return TaskListImages.getImage(TaskListImages.OVERLAY_INCOMMING);
	}

}
