/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.trac.core.TracAttribute;
import org.eclipse.mylyn.internal.trac.core.TracAttributeMapper;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Robert Elves
 */
public class TracPeoplePart extends AbstractTaskEditorPart {

	private static final int COLUMN_MARGIN = 5;

	public TracPeoplePart() {
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
			if (editor instanceof TracCcAttributeEditor) {
				GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).hint(130, 95).applyTo(
						editor.getControl());
			} else {
				GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(editor.getControl());
			}
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
		addAttribute(peopleComposite, toolkit, getTaskData().getRoot().getMappedAttribute(TracAttributeMapper.NEW_CC));
		addSelfToCC(peopleComposite);
		TaskAttribute cc = getTaskData().getRoot().getMappedAttribute(TracAttribute.CC.getTaskKey());
		TaskAttribute removeCc = getTaskData().getRoot().getMappedAttribute(TracAttributeMapper.REMOVE_CC);
		if (cc != null && removeCc != null) {
			addAttribute(peopleComposite, toolkit, cc);
			toolkit.createLabel(peopleComposite, "");
			Label label = toolkit.createLabel(peopleComposite, "(Select to remove)");
			GridDataFactory.fillDefaults().indent(0, 5).align(SWT.CENTER, SWT.CENTER).applyTo(label);
		}

		toolkit.paintBordersFor(peopleComposite);
		section.setClient(peopleComposite);
		setSection(toolkit, section);
	}

	/**
	 * Creates a check box for adding the repository user to the cc list. Does nothing if the repository does not have a
	 * valid username, the repository user is the assignee, reporter or already on the the cc list.
	 */
	private void addSelfToCC(Composite composite) {
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
	}

}