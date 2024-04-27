/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.util.AbstractMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class BugzillaRestTaskSchema extends AbstractTaskSchema {

	private static final BugzillaRestTaskSchema instance = new BugzillaRestTaskSchema();

	private static Map<String, String> field2AttributeFieldMapper = Map.ofEntries(
			new AbstractMap.SimpleEntry<>("summary", getDefault().SUMMARY.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("description", getDefault().DESCRIPTION.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("status", getDefault().STATUS.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("product", getDefault().PRODUCT.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("component", getDefault().COMPONENT.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("cc", getDefault().CC.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("severity", getDefault().SEVERITY.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("priority", getDefault().PRIORITY.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("assigned_to", getDefault().ASSIGNED_TO.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("op_sys", getDefault().OS.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("resolution", getDefault().RESOLUTION.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("version", getDefault().VERSION.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("dup_id", getDefault().DUPE_OF.getKey()), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("last_change_time", getDefault().DATE_MODIFICATION.getKey())); //$NON-NLS-1$

	private static Map<String, String> attribute2FieldMapper = Map.ofEntries(
			new AbstractMap.SimpleEntry<>(getDefault().DESCRIPTION.getKey(), "description"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().OPERATION.getKey(), "status"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().PRODUCT.getKey(), "product"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().COMPONENT.getKey(), "component"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().CC.getKey(), "cc"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().SEVERITY.getKey(), "severity"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().PRIORITY.getKey(), "priority"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().ASSIGNED_TO.getKey(), "assigned_to"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().OS.getKey(), "op_sys"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().VERSION.getKey(), "version"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().RESOLUTION.getKey(), "resolution"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>(getDefault().DUPE_OF.getKey(), "dup_id"), //$NON-NLS-1$
			new AbstractMap.SimpleEntry<>("resolutionInput", "resolution"), //$NON-NLS-1$  //$NON-NLS-2$
			new AbstractMap.SimpleEntry<>(getDefault().DATE_MODIFICATION.getKey(), "last_change_time") //$NON-NLS-1$
			);

	public static String getAttributeNameFromFieldName(String fieldName) {
		String result = field2AttributeFieldMapper.get(fieldName);
		if (result == null) {
			result = fieldName;
		}
		return result;
	}

	public static String getFieldNameFromAttributeName(String attributeName) {
		String result = attribute2FieldMapper.get(attributeName);
		if (result == null) {
			result = attributeName;
		}
		return result;
	}

	public static BugzillaRestTaskSchema getDefault() {
		return instance;
	}

	private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

	public final Field BUG_ID = createField("bug_id", Messages.BugzillaRestTaskSchema_ID, TaskAttribute.TYPE_SHORT_TEXT, Flag.REQUIRED); //$NON-NLS-1$

	public final Field PRODUCT = inheritFrom(parent.PRODUCT).addFlags(Flag.REQUIRED).create();

	public final Field COMPONENT = inheritFrom(parent.COMPONENT).addFlags(Flag.REQUIRED)
			.dependsOn(PRODUCT.getKey())
			.create();

	public final Field SUMMARY = inheritFrom(parent.SUMMARY).addFlags(Flag.REQUIRED).create();

	public final Field VERSION = createField(TaskAttribute.VERSION, Messages.BugzillaRestTaskSchema_Version, TaskAttribute.TYPE_SINGLE_SELECT, null,
			PRODUCT.getKey(), Flag.ATTRIBUTE, Flag.REQUIRED);

	public final Field DESCRIPTION = inheritFrom(parent.DESCRIPTION).addFlags(Flag.REQUIRED, Flag.READ_ONLY).create();

	public final Field OS = createField("os", Messages.BugzillaRestTaskSchema_OS, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE); //$NON-NLS-1$

	public final Field PLATFORM = createField("platform", Messages.BugzillaRestTaskSchema_Platform, TaskAttribute.TYPE_SINGLE_SELECT, Flag.ATTRIBUTE); //$NON-NLS-1$

	public final Field PRIORITY = inheritFrom(parent.PRIORITY).create();

	public final Field SEVERITY = inheritFrom(parent.SEVERITY).create();

	public final Field STATUS = inheritFrom(parent.STATUS).create();

	public final Field ALIAS = createField("alias", Messages.BugzillaRestTaskSchema_Alias, TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE); //$NON-NLS-1$

	public final Field ASSIGNED_TO = inheritFrom(parent.USER_ASSIGNED).label(Messages.BugzillaRestTaskSchema_Assigned_To)
			.dependsOn(COMPONENT.getKey())
			.create();

	public final Field ADD_CC = createField("addCC", Messages.BugzillaRestTaskSchema_Add_CC, TaskAttribute.TYPE_MULTI_LABEL, Flag.PEOPLE); //$NON-NLS-1$

	public final Field CC = createField(TaskAttribute.USER_CC, Messages.BugzillaRestTaskSchema_Remove_CC,
			IBugzillaRestConstants.EDITOR_TYPE_CC, Flag.PEOPLE);

	public final Field REMOVE_CC = createField("removeCC", Messages.BugzillaRestTaskSchema_CC_Selected, //$NON-NLS-1$
			IBugzillaRestConstants.EDITOR_TYPE_CC);

	public final Field ADD_SELF_CC = inheritFrom(parent.ADD_SELF_CC).create();

	public final Field COMMENT_ISPRIVATE = inheritFrom(parent.COMMENT_ISPRIVATE).addFlags(Flag.ATTRIBUTE).create();

	public final Field COMMENT_NUMBER = inheritFrom(parent.COMMENT_NUMBER).addFlags(Flag.ATTRIBUTE).create();

	public final Field QA_CONTACT = createField("qa_contact", Messages.BugzillaRestTaskSchema_QA_Contact, TaskAttribute.TYPE_PERSON, null, //$NON-NLS-1$
			COMPONENT.getKey(), Flag.PEOPLE);

	public final Field TARGET_MILESTONE = createField("target_milestone", Messages.BugzillaRestTaskSchema_Target_Milestone, //$NON-NLS-1$
			TaskAttribute.TYPE_SINGLE_SELECT, null, PRODUCT.getKey(), Flag.ATTRIBUTE, Flag.REQUIRED);

	public final Field RESOLUTION = inheritFrom(parent.RESOLUTION).removeFlags(Flag.READ_ONLY).create();

	public final Field OPERATION = createField(TaskAttribute.OPERATION, Messages.BugzillaRestTaskSchema_Operation, TaskAttribute.TYPE_OPERATION);

	public final Field NEW_COMMENT = inheritFrom(parent.NEW_COMMENT).create();

	public final Field DUPE_OF = createField("dupe_of", Messages.BugzillaRestTaskSchema_Dup_Of, TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID); //$NON-NLS-1$

	public final Field DEPENDS_ON = createField("depends_on", Messages.BugzillaRestTaskSchema_Depends_On, TaskAttribute.TYPE_TASK_DEPENDENCY, //$NON-NLS-1$
			Flag.ATTRIBUTE);

	public final Field BLOCKS = createField("blocks", Messages.BugzillaRestTaskSchema_Blocks, TaskAttribute.TYPE_TASK_DEPENDENCY, Flag.ATTRIBUTE); //$NON-NLS-1$

	public final Field KEYWORDS = createField("keywords", Messages.BugzillaRestTaskSchema_Keywords, IBugzillaRestConstants.EDITOR_TYPE_KEYWORD, //$NON-NLS-1$
			Flag.ATTRIBUTE);

	public final Field DATE_MODIFICATION = inheritFrom(parent.DATE_MODIFICATION).create();

	public final Field RESET_QA_CONTACT = createField("reset_qa_contact", Messages.BugzillaRestTaskSchema_Reset_Qa_Contact, //$NON-NLS-1$
			TaskAttribute.TYPE_BOOLEAN, Flag.PEOPLE);

	public final Field RESET_ASSIGNED_TO = createField("reset_assigned_to", Messages.BugzillaRestTaskSchema_Reset_Assigned_To, //$NON-NLS-1$
			TaskAttribute.TYPE_BOOLEAN, Flag.PEOPLE);

	@Override
	public void initialize(TaskData taskData) {
		for (Field field : getFields()) {
			if (field.equals(COMMENT_ISPRIVATE) || field.equals(COMMENT_NUMBER)) {
				continue;
			}
			TaskAttribute newField = field.createAttribute(taskData.getRoot());
			if (field.equals(DESCRIPTION)) {
				COMMENT_ISPRIVATE.createAttribute(newField);
				COMMENT_NUMBER.createAttribute(newField);
			}
		}
	}

}