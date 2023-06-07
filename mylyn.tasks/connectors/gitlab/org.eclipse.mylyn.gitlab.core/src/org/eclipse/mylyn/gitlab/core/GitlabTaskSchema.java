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

package org.eclipse.mylyn.gitlab.core;

import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;

import com.google.common.collect.ImmutableMap;

public class GitlabTaskSchema extends AbstractTaskSchema {

	private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

	private static final GitlabTaskSchema instance = new GitlabTaskSchema();

	public static GitlabTaskSchema getDefault() {
		return instance;
	}

	public final Field PRODUCT = inheritFrom(parent.PRODUCT).addFlags(Flag.REQUIRED).create();

	public final Field DESCRIPTION = inheritFrom(parent.DESCRIPTION).addFlags(Flag.REQUIRED).create();

	public final Field SUMMARY = inheritFrom(parent.SUMMARY).addFlags(Flag.REQUIRED).create();

	public final Field STATUS = inheritFrom(parent.STATUS).create();

	public final Field ASSIGNED_TO = inheritFrom(parent.USER_ASSIGNED).label("Assigned to").create();

	public final Field CREATED = inheritFrom(parent.DATE_CREATION).create();

	public final Field UPDATED = inheritFrom(parent.DATE_MODIFICATION).create();

	public final Field COMPLETED = inheritFrom(parent.DATE_COMPLETION).create();

	public final Field AUTHOR = inheritFrom(parent.USER_REPORTER).create();

	public final Field TASK_KEY = inheritFrom(parent.TASK_KEY).create();

	public final Field URL = inheritFrom(parent.TASK_URL).addFlags(Flag.ATTRIBUTE).create();

	public final Field PRIORITY = inheritFrom(parent.PRIORITY).create();

	public final Field ISSUE_TYPE = createField("issue_type", "Issue Type", TaskAttribute.TYPE_SHORT_TEXT,
			Flag.ATTRIBUTE);

	public final Field USER_NOTES_COUNT = createField("user_notes_count", "User Notes Count",
			TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field BLOCKING_ISSUE_COUNT = createField("blocking_issues_count", "Blocking Issue Count",
			TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field MERGE_REQUEST_COUNT = createField("merge_requests_count", "Merge Request Count",
			TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field UPVOTES = createField("upvotes", "Up Votes", TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field DOWNVOTES = createField("downvotes", "Down Notes", TaskAttribute.TYPE_INTEGER, Flag.ATTRIBUTE);

	public final Field TYPE = createField("type", "Type", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE);

//	public final Field WEB_URL = createField("web_url", "WEB URL", TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE);

	public final Field TASK_STATUS = createField("task_status", "Task Status", TaskAttribute.TYPE_SHORT_TEXT,
			Flag.ATTRIBUTE);

	public final Field HAS_TASKS = createField("has_tasks", "has Tasks", TaskAttribute.TYPE_BOOLEAN, Flag.ATTRIBUTE);

	public final Field SUBSCRIBED = createField("subscribed", "subscribed", TaskAttribute.TYPE_BOOLEAN, Flag.ATTRIBUTE);

	public final Field CONFIDENTIAL = createField("confidential", "confidential", TaskAttribute.TYPE_BOOLEAN,
			Flag.ATTRIBUTE);


	public final Field DISCUSSION_LOCKED = createField("discussion_locked", "Discussion locked",
			TaskAttribute.TYPE_BOOLEAN, Flag.ATTRIBUTE);

	public final Field GROUP = createField("group", "Group", TaskAttribute.TYPE_SHORT_TEXT);

	private static ImmutableMap<String, String> json2AttributeMapper = new ImmutableMap.Builder<String, String>()
			.put("project_id", getDefault().PRODUCT.getKey()) //$NON-NLS-1$
			.put("description", getDefault().DESCRIPTION.getKey()) //$NON-NLS-1$
			.put("title", getDefault().SUMMARY.getKey()) //$NON-NLS-1$
			.put("state", getDefault().STATUS.getKey()) //$NON-NLS-1$
			.put("assignee", getDefault().ASSIGNED_TO.getKey()) //$NON-NLS-1$
			.put("author", getDefault().AUTHOR.getKey()) //$NON-NLS-1$
			.put("id", getDefault().TASK_KEY.getKey()) //$NON-NLS-1$
			.put("severity", getDefault().PRIORITY.getKey()) //$NON-NLS-1$
			.put("created_at", getDefault().CREATED.getKey()) //$NON-NLS-1$
			.put("updated_at", getDefault().UPDATED.getKey()) //$NON-NLS-1$
			.put("closed_at", getDefault().COMPLETED.getKey()) //$NON-NLS-1$
			.put("web_url", getDefault().URL.getKey()) //$NON-NLS-1$

			.build();

	private static ImmutableMap<String, String> attribute2jsonMapper = new ImmutableMap.Builder<String, String>()
			.put(getDefault().PRODUCT.getKey(), "project_id") //$NON-NLS-1$
			.put(getDefault().DESCRIPTION.getKey(), "description") //$NON-NLS-1$
			.put(getDefault().SUMMARY.getKey(), "title") //$NON-NLS-1$
			.put(getDefault().STATUS.getKey(), "state") //$NON-NLS-1$
			.put(getDefault().ASSIGNED_TO.getKey(), "assignee") //$NON-NLS-1$
			.put(getDefault().AUTHOR.getKey(), "author") //$NON-NLS-1$
			.put(getDefault().TASK_KEY.getKey(), "id") //$NON-NLS-1$
			.put(getDefault().PRIORITY.getKey(), "severity") //$NON-NLS-1$
			.put(getDefault().CREATED.getKey(), "created_at") //$NON-NLS-1$
			.put(getDefault().UPDATED.getKey(), "updated_at") //$NON-NLS-1$
			.put(getDefault().COMPLETED.getKey(), "closed_at") //$NON-NLS-1$
			.put(getDefault().URL.getKey(), "web_url") //$NON-NLS-1$
			.build();

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
