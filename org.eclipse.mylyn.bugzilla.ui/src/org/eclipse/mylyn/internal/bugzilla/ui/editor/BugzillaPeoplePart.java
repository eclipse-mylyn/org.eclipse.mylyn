/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Robert Elves
 */
public class BugzillaPeoplePart extends AbstractTaskEditorPart {
	private static final int COLUMN_MARGIN = 5;

	public BugzillaPeoplePart() {
		setPartName("People");
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			editor.createLabelControl(composite, toolkit);
			GridDataFactory.defaultsFor(editor.getLabelControl()).indent(COLUMN_MARGIN, 0).applyTo(
					editor.getLabelControl());
			editor.createControl(composite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);
		Composite peopleComposite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 5;
		peopleComposite.setLayout(layout);

		addAttribute(peopleComposite, toolkit, getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED));
		addAttribute(peopleComposite, toolkit, getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_REPORTER));
		addSelfToCC(peopleComposite);
		addAttribute(peopleComposite, toolkit, getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_CC));

		toolkit.paintBordersFor(peopleComposite);
		section.setClient(peopleComposite);
		setSection(toolkit, section);
	}

	/**
	 * Creates a check box for adding the repository user to the cc list. Does nothing if the repository does not have a
	 * valid username, the repository user is the assignee, reporter or already on the the cc list.
	 */
	protected void addSelfToCC(Composite composite) {

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

		FormToolkit toolkit = getManagedForm().getToolkit();
		TaskAttribute attrAddToCC = getTaskData().getRoot().getMappedAttribute(TaskAttribute.ADD_SELF_CC);
		if (attrAddToCC == null) {
			attrAddToCC = BugzillaTaskDataHandler.createAttribute(getTaskData(), BugzillaReportElement.ADDSELFCC);
		}
		addAttribute(composite, toolkit, attrAddToCC);
	}
}