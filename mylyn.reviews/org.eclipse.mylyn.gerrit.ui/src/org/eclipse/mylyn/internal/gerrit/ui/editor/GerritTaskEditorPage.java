/*********************************************************************
 * Copyright (c) 2010, 2015 Sony Ericsson/ST Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui.editor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritQueryResultSchema;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
import org.eclipse.mylyn.internal.tasks.ui.editors.PersonAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttributePart;
import org.eclipse.mylyn.reviews.internal.core.TaskBuildStatusMapper;
import org.eclipse.mylyn.reviews.ui.spi.editor.AbstractReviewTaskEditorPage;
import org.eclipse.mylyn.reviews.ui.spi.editor.BuildStatusAttributeEditor;
import org.eclipse.mylyn.reviews.ui.spi.editor.ReviewDetailSection;
import org.eclipse.mylyn.reviews.ui.spi.editor.ReviewSetSection;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
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
 * @author Miles Parker
 */
public class GerritTaskEditorPage extends AbstractReviewTaskEditorPage {

	final class GerritAttributePart extends TaskEditorAttributePart {
		@Override
		protected List<TaskAttribute> getOverlayAttributes() {
			TaskAttribute root = getModel().getTaskData().getRoot();
			List<TaskAttribute> attributes = new ArrayList<>();
			TaskAttribute project = root.getAttribute(GerritQueryResultSchema.getDefault().PROJECT.getKey());
			TaskAttribute branch = root.getAttribute(GerritQueryResultSchema.getDefault().BRANCH.getKey());
			if (project != null) {
				attributes.add(project);
			}
			if (branch != null) {
				attributes.add(branch);
			}
			return attributes;
		}
	}

	public GerritTaskEditorPage(TaskEditor editor) {
		super(editor, GerritTaskEditorPage.class.getName(), Messages.GerritTaskEditorPage_Gerrit_Page,
				GerritConnector.CONNECTOR_KIND);
		setNeedsPrivateSection(true);
		setNeedsSubmit(false);
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
				} else if (type.equals(TaskBuildStatusMapper.BUILD_RESULT_TYPE)) {
					return new BuildStatusAttributeEditor(getModel(), getEditorSite(), taskAttribute);
				} else if (TaskAttribute.TYPE_PERSON.equals(type)) {
					return new PersonAttributeEditor(getModel(), taskAttribute) {
						@Override
						public String getValue() {
							if (isReadOnly()) {
								// "label <id>" format doesn't fit in attributes section so just return label
								return getModel().getTaskData().getAttributeMapper().getValueLabel(getTaskAttribute());
							} else {
								return getTaskAttribute().getValue();
							}
						}
					};
				}
				return super.createEditor(type, taskAttribute);
			}
		};

	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = new LinkedHashSet<>();
		Set<TaskEditorPartDescriptor> superDescriptors = super.createPartDescriptors();
		TaskEditorPartDescriptor commentsDescriptor = null;
		TaskEditorPartDescriptor newCommentsDescriptor = null;
		for (TaskEditorPartDescriptor taskEditorPartDescriptor : superDescriptors) {
			TaskEditorPartDescriptor descriptor = getNewDescriptor(taskEditorPartDescriptor);
			if (descriptor != null) {
				if (ID_PART_COMMENTS.equals(descriptor.getId())) {
					commentsDescriptor = descriptor;
				} else if (ID_PART_NEW_COMMENT.equals(descriptor.getId())) {
					newCommentsDescriptor = descriptor;
				} else {
					descriptors.add(descriptor);
				}
			}

		}
		descriptors.add(new TaskEditorPartDescriptor(ReviewDetailSection.class.getName()) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new GerritReviewDetailSection();
			}
		});
		descriptors.add(new TaskEditorPartDescriptor(ReviewSetSection.class.getName()) {
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

	private TaskEditorPartDescriptor getNewDescriptor(TaskEditorPartDescriptor descriptor) {
		if (PATH_ACTIONS.equals(descriptor.getPath()) || PATH_PEOPLE.equals(descriptor.getPath())) {
			return null;
		} else if (ID_PART_ATTRIBUTES.equals(descriptor.getId())) {
			return new TaskEditorPartDescriptor(ID_PART_ATTRIBUTES) {

				@Override
				public AbstractTaskEditorPart createPart() {
					return new GerritAttributePart();
				}
			}.setPath(PATH_ATTRIBUTES);
		}
		return descriptor;
	}
}
