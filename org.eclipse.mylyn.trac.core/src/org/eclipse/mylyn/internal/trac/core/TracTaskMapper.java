/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector.TaskKind;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

/**
 * @author Steffen Pingel
 */
public class TracTaskMapper extends TaskMapper {

	private final ITracClient client;

	public TracTaskMapper(TaskData taskData, ITracClient client) {
		super(taskData);
		this.client = client;
	}

	@Override
	public boolean hasChanges(ITask task) {
		boolean changed = false;
		changed |= hasChanges(task.getCompletionDate(), TaskAttribute.DATE_COMPLETION);
		changed |= hasChanges(task.getCreationDate(), TaskAttribute.DATE_CREATION);
		changed |= hasChanges(task.getModificationDate(), TaskAttribute.DATE_MODIFICATION);
		changed |= hasChanges(task.getDueDate(), TaskAttribute.DATE_DUE);
		changed |= hasChanges(task.getOwner(), TaskAttribute.USER_ASSIGNED);
		changed |= hasChanges(task.getPriority(), TaskAttribute.PRIORITY);
		changed |= hasChanges(task.getSummary(), TaskAttribute.SUMMARY);
		changed |= hasChanges(task.getTaskKey(), TaskAttribute.TASK_KEY);
		changed |= hasChanges(task.getTaskKind(), TaskAttribute.TASK_KIND);
		changed |= hasChanges(task.getUrl(), TaskAttribute.TASK_URL);
		return changed;
	}

	private boolean hasChanges(Object value, String attributeKey) {
		TaskData taskData = getTaskData();
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeKey);
		if (attribute != null) {
			if (TaskAttribute.TYPE_BOOLEAN.equals(attribute.getMetaData().getType())) {
				return areNotEquals(value, taskData.getAttributeMapper().getBooleanValue(attribute));
			} else if (TaskAttribute.TYPE_DATE.equals(attribute.getMetaData().getType())) {
				return areNotEquals(value, taskData.getAttributeMapper().getDateValue(attribute));
			} else if (TaskAttribute.TYPE_INTEGER.equals(attribute.getMetaData().getType())) {
				return areNotEquals(value, taskData.getAttributeMapper().getIntegerValue(attribute));
			} else if (TaskAttribute.PRIORITY.equals(attributeKey)) {
				PriorityLevel priorityLevel = getPriorityLevel();
				return areNotEquals(value, (priorityLevel != null) ? priorityLevel.toString() : getPriority());
			} else if (TaskAttribute.TASK_KIND.equals(attributeKey)) {
				return areNotEquals(value, getTaskKind());
			} else {
				return areNotEquals(value, taskData.getAttributeMapper().getValue(attribute));
			}
		}
		return false;
	}

	private boolean areNotEquals(Object existingProperty, Object newProperty) {
		return (existingProperty != null) ? !existingProperty.equals(newProperty) : newProperty != null;
	}

	@Override
	public PriorityLevel getPriorityLevel() {
		if (client != null) {
			String priority = getPriority();
			TracPriority[] tracPriorities = client.getPriorities();
			return TracRepositoryConnector.getTaskPriority(priority, tracPriorities);
		}
		return null;
	}

	@Override
	public String getTaskKind() {
		String tracTaskKind = super.getTaskKind();
		TaskKind taskKind = TaskKind.fromType(tracTaskKind);
		return (taskKind != null) ? taskKind.toString() : tracTaskKind;
	}

}
