/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Frank Becker
 */
public class BugzillaFlagPart extends AbstractTaskEditorPart {

	private static final int COLUMN_MARGIN = 5;

	Composite flagComposite = null;

	public BugzillaFlagPart() {
		setPartName(Messages.BugzillaFlagPart_flags);
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		final Section section = createSection(parent, toolkit, false);
		expandSection(toolkit, section);
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent event) {
				if (flagComposite == null) {
					expandSection(toolkit, section);
					getTaskEditorPage().reflow();
				}
			}
		});
		setSection(toolkit, section);
	}

	private void expandSection(FormToolkit toolkit, Section section) {
		flagComposite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 5;
		flagComposite.setLayout(layout);

		Map<String, TaskAttribute> attributes = getTaskData().getRoot().getAttributes();
		for (TaskAttribute attribute : attributes.values()) {
			TaskAttributeMetaData properties = attribute.getMetaData();
			if (!IBugzillaConstants.EDITOR_TYPE_FLAG.equals(properties.getType())) {
				continue;
			}
			addAttribute(flagComposite, toolkit, attribute);
		}
		toolkit.paintBordersFor(flagComposite);
		section.setClient(flagComposite);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			editor.createLabelControl(composite, toolkit);
			GridDataFactory.defaultsFor(editor.getLabelControl()).indent(COLUMN_MARGIN, 0).applyTo(
					editor.getLabelControl());
			editor.createControl(composite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).hint(130, SWT.DEFAULT).applyTo(
					editor.getControl());
		}
	}

}
