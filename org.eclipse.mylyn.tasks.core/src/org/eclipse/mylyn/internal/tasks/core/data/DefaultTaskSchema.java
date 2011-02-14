/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskSchema extends AbstractTaskSchema {

	private static DefaultTaskSchema instance = new DefaultTaskSchema();

	public static Field getField(String taskKey) {
		return instance.getFieldByKey(taskKey);
	}

	public static final Field ADD_SELF_CC = instance.createField(TaskAttribute.ADD_SELF_CC,
			Messages.DefaultTaskSchema_Add_Self_to_CC_Label, TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_AUTHOR = instance.createField(TaskAttribute.ATTACHMENT_AUTHOR,
			Messages.DefaultTaskSchema_Author_Label, TaskAttribute.TYPE_PERSON);

	public static final Field ATTACHMENT_CONTENT_TYPE = instance.createField(TaskAttribute.ATTACHMENT_CONTENT_TYPE,
			Messages.DefaultTaskSchema_Content_Type_Label, TaskAttribute.TYPE_SHORT_TEXT);

	public static final Field ATTACHMENT_DATE = instance.createField(TaskAttribute.ATTACHMENT_DATE,
			Messages.DefaultTaskSchema_Created_Label, TaskAttribute.TYPE_DATETIME, Flag.READ_ONLY);

	public static final Field ATTACHMENT_DESCRIPTION = instance.createField(TaskAttribute.ATTACHMENT_DESCRIPTION,
			Messages.DefaultTaskSchema_Description_Label, TaskAttribute.TYPE_SHORT_RICH_TEXT);

	public static final Field ATTACHMENT_FILENAME = instance.createField(TaskAttribute.ATTACHMENT_FILENAME,
			Messages.DefaultTaskSchema_Filename_Label, TaskAttribute.TYPE_SHORT_TEXT);

	public static final Field ATTACHMENT_ID = instance.createField(TaskAttribute.ATTACHMENT_ID,
			Messages.DefaultTaskSchema_ID_Label, TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY);

	public static final Field ATTACHMENT_IS_DEPRECATED = instance.createField(TaskAttribute.ATTACHMENT_IS_DEPRECATED,
			Messages.DefaultTaskSchema_Deprecated_Label, TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_IS_PATCH = instance.createField(TaskAttribute.ATTACHMENT_IS_PATCH,
			Messages.DefaultTaskSchema_Patch_Label, TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_REPLACE_EXISTING = instance.createField(
			TaskAttribute.ATTACHMENT_REPLACE_EXISTING, Messages.DefaultTaskSchema_Replace_existing_attachment,
			TaskAttribute.TYPE_BOOLEAN);

	public static final Field ATTACHMENT_SIZE = instance.createField(TaskAttribute.ATTACHMENT_SIZE,
			Messages.DefaultTaskSchema_Size_Label, TaskAttribute.TYPE_LONG, Flag.READ_ONLY);

	public static final Field ATTACHMENT_URL = instance.createField(TaskAttribute.ATTACHMENT_URL,
			Messages.DefaultTaskSchema_URL_Label, TaskAttribute.TYPE_URL);

	public static final Field COMMENT_ATTACHMENT_ID = instance.createField(TaskAttribute.COMMENT_ATTACHMENT_ID,
			Messages.DefaultTaskSchema_Attachment_ID_Label, TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY);

	public static final Field COMMENT_AUTHOR = instance.createField(TaskAttribute.COMMENT_AUTHOR,
			Messages.DefaultTaskSchema_Author_Label, TaskAttribute.TYPE_PERSON, Flag.READ_ONLY);

	public static final Field COMMENT_DATE = instance.createField(TaskAttribute.COMMENT_DATE,
			Messages.DefaultTaskSchema_Created_Label, TaskAttribute.TYPE_DATETIME, Flag.READ_ONLY);

	public static final Field COMMENT_HAS_ATTACHMENT = instance.createField(TaskAttribute.COMMENT_HAS_ATTACHMENT,
			Messages.DefaultTaskSchema_Attachment_Label, TaskAttribute.TYPE_BOOLEAN, Flag.READ_ONLY);

	public static final Field COMMENT_NUMBER = instance.createField(TaskAttribute.COMMENT_NUMBER,
			Messages.DefaultTaskSchema_Number_Label, TaskAttribute.TYPE_INTEGER, Flag.READ_ONLY);

	public static final Field COMMENT_TEXT = instance.createField(TaskAttribute.COMMENT_TEXT,
			Messages.DefaultTaskSchema_Description_Label, TaskAttribute.TYPE_LONG_RICH_TEXT, Flag.READ_ONLY);

	public static final Field COMMENT_URL = instance.createField(TaskAttribute.COMMENT_URL,
			Messages.DefaultTaskSchema_URL_Label, TaskAttribute.TYPE_URL, Flag.READ_ONLY);

	public static final Field COMPONENT = instance.createField(TaskAttribute.COMPONENT,
			Messages.DefaultTaskSchema_Component_Label, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field DATE_COMPLETION = instance.createField(TaskAttribute.DATE_COMPLETION,
			Messages.DefaultTaskSchema_Completion_Label, TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DATE_CREATION = instance.createField(TaskAttribute.DATE_CREATION,
			Messages.DefaultTaskSchema_Created_Label, TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DATE_DUE = instance.createField(TaskAttribute.DATE_DUE,
			Messages.DefaultTaskSchema_Due_Label, TaskAttribute.TYPE_DATE);

	public static final Field DATE_MODIFICATION = instance.createField(TaskAttribute.DATE_MODIFICATION,
			Messages.DefaultTaskSchema_Modified_Label, TaskAttribute.TYPE_DATE, Flag.READ_ONLY);

	public static final Field DESCRIPTION = instance.createField(TaskAttribute.DESCRIPTION,
			Messages.DefaultTaskSchema_Description_Label, TaskAttribute.TYPE_LONG_RICH_TEXT);

	public static final Field KEYWORDS = instance.createField(TaskAttribute.KEYWORDS,
			Messages.DefaultTaskSchema_Keywords_Label, TaskAttribute.TYPE_MULTI_SELECT, Flag.ATTRIBUTE);

	public static final Field NEW_COMMENT = instance.createField(TaskAttribute.COMMENT_NEW,
			Messages.DefaultTaskSchema_Rank_Label, TaskAttribute.TYPE_LONG_RICH_TEXT);

	public static final Field PRIORITY = instance.createField(TaskAttribute.PRIORITY,
			Messages.DefaultTaskSchema_Priority_Label, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field PRODUCT = instance.createField(TaskAttribute.PRODUCT,
			Messages.DefaultTaskSchema_Product_Label, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field RANK = instance.createField(TaskAttribute.RANK, Messages.DefaultTaskSchema_Rank_Label,
			TaskAttribute.TYPE_INTEGER, Flag.READ_ONLY);

	public static final Field RESOLUTION = instance.createField(TaskAttribute.RESOLUTION,
			Messages.DefaultTaskSchema_Resolution_Label, TaskAttribute.TYPE_SINGLE_SELECT, Flag.READ_ONLY);

	public static final Field SEVERITY = instance.createField(TaskAttribute.SEVERITY,
			Messages.DefaultTaskSchema_Severity_Label, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field STATUS = instance.createField(TaskAttribute.STATUS,
			Messages.DefaultTaskSchema_Status_Label, TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY);

	public static final Field SUMMARY = instance.createField(TaskAttribute.SUMMARY,
			Messages.DefaultTaskSchema_Summary_Label, TaskAttribute.TYPE_SHORT_RICH_TEXT);

	public static final Field TASK_KEY = instance.createField(TaskAttribute.TASK_KEY,
			Messages.DefaultTaskSchema_Key_Label, TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY);

	public static final Field TASK_KIND = instance.createField(TaskAttribute.TASK_KIND,
			Messages.DefaultTaskSchema_Kind_Label, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE);

	public static final Field USER_ASSIGNED = instance.createField(TaskAttribute.USER_ASSIGNED,
			Messages.DefaultTaskSchema_Owner_Label, TaskAttribute.TYPE_PERSON, Flag.PEOPLE);

	public static final Field USER_REPORTER = instance.createField(TaskAttribute.USER_REPORTER,
			Messages.DefaultTaskSchema_Reporter_Label, TaskAttribute.TYPE_PERSON, Flag.PEOPLE);

	public static final Field TASK_URL = instance.createField(TaskAttribute.TASK_URL,
			Messages.DefaultTaskSchema_URL_Label, TaskAttribute.TYPE_URL, Flag.READ_ONLY);

}
