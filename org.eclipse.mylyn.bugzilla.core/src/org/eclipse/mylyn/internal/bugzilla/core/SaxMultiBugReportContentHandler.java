/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
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

	private Map<String, TaskCommentMapper> attachIdToComment = new HashMap<String, TaskCommentMapper>();

	private int commentNum = 0;

	private TaskAttachmentMapper attachment;

	private final Map<String, TaskData> taskDataMap;

	private TaskData repositoryTaskData;

	private List<TaskComment> longDescs;

	private String errorMessage = null;

	private final List<BugzillaCustomField> customFields;

	private final TaskDataCollector collector;

	private boolean isDeprecated = false;

	private boolean isPatch = false;

	private TaskAttribute attachmentAttribute;

	//private int retrieved = 1;

	public SaxMultiBugReportContentHandler(TaskAttributeMapper mapper, TaskDataCollector collector,
			Map<String, TaskData> taskDataMap, List<BugzillaCustomField> customFields) {
		this.taskDataMap = taskDataMap;
		this.customFields = customFields;
		this.collector = collector;
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
		if (localName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
			return;
		}
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
			attachIdToComment = new HashMap<String, TaskCommentMapper>();
			commentNum = 0;
			taskComment = null;
			longDescs = new ArrayList<TaskComment>();
			break;
		case LONG_DESC:
			taskComment = new TaskComment(commentNum++);
//			TaskAttributeProperties.defaults()
//					.setReadOnly(true)
//					.setKind(TaskAttribute.KIND_DEFAULT)
//					.setLabel("comment")
//					.setType(TaskAttribute.TYPE_LONG_TEXT)
//					.applyTo(taskComment);
			break;
		case WHO:
			if (taskComment != null) {
				if (attributes != null && attributes.getLength() > 0) {
					String name = attributes.getValue(ATTRIBUTE_NAME);
					if (name != null) {
						taskComment.authorName = name;
					}
//						BugzillaTaskDataHandler.createAttribute(taskComment, BugzillaReportElement.WHO_NAME).setValue(
//								name);
				}
			}
			break;
		case REPORTER:
			if (attributes != null && attributes.getLength() > 0) {
				String name = attributes.getValue(ATTRIBUTE_NAME);
				if (name != null) {
					BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaReportElement.REPORTER_NAME)
							.setValue(name);
				}
			}
			break;
		case ASSIGNED_TO:
			if (attributes != null && attributes.getLength() > 0) {
				String name = attributes.getValue(ATTRIBUTE_NAME);
				if (name != null) {
					BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaReportElement.ASSIGNED_TO_NAME)
							.setValue(name);
				}
			}
			break;
		case ATTACHMENT:
			if (attributes != null) {
				isDeprecated = "1".equals(attributes.getValue(BugzillaReportElement.IS_OBSOLETE.getKey()));
				isPatch = "1".equals(attributes.getValue(BugzillaReportElement.IS_PATCH.getKey()));
			}
			break;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		String parsedText = characters.toString();

		if (localName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
			TaskAttribute endAttribute = repositoryTaskData.getRoot().getAttribute(localName);
			if (endAttribute == null) {
				String desc = "???";
				for (BugzillaCustomField bugzillaCustomField : customFields) {
					if (localName.equals(bugzillaCustomField.getName())) {
						desc = bugzillaCustomField.getDescription();
					}
				}
				TaskAttribute atr = repositoryTaskData.getRoot().createAttribute(localName);
				atr.getMetaData().defaults().setLabel(desc).setReadOnly(true);
				atr.setValue(parsedText);
			} else {
				endAttribute.addValue(parsedText);
			}
		}

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

			TaskAttribute attr = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
			if (attr == null) {
				attr = BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaReportElement.BUG_ID);
			}
			attr.setValue(parsedText);

			break;
		}

			// Comment attributes
		case WHO:
			if (taskComment != null) {
				taskComment.author = parsedText;
			}
			break;
		case BUG_WHEN:
			if (taskComment != null) {
				taskComment.createdTimeStamp = parsedText;
			}
			break;
		case THETEXT:
			if (taskComment != null) {
				taskComment.commentText = parsedText;
			}
			break;
		case LONG_DESC:
			if (taskComment != null) {
				longDescs.add(taskComment);
			}
			break;

		// Attachment attributes
		case ATTACHID:
			attachmentAttribute = repositoryTaskData.getRoot().createAttribute(
					TaskAttribute.PREFIX_ATTACHMENT + parsedText);
			attachment = TaskAttachmentMapper.createFrom(attachmentAttribute);
			attachment.setAttachmentId(parsedText);
			attachment.setPatch(isPatch);
			attachment.setDeprecated(isDeprecated);
			break;

		case DATE:
		case DESC:
			if (attachment != null) {
				attachment.setDescription(parsedText);
			}
		case FILENAME:
			if (attachment != null) {
				attachment.setFileName(parsedText);
			}
		case CTYPE:
		case TYPE:
			if (attachment != null) {
				attachment.setContentType(parsedText);
			}
		case SIZE:
			if (attachment != null) {
				try {
					if (parsedText != null) {
						attachment.setLength(Long.parseLong(parsedText));
					}
				} catch (NumberFormatException e) {
					// ignore
				}
			}
			break;
		case DATA:
			break;
		case ATTACHMENT:
			if (attachment != null) {
				attachment.applyTo(attachmentAttribute);
			}
			isPatch = false;
			isDeprecated = false;
			attachment = null;
			attachmentAttribute = null;
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

			int longDescsSize = longDescs.size() - 1;
			commentNum = 1;
			if (longDescsSize == 0) {
				addDescription(longDescs.get(0).commentText);
			} else if (longDescsSize == 1) {
				if (longDescs.get(0).createdTimeStamp.compareTo(longDescs.get(1).createdTimeStamp) <= 0) {
					// if created_0 is equal to created_1 we assume that longDescs at index 0 is the description.
					addDescription(longDescs.get(0).commentText);
					addComment(longDescs.get(1));
				} else {
					addDescription(longDescs.get(1).commentText);
					addComment(longDescs.get(0));
				}
			} else if (longDescsSize > 1) {
				String created_0 = longDescs.get(0).createdTimeStamp;
				String created_1 = longDescs.get(1).createdTimeStamp;
				String created_n = longDescs.get(longDescsSize).createdTimeStamp;
				if (created_0.compareTo(created_1) <= 0 && created_0.compareTo(created_n) < 0) {
					// if created_0 is equal to created_1 we assume that longDescs at index 0 is the description.
//					BugzillaTaskDataHandler.createAttribute(repositoryTaskData.getRoot(),
//							BugzillaReportElement.LONG_DESC).setValue(longDescs.get(0).commentText);
					addDescription(longDescs.get(0).commentText);

					if (created_1.compareTo(created_n) < 0) {
						for (int i = 1; i <= longDescsSize; i++) {
							addComment(longDescs.get(i));
						}
					} else {
						for (int i = longDescsSize; i > 0; i--) {
							addComment(longDescs.get(i));
						}
					}
				} else {
					addDescription(longDescs.get(longDescsSize).commentText);
					if (created_0.compareTo(created_1) < 0) {
						for (int i = 0; i < longDescsSize; i++) {
							addComment(longDescs.get(i));
						}
					} else {
						for (int i = longDescsSize - 1; i >= 0; i--) {
							addComment(longDescs.get(i));
						}
					}
				}
			}

			TaskAttribute numCommentsAttribute = repositoryTaskData.getRoot().getMappedAttribute(
					BugzillaReportElement.LONGDESCLENGTH.getKey());
			TaskAttribute[] taskAttachments = repositoryTaskData.getAttributeMapper().getAttributesByType(
					repositoryTaskData, TaskAttribute.TYPE_ATTACHMENT);

			if (numCommentsAttribute == null) {
				numCommentsAttribute = BugzillaTaskDataHandler.createAttribute(repositoryTaskData,
						BugzillaReportElement.LONGDESCLENGTH);
				numCommentsAttribute.setValue("" + taskAttachments.length);
			} else {
				numCommentsAttribute.setValue(""
						+ repositoryTaskData.getRoot()
								.getMappedAttribute(BugzillaReportElement.LONGDESCLENGTH.getKey())
								.getValue());
			}

			// Set the creator name on all attachments
			for (TaskAttribute attachment : taskAttachments) {
				TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attachment);
				TaskCommentMapper taskComment = attachIdToComment.get(attachmentMapper.getAttachmentId());
				if (taskComment != null) {
					attachmentMapper.setAuthor(taskComment.getAuthor());
					attachmentMapper.setCreationDate(taskComment.getCreationDate());
				}
				attachmentMapper.setUrl(repositoryTaskData.getRepositoryUrl()
						+ IBugzillaConstants.URL_GET_ATTACHMENT_SUFFIX + attachmentMapper.getAttachmentId());
				attachmentMapper.applyTo(attachment);
			}
			collector.accept(repositoryTaskData);
			break;

		case BLOCKED:
		case DEPENDSON:
			TaskAttribute dependancyAttribute = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
			if (dependancyAttribute == null) {
				BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaReportElement.DEPENDSON).setValue(
						parsedText);
			} else {
				if (dependancyAttribute.getValue().equals("")) {
					dependancyAttribute.setValue(parsedText);
				} else {
					dependancyAttribute.setValue(dependancyAttribute.getValue() + ", " + parsedText);
				}
			}
			break;
		// All others added as report attribute
		case ASSIGNED_TO:
			TaskAttribute assignedAttribute = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
			if (assignedAttribute == null) {
				assignedAttribute = BugzillaTaskDataHandler.createAttribute(repositoryTaskData, tag);
				assignedAttribute.setValue(parsedText);
			} else {
				assignedAttribute.addValue(parsedText);
			}
			break;

		default:
			TaskAttribute defaultAttribute = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
			if (defaultAttribute == null) {
				defaultAttribute = BugzillaTaskDataHandler.createAttribute(repositoryTaskData, tag);
				defaultAttribute.setValue(parsedText);
			} else {
				defaultAttribute.addValue(parsedText);
			}
			break;
		}

	}

	private void addDescription(String commentText) {
		TaskAttribute attrDescription = repositoryTaskData.getRoot().createAttribute(TaskAttribute.DESCRIPTION);
		attrDescription.setValue(commentText);
		attrDescription.getMetaData().defaults().setReadOnly(true);
	}

	private void addComment(TaskComment comment) {
		TaskAttribute attribute = repositoryTaskData.getRoot().createAttribute(
				TaskAttribute.PREFIX_COMMENT + commentNum);
		TaskCommentMapper taskComment = TaskCommentMapper.createFrom(attribute);
		taskComment.setCommentId(commentNum + "");
		taskComment.setNumber(commentNum);
		IRepositoryPerson author = repositoryTaskData.getAttributeMapper().getTaskRepository().createPerson(
				comment.author);
		author.setName(comment.authorName);
		taskComment.setAuthor(author);
		TaskAttribute attrTimestamp = attribute.createAttribute(BugzillaReportElement.BUG_WHEN.getKey());
		attrTimestamp.setValue(comment.createdTimeStamp);
		taskComment.setCreationDate(repositoryTaskData.getAttributeMapper().getDateValue(attrTimestamp));
		taskComment.setText(comment.commentText);
		taskComment.applyTo(attribute);
		commentNum++;

		parseAttachment(taskComment);

	}

	/** determines attachment id from comment */
	private void parseAttachment(TaskCommentMapper comment) {

		String attachmentID = "";
		String commentText = comment.getText();
		if (commentText.startsWith(COMMENT_ATTACHMENT_STRING)) {
			int endIndex = commentText.indexOf(")");
			if (endIndex > 0 && endIndex < commentText.length()) {
				attachmentID = commentText.substring(COMMENT_ATTACHMENT_STRING.length(), endIndex);
				if (!attachmentID.equals("")) {
					attachIdToComment.put(attachmentID, comment);
				}
			}
		}
	}

//
//	/** determines attachment id from comment */
//	private void parseAttachment(TaskComment taskComment, String commentText) {
//
//		String attachmentID = "";
//
//		if (commentText.startsWith(COMMENT_ATTACHMENT_STRING)) {
//			int endIndex = commentText.indexOf(")");
//			if (endIndex > 0 && endIndex < commentText.length()) {
//				attachmentID = commentText.substring(COMMENT_ATTACHMENT_STRING.length(), endIndex);
//				if (!attachmentID.equals("")) {
//					taskComment.hasAttachment = true;
//					taskComment.attachmentId = attachmentID;
//					attachIdToComment.put(attachmentID, taskComment);
//				}
//			}
//		}
//	}

	private class TaskComment {

		public int number;

		public String author;

		public String authorName;

		public String createdTimeStamp;

		public String commentText;

		public boolean hasAttachment;

		public String attachmentId;

		public TaskComment(int num) {
			this.number = num;
		}
	}

}
