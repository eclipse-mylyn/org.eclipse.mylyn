/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.internal.trac.core.TracAttributeMapper.Flag;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public enum TracAttribute {

	CC(Key.CC, Messages.TracAttribute_CC, TaskAttribute.USER_CC, TaskAttribute.TYPE_SHORT_TEXT, Flag.PEOPLE),

	CHANGE_TIME(Key.CHANGE_TIME, Messages.TracAttribute_Last_Modification, TaskAttribute.DATE_MODIFICATION, TaskAttribute.TYPE_DATE,
			Flag.READ_ONLY),

	COMPONENT(Key.COMPONENT, Messages.TracAttribute_Component, TaskAttribute.PRODUCT, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE),

	DESCRIPTION(Key.DESCRIPTION, Messages.TracAttribute_Description, TaskAttribute.DESCRIPTION, TaskAttribute.TYPE_LONG_RICH_TEXT),

	ID(Key.ID, Messages.TracAttribute_ID, TaskAttribute.TASK_KEY, TaskAttribute.TYPE_SHORT_TEXT, Flag.PEOPLE),

	KEYWORDS(Key.KEYWORDS, Messages.TracAttribute_Keywords, TaskAttribute.KEYWORDS, TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE),

	MILESTONE(Key.MILESTONE, Messages.TracAttribute_Milestone, null, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE),

	OWNER(Key.OWNER, Messages.TracAttribute_Assigned_to, TaskAttribute.USER_ASSIGNED, TaskAttribute.TYPE_PERSON, Flag.PEOPLE),

	PRIORITY(Key.PRIORITY, Messages.TracAttribute_Priority, TaskAttribute.PRIORITY, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE),

	REPORTER(Key.REPORTER, Messages.TracAttribute_Reporter, TaskAttribute.USER_REPORTER, TaskAttribute.TYPE_PERSON, Flag.READ_ONLY),

	RESOLUTION(Key.RESOLUTION, Messages.TracAttribute_Resolution, TaskAttribute.RESOLUTION, TaskAttribute.TYPE_SINGLE_SELECT),

	SEVERITY(Key.SEVERITY, Messages.TracAttribute_Severity, null, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE),

	STATUS(Key.STATUS, Messages.TracAttribute_Status, TaskAttribute.STATUS, TaskAttribute.TYPE_SHORT_TEXT),

	SUMMARY(Key.SUMMARY, Messages.TracAttribute_Summary, TaskAttribute.SUMMARY, TaskAttribute.TYPE_SHORT_RICH_TEXT),

	TIME(Key.TIME, Messages.TracAttribute_Created, TaskAttribute.DATE_CREATION, TaskAttribute.TYPE_DATE, Flag.READ_ONLY),

	TYPE(Key.TYPE, Messages.TracAttribute_Type, TaskAttribute.TASK_KIND, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE),

	VERSION(Key.VERSION, Messages.TracAttribute_Version, null, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	static Map<String, TracAttribute> attributeByTracKey = new HashMap<String, TracAttribute>();

	static Map<String, String> tracKeyByTaskKey = new HashMap<String, String>();

	private final String tracKey;

	private final String prettyName;

	private final String taskKey;

	private final String type;

	private EnumSet<Flag> flags;

	public static TracAttribute getByTaskKey(String taskKey) {
		for (TracAttribute attribute : values()) {
			if (taskKey.equals(attribute.getTaskKey())) {
				return attribute;
			}
		}
		return null;
	}

	public static TracAttribute getByTracKey(String tracKey) {
		for (TracAttribute attribute : values()) {
			if (tracKey.equals(attribute.getTracKey())) {
				return attribute;
			}
		}
		return null;
	}

	TracAttribute(Key tracKey, String prettyName, String taskKey, String type, Flag firstFlag, Flag... moreFlags) {
		this.tracKey = tracKey.getKey();
		this.taskKey = taskKey;
		this.prettyName = prettyName;
		this.type = type;
		if (firstFlag == null) {
			this.flags = TracAttributeMapper.NO_FLAGS;
		} else {
			this.flags = EnumSet.of(firstFlag, moreFlags);
		}
	}

	TracAttribute(Key tracKey, String prettyName, String taskKey, String type) {
		this(tracKey, prettyName, taskKey, type, null);
	}

	public String getTaskKey() {
		return taskKey;
	}

	public String getTracKey() {
		return tracKey;
	}

	public String getKind() {
		if (flags.contains(Flag.ATTRIBUTE)) {
			return TaskAttribute.KIND_DEFAULT;
		} else if (flags.contains(Flag.PEOPLE)) {
			return TaskAttribute.KIND_PEOPLE;
		}
		return null;
	}

	public String getType() {
		return type;
	}

	public boolean isReadOnly() {
		return flags.contains(Flag.READ_ONLY);
	}

	@Override
	public String toString() {
		return prettyName;
	}

}
