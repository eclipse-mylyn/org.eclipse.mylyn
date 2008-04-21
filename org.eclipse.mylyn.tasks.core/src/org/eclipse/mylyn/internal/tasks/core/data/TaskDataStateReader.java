/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import org.eclipse.mylyn.tasks.core.IdentityAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.AbstractAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.ITaskDataState;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Steffen Pingel
 */
public class TaskDataStateReader extends DefaultHandler {

	TaskStateHandler handler;

	private enum Version {
		V_1_0, V_2_0
	};

	TaskDataState result;

	private Version version;

	public TaskDataStateReader() {
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if (handler != null) {
			handler.startElement(uri, localName, name, attributes);
		}
		if (ITaskDataConstants.ELEMENT_TASK_STATE.equals(name)) {
			if ("1.0".equals(attributes.getValue(ITaskDataConstants.ATTRIBUTE_VERSION))) {
				version = Version.V_1_0;
			}
			handler = new TaskStateHandler();
			handler.start(uri, localName, name, attributes);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (handler != null) {
			handler.characters(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (handler != null) {
			handler.endElement(uri, localName, name);
			if (ITaskDataConstants.ELEMENT_TASK_STATE.equals(name)) {
				result = handler.getState();
				handler = null;
			}
		}
	}

	public ITaskDataState getResult() {
		return (handler != null) ? handler.getState() : null;
	}

	private AbstractAttributeMapper getAttributeMapper(String connectorKind, String repositoryUrl) {
		return IdentityAttributeMapper.getInstance();
	}

	public TaskDataState getTaskDataState() {
		return result;
	}

	private class TaskStateHandler extends ElementHandler {

		private TaskDataState state;

		public TaskStateHandler() {
			super(null, ITaskDataConstants.ELEMENT_TASK_STATE);

			addElementHandler(new TaskDataHandler(this, ITaskDataConstants.ELEMENT_NEW_DATA));
			addElementHandler(new TaskDataHandler(this, ITaskDataConstants.ELEMENT_OLD_DATA));
			addElementHandler(new TaskDataHandler(this, ITaskDataConstants.ELEMENT_EDITS_DATA));
		}

		@Override
		public void done(ElementHandler elementHandler) {
			TaskDataHandler taskDataHandler = (TaskDataHandler) elementHandler;
			TaskData data = taskDataHandler.getTaskData();
			if (state == null) {
				state = new TaskDataState(data.getConnectorKind(), data.getRepositoryUrl(), data.getTaskId());
			}
			if (ITaskDataConstants.ELEMENT_NEW_DATA.equals(elementHandler.getElementName())) {
				state.setNewTaskData(taskDataHandler.getTaskData());
			} else if (ITaskDataConstants.ELEMENT_OLD_DATA.equals(elementHandler.getElementName())) {
				state.setOldTaskData(taskDataHandler.getTaskData());
			} else if (ITaskDataConstants.ELEMENT_EDITS_DATA.equals(elementHandler.getElementName())) {
				state.setEditsTaskData(taskDataHandler.getTaskData());
			}
			super.done(elementHandler);
		}

		public TaskDataState getState() {
			return state;
		}

		public TaskData createTaskData(Attributes attributes) throws SAXException {
			if (state == null) {
				String connectorKind = getValue(attributes, ITaskDataConstants.ATTRIBUTE_REPOSITORY_KIND);
				String repositoryUrl = getValue(attributes, ITaskDataConstants.ATTRIBUTE_REPOSITORY_URL);
				String taskId = getValue(attributes, ITaskDataConstants.ATTRIBUTE_ID);
				return new TaskData(getAttributeMapper(connectorKind, repositoryUrl), connectorKind, repositoryUrl,
						taskId);
			} else {
				return new TaskData(getAttributeMapper(state.getConnectorKind(), state.getRepositoryUrl()),
						state.getConnectorKind(), state.getRepositoryUrl(), state.getTaskId());
			}
		}
	}

	private class TaskDataHandler extends ElementHandler {

		private TaskData taskData;

		public TaskDataHandler(TaskStateHandler parent, String elementName) {
			super(parent, elementName);
		}

		public TaskData getTaskData() {
			return taskData;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			taskData = ((TaskStateHandler) getParent()).createTaskData(attributes);
			String taskKind = getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_TASK_KIND);
			if (taskKind != null) {
				createAttribute(taskData.getRoot(), TaskAttribute.TASK_KIND).setValue(taskKind);
			}

			addElementHandler(new AttributeHandler(this, taskData.getRoot()));
			addElementHandler(new CommentHandler(this, taskData.getRoot()));
			addElementHandler(new AttachmentHandler(this, taskData.getRoot()));
			addElementHandler(new OperationHandler(this, taskData.getRoot()));
			// the selected operation was never serialized, no need to read it
		}

	}

	private class AttributeHandler extends ElementHandler {

		private final TaskAttribute parentAttribute;

		private TaskAttribute attribute;

		public AttributeHandler(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_ATTRIBUTE);
			this.parentAttribute = parentAttribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			String id = getValue(attributes, ITaskDataConstants.ATTRIBUTE_ID);
			String label = getValue(attributes, ITaskDataConstants.ATTRIBUTE_NAME);
			boolean hidden = Boolean.parseBoolean(getValue(attributes, ITaskDataConstants.ATTRIBUTE_HIDDEN));
			boolean readOnly = Boolean.parseBoolean(getValue(attributes, ITaskDataConstants.ATTRIBUTE_READONLY));
			attribute = parentAttribute.createAttribute(id);
			attribute.putMetaDataValue(TaskAttribute.META_LABEL, label);
			attribute.putMetaDataValue(TaskAttribute.META_READ_ONLY, Boolean.toString(readOnly));
			attribute.putMetaDataValue(TaskAttribute.META_SHOW_IN_EDITOR, Boolean.toString(!hidden));

			addElementHandler(new OptionHandler(this, attribute));
			addElementHandler(new ValueHandler(this, attribute));
			addElementHandler(new MetaDataHandler(this, attribute));
		}

	}

	private class OptionHandler extends ElementHandler {

		private final TaskAttribute attribute;

		private String parameter;

		public OptionHandler(ElementHandler parent, TaskAttribute attribute) {
			super(parent, ITaskDataConstants.ELEMENT_OPTION);
			this.attribute = attribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			parameter = getValue(attributes, ITaskDataConstants.ATTRIBUTE_PARAMETER);
			clearCurrentElementText();
		}

		@Override
		public void end(String uri, String localName, String name) {
			attribute.putOption(parameter, getCurrentElementText());
		}

	}

	private class ValueHandler extends ElementHandler {

		private final TaskAttribute attribute;

		public ValueHandler(ElementHandler parent, TaskAttribute attribute) {
			super(parent, ITaskDataConstants.ELEMENT_VALUE);
			this.attribute = attribute;
		}

		@Override
		protected void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			clearCurrentElementText();
		}

		@Override
		public void end(String uri, String localName, String name) {
			attribute.addValue(getCurrentElementText());
		}

	}

	private class MetaDataHandler extends ElementHandler {

		private final TaskAttribute attribute;

		private String key;

		public MetaDataHandler(ElementHandler parent, TaskAttribute attribute) {
			super(parent, ITaskDataConstants.ELEMENT_META);
			this.attribute = attribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			key = getValue(attributes, ITaskDataConstants.ATTRIBUTE_KEY);
			clearCurrentElementText();
		}

		@Override
		public void end(String uri, String localName, String name) {
			attribute.putMetaDataValue(key, getCurrentElementText());
		}

	}

	private class CommentHandler extends ElementHandler {

		private final TaskAttribute parentAttribute;

		private TaskAttribute attribute;

		private TaskAttribute container;

		public CommentHandler(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_COMMENT);
			this.parentAttribute = parentAttribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			if (container == null) {
				container = createAttribute(parentAttribute, TaskAttribute.CONTAINER_COMMENTS);
			}

			String commentId = getValue(attributes, ITaskDataConstants.ATTRIBUTE_NUMBER);
			attribute = container.createAttribute(commentId);
			createAttribute(attribute, TaskAttribute.COMMENT_ATTACHMENT_ID).setValue(
					getValue(attributes, ITaskDataConstants.ATTRIBUTE_ATTACHMENT_ID));
			createAttribute(attribute, TaskAttribute.COMMENT_HAS_ATTACHMENT).setValue(
					getValue(attributes, ITaskDataConstants.ATTRIBUTE_HAS_ATTACHMENT));

			addElementHandler(new AttributeHandler(this, attribute));
		}

	}

	private class AttachmentHandler extends ElementHandler {

		private final TaskAttribute parentAttribute;

		private TaskAttribute attribute;

		private TaskAttribute container;

		private int attachmentId;

		public AttachmentHandler(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_ATTACHMENT);
			this.parentAttribute = parentAttribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			if (container == null) {
				container = createAttribute(parentAttribute, TaskAttribute.CONTAINER_ATTACHMENTS);
			}

			// create a unique id for each attachment since the actual id is in a child attribute
			attribute = container.createAttribute(++attachmentId + "");
			createAttribute(attribute, TaskAttribute.ATTACHMENT_AUTHOR).setValue(
					getValue(attributes, ITaskDataConstants.ATTRIBUTE_CREATOR));
			createAttribute(attribute, TaskAttribute.ATTACHMENT_IS_DEPRECATED).setValue(
					getValue(attributes, ITaskDataConstants.ATTRIBUTE_IS_OBSOLETE));
			createAttribute(attribute, TaskAttribute.ATTACHMENT_IS_PATCH).setValue(
					getValue(attributes, ITaskDataConstants.ATTRIBUTE_IS_PATCH));

			addElementHandler(new AttributeHandler(this, attribute));
		}

	}

	private class OperationHandler extends ElementHandler {

		private final TaskAttribute parentAttribute;

		private TaskAttribute attribute;

		private TaskAttribute container;

		public OperationHandler(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_OPERATION);
			this.parentAttribute = parentAttribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			if (container == null) {
				container = createAttribute(parentAttribute, TaskAttribute.CONTAINER_OPERATIONS);
			}

			String operationId = getValue(attributes, ITaskDataConstants.ATTRIBUTE_KNOB_NAME);
			attribute = container.createAttribute(operationId);
			createAttribute(attribute, TaskAttribute.OPERATION_NAME).setValue(
					getValue(attributes, ITaskDataConstants.ATTRIBUTE_OPERATION_NAME));
			if (Boolean.parseBoolean(getValue(attributes, ITaskDataConstants.ATTRIBUTE_IS_CHECKED))) {
				container.setValue(operationId);
			}

			String value = getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_OPTION_NAME);
			if (value.length() > 0) {
				attribute.putMetaDataValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, value);
				TaskAttribute child = createAttribute(attribute, value);
				child.setValue(getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_OPTION_SELECTION));
				addElementHandler(new NameHandler(this, child));
			} else {
				value = getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_INPUT_NAME);
				if (value.length() > 0) {
					attribute.putMetaDataValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, value);
					TaskAttribute child = createAttribute(attribute, value);
					child.setValue(getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_INPUT_VALUE));
				}
			}
		}

	}

	private class NameHandler extends ElementHandler {

		private final TaskAttribute attribute;

		private String value;

		public NameHandler(ElementHandler parent, TaskAttribute attribute) {
			super(parent, ITaskDataConstants.ELEMENT_NAME);
			this.attribute = attribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			value = getValue(attributes, ITaskDataConstants.ATTRIBUTE_VALUE);
			clearCurrentElementText();
		}

		@Override
		public void end(String uri, String localName, String name) {
			attribute.putOption(value, getCurrentElementText());
		}

	}

	private TaskAttribute createAttribute(TaskAttribute parent, String id) {
		TaskAttribute attribute = parent.createAttribute(id);
		attribute.putMetaDataValue(TaskAttribute.META_LABEL, null);
		attribute.putMetaDataValue(TaskAttribute.META_READ_ONLY, Boolean.toString(false));
		attribute.putMetaDataValue(TaskAttribute.META_SHOW_IN_EDITOR, Boolean.toString(false));
		attribute.putMetaDataValue(TaskAttribute.META_ARTIFICIAL, Boolean.toString(true));
		return attribute;
	}

}