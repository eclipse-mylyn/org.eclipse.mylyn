/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskMapper implements ITaskMapping {

	private final TaskData taskData;

	public TaskMapper(TaskData taskData) {
		Assert.isNotNull(taskData);
		this.taskData = taskData;
	}

	public boolean applyTo(ITask task) {
		boolean changed = false;
		if (hasTaskPropertyChanged(task.getCompletionDate(), getCompletionDate())) {
			task.setCompletionDate(getCompletionDate());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getCreationDate(), getCreationDate())) {
			task.setCreationDate(getCreationDate());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getModificationDate(), getModificationDate())) {
			task.setModificationDate(getModificationDate());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getDueDate(), getDueDate())) {
			task.setDueDate(getDueDate());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getOwner(), getOwner())) {
			task.setOwner(getOwner());
			changed = true;
		}
		if (getPriority() != null && hasTaskPropertyChanged(task.getPriority(), getPriority().toString())) {
			task.setPriority(getPriority().toString());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getSummary(), getSummary())) {
			task.setSummary(getSummary());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getTaskKind(), getTaskKind())) {
			task.setTaskKind(getTaskKind());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getUrl(), getTaskUrl())) {
			task.setUrl(getTaskUrl());
			changed = true;
		}
		// TODO task key
		return changed;
	}

	protected boolean getBooleanValue(String attributeKey) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeMapper().getBooleanValue(attribute);
		}
		return false;
	}

	public Date getCompletionDate() {
		return getDateValue(TaskAttribute.DATE_COMPLETION);
	}

	public String getComponent() {
		return getValue(TaskAttribute.COMPONENT);
	}

	public Date getCreationDate() {
		return getDateValue(TaskAttribute.DATE_CREATION);
	}

	protected Date getDateValue(String attributeKey) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeMapper().getDateValue(attribute);
		}
		return null;
	}

	public String getDescription() {
		return getValue(TaskAttribute.DESCRIPTION);
	}

	public Date getDueDate() {
		return getDateValue(TaskAttribute.DATE_DUE);
	}

	public Date getModificationDate() {
		return getDateValue(TaskAttribute.DATE_MODIFIED);
	}

	public String getOwner() {
		return getValue(TaskAttribute.USER_OWNER);
	}

	public PriorityLevel getPriority() {
		String value = getValue(TaskAttribute.PRIORITY);
		return (value != null) ? PriorityLevel.fromString(value) : null;
	}

	public String getProduct() {
		return getValue(TaskAttribute.PRODUCT);
	}

	public String getSummary() {
		return getValue(TaskAttribute.SUMMARY);
	}

	public TaskData getTaskData() {
		return taskData;
	}

	public String getTaskKey() {
		return getValue(TaskAttribute.TASK_KEY);
	}

	public String getTaskKind() {
		return getValue(TaskAttribute.TASK_KIND);
	}

	public String getTaskUrl() {
		return getValue(TaskAttribute.TASK_URL);
	}

	public String getValue(String attributeKey) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeMapper().getValue(attribute);
		}
		return null;
	}

	protected boolean hasTaskPropertyChanged(Object existingProperty, Object newProperty) {
		// the query hit does not have this property
		if (newProperty == null) {
			return false;
		}
		return (existingProperty == null) ? true : !existingProperty.equals(newProperty);
	}

	public TaskAttribute setBooleanValue(String attributeKey, boolean value) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attributeKey);
		if (attribute == null) {
			attribute = taskData.getRoot().createAttribute(attributeKey);
		}
		taskData.getAttributeMapper().setBooleanValue(attribute, value);
		return attribute;
	}

	public void setCompletionDate(Date dateCompleted) {
		setDateValue(TaskAttribute.DATE_COMPLETION, dateCompleted);
	}

	public void setComponent(String component) {
		setValue(TaskAttribute.COMPONENT, component);
	}

	public void setCreationDate(Date dateCreated) {
		setDateValue(TaskAttribute.DATE_CREATION, dateCreated);
	}

	protected TaskAttribute setDateValue(String attributeKey, Date value) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attributeKey);
		if (attribute == null) {
			attribute = taskData.getRoot().createAttribute(attributeKey);
		}
		taskData.getAttributeMapper().setDateValue(attribute, value);
		return attribute;
	}

	public void setDescription(String description) {
		setValue(TaskAttribute.DESCRIPTION, description);
	}

	public void setDueDate(Date value) {
		setDateValue(TaskAttribute.DATE_DUE, value);
	}

	public void setModificationDate(Date dateModified) {
		setDateValue(TaskAttribute.DATE_MODIFIED, dateModified);
	}

	// TODO use Person class?
	public void setOwner(String owner) {
		setValue(TaskAttribute.USER_OWNER, owner);
	}

	public void setPriority(PriorityLevel priority) {
		setValue(TaskAttribute.PRIORITY, priority.toString());
	}

	public void setProduct(String product) {
		setValue(TaskAttribute.PRODUCT, product);
	}

	// TODO use Person class?
	public void setReporter(String reporter) {
		setValue(TaskAttribute.USER_REPORTER, reporter);
	}

	public void setSummary(String summary) {
		setValue(TaskAttribute.SUMMARY, summary);
	}

	public void setTaskKind(String taskKind) {
		setValue(TaskAttribute.TASK_KIND, taskKind);
	}

	public void setTaskUrl(String taskUrl) {
		setValue(TaskAttribute.TASK_URL, taskUrl);
	}

	protected TaskAttribute setValue(String attributeKey, String value) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attributeKey);
		if (attribute == null) {
			attribute = taskData.getRoot().createAttribute(attributeKey);
		}
		taskData.getAttributeMapper().setValue(attribute, value);
		return attribute;
	}

}
