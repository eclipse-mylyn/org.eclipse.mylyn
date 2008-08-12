/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Raphael Ackermann (bug 195514)
 * @author Steffen Pingel
 */
public class TaskEditorSummaryPart extends AbstractTaskEditorPart {

	private static final int COLUMN_MARGIN = 6;

	private Composite headerComposite;

	private AbstractAttributeEditor summaryEditor;

	public TaskEditorSummaryPart() {
		setPartName("Summary");
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute) {
		addAttribute(composite, toolkit, attribute, COLUMN_MARGIN);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, int indent) {
		addAttribute(composite, toolkit, attribute, indent, true);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, int indent,
			boolean showLabel) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			// having editable controls in the header looks odd
			editor.setReadOnly(true);
			editor.setDecorationEnabled(false);

			boolean isPriority = isAttribute(attribute, TaskAttribute.PRIORITY);

			if (showLabel && !isPriority) {
				editor.createLabelControl(composite, toolkit);
				GridDataFactory.defaultsFor(editor.getLabelControl()).indent(indent, 0).applyTo(
						editor.getLabelControl());
			}

			if (isPriority) {
				ITaskMapping mapping = getTaskEditorPage().getConnector().getTaskMapping(getTaskData());
				if (mapping != null) {
					PriorityLevel priorityLevel = mapping.getPriorityLevel();
					if (priorityLevel != null) {
						Image image = CommonImages.getImage(TasksUiInternal.getPriorityImage(getTaskEditorPage().getTask()));
						if (image != null) {
							Label label = toolkit.createLabel(headerComposite, null);
							label.setImage(image);
							GridDataFactory.defaultsFor(label).indent(5, -3).applyTo(label);
						}
					}
				}
			}

			if (isAttribute(attribute, TaskAttribute.DATE_MODIFICATION) && editor instanceof DateAttributeEditor) {
				((DateAttributeEditor) editor).setShowTime(true);
			}

			editor.createControl(composite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
		}
	}

	private boolean isAttribute(TaskAttribute attribute, String id) {
		return attribute.getId().equals(
				attribute.getTaskData().getAttributeMapper().mapToRepositoryKey(attribute.getParentAttribute(), id));
	}

	private void addSummaryText(Composite composite, FormToolkit toolkit) {
		summaryEditor = createAttributeEditor(getTaskData().getRoot().getMappedAttribute(TaskAttribute.SUMMARY));
		if (summaryEditor != null) {
			summaryEditor.createControl(composite, toolkit);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(summaryEditor.getControl());
			getTaskEditorPage().getAttributeEditorToolkit().adapt(summaryEditor);
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		composite.setLayout(layout);

		addSummaryText(composite, toolkit);

		if (needsHeader()) {
			createHeaderLayout(composite, toolkit);
		}

		toolkit.paintBordersFor(composite);

		setControl(composite);
	}

	protected void createHeaderLayout(Composite composite, FormToolkit toolkit) {
		headerComposite = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(11, false);
		layout.verticalSpacing = 1;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		headerComposite.setLayout(layout);

//		ITaskMapping mapping = getTaskEditorPage().getConnector().getTaskMapping(getTaskData());
//		if (mapping != null) {
//			toolkit.createLabel(headerComposite, mapping.getStatus());
//			String resolution = mapping.getResolution();
//			if (resolution != null && resolution.length() > 0) {
//				toolkit.createLabel(headerComposite, "(" + resolution + ")");
//			}
//			String priority = mapping.getPriority();
//			if (priority != null) {
//				Label label = toolkit.createLabel(headerComposite, "Priority:");
//				label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
//				PriorityLevel priorityLevel = mapping.getPriorityLevel();
//				if (priorityLevel != null) {
//					Image image = TasksUiImages.getImageForPriority(priorityLevel);
//					if (image != null) {
//						label = toolkit.createLabel(headerComposite, null);
//						label.setImage(image);
//					}
//				}
//				toolkit.createLabel(headerComposite, priority);
//			}
//		}
		TaskAttribute priorityAttribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.PRIORITY);
		addAttribute(headerComposite, toolkit, priorityAttribute);

		TaskAttribute statusAtribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.STATUS);
		addAttribute(headerComposite, toolkit, statusAtribute, 0);

		ITaskMapping mapping = getTaskEditorPage().getConnector().getTaskMapping(getTaskData());
		if (mapping != null && mapping.getResolution() != null && mapping.getResolution().length() > 0) {
			TaskAttribute resolutionAtribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
			addAttribute(headerComposite, toolkit, resolutionAtribute, 0, false);
		}

		TaskAttribute keyAttribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.TASK_KEY);
		addAttribute(headerComposite, toolkit, keyAttribute);

		TaskAttribute dateCreation = getTaskData().getRoot().getMappedAttribute(TaskAttribute.DATE_CREATION);
		addAttribute(headerComposite, toolkit, dateCreation);

		TaskAttribute dateModified = getTaskData().getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
		addAttribute(headerComposite, toolkit, dateModified);
	}

	public boolean needsHeader() {
		return !getTaskData().isNew();
	}

	@Override
	public void setFocus() {
		if (summaryEditor != null) {
			summaryEditor.getControl().setFocus();
		}
	}

}
