/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

/**
 * Bugzilla XML element enum. Each enum has the attribute name and associated xml element tag name.
 * 
 * @author Rob Elves
 */
public enum BugzillaReportElement {
	// Format: ENUM ( "pretty name", "xml key", <hidden: true/false>, <readonly: true/false>)
	// Hidden elements are not automatically displayed in ui
	STATUS_WHITEBOARD("Status Whiteboard:", "status_whiteboard", true, false), ACTUAL_TIME("Hours Worked:",
			"actual_time", true), ADD_COMMENT("Additional Comments:", "comment", true, false), ASSIGNED_TO(
					"Assigned to:", "assigned_to", true, true), ASSIGNED_TO_NAME("Assigned to:", "assigned_to_name", true, true), ATTACHID(
							"attachid", "attachid"), ATTACHMENT("attachment", "attachment"), BLOCKED("Blocks:", "blocked", true, false), BUG(
									"bug", "bug", true), BUG_FILE_LOC("URL:", "bug_file_loc", true, false), BUG_ID("Bug:", "bug_id", true), BUG_SEVERITY(
											"Severity:", "bug_severity", false), BUG_STATUS("Status:", "bug_status", true, true), BUG_WHEN("bug_when",
													"bug_when", true, true), BUGZILLA("bugzilla", "bugzilla", true), CC("CC:", "cc", true, true), CCLIST_ACCESSIBLE(
															"CC List", "cclist_accessible", true), CLASSIFICATION("Classification:", "classification", true), CLASSIFICATION_ID(
																	"Classification ID:", "classification_id", true), COMPONENT("Component:", "component", false), CREATION_TS(
																			"Opened:", "creation_ts", true), CTYPE("Content Type", "ctype"), DATA("data", "data"), DATE("Date", "date"), DEADLINE(
																					"Deadline:", "deadline", true, true), DELTA_TS("Modified:", "delta_ts", true), DEPENDSON(
																							"Depends on (Subtasks):", "dependson", true, false), DESC("desc", "desc", true, true), EVERCONFIRMED(
																									"everconfirmed", "everconfirmed", true), ESTIMATED_TIME("Estimated Time:", "estimated_time", true), FILENAME(
																											"filename", "filename"), GROUP("Group", "group", true, true), IS_OBSOLETE("Obsolete", "isobsolete", true), IS_PATCH(
																													"Patch", "ispatch", true), KEYWORDS("Keywords:", "keywords", true), LONG_DESC("Description:", "long_desc"), LONGDESCLENGTH(
																															"Number of comments", "longdesclength", true), NEWCC("Add CC:", "newcc", true), OP_SYS("OS:", "op_sys",
																																	false), PRIORITY("Priority:", "priority", false, false), PRODUCT("Product:", "product", false), REP_PLATFORM(
																																			"Platform:", "rep_platform", false), REPORTER("Reporter:", "reporter", true, true), REPORTER_NAME(
																																					"Reporter:", "reporter_name", true, true), REPORTER_ACCESSIBLE("Reporter", "reporter_accessible", true), RESOLUTION(
																																							"Resolution:", "resolution", false, true), // Exiting bug field, new cc
																																							REMAINING_TIME("Hours Left:", "remaining_time", true), SET_DEFAULT_ASSIGNEE("Reassign to default assignee",
																																									"set_default_assignee", false), SHORT_DESC("Summary:", "short_desc", true), SIZE("Size:", "size"), TARGET_MILESTONE(
																																											"Target milestone:", "target_milestone", false), THETEXT("thetext", "thetext", false, true), TYPE("type",
																																											"type"), UNKNOWN("UNKNOWN", "UNKNOWN"), VERSION("Version:", "version", false), INSTALL_VERSION(
																																													"version of bugzilla installed", "install_version", true), VOTES("Votes:", "votes", true, true), WORK_TIME(
																																															"Add Time:", "work_time", true, false), WHO("who", "who"), WHO_NAME("who_name", "who_name", true, true), QA_CONTACT(
																																																	"QA Contact", "qa_contact", true, false),QA_CONTACT_NAME("QA Contact", "qa_contact_name", true, true), ADDSELFCC("Add self to CC", "addselfcc", true, false),
																																																	// Used by search engine
																																																	LI("used by search engine", "li", true), ID("used by search engine", "id", true), SHORT_SHORT_DESC(
																																																			"used by search engine", "short_short_desc", false), SEQ("used by search engine", "seq", false), RESULT(
																																																					"used by search engine", "result", false), RDF("used by search engine", "rdf", false), INSTALLATION(
																																																							"used by search engine", "installation", false), BUGS("used by search engine", "bugs", false), STATUS_OPEN(
																																																									"open status values", "status_open", true, true), NEW_COMMENT("new comment", "new_comment", true, false);

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
