/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for xml bugzilla reports.
 * 
 * @author Rob Elves
 */
public class SaxMultiBugReportContentHandler extends DefaultHandler {

	private static final String ATTRIBUTE_NAME = "name";

	private static final String COMMENT_ATTACHMENT_STRING = "Created an attachment (id=";

	private StringBuffer characters;

	private TaskComment taskComment;

	private Map<String, TaskComment> attachIdToComment = new HashMap<String, TaskComment>();

	private int commentNum = 0;

	private RepositoryAttachment attachment;

	private Map<String, RepositoryTaskData> taskDataMap;

	private RepositoryTaskData repositoryTaskData;

	private String errorMessage = null;

	private AbstractAttributeFactory attributeFactory;

	//private int retrieved = 1;

	public SaxMultiBugReportContentHandler(AbstractAttributeFactory factory, Map<String, RepositoryTaskData> taskDataMap) {
		this.attributeFactory = factory;
		this.taskDataMap = taskDataMap;
	}

	public boolean errorOccurred() {
		return errorMessage != null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

//	public RepositoryTaskData getReport() {
//		return repositoryTaskData;
//	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
		//System.err.println(String.copyValueOf(ch, start, length));
		// if (monitor.isCanceled()) {
		// throw new OperationCanceledException("Search cancelled");
		// }
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		characters = new StringBuffer();
		BugzillaReportElement tag = BugzillaReportElement.UNKNOWN;
		try {
			tag = BugzillaReportElement.valueOf(localName.trim().toUpperCase(Locale.ENGLISH));
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}
		switch (tag) {
		case BUGZILLA:
			// Note: here we can get the bugzilla version if necessary
			break;
		case BUG:
			if (attributes != null && (attributes.getValue("error") != null)) {
				errorMessage = attributes.getValue("error");
			}
			attachIdToComment = new HashMap<String, TaskComment>();
			commentNum = 0;
			taskComment = null;
			break;
		case LONG_DESC:
			taskComment = new TaskComment(attributeFactory, commentNum++);
			break;
		case WHO:
			if (taskComment != null) {
				if (attributes != null && attributes.getLength() > 0) {
					String name = attributes.getValue(ATTRIBUTE_NAME);
					if (name != null) {
						taskComment.setAttributeValue(BugzillaReportElement.WHO_NAME.getKeyString(), name);
					}
				}
			}
			break;
		case REPORTER:
			if (attributes != null && attributes.getLength() > 0) {
				String name = attributes.getValue(ATTRIBUTE_NAME);
				if (name != null) {
					RepositoryTaskAttribute attr = attributeFactory.createAttribute(BugzillaReportElement.REPORTER_NAME.getKeyString());
					attr.setValue(name);
					repositoryTaskData.addAttribute(BugzillaReportElement.REPORTER_NAME.getKeyString(), attr);
				}
			}
			break;
		case ASSIGNED_TO:
			if (attributes != null && attributes.getLength() > 0) {
				String name = attributes.getValue(ATTRIBUTE_NAME);
				if (name != null) {
					RepositoryTaskAttribute attr = attributeFactory.createAttribute(BugzillaReportElement.ASSIGNED_TO_NAME.getKeyString());
					attr.setValue(name);
					repositoryTaskData.addAttribute(BugzillaReportElement.ASSIGNED_TO_NAME.getKeyString(), attr);
				}
			}
			break;
		case ATTACHMENT:
			attachment = new RepositoryAttachment(attributeFactory);
			if (attributes != null) {
				if ("1".equals(attributes.getValue(BugzillaReportElement.IS_OBSOLETE.getKeyString()))) {
					attachment.addAttribute(BugzillaReportElement.IS_OBSOLETE.getKeyString(),
							attributeFactory.createAttribute(BugzillaReportElement.IS_OBSOLETE.getKeyString()));
					attachment.setObsolete(true);
				}
				if ("1".equals(attributes.getValue(BugzillaReportElement.IS_PATCH.getKeyString()))) {
					attachment.setPatch(true);
				}
			}
			break;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		String parsedText = characters.toString();

		BugzillaReportElement tag = BugzillaReportElement.UNKNOWN;
		try {
			tag = BugzillaReportElement.valueOf(localName.trim().toUpperCase(Locale.ENGLISH));
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}
		switch (tag) {
		case BUG_ID: {
			try {
				repositoryTaskData = taskDataMap.get(parsedText.trim());
				if (repositoryTaskData == null) {
					errorMessage = parsedText + " id not found.";
				}
			} catch (Exception e) {
				errorMessage = "Bug id from server did not match requested id.";
			}

			RepositoryTaskAttribute attr = repositoryTaskData.getAttribute(tag.getKeyString());
			if (attr == null) {
				attr = attributeFactory.createAttribute(tag.getKeyString());
				repositoryTaskData.addAttribute(tag.getKeyString(), attr);
			}
			attr.setValue(parsedText);
			break;
		}

			// Comment attributes
		case WHO:
			if (taskComment != null) {
				RepositoryTaskAttribute attr = attributeFactory.createAttribute(tag.getKeyString());
				attr.setValue(parsedText);
				taskComment.addAttribute(tag.getKeyString(), attr);
			}
			break;
		case BUG_WHEN:
			if (taskComment != null) {
				RepositoryTaskAttribute attr = attributeFactory.createAttribute(tag.getKeyString());
				attr.setValue(parsedText);
				taskComment.addAttribute(tag.getKeyString(), attr);
			}
			break;
		case THETEXT:
			if (taskComment != null) {
				RepositoryTaskAttribute attr = attributeFactory.createAttribute(tag.getKeyString());
				attr.setValue(parsedText);
				taskComment.addAttribute(tag.getKeyString(), attr);

				// Check for attachment
				parseAttachment(taskComment, parsedText);
			}
			break;
		case LONG_DESC:
			if (taskComment != null) {
				if (taskComment.getNumber() == 0) {
					repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.DESCRIPTION, taskComment.getText());
					break;
				}
				repositoryTaskData.addComment(taskComment);
			}
			break;

		// Attachment attributes
		case ATTACHID:
		case DATE:
		case DESC:
		case FILENAME:
		case CTYPE:
		case TYPE:
			if (attachment != null) {
				RepositoryTaskAttribute attr = attributeFactory.createAttribute(tag.getKeyString());
				attr.setValue(parsedText);
				attachment.addAttribute(tag.getKeyString(), attr);
			}
			break;
		case DATA:
			break;
		case ATTACHMENT:
			if (attachment != null) {
				repositoryTaskData.addAttachment(attachment);
			}
			break;

		// IGNORED ELEMENTS
		// case REPORTER_ACCESSIBLE:
		// case CLASSIFICATION_ID:
		// case CLASSIFICATION:
		// case CCLIST_ACCESSIBLE:
		// case EVERCONFIRMED:
		case BUGZILLA:
			break;
// Considering solution for bug#198714			
//		case DELTA_TS:
//			RepositoryTaskAttribute delta_ts_attribute = repositoryTaskData.getAttribute(tag.getKeyString());
//			if (delta_ts_attribute == null) {
//				delta_ts_attribute = attributeFactory.createAttribute(tag.getKeyString());
//				repositoryTaskData.addAttribute(tag.getKeyString(), delta_ts_attribute);
//			}
//			delta_ts_attribute.setValue(BugzillaClient.stripTimeZone(parsedText));
//			break;
		case BUG:
			// Reached end of bug. Need to set LONGDESCLENGTH to number of
			// comments
			RepositoryTaskAttribute numCommentsAttribute = repositoryTaskData.getAttribute(BugzillaReportElement.LONGDESCLENGTH.getKeyString());
			if (numCommentsAttribute == null) {
				numCommentsAttribute = attributeFactory.createAttribute(BugzillaReportElement.LONGDESCLENGTH.getKeyString());
				numCommentsAttribute.setValue("" + repositoryTaskData.getComments().size());
				repositoryTaskData.addAttribute(BugzillaReportElement.LONGDESCLENGTH.getKeyString(),
						numCommentsAttribute);
			} else {
				numCommentsAttribute.setValue("" + repositoryTaskData.getComments().size());
			}

			// Set the creator name on all attachments
			for (RepositoryAttachment attachment : repositoryTaskData.getAttachments()) {
				TaskComment taskComment = attachIdToComment.get(attachment.getId());
				if (taskComment != null) {
					attachment.setCreator(taskComment.getAuthor());
				}
				attachment.setAttributeValue(RepositoryTaskAttribute.ATTACHMENT_URL,
						repositoryTaskData.getRepositoryUrl() + IBugzillaConstants.URL_GET_ATTACHMENT_SUFFIX
								+ attachment.getId());
				attachment.setRepositoryKind(repositoryTaskData.getRepositoryKind());
				attachment.setRepositoryUrl(repositoryTaskData.getRepositoryUrl());
				attachment.setTaskId(repositoryTaskData.getId());
			}
			break;

		case BLOCKED:
		case DEPENDSON:
			RepositoryTaskAttribute dependancyAttribute = repositoryTaskData.getAttribute(tag.getKeyString());
			if (dependancyAttribute == null) {
				dependancyAttribute = attributeFactory.createAttribute(tag.getKeyString());
				dependancyAttribute.setValue(parsedText);
				repositoryTaskData.addAttribute(tag.getKeyString(), dependancyAttribute);
			} else {
				if (dependancyAttribute.getValue().equals("")) {
					dependancyAttribute.setValue(parsedText);
				} else {
					dependancyAttribute.setValue(dependancyAttribute.getValue() + ", " + parsedText);
				}
			}
			break;
		// All others added as report attribute
		default:
			RepositoryTaskAttribute attribute = repositoryTaskData.getAttribute(tag.getKeyString());
			if (attribute == null) {
				attribute = attributeFactory.createAttribute(tag.getKeyString());
				attribute.setValue(parsedText);
				repositoryTaskData.addAttribute(tag.getKeyString(), attribute);
			} else {
				attribute.addValue(parsedText);
			}
			break;
		}

	}

	/** determines attachment id from comment */
	private void parseAttachment(TaskComment taskComment, String commentText) {

		String attachmentID = "";

		if (commentText.startsWith(COMMENT_ATTACHMENT_STRING)) {
			int endIndex = commentText.indexOf(")");
			if (endIndex > 0 && endIndex < commentText.length()) {
				attachmentID = commentText.substring(COMMENT_ATTACHMENT_STRING.length(), endIndex);
				if (!attachmentID.equals("")) {
					taskComment.setHasAttachment(true);
					taskComment.setAttachmentId(attachmentID);
					attachIdToComment.put(attachmentID, taskComment);
				}
			}
		}
	}
}
