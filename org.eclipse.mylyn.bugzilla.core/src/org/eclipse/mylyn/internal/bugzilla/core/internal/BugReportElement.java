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

package org.eclipse.mylar.internal.bugzilla.core.internal;

/**
 * Bugzilla XML element enum. Each enum has the field name
 * and associated xml element tag name.
 * 
 * @author Rob Elves
 */
public enum BugReportElement {
	
	// Format: ENUM ( "pretty name", "xml key" )
	
	BUGZILLA ("bugzilla", "bugzilla"),
	BUG ("bug","bug"),
	BUG_ID ("Bug", "bug_id"),
	CREATION_TS ("Creation Date", "creation_ts"),
	SHORT_DESC ("Summary", "short_desc"),
	DELTA_TS ("Last Modification", "delta_ts"),
	REPORTER_ACCESSIBLE ("reporter_accessible", "reporter_accessible"),
	CCLIST_ACCESSIBLE ("cclist_accessible", "cclist_accessible"),
	CLASSIFICATION_ID ("Classification ID", "classification_id"),
	CLASSIFICATION ("Classification", "classification"),
	PRODUCT ("Product", "product"),
	COMPONENT ("Component", "component"),
	VERSION ("Version", "version"),
	REP_PLATFORM ("Platform", "rep_platform"),
	OP_SYS ("OS", "op_sys"),
	BUG_STATUS ("Status", "bug_status"),
	PRIORITY ("Priority", "priority"),
	BUG_SEVERITY ("Severity", "bug_severity"),
	TARGET_MILESTONE ("Target Milestone", "target_milestone"), 
	EVERCONFIRMED ("everconfirmed", "everconfirmed"), 
	REPORTER ("Reporter", "reporter"), 
	ASSIGNED_TO ("Assigned To", "assigned_to"), 
	CC ("CC", "cc"), 
	LONG_DESC ("Description", "long_desc"),
	WHO ("who", "who"),
	BUG_WHEN ("bug_when", "bug_when"), 
	THETEXT ("thetext", "thetext"), 
	ATTACHMENT ("attachment", "attachment"), 
	ATTACHID ("attachid", "attachid"), 
	DATE ("Date", "date"), 
	DESC ("desc", "desc"), 
	FILENAME ("filename", "filename"), 
	TYPE ("type", "type"), 
	DATA ("data", "data"),
	UNKNOWN ("UNKNOWN", "UNKNOWN");
	
	private final String prettyName;
	private final String keyString;

	BugReportElement(String prettyName, String fieldName) {		
		this.prettyName = prettyName;
		keyString = fieldName;
	}

	public String getKeyString() {
		return keyString;
	}

	public String toString() {
		return prettyName;
	}

//	public BugReportElementTag fromString(String str) {
//		for (BugReportElementTag tag : BugReportElementTag.values()) {
//			if (tag.toString().equals(str)) {
//				return tag;
//			}
//		}
//		return UNKNOWN;
//	}
}
