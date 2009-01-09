/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Rob Elves
 */
public enum BugzillaAttribute {

	STATUS_WHITEBOARD(Messages.BugzillaAttribute_Status_Whiteboard,
			"status_whiteboard", TaskAttribute.TYPE_LONG_RICH_TEXT, false, false), //$NON-NLS-1$

	ACTUAL_TIME(Messages.BugzillaAttribute_Worked, "actual_time", TaskAttribute.TYPE_SHORT_TEXT, true, true), //$NON-NLS-1$

	ADD_COMMENT(Messages.BugzillaAttribute_Additional_Comments, "comment", TaskAttribute.TYPE_LONG_TEXT, true, false), //$NON-NLS-1$

	ASSIGNED_TO(Messages.BugzillaAttribute_Assigned_to, "assigned_to", TaskAttribute.TYPE_PERSON, true, true), //$NON-NLS-1$

	ASSIGNED_TO_NAME(Messages.BugzillaAttribute_Assigned_to_NAME,
			"assigned_to_name", TaskAttribute.TYPE_PERSON, true, true), //$NON-NLS-1$

	ATTACHID(Messages.BugzillaAttribute_ATTACH_ID, "attachid", TaskAttribute.TYPE_SHORT_TEXT, false, false), //$NON-NLS-1$

	ATTACHMENT(Messages.BugzillaAttribute_attachment, "attachment", TaskAttribute.TYPE_ATTACHMENT, false, false), //$NON-NLS-1$

	BLOCKED(Messages.BugzillaAttribute_Blocks, "blocked", TaskAttribute.TYPE_TASK_DEPENDENCY, false, false), //$NON-NLS-1$

	BUG(Messages.BugzillaAttribute_bug, "bug", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	BUG_FILE_LOC(Messages.BugzillaAttribute_URL, "bug_file_loc", TaskAttribute.TYPE_URL, false, false), //$NON-NLS-1$

	BUG_ID(Messages.BugzillaAttribute_Bug_ID, "bug_id", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	BUG_SEVERITY(Messages.BugzillaAttribute_Severity, "bug_severity", TaskAttribute.TYPE_SINGLE_SELECT, false, false), //$NON-NLS-1$

	BUG_STATUS(Messages.BugzillaAttribute_Status, "bug_status", TaskAttribute.TYPE_SHORT_TEXT, true, true), //$NON-NLS-1$

	BUG_WHEN(Messages.BugzillaAttribute_bug_when, "bug_when", TaskAttribute.TYPE_DATE, true, true), //$NON-NLS-1$

	BUGZILLA(Messages.BugzillaAttribute_bugzilla, "bugzilla", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	CC(Messages.BugzillaAttribute_CC, "cc", IBugzillaConstants.EDITOR_TYPE_REMOVECC, true, true), //$NON-NLS-1$

	REMOVECC(Messages.BugzillaAttribute_Remove_CC, "removecc", IBugzillaConstants.EDITOR_TYPE_REMOVECC, true, true), //$NON-NLS-1$

	CCLIST_ACCESSIBLE(Messages.BugzillaAttribute_CC_List, "cclist_accessible", TaskAttribute.TYPE_BOOLEAN, true, false), //$NON-NLS-1$

	CLASSIFICATION(Messages.BugzillaAttribute_Classification,
			"classification", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	CLASSIFICATION_ID(Messages.BugzillaAttribute_Classification_ID,
			"classification_id", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	COMPONENT(Messages.BugzillaAttribute_Component, "component", TaskAttribute.TYPE_SINGLE_SELECT, false, false), //$NON-NLS-1$

	CREATION_TS(Messages.BugzillaAttribute_Opened, "creation_ts", TaskAttribute.TYPE_DATE, true, false), //$NON-NLS-1$

	CTYPE(Messages.BugzillaAttribute_Content_Type, "ctype", TaskAttribute.TYPE_SHORT_TEXT, false, false), //$NON-NLS-1$

	DATA(Messages.BugzillaAttribute_data, "data", TaskAttribute.TYPE_SHORT_TEXT, false, false), //$NON-NLS-1$

	DATE(Messages.BugzillaAttribute_Date, "date", TaskAttribute.TYPE_DATE, false, false), //$NON-NLS-1$

	DEADLINE(Messages.BugzillaAttribute_Due, "deadline", TaskAttribute.TYPE_DATE, true, false), //$NON-NLS-1$

	DELTA_TS(Messages.BugzillaAttribute_Modified, "delta_ts", TaskAttribute.TYPE_DATE, true, false), //$NON-NLS-1$

	DEPENDSON(Messages.BugzillaAttribute_Depends_on__Subtasks_,
			"dependson", TaskAttribute.TYPE_TASK_DEPENDENCY, false, false), //$NON-NLS-1$

	DESC(Messages.BugzillaAttribute_desc, "desc", TaskAttribute.TYPE_LONG_TEXT, true, true), //$NON-NLS-1$

	EVERCONFIRMED(Messages.BugzillaAttribute_everconfirmed, "everconfirmed", TaskAttribute.TYPE_BOOLEAN, true, false), //$NON-NLS-1$

	ESTIMATED_TIME(Messages.BugzillaAttribute_Estimated_Time,
			"estimated_time", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	FILENAME(Messages.BugzillaAttribute_filename, "filename", TaskAttribute.TYPE_SHORT_TEXT, false, false), //$NON-NLS-1$

	FLAG(Messages.BugzillaAttribute_flag, "flag", IBugzillaConstants.EDITOR_TYPE_FLAG, false, false), //$NON-NLS-1$

	GROUP(Messages.BugzillaAttribute_Group, "group", TaskAttribute.TYPE_BOOLEAN, true, true), //$NON-NLS-1$

	IS_OBSOLETE(Messages.BugzillaAttribute_Obsolete, "isobsolete", TaskAttribute.TYPE_BOOLEAN, true, false), //$NON-NLS-1$

	IS_PATCH(Messages.BugzillaAttribute_Patch, "ispatch", TaskAttribute.TYPE_BOOLEAN, true, false), //$NON-NLS-1$

	KEYWORDS(Messages.BugzillaAttribute_Keywords, "keywords", IBugzillaConstants.EDITOR_TYPE_KEYWORDS, false, false), //$NON-NLS-1$

	LONG_DESC(Messages.BugzillaAttribute_Description, "long_desc", TaskAttribute.TYPE_LONG_RICH_TEXT, true, true), //$NON-NLS-1$

	LONGDESCLENGTH(Messages.BugzillaAttribute_Number_of_comments,
			"longdesclength", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	NEWCC(Messages.BugzillaAttribute_Add_CC, "newcc", TaskAttribute.TYPE_PERSON, true, false), //$NON-NLS-1$

	OP_SYS(Messages.BugzillaAttribute_OS, "op_sys", TaskAttribute.TYPE_SINGLE_SELECT, false, false), //$NON-NLS-1$

	PRIORITY(Messages.BugzillaAttribute_Priority, "priority", TaskAttribute.TYPE_SINGLE_SELECT, false, false), //$NON-NLS-1$

	PRODUCT(Messages.BugzillaAttribute_Product, "product", TaskAttribute.TYPE_SHORT_TEXT, false, true), //$NON-NLS-1$

	REP_PLATFORM(Messages.BugzillaAttribute_Platform, "rep_platform", TaskAttribute.TYPE_SINGLE_SELECT, false, false), //$NON-NLS-1$

	REPORTER(Messages.BugzillaAttribute_Reporter, "reporter", TaskAttribute.TYPE_PERSON, true, true), //$NON-NLS-1$

	REPORTER_NAME(Messages.BugzillaAttribute_REPORT_NAME, "reporter_name", TaskAttribute.TYPE_PERSON, true, true), //$NON-NLS-1$

	REPORTER_ACCESSIBLE(Messages.BugzillaAttribute_REPORT_ACCESSIBLE, "reporter_accessible", //$NON-NLS-1$
			TaskAttribute.TYPE_BOOLEAN, true, false),

	RESOLUTION(Messages.BugzillaAttribute_Resolution, "resolution", TaskAttribute.TYPE_SHORT_TEXT, false, true), //$NON-NLS-1$

	REMAINING_TIME(Messages.BugzillaAttribute_Remaining, "remaining_time", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	SET_DEFAULT_ASSIGNEE(Messages.BugzillaAttribute_Reassign_to_default_assignee,
			"set_default_assignee", TaskAttribute.TYPE_BOOLEAN, true, //$NON-NLS-1$
			false),

	SHORT_DESC(Messages.BugzillaAttribute_Summary, "short_desc", TaskAttribute.TYPE_SHORT_RICH_TEXT, true, false), //$NON-NLS-1$

	SIZE(Messages.BugzillaAttribute_Size, "size", TaskAttribute.TYPE_SHORT_TEXT, false, false), //$NON-NLS-1$

	TARGET_MILESTONE(Messages.BugzillaAttribute_Target_milestone,
			"target_milestone", TaskAttribute.TYPE_SINGLE_SELECT, false, false), //$NON-NLS-1$

	THETEXT(Messages.BugzillaAttribute_thetext, "thetext", TaskAttribute.TYPE_SHORT_TEXT, false, true), //$NON-NLS-1$

	TYPE(Messages.BugzillaAttribute_type, "type", TaskAttribute.TYPE_SHORT_TEXT, false, false), //$NON-NLS-1$

	UNKNOWN(Messages.BugzillaAttribute_UNKNOWN, "UNKNOWN", TaskAttribute.TYPE_SHORT_TEXT, false, false), //$NON-NLS-1$

	VERSION(Messages.BugzillaAttribute_Version, "version", TaskAttribute.TYPE_SINGLE_SELECT, false, false), //$NON-NLS-1$

	INSTALL_VERSION(Messages.BugzillaAttribute_version_of_bugzilla_installed, "install_version", null, true, false), //$NON-NLS-1$

	VOTES(Messages.BugzillaAttribute_Votes, "votes", IBugzillaConstants.EDITOR_TYPE_VOTES, false, true), //$NON-NLS-1$

	WORK_TIME(Messages.BugzillaAttribute_Add, "work_time", TaskAttribute.TYPE_SHORT_TEXT, true, false), //$NON-NLS-1$

	WHO(Messages.BugzillaAttribute_who, "who", TaskAttribute.TYPE_PERSON, false, false), //$NON-NLS-1$

	WHO_NAME(Messages.BugzillaAttribute_who_name, "who_name", TaskAttribute.TYPE_SHORT_TEXT, true, true), //$NON-NLS-1$

	QA_CONTACT(Messages.BugzillaAttribute_QA_Contact, "qa_contact", TaskAttribute.TYPE_PERSON, true, false), //$NON-NLS-1$

	QA_CONTACT_NAME(Messages.BugzillaAttribute_QA_Contact_NAME,
			"qa_contact_name", TaskAttribute.TYPE_SHORT_TEXT, true, true), //$NON-NLS-1$

	ADDSELFCC(Messages.BugzillaAttribute_Add_self_to_CC, "addselfcc", TaskAttribute.TYPE_BOOLEAN, false, false), //$NON-NLS-1$

	STATUS_OPEN(Messages.BugzillaAttribute_open_status_values, "status_open", null, true, true), //$NON-NLS-1$

	NEW_COMMENT(Messages.BugzillaAttribute_new_comment, "new_comment", TaskAttribute.TYPE_LONG_RICH_TEXT, true, false), //$NON-NLS-1$

	// Used by search engine
	LI(Messages.BugzillaAttribute_used_by_search_engine_li, "li", null, true, false), //$NON-NLS-1$

	ID(Messages.BugzillaAttribute_used_by_search_engine_id, "id", null, true, false), //$NON-NLS-1$

	SHORT_SHORT_DESC(Messages.BugzillaAttribute_used_by_search_engine_desc, "short_short_desc", null, false, false), //$NON-NLS-1$

	SEQ(Messages.BugzillaAttribute_used_by_search_engine_seq, "seq", null, false, false), //$NON-NLS-1$

	RESULT(Messages.BugzillaAttribute_used_by_search_engine_result, "result", null, false, false), //$NON-NLS-1$

	RDF(Messages.BugzillaAttribute_used_by_search_engine_rdf, "rdf", null, false, false), //$NON-NLS-1$

	INSTALLATION(Messages.BugzillaAttribute_used_by_search_engine_installation, "installation", null, false, false), //$NON-NLS-1$

	BUGS(Messages.BugzillaAttribute_used_by_search_engine_bugs, "bugs", null, false, false); //$NON-NLS-1$

	private final boolean isHidden;

	private final boolean isReadOnly;

	private final String keyString;

	private final String prettyName;

	private final String type;

	public static final BugzillaAttribute[] EXTENDED_ATTRIBUTES = { DELTA_TS, BUG_SEVERITY, PRODUCT };

	BugzillaAttribute(String prettyName, String idKey, String type, boolean hidden, boolean readonly) {
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

	@Override
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
