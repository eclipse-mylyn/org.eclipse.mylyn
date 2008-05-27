/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Rob Elves
 */
public enum BugzillaReportElement {

	STATUS_WHITEBOARD("Status Whiteboard:", "status_whiteboard", TaskAttribute.TYPE_LONG_TEXT, false, false),

	ACTUAL_TIME("Hours Worked:", "actual_time", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	ADD_COMMENT("Additional Comments:", "comment", TaskAttribute.TYPE_LONG_TEXT, true, false),

	ASSIGNED_TO("Assigned to:", "assigned_to", TaskAttribute.TYPE_PERSON, true, true),

	ASSIGNED_TO_NAME("Assigned to:", "assigned_to_name", TaskAttribute.TYPE_PERSON, true, true),

	ATTACHID("attachid", "attachid", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	ATTACHMENT("attachment", "attachment", TaskAttribute.TYPE_ATTACHMENT, false, false),

	BLOCKED("Blocks:", "blocked", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	BUG("bug", "bug", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	BUG_FILE_LOC("URL:", "bug_file_loc", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	BUG_ID("Bug:", "bug_id", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	BUG_SEVERITY("Severity:", "bug_severity", TaskAttribute.TYPE_SINGLE_SELECT, false, false),

	BUG_STATUS("Status:", "bug_status", TaskAttribute.TYPE_SHORT_TEXT, true, true),

	BUG_WHEN("bug_when", "bug_when", TaskAttribute.TYPE_SHORT_TEXT, true, true),

	BUGZILLA("bugzilla", "bugzilla", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	CC("CC:", "cc", TaskAttribute.TYPE_MULTI_SELECT, true, true),

	REMOVECC("Remove CC", "removecc", TaskAttribute.TYPE_MULTI_SELECT, true, true),

	CCLIST_ACCESSIBLE("CC List", "cclist_accessible", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	CLASSIFICATION("Classification:", "classification", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	CLASSIFICATION_ID("Classification ID:", "classification_id", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	COMPONENT("Component:", "component", TaskAttribute.TYPE_SINGLE_SELECT, false, false),

	CREATION_TS("Opened:", "creation_ts", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	CTYPE("Content Type", "ctype", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	DATA("data", "data", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	DATE("Date", "date", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	DEADLINE("Deadline:", "deadline", TaskAttribute.TYPE_SHORT_TEXT, true, true),

	DELTA_TS("Modified:", "delta_ts", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	DEPENDSON("Depends on (Subtasks):", "dependson", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	DESC("desc", "desc", TaskAttribute.TYPE_LONG_TEXT, true, true),

	EVERCONFIRMED("everconfirmed", "everconfirmed", TaskAttribute.TYPE_BOOLEAN, true, false),

	ESTIMATED_TIME("Estimated Time:", "estimated_time", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	FILENAME("filename", "filename", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	GROUP("Group", "group", TaskAttribute.TYPE_BOOLEAN, true, true),

	IS_OBSOLETE("Obsolete", "isobsolete", TaskAttribute.TYPE_BOOLEAN, true, false),

	IS_PATCH("Patch", "ispatch", TaskAttribute.TYPE_BOOLEAN, true, false),

	KEYWORDS("Keywords:", "keywords", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	LONG_DESC("Description:", "long_desc", TaskAttribute.TYPE_LONG_TEXT, false, false),

	LONGDESCLENGTH("Number of comments", "longdesclength", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	NEWCC("Add CC:", "newcc", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	OP_SYS("OS:", "op_sys", TaskAttribute.TYPE_SINGLE_SELECT, false, false),

	PRIORITY("Priority:", "priority", TaskAttribute.TYPE_SINGLE_SELECT, false, false),

	PRODUCT("Product:", "product", TaskAttribute.TYPE_SHORT_TEXT, false, true),

	REP_PLATFORM("Platform:", "rep_platform", TaskAttribute.TYPE_SINGLE_SELECT, false, false),

	REPORTER("Reporter:", "reporter", TaskAttribute.TYPE_PERSON, true, true),

	REPORTER_NAME("Reporter:", "reporter_name", TaskAttribute.TYPE_PERSON, true, true),

	REPORTER_ACCESSIBLE("Reporter", "reporter_accessible", TaskAttribute.TYPE_BOOLEAN, true, false),

	RESOLUTION("Resolution:", "resolution", TaskAttribute.TYPE_SHORT_TEXT, false, true), 

	REMAINING_TIME("Hours Left:", "remaining_time", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	SET_DEFAULT_ASSIGNEE("Reassign to default assignee", "set_default_assignee", TaskAttribute.TYPE_OPERATION, false,
			false),

	SHORT_DESC("Summary:", "short_desc", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	SIZE("Size:", "size", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	TARGET_MILESTONE("Target milestone:", "target_milestone", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	THETEXT("thetext", "thetext", TaskAttribute.TYPE_SHORT_TEXT, false, true),

	TYPE("type", "type", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	UNKNOWN("UNKNOWN", "UNKNOWN", TaskAttribute.TYPE_SHORT_TEXT, false, false),

	VERSION("Version:", "version", TaskAttribute.TYPE_SINGLE_SELECT, false, false),

	INSTALL_VERSION("version of bugzilla installed", "install_version", null, true, false),

	VOTES("Votes:", "votes", TaskAttribute.TYPE_SHORT_TEXT, true, true),

	WORK_TIME("Add Time:", "work_time", TaskAttribute.TYPE_SHORT_TEXT, true, false),

	WHO("who", "who", TaskAttribute.TYPE_PERSON, false, false),

	WHO_NAME("who_name", "who_name", TaskAttribute.TYPE_SHORT_TEXT, true, true),

	QA_CONTACT("QA Contact", "qa_contact", TaskAttribute.TYPE_PERSON, true, false),

	QA_CONTACT_NAME("QA Contact", "qa_contact_name", TaskAttribute.TYPE_SHORT_TEXT, true, true),

	ADDSELFCC("Add self to CC", "addselfcc", TaskAttribute.TYPE_BOOLEAN, false, false),

	STATUS_OPEN("open status values", "status_open", null, true, true),

	NEW_COMMENT("new comment", "new_comment", TaskAttribute.TYPE_LONG_TEXT, true, false),

	// Used by search engine
	LI("used by search engine", "li", null, true, false),

	ID("used by search engine", "id", null, true, false),

	SHORT_SHORT_DESC("used by search engine", "short_short_desc", null, false, false),

	SEQ("used by search engine", "seq", null, false, false),

	RESULT("used by search engine", "result", null, false, false),

	RDF("used by search engine", "rdf", null, false, false),

	INSTALLATION("used by search engine", "installation", null, false, false),

	BUGS("used by search engine", "bugs", null, false, false);

	private final boolean isHidden;

	private final boolean isReadOnly;

	private final String keyString;

	private final String prettyName;

	private final String type;

	BugzillaReportElement(String prettyName, String idKey, String type, boolean hidden, boolean readonly) {
		this.prettyName = prettyName;
		this.keyString = idKey;
		this.type = type;
		this.isHidden = hidden;
		this.isReadOnly = readonly;
	}

	public String getKey() {
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

	public String getKind() {
		return isHidden() ? null : TaskAttribute.KIND_DEFAULT;
	}

	public String getType() {
		return type;
	}
}
