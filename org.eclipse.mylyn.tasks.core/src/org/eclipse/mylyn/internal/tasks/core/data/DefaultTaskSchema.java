/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskSchema {

	public static class Field {

		private EnumSet<Flag> flags;

		private final String key;

		private final String label;

		private final String type;

		Field(String key, String label, String type) {
			this(key, label, type, null);
		}

		Field(String key, String label, String type, Flag firstFlag, Flag... moreFlags) {
			Assert.isNotNull(key);
			Assert.isNotNull(label);
			Assert.isNotNull(type);
			this.key = key;
			this.label = label;
			this.type = type;
			if (firstFlag == null) {
				this.flags = NO_FLAGS;
			} else {
				this.flags = EnumSet.of(firstFlag, moreFlags);
			}
		}

		public TaskAttribute createAttribute(TaskAttribute parent) {
			TaskAttribute attribute = parent.createMappedAttribute(getKey());
			// meta data
			TaskAttributeMetaData metaData = attribute.getMetaData();
			metaData.setLabel(getLabel());
			metaData.setType(getType());
			metaData.setReadOnly(isReadOnly());
			metaData.setKind(getKind());
			// options
			Map<String, String> options = getDefaultOptions();
			if (options != null) {
				for (Entry<String, String> option : options.entrySet()) {
					attribute.putOption(option.getKey(), option.getValue());
				}
			}
			return attribute;
		}

		public Map<String, String> getDefaultOptions() {
			return Collections.emptyMap();
		}

		public String getKey() {
			return key;
		}

		public String getKind() {
			if (flags.contains(Flag.ATTRIBUTE)) {
				return TaskAttribute.KIND_DEFAULT;
			} else if (flags.contains(Flag.PEOPLE)) {
				return TaskAttribute.KIND_PEOPLE;
			}
			return null;
		}

		public String getLabel() {
			return label;
		}

		public String getType() {
			return type;
		}

		public boolean isReadOnly() {
			return flags.contains(Flag.READ_ONLY);
		}

		@Override
		public String toString() {
			return getLabel();
		}

	};

	public enum Flag {
		ATTRIBUTE, PEOPLE, READ_ONLY
	}

	private static Map<String, Field> fieldByKey = new HashMap<String, Field>();

	public static final EnumSet<Flag> NO_FLAGS = EnumSet.noneOf(Flag.class);

	public static Field getField(String taskKey) {
		return fieldByKey.get(taskKey);
	}

	public static final Field ADD_SELF_CC = createField(TaskAttribute.ADD_SELF_CC, "Add Self to CC",
			TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_AUTHOR = createField(TaskAttribute.ATTACHMENT_AUTHOR, "Author",
			TaskAttribute.TYPE_PERSON);

	public static final Field ATTACHMENT_CONTENT_TYPE = createField(TaskAttribute.ATTACHMENT_CONTENT_TYPE,
			"Content-Type", TaskAttribute.TYPE_SHORT_TEXT);

	public static final Field ATTACHMENT_DATE = createField(TaskAttribute.ATTACHMENT_DATE, "Created",
			TaskAttribute.TYPE_DATETIME);

	public static final Field ATTACHMENT_DESCRIPTION = createField(TaskAttribute.ATTACHMENT_DESCRIPTION, "Description",
			TaskAttribute.TYPE_SHORT_RICH_TEXT);

	public static final Field ATTACHMENT_FILENAME = createField(TaskAttribute.ATTACHMENT_FILENAME, "Filename",
			TaskAttribute.TYPE_SHORT_TEXT);

	public static final Field ATTACHMENT_ID = createField(TaskAttribute.ATTACHMENT_ID, "ID",
			TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY);

	public static final Field ATTACHMENT_IS_DEPRECATED = createField(TaskAttribute.ATTACHMENT_IS_DEPRECATED,
			"Deprecated", TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_IS_PATCH = createField(TaskAttribute.ATTACHMENT_IS_PATCH, "Patch",
			TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_SIZE = createField(TaskAttribute.ATTACHMENT_SIZE, "Size",
			TaskAttribute.TYPE_LONG, Flag.READ_ONLY);

	public static final Field ATTACHMENT_URL = createField(TaskAttribute.ATTACHMENT_URL, "URL", TaskAttribute.TYPE_URL);

	public static final Field COMMENT_ATTACHMENT_ID = createField(TaskAttribute.COMMENT_ATTACHMENT_ID, "Attachment ID",
			TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY);

	public static final Field COMMENT_AUTHOR = createField(TaskAttribute.COMMENT_AUTHOR, "Author",
			TaskAttribute.TYPE_PERSON);

	public static final Field COMMENT_DATE = createField(TaskAttribute.COMMENT_DATE, "Created",
			TaskAttribute.TYPE_DATETIME);

	public static final Field COMMENT_HAS_ATTACHMENT = createField(TaskAttribute.COMMENT_HAS_ATTACHMENT, "Attachment",
			TaskAttribute.TYPE_BOOLEAN);

	public static final Field COMMENT_NUMBER = createField(TaskAttribute.COMMENT_NUMBER, "Number",
			TaskAttribute.TYPE_INTEGER, Flag.READ_ONLY);

	public static final Field COMMENT_TEXT = createField(TaskAttribute.COMMENT_TEXT, "Description",
			TaskAttribute.TYPE_LONG_RICH_TEXT);

	public static final Field COMMENT_URL = createField(TaskAttribute.COMMENT_URL, "URL", TaskAttribute.TYPE_URL);

	public static final Field COMPONENT = createField(TaskAttribute.COMPONENT, "Component",
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field DATE_COMPLETION = createField(TaskAttribute.DATE_COMPLETION, "Completion",
			TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DATE_CREATION = createField(TaskAttribute.DATE_CREATION, "Created",
			TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DATE_DUE = createField(TaskAttribute.DATE_DUE, "Due", TaskAttribute.TYPE_DATE);

	public static final Field DATE_MODIFICATION = createField(TaskAttribute.DATE_MODIFICATION, "Modified",
			TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DESCRIPTION = createField(TaskAttribute.DESCRIPTION, "Description",
			TaskAttribute.TYPE_LONG_RICH_TEXT);

	public static final Field KEYWORDS = createField(TaskAttribute.KEYWORDS, "Keywords",
			TaskAttribute.TYPE_MULTI_SELECT, Flag.ATTRIBUTE);

	public static final Field PRIORITY = createField(TaskAttribute.PRIORITY, "Priority",
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field PRODUCT = createField(TaskAttribute.PRIORITY, "Product",
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field RANK = createField(TaskAttribute.RANK, "Rank", TaskAttribute.TYPE_INTEGER, Flag.READ_ONLY);

	public static final Field RESOLUTION = createField(TaskAttribute.RESOLUTION, "Resolution",
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.READ_ONLY);

	public static final Field SEVERITY = createField(TaskAttribute.SEVERITY, "Severity",
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field STATUS = createField(TaskAttribute.STATUS, "Status", TaskAttribute.TYPE_SHORT_TEXT,
			Flag.READ_ONLY);

	public static final Field SUMMARY = createField(TaskAttribute.SUMMARY, "Summary",
			TaskAttribute.TYPE_SHORT_RICH_TEXT);

	public static final Field TASK_KEY = createField(TaskAttribute.TASK_KEY, "Key", TaskAttribute.TYPE_SHORT_TEXT,
			Flag.READ_ONLY);

	public static final Field TASK_KIND = createField(TaskAttribute.TASK_KIND, "Kind",
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field USER_ASSIGNED = createField(TaskAttribute.USER_ASSIGNED, "Owner",
			TaskAttribute.TYPE_PERSON, Flag.PEOPLE);

	public static final Field USER_REPORTER = createField(TaskAttribute.USER_REPORTER, "Reporter",
			TaskAttribute.TYPE_PERSON, Flag.PEOPLE);

	public static final Field TASK_URL = createField(TaskAttribute.TASK_URL, "URL", TaskAttribute.TYPE_URL,
			Flag.READ_ONLY);

	private static Field createField(String key, String label, String type) {
		return createField(key, label, type, null);
	}

	private static Field createField(String key, String label, String type, Flag firstFlag, Flag... moreFlags) {
		Field field = new Field(key, label, type, firstFlag, moreFlags);
		fieldByKey.put(key, field);
		return field;
	}

}
