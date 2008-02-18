/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;

/**
 * @author Steffen Pingel
 * 
 * TODO url, due date, completed
 */
public class DefaultTaskSchema {

	private final RepositoryTaskData taskData;

	public DefaultTaskSchema(RepositoryTaskData taskData) {
		Assert.isNotNull(taskData);
			
		this.taskData = taskData;
	}

	public String getComponent() {
		return getValue(RepositoryTaskAttribute.COMPONENT);
	}

	public Date getDateCreated() {
		return getDateValue(RepositoryTaskAttribute.DATE_CREATION);
	}

	public Date getDateModified() {
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

	private String getValue(String attributeKey) {
		RepositoryTaskAttribute attribute = taskData.getAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeFactory().getAttributeMapper().getValue(attribute);
		}
		return null;
	}

	public void setComponent(String component) {
		setValue(RepositoryTaskAttribute.COMPONENT, component);
	}

	public void setDateCreated(Date dateCreated) {
		setDateValue(RepositoryTaskAttribute.DATE_CREATION, dateCreated);
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

	private RepositoryTaskAttribute setValue(String attributeKey, String value) {
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
