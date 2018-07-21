/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorPeoplePart;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;

/**
 * @author Rob Elves
 */
public class BugzillaPeoplePart extends TaskEditorPeoplePart {

	private static final int COLUMN_MARGIN = 5;

	public BugzillaPeoplePart() {
	}

	@Override
	protected Collection<TaskAttribute> getAttributes() {
		Map<String, TaskAttribute> allAttributes = getTaskData().getRoot().getAttributes();
		List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(allAttributes.size());
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED));
		TaskAttribute assignee = getTaskData().getRoot().getAttribute(BugzillaAttribute.SET_DEFAULT_ASSIGNEE.getKey());
		if (assignee != null) {
			attributes.add(assignee);
		}
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_REPORTER));
		String useQaContact = getTaskData().getAttributeMapper()
				.getTaskRepository()
				.getProperty(IBugzillaConstants.BUGZILLA_PARAM_USEQACONTACT);
		if (useQaContact == null || Boolean.parseBoolean(useQaContact)) {
			attributes.add(getTaskData().getRoot().getMappedAttribute(BugzillaAttribute.QA_CONTACT.getKey()));
		}
		attributes.add(getTaskData().getRoot().getMappedAttribute(BugzillaAttribute.NEWCC.getKey()));
		if (!getTaskData().isNew()) {
			addSelfToCC(attributes);
		}
		attributes.add(getTaskData().getRoot().getMappedAttribute(BugzillaAttribute.CC.getKey()));

		for (TaskAttribute attribute : allAttributes.values()) {
			if (TaskAttribute.TYPE_PERSON.equals(attribute.getMetaData().getType())) {
				if (!attribute.getId().endsWith("_name") //$NON-NLS-1$
						&& !attribute.getId().equals(BugzillaAttribute.EXPORTER_NAME.getKey())) {
					if (!attributes.contains(attribute)) {
						attributes.add(attribute);
					}
				}
			}
		}
		return attributes;
	}

	/**
	 * Adds ADD_SELF_CC attribute. Does nothing if the repository does not have a valid username, the repository user is
	 * the assignee, reporter or already on the the cc list.
	 */
	protected void addSelfToCC(Collection<TaskAttribute> attributes) {

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
			attrAddToCC = BugzillaTaskDataHandler.createAttribute(getTaskData(), BugzillaAttribute.ADDSELFCC);
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
