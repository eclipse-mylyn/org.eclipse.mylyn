/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tasks.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
// TODO EDITOR return null if attribute value invalid for primitive types? 
public class TaskAttributeMapper {

	private final TaskRepository taskRepository;

	public TaskAttributeMapper(TaskRepository taskRepository) {
		Assert.isNotNull(taskRepository);
		this.taskRepository = taskRepository;
	}

	public TaskAttribute createTaskAttachment(TaskData taskData) {
		TaskAttribute taskAttribute = taskData.getRoot().createAttribute(
				mapToRepositoryKey(taskData.getRoot(), TaskAttribute.NEW_ATTACHMENT));
		TaskAttachmentMapper.createFrom(taskAttribute);
		return taskAttribute;
	}

	public boolean equals(TaskAttribute newAttribute, TaskAttribute oldAttribute) {
		return newAttribute.getValues().equals(oldAttribute.getValues());
	}

	public TaskAttribute getAssoctiatedAttribute(TaskAttribute taskAttribute) {
		String id = taskAttribute.getMetaData(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID);
		if (id != null) {
			if (TaskAttribute.TYPE_OPERATION.equals(TaskAttributeProperties.from(taskAttribute).getType())) {
				return taskAttribute.getTaskData().getRoot().getAttribute(id);
			}
			return taskAttribute.getAttribute(id);
		}
		return null;
	}

	public TaskAttribute[] getAttributesByType(TaskData taskData, String type) {
		Assert.isNotNull(taskData);
		Assert.isNotNull(type);
		List<TaskAttribute> result = new ArrayList<TaskAttribute>();
		for (TaskAttribute taskAttribute : taskData.getRoot().getAttributes().values()) {
			if (type.equals(taskAttribute.getProperties().getType())) {
				result.add(taskAttribute);
			}
		}
		return result.toArray(new TaskAttribute[0]);
	}

	public boolean getBooleanValue(TaskAttribute attribute) {
		String booleanString = attribute.getValue();
		if (booleanString != null && booleanString.length() > 0) {
			return Boolean.parseBoolean(booleanString);
		}
		return false;
	}

	public Date getDateValue(TaskAttribute attribute) {
		String dateString = attribute.getValue();
		try {
			if (dateString != null && dateString.length() > 0) {
				return new Date(Long.parseLong(dateString));
			}
		} catch (NumberFormatException e) {
			// ignore
		}
		return null;
	}

	public String getDefaultOption(TaskAttribute taskAttribute) {
		return TaskAttributeProperties.from(taskAttribute).getDefaultOption();
	}

	public Integer getIntegerValue(TaskAttribute attribute) {
		String integerString = attribute.getValue();
		try {
			if (integerString != null) {
				return Integer.parseInt(integerString);
			}
		} catch (NumberFormatException e) {
			// ignore
		}
		return null;
	}

	public String getLabel(TaskAttribute taskAttribute) {
		return TaskAttributeProperties.from(taskAttribute).getLabel();
	}

	public Long getLongValue(TaskAttribute attribute) {
		String longString = attribute.getValue();
		try {
			if (longString != null) {
				return Long.parseLong(longString);
			}
		} catch (NumberFormatException e) {
			// ignore
		}
		return null;
	}

	/**
	 * Returns labelByValue.
	 */
	public Map<String, String> getOptions(TaskAttribute attribute) {
		return attribute.getOptions();
	}

	public IRepositoryPerson getRepositoryPerson(TaskAttribute taskAttribute) {
		IRepositoryPerson person = taskRepository.createPerson(taskAttribute.getValue());
		TaskAttribute child = taskAttribute.getMappedAttribute(TaskAttribute.PERSON_NAME);
		if (child != null) {
			person.setName(getValue(child));
		}
		return person;
	}

	public TaskOperation getTaskOperation(TaskAttribute taskAttribute) {
		return TaskOperation.createFrom(taskAttribute);
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public String getType(TaskAttribute taskAttribute) {
		return TaskAttributeProperties.from(taskAttribute).getType();
	}

	public String getValue(TaskAttribute taskAttribute) {
		return taskAttribute.getValue();
	}

	public String getValueLabel(TaskAttribute taskAttribute) {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String value : taskAttribute.getValues()) {
			String option = taskAttribute.getOption(value);
			if (option != null) {
				value = option;
			}
			sb.append(sep).append(value);
			sep = ", ";
		}
		return sb.toString();
	}

	public String[] getValueLabels(TaskAttribute taskAttribute) {
		List<String> values = taskAttribute.getValues();
		List<String> result = new ArrayList<String>(values.size());
		for (String value : values) {
			String option = taskAttribute.getOption(value);
			if (option != null) {
				value = option;
			}
			result.add(value);
		}
		return result.toArray(new String[0]);
	}

	public String[] getValues(TaskAttribute attribute) {
		return attribute.getValues().toArray(new String[0]);
	}

	public boolean hasValue(TaskAttribute attribute) {
		return attribute.getValues().size() > 0;
	}

	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		return key;
	}

	public void setBooleanValue(TaskAttribute attribute, Boolean value) {
		attribute.setValue(Boolean.toString(value));
	}

	public void setDateValue(TaskAttribute attribute, Date date) {
		if (date != null) {
			attribute.setValue(Long.toString(date.getTime()));
		} else {
			attribute.clearValues();
		}
	}

	public void setIntegerValue(TaskAttribute attribute, Integer value) {
		if (value != null) {
			attribute.setValue(value.toString());
		} else {
			attribute.clearValues();
		}
	}

	public void setLongValue(TaskAttribute attribute, Long value) {
		if (value != null) {
			attribute.setValue(value.toString());
		} else {
			attribute.clearValues();
		}
	}

	public void setRepositoryPerson(TaskAttribute taskAttribute, IRepositoryPerson person) {
		setValue(taskAttribute, person.getPersonId());
		if (person.getName() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.PERSON_NAME);
			setValue(child, person.getName());
		}
	}

	public void setTaskOperation(TaskAttribute taskAttribute, TaskOperation taskOperation) {
		taskOperation.applyTo(taskAttribute);
	}

	public void setValue(TaskAttribute attribute, String value) {
		attribute.setValue(value);
	}

	public void setValues(TaskAttribute attribute, String[] values) {
		attribute.setValues(Arrays.asList(values));
	}

	public void updateTaskAttachment(ITaskAttachment taskAttachment, TaskAttribute taskAttribute) {
		TaskAttachmentMapper.createFrom(taskAttribute).applyTo(taskAttachment);
	}

	public void updateTaskComment(ITaskComment taskComment, TaskAttribute taskAttribute) {
		TaskCommentMapper.createFrom(taskAttribute).applyTo(taskComment);
	}

}
