/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.eclipse.mylyn.bugzilla.deprecated;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;

/**
 * @author Rob Elves
 * @author Mik Kersten
 * @deprecated
 */
@Deprecated
public class BugzillaAttributeFactory extends AbstractAttributeFactory {

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

	private static final long serialVersionUID = 5087501781682994759L;

	@Override
	public String mapCommonAttributeKey(String key) {
		if (key.equals(RepositoryTaskAttribute.NEW_CC)) {
			return BugzillaAttribute.NEWCC.getKey();
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_DATE)) {
			return BugzillaAttribute.BUG_WHEN.getKey();
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_AUTHOR)) {
			return BugzillaAttribute.WHO.getKey();
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_AUTHOR_NAME)) {
			return BugzillaAttribute.WHO_NAME.getKey();
		} else if (key.equals(RepositoryTaskAttribute.USER_CC)) {
			return BugzillaAttribute.CC.getKey();
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_TEXT)) {
			return BugzillaAttribute.THETEXT.getKey();
		} else if (key.equals(RepositoryTaskAttribute.DATE_CREATION)) {
			return BugzillaAttribute.CREATION_TS.getKey();
		} else if (key.equals(RepositoryTaskAttribute.DESCRIPTION)) {
			return BugzillaAttribute.DESC.getKey();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_ID)) {
			return BugzillaAttribute.ATTACHID.getKey();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_TYPE)) {
			return BugzillaAttribute.TYPE.getKey();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_CTYPE)) {
			return BugzillaAttribute.CTYPE.getKey();
		} else if (key.equals(RepositoryTaskAttribute.USER_ASSIGNED)) {
			return BugzillaAttribute.ASSIGNED_TO.getKey();
		} else if (key.equals(RepositoryTaskAttribute.USER_ASSIGNED_NAME)) {
			return BugzillaAttribute.ASSIGNED_TO_NAME.getKey();
		} else if (key.equals(RepositoryTaskAttribute.RESOLUTION)) {
			return BugzillaAttribute.RESOLUTION.getKey();
		} else if (key.equals(RepositoryTaskAttribute.STATUS)) {
			return BugzillaAttribute.BUG_STATUS.getKey();
		} else if (key.equals(RepositoryTaskAttribute.DATE_MODIFIED)) {
			return BugzillaAttribute.DELTA_TS.getKey();
		} else if (key.equals(RepositoryTaskAttribute.USER_REPORTER)) {
			return BugzillaAttribute.REPORTER.getKey();
		} else if (key.equals(RepositoryTaskAttribute.USER_REPORTER_NAME)) {
			return BugzillaAttribute.REPORTER_NAME.getKey();
		} else if (key.equals(RepositoryTaskAttribute.SUMMARY)) {
			return BugzillaAttribute.SHORT_DESC.getKey();
		} else if (key.equals(RepositoryTaskAttribute.PRODUCT)) {
			return BugzillaAttribute.PRODUCT.getKey();
		} else if (key.equals(RepositoryTaskAttribute.KEYWORDS)) {
			return BugzillaAttribute.KEYWORDS.getKey();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_DATE)) {
			return BugzillaAttribute.DATE.getKey();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_SIZE)) {
			return BugzillaAttribute.SIZE.getKey();
		} else if (key.equals(RepositoryTaskAttribute.ADD_SELF_CC)) {
			return BugzillaAttribute.ADDSELFCC.getKey();
		} else if (key.equals(RepositoryTaskAttribute.PRIORITY)) {
			return BugzillaAttribute.PRIORITY.getKey();
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_NEW)) {
			return BugzillaAttribute.NEW_COMMENT.getKey();
		} else if (key.equals(RepositoryTaskAttribute.COMPONENT)) {
			return BugzillaAttribute.COMPONENT.getKey();
		} else {
			return key;
		}
	}

	@Override
	public boolean isHidden(String key) {
		try {
			return BugzillaAttribute.valueOf(key.trim().toUpperCase(Locale.ENGLISH)).isHidden();
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public String getName(String key) {
		try {
			return BugzillaAttribute.valueOf(key.trim().toUpperCase(Locale.ENGLISH)).toString();
		} catch (IllegalArgumentException e) {
			return "<unknown>";
		}
	}

	@Override
	public boolean isReadOnly(String key) {
		try {
			return BugzillaAttribute.valueOf(key.trim().toUpperCase(Locale.ENGLISH)).isReadOnly();
		} catch (IllegalArgumentException e) {
			return true;
		}
	}

	@Override
	public Date getDateForAttributeType(String attributeKey, String dateString) {
		if (dateString == null || dateString.equals("")) {
			return null;
		}
		try {
			String mappedKey = mapCommonAttributeKey(attributeKey);
			Date parsedDate = null;
			if (mappedKey.equals(BugzillaAttribute.DELTA_TS.getKey())) {
				parsedDate = new SimpleDateFormat(delta_ts_format).parse(dateString);
			} else if (mappedKey.equals(BugzillaAttribute.CREATION_TS.getKey())) {
				parsedDate = new SimpleDateFormat(creation_ts_format).parse(dateString);
			} else if (mappedKey.equals(BugzillaAttribute.BUG_WHEN.getKey())) {
				parsedDate = new SimpleDateFormat(comment_creation_ts_format).parse(dateString);
			} else if (mappedKey.equals(BugzillaAttribute.DATE.getKey())) {
				parsedDate = new SimpleDateFormat(attachment_creation_ts_format).parse(dateString);
			}
			return parsedDate;
		} catch (Exception e) {
			return null;
			// throw new CoreException(new Status(IStatus.ERROR,
			// "Error parsing date string: " + dateString, e));
		}
	}

}
