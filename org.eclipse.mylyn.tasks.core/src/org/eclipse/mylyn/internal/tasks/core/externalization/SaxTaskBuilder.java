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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DayDateRange;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.xml.sax.Attributes;

import com.google.common.base.Strings;

public class SaxTaskBuilder extends SaxTaskListElementBuilder<AbstractTask> {

	private AbstractTask task;

	private final RepositoryModel repositoryModel;

	private final IRepositoryManager repositoryManager;

	public SaxTaskBuilder(RepositoryModel repositoryModel, IRepositoryManager repositoryManager) {
		this.repositoryModel = repositoryModel;
		this.repositoryManager = repositoryManager;
	}

	@Override
	public void beginItem(Attributes elementAttributes) {
		try {
			String handle = elementAttributes.getValue(TaskListExternalizationConstants.KEY_HANDLE);
			String taskId = elementAttributes.getValue(TaskListExternalizationConstants.KEY_TASK_ID);
			String repositoryUrl = elementAttributes.getValue(TaskListExternalizationConstants.KEY_REPOSITORY_URL);
			String summary = Strings
					.nullToEmpty(elementAttributes.getValue(TaskListExternalizationConstants.KEY_LABEL));

			// local tasks may not have a connector kind
			String connectorKind = Strings
					.nullToEmpty(elementAttributes.getValue(TaskListExternalizationConstants.KEY_CONNECTOR_KIND));

			if (handle != null) {
				if (taskId == null) {
					taskId = RepositoryTaskHandleUtil.getTaskId(handle);
				}
				if (repositoryUrl == null) {
					repositoryUrl = RepositoryTaskHandleUtil.getRepositoryUrl(handle);
				}
			} else {
				addError(
						new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Task is missing handle attribute")); //$NON-NLS-1$
				return;
			}

			task = createTask(connectorKind, repositoryUrl, taskId, summary);

			// we have to check the task's connector kind as local tasks may not have a connector kind in XML
			if (repositoryManager.getRepositoryConnector(task.getConnectorKind()) == null) {
				addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
						String.format("Unable to read task, missing connector with kind \"%s\"", connectorKind))); //$NON-NLS-1$
				return;
			}

			readTaskInfo(task, elementAttributes);
		} catch (Exception e) {
			addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
					String.format("Exception reading task: %s", e.getMessage()), e)); //$NON-NLS-1$
		}
	}

	private AbstractTask createTask(String connectorKind, String repositoryUrl, String taskId, String summary) {
		AbstractTask task;
		if (repositoryUrl.equals(LocalRepositoryConnector.REPOSITORY_URL)) {
			task = new LocalTask(taskId, summary);
		} else {
			TaskRepository taskRepository = repositoryModel.getTaskRepository(connectorKind, repositoryUrl);
			task = (AbstractTask) repositoryModel.createTask(taskRepository, taskId);
			task.setSummary(summary);
		}
		return task;
	}

	private void readTaskInfo(AbstractTask task, Attributes elementAttributes) {
		String priority = elementAttributes.getValue(TaskListExternalizationConstants.KEY_PRIORITY);
		if (priority != null) {
			task.setPriority(priority);
		} else {
			task.setPriority(TaskListExternalizationConstants.DEFAULT_PRIORITY);
		}

		String kind = elementAttributes.getValue(TaskListExternalizationConstants.KEY_KIND);
		if (kind != null) {
			task.setTaskKind(kind);
		}

		String active = elementAttributes.getValue(TaskListExternalizationConstants.KEY_ACTIVE);
		task.setActive(Boolean.valueOf(active));

		String url = elementAttributes.getValue(TaskListExternalizationConstants.KEY_ISSUEURL);
		task.setUrl(Strings.nullToEmpty(url));

		String notes = elementAttributes.getValue(TaskListExternalizationConstants.KEY_NOTES);
		task.setNotes(Strings.nullToEmpty(notes));

		String estimationString = elementAttributes.getValue(TaskListExternalizationConstants.KEY_TIME_ESTIMATED);
		if (estimationString != null) {
			try {
				int estimate = Integer.parseInt(estimationString);
				task.setEstimatedTimeHours(estimate);
			} catch (Exception e) {
				task.setEstimatedTimeHours(0);
			}
		} else {
			task.setEstimatedTimeHours(0);
		}

		String completionDate = elementAttributes.getValue(TaskListExternalizationConstants.KEY_DATE_END);
		if (completionDate != null) {
			task.setCompletionDate(getDateFromString(completionDate));
		}

		String creationDate = elementAttributes.getValue(TaskListExternalizationConstants.KEY_DATE_CREATION);
		if (creationDate != null) {
			task.setCreationDate(getDateFromString(creationDate));
		}

		String modificationDate = elementAttributes.getValue(TaskListExternalizationConstants.KEY_DATE_MODIFICATION);
		if (modificationDate != null) {
			task.setModificationDate(getDateFromString(modificationDate));
		}

		String dueDate = elementAttributes.getValue(TaskListExternalizationConstants.KEY_DATE_DUE);
		if (dueDate != null) {
			task.setDueDate(getDateFromString(dueDate));
		}

		// Scheduled date range (3.0)
		String scheduledStart = elementAttributes.getValue(TaskListExternalizationConstants.KEY_DATE_SCHEDULED_START);
		String scheduledEnd = elementAttributes.getValue(TaskListExternalizationConstants.KEY_DATE_SCHEDULED_END);
		if (scheduledStart != null && scheduledEnd != null) {
			Date startDate = getDateFromString(scheduledStart);
			Date endDate = getDateFromString(scheduledEnd);
			if (startDate != null && endDate != null && startDate.compareTo(endDate) <= 0) {
				Calendar calStart = TaskActivityUtil.getCalendar();
				calStart.setTime(startDate);
				Calendar calEnd = TaskActivityUtil.getCalendar();
				calEnd.setTime(endDate);
				if (DayDateRange.isDayRange(calStart, calEnd)) {
					task.setScheduledForDate(new DayDateRange(calStart, calEnd));
				} else if (WeekDateRange.isWeekRange(calStart, calEnd)) {
					task.setScheduledForDate(new WeekDateRange(calStart, calEnd));
				} else {
					// Neither week nor day found, default to today
					task.setScheduledForDate(TaskActivityUtil.getDayOf(new Date()));
				}
			}
		}

		String reminded = elementAttributes.getValue(TaskListExternalizationConstants.KEY_REMINDED);
		if (reminded != null) {
			task.setReminded(Boolean.valueOf(reminded));
		}

		String markReadPending = elementAttributes.getValue(TaskListExternalizationConstants.KEY_MARK_READ_PENDING);
		if (markReadPending != null) {
			task.setMarkReadPending(Boolean.valueOf(markReadPending));
		}

		String owner = elementAttributes.getValue(TaskListExternalizationConstants.KEY_OWNER);
		if (owner != null) {
			task.setOwner(owner);
		}

		String ownerId = elementAttributes.getValue(TaskListExternalizationConstants.KEY_OWNER_ID);
		if (ownerId != null) {
			task.setOwnerId(ownerId);
		}

		String notified = elementAttributes.getValue(TaskListExternalizationConstants.KEY_NOTIFIED_INCOMING);
		task.setNotified(Boolean.valueOf(notified));

		String syncStateString = elementAttributes.getValue(TaskListExternalizationConstants.KEY_SYNC_STATE);
		if (syncStateString != null) {
			try {
				SynchronizationState state = SynchronizationState.valueOf(syncStateString);
				task.setSynchronizationState(state);
			} catch (IllegalArgumentException e) {
				// invalid sync state, ignore
				addError(new Status(IStatus.OK, ITasksCoreConstants.ID_PLUGIN,
						String.format("Invalid synchronization state \"%s\"", syncStateString))); //$NON-NLS-1$
			}
		}

		String key = elementAttributes.getValue(TaskListExternalizationConstants.KEY_KEY);
		if (key != null) {
			task.setTaskKey(key);
		}

		task.setSynchronizing(false);
	}

	private Date getDateFromString(String dateString) {
		Date date = null;
		if (Strings.isNullOrEmpty(dateString)) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(TaskListExternalizationConstants.IN_DATE_FORMAT, Locale.ENGLISH);
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			addError(new Status(IStatus.OK, ITasksCoreConstants.ID_PLUGIN,
					String.format("Could not parse date \"%s\"", dateString), e)); //$NON-NLS-1$
		}
		return date;
	}

	@Override
	protected void applyAttribute(String attributeKey, String attributeValue) {
		getItem().setAttribute(attributeKey, attributeValue);
	}

	@Override
	public AbstractTask getItem() {
		return task;
	}

	@Override
	public void addToTaskList(ITransferList taskList) {
		taskList.addTask(task);
	}

}
