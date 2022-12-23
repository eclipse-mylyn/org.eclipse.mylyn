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

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskMapper implements ITaskMapping {

	private final boolean createNonExistingAttributes;

	private final TaskData taskData;

	public TaskMapper(@NonNull TaskData taskData) {
		this(taskData, false);
	}

	public TaskMapper(@NonNull TaskData taskData, boolean createNonExistingAttributes) {
		this.createNonExistingAttributes = createNonExistingAttributes;
		Assert.isNotNull(taskData);
		this.taskData = taskData;
	}

	public boolean applyTo(@NonNull ITask task) {
		boolean changed = false;
		if (hasChanges(task.getCompletionDate(), getCompletionDate(), TaskAttribute.DATE_COMPLETION)) {
			task.setCompletionDate(getCompletionDate());
			changed = true;
		}
		if (hasChanges(task.getCreationDate(), getCreationDate(), TaskAttribute.DATE_CREATION)) {
			task.setCreationDate(getCreationDate());
			changed = true;
		}
		if (hasChanges(task.getModificationDate(), getModificationDate(), TaskAttribute.DATE_MODIFICATION)) {
			task.setModificationDate(getModificationDate());
			changed = true;
		}
		if (hasChanges(task.getDueDate(), getDueDate(), TaskAttribute.DATE_DUE)) {
			task.setDueDate(getDueDate());
			changed = true;
		}
		if (hasChanges(task.getOwner(), getOwner(), TaskAttribute.USER_ASSIGNED)) {
			task.setOwner(getOwner());
			changed = true;
		}
		if (hasChanges(task.getOwnerId(), getOwnerId(), TaskAttribute.USER_ASSIGNED)) {
			task.setOwnerId(getOwnerId());
			changed = true;
		}
		if (hasChanges(task.getPriority(), getPriorityLevelString(), TaskAttribute.PRIORITY)) {
			task.setPriority(getPriorityLevelString());
			changed = true;
		}
		String priorityLabel = getPriority();
		if (hasChanges(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_PRIORITY_LABEL), priorityLabel,
				TaskAttribute.PRIORITY)) {
			task.setAttribute(ITasksCoreConstants.ATTRIBUTE_PRIORITY_LABEL, priorityLabel);
			changed = true;
		}
		if (hasChanges(task.getSummary(), getSummary(), TaskAttribute.SUMMARY)) {
			task.setSummary(getSummary());
			changed = true;
		}
		if (hasChanges(task.getTaskKey(), getTaskKey(), TaskAttribute.TASK_KEY)) {
			task.setTaskKey(getTaskKey());
			changed = true;
		}
		if (hasChanges(task.getTaskKind(), getTaskKind(), TaskAttribute.TASK_KIND)) {
			task.setTaskKind(getTaskKind());
			changed = true;
		}
		if (hasChanges(task.getUrl(), getTaskUrl(), TaskAttribute.TASK_URL)) {
			task.setUrl(getTaskUrl());
			changed = true;
		}
		return changed;
	}

	@Nullable
	private String getPriorityLevelString() {
		PriorityLevel priorityLevel = getPriorityLevel();
		return (priorityLevel != null) ? priorityLevel.toString() : PriorityLevel.getDefault().toString();
	}

	private boolean hasChanges(@Nullable Object existingValue, @Nullable Object newValue, @NonNull String attributeId) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeId);
		if (attribute != null) {
			return areNotEquals(existingValue, newValue);
		}
		return false;
	}

	private boolean areNotEquals(@Nullable Object existingProperty, @Nullable Object newProperty) {
		return (existingProperty != null) ? !existingProperty.equals(newProperty) : newProperty != null;
	}

	private void copyAttributeValue(@NonNull TaskAttribute sourceAttribute, @Nullable TaskAttribute targetAttribute) {
		if (targetAttribute == null) {
			return;
		}
		if (!targetAttribute.getMetaData().isReadOnly()) {
			targetAttribute.clearValues();
			if (targetAttribute.getOptions().size() > 0) {
				List<String> values = sourceAttribute.getValues();
				for (String value : values) {
					if (targetAttribute.getOptions().containsKey(value)) {
						targetAttribute.addValue(value);
					}
				}
			} else {
				List<String> values = sourceAttribute.getValues();
				for (String value : values) {
					targetAttribute.addValue(value);
				}
			}
		}
	}

	/**
	 * TODO update comment Sets attribute values from <code>sourceTaskData</code> on <code>targetTaskData</code>. Sets
	 * the following attributes:
	 * <ul>
	 * <li>summary
	 * <li>description
	 * </ul>
	 * Other attribute values are only set if they exist on <code>sourceTaskData</code> and <code>targetTaskData</code>.
	 *
	 * @param sourceTaskData
	 *            the source task data values are copied from, the connector kind of repository of
	 *            <code>sourceTaskData</code> can be different from <code>targetTaskData</code>
	 * @param targetTaskData
	 *            the target task data values are copied to, the connector kind matches the one of this task data
	 *            handler
	 * @since 2.2
	 */
	public void merge(@NonNull ITaskMapping source) {
		if (source.getTaskData() != null && this.getTaskData() != null
				&& source.getTaskData().getConnectorKind().equals(this.getTaskData().getConnectorKind())) {
			// task data objects are from the same connector, copy all attributes
			for (TaskAttribute sourceAttribute : source.getTaskData().getRoot().getAttributes().values()) {
				copyAttributeValue(sourceAttribute, this.getTaskData().getRoot().getAttribute(sourceAttribute.getId()));
			}
		} else {
			if (source.getCc() != null) {
				setCc(source.getCc());
			}
			if (source.getDescription() != null) {
				setDescription(source.getDescription());
			}
			if (source.getComponent() != null) {
				setComponent(source.getComponent());
			}
			if (source.getKeywords() != null) {
				setKeywords(source.getKeywords());
			}
			if (source.getOwner() != null) {
				setOwner(source.getOwner());
			}
			if (source.getPriorityLevel() != null) {
				setPriorityLevel(source.getPriorityLevel());
			}
			if (source.getProduct() != null) {
				setProduct(source.getProduct());
			}
			if (source.getSeverity() != null) {
				setSeverity(source.getSeverity());
			}
			if (source.getSummary() != null) {
				setSummary(source.getSummary());
			}
			if (source.getVersion() != null) {
				setVersion(source.getVersion());
			}
		}
	}

	@NonNull
	private TaskAttribute createAttribute(@NonNull String attributeKey, @Nullable String type) {
		TaskAttribute attribute;
		Field field = DefaultTaskSchema.getField(attributeKey);
		if (field != null) {
			attribute = field.createAttribute(taskData.getRoot());
		} else {
			attribute = taskData.getRoot().createMappedAttribute(attributeKey);
			attribute.getMetaData().defaults().setType(type);
		}
		return attribute;
	}

	@Nullable
	public List<String> getCc() {
		return getValues(TaskAttribute.USER_CC);
	}

	@Nullable
	public Date getCompletionDate() {
		return getDateValue(TaskAttribute.DATE_COMPLETION);
	}

	@Nullable
	public String getComponent() {
		return getValue(TaskAttribute.COMPONENT);
	}

	@Nullable
	public Date getCreationDate() {
		return getDateValue(TaskAttribute.DATE_CREATION);
	}

	@Nullable
	private Date getDateValue(@NonNull String attributeKey) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeMapper().getDateValue(attribute);
		}
		return null;
	}

	@Nullable
	public String getDescription() {
		return getValue(TaskAttribute.DESCRIPTION);
	}

	@Nullable
	public Date getDueDate() {
		return getDateValue(TaskAttribute.DATE_DUE);
	}

	@Nullable
	public List<String> getKeywords() {
		return getValues(TaskAttribute.KEYWORDS);
	}

	@Nullable
	private TaskAttribute getWriteableAttribute(@NonNull String attributeKey, String type) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeKey);
		if (createNonExistingAttributes) {
			if (attribute == null) {
				attribute = createAttribute(attributeKey, type);
			}
		} else if (attribute != null && attribute.getMetaData().isReadOnly()) {
			return null;
		}
		return attribute;
	}

	@Nullable
	public Date getModificationDate() {
		return getDateValue(TaskAttribute.DATE_MODIFICATION);
	}

	@Nullable
	public String getOwner() {
		return getValue(TaskAttribute.USER_ASSIGNED);
	}

	/**
	 * @since 3.15
	 */
	@Nullable
	public String getOwnerId() {
		return getValueId(TaskAttribute.USER_ASSIGNED);
	}

	@Nullable
	public String getPriority() {
		return getValue(TaskAttribute.PRIORITY);
	}

	/**
	 * Connectors should override {@link TaskAttributeMapper#getPriorityLevel(TaskAttribute, String)} to customize how
	 * priority options are mapped to {@link PriorityLevel}
	 */
	@Nullable
	public PriorityLevel getPriorityLevel() {
		String valueLabel = getPriority();
		if (valueLabel != null && PriorityLevel.isValidPriority(valueLabel)) {
			return PriorityLevel.fromString(valueLabel);
		}
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY);
		if (attribute != null) {
			return getTaskData().getAttributeMapper().getPriorityLevel(attribute, attribute.getValue());
		}
		return null;
	}

	@Nullable
	public String getProduct() {
		return getValue(TaskAttribute.PRODUCT);
	}

	@Nullable
	public String getReporter() {
		return getValue(TaskAttribute.USER_REPORTER);
	}

	@Nullable
	public String getResolution() {
		return getValue(TaskAttribute.RESOLUTION);
	}

	/**
	 * @since 3.2
	 */
	@Nullable
	public String getSeverity() {
		return getValue(TaskAttribute.SEVERITY);
	}

	@Nullable
	public String getSummary() {
		return getValue(TaskAttribute.SUMMARY);
	}

	@Nullable
	public String getStatus() {
		return getValue(TaskAttribute.STATUS);
	}

	@NonNull
	public TaskData getTaskData() {
		return taskData;
	}

	@Nullable
	public String getTaskKey() {
		return getValue(TaskAttribute.TASK_KEY);
	}

	@Nullable
	public String getTaskKind() {
		return getValue(TaskAttribute.TASK_KIND);
	}

	@Nullable
	public String getTaskStatus() {
		return getValue(TaskAttribute.STATUS);
	}

	@Nullable
	public String getTaskUrl() {
		return getValue(TaskAttribute.TASK_URL);
	}

	/**
	 * Returns the label of the attribute value.
	 */
	@Nullable
	public String getValue(@NonNull String attributeKey) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeMapper().getValueLabel(attribute);
		}
		return null;
	}

	/**
	 * Returns the id of the attribute value.
	 */
	@Nullable
	private String getValueId(@NonNull String attributeKey) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeMapper().getValue(attribute);
		}
		return null;
	}

	@Nullable
	private List<String> getValues(@NonNull String attributeKey) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeKey);
		if (attribute != null) {
			return taskData.getAttributeMapper().getValueLabels(attribute);
		}
		return null;
	}

	/**
	 * @since 3.2
	 */
	@Nullable
	public String getVersion() {
		return getValue(TaskAttribute.VERSION);
	}

	public boolean hasChanges(@NonNull ITask task) {
		boolean changed = false;
		changed |= hasChanges(task.getCompletionDate(), getCompletionDate(), TaskAttribute.DATE_COMPLETION);
		changed |= hasChanges(task.getCreationDate(), getCreationDate(), TaskAttribute.DATE_CREATION);
		changed |= hasChanges(task.getModificationDate(), getModificationDate(), TaskAttribute.DATE_MODIFICATION);
		changed |= hasChanges(task.getDueDate(), getDueDate(), TaskAttribute.DATE_DUE);
		changed |= hasChanges(task.getOwner(), getOwner(), TaskAttribute.USER_ASSIGNED);
		changed |= hasChanges(task.getOwnerId(), getOwnerId(), TaskAttribute.USER_ASSIGNED);
		changed |= hasChanges(task.getPriority(), getPriorityLevelString(), TaskAttribute.PRIORITY);
		changed |= hasChanges(task.getSummary(), getSummary(), TaskAttribute.SUMMARY);
		changed |= hasChanges(task.getTaskKey(), getTaskKey(), TaskAttribute.TASK_KEY);
		changed |= hasChanges(task.getTaskKind(), getTaskKind(), TaskAttribute.TASK_KIND);
		changed |= hasChanges(task.getUrl(), getTaskUrl(), TaskAttribute.TASK_URL);
		changed |= hasChanges(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_PRIORITY_LABEL), getPriority(),
				TaskAttribute.PRIORITY);
		return changed;
	}

//	private boolean hasChanges(Object value, String attributeKey) {
//		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeKey);
//		if (attribute != null) {
//			if (TaskAttribute.TYPE_BOOLEAN.equals(attribute.getMetaData().getType())) {
//				return areNotEquals(value, taskData.getAttributeMapper().getBooleanValue(attribute));
//			} else if (TaskAttribute.TYPE_DATE.equals(attribute.getMetaData().getType())) {
//				return areNotEquals(value, taskData.getAttributeMapper().getDateValue(attribute));
//			} else if (TaskAttribute.TYPE_INTEGER.equals(attribute.getMetaData().getType())) {
//				return areNotEquals(value, taskData.getAttributeMapper().getIntegerValue(attribute));
//			} else {
//				return areNotEquals(value, taskData.getAttributeMapper().getValue(attribute));
//			}
//		}
//		return false;
//	}

	public void setCc(@NonNull List<String> cc) {
		setValues(TaskAttribute.USER_CC, cc);
	}

	public void setCompletionDate(@Nullable Date dateCompleted) {
		setDateValue(TaskAttribute.DATE_COMPLETION, dateCompleted);
	}

	public void setComponent(@NonNull String component) {
		setValue(TaskAttribute.COMPONENT, component);
	}

	public void setCreationDate(@Nullable Date dateCreated) {
		setDateValue(TaskAttribute.DATE_CREATION, dateCreated);
	}

	@Nullable
	private TaskAttribute setDateValue(@NonNull String attributeKey, @Nullable Date value) {
		TaskAttribute attribute = getWriteableAttribute(attributeKey, TaskAttribute.TYPE_DATE);
		if (attribute != null) {
			taskData.getAttributeMapper().setDateValue(attribute, value);
		}
		return attribute;
	}

	public void setDescription(@NonNull String description) {
		setValue(TaskAttribute.DESCRIPTION, description);
	}

	public void setDueDate(Date value) {
		setDateValue(TaskAttribute.DATE_DUE, value);
	}

	public void setKeywords(@NonNull List<String> keywords) {
		setValues(TaskAttribute.KEYWORDS, keywords);
	}

	public void setModificationDate(@Nullable Date dateModified) {
		setDateValue(TaskAttribute.DATE_MODIFICATION, dateModified);
	}

	// TODO use Person class?
	public void setOwner(@NonNull String owner) {
		setValue(TaskAttribute.USER_ASSIGNED, owner);
	}

	public void setPriority(@NonNull String priority) {
		setValue(TaskAttribute.PRIORITY, priority);
	}

	public void setPriorityLevel(@NonNull PriorityLevel priority) {
		setPriority(priority.toString());
	}

	public void setProduct(@NonNull String product) {
		setValue(TaskAttribute.PRODUCT, product);
	}

	// TODO use Person class?
	public void setReporter(@NonNull String reporter) {
		setValue(TaskAttribute.USER_REPORTER, reporter);
	}

	/**
	 * @since 3.2
	 */
	public void setSeverity(@NonNull String severity) {
		setValue(TaskAttribute.SEVERITY, severity);
	}

	public void setSummary(@NonNull String summary) {
		setValue(TaskAttribute.SUMMARY, summary);
	}

	public void setStatus(@NonNull String status) {
		setValue(TaskAttribute.STATUS, status);
	}

	public void setTaskKind(@NonNull String taskKind) {
		setValue(TaskAttribute.TASK_KIND, taskKind);
	}

	/**
	 * @since 3.3
	 */
	public void setTaskKey(@NonNull String taskKey) {
		setValue(TaskAttribute.TASK_KEY, taskKey);
	}

	public void setTaskUrl(@NonNull String taskUrl) {
		setValue(TaskAttribute.TASK_URL, taskUrl);
	}

	/**
	 * @since 3.2
	 */
	public void setVersion(@NonNull String version) {
		setValue(TaskAttribute.VERSION, version);
	}

	@Nullable
	public TaskAttribute setValue(@NonNull String attributeKey, @NonNull String value) {
		TaskAttribute attribute = getWriteableAttribute(attributeKey, TaskAttribute.TYPE_SHORT_TEXT);
		if (attribute != null) {
			taskData.getAttributeMapper().setValue(attribute, value);
		}
		return attribute;
	}

	@Nullable
	private TaskAttribute setValues(@NonNull String attributeKey, @NonNull List<String> values) {
		TaskAttribute attribute = getWriteableAttribute(attributeKey, TaskAttribute.TYPE_SHORT_TEXT);
		if (attribute != null) {
			taskData.getAttributeMapper().setValues(attribute, values);
		}
		return attribute;
	}

}
