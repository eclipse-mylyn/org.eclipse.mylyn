/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class DefaultTaskSchema {

	private final RepositoryTaskData taskData;

	public DefaultTaskSchema(RepositoryTaskData taskData) {
		Assert.isNotNull(taskData);

		this.taskData = taskData;
	}

	public boolean applyTo(AbstractTask task) {
		boolean changed = false;
		if (hasTaskPropertyChanged(task.getCompletionDate(), getCompletionDate())) {
			task.setCompletionDate(getCompletionDate());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getSummary(), getSummary())) {
			task.setSummary(getSummary());
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
		if (hasTaskPropertyChanged(task.getUrl(), getTaskUrl())) {
			task.setUrl(getTaskUrl());
			changed = true;
		}
		if (hasTaskPropertyChanged(task.getTaskKind(), getTaskKind())) {
			task.setTaskKind(getTaskKind());
			changed = true;
		}
		return changed;
	}

	public boolean getBooleanValue(String attributeKey) {
		RepositoryTaskAttribute attribute = taskData.getAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeFactory().getAttributeMapper().getBooleanValue(attribute);
		}
		return false;
	}

	public String getComponent() {
		return getValue(RepositoryTaskAttribute.COMPONENT);
	}

	public Date getCreationDate() {
		return getDateValue(RepositoryTaskAttribute.DATE_CREATION);
	}

	public Date getCompletionDate() {
		return getDateValue(RepositoryTaskAttribute.DATE_COMPLETION);
	}

	public Date getModificationDate() {
		return getDateValue(RepositoryTaskAttribute.DATE_MODIFIED);
	}

	private Date getDateValue(String attributeKey) {
		RepositoryTaskAttribute attribute = taskData.getAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeFactory().getAttributeMapper().getDateValue(attribute);
		}
		return null;
	}

	public String getDescription() {
		return getValue(RepositoryTaskAttribute.DESCRIPTION);
	}

	public Date getDueDate() {
		return getDateValue(RepositoryTaskAttribute.DATE_DUE);
	}

	public String getOwner() {
		return getValue(RepositoryTaskAttribute.USER_OWNER);
	}

	public PriorityLevel getPriority() {
		return PriorityLevel.fromString(getValue(RepositoryTaskAttribute.PRIORITY));
	}

	public String getProduct() {
		return getValue(RepositoryTaskAttribute.PRODUCT);
	}

	public String getSummary() {
		return getValue(RepositoryTaskAttribute.SUMMARY);
	}

	public String getTaskKind() {
		return taskData.getTaskKind();
	}

	public String getTaskUrl() {
		return getValue(RepositoryTaskAttribute.TASK_URL);
	}

	public String getValue(String attributeKey) {
		RepositoryTaskAttribute attribute = taskData.getAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeFactory().getAttributeMapper().getValue(attribute);
		}
		return null;
	}

	protected final boolean hasTaskPropertyChanged(Object existingProperty, Object newProperty) {
		// the query hit does not have this property
		if (newProperty == null) {
			return false;
		}
		return (existingProperty == null) ? true : !existingProperty.equals(newProperty);
	}

	public RepositoryTaskAttribute setBooleanValue(String attributeKey, boolean value) {
		RepositoryTaskAttribute attribute = taskData.getAttribute(attributeKey);
		if (attribute == null) {
			attribute = new RepositoryTaskAttribute(attributeKey, null, false);
			taskData.addAttribute(attributeKey, attribute);
		}

		AbstractAttributeMapper attributeMapper = taskData.getAttributeFactory().getAttributeMapper();
		if (attributeMapper != null) {
			attributeMapper.setBooleanValue(attribute, value);
		} else {
			attribute.setValue(value + "");
		}
		return attribute;
	}

	public void setComponent(String component) {
		setValue(RepositoryTaskAttribute.COMPONENT, component);
	}

	public void setCreationDate(Date dateCreated) {
		setDateValue(RepositoryTaskAttribute.DATE_CREATION, dateCreated);
	}

	public void setCompletionDate(Date dateCompleted) {
		setDateValue(RepositoryTaskAttribute.DATE_COMPLETION, dateCompleted);
	}

	public void setModificationDate(Date dateModified) {
		setDateValue(RepositoryTaskAttribute.DATE_MODIFIED, dateModified);
	}

	private RepositoryTaskAttribute setDateValue(String attributeKey, Date value) {
		RepositoryTaskAttribute attribute = taskData.getAttribute(attributeKey);
		if (attribute == null) {
			attribute = new RepositoryTaskAttribute(attributeKey, null, false);
			taskData.addAttribute(attributeKey, attribute);
		}
		taskData.getAttributeFactory().getAttributeMapper().setDateValue(attribute, value);
		return attribute;
	}

	public void setDescription(String description) {
		setValue(RepositoryTaskAttribute.DESCRIPTION, description);
	}

	public void setDueDate(Date value) {
		setDateValue(RepositoryTaskAttribute.DATE_DUE, value);
	}

	// TODO use Person class?
	public void setOwner(String owner) {
		setValue(RepositoryTaskAttribute.USER_OWNER, owner);
	}

	public void setPriority(PriorityLevel priority) {
		setValue(RepositoryTaskAttribute.PRIORITY, priority.toString());
	}

	public void setProduct(String product) {
		setValue(RepositoryTaskAttribute.PRODUCT, product);
	}

	// TODO use Person class?
	public void setReporter(String reporter) {
		setValue(RepositoryTaskAttribute.USER_REPORTER, reporter);
	}

	public void setSummary(String summary) {
		setValue(RepositoryTaskAttribute.SUMMARY, summary);
	}

	public void setTaskKind(String taskKind) {
		taskData.setTaskKind(taskKind);
	}

	public void setTaskUrl(String taskUrl) {
		setValue(RepositoryTaskAttribute.TASK_URL, taskUrl);
	}

	public RepositoryTaskAttribute setValue(String attributeKey, String value) {
		RepositoryTaskAttribute attribute = taskData.getAttribute(attributeKey);
		if (attribute == null) {
			attribute = new RepositoryTaskAttribute(attributeKey, null, false);
			taskData.addAttribute(attributeKey, attribute);
		}

		AbstractAttributeMapper attributeMapper = taskData.getAttributeFactory().getAttributeMapper();
		if (attributeMapper != null) {
			attributeMapper.setValue(attribute, value);
		} else {
			attribute.setValue(value);
		}
		return attribute;
	}

}
