/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.Locale;

import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for RDF bugzilla query results.
 * 
 * @author Rob Elves
 */
public class SaxBugzillaQueryContentHandler extends DefaultHandler {

	private StringBuffer characters;

	private final TaskDataCollector collector;

	private final String repositoryUrl;

	private int resultCount;

	private final TaskAttributeMapper mapper;

	private TaskData taskData;

	public SaxBugzillaQueryContentHandler(String repositoryUrl, TaskDataCollector collector,
			TaskAttributeMapper mapper) {
		this.repositoryUrl = repositoryUrl;
		this.collector = collector;
		this.mapper = mapper;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		characters = new StringBuffer();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		String parsedText = characters.toString();
		BugzillaAttribute tag = BugzillaAttribute.UNKNOWN;
		try {
			String tagName = localName.trim().toUpperCase(Locale.ENGLISH);
			try {
				tag = BugzillaAttribute.valueOf(tagName);
			} catch (IllegalArgumentException e) {
				if (tagName.equals("ASSIGNED_TO_REALNAME")) { //$NON-NLS-1$
					tag = BugzillaAttribute.ASSIGNED_TO_NAME;
				} else if (tagName.equals("REPORTER_REALNAME")) { //$NON-NLS-1$
					tag = BugzillaAttribute.REPORTER_NAME;
				} else {
					throw e;
				}
			}
			switch (tag) {
			case QUERY_TIMESTAMP:
				if (collector instanceof BugzillaTaskDataCollector) {
					if (parsedText != null && parsedText.length() > 0) {
						BugzillaTaskDataCollector bCollector = (BugzillaTaskDataCollector) collector;
						bCollector.setQueryTimestamp(parsedText);
					}
				}
				break;
			case ID:
				taskData = new TaskData(mapper, getConnectorKind(), repositoryUrl, parsedText);
				taskData.setPartial(true);
				break;
			case SHORT_SHORT_DESC:
				if (taskData != null) {
					BugzillaTaskDataHandler.createAttribute(taskData, BugzillaAttribute.SHORT_DESC)
							.setValue(parsedText);
				}
				break;
			case LI:
				if (taskData != null) {
					collector.accept(taskData);
				}
				resultCount++;
				break;
			default:
				if (taskData != null) {
					BugzillaTaskDataHandler.createAttribute(taskData, tag).setValue(parsedText);
				}
				break;
			}
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}

	}

	protected String getConnectorKind() {
		return mapper.getTaskRepository().getConnectorKind();
	}

	public int getResultCount() {
		return resultCount;
	}

}
