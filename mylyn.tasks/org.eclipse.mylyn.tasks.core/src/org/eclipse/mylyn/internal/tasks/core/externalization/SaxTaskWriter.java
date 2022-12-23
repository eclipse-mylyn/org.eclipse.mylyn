/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.xml.sax.SAXException;

public class SaxTaskWriter extends SaxTaskListElementWriter<AbstractTask> {

	public SaxTaskWriter(ContentHandlerWrapper handler) {
		super(handler);
	}

	@Override
	public void writeElement(AbstractTask task) throws SAXException {
		if (task.getClass() == TaskTask.class || task instanceof LocalTask) {
			handler.startElement(TaskListExternalizationConstants.NODE_TASK, createTaskElementAttributes(task));
			writeAttributes(task);
			for (ITask subTask : task.getChildren()) {
				createTaskReference(subTask);
			}
			handler.endElement(TaskListExternalizationConstants.NODE_TASK);
		} else {
			addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
					String.format("Unable to externalize task \"%s\" as it is of an unsupported type %s", //$NON-NLS-1$
							task.getTaskId(), task.getClass())));
		}
	}

	private AttributesWrapper createTaskElementAttributes(AbstractTask task) {
		AttributesWrapper attributes = new AttributesWrapper();

		attributes.addAttribute(TaskListExternalizationConstants.KEY_CONNECTOR_KIND, task.getConnectorKind());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_REPOSITORY_URL, task.getRepositoryUrl());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_TASK_ID, task.getTaskId());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_KEY, task.getTaskKey());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_HANDLE, task.getHandleIdentifier());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_LABEL, stripControlCharacters(task.getSummary()));
		attributes.addAttribute(TaskListExternalizationConstants.KEY_PRIORITY, task.getPriority());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_KIND, task.getTaskKind());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_ISSUEURL, task.getUrl());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_NOTES, stripControlCharacters(task.getNotes()));
		attributes.addAttribute(TaskListExternalizationConstants.KEY_TIME_ESTIMATED,
				Integer.toString(task.getEstimatedTimeHours()));
		attributes.addAttribute(TaskListExternalizationConstants.KEY_DATE_END,
				formatExternDate(task.getCompletionDate()));
		attributes.addAttribute(TaskListExternalizationConstants.KEY_DATE_CREATION,
				formatExternDate(task.getCreationDate()));
		attributes.addAttribute(TaskListExternalizationConstants.KEY_DATE_MODIFICATION,
				formatExternDate(task.getModificationDate()));
		attributes.addAttribute(TaskListExternalizationConstants.KEY_DATE_DUE, formatExternDate(task.getDueDate()));
		attributes.addAttribute(TaskListExternalizationConstants.KEY_OWNER, task.getOwner());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_OWNER_ID, task.getOwnerId());

		if (task.isActive()) {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_ACTIVE,
					TaskListExternalizationConstants.VAL_TRUE);
		} else {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_ACTIVE,
					TaskListExternalizationConstants.VAL_FALSE);
		}

		if (task.getScheduledForDate() != null) {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_DATE_SCHEDULED_START,
					formatExternCalendar(task.getScheduledForDate().getStartDate()));
			attributes.addAttribute(TaskListExternalizationConstants.KEY_DATE_SCHEDULED_END,
					formatExternCalendar(task.getScheduledForDate().getEndDate()));
		}

		if (task.isReminded()) {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_REMINDED,
					TaskListExternalizationConstants.VAL_TRUE);
		} else {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_REMINDED,
					TaskListExternalizationConstants.VAL_FALSE);
		}

		if (task.isMarkReadPending()) {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_MARK_READ_PENDING,
					TaskListExternalizationConstants.VAL_TRUE);
		} else {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_MARK_READ_PENDING,
					TaskListExternalizationConstants.VAL_FALSE);
		}

		if (task.isNotified()) {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_NOTIFIED_INCOMING,
					TaskListExternalizationConstants.VAL_TRUE);
		} else {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_NOTIFIED_INCOMING,
					TaskListExternalizationConstants.VAL_FALSE);
		}

		if (task.getSynchronizationState() != null) {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_SYNC_STATE,
					task.getSynchronizationState().name());
		} else {
			attributes.addAttribute(TaskListExternalizationConstants.KEY_SYNC_STATE,
					SynchronizationState.SYNCHRONIZED.name());
		}

		return attributes;
	}

	public void createTaskReference(ITask task) throws SAXException {
		AttributesWrapper attributes = new AttributesWrapper();
		attributes.addAttribute(TaskListExternalizationConstants.KEY_HANDLE, task.getHandleIdentifier());
		handler.startElement(TaskListExternalizationConstants.NODE_SUB_TASK, attributes);
		handler.endElement(TaskListExternalizationConstants.NODE_SUB_TASK);
	}
}
