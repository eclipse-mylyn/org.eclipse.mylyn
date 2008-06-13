/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	CC(Key.CC, "CC:", TaskAttribute.USER_CC, TaskAttribute.TYPE_SHORT_TEXT, EnumSet.of(Flag.PEOPLE)),

	CHANGE_TIME(Key.CHANGE_TIME, "Last Modification:", TaskAttribute.DATE_MODIFICATION, TaskAttribute.TYPE_DATE,
			EnumSet.of(Flag.READ_ONLY)),

	COMPONENT(Key.COMPONENT, "Component:", TaskAttribute.PRODUCT, TaskAttribute.TYPE_SINGLE_SELECT,
			EnumSet.of(Flag.ATTRIBUTE)),

	DESCRIPTION(Key.DESCRIPTION, "Description:", TaskAttribute.DESCRIPTION, TaskAttribute.TYPE_LONG_RICH_TEXT),

	ID(Key.ID, "ID:", TaskAttribute.TASK_KEY, TaskAttribute.TYPE_SHORT_TEXT, EnumSet.of(Flag.PEOPLE)),

	KEYWORDS(Key.KEYWORDS, "Keywords:", TaskAttribute.KEYWORDS, TaskAttribute.TYPE_SHORT_TEXT,
			EnumSet.of(Flag.ATTRIBUTE)),

	MILESTONE(Key.MILESTONE, "Milestone:", null, TaskAttribute.TYPE_SINGLE_SELECT, EnumSet.of(Flag.ATTRIBUTE)),

	OWNER(Key.OWNER, "Assigned to:", TaskAttribute.USER_ASSIGNED, TaskAttribute.TYPE_PERSON, EnumSet.of(Flag.PEOPLE)),

	PRIORITY(Key.PRIORITY, "Priority:", TaskAttribute.PRIORITY, TaskAttribute.TYPE_SINGLE_SELECT,
			EnumSet.of(Flag.ATTRIBUTE)),

	REPORTER(Key.REPORTER, "Reporter:", TaskAttribute.USER_REPORTER, TaskAttribute.TYPE_PERSON,
			EnumSet.of(Flag.READ_ONLY)),

	RESOLUTION(Key.RESOLUTION, "Resolution:", TaskAttribute.RESOLUTION, TaskAttribute.TYPE_SINGLE_SELECT),

	SEVERITY(Key.SEVERITY, "Severity:", null, TaskAttribute.TYPE_SINGLE_SELECT, EnumSet.of(Flag.ATTRIBUTE)),

	STATUS(Key.STATUS, "Status:", TaskAttribute.STATUS, TaskAttribute.TYPE_SHORT_TEXT),

	SUMMARY(Key.SUMMARY, "Summary:", TaskAttribute.SUMMARY, TaskAttribute.TYPE_SHORT_RICH_TEXT),

	TIME(Key.TIME, "Created:", TaskAttribute.DATE_CREATION, TaskAttribute.TYPE_DATE, EnumSet.of(Flag.READ_ONLY)),

	TYPE(Key.TYPE, "Type:", null, TaskAttribute.TYPE_SINGLE_SELECT, EnumSet.of(Flag.ATTRIBUTE)),

	VERSION(Key.VERSION, "Version:", null, TaskAttribute.TYPE_SINGLE_SELECT, EnumSet.of(Flag.ATTRIBUTE));

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

	TracAttribute(Key tracKey, String prettyName, String taskKey, String type, EnumSet<Flag> flags) {
		this.tracKey = tracKey.getKey();
		this.taskKey = taskKey;
		this.prettyName = prettyName;
		this.type = type;
		this.flags = flags;
	}

	TracAttribute(Key tracKey, String prettyName, String taskKey, String type) {
		this(tracKey, prettyName, taskKey, type, TracAttributeMapper.NO_FLAGS);
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
