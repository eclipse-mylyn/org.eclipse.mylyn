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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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
 * @author Hiroyuki Inaba (internationalization)
 */
public class SaxMultiBugReportContentHandler extends DefaultHandler {

	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

	private static final String ID_STRING_BEGIN = " (id="; //$NON-NLS-1$

	private static final String ID_STRING_END = ")"; //$NON-NLS-1$

	private StringBuffer characters;

	private TaskComment taskComment;

	private Map<String, TaskCommentMapper> attachIdToComment = new HashMap<String, TaskCommentMapper>();

	private int commentNum = 0;

	private BugzillaAttachmentMapper attachment;

	private final Map<String, TaskData> taskDataMap;

	private TaskData repositoryTaskData;

	private List<TaskComment> longDescs;

	private String errorMessage = null;

	private final List<BugzillaCustomField> customFields;

	private final TaskDataCollector collector;

	private boolean isDeprecated = false;

	private boolean isPatch = false;

	private String token;

	private String exporter;

	private TaskAttribute attachmentAttribute;

	private boolean bugParseErrorOccurred;

	private final BugzillaRepositoryConnector connector;

	private boolean useIsPrivate;

	private final TaskAttributeMapper mapper;

	private final SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //$NON-NLS-1$

	private final SimpleDateFormat simpleFormatter_deltaTS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	private String bugIDValue;

	public SaxMultiBugReportContentHandler(TaskAttributeMapper mapper, TaskDataCollector collector,
			Map<String, TaskData> taskDataMap, List<BugzillaCustomField> customFields,
			BugzillaRepositoryConnector connector) {
		this.mapper = mapper;
		this.taskDataMap = taskDataMap;
		this.customFields = customFields;
		this.collector = collector;
		this.connector = connector;
		TaskRepository taskRepository = mapper.getTaskRepository();
		String useParam = taskRepository.getProperty(IBugzillaConstants.BUGZILLA_INSIDER_GROUP);
		useIsPrivate = (useParam == null || (useParam != null && useParam.equals("true"))); //$NON-NLS-1$
	}

	public boolean errorOccurred() {
		return errorMessage != null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
		//System.err.println(String.copyValueOf(ch, start, length));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		characters = new StringBuffer();
		BugzillaAttribute tag = BugzillaAttribute.UNKNOWN;
		if (localName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
			return;
		}
		try {
			tag = BugzillaAttribute.valueOf(localName.trim().toUpperCase(Locale.ENGLISH));
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
//			String version = attributes.getValue("version");
//			urlbase = attributes.getValue("urlbase"); //$NON-NLS-1$
//			String maintainer = attributes.getValue("maintainer");
			exporter = attributes.getValue("exporter"); //$NON-NLS-1$
			break;
		case BUG:
			if (attributes != null && (attributes.getValue("error") != null)) { //$NON-NLS-1$
				errorMessage = attributes.getValue("error"); //$NON-NLS-1$
				bugParseErrorOccurred = true;
				repositoryTaskData = null;
			}
			attachIdToComment = new HashMap<String, TaskCommentMapper>();
			commentNum = 0;
			taskComment = null;
			longDescs = new ArrayList<TaskComment>();
			token = null;
			bugIDValue = null;
			break;
		case LONG_DESC:
			String is_private = attributes.getValue("isprivate"); //$NON-NLS-1$
			taskComment = new TaskComment(is_private);
			break;
		case WHO:
			if (taskComment != null) {
				if (attributes != null && attributes.getLength() > 0) {
					String name = attributes.getValue(ATTRIBUTE_NAME);
					if (name != null && name.length() > 0) {
						taskComment.authorName = name;
					}
				}
			}
			break;
		case REPORTER:
			if (attributes != null && attributes.getLength() > 0) {
				String name = attributes.getValue(ATTRIBUTE_NAME);
				if (name != null) {
					BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaAttribute.REPORTER_NAME)
							.setValue(name);
				}
			}
			break;
		case QA_CONTACT:
			if (attributes != null && attributes.getLength() > 0) {
				String name = attributes.getValue(ATTRIBUTE_NAME);
				if (name != null) {
					BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaAttribute.QA_CONTACT_NAME)
							.setValue(name);
				}
			}
			break;
		case ASSIGNED_TO:
			if (attributes != null && attributes.getLength() > 0) {
				String name = attributes.getValue(ATTRIBUTE_NAME);
				if (name != null) {
					BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaAttribute.ASSIGNED_TO_NAME)
							.setValue(name);
				}
			}
			break;
		case ATTACHMENT:
			if (attributes != null) {
				isDeprecated = "1".equals(attributes.getValue(BugzillaAttribute.IS_OBSOLETE.getKey())); //$NON-NLS-1$
				isPatch = "1".equals(attributes.getValue(BugzillaAttribute.IS_PATCH.getKey())); //$NON-NLS-1$
			}
			break;
		case FLAG:
			if (attributes != null && attributes.getLength() > 0) {
				String name = attributes.getValue(ATTRIBUTE_NAME);
				if (name != null) {
					BugzillaFlagMapper mapper = new BugzillaFlagMapper(connector);
					String requestee = attributes.getValue("requestee"); //$NON-NLS-1$
					mapper.setRequestee(requestee);
					String setter = attributes.getValue("setter"); //$NON-NLS-1$
					mapper.setSetter(setter);
					String status = attributes.getValue("status"); //$NON-NLS-1$
					mapper.setState(status);
					mapper.setFlagId(name);
					String id = attributes.getValue("id"); //$NON-NLS-1$
					if (id != null && !id.equals("")) { //$NON-NLS-1$
						/*
						 * for version 3.2rc1 and 3.2.rc2 the id was not defined so we ignore
						 * the definition
						 */
						try {
							mapper.setNumber(Integer.valueOf(id));
							TaskAttribute attribute;
							if (attachmentAttribute != null) {
								attribute = attachmentAttribute.createAttribute(BugzillaAttribute.KIND_FLAG + id);
							} else {
								attribute = repositoryTaskData.getRoot()
										.createAttribute(BugzillaAttribute.KIND_FLAG + id);
							}
							mapper.applyTo(attribute);
						} catch (NumberFormatException e) {
							// ignore
						}
					}
				}
			}
			break;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		//remove whitespaces from the end of the parsed Text
		while (characters.length() > 0 && Character.isWhitespace(characters.charAt(characters.length() - 1))) {
			characters.setLength(characters.length() - 1);
		}

		String parsedText = characters.toString();

		if (localName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
			TaskAttribute endAttribute = repositoryTaskData.getRoot().getAttribute(localName);
			if (endAttribute == null) {
				String desc = "???"; //$NON-NLS-1$
				BugzillaCustomField customField = null;
				for (BugzillaCustomField bugzillaCustomField : customFields) {
					if (localName.equals(bugzillaCustomField.getName())) {
						customField = bugzillaCustomField;
						break;
					}
				}
				if (customField != null) {
					TaskAttribute atr = repositoryTaskData.getRoot().createAttribute(localName);
					desc = customField.getDescription();
					atr.getMetaData().defaults().setLabel(desc).setReadOnly(false);
					atr.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
					atr.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
					switch (customField.getFieldType()) {
					case FreeText:
						atr.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						break;
					case DropDown:
						atr.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						break;
					case MultipleSelection:
						atr.getMetaData().setType(TaskAttribute.TYPE_MULTI_SELECT);
						break;
					case LargeText:
						atr.getMetaData().setType(TaskAttribute.TYPE_LONG_TEXT);
						break;
					case DateTime:
						atr.getMetaData().setType(TaskAttribute.TYPE_DATETIME);
						break;

					default:
						List<String> options = customField.getOptions();
						if (options.size() > 0) {
							atr.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						} else {
							atr.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						}
					}
					atr.getMetaData().setReadOnly(false);
					atr.setValue(parsedText);
				}
			} else {
				endAttribute.addValue(parsedText);
			}
		}

		BugzillaAttribute tag = BugzillaAttribute.UNKNOWN;
		try {
			tag = BugzillaAttribute.valueOf(localName.trim().toUpperCase(Locale.ENGLISH));
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}
		switch (tag) {
		case BUG_ID: {
			bugIDValue = parsedText.trim();
			repositoryTaskData = taskDataMap.get(parsedText.trim());
			if (repositoryTaskData == null) {
				errorMessage = parsedText + Messages.SaxMultiBugReportContentHandler_id_not_found;
				bugParseErrorOccurred = true;
				break;
			}

			TaskAttribute attr = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
			if (attr == null) {
				attr = BugzillaTaskDataHandler.createAttribute(repositoryTaskData, tag);
			}
			attr.setValue(parsedText);

			if (exporter != null) {
				createAttrribute(exporter, BugzillaAttribute.EXPORTER_NAME);
			} else {
				createAttrribute("", BugzillaAttribute.EXPORTER_NAME); //$NON-NLS-1$
			}

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
		case WORK_TIME:
			if (taskComment != null) {
				taskComment.timeWorked = parsedText;
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
			attachmentAttribute = repositoryTaskData.getRoot()
					.createAttribute(TaskAttribute.PREFIX_ATTACHMENT + parsedText);
			attachment = BugzillaAttachmentMapper.createFrom(attachmentAttribute);
			attachment.setLength(-1L);
			attachment.setAttachmentId(parsedText);
			attachment.setPatch(isPatch);
			attachment.setDeprecated(isDeprecated);
			attachment.setToken(null);
			break;
		case DATE:
			if (attachment != null) {
				try {
					attachment.setCreationDate(simpleFormatter.parse(parsedText));
					break;
				} catch (ParseException e) {
				} catch (NumberFormatException e) {
				}
			}
			break;
		case DESC:
			if (attachment != null) {
				attachment.setDescription(parsedText);
			}
			break;
		case FILENAME:
			if (attachment != null) {
				attachment.setFileName(parsedText);
			}
			break;
		case CTYPE:
		case TYPE:
			if (attachment != null) {
				attachment.setContentType(parsedText);
			}
			break;
		case DELTA_TS:
			if (attachment != null) {
				try {
					if (parsedText != null) {
						try {
							attachment.setDeltaDate(simpleFormatter_deltaTS.parse(parsedText));
						} catch (ParseException e) {
						}
					}
				} catch (NumberFormatException e) {
					// ignore
				}
			} else {
				createAttrribute(parsedText, tag);
			}
			break;

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
		case ATTACHMENT:
			if (attachment != null) {
				attachment.applyTo(attachmentAttribute);
			}
			isPatch = false;
			isDeprecated = false;
			attachment = null;
			attachmentAttribute = null;
			break;
		case DATA:
			// ignored
			break;
		case BUGZILLA:
			// ignored
			break;
		case BUG:
			// Reached end of bug.
			if (bugParseErrorOccurred) {
				bugParseErrorOccurred = false;
				break;
			}
			addDescriptionAndComments();

			// Need to set LONGDESCLENGTH to number of comments + 1 for description
			TaskAttribute numCommentsAttribute = repositoryTaskData.getRoot()
					.getMappedAttribute(BugzillaAttribute.LONGDESCLENGTH.getKey());
			if (numCommentsAttribute == null) {
				numCommentsAttribute = BugzillaTaskDataHandler.createAttribute(repositoryTaskData,
						BugzillaAttribute.LONGDESCLENGTH);
			}

			numCommentsAttribute.setValue("" + commentNum); //$NON-NLS-1$

			updateAttachmentMetaData();
			TaskAttribute attrCreation = repositoryTaskData.getRoot()
					.getAttribute(BugzillaAttribute.CREATION_TS.getKey());

			updateCustomFields(repositoryTaskData);

			if (token != null) {
				TaskAttribute tokenAttribute = BugzillaTaskDataHandler.createAttribute(repositoryTaskData,
						BugzillaAttribute.TOKEN);
				tokenAttribute.setValue(token);
			}

			// Work around (bug#285796) If planning enabled (ESTIMATE_TIME attr present) create DEADLINE attr since it should be present.
			TaskAttribute estimatedTimeAttr = repositoryTaskData.getRoot()
					.getMappedAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey());
			TaskAttribute deadlineAttr = repositoryTaskData.getRoot()
					.getMappedAttribute(BugzillaAttribute.DEADLINE.getKey());
			if (estimatedTimeAttr != null && deadlineAttr == null) {
				BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaAttribute.DEADLINE);
			}

			// Save/Add to collector. Guard against empty data sets
			if (attrCreation != null && !attrCreation.equals("")) { //$NON-NLS-1$
				collector.accept(repositoryTaskData);
			}
			repositoryTaskData = null;
			break;
		case BLOCKED:
			// handled similarly to DEPENDSON
		case DEPENDSON:
			TaskAttribute blockOrDepends = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
			if (blockOrDepends == null) {
				BugzillaTaskDataHandler.createAttribute(repositoryTaskData, tag).setValue(parsedText);
			} else {
				if (blockOrDepends.getValue().equals("")) { //$NON-NLS-1$
					blockOrDepends.setValue(parsedText);
				} else {
					blockOrDepends.setValue(blockOrDepends.getValue() + ", " + parsedText); //$NON-NLS-1$
				}
			}
			break;
		case DUP_ID:
			TaskAttribute duplicateOf = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
			if (duplicateOf == null) {
				BugzillaTaskDataHandler.createAttribute(repositoryTaskData, tag).setValue(parsedText);
			} else {
				if (duplicateOf.getValue().equals("")) { //$NON-NLS-1$
					duplicateOf.setValue(parsedText);
				}
			}
			break;
		case UNKNOWN:
			//ignore
			break;
		case FLAG:
			//ignore
			break;
		case TOKEN:
			if (attachment != null) {
				attachment.setToken(parsedText);
			} else {
				token = parsedText;
			}
			break;
		case ATTACHER:
			if (attachment != null) {
				IRepositoryPerson author = repositoryTaskData.getAttributeMapper()
						.getTaskRepository()
						.createPerson(parsedText);
				attachment.setAuthor(author);
			}
			break;
		case TARGET_MILESTONE:
			BugzillaUtil.createAttributeWithKindDefaultIfUsed(parsedText, tag, repositoryTaskData,
					IBugzillaConstants.BUGZILLA_PARAM_USETARGETMILESTONE, true);
			break;
		case QA_CONTACT:
			BugzillaUtil.createAttributeWithKindDefaultIfUsed(parsedText, tag, repositoryTaskData,
					IBugzillaConstants.BUGZILLA_PARAM_USEQACONTACT, true);
			break;
		case STATUS_WHITEBOARD:
			BugzillaUtil.createAttributeWithKindDefaultIfUsed(parsedText, tag, repositoryTaskData,
					IBugzillaConstants.BUGZILLA_PARAM_USESTATUSWHITEBOARD, true);
			break;
		case CLASSIFICATION:
			BugzillaUtil.createAttributeWithKindDefaultIfUsed(parsedText, tag, repositoryTaskData,
					IBugzillaConstants.BUGZILLA_PARAM_USECLASSIFICATION, false);
			break;
		case ALIAS:
			if (repositoryTaskData == null) {
				repositoryTaskData = taskDataMap.get(parsedText.trim());
				if (repositoryTaskData == null) {
					errorMessage = parsedText + Messages.SaxMultiBugReportContentHandler_id_not_found;
					bugParseErrorOccurred = true;
					break;
				} else {
					errorMessage = null;
					bugParseErrorOccurred = false;
				}
			}
			BugzillaUtil.createAttributeWithKindDefaultIfUsed(parsedText, tag, repositoryTaskData,
					IBugzillaConstants.BUGZILLA_PARAM_USEBUGALIASES, false);
			TaskAttribute attr = repositoryTaskData.getRoot().getMappedAttribute(BugzillaAttribute.BUG_ID.getKey());
			if (attr == null) {
				attr = BugzillaTaskDataHandler.createAttribute(repositoryTaskData, BugzillaAttribute.BUG_ID);
			}
			attr.setValue(bugIDValue);

			if (exporter != null) {
				createAttrribute(exporter, BugzillaAttribute.EXPORTER_NAME);
			} else {
				createAttrribute("", BugzillaAttribute.EXPORTER_NAME); //$NON-NLS-1$
			}
			break;
		case SEE_ALSO:
			TaskAttribute seeAlso = repositoryTaskData.getRoot()
					.getMappedAttribute(BugzillaAttribute.SEE_ALSO_READ.getKey());
			if (seeAlso == null) {
				BugzillaUtil.createAttributeWithKindDefaultIfUsed("", tag, repositoryTaskData,
						IBugzillaConstants.BUGZILLA_PARAM_USE_SEE_ALSO, false);
				BugzillaUtil.createAttributeWithKindDefaultIfUsed(parsedText, BugzillaAttribute.SEE_ALSO_READ,
						repositoryTaskData, IBugzillaConstants.BUGZILLA_PARAM_USE_SEE_ALSO, false);
			} else {
				if (seeAlso.getValue().equals("")) { //$NON-NLS-1$
					seeAlso.setValue(parsedText);
				} else {
					seeAlso.addValue(parsedText);
				}
			}
			break;
		case COMMENTID:
			if (taskComment != null) {
				taskComment.id = Integer.parseInt(parsedText);
			}
			break;
		default:
			createAttrribute(parsedText, tag);
			break;
		}

	}

	private TaskAttribute createAttrribute(String parsedText, BugzillaAttribute tag) {
		TaskAttribute attribute = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
		if (attribute == null) {
			attribute = BugzillaTaskDataHandler.createAttribute(repositoryTaskData, tag);
			attribute.setValue(parsedText);
		} else {
			attribute.addValue(parsedText);
		}
		return attribute;
	}

	private void updateCustomFields(TaskData taskData) {
		RepositoryConfiguration config = connector.getRepositoryConfiguration(repositoryTaskData.getRepositoryUrl());
		if (config != null) {
			for (BugzillaCustomField bugzillaCustomField : config.getCustomFields()) {

				TaskAttribute atr = taskData.getRoot().getAttribute(bugzillaCustomField.getName());
				if (atr == null) {
					atr = taskData.getRoot().createAttribute(bugzillaCustomField.getName());
				}

				if (atr != null) {
					atr.getMetaData().defaults().setLabel(bugzillaCustomField.getDescription());
					atr.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);

					switch (bugzillaCustomField.getFieldType()) {
					case FreeText:
						atr.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						break;
					case DropDown:
						atr.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						break;
					case MultipleSelection:
						atr.getMetaData().setType(TaskAttribute.TYPE_MULTI_SELECT);
						break;
					case LargeText:
						atr.getMetaData().setType(TaskAttribute.TYPE_LONG_TEXT);
						break;
					case DateTime:
						atr.getMetaData().setType(TaskAttribute.TYPE_DATETIME);
						break;
					case BugId:
						atr.getMetaData().setType(TaskAttribute.TYPE_TASK_DEPENDENCY);
						break;

					default:
						List<String> options = bugzillaCustomField.getOptions();
						if (options.size() > 0) {
							atr.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						} else {
							atr.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						}
					}
					atr.getMetaData().setReadOnly(false);
				}
			}
		}

	}

	private void updateAttachmentMetaData() {
		List<TaskAttribute> taskAttachments = repositoryTaskData.getAttributeMapper()
				.getAttributesByType(repositoryTaskData, TaskAttribute.TYPE_ATTACHMENT);
		for (TaskAttribute attachment : taskAttachments) {
			BugzillaAttachmentMapper attachmentMapper = BugzillaAttachmentMapper.createFrom(attachment);
			if (attachmentMapper.getAuthor() == null || attachmentMapper.getCreationDate() == null) {
				TaskCommentMapper taskComment = attachIdToComment.get(attachmentMapper.getAttachmentId());
				if (taskComment != null) {
					attachmentMapper.setAuthor(taskComment.getAuthor());
					attachmentMapper.setCreationDate(taskComment.getCreationDate());
				}
			}
			attachmentMapper.setUrl(repositoryTaskData.getRepositoryUrl() + IBugzillaConstants.URL_GET_ATTACHMENT_SUFFIX
					+ attachmentMapper.getAttachmentId());
			attachmentMapper.applyTo(attachment);
		}
	}

	private void addDescriptionAndComments() {
		int longDescsSize = longDescs.size() - 1;
		commentNum = 1;
		if (longDescsSize == 0) {
			addDescription(longDescs.get(0));
		} else if (longDescsSize == 1) {
			if (longDescs.get(0).createdTimeStamp.compareTo(longDescs.get(1).createdTimeStamp) <= 0) {
				// if created_0 is equal to created_1 we assume that longDescs at index 0 is the description.
				addDescription(longDescs.get(0));
				addComment(longDescs.get(1));
			} else {
				addDescription(longDescs.get(1));
				addComment(longDescs.get(0));
			}
		} else if (longDescsSize > 1) {
			String created_0 = longDescs.get(0).createdTimeStamp;
			String created_1 = longDescs.get(1).createdTimeStamp;
			String created_n = longDescs.get(longDescsSize).createdTimeStamp;
			if (created_0.compareTo(created_1) <= 0 && created_0.compareTo(created_n) < 0) {
				// if created_0 is equal to created_1 we assume that longDescs at index 0 is the description.
				addDescription(longDescs.get(0));

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
				addDescription(longDescs.get(longDescsSize));
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
	}

	private void addDescription(TaskComment comment) {
		TaskAttribute attrDescription = BugzillaTaskDataHandler.createAttribute(repositoryTaskData,
				BugzillaAttribute.LONG_DESC);
		TaskAttribute idAttribute = attrDescription.createAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_ID);
		TaskAttribute isprivateAttribute = attrDescription
				.createAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_IS_PRIVATE);
		attrDescription.setValue(comment.commentText);
		idAttribute.setValue(Integer.toString(comment.id));
		if (comment.isPrivate != null) {
			isprivateAttribute.setValue(comment.isPrivate);
		}
		if (!useIsPrivate) {
			if ("1".equals(comment.isPrivate)) { //$NON-NLS-1$
				TaskRepository taskRepository = mapper.getTaskRepository();
				taskRepository.setProperty(IBugzillaConstants.BUGZILLA_INSIDER_GROUP, "true"); //$NON-NLS-1$
				useIsPrivate = true;
			}
		}
	}

	private void addComment(TaskComment comment) {
		TaskAttribute attribute = repositoryTaskData.getRoot()
				.createAttribute(TaskAttribute.PREFIX_COMMENT + commentNum);
		TaskCommentMapper taskComment = TaskCommentMapper.createFrom(attribute);
		taskComment.setCommentId(comment.id + ""); //$NON-NLS-1$
		taskComment.setNumber(commentNum);
		taskComment.setUrl(repositoryTaskData.getRepositoryUrl() + "/show_bug.cgi?id=" //$NON-NLS-1$
				+ repositoryTaskData.getTaskId() + "#c" + commentNum); //$NON-NLS-1$
		IRepositoryPerson author = repositoryTaskData.getAttributeMapper()
				.getTaskRepository()
				.createPerson(comment.author);
		author.setName(comment.authorName);
		taskComment.setAuthor(author);
		if (useIsPrivate) {
			taskComment.setIsPrivate("1".equals(comment.isPrivate)); //$NON-NLS-1$
		} else {
			if ("1".equals(comment.isPrivate)) { //$NON-NLS-1$
				TaskRepository taskRepository = mapper.getTaskRepository();
				taskRepository.setProperty(IBugzillaConstants.BUGZILLA_INSIDER_GROUP, "true"); //$NON-NLS-1$
				useIsPrivate = true;
				taskComment.setIsPrivate("1".equals(comment.isPrivate)); //$NON-NLS-1$
			} else {
				taskComment.setIsPrivate(null);
			}
		}
		TaskAttribute attrTimestamp = attribute.createAttribute(BugzillaAttribute.BUG_WHEN.getKey());
		attrTimestamp.setValue(comment.createdTimeStamp);
		taskComment.setCreationDate(repositoryTaskData.getAttributeMapper().getDateValue(attrTimestamp));
		if (comment.commentText != null) {
			String commentText = comment.commentText.trim();
			taskComment.setText(commentText);

		}
		taskComment.applyTo(attribute);
		commentNum++;

		if (comment.timeWorked != null) {
			TaskAttribute workTime = BugzillaTaskDataHandler.createAttribute(attribute, BugzillaAttribute.WORK_TIME);
			workTime.setValue(comment.timeWorked);
		}

		parseAttachment(taskComment);
	}

	/** determines attachment id from comment */
	private void parseAttachment(TaskCommentMapper comment) {
		String attachmentID = ""; //$NON-NLS-1$
		String commentText = comment.getText();
		int firstDelimiter = commentText.indexOf("\n"); //$NON-NLS-1$
		if (firstDelimiter < 0) {
			firstDelimiter = commentText.length();
		}
		int startIndex = commentText.indexOf(ID_STRING_BEGIN);
		if (startIndex > 0 && startIndex < firstDelimiter) {
			int endIndex = commentText.indexOf(ID_STRING_END, startIndex);
			if (endIndex > 0 && endIndex < firstDelimiter) {
				startIndex += ID_STRING_BEGIN.length();
				int p = startIndex;
				while (p < endIndex) {
					char c = commentText.charAt(p);
					if (c < '0' || c > '9') {
						break;
					}
					p++;
				}
				if (p == endIndex) {
					attachmentID = commentText.substring(startIndex, endIndex);
					if (!attachmentID.equals("")) { //$NON-NLS-1$
						attachIdToComment.put(attachmentID, comment);
					}
				}
			}
		}
	}

	private static class TaskComment {

		@SuppressWarnings("unused")
		public int number;

		public int id;

		public String author;

		public String authorName;

		public String createdTimeStamp;

		public String commentText;

		public String timeWorked;

		public String isPrivate;

		public TaskComment(String isprivate) {
			this.isPrivate = isprivate;
		}
	}

}
