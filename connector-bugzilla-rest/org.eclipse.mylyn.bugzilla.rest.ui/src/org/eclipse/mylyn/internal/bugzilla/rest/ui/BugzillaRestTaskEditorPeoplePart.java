/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskSchema;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPeoplePart;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;

public class BugzillaRestTaskEditorPeoplePart extends TaskEditorPeoplePart {

	@Override
	protected Collection<TaskAttribute> getAttributes() {
		Map<String, TaskAttribute> allAttributes = getTaskData().getRoot().getAttributes();
		List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(allAttributes.size());
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED));
		attributes.add(getTaskData().getRoot()
				.getMappedAttribute(BugzillaRestTaskSchema.getDefault().RESET_ASSIGNED_TO.getKey()));
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_REPORTER));
		addSelfToCC(attributes);
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
	private void addSelfToCC(List<TaskAttribute> attributes) {
		if (getTaskData().isNew()) {
			return;
		}
		TaskRepository repository = this.getTaskEditorPage().getTaskRepository();

		if (repository.getUserName() == null) {
			return;
		}

		TaskAttribute root = getTaskData().getRoot();
		TaskAttribute owner = root.getMappedAttribute(TaskAttribute.USER_ASSIGNED);
		if (owner != null && owner.getValue().indexOf(repository.getUserName()) != -1) {
			return;
		}

		TaskAttribute reporter = root.getMappedAttribute(TaskAttribute.USER_REPORTER);
		if (reporter != null && reporter.getValue().indexOf(repository.getUserName()) != -1) {
			return;
		}

		TaskAttribute ccAttribute = root.getMappedAttribute(TaskAttribute.USER_CC);
		if (ccAttribute != null && ccAttribute.getValues().contains(repository.getUserName())) {
			return;
		}

		TaskAttribute attrAddToCC = getTaskData().getRoot().getMappedAttribute(TaskAttribute.ADD_SELF_CC);
		if (attrAddToCC == null) {
			attrAddToCC = getTaskData().getRoot().createMappedAttribute(TaskAttribute.ADD_SELF_CC);
		}
		attributes.add(attrAddToCC);

	}

	@Override
	protected GridDataFactory createLayoutData(AbstractAttributeEditor editor) {
		LayoutHint layoutHint = editor.getLayoutHint();
		GridDataFactory gridDataFactory = GridDataFactory.fillDefaults().indent(3, 0);// prevent clipping of decorators on Mac
		if (layoutHint != null && layoutHint.rowSpan == RowSpan.MULTIPLE) {
			gridDataFactory.grab(true, true).align(SWT.FILL, SWT.FILL).hint(130, 95);
		} else {
			gridDataFactory.grab(true, false).align(SWT.FILL, SWT.TOP);

		}
		return gridDataFactory;
	}

}
