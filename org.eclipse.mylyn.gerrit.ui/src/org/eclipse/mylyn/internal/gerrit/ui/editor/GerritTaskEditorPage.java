/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui.editor;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
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

/**
 * @author Mikael Kober
 * @author Thomas Westling
 */
public class GerritTaskEditorPage extends AbstractTaskEditorPage {

	public GerritTaskEditorPage(TaskEditor editor) {
		super(editor, GerritConnector.CONNECTOR_KIND);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(false);
	}

	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		return new AttributeEditorFactory(getModel(), getTaskRepository(), getEditorSite()) {
			@Override
			public AbstractAttributeEditor createEditor(String type, TaskAttribute taskAttribute) {
				if (taskAttribute.getId().equals(GerritTaskSchema.getDefault().CHANGE_ID.getKey())) {
					AbstractAttributeEditor editor = super.createEditor(type, taskAttribute);
					editor.setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
					return editor;
				} else if (taskAttribute.getId().equals(GerritTaskSchema.getDefault().PROJECT.getKey())) {
					AbstractAttributeEditor editor = super.createEditor(type, taskAttribute);
					editor.setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
					return editor;
				}
				return super.createEditor(type, taskAttribute);
			}
		};
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();
		TaskEditorPartDescriptor commentsDescriptor = null;
		TaskEditorPartDescriptor newCommentsDescriptor = null;
		for (Iterator<TaskEditorPartDescriptor> it = descriptors.iterator(); it.hasNext();) {
			TaskEditorPartDescriptor descriptor = it.next();
			if (PATH_ACTIONS.equals(descriptor.getPath())) {
				it.remove();
			}
			if (PATH_PEOPLE.equals(descriptor.getPath())) {
				it.remove();
			}
			if (ID_PART_COMMENTS.equals(descriptor.getId())) {
				it.remove();
				commentsDescriptor = descriptor;
			}
			if (ID_PART_NEW_COMMENT.equals(descriptor.getId())) {
				it.remove();
				newCommentsDescriptor = descriptor;
			}
		}
		descriptors.add(new TaskEditorPartDescriptor(ReviewSection.class.getName()) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new ReviewSection();
			}
		});
		descriptors.add(new TaskEditorPartDescriptor(PatchSetSection.class.getName()) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new PatchSetSection();
			}
		});
		if (commentsDescriptor != null) {
			descriptors.add(commentsDescriptor);
		}
		if (newCommentsDescriptor != null) {
			descriptors.add(newCommentsDescriptor);
		}
		return descriptors;
	}

}
