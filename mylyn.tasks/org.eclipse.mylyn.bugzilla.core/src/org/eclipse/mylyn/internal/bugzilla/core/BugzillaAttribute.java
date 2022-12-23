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

import java.util.Arrays;
import java.util.EnumSet;

import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Flag;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Rob Elves
 */
public enum BugzillaAttribute {
	STATUS_WHITEBOARD(Messages.BugzillaAttribute_Status_Whiteboard, "status_whiteboard", TaskAttribute.TYPE_SHORT_TEXT, //$NON-NLS-1$
			Flag.ATTRIBUTE),

	ACTUAL_TIME(Messages.BugzillaAttribute_Worked, "actual_time", TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY), //$NON-NLS-1$

	ADD_COMMENT(Messages.BugzillaAttribute_Additional_Comments, "comment", TaskAttribute.TYPE_LONG_TEXT), //$NON-NLS-1$

	ALIAS(Messages.BugzillaAttribute_Alias, "alias", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	ASSIGNED_TO(Messages.BugzillaAttribute_Assigned_to, "assigned_to", TaskAttribute.TYPE_PERSON, Flag.PEOPLE, //$NON-NLS-1$
			Flag.READ_ONLY),

	ASSIGNED_TO_NAME(Messages.BugzillaAttribute_Assigned_to_NAME, "assigned_to_name", TaskAttribute.TYPE_PERSON, //$NON-NLS-1$
			Flag.READ_ONLY),

	ATTACHID(Messages.BugzillaAttribute_ATTACH_ID, "attachid", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE), //$NON-NLS-1$

	ATTACHMENT(Messages.BugzillaAttribute_attachment, "attachment", TaskAttribute.TYPE_ATTACHMENT, Flag.ATTRIBUTE), //$NON-NLS-1$

	BLOCKED(Messages.BugzillaAttribute_Blocks, "blocked", TaskAttribute.TYPE_TASK_DEPENDENCY, Flag.ATTRIBUTE), //$NON-NLS-1$

	BUG(Messages.BugzillaAttribute_bug, "bug", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	BUG_FILE_LOC(Messages.BugzillaAttribute_URL, "bug_file_loc", TaskAttribute.TYPE_URL, Flag.ATTRIBUTE), //$NON-NLS-1$

	BUG_ID(Messages.BugzillaAttribute_Bug_ID, "bug_id", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	BUG_SEVERITY(Messages.BugzillaAttribute_Severity, "bug_severity", TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE), //$NON-NLS-1$

	BUG_STATUS(Messages.BugzillaAttribute_Status, "bug_status", TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY), //$NON-NLS-1$

	BUG_WHEN(Messages.BugzillaAttribute_bug_when, "bug_when", TaskAttribute.TYPE_DATE, Flag.READ_ONLY), //$NON-NLS-1$

	BUGZILLA(Messages.BugzillaAttribute_bugzilla, "bugzilla", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	CC(Messages.BugzillaAttribute_CC, "cc", IBugzillaConstants.EDITOR_TYPE_REMOVECC, Flag.READ_ONLY), //$NON-NLS-1$

	REMOVECC(Messages.BugzillaAttribute_Remove_CC, "removecc", IBugzillaConstants.EDITOR_TYPE_REMOVECC, Flag.READ_ONLY), //$NON-NLS-1$

	CCLIST_ACCESSIBLE(Messages.BugzillaAttribute_CC_List, "cclist_accessible", TaskAttribute.TYPE_BOOLEAN), //$NON-NLS-1$

	CLASSIFICATION(Messages.BugzillaAttribute_Classification, "classification", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	CLASSIFICATION_ID(Messages.BugzillaAttribute_Classification_ID, "classification_id", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	COMMENTID(Messages.BugzillaAttribute_Comment_ID, "commentid", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	COMPONENT(Messages.BugzillaAttribute_Component, "component", TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE), //$NON-NLS-1$

	CONFIRM_PRODUCT_CHANGE("confirm_product_change", "confirm_product_change", TaskAttribute.TYPE_BOOLEAN), //$NON-NLS-1$ //$NON-NLS-2$

	CREATION_TS(Messages.BugzillaAttribute_Opened, "creation_ts", TaskAttribute.TYPE_DATE), //$NON-NLS-1$

	CTYPE(Messages.BugzillaAttribute_Content_Type, "ctype", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE), //$NON-NLS-1$

	DATA(Messages.BugzillaAttribute_data, "data", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE), //$NON-NLS-1$

	DATE(Messages.BugzillaAttribute_Date, "date", TaskAttribute.TYPE_DATE, Flag.ATTRIBUTE), //$NON-NLS-1$

	DEADLINE(Messages.BugzillaAttribute_Due, "deadline", TaskAttribute.TYPE_DATE), //$NON-NLS-1$

	DELTA_TS(Messages.BugzillaAttribute_Modified, "delta_ts", TaskAttribute.TYPE_DATE), //$NON-NLS-1$

	DEPENDSON(Messages.BugzillaAttribute_Depends_on__Subtasks_, "dependson", TaskAttribute.TYPE_TASK_DEPENDENCY, //$NON-NLS-1$
			Flag.ATTRIBUTE),

	DESC(Messages.BugzillaAttribute_desc, "desc", TaskAttribute.TYPE_LONG_TEXT, Flag.READ_ONLY), //$NON-NLS-1$

	DUP_ID(Messages.BugzillaAttribute_Duplicate_of, "dup_id", TaskAttribute.TYPE_TASK_DEPENDENCY, Flag.ATTRIBUTE), //$NON-NLS-1$

	EVERCONFIRMED(Messages.BugzillaAttribute_everconfirmed, "everconfirmed", TaskAttribute.TYPE_BOOLEAN), //$NON-NLS-1$

	ESTIMATED_TIME(Messages.BugzillaAttribute_Estimated_Time, "estimated_time", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	EXPORTER_NAME(Messages.BugzillaAttribute_Exporter, "exporter", TaskAttribute.TYPE_PERSON, Flag.READ_ONLY), //$NON-NLS-1$

	FILENAME(Messages.BugzillaAttribute_filename, "filename", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE), //$NON-NLS-1$

	FLAG(Messages.BugzillaAttribute_flag, "flag", IBugzillaConstants.EDITOR_TYPE_FLAG), //$NON-NLS-1$

	GROUP(Messages.BugzillaAttribute_Group, "group", TaskAttribute.TYPE_BOOLEAN, Flag.READ_ONLY), //$NON-NLS-1$

	IS_OBSOLETE(Messages.BugzillaAttribute_Obsolete, "isobsolete", TaskAttribute.TYPE_BOOLEAN), //$NON-NLS-1$

	IS_PATCH(Messages.BugzillaAttribute_Patch, "ispatch", TaskAttribute.TYPE_BOOLEAN), //$NON-NLS-1$

	IS_PRIVATE(Messages.BugzillaAttribute_Private, "isprivate", TaskAttribute.TYPE_BOOLEAN), //$NON-NLS-1$

	KEYWORDS(Messages.BugzillaAttribute_Keywords, "keywords", IBugzillaConstants.EDITOR_TYPE_KEYWORDS, Flag.ATTRIBUTE), //$NON-NLS-1$

	LONG_DESC(Messages.BugzillaAttribute_Description, "long_desc", TaskAttribute.TYPE_LONG_RICH_TEXT, Flag.READ_ONLY), //$NON-NLS-1$

	LONGDESCLENGTH(Messages.BugzillaAttribute_Number_of_comments, "longdesclength", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	NEWCC(Messages.BugzillaAttribute_Add_CC, "newcc", TaskAttribute.TYPE_MULTI_LABEL, Flag.PEOPLE), //$NON-NLS-1$

	OP_SYS(Messages.BugzillaAttribute_OS, "op_sys", TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE), //$NON-NLS-1$

	PRIORITY(Messages.BugzillaAttribute_Priority, "priority", TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE), //$NON-NLS-1$

	PRODUCT(Messages.BugzillaAttribute_Product, "product", TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE), //$NON-NLS-1$

	REP_PLATFORM(Messages.BugzillaAttribute_Platform, "rep_platform", TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE), //$NON-NLS-1$

	REPORTER(Messages.BugzillaAttribute_Reporter, "reporter", TaskAttribute.TYPE_PERSON, Flag.PEOPLE, Flag.READ_ONLY), //$NON-NLS-1$

	REPORTER_NAME(Messages.BugzillaAttribute_REPORT_NAME, "reporter_name", TaskAttribute.TYPE_PERSON, Flag.READ_ONLY), //$NON-NLS-1$

	REPORTER_ACCESSIBLE(Messages.BugzillaAttribute_REPORT_ACCESSIBLE, "reporter_accessible", //$NON-NLS-1$
			TaskAttribute.TYPE_BOOLEAN),

	RESOLUTION(Messages.BugzillaAttribute_Resolution, "resolution", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE, //$NON-NLS-1$
			Flag.READ_ONLY),

	REMAINING_TIME(Messages.BugzillaAttribute_Remaining, "remaining_time", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	REMOVE_SEE_ALSO(Messages.BugzillaAttribute_See_Also, "remove_see_also", IBugzillaConstants.EDITOR_TYPE_SEEALSO), //$NON-NLS-1$

	SEE_ALSO(Messages.BugzillaAttribute_AddSeeAlso, "see_also", TaskAttribute.TYPE_URL, Flag.ATTRIBUTE), //$NON-NLS-1$

	SEE_ALSO_READ(Messages.BugzillaAttribute_See_Also, "read_see_also", IBugzillaConstants.EDITOR_TYPE_SEEALSO, //$NON-NLS-1$
			Flag.ATTRIBUTE),

	SET_DEFAULT_ASSIGNEE(Messages.BugzillaAttribute_Reassign_to_default_assignee, "set_default_assignee", //$NON-NLS-1$
			TaskAttribute.TYPE_BOOLEAN),

	SHORT_DESC(Messages.BugzillaAttribute_Summary, "short_desc", TaskAttribute.TYPE_SHORT_RICH_TEXT), //$NON-NLS-1$

	SIZE(Messages.BugzillaAttribute_Size, "size", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE), //$NON-NLS-1$

	TARGET_MILESTONE(Messages.BugzillaAttribute_Target_milestone, "target_milestone", TaskAttribute.TYPE_SINGLE_SELECT, //$NON-NLS-1$
			Flag.ATTRIBUTE),

	THETEXT(Messages.BugzillaAttribute_thetext, "thetext", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE, //$NON-NLS-1$
			Flag.READ_ONLY),

	TYPE(Messages.BugzillaAttribute_type, "type", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE), //$NON-NLS-1$

	UNKNOWN(Messages.BugzillaAttribute_UNKNOWN, "UNKNOWN", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE), //$NON-NLS-1$

	VERSION(Messages.BugzillaAttribute_Version, "version", TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE), //$NON-NLS-1$

	INSTALL_VERSION(Messages.BugzillaAttribute_version_of_bugzilla_installed, "install_version", null), //$NON-NLS-1$

	VOTES(Messages.BugzillaAttribute_Votes, "votes", IBugzillaConstants.EDITOR_TYPE_VOTES, Flag.ATTRIBUTE, //$NON-NLS-1$
			Flag.READ_ONLY),

	WORK_TIME(Messages.BugzillaAttribute_Add, "work_time", TaskAttribute.TYPE_SHORT_TEXT), //$NON-NLS-1$

	WHO(Messages.BugzillaAttribute_who, "who", TaskAttribute.TYPE_PERSON, Flag.ATTRIBUTE), //$NON-NLS-1$

	WHO_NAME(Messages.BugzillaAttribute_who_name, "who_name", TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY), //$NON-NLS-1$

	QA_CONTACT(Messages.BugzillaAttribute_QA_Contact, "qa_contact", TaskAttribute.TYPE_PERSON, Flag.PEOPLE), //$NON-NLS-1$

	QA_CONTACT_NAME(Messages.BugzillaAttribute_QA_Contact_NAME, "qa_contact_name", TaskAttribute.TYPE_SHORT_TEXT, //$NON-NLS-1$
			Flag.READ_ONLY),

	ADDSELFCC(Messages.BugzillaAttribute_Add_self_to_CC, "addselfcc", TaskAttribute.TYPE_BOOLEAN), //$NON-NLS-1$

	STATUS_OPEN(Messages.BugzillaAttribute_open_status_values, "status_open", null, Flag.READ_ONLY), //$NON-NLS-1$

	NEW_COMMENT(Messages.BugzillaAttribute_new_comment, "new_comment", TaskAttribute.TYPE_LONG_RICH_TEXT), //$NON-NLS-1$

	TOKEN("token", "token", null, Flag.READ_ONLY), //$NON-NLS-1$ //$NON-NLS-2$

	// Used by search engine
	LI(Messages.BugzillaAttribute_used_by_search_engine_li, "li", null), //$NON-NLS-1$

	ID(Messages.BugzillaAttribute_used_by_search_engine_id, "id", null), //$NON-NLS-1$

	SHORT_SHORT_DESC(Messages.BugzillaAttribute_used_by_search_engine_desc, "short_short_desc", null, Flag.ATTRIBUTE), //$NON-NLS-1$

	SEQ(Messages.BugzillaAttribute_used_by_search_engine_seq, "seq", null, Flag.ATTRIBUTE), //$NON-NLS-1$

	RESULT(Messages.BugzillaAttribute_used_by_search_engine_result, "result", null, Flag.ATTRIBUTE), //$NON-NLS-1$

	RDF(Messages.BugzillaAttribute_used_by_search_engine_rdf, "rdf", null, Flag.ATTRIBUTE), //$NON-NLS-1$

	INSTALLATION(Messages.BugzillaAttribute_used_by_search_engine_installation, "installation", null, Flag.ATTRIBUTE), //$NON-NLS-1$

	BUGS(Messages.BugzillaAttribute_used_by_search_engine_bugs, "bugs", null, Flag.ATTRIBUTE), //$NON-NLS-1$

	QUERY_TIMESTAMP(Messages.BugzillaAttribute_Query_Timestamp, "query_timestamp", null, Flag.ATTRIBUTE), //$NON-NLS-1$

	// new in Bugzilla 3.6
	ATTACHER(Messages.BugzillaAttribute_Attacher, "attacher", null, Flag.ATTRIBUTE); //$NON-NLS-1$

	public static final String KIND_FLAG = "task.common.kind.flag"; //$NON-NLS-1$

	public static final String KIND_FLAG_TYPE = "task.common.kind.flag_type"; //$NON-NLS-1$

	private final String keyString;

	private final String prettyName;

	private final String type;

	private EnumSet<Flag> flags;

	public static final BugzillaAttribute[] EXTENDED_ATTRIBUTES = { DELTA_TS, BUG_SEVERITY, PRODUCT };

	public static final BugzillaAttribute[] PERSON_ATTRIBUTES = { ASSIGNED_TO, REPORTER, QA_CONTACT };

	BugzillaAttribute(String prettyName, String idKey, String type) {
		this(prettyName, idKey, type, (Flag[]) null);
	}

	BugzillaAttribute(String prettyName, String idKey, String type, Flag... flags) {
		this.prettyName = prettyName;
		this.keyString = idKey;
		this.type = type;
		if (flags == null || flags.length == 0) {
			this.flags = EnumSet.noneOf(Flag.class);
		} else {
			this.flags = EnumSet.copyOf(Arrays.asList(flags));
		}
	}

	public String getKey() {
		return keyString;
	}

	public boolean isReadOnly() {
		return flags.contains(Flag.READ_ONLY);
	}

	@Override
	public String toString() {
		return prettyName;
	}

	public String getKind() {
		if (flags.contains(Flag.ATTRIBUTE)) {
			return TaskAttribute.KIND_DEFAULT;
		} else if (flags.contains(Flag.PEOPLE)) {
			return TaskAttribute.KIND_PEOPLE;
		} else if (flags.contains(Flag.OPERATION)) {
			return TaskAttribute.KIND_OPERATION;
		}
		return null;
	}

	public String getType() {
		return type;
	}

}