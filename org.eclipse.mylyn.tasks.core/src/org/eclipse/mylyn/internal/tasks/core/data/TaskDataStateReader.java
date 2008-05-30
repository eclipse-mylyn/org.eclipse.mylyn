/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Steffen Pingel
 */
public class TaskDataStateReader extends DefaultHandler {

	private class AttachmentHandler10 extends ElementHandler {

		private int id;

		private TaskAttribute attribute;

		private final TaskAttribute parentAttribute;

		public AttachmentHandler10(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_ATTACHMENT);
			this.parentAttribute = parentAttribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			// create a unique id for each attachment since the actual id is in a child attribute
			attribute = createAttribute(parentAttribute, TaskAttribute.PREFIX_ATTACHMENT + ++id);
			attribute.getMetaData().defaults().setReadOnly(true).setType(TaskAttribute.TYPE_ATTACHMENT);
			attribute.setValue(getValue(attributes, ITaskDataConstants.ATTRIBUTE_ID) + "");

			TaskAttribute child = createAttribute(attribute, TaskAttribute.ATTACHMENT_AUTHOR);
			child.setValue(getValue(attributes, ITaskDataConstants.ATTRIBUTE_CREATOR));
			child.getMetaData().putValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_PERSON);

			child = createAttribute(attribute, TaskAttribute.ATTACHMENT_IS_DEPRECATED);
			child.setValue(getValue(attributes, ITaskDataConstants.ATTRIBUTE_IS_OBSOLETE));
			child.getMetaData().putValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_BOOLEAN);

			child = createAttribute(attribute, TaskAttribute.ATTACHMENT_IS_PATCH);
			child.setValue(getValue(attributes, ITaskDataConstants.ATTRIBUTE_IS_PATCH));
			child.getMetaData().putValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_BOOLEAN);

			addElementHandler(new AttributeHandler10(this, attribute) {
				@Override
				protected String mapId(String value) {
					// migrate key for description
					if (TaskAttribute.DESCRIPTION.equals(value)) {
						return TaskAttribute.ATTACHMENT_DESCRIPTION;
					}
					return super.mapId(value);
				}
			});
		}

	}

	private class AttributeHandler10 extends ElementHandler {

		private TaskAttribute attribute;

		private final TaskAttribute parentAttribute;

		public AttributeHandler10(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_ATTRIBUTE);
			this.parentAttribute = parentAttribute;
		}

		@Override
		protected void end(String uri, String localName, String name) {
			// detect type
			if (attribute.getOptions().size() > 0) {
				if (attribute.getValues().size() > 1) {
					attribute.getMetaData()
							.putValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_MULTI_SELECT);
				} else {
					attribute.getMetaData().putValue(TaskAttribute.META_ATTRIBUTE_TYPE,
							TaskAttribute.TYPE_SINGLE_SELECT);
				}
			}
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			String id = mapId(getValue(attributes, ITaskDataConstants.ATTRIBUTE_ID));
			String label = getValue(attributes, ITaskDataConstants.ATTRIBUTE_NAME);
			boolean hidden = Boolean.parseBoolean(getValue(attributes, ITaskDataConstants.ATTRIBUTE_HIDDEN));
			boolean readOnly = Boolean.parseBoolean(getValue(attributes, ITaskDataConstants.ATTRIBUTE_READONLY));
			attribute = parentAttribute.createAttribute(id);
			String kind = (hidden) ? null : TaskAttribute.KIND_DEFAULT;
			attribute.getMetaData().defaults().setLabel(label).setReadOnly(readOnly).setKind(kind);

			addElementHandler(new OptionHandler10(this, attribute));
			addElementHandler(new ValueHandler10(this, attribute));
			addElementHandler(new MetaDataHandler10(this, attribute));
		}

		protected String mapId(String value) {
			return value;
		}

	}

	private class AttributeHandler20 extends ElementHandler {

		private TaskAttribute attribute;

		private final TaskAttribute parentAttribute;

		public AttributeHandler20(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_ATTRIBUTE);
			this.parentAttribute = parentAttribute;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			String id = getValue(attributes, ITaskDataConstants.ATTRIBUTE_ID);
			attribute = parentAttribute.createAttribute(id);

			addElementHandler(new ValueHandler20(this, attribute));
			addElementHandler(new MapHandler20(this, attribute, ITaskDataConstants.ELEMENT_OPTION));
			addElementHandler(new MapHandler20(this, attribute, ITaskDataConstants.ELEMENT_META));
			addElementHandler(new AttributeHandler20(this, attribute));
		}

	}

	private class CommentHandler10 extends ElementHandler {

		private int id;

		private TaskAttribute attribute;

		private final TaskAttribute parentAttribute;

		public CommentHandler10(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_COMMENT);
			this.parentAttribute = parentAttribute;
		}

		@Override
		protected void end(String uri, String localName, String name) {
			TaskAttribute child = attribute.getMappedAttribute(TaskAttribute.COMMENT_TEXT);
			if (child != null) {
				child.getMetaData().putValue(TaskAttribute.META_READ_ONLY, Boolean.toString(true));
				child.getMetaData().putValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_LONG_RICH_TEXT);
			}
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			attribute = createAttribute(parentAttribute, TaskAttribute.PREFIX_COMMENT + ++id);
			attribute.getMetaData().defaults().setReadOnly(true).setType(TaskAttribute.TYPE_COMMENT);
			attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, TaskAttribute.COMMENT_TEXT);
			attribute.setValue(getValue(attributes, ITaskDataConstants.ATTRIBUTE_NUMBER));

			TaskAttribute child = createAttribute(attribute, TaskAttribute.COMMENT_ATTACHMENT_ID);
			child.setValue(getValue(attributes, ITaskDataConstants.ATTRIBUTE_ATTACHMENT_ID));

			child = createAttribute(attribute, TaskAttribute.COMMENT_HAS_ATTACHMENT);
			child.setValue(getValue(attributes, ITaskDataConstants.ATTRIBUTE_HAS_ATTACHMENT));
			child.getMetaData().putValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_BOOLEAN);

			child = createAttribute(attribute, TaskAttribute.COMMENT_NUMBER);
			child.setValue(getValue(attributes, ITaskDataConstants.ATTRIBUTE_NUMBER));
			child.getMetaData().putValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_INTEGER);

			addElementHandler(new AttributeHandler10(this, attribute));
		}

	}

	private class MetaDataHandler10 extends ElementHandler {

		private final TaskAttribute attribute;

		private String key;

		public MetaDataHandler10(ElementHandler parent, TaskAttribute attribute) {
			super(parent, ITaskDataConstants.ELEMENT_META);
			this.attribute = attribute;
		}

		@Override
		public void end(String uri, String localName, String name) {
			attribute.getMetaData().putValue(key, getCurrentElementText());
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			key = getValue(attributes, ITaskDataConstants.ATTRIBUTE_KEY);
			clearCurrentElementText();
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
		public void end(String uri, String localName, String name) {
			attribute.putOption(value, getCurrentElementText());
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			value = getValue(attributes, ITaskDataConstants.ATTRIBUTE_VALUE);
			clearCurrentElementText();
		}

	}

	private class OperationHandler10 extends ElementHandler {

		private TaskAttribute attribute;

		private final TaskAttribute operationAttribute;

		private final TaskAttribute parentAttribute;

		private int id;

		public OperationHandler10(ElementHandler parent, TaskAttribute parentAttribute) {
			super(parent, ITaskDataConstants.ELEMENT_OPERATION);
			this.parentAttribute = parentAttribute;
			this.operationAttribute = createAttribute(parentAttribute, TaskAttribute.OPERATION);
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			attribute = createAttribute(parentAttribute, TaskAttribute.PREFIX_OPERATION + ++id);
			attribute.getMetaData().putValue(TaskAttribute.META_ATTRIBUTE_TYPE, TaskAttribute.TYPE_CONTAINER);
			attribute.getMetaData().putValue(TaskAttribute.META_LABEL,
					getValue(attributes, ITaskDataConstants.ATTRIBUTE_OPERATION_NAME));
			String operationId = getValue(attributes, ITaskDataConstants.ATTRIBUTE_KNOB_NAME);
			attribute.setValue(operationId);

			if (Boolean.parseBoolean(getValue(attributes, ITaskDataConstants.ATTRIBUTE_IS_CHECKED))) {
				operationAttribute.setValue(operationId);
			}

			String value = getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_OPTION_NAME);
			TaskAttribute child;
			if (value.length() > 0) {
				attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, value);
				child = createAttribute(attribute, value);
				child.setValue(getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_OPTION_SELECTION));
				attribute.getMetaData().defaults().setReadOnly(false).setType(TaskAttribute.TYPE_SINGLE_SELECT);
				addElementHandler(new NameHandler(this, child));
			} else {
				value = getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_INPUT_NAME);
				if (value.length() > 0) {
					attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, value);
					child = createAttribute(attribute, value);
					child.setValue(getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_INPUT_VALUE));
					attribute.getMetaData().defaults().setReadOnly(false).setType(TaskAttribute.TYPE_SHORT_TEXT);
				}
			}
		}

	}

	private class OptionHandler10 extends ElementHandler {

		private final TaskAttribute attribute;

		private String parameter;

		public OptionHandler10(ElementHandler parent, TaskAttribute attribute) {
			super(parent, ITaskDataConstants.ELEMENT_OPTION);
			this.attribute = attribute;
		}

		@Override
		public void end(String uri, String localName, String name) {
			attribute.putOption(parameter, getCurrentElementText());
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			parameter = getValue(attributes, ITaskDataConstants.ATTRIBUTE_PARAMETER);
			clearCurrentElementText();
		}

	}

	private class TaskDataHandler10 extends ElementHandler {

		private TaskData taskData;

		public TaskDataHandler10(TaskStateHandler parent, String elementName) {
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

			addElementHandler(new AttributeHandler10(this, taskData.getRoot()));
			addElementHandler(new CommentHandler10(this, taskData.getRoot()));
			addElementHandler(new AttachmentHandler10(this, taskData.getRoot()));
			addElementHandler(new OperationHandler10(this, taskData.getRoot()));
			// the selected operation was never serialized, no need to read it
		}

	}

	private class TaskDataHandler20 extends ElementHandler {

		private TaskData taskData;

		public TaskDataHandler20(TaskStateHandler parent, String elementName) {
			super(parent, elementName);
		}

		public TaskData getTaskData() {
			return taskData;
		}

		@Override
		public void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			taskData = ((TaskStateHandler) getParent()).createTaskData(attributes);

			// skip the root node
			ElementHandler handler = new ElementHandler(this, ITaskDataConstants.ELEMENT_ATTRIBUTE);
			handler.addElementHandler(new AttributeHandler20(handler, taskData.getRoot()));
			addElementHandler(handler);
		}

	}

	private class TaskStateHandler extends ElementHandler {

		private TaskAttributeMapper attributeMapper;

		private TaskDataState state;

		private final String version;

		public TaskStateHandler(String version) {
			super(null, ITaskDataConstants.ELEMENT_TASK_STATE);
			this.version = version;

			if ("1.0".equals(version)) {
				addElementHandler(new TaskDataHandler10(this, ITaskDataConstants.ELEMENT_NEW_DATA));
				addElementHandler(new TaskDataHandler10(this, ITaskDataConstants.ELEMENT_OLD_DATA));
				addElementHandler(new TaskDataHandler10(this, ITaskDataConstants.ELEMENT_EDITS_DATA));
			} else if ("2.0".equals(version)) {
				addElementHandler(new TaskDataHandler20(this, ITaskDataConstants.ELEMENT_NEW_DATA));
				addElementHandler(new TaskDataHandler20(this, ITaskDataConstants.ELEMENT_OLD_DATA));
				addElementHandler(new TaskDataHandler20(this, ITaskDataConstants.ELEMENT_EDITS_DATA));
			}
		}

		public TaskData createTaskData(Attributes attributes) throws SAXException {
			TaskData taskData;
			if (state == null) {
				String connectorKind = getValue(attributes, ITaskDataConstants.ATTRIBUTE_REPOSITORY_KIND);
				String repositoryUrl = getValue(attributes, ITaskDataConstants.ATTRIBUTE_REPOSITORY_URL);
				String taskId = getValue(attributes, ITaskDataConstants.ATTRIBUTE_ID);
				attributeMapper = getAttributeMapper(connectorKind, repositoryUrl);
				taskData = new TaskData(attributeMapper, connectorKind, repositoryUrl, taskId);
			} else {
				taskData = new TaskData(attributeMapper, state.getConnectorKind(), state.getRepositoryUrl(),
						state.getTaskId());
			}
			String taskDataVersion = getOptionalValue(attributes, ITaskDataConstants.ATTRIBUTE_VERSION);
			if (taskDataVersion.length() > 0) {
				taskData.setVersion(taskDataVersion);
			} else {
				taskData.setVersion(version);
			}
			return taskData;
		}

		@Override
		public void done(ElementHandler elementHandler) {
			TaskData taskData;
			if (elementHandler instanceof TaskDataHandler10) {
				TaskDataHandler10 taskDataHandler = (TaskDataHandler10) elementHandler;
				TaskData data = taskDataHandler.getTaskData();
				if (state == null) {
					state = new TaskDataState(data.getConnectorKind(), data.getRepositoryUrl(), data.getTaskId());
				}
				taskData = taskDataHandler.getTaskData();
			} else {
				TaskDataHandler20 taskDataHandler = (TaskDataHandler20) elementHandler;
				taskData = taskDataHandler.getTaskData();
			}

			if (ITaskDataConstants.ELEMENT_NEW_DATA.equals(elementHandler.getElementName())) {
				state.setRepositoryData(taskData);
			} else if (ITaskDataConstants.ELEMENT_OLD_DATA.equals(elementHandler.getElementName())) {
				state.setLastReadData(taskData);
			} else if (ITaskDataConstants.ELEMENT_EDITS_DATA.equals(elementHandler.getElementName())) {
				state.setEditsData(taskData);
			}
			super.done(elementHandler);
		}

		public TaskDataState getState() {
			return state;
		}

		@Override
		protected void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			if ("2.0".equals(version)) {
				String connectorKind = getValue(attributes, ITaskDataConstants.ATTRIBUTE_CONNECTOR_KIND);
				String repositoryUrl = getValue(attributes, ITaskDataConstants.ATTRIBUTE_REPOSITORY_URL);
				String taskId = getValue(attributes, ITaskDataConstants.ATTRIBUTE_TASK_ID);
				attributeMapper = getAttributeMapper(connectorKind, repositoryUrl);
				state = new TaskDataState(connectorKind, repositoryUrl, taskId);
			}
		}
	}

	private class ValueHandler10 extends ElementHandler {

		private final TaskAttribute attribute;

		public ValueHandler10(ElementHandler parent, TaskAttribute attribute) {
			super(parent, ITaskDataConstants.ELEMENT_VALUE);
			this.attribute = attribute;
		}

		@Override
		public void end(String uri, String localName, String name) {
			attribute.addValue(getCurrentElementText());
		}

		@Override
		protected void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			clearCurrentElementText();
		}

	}

	private class ValueHandler20 extends ElementHandler {

		private final TaskAttribute attribute;

		public ValueHandler20(ElementHandler parent, TaskAttribute attribute) {
			super(parent, ITaskDataConstants.ELEMENT_VALUE);
			this.attribute = attribute;
		}

		@Override
		public void end(String uri, String localName, String name) {
			attribute.addValue(getCurrentElementText());
		}

		@Override
		protected void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			clearCurrentElementText();
		}

	}

	private class MapHandler20 extends ElementHandler {

		private final TaskAttribute attribute;

		private String key = "";

		private String value = "";

		public MapHandler20(ElementHandler parent, TaskAttribute attribute, String elementName) {
			super(parent, elementName);
			this.attribute = attribute;
		}

		@Override
		public void end(String uri, String localName, String name) {
			if (ITaskDataConstants.ELEMENT_OPTION.equals(getElementName())) {
				attribute.putOption(key, value);
			} else if (ITaskDataConstants.ELEMENT_META.equals(getElementName())) {
				attribute.getMetaData().putValue(key, value);
			}
			key = "";
			value = "";
		}

		@Override
		protected void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			addElementHandler(new TextHandler20(this, ITaskDataConstants.ELEMENT_KEY));
			addElementHandler(new TextHandler20(this, ITaskDataConstants.ELEMENT_VALUE));
		}

		@Override
		protected void done(ElementHandler handler) {
			if (ITaskDataConstants.ELEMENT_KEY.equals(handler.getElementName())) {
				key = handler.getCurrentElementText();
			} else if (ITaskDataConstants.ELEMENT_VALUE.equals(handler.getElementName())) {
				value = handler.getCurrentElementText();
			}
			super.done(handler);
		}

	}

	private class TextHandler20 extends ElementHandler {

		public TextHandler20(ElementHandler parent, String elementName) {
			super(parent, elementName);
		}

		@Override
		protected void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
			clearCurrentElementText();
		}

	}

	private TaskStateHandler handler;

	private final IRepositoryManager repositoryManager;

	private TaskDataState result;

	public TaskDataStateReader(IRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (handler != null) {
			handler.characters(ch, start, length);
		}
	}

	private TaskAttribute createAttribute(TaskAttribute parent, String id) {
		TaskAttribute attribute = parent.createAttribute(id);
		attribute.getMetaData().defaults();
		return attribute;
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

	private TaskAttributeMapper getAttributeMapper(String connectorKind, String repositoryUrl) throws SAXException {
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(connectorKind);
		if (connector == null) {
			throw new SAXException("No repository connector for kind \"" + connectorKind + "\" found");
		}

		TaskRepository taskRepository = repositoryManager.getRepository(connectorKind, repositoryUrl);
		if (taskRepository == null) {
			throw new SAXException("Repository \"" + repositoryUrl + "\" not found for kind \"" + connectorKind + "\"");
		}

		final TaskAttributeMapper attributeMapper;
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		if (taskDataHandler != null) {
			attributeMapper = taskDataHandler.getAttributeMapper(taskRepository);
		} else {
			attributeMapper = new TaskAttributeMapper(taskRepository);
		}
		return attributeMapper;
	}

	public TaskDataState getTaskDataState() {
		return result;
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if (handler != null) {
			handler.startElement(uri, localName, name, attributes);
		}
		if (ITaskDataConstants.ELEMENT_TASK_STATE.equals(name)) {
			String version = attributes.getValue(ITaskDataConstants.ATTRIBUTE_VERSION);
			handler = new TaskStateHandler(version);
			handler.start(uri, localName, name, attributes);
		}
	}

}