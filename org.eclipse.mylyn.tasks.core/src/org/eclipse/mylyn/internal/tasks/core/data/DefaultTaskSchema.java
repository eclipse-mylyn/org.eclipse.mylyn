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

	public static final Field ADD_SELF_CC = createField(TaskAttribute.ADD_SELF_CC, Messages.DefaultTaskSchema_Add_Self_to_CC_Label,
			TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_AUTHOR = createField(TaskAttribute.ATTACHMENT_AUTHOR, Messages.DefaultTaskSchema_Author_Label,
			TaskAttribute.TYPE_PERSON);

	public static final Field ATTACHMENT_CONTENT_TYPE = createField(TaskAttribute.ATTACHMENT_CONTENT_TYPE,
			Messages.DefaultTaskSchema_Content_Type_Label, TaskAttribute.TYPE_SHORT_TEXT);

	public static final Field ATTACHMENT_DATE = createField(TaskAttribute.ATTACHMENT_DATE, Messages.DefaultTaskSchema_Created_Label,
			TaskAttribute.TYPE_DATETIME);

	public static final Field ATTACHMENT_DESCRIPTION = createField(TaskAttribute.ATTACHMENT_DESCRIPTION, Messages.DefaultTaskSchema_Description_Label,
			TaskAttribute.TYPE_SHORT_RICH_TEXT);

	public static final Field ATTACHMENT_FILENAME = createField(TaskAttribute.ATTACHMENT_FILENAME, Messages.DefaultTaskSchema_Filename_Label,
			TaskAttribute.TYPE_SHORT_TEXT);

	public static final Field ATTACHMENT_ID = createField(TaskAttribute.ATTACHMENT_ID, Messages.DefaultTaskSchema_ID_Label,
			TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY);

	public static final Field ATTACHMENT_IS_DEPRECATED = createField(TaskAttribute.ATTACHMENT_IS_DEPRECATED,
			Messages.DefaultTaskSchema_Deprecated_Label, TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_IS_PATCH = createField(TaskAttribute.ATTACHMENT_IS_PATCH, Messages.DefaultTaskSchema_Patch_Label,
			TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_SIZE = createField(TaskAttribute.ATTACHMENT_SIZE, Messages.DefaultTaskSchema_Size_Label,
			TaskAttribute.TYPE_LONG, Flag.READ_ONLY);

	public static final Field ATTACHMENT_URL = createField(TaskAttribute.ATTACHMENT_URL, Messages.DefaultTaskSchema_URL_Label, TaskAttribute.TYPE_URL);

	public static final Field COMMENT_ATTACHMENT_ID = createField(TaskAttribute.COMMENT_ATTACHMENT_ID, Messages.DefaultTaskSchema_Attachment_ID_Label,
			TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY);

	public static final Field COMMENT_AUTHOR = createField(TaskAttribute.COMMENT_AUTHOR, Messages.DefaultTaskSchema_Author_Label,
			TaskAttribute.TYPE_PERSON);

	public static final Field COMMENT_DATE = createField(TaskAttribute.COMMENT_DATE, Messages.DefaultTaskSchema_Created_Label,
			TaskAttribute.TYPE_DATETIME);

	public static final Field COMMENT_HAS_ATTACHMENT = createField(TaskAttribute.COMMENT_HAS_ATTACHMENT, Messages.DefaultTaskSchema_Attachment_Label,
			TaskAttribute.TYPE_BOOLEAN);

	public static final Field COMMENT_NUMBER = createField(TaskAttribute.COMMENT_NUMBER, Messages.DefaultTaskSchema_Number_Label,
			TaskAttribute.TYPE_INTEGER, Flag.READ_ONLY);

	public static final Field COMMENT_TEXT = createField(TaskAttribute.COMMENT_TEXT, Messages.DefaultTaskSchema_Description_Label,
			TaskAttribute.TYPE_LONG_RICH_TEXT);

	public static final Field COMMENT_URL = createField(TaskAttribute.COMMENT_URL, Messages.DefaultTaskSchema_URL_Label, TaskAttribute.TYPE_URL);

	public static final Field COMPONENT = createField(TaskAttribute.COMPONENT, Messages.DefaultTaskSchema_Component_Label,
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field DATE_COMPLETION = createField(TaskAttribute.DATE_COMPLETION, Messages.DefaultTaskSchema_Completion_Label,
			TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DATE_CREATION = createField(TaskAttribute.DATE_CREATION, Messages.DefaultTaskSchema_Created_Label,
			TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DATE_DUE = createField(TaskAttribute.DATE_DUE, Messages.DefaultTaskSchema_Due_Label, TaskAttribute.TYPE_DATE);

	public static final Field DATE_MODIFICATION = createField(TaskAttribute.DATE_MODIFICATION, Messages.DefaultTaskSchema_Modified_Label,
			TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DESCRIPTION = createField(TaskAttribute.DESCRIPTION, Messages.DefaultTaskSchema_Description_Label,
			TaskAttribute.TYPE_LONG_RICH_TEXT);

	public static final Field KEYWORDS = createField(TaskAttribute.KEYWORDS, Messages.DefaultTaskSchema_Keywords_Label,
			TaskAttribute.TYPE_MULTI_SELECT, Flag.ATTRIBUTE);

	public static final Field PRIORITY = createField(TaskAttribute.PRIORITY, Messages.DefaultTaskSchema_Priority_Label,
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field PRODUCT = createField(TaskAttribute.PRIORITY, Messages.DefaultTaskSchema_Product_Label,
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field RANK = createField(TaskAttribute.RANK, Messages.DefaultTaskSchema_Rank_Label, TaskAttribute.TYPE_INTEGER, Flag.READ_ONLY);

	public static final Field RESOLUTION = createField(TaskAttribute.RESOLUTION, Messages.DefaultTaskSchema_Resolution_Label,
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.READ_ONLY);

	public static final Field SEVERITY = createField(TaskAttribute.SEVERITY, Messages.DefaultTaskSchema_Severity_Label,
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field STATUS = createField(TaskAttribute.STATUS, Messages.DefaultTaskSchema_Status_Label, TaskAttribute.TYPE_SHORT_TEXT,
			Flag.READ_ONLY);

	public static final Field SUMMARY = createField(TaskAttribute.SUMMARY, Messages.DefaultTaskSchema_Summary_Label,
			TaskAttribute.TYPE_SHORT_RICH_TEXT);

	public static final Field TASK_KEY = createField(TaskAttribute.TASK_KEY, Messages.DefaultTaskSchema_Key_Label, TaskAttribute.TYPE_SHORT_TEXT,
			Flag.READ_ONLY);

	public static final Field TASK_KIND = createField(TaskAttribute.TASK_KIND, Messages.DefaultTaskSchema_Kind_Label,
			TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field USER_ASSIGNED = createField(TaskAttribute.USER_ASSIGNED, Messages.DefaultTaskSchema_Owner_Label,
			TaskAttribute.TYPE_PERSON, Flag.PEOPLE);

	public static final Field USER_REPORTER = createField(TaskAttribute.USER_REPORTER, Messages.DefaultTaskSchema_Reporter_Label,
			TaskAttribute.TYPE_PERSON, Flag.PEOPLE);

	public static final Field TASK_URL = createField(TaskAttribute.TASK_URL, Messages.DefaultTaskSchema_URL_Label, TaskAttribute.TYPE_URL,
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
