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
 *     Robert Munteanu - fix for bug #377081
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class TaskEditorPeoplePart extends AbstractTaskEditorPart {

	private static final int COLUMN_MARGIN = 5;

	public TaskEditorPeoplePart() {
		setPartName(Messages.TaskEditorPeoplePart_People);
	}

	protected void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			editor.createLabelControl(composite, toolkit);
			GridDataFactory.defaultsFor(editor.getLabelControl())
			.indent(COLUMN_MARGIN, 0)
			.applyTo(editor.getLabelControl());
			editor.createControl(composite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);

			GridDataFactory dataFactory = createLayoutData(editor);
			dataFactory.applyTo(editor.getControl());
		}
	}

	protected GridDataFactory createLayoutData(AbstractAttributeEditor editor) {
		GridDataFactory gridDataFactory = GridDataFactory.fillDefaults()
				.grab(true, false)
				.align(SWT.FILL, SWT.TOP)
				.indent(3, 0);// prevent clipping of decorators on Mac

		if (editor instanceof MultiSelectionAttributeEditor) {
			gridDataFactory.hint(SWT.DEFAULT, 95);
		}
		return gridDataFactory;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);

		Composite peopleComposite = toolkit.createComposite(section);
		GridLayout layout = EditorUtil.createSectionClientLayout();
		layout.numColumns = 2;
		peopleComposite.setLayout(layout);

		createAttributeEditors(toolkit, peopleComposite);

		toolkit.paintBordersFor(peopleComposite);
		section.setClient(peopleComposite);
		setSection(toolkit, section);
	}

	protected void createAttributeEditors(FormToolkit toolkit, Composite peopleComposite) {
		Collection<TaskAttribute> attributes = getAttributes();
		for (TaskAttribute attribute : attributes) {
			addAttribute(peopleComposite, toolkit, attribute);
		}
	}

	protected Collection<TaskAttribute> getAttributes() {
		Map<String, TaskAttribute> allAttributes = getTaskData().getRoot().getAttributes();
		List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(allAttributes.size());
		attributes.add(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED));
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
