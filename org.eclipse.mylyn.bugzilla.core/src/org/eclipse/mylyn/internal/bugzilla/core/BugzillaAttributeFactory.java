/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class BugzillaAttributeFactory extends AbstractAttributeFactory {

	private static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm";

	private static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

	private static final String delta_ts_format = DATE_FORMAT_2;

	private static final String creation_ts_format = DATE_FORMAT_1;

	/**
	 * public for testing Bugzilla 2.18 uses DATE_FORMAT_1 but later versions
	 * use DATE_FORMAT_2 Using lowest common denominator DATE_FORMAT_1
	 */
	public static final String comment_creation_ts_format = DATE_FORMAT_1;

	private static final String attachment_creation_ts_format = DATE_FORMAT_1;
	
	private static final long serialVersionUID = 5087501781682994759L;

	@Override
	public String mapCommonAttributeKey(String key) {		
		if (key.equals(RepositoryTaskAttribute.NEW_CC)) {
			return BugzillaReportElement.NEWCC.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_DATE)) {
			return BugzillaReportElement.BUG_WHEN.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_AUTHOR)) { // was USER_OWNER
			return BugzillaReportElement.WHO.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.USER_CC)) {
			return BugzillaReportElement.CC.getKeyString();
		}  else if (key.equals(RepositoryTaskAttribute.COMMENT_TEXT)) {
			return BugzillaReportElement.THETEXT.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.DATE_CREATION)) {
			return BugzillaReportElement.CREATION_TS.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.DESCRIPTION)) {
			return BugzillaReportElement.DESC.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_ID)) {
			return BugzillaReportElement.ATTACHID.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_TYPE)) {
			return BugzillaReportElement.TYPE.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_CTYPE)) {
			return BugzillaReportElement.CTYPE.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.USER_ASSIGNED)) {
			return BugzillaReportElement.ASSIGNED_TO.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.RESOLUTION)) {
			return BugzillaReportElement.RESOLUTION.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.STATUS)) {
			return BugzillaReportElement.BUG_STATUS.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.DATE_MODIFIED)) {
			return BugzillaReportElement.DELTA_TS.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.USER_REPORTER)) {
			return BugzillaReportElement.REPORTER.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.SUMMARY)) {
			return BugzillaReportElement.SHORT_DESC.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.PRODUCT)) {
			return BugzillaReportElement.PRODUCT.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.DATE_CREATION)) {
			return BugzillaReportElement.CREATION_TS.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.KEYWORDS)) {
			return BugzillaReportElement.KEYWORDS.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.ATTACHMENT_DATE)) {
			return BugzillaReportElement.DATE.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.ADD_SELF_CC)) {
			return BugzillaReportElement.ADDSELFCC.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.PRIORITY)) {
			return BugzillaReportElement.PRIORITY.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_NEW)) {
			return BugzillaReportElement.NEW_COMMENT.getKeyString();
		} else {
			return key;
		}
	}
	
	@Override
	public boolean getIsHidden(String key) {
		try {
			return BugzillaReportElement.valueOf(key.trim().toUpperCase(Locale.ENGLISH)).isHidden();
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public String getName(String key) {
		try {
			return BugzillaReportElement.valueOf(key.trim().toUpperCase(Locale.ENGLISH)).toString();
		} catch (IllegalArgumentException e) {
			return "<unknown>";
		}
	}

	@Override
	public boolean isReadOnly(String key) {
		try {
			return BugzillaReportElement.valueOf(key.trim().toUpperCase(Locale.ENGLISH)).isReadOnly();
		} catch (IllegalArgumentException e) {
			return true;
		}
	}
	
	public Date getDateForAttributeType(String attributeKey, String dateString) {
		if (dateString == null || dateString.equals("")) {
			return null;
		}
		try {
			String mappedKey = mapCommonAttributeKey(attributeKey);
			Date parsedDate = null;
			if (mappedKey.equals(BugzillaReportElement.DELTA_TS.getKeyString())) {
				parsedDate = new SimpleDateFormat(delta_ts_format).parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.CREATION_TS.getKeyString())) {
				parsedDate = new SimpleDateFormat(creation_ts_format).parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.BUG_WHEN.getKeyString())) {
				parsedDate = new SimpleDateFormat(comment_creation_ts_format).parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.DATE.getKeyString())) {
				parsedDate = new SimpleDateFormat(attachment_creation_ts_format).parse(dateString);
			}
			return parsedDate;
		} catch (Exception e) {
			return null;
			// throw new CoreException(new Status(IStatus.ERROR,
			// BugzillaPlugin.PLUGIN_ID, 0,
			// "Error parsing date string: " + dateString, e));
		}
	}

}
