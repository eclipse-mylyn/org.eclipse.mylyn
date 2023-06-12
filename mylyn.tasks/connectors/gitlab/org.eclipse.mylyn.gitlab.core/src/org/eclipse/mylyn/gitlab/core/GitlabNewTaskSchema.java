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
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Flag;

import com.google.common.collect.ImmutableMap;

public class GitlabNewTaskSchema extends AbstractTaskSchema {

	private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

	private static final GitlabNewTaskSchema instance = new GitlabNewTaskSchema();

	public static GitlabNewTaskSchema getDefault() {
		return instance;
	}

	public final Field PRODUCT = inheritFrom(parent.PRODUCT).addFlags(Flag.REQUIRED).create();

	public final Field DESCRIPTION = inheritFrom(parent.DESCRIPTION).addFlags(Flag.REQUIRED).create();

	public final Field SUMMARY = inheritFrom(parent.SUMMARY).addFlags(Flag.REQUIRED).create();

	public final Field STATUS = inheritFrom(parent.STATUS).create();

	public final Field PRIORITY = inheritFrom(parent.PRIORITY).create();

	public final Field ISSUE_TYPE = createField("issue_type", "Issue Type", TaskAttribute.TYPE_SINGLE_SELECT,
			Flag.ATTRIBUTE);

	private static ImmutableMap<String, String> json2AttributeMapper = new ImmutableMap.Builder<String, String>()
			.put("project_id", getDefault().PRODUCT.getKey()) //$NON-NLS-1$
			.put("description", getDefault().DESCRIPTION.getKey()) //$NON-NLS-1$
			.put("title", getDefault().SUMMARY.getKey()) //$NON-NLS-1$
			.put("state", getDefault().STATUS.getKey()) //$NON-NLS-1$
			.put("severity", getDefault().PRIORITY.getKey()) //$NON-NLS-1$

			.build();

	private static ImmutableMap<String, String> attribute2jsonMapper = new ImmutableMap.Builder<String, String>()
			.put(getDefault().PRODUCT.getKey(), "project_id") //$NON-NLS-1$
			.put(getDefault().DESCRIPTION.getKey(), "description") //$NON-NLS-1$
			.put(getDefault().SUMMARY.getKey(), "title") //$NON-NLS-1$
			.put(getDefault().STATUS.getKey(), "state") //$NON-NLS-1$
			.put(getDefault().PRIORITY.getKey(), "severity") //$NON-NLS-1$
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
