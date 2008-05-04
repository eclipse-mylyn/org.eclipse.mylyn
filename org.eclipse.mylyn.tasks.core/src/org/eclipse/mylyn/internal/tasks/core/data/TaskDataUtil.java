/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AttributeContainer;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryOperation;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskDataUtil {

	private static class TaskDataAccessor {

		private final TaskAttributeMapper mapper;

		private final TaskData taskData;

		public TaskDataAccessor(TaskData taskData) {
			this.taskData = taskData;
			this.mapper = taskData.getAttributeMapper();
		}

		private void addAttributes(AttributeContainer container, Map<String, TaskAttribute> attributeById) {
			for (String id : attributeById.keySet()) {
				TaskAttribute attribute = attributeById.get(id);
				RepositoryTaskAttribute legacyAttribute = mapAttribute(attribute);
				if (legacyAttribute != null) {
					container.addAttribute(legacyAttribute.getId(), legacyAttribute);
				}
			}
		}

		private boolean filter(String key) {
			return key.startsWith("tasks.meta.");
		}

		public String getValue(String attributeId) {
			TaskAttribute attribute = taskData.getRoot().getAttribute(attributeId);
			return getValue(attribute);
		}

		private String getValue(TaskAttribute attribute) {
			if (attribute != null) {
				return mapper.getValue(attribute);
			}
			return "";
		}

		public RepositoryAttachment mapAttachment(TaskAttribute attribute, AbstractAttributeFactory factory) {
			RepositoryAttachment legacyAttachment = new RepositoryAttachment(factory);
			legacyAttachment.setCreator(getValue(attribute.getAttribute(TaskAttribute.ATTACHMENT_AUTHOR)));
			legacyAttachment.setObsolete(Boolean.parseBoolean(getValue(attribute.getAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED))));
			legacyAttachment.setPatch((Boolean.parseBoolean(getValue(attribute.getAttribute(TaskAttribute.ATTACHMENT_IS_PATCH)))));
			legacyAttachment.setRepositoryKind(attribute.getTaskData().getConnectorKind());
			legacyAttachment.setRepositoryUrl(attribute.getTaskData().getRepositoryUrl());
			legacyAttachment.setTaskId(attribute.getTaskData().getTaskId());
			addAttributes(legacyAttachment, attribute.getAttributes());
			return legacyAttachment;
		}

		public RepositoryTaskAttribute mapAttribute(TaskAttribute attribute) {
			if (attribute.getMetaData(TaskAttribute.META_ARTIFICIAL) != null) {
				return null;
			}

			String name = attribute.getMetaData(TaskAttribute.META_LABEL);
			boolean hidden = !TaskAttribute.META_ATTRIBUTE_KIND.equals(attribute.getMetaData(TaskAttribute.META_ATTRIBUTE_KIND));
			RepositoryTaskAttribute legacyAttribute = new RepositoryTaskAttribute(attribute.getId(), name, hidden);
			legacyAttribute.setReadOnly(Boolean.parseBoolean(attribute.getMetaData(TaskAttribute.META_READ_ONLY)));
			legacyAttribute.setValues(attribute.getValues());
			for (String key : attribute.getMetaDatas().keySet()) {
				if (!filter(key)) {
					legacyAttribute.putMetaDataValue(key, attribute.getMetaData(key));
				}
			}
			for (String key : attribute.getOptions().keySet()) {
				legacyAttribute.addOption(attribute.getOption(key), key);
			}
			return legacyAttribute;
		}

		public TaskComment mapComment(TaskAttribute attribute, AbstractAttributeFactory factory) {
			Integer num = Integer.parseInt(attribute.getId());
			TaskComment legacyComment = new TaskComment(factory, num);
			legacyComment.setAttachmentId(getValue(attribute.getAttribute(TaskAttribute.COMMENT_ATTACHMENT_ID)));
			legacyComment.setHasAttachment(Boolean.parseBoolean(getValue(attribute.getAttribute(TaskAttribute.COMMENT_HAS_ATTACHMENT))));
			addAttributes(legacyComment, attribute.getAttributes());
			return legacyComment;
		}

		public RepositoryOperation mapOperation(TaskAttribute attribute, AbstractAttributeFactory factory) {
			String knobName = attribute.getValue();
			String operationName = attribute.getMetaData(TaskAttribute.META_LABEL);
			RepositoryOperation legacyOperation = new RepositoryOperation(knobName, operationName);

			TaskAttribute associatedAttribute = mapper.getAssoctiatedAttribute(attribute);
			if (associatedAttribute != null) {
				if (TaskAttribute.TYPE_SINGLE_SELECT.equals(associatedAttribute.getMetaData(TaskAttribute.META_ATTRIBUTE_TYPE))) {
					legacyOperation.setUpOptions(associatedAttribute.getId());
					legacyOperation.setOptionSelection(associatedAttribute.getValue());
					Map<String, String> options = associatedAttribute.getOptions();
					for (String key : options.keySet()) {
						legacyOperation.addOption(options.get(key), key);
					}
				} else if (TaskAttribute.TYPE_SHORT_TEXT.equals(associatedAttribute.getMetaData(TaskAttribute.META_ATTRIBUTE_TYPE))) {
					legacyOperation.setInputName(associatedAttribute.getId());
					legacyOperation.setInputValue(associatedAttribute.getValue());
				}
			}

			return legacyOperation;
		}

	}

	private static class LegacyDataAccessor {

		private final TaskData taskData;

		private TaskAttribute comments;

		private TaskAttribute attachments;

		private int attachmentId;

		public LegacyDataAccessor(TaskData taskData) {
			this.taskData = taskData;
		}

		public void addAttachment(RepositoryAttachment legacyAttachment) {
			if (attachments == null) {
				attachments = createAttribute(taskData.getRoot(), TaskAttribute.CONTAINER_ATTACHMENTS);
			}

			// see TaskDataStateReader.AttachmentHandler
			TaskAttribute attribute = attachments.createAttribute(++attachmentId + "");
			createAttribute(attribute, TaskAttribute.ATTACHMENT_AUTHOR).setValue(legacyAttachment.getCreator());
			createAttribute(attribute, TaskAttribute.ATTACHMENT_IS_DEPRECATED).setValue(
					Boolean.toString(legacyAttachment.isObsolete()));
			createAttribute(attribute, TaskAttribute.ATTACHMENT_IS_PATCH).setValue(
					Boolean.toString(legacyAttachment.isPatch()));
			addAttributes(attribute, legacyAttachment.getAttributes());
		}

		public void addAttribute(TaskAttribute parent, RepositoryTaskAttribute legacyAttribute) {
			TaskAttribute attribute = parent.createAttribute(legacyAttribute.getId());
			attribute.setValues(legacyAttribute.getValues());
			List<String> options = legacyAttribute.getOptions();
			for (String value : options) {
				attribute.putOption(legacyAttribute.getOptionParameter(value), value);
			}
			Map<String, String> metaData = legacyAttribute.getMetaData();
			for (String key : metaData.keySet()) {
				attribute.putMetaDataValue(key, metaData.get(key));
			}
			attribute.putMetaDataValue(TaskAttribute.META_LABEL, legacyAttribute.getName());
			attribute.putMetaDataValue(TaskAttribute.META_READ_ONLY, Boolean.toString(legacyAttribute.isReadOnly()));
			String kind = (legacyAttribute.isHidden()) ? null : TaskAttribute.META_ATTRIBUTE_KIND;
			attribute.putMetaDataValue(TaskAttribute.META_ATTRIBUTE_KIND, kind);
		}

		public void addAttributes(TaskAttribute parent, List<RepositoryTaskAttribute> list) {
			for (RepositoryTaskAttribute legacyAttribute : list) {
				addAttribute(parent, legacyAttribute);
			}
		}

		public void addComment(TaskComment legacyComment) {
			if (comments == null) {
				comments = createAttribute(taskData.getRoot(), TaskAttribute.CONTAINER_COMMENTS);
			}

			TaskAttribute attribute = comments.createAttribute(legacyComment.getNumber() + "");
			createAttribute(attribute, TaskAttribute.COMMENT_ATTACHMENT_ID).setValue(legacyComment.getAttachmentId());
			createAttribute(attribute, TaskAttribute.COMMENT_HAS_ATTACHMENT).setValue(
					Boolean.toString(legacyComment.hasAttachment()));
			addAttributes(attribute, legacyComment.getAttributes());
		}

		public void addOperation(TaskAttribute parent, RepositoryOperation legacyOperation) {
			String operationId = legacyOperation.getKnobName();
			TaskAttribute attribute = parent.createAttribute(operationId);
			attribute.setValue(operationId);
			attribute.putMetaDataValue(TaskAttribute.META_LABEL, legacyOperation.getOperationName());

			if (legacyOperation.getOptionName() != null) {
				attribute.putMetaDataValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, legacyOperation.getOptionName());
				TaskAttribute child = createAttribute(attribute, legacyOperation.getOptionName());
				child.setValue(legacyOperation.getOptionSelection());
				child.putMetaDataValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_SINGLE_SELECT);
				List<String> options = legacyOperation.getOptionNames();
				for (String option : options) {
					child.putOption(legacyOperation.getOptionValue(option), option);
				}
			} else if (legacyOperation.getInputName() != null) {
				attribute.putMetaDataValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, legacyOperation.getInputName());
				TaskAttribute child = createAttribute(attribute, legacyOperation.getInputName());
				child.putMetaDataValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_SHORT_TEXT);
				child.setValue(legacyOperation.getInputValue());
			}
		}

		private TaskAttribute createAttribute(TaskAttribute parent, String id) {
			TaskAttribute attribute = parent.createAttribute(id);
			attribute.putMetaDataValue(TaskAttribute.META_READ_ONLY, Boolean.toString(false));
			attribute.removeMetaDataValue(TaskAttribute.META_ATTRIBUTE_KIND);
			attribute.putMetaDataValue(TaskAttribute.META_ARTIFICIAL, Boolean.toString(true));
			return attribute;
		}
	}

	public static RepositoryTaskData toLegacyData(TaskData taskData, AbstractAttributeFactory factory) {
		TaskDataAccessor accessor = new TaskDataAccessor(taskData);
		RepositoryTaskData legacyData = new RepositoryTaskData(factory, taskData.getConnectorKind(),
				taskData.getRepositoryUrl(), taskData.getTaskId());
		legacyData.setNew(taskData.isNew());
		legacyData.setTaskKind(accessor.getValue(TaskAttribute.TASK_KIND));
		Map<String, TaskAttribute> attributesById = taskData.getRoot().getAttributes();
		for (String id : attributesById.keySet()) {
			TaskAttribute attribute = attributesById.get(id);
			if (TaskAttribute.CONTAINER_ATTACHMENTS.equals(id)) {
				for (TaskAttribute child : attribute.getAttributes().values()) {
					legacyData.addAttachment(accessor.mapAttachment(child, factory));
				}
			} else if (TaskAttribute.CONTAINER_COMMENTS.equals(id)) {
				for (TaskAttribute child : attribute.getAttributes().values()) {
					legacyData.addComment(accessor.mapComment(child, factory));
				}
			} else if (TaskAttribute.CONTAINER_OPERATIONS.equals(id)) {
				for (TaskAttribute child : attribute.getAttributes().values()) {
					RepositoryOperation operation = accessor.mapOperation(child, factory);
					if (operation.getKnobName().equals(attribute.getValue())) {
						operation.setChecked(true);
						legacyData.setSelectedOperation(operation);
					}
					legacyData.addOperation(operation);
				}
			} else {
				RepositoryTaskAttribute legacyAttribute = accessor.mapAttribute(attribute);
				if (legacyAttribute != null) {
					legacyData.addAttribute(legacyAttribute.getId(), legacyAttribute);
				}
			}
		}
		return legacyData;
	}

	public static TaskData toTaskData(RepositoryTaskData legacyData, TaskAttributeMapper mapper) {
		TaskData taskData = new TaskData(mapper, legacyData.getConnectorKind(), legacyData.getRepositoryUrl(),
				legacyData.getTaskId());
		LegacyDataAccessor accessor = new LegacyDataAccessor(taskData);
		accessor.createAttribute(taskData.getRoot(), TaskAttribute.TASK_KIND).setValue(legacyData.getTaskKind());
		accessor.addAttributes(taskData.getRoot(), legacyData.getAttributes());

		TaskAttribute parent = accessor.createAttribute(taskData.getRoot(), TaskAttribute.CONTAINER_OPERATIONS);
		List<RepositoryOperation> legacyOperations = legacyData.getOperations();
		for (RepositoryOperation legacyOperation : legacyOperations) {
			accessor.addOperation(parent, legacyOperation);
			if (legacyOperation.isChecked()) {
				parent.setValue(legacyOperation.getKnobName());
			}
		}
		if (legacyData.getSelectedOperation() != null) {
			parent.setValue(legacyData.getSelectedOperation().getKnobName());
		}

		List<TaskComment> legacyComments = legacyData.getComments();
		for (TaskComment legacyComment : legacyComments) {
			accessor.addComment(legacyComment);
		}

		List<RepositoryAttachment> legacyAttachments = legacyData.getAttachments();
		for (RepositoryAttachment legacyAttachment : legacyAttachments) {
			accessor.addAttachment(legacyAttachment);
		}

		return taskData;
	}

}
