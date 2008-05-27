/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.Locale;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
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

	/** The bug id */
	private String id;

	/** The summary of the bug */
	private String description = "";

	/** The priority of the bug */
	private String priority = PriorityLevel.getDefault().toString();

	private String owner = "";

	private StringBuffer characters;

	private final TaskDataCollector collector;

	private final String repositoryUrl;

	private int resultCount;

	private final TaskAttributeMapper mapper;

	public SaxBugzillaQueryContentHandler(String repositoryUrl, TaskDataCollector collector, TaskAttributeMapper mapper) {
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
		BugzillaReportElement tag = BugzillaReportElement.UNKNOWN;
		try {
			tag = BugzillaReportElement.valueOf(localName.trim().toUpperCase(Locale.ENGLISH));
			switch (tag) {
			case LI:
// hit = new BugzillaQueryHit();
// hit.setRepository(repositoryUrl);
// break;
			}
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		String parsedText = characters.toString();
		BugzillaReportElement tag = BugzillaReportElement.UNKNOWN;
		try {
			tag = BugzillaReportElement.valueOf(localName.trim().toUpperCase(Locale.ENGLISH));
			switch (tag) {
			case ID:
				id = parsedText;
				break;
			// case BUG_SEVERITY:
			// severity = parsedText;
			// break;
			case PRIORITY:
				priority = parsedText;
				break;
			// case REP_PLATFORM:
			// platform = parsedText;
			// break;
			case ASSIGNED_TO:
				//hit.setOwner(parsedText);
				owner = parsedText;
				break;
			case BUG_STATUS:
				//	state = parsedText;
				break;
			// case RESOLUTION:
			// resolution = parsedText;
			// break;
			case SHORT_DESC:
				description = parsedText;
				break;
			case SHORT_SHORT_DESC:
				description = parsedText;
				break;
			case LI:
				TaskData taskData = new TaskData(mapper, BugzillaCorePlugin.CONNECTOR_KIND, repositoryUrl, id);
				BugzillaTaskDataHandler.createAttribute(taskData, BugzillaReportElement.SHORT_DESC).setValue(
						description);
				BugzillaTaskDataHandler.createAttribute(taskData, BugzillaReportElement.PRIORITY).setValue(priority);
				BugzillaTaskDataHandler.createAttribute(taskData, BugzillaReportElement.ASSIGNED_TO).setValue(owner);
				taskData.setPartial(true);
				collector.accept(taskData);
				resultCount++;
			}
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}

	}

	public int getResultCount() {
		return resultCount;
	}

}
