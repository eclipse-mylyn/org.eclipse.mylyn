/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskAttributeMapper {

	private final TaskRepository taskRepository;

	public TaskAttributeMapper(@NonNull TaskRepository taskRepository) {
		Assert.isNotNull(taskRepository);
		this.taskRepository = taskRepository;
	}

	@NonNull
	public TaskAttribute createTaskAttachment(@NonNull TaskData taskData) {
		TaskAttribute taskAttribute = taskData.getRoot()
				.createAttribute(mapToRepositoryKey(taskData.getRoot(), TaskAttribute.NEW_ATTACHMENT));
//		TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(taskAttribute);
//		mapper.setContentType("");
//		mapper.setFileName("");
//		mapper.setContentType("");
//		mapper.applyTo(taskAttribute);
		return taskAttribute;
	}

	public boolean equals(@NonNull TaskAttribute newAttribute, @NonNull TaskAttribute oldAttribute) {
		if (TaskAttribute.TYPE_COMMENT.equals(newAttribute.getMetaData().getType())) {
			if (newAttribute.getValues().equals(oldAttribute.getValues())) {
				return true;
			}
			// the comment mapping accidentally changed throughout the Mylyn 3.7 cycle therefore some
			// cases need to be considered equal even though attribute values differ
			if (oldAttribute != null) {
				TaskAttribute commentIdAttribute = oldAttribute.getAttribute("task.common.comment.id"); //$NON-NLS-1$
				// ID not present
				if ((commentIdAttribute == null || commentIdAttribute.getValue().equals("")) //$NON-NLS-1$
						&& newAttribute.getValue().equals("")) { //$NON-NLS-1$
					return true;
				}
				// ID previously stored in a sub attribute
				if (commentIdAttribute != null && commentIdAttribute.getValue().equals(newAttribute.getValue())) {
					return true;
				}
			}
		}
		return newAttribute.getValues().equals(oldAttribute.getValues());
	}

	@Nullable
	public TaskAttribute getAssoctiatedAttribute(@NonNull TaskAttribute taskAttribute) {
		String id = taskAttribute.getMetaDatum(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID);
		if (id != null) {
			// look up as nested attribute first
			TaskAttribute associatedAttribute = taskAttribute.getAttribute(id);
			if (associatedAttribute != null) {
				return associatedAttribute;
			}
			// fall back to root
			return taskAttribute.getTaskData().getRoot().getAttribute(id);
		}
		return null;
	}

	@Nullable
	public TaskAttribute getAssoctiatedAttribute(@NonNull TaskOperation taskOperation) {
		TaskAttribute taskAttribute = taskOperation.getTaskAttribute();
		if (taskAttribute != null) {
			return getAssoctiatedAttribute(taskAttribute);
		}
		return null;
	}

	@NonNull
	public List<TaskAttribute> getAttributesByType(@NonNull TaskData taskData, @NonNull String type) {
		Assert.isNotNull(taskData);
		Assert.isNotNull(type);
		List<TaskAttribute> result = new ArrayList<TaskAttribute>();
		for (TaskAttribute taskAttribute : taskData.getRoot().getAttributes().values()) {
			if (type.equals(taskAttribute.getMetaData().getType())) {
				result.add(taskAttribute);
			}
		}
		return result;
	}

	public boolean getBooleanValue(@NonNull TaskAttribute attribute) {
		String booleanString = attribute.getValue();
		if (booleanString != null && booleanString.length() > 0) {
			return Boolean.parseBoolean(booleanString);
		}
		return false;
	}

	/**
	 * Connectors should ensure that this method returns dates at midnight in the local time zone when the
	 * {@link TaskAttribute#META_ATTRIBUTE_PRECISION precision} is {@link TimeUnit#DAYS} or coarser. This is because
	 * {@link Date Dates} are automatically displayed in the local time zone. This is not a concern when the precision
	 * is finer than {@link TimeUnit#DAYS}, because in that case the value has a time component which can meaningfully
	 * be converted to local time.
	 */
	@Nullable
	public Date getDateValue(@NonNull TaskAttribute attribute) {
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

	/**
	 * @deprecated Not used, see {@link TaskAttributeMetaData#setDefaultOption(String)}
	 */
	@Nullable
	@Deprecated
	public String getDefaultOption(@NonNull TaskAttribute taskAttribute) {
		return taskAttribute.getMetaData().getDefaultOption();
	}

	/**
	 * @since 3.5
	 */
	@Nullable
	public Double getDoubleValue(@NonNull TaskAttribute attribute) {
		String doubleString = attribute.getValue();
		try {
			if (doubleString != null) {
				return Double.parseDouble(doubleString);
			}
		} catch (NumberFormatException e) {
			// ignore
		}
		return null;
	}

	@Nullable
	public Integer getIntegerValue(@NonNull TaskAttribute attribute) {
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

	@Nullable
	public String getLabel(@NonNull TaskAttribute taskAttribute) {
		return taskAttribute.getMetaData().getLabel();
	}

	@Nullable
	public Long getLongValue(@NonNull TaskAttribute attribute) {
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
	@NonNull
	public Map<String, String> getOptions(@NonNull TaskAttribute attribute) {
		return attribute.getOptions();
	}

	@NonNull
	public IRepositoryPerson getRepositoryPerson(@NonNull TaskAttribute taskAttribute) {
		IRepositoryPerson person = taskRepository.createPerson(taskAttribute.getValue());
		TaskAttribute child = taskAttribute.getMappedAttribute(TaskAttribute.PERSON_NAME);
		if (child != null) {
			person.setName(getValue(child));
		}
		return person;
	}

	@NonNull
	public List<TaskOperation> getTaskOperations(@NonNull TaskAttribute operationsAttribute) {
		Assert.isNotNull(operationsAttribute);
		TaskData taskData = operationsAttribute.getTaskData();
		List<TaskOperation> result = new ArrayList<TaskOperation>();
		for (TaskAttribute taskAttribute : taskData.getRoot().getAttributes().values()) {
			if (TaskAttribute.TYPE_OPERATION.equals(taskAttribute.getMetaData().getType())
					&& !taskAttribute.getId().equals(mapToRepositoryKey(taskData.getRoot(), TaskAttribute.OPERATION))) {
				result.add(TaskOperation.createFrom(taskAttribute));
			}
		}
		return result;
	}

	@NonNull
	public TaskOperation getTaskOperation(@NonNull TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		return TaskOperation.createFrom(taskAttribute);
	}

	@NonNull
	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	/**
	 * @return empty String if not available
	 */
	@Nullable
	public String getValue(@NonNull TaskAttribute taskAttribute) {
		return taskAttribute.getValue();
	}

	@NonNull
	public String getValueLabel(@NonNull TaskAttribute taskAttribute) {
		List<String> labels = getValueLabels(taskAttribute);
		StringBuilder sb = new StringBuilder();
		String sep = ""; //$NON-NLS-1$
		for (String value : labels) {
			sb.append(sep).append(value);
			sep = ", "; //$NON-NLS-1$
		}
		return sb.toString();
	}

	@NonNull
	public List<String> getValueLabels(@NonNull TaskAttribute taskAttribute) {
		List<String> values = taskAttribute.getValues();
		Map<String, String> options = getOptions(taskAttribute);
		List<String> result = new ArrayList<String>(values.size());
		for (String value : values) {
			String option = options.get(value);
			if (option != null) {
				value = option;
			} else if (taskAttribute.getOption(value) != null) {
				value = taskAttribute.getOption(value);
			}
			result.add(value);
		}
		return result;
	}

	@NonNull
	public List<String> getValues(@NonNull TaskAttribute attribute) {
		return new ArrayList<String>(attribute.getValues());
	}

	public boolean hasValue(@NonNull TaskAttribute attribute) {
		return attribute.getValues().size() > 0;
	}

	public String mapToRepositoryKey(@NonNull TaskAttribute parent, @NonNull String key) {
		return key;
	}

	public void setBooleanValue(@NonNull TaskAttribute attribute, @NonNull Boolean value) {
		attribute.setValue(Boolean.toString(value));
	}

	/**
	 * Connectors should ensure that this method accepts dates at midnight in the local time zone when the
	 * {@link TaskAttribute#META_ATTRIBUTE_PRECISION precision} is {@link TimeUnit#DAYS} or coarser.
	 */
	public void setDateValue(@NonNull TaskAttribute attribute, @Nullable Date date) {
		if (date != null) {
			attribute.setValue(Long.toString(date.getTime()));
		} else {
			attribute.clearValues();
		}
	}

	/**
	 * @since 3.5
	 */
	public void setDoubleValue(@NonNull TaskAttribute attribute, @Nullable Double value) {
		if (value != null) {
			attribute.setValue(value.toString());
		} else {
			attribute.clearValues();
		}
	}

	public void setIntegerValue(@NonNull TaskAttribute attribute, @Nullable Integer value) {
		if (value != null) {
			attribute.setValue(value.toString());
		} else {
			attribute.clearValues();
		}
	}

	public void setLongValue(@NonNull TaskAttribute attribute, @Nullable Long value) {
		if (value != null) {
			attribute.setValue(value.toString());
		} else {
			attribute.clearValues();
		}
	}

	public void setRepositoryPerson(@NonNull TaskAttribute taskAttribute, @NonNull IRepositoryPerson person) {
		setValue(taskAttribute, person.getPersonId());
		if (person.getName() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.PERSON_NAME);
			setValue(child, person.getName());
		}
	}

	public void setTaskOperation(@NonNull TaskAttribute taskAttribute, @NonNull TaskOperation taskOperation) {
		Assert.isNotNull(taskAttribute);
		Assert.isNotNull(taskOperation);
		TaskOperation.applyTo(taskAttribute, taskOperation.getOperationId(), taskOperation.getLabel());
	}

	public void setValue(@NonNull TaskAttribute attribute, @NonNull String value) {
		attribute.setValue(value);
	}

	public void setValues(@NonNull TaskAttribute attribute, @NonNull List<String> values) {
		attribute.setValues(values);
	}

	public void updateTaskAttachment(@NonNull ITaskAttachment taskAttachment, @NonNull TaskAttribute taskAttribute) {
		TaskAttachmentMapper.createFrom(taskAttribute).applyTo(taskAttachment);
	}

	public void updateTaskComment(@NonNull ITaskComment taskComment, @NonNull TaskAttribute taskAttribute) {
		TaskCommentMapper.createFrom(taskAttribute).applyTo(taskComment);
	}

	/**
	 * Connectors may override this method to specify the mapping from the repository's priority options to
	 * {@link PriorityLevel}
	 *
	 * @return the {@link PriorityLevel} corresponding to the given option for the given priority attribute
	 * @since 3.20
	 */
	public PriorityLevel getPriorityLevel(TaskAttribute priorityAttribute, String priorityOption) {
		return PriorityLevel.fromString(priorityOption);
	}

}
