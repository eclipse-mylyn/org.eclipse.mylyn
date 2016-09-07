/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import java.util.Set;

import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCore;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskSchema;
import org.eclipse.mylyn.internal.bugzilla.rest.core.IBugzillaRestConstants;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

public class BugzillaRestTaskEditorPage extends AbstractTaskEditorPage {

	public BugzillaRestTaskEditorPage(TaskEditor editor) {
		this(editor, BugzillaRestCore.CONNECTOR_KIND);
	}

	public BugzillaRestTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, connectorKind);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
	}

	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		AttributeEditorFactory factory = new AttributeEditorFactory(getModel(), getTaskRepository(), getEditorSite()) {

			@Override
			public AbstractAttributeEditor createEditor(String type, TaskAttribute taskAttribute) {
				AbstractAttributeEditor editor;
				if (IBugzillaRestConstants.EDITOR_TYPE_CC.equals(type)) {
					editor = new BugzillaCcAttributeEditor(getModel(), taskAttribute);
				} else if (IBugzillaRestConstants.EDITOR_TYPE_KEYWORD.equals(type)) {
					editor = new BugzillaKeywordAttributeEditor(getModel(), taskAttribute);
				} else {
					editor = super.createEditor(type, taskAttribute);
				}
				if (editor != null
						&& BugzillaRestTaskSchema.getDefault().ADD_CC.getKey().equals(taskAttribute.getId())) {
					editor.setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));
				}
				return editor;
			}
		};
		return factory;
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();
		// remove unnecessary default editor parts
		for (TaskEditorPartDescriptor taskEditorPartDescriptor : descriptors) {
			if (taskEditorPartDescriptor.getId().equals(ID_PART_PEOPLE)) {
				descriptors.remove(taskEditorPartDescriptor);
				break;
			}
		}
		// Add the updated Bugzilla people part
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_PEOPLE) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new BugzillaRestTaskEditorPeoplePart();
			}
		}.setPath(PATH_PEOPLE));

		return descriptors;
	}

}
