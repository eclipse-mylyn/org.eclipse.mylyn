/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gitlab.core;

import java.util.Map;

import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class GitlabTaskSchema extends GitlabNewTaskSchema {

	private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

	private static final GitlabTaskSchema instance = new GitlabTaskSchema();

	public static GitlabTaskSchema getDefault() {
		return instance;
	}

	public final Field CREATED = inheritFrom(parent.DATE_CREATION).create();

	public final Field UPDATED = inheritFrom(parent.DATE_MODIFICATION).create();

	public final Field COMPLETED = inheritFrom(parent.DATE_COMPLETION).create();

	public final Field AUTHOR = inheritFrom(parent.USER_REPORTER).create();

	public final Field TASK_KEY = inheritFrom(parent.TASK_KEY).create();

	public final Field URL = inheritFrom(parent.TASK_URL).addFlags(Flag.ATTRIBUTE).create();

	public final Field USER_NOTES_COUNT = createField("user_notes_count", "User Notes Count",
			TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field BLOCKING_ISSUE_COUNT = createField("blocking_issues_count", "Blocking Issue Count",
			TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field MERGE_REQUEST_COUNT = createField("merge_requests_count", "Merge Request Count",
			TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field UPVOTES = createField("upvotes", "Up Votes", TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field DOWNVOTES = createField("downvotes", "Down Notes", TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

//	public final Field WEB_URL = createField("web_url", "WEB URL", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE);

	public final Field HAS_TASKS = createField("has_tasks", "has Tasks", TaskAttribute.TYPE_BOOLEAN, Flag.ATTRIBUTE);

	public final Field SUBSCRIBED = createField("subscribed", "subscribed", TaskAttribute.TYPE_BOOLEAN, Flag.ATTRIBUTE);

	public final Field CONFIDENTIAL = createField("confidential", "confidential", TaskAttribute.TYPE_BOOLEAN,
			Flag.ATTRIBUTE);

	public final Field NEW_COMMENT = inheritFrom(parent.NEW_COMMENT).create();

	public final Field DISCUSSION_LOCKED = createField("discussion_locked", "Discussion locked",
			TaskAttribute.TYPE_BOOLEAN, Flag.ATTRIBUTE);

	public final Field IID = createField("iid", "interne ID", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE);

	public final Field OPERATION = createField(TaskAttribute.OPERATION, "Operation", TaskAttribute.TYPE_OPERATION);

	public final Field CLOSED_BY = createField("closed_by", "closed by", TaskAttribute.TYPE_PERSON, Flag.PEOPLE, //$NON-NLS-1$
			Flag.READ_ONLY);

	public final Field ASSIGNED_TO = inheritFrom(parent.USER_ASSIGNED).label("Assigned to").create();

	public final Field TYPE = createField("type", "Type", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE);

	public final Field TASK_STATUS = createField("task_status", "Task Status", TaskAttribute.TYPE_SHORT_TEXT,
			Flag.ATTRIBUTE);

	public final Field TASK_LABELS = createField("labels", "Labels", TaskAttribute.TYPE_MULTI_SELECT, Flag.ATTRIBUTE);

	public final Field TASK_MILESTONE = createField("milestone", "Milestone", TaskAttribute.TYPE_SINGLE_SELECT,
			Flag.ATTRIBUTE);

	public final Field DUE_DATE = createField("due_date", "Due date", TaskAttribute.TYPE_DATE, Flag.ATTRIBUTE);

	private static Map<String, String> json2AttributeMapper = Map
			.ofEntries(Map.entry("project_id", getDefault().PRODUCT.getKey()) //$NON-NLS-1$
					, Map.entry("description", getDefault().DESCRIPTION.getKey()) //$NON-NLS-1$
					, Map.entry("title", getDefault().SUMMARY.getKey()) //$NON-NLS-1$
					, Map.entry("state", getDefault().STATUS.getKey()) //$NON-NLS-1$
					, Map.entry("assignee", getDefault().ASSIGNED_TO.getKey()) //$NON-NLS-1$
					, Map.entry("author", getDefault().AUTHOR.getKey()) //$NON-NLS-1$
					, Map.entry("id", getDefault().TASK_KEY.getKey()) //$NON-NLS-1$
					, Map.entry("severity", getDefault().PRIORITY.getKey()) //$NON-NLS-1$
					, Map.entry("created_at", getDefault().CREATED.getKey()) //$NON-NLS-1$
					, Map.entry("updated_at", getDefault().UPDATED.getKey()) //$NON-NLS-1$
					, Map.entry("closed_at", getDefault().COMPLETED.getKey()) //$NON-NLS-1$
					, Map.entry("web_url", getDefault().URL.getKey()) //$NON-NLS-1$
			);

	private static Map<String, String> attribute2jsonMapper = Map
			.ofEntries(Map.entry(getDefault().PRODUCT.getKey(), "project_id") //$NON-NLS-1$
					, Map.entry(getDefault().DESCRIPTION.getKey(), "description") //$NON-NLS-1$
					, Map.entry(getDefault().SUMMARY.getKey(), "title") //$NON-NLS-1$
					, Map.entry(getDefault().STATUS.getKey(), "state") //$NON-NLS-1$
					, Map.entry(getDefault().ASSIGNED_TO.getKey(), "assignee") //$NON-NLS-1$
					, Map.entry(getDefault().AUTHOR.getKey(), "author") //$NON-NLS-1$
					, Map.entry(getDefault().TASK_KEY.getKey(), "id") //$NON-NLS-1$
					, Map.entry(getDefault().PRIORITY.getKey(), "severity") //$NON-NLS-1$
					, Map.entry(getDefault().CREATED.getKey(), "created_at") //$NON-NLS-1$
					, Map.entry(getDefault().UPDATED.getKey(), "updated_at") //$NON-NLS-1$
					, Map.entry(getDefault().COMPLETED.getKey(), "closed_at") //$NON-NLS-1$
					, Map.entry(getDefault().URL.getKey(), "web_url") //$NON-NLS-1$
			);

	public static String getAttributeNameFromJsonName(String fieldName) {
		String result = json2AttributeMapper.get(fieldName);
		if (result == null) {
			result = fieldName;
		}
		return result;
	}

	public static String getJsonNameFromAttributeName(String attributeName) {
		String result = attribute2jsonMapper.get(attributeName);
		if (result == null) {
			result = attributeName;
		}
		return result;
	}

}
