/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskSchema;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPeoplePart;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;

public class BugzillaRestTaskEditorPeoplePart extends TaskEditorPeoplePart {

	@Override
	protected Collection<TaskAttribute> getAttributes() {
		Map<String, TaskAttribute> allAttributes = getTaskData().getRoot().getAttributes();
		List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(allAttributes.size());
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED));
		attributes.add(getTaskData().getRoot()
				.getMappedAttribute(BugzillaRestTaskSchema.getDefault().RESET_ASSIGNED_TO.getKey()));
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_REPORTER));
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.ADD_SELF_CC));
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_CC));
		for (TaskAttribute attribute : allAttributes.values()) {
			TaskAttributeMetaData properties = attribute.getMetaData();
			if (TaskAttribute.KIND_PEOPLE.equals(properties.getKind())) {
				if (!attributes.contains(attribute)) {
					attributes.add(attribute);
				}
			}
		}
		return attributes;
	}

}
