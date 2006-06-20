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

/**
 * Bugzilla XML element enum. Each enum has the attribute name
 * and associated xml element tag name.
 * 
 * @author Rob Elves
 */
public enum BugzillaReportElement {
	// Format: ENUM ( "pretty name", "xml key", <hidden: true/false>, <readonly: true/false>)
	// Hidden elements are not automatically displayed in ui	
	ACTUAL_TIME ("Hours Worked:", "actual_time", true),
	ADD_COMMENT ("Additional Comments:", "comment", true, false),
	ASSIGNED_TO ("Assigned to:", "assigned_to", false, true),
	ATTACHID ("attachid", "attachid"),
	ATTACHMENT ("attachment", "attachment"),
	BLOCKED ("Bug blocks:", "blocked"),
	BUG ("bug","bug", true),
	BUG_FILE_LOC ("URL:", "bug_file_loc", true),
	BUG_ID ("Bug:", "bug_id", true),
	BUG_SEVERITY ("Severity:", "bug_severity", false),
	BUG_STATUS ("Status:", "bug_status", false, true),
	BUG_WHEN ("bug_when", "bug_when", true, true),
	BUGZILLA ("bugzilla", "bugzilla", true),
	CC ("CC:", "cc", true, true),
	CCLIST_ACCESSIBLE ("cclist_accessible", "cclist_accessible", true),
	CLASSIFICATION ("Classification:", "classification", true),
	CLASSIFICATION_ID ("Classification ID:", "classification_id", true),
	COMPONENT ("Component:", "component", false),
	CREATION_TS ("Creation date:", "creation_ts", true),
	CTYPE ("Content Type", "ctype"),
	DATA ("data", "data"), 
	DATE ("Date", "date"), 
	DEADLINE ("Deadline:", "deadline", true, true),
	DELTA_TS ("Last Modification", "delta_ts", true), 
	DEPENDSON ("Bug depends on:", "dependson"), 
	DESC ("desc", "desc"), 
	EVERCONFIRMED ("everconfirmed", "everconfirmed", true),
	ESTIMATED_TIME ("Estimated Time:", "estimated_time", true),
	FILENAME ("filename", "filename"),
	IS_OBSOLETE ("Obsolete", "isobsolete", true), 
	KEYWORDS ("Keywords:", "keywords", true),
	LONG_DESC ("Description:", "long_desc"), 
	LONGDESCLENGTH ("Number of comments", "longdesclength", true), 
	NEWCC ("Add CC:", "newcc", true), 
	OP_SYS ("OS:", "op_sys", false), 
	PRIORITY ("Priority:", "priority", false), 
	PRODUCT ("Product:", "product", false), 
	REP_PLATFORM ("Platform:", "rep_platform", false),
	REPORTER ("Reporter:", "reporter", false, true),
	REPORTER_ACCESSIBLE ("reporter_accessible", "reporter_accessible", true),
	RESOLUTION ("Resolution:", "resolution", false, true), // Exiting bug field, new cc
	REMAINING_TIME( "Hours Left:", "remaining_time", true),
	SHORT_DESC ("Summary:", "short_desc", true),
	TARGET_MILESTONE ("Target milestone:", "target_milestone", false),
	THETEXT ("thetext", "thetext"),
	TYPE ("type", "type"),
	UNKNOWN ("UNKNOWN", "UNKNOWN"),
	VERSION ("Version:", "version", false),
	VOTES ("Votes:", "votes", false, true),
	WORK_TIME("Add Time:", "work_time", true, false),
	WHO ("who", "who"),
	QA_CONTACT("QA Contact", "qa_contact", false, false),
	ADDSELFCC ("Add self to CC", "addselfcc", true, false),
	// Used by search engine
	LI ("used by search engine", "li", true),
	ID ("used by search engine", "id", true),
	SHORT_SHORT_DESC ("used by search engine", "short_short_desc", false),
	SEQ ("used by search engine", "seq", false),	
	RESULT ("used by search engine", "result", false),
	RDF ("used by search engine", "rdf", false),
	INSTALLATION ("used by search engine", "installation", false),
	BUGS ("used by search engine", "bugs", false);
	
	private final boolean isHidden;
	
	private final boolean isReadOnly;
	
	private final String keyString;
	
	private final String prettyName;

	BugzillaReportElement(String prettyName, String fieldName) {		
		this(prettyName, fieldName, false, false);
	}
	
	BugzillaReportElement(String prettyName, String fieldName, boolean hidden) {		
		this(prettyName, fieldName, hidden, false);
	}
	
	BugzillaReportElement(String prettyName, String fieldName, boolean hidden, boolean readonly) {		
		this.prettyName = prettyName;
		this.keyString = fieldName;
		this.isHidden = hidden;
		this.isReadOnly = readonly;
	}

	public String getKeyString() {
		return keyString;
	}

	public boolean isHidden() {
		return isHidden;
	}	
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public String toString() {
		return prettyName;
	}
}
