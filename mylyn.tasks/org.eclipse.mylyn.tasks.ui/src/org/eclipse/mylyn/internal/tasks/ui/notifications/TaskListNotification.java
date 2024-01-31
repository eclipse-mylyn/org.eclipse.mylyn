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

import java.util.Date;
import java.util.Objects;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskListNotification extends AbstractUiNotification {

	private final static String ID_EVENT_TASK_CHANGED = "org.eclipse.mylyn.tasks.ui.events.TaskChanged"; //$NON-NLS-1$

	private static DecoratingLabelProvider labelProvider;

	protected final ITask task;

	protected Date date;

	private String description;

	private final Object token;

	public TaskListNotification(ITask task) {
		this(ID_EVENT_TASK_CHANGED, task, null);
	}

	public TaskListNotification(String eventId, ITask task) {
		this(eventId, task, null);
	}

	public TaskListNotification(ITask task, Object token) {
		this(ID_EVENT_TASK_CHANGED, task, token);
	}

	public TaskListNotification(String eventId, ITask task, Object token) {
		super(eventId);
		Assert.isNotNull(task);
		this.task = task;
		this.token = token;
	}

	private LabelProvider getLabelProvider() {
		// lazily instantiate on UI thread
		if (labelProvider == null) {
			labelProvider = new DecoratingLabelProvider(new TaskElementLabelProvider(true),
					PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
		}
		return labelProvider;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getLabel() {
		return getLabelProvider().getText(task);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void open() {
		PlatformUI.getWorkbench().getDisplay().syncExec(() -> TasksUiInternal.refreshAndOpenTaskListElement(task));
	}

	@Override
	public Image getNotificationImage() {
		return getLabelProvider().getImage(task);
	}

	protected ITask getTask() {
		return task;
	}

	@Override
	public Image getNotificationKindImage() {
		if (task.getSynchronizationState() == SynchronizationState.INCOMING_NEW) {
			return CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING_NEW);
		} else if (task.getSynchronizationState() == SynchronizationState.OUTGOING_NEW) {
			return CommonImages.getImage(CommonImages.OVERLAY_SYNC_OUTGOING_NEW);
		} else {
			return CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING);
		}
	}

	@Override
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int compareTo(AbstractNotification anotherNotification) throws ClassCastException {
		Date anotherDate = anotherNotification.getDate();
		if (date != null && anotherDate != null) {
			return date.compareTo(anotherDate) * -1;
		} else if (date == null) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, description, task);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		TaskListNotification other = (TaskListNotification) obj;
		if (!Objects.equals(date, other.date)) {
			return false;
		}
		if (!Objects.equals(description, other.description)) {
			return false;
		}
		if (!Objects.equals(task, other.task)) {
			return false;
		}
		return true;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == AbstractTask.class) {
			return task;
		}
		return null;
	}

	@Override
	public Object getToken() {
		return token;
	}

}
