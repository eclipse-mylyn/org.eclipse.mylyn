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

import javax.xml.transform.sax.TransformerHandler;

import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Steffen Pingel
 */
public class TaskDataStateWriter {

	private static final String TASK_DATA_STATE_VERSION = "2.0";

	private static final String CDATA = "CDATA";

	private final TransformerHandler handler;

	public TaskDataStateWriter(TransformerHandler handler) {
		this.handler = handler;
	}

	public void write(ITaskDataWorkingCopy state) throws SAXException {
		handler.startDocument();
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_CONNECTOR_KIND, CDATA, state.getConnectorKind());
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_REPOSITORY_URL, CDATA, state.getRepositoryUrl());
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_TASK_ID, CDATA, state.getTaskId());
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_VERSION, CDATA, TASK_DATA_STATE_VERSION);
		handler.startElement("", "", ITaskDataConstants.ELEMENT_TASK_STATE, atts);
		if (state.getRepositoryData() != null) {
			writeTaskData(state.getRepositoryData(), ITaskDataConstants.ELEMENT_NEW_DATA);
		}
		if (state.getLastReadData() != null) {
			writeTaskData(state.getLastReadData(), ITaskDataConstants.ELEMENT_OLD_DATA);
		}
		if (state.getEditsData() != null) {
			writeTaskData(state.getEditsData(), ITaskDataConstants.ELEMENT_EDITS_DATA);
		}
		handler.endElement("", "", ITaskDataConstants.ELEMENT_TASK_STATE);
		handler.endDocument();
	}

	private void writeTaskData(TaskData taskData, String elementName) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_CONNECTOR_KIND, CDATA, taskData.getConnectorKind());
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_REPOSITORY_URL, CDATA, taskData.getRepositoryUrl());
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_TASK_ID, CDATA, taskData.getTaskId());
		if (taskData.getVersion() != null) {
			atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_VERSION, CDATA, taskData.getVersion());
		}
		handler.startElement("", "", elementName, atts);
		atts.clear();
		handler.startElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTES, atts);
		writeTaskAttribute(taskData.getRoot());
		handler.endElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTES);
		handler.endElement("", "", elementName);
	}

	private void writeTaskAttribute(TaskAttribute attribute) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_ID, CDATA, attribute.getId());
		handler.startElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTE, atts);
		atts.clear();

		handler.startElement("", "", ITaskDataConstants.ELEMENT_VALUES, atts);
		List<String> values = attribute.getValues();
		for (String value : values) {
			handler.startElement("", "", ITaskDataConstants.ELEMENT_VALUE, atts);
			handler.characters(value.toCharArray(), 0, value.length());
			handler.endElement("", "", ITaskDataConstants.ELEMENT_VALUE);
		}
		handler.endElement("", "", ITaskDataConstants.ELEMENT_VALUES);

		handler.startElement("", "", ITaskDataConstants.ELEMENT_OPTIONS, atts);
		writeMap(atts, attribute.getOptions(), ITaskDataConstants.ELEMENT_OPTION);
		handler.endElement("", "", ITaskDataConstants.ELEMENT_OPTIONS);

		handler.startElement("", "", ITaskDataConstants.ELEMENT_META_DATA, atts);
		writeMap(atts, attribute.getMetaDatas(), ITaskDataConstants.ELEMENT_META);
		handler.endElement("", "", ITaskDataConstants.ELEMENT_META_DATA);

		handler.startElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTES, atts);
		Map<String, TaskAttribute> children = attribute.getAttributes();
		for (TaskAttribute child : children.values()) {
			writeTaskAttribute(child);
		}
		handler.endElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTES);

		handler.endElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTE);
	}

	private void writeMap(AttributesImpl atts, Map<String, String> options, String elementName) throws SAXException {
		for (String key : options.keySet()) {
			String value = options.get(key);
			handler.startElement("", "", elementName, atts);
			handler.startElement("", "", ITaskDataConstants.ELEMENT_KEY, atts);
			handler.characters(key.toCharArray(), 0, key.length());
			handler.endElement("", "", ITaskDataConstants.ELEMENT_KEY);
			handler.startElement("", "", ITaskDataConstants.ELEMENT_VALUE, atts);
			handler.characters(value.toCharArray(), 0, value.length());
			handler.endElement("", "", ITaskDataConstants.ELEMENT_VALUE);
			handler.endElement("", "", elementName);
		}
	}

}
