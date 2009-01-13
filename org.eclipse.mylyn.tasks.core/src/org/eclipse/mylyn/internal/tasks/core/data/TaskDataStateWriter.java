/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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

	private static final String TASK_DATA_STATE_VERSION = "2.0"; //$NON-NLS-1$

	private static final String CDATA = "CDATA"; //$NON-NLS-1$

	private final TransformerHandler handler;

	public TaskDataStateWriter(TransformerHandler handler) {
		this.handler = handler;
	}

	public void write(ITaskDataWorkingCopy state) throws SAXException {
		handler.startDocument();
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_CONNECTOR_KIND, CDATA, state.getConnectorKind()); //$NON-NLS-1$ //$NON-NLS-2$
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_REPOSITORY_URL, CDATA, state.getRepositoryUrl()); //$NON-NLS-1$ //$NON-NLS-2$
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_TASK_ID, CDATA, state.getTaskId()); //$NON-NLS-1$ //$NON-NLS-2$
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_VERSION, CDATA, TASK_DATA_STATE_VERSION); //$NON-NLS-1$ //$NON-NLS-2$
		handler.startElement("", "", ITaskDataConstants.ELEMENT_TASK_STATE, atts); //$NON-NLS-1$ //$NON-NLS-2$
		if (state.getRepositoryData() != null) {
			writeTaskData(state.getRepositoryData(), ITaskDataConstants.ELEMENT_NEW_DATA);
		}
		if (state.getLastReadData() != null) {
			writeTaskData(state.getLastReadData(), ITaskDataConstants.ELEMENT_OLD_DATA);
		}
		if (state.getEditsData() != null) {
			writeTaskData(state.getEditsData(), ITaskDataConstants.ELEMENT_EDITS_DATA);
		}
		handler.endElement("", "", ITaskDataConstants.ELEMENT_TASK_STATE); //$NON-NLS-1$ //$NON-NLS-2$
		handler.endDocument();
	}

	private void writeTaskData(TaskData taskData, String elementName) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_CONNECTOR_KIND, CDATA, taskData.getConnectorKind()); //$NON-NLS-1$ //$NON-NLS-2$
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_REPOSITORY_URL, CDATA, taskData.getRepositoryUrl()); //$NON-NLS-1$ //$NON-NLS-2$
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_TASK_ID, CDATA, taskData.getTaskId()); //$NON-NLS-1$ //$NON-NLS-2$
		if (taskData.getVersion() != null) {
			atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_VERSION, CDATA, taskData.getVersion()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		handler.startElement("", "", elementName, atts); //$NON-NLS-1$ //$NON-NLS-2$
		atts.clear();
		handler.startElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTES, atts); //$NON-NLS-1$ //$NON-NLS-2$
		writeTaskAttribute(taskData.getRoot());
		handler.endElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTES); //$NON-NLS-1$ //$NON-NLS-2$
		handler.endElement("", "", elementName); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void writeTaskAttribute(TaskAttribute attribute) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", ITaskDataConstants.ATTRIBUTE_ID, CDATA, attribute.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		handler.startElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTE, atts); //$NON-NLS-1$ //$NON-NLS-2$
		atts.clear();

		handler.startElement("", "", ITaskDataConstants.ELEMENT_VALUES, atts); //$NON-NLS-1$ //$NON-NLS-2$
		List<String> values = attribute.getValues();
		for (String value : values) {
			handler.startElement("", "", ITaskDataConstants.ELEMENT_VALUE, atts); //$NON-NLS-1$ //$NON-NLS-2$
			handler.characters(value.toCharArray(), 0, value.length());
			handler.endElement("", "", ITaskDataConstants.ELEMENT_VALUE); //$NON-NLS-1$ //$NON-NLS-2$
		}
		handler.endElement("", "", ITaskDataConstants.ELEMENT_VALUES); //$NON-NLS-1$ //$NON-NLS-2$

		handler.startElement("", "", ITaskDataConstants.ELEMENT_OPTIONS, atts); //$NON-NLS-1$ //$NON-NLS-2$
		writeMap(atts, attribute.getOptions(), ITaskDataConstants.ELEMENT_OPTION);
		handler.endElement("", "", ITaskDataConstants.ELEMENT_OPTIONS); //$NON-NLS-1$ //$NON-NLS-2$

		handler.startElement("", "", ITaskDataConstants.ELEMENT_META_DATA, atts); //$NON-NLS-1$ //$NON-NLS-2$
		writeMap(atts, attribute.getMetaData().getValues(), ITaskDataConstants.ELEMENT_META);
		handler.endElement("", "", ITaskDataConstants.ELEMENT_META_DATA); //$NON-NLS-1$ //$NON-NLS-2$

		handler.startElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTES, atts); //$NON-NLS-1$ //$NON-NLS-2$
		Map<String, TaskAttribute> children = attribute.getAttributes();
		for (TaskAttribute child : children.values()) {
			writeTaskAttribute(child);
		}
		handler.endElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTES); //$NON-NLS-1$ //$NON-NLS-2$

		handler.endElement("", "", ITaskDataConstants.ELEMENT_ATTRIBUTE); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void writeMap(AttributesImpl atts, Map<String, String> options, String elementName) throws SAXException {
		for (String key : options.keySet()) {
			String value = options.get(key);
			handler.startElement("", "", elementName, atts); //$NON-NLS-1$ //$NON-NLS-2$
			handler.startElement("", "", ITaskDataConstants.ELEMENT_KEY, atts); //$NON-NLS-1$ //$NON-NLS-2$
			handler.characters(key.toCharArray(), 0, key.length());
			handler.endElement("", "", ITaskDataConstants.ELEMENT_KEY); //$NON-NLS-1$ //$NON-NLS-2$
			handler.startElement("", "", ITaskDataConstants.ELEMENT_VALUE, atts); //$NON-NLS-1$ //$NON-NLS-2$
			handler.characters(value.toCharArray(), 0, value.length());
			handler.endElement("", "", ITaskDataConstants.ELEMENT_VALUE); //$NON-NLS-1$ //$NON-NLS-2$
			handler.endElement("", "", elementName); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}
