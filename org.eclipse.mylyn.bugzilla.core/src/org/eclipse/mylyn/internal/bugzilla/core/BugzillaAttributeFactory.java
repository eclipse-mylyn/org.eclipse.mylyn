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

package org.eclipse.mylar.internal.bugzilla.core;

import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class BugzillaAttributeFactory extends AbstractAttributeFactory {

	private static final long serialVersionUID = 5087501781682994759L;

	@Override
	public String mapCommonAttributeKey(String key) {
		if (key == null) {
			return key;
		} else if (key.equals(RepositoryTaskAttribute.COMMENT_DATE)) {
			return BugzillaReportElement.BUG_WHEN.getKeyString();
		} else if (key.equals(RepositoryTaskAttribute.USER_OWNER)) {
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
		}
		return key;
	}
	
	@Override
	public boolean getIsHidden(String key) {
		try {
			return BugzillaReportElement.valueOf(key.trim().toUpperCase()).isHidden();
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public String getName(String key) {
		try {
			return BugzillaReportElement.valueOf(key.trim().toUpperCase()).toString();
		} catch (IllegalArgumentException e) {
			return "<unknown>";
		}
	}

	@Override
	public boolean isReadOnly(String key) {
		try {
			return BugzillaReportElement.valueOf(key.trim().toUpperCase()).isReadOnly();
		} catch (IllegalArgumentException e) {
			return true;
		}
	}

}
