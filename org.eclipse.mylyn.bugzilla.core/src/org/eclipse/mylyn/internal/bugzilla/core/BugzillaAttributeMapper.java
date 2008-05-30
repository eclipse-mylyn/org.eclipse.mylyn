/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * @author Rob Elves
 */
public class BugzillaAttributeMapper extends TaskAttributeMapper {

	private static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm";

	private static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

	private static final String delta_ts_format = DATE_FORMAT_2;

	private static final String creation_ts_format = DATE_FORMAT_1;

	/**
	 * public for testing Bugzilla 2.18 uses DATE_FORMAT_1 but later versions use DATE_FORMAT_2 Using lowest common
	 * denominator DATE_FORMAT_1
	 */
	public static final String comment_creation_ts_format = DATE_FORMAT_1;

	private static final String attachment_creation_ts_format = DATE_FORMAT_1;

	public BugzillaAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	@Override
	public Date getDateValue(TaskAttribute attribute) {
		if (attribute == null) {
			return null;
		}
		String dateString = attribute.getValue();
		String id = attribute.getId();
		Date parsedDate = getDate(id, dateString);
		if (parsedDate == null) {
			parsedDate = super.getDateValue(attribute);
		}
		return parsedDate;
	}

	private Date getDate(String attributeId, String dateString) {
		Date parsedDate = null;
		try {
			if (attributeId.equals(BugzillaReportElement.DELTA_TS.getKey())) {
				parsedDate = new SimpleDateFormat(delta_ts_format).parse(dateString);
			} else if (attributeId.equals(BugzillaReportElement.CREATION_TS.getKey())) {
				parsedDate = new SimpleDateFormat(creation_ts_format).parse(dateString);
			} else if (attributeId.equals(BugzillaReportElement.BUG_WHEN.getKey())) {
				parsedDate = new SimpleDateFormat(comment_creation_ts_format).parse(dateString);
			} else if (attributeId.equals(BugzillaReportElement.DATE.getKey())) {
				parsedDate = new SimpleDateFormat(attachment_creation_ts_format).parse(dateString);
			}
		} catch (ParseException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}
		return parsedDate;
	}

	@Override
	public void setDateValue(TaskAttribute attribute, Date date) {
		if (date != null) {
			String dateString = null;
			String attributeId = attribute.getId();

			if (attributeId.equals(BugzillaReportElement.DELTA_TS.getKey())) {
				dateString = new SimpleDateFormat(delta_ts_format).format(date);
			} else if (attributeId.equals(BugzillaReportElement.CREATION_TS.getKey())) {
				dateString = new SimpleDateFormat(creation_ts_format).format(date);
			} else if (attributeId.equals(BugzillaReportElement.BUG_WHEN.getKey())) {
				dateString = new SimpleDateFormat(comment_creation_ts_format).format(date);
			} else if (attributeId.equals(BugzillaReportElement.DATE.getKey())) {
				dateString = new SimpleDateFormat(attachment_creation_ts_format).format(date);
			}

			if (dateString == null) {
				super.setDateValue(attribute, date);
			} else {
				attribute.setValue(dateString);
			}

		} else {
			attribute.clearValues();
		}
	}

	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		/*if (key.equals(TaskAttribute.NEW_CC)) {
			return BugzillaReportElement.NEWCC.getKey();
		} else*/if (key.equals(TaskAttribute.COMMENT_DATE)) {
			return BugzillaReportElement.BUG_WHEN.getKey();
		} else if (key.equals(TaskAttribute.COMMENT_AUTHOR)) {
			return BugzillaReportElement.WHO.getKey();
		} else if (key.equals(TaskAttribute.COMMENT_AUTHOR_NAME)) {
			return BugzillaReportElement.WHO_NAME.getKey();
		} else if (key.equals(TaskAttribute.USER_CC)) {
			return BugzillaReportElement.CC.getKey();
		} else if (key.equals(TaskAttribute.COMMENT_TEXT)) {
			return BugzillaReportElement.THETEXT.getKey();
		} else if (key.equals(TaskAttribute.DATE_CREATION)) {
			return BugzillaReportElement.CREATION_TS.getKey();
		} else if (key.equals(TaskAttribute.DESCRIPTION)) {
			return BugzillaReportElement.LONG_DESC.getKey();
		} else if (key.equals(TaskAttribute.ATTACHMENT_ID)) {
			return BugzillaReportElement.ATTACHID.getKey();
		} else if (key.equals(TaskAttribute.ATTACHMENT_DESCRIPTION)) {
			return BugzillaReportElement.DESC.getKey();
		} else if (key.equals(TaskAttribute.ATTACHMENT_CONTENT_TYPE)) {
			return BugzillaReportElement.CTYPE.getKey();
			//return BugzillaReportElement.TYPE.getKey();*/
		} else if (key.equals(TaskAttribute.USER_ASSIGNED)) {
			return BugzillaReportElement.ASSIGNED_TO.getKey();
		} else if (key.equals(TaskAttribute.USER_ASSIGNED_NAME)) {
			return BugzillaReportElement.ASSIGNED_TO_NAME.getKey();
		} else if (key.equals(TaskAttribute.RESOLUTION)) {
			return BugzillaReportElement.RESOLUTION.getKey();
		} else if (key.equals(TaskAttribute.STATUS)) {
			return BugzillaReportElement.BUG_STATUS.getKey();
		} else if (key.equals(TaskAttribute.DATE_MODIFICATION)) {
			return BugzillaReportElement.DELTA_TS.getKey();
		} else if (key.equals(TaskAttribute.USER_REPORTER)) {
			return BugzillaReportElement.REPORTER.getKey();
		} else if (key.equals(TaskAttribute.USER_REPORTER_NAME)) {
			return BugzillaReportElement.REPORTER_NAME.getKey();
		} else if (key.equals(TaskAttribute.SUMMARY)) {
			return BugzillaReportElement.SHORT_DESC.getKey();
		} else if (key.equals(TaskAttribute.PRODUCT)) {
			return BugzillaReportElement.PRODUCT.getKey();
		} else if (key.equals(TaskAttribute.KEYWORDS)) {
			return BugzillaReportElement.KEYWORDS.getKey();
		} else if (key.equals(TaskAttribute.ATTACHMENT_DATE)) {
			return BugzillaReportElement.DATE.getKey();
		} else if (key.equals(TaskAttribute.ATTACHMENT_SIZE)) {
			return BugzillaReportElement.SIZE.getKey();
		} else if (key.equals(TaskAttribute.ADD_SELF_CC)) {
			return BugzillaReportElement.ADDSELFCC.getKey();
		} else if (key.equals(TaskAttribute.PRIORITY)) {
			return BugzillaReportElement.PRIORITY.getKey();
		} else if (key.equals(TaskAttribute.COMMENT_NEW)) {
			return BugzillaReportElement.NEW_COMMENT.getKey();
		} else if (key.equals(TaskAttribute.COMPONENT)) {
			return BugzillaReportElement.COMPONENT.getKey();
		}
		return super.mapToRepositoryKey(parent, key);
	}

	@Override
	public TaskAttribute getAssoctiatedAttribute(TaskAttribute taskAttribute) {
		String id = taskAttribute.getMetaData().getValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID);
		if (id != null) {
			// operation associated input attributes are stored on the root attribute
			if (TaskAttribute.TYPE_OPERATION.equals(taskAttribute.getMetaData().getType())) {
				return taskAttribute.getTaskData().getRoot().getAttribute(id);
			}
			return taskAttribute.getAttribute(id);
		}
		return null;
	}
}
