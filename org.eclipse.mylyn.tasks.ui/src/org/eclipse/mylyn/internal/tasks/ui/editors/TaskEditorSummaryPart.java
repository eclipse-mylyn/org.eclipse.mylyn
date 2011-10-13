/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raphael Ackermann - initial API and implementation, bug 195514
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Raphael Ackermann
 * @author Steffen Pingel
 */
public class TaskEditorSummaryPart extends AbstractTaskEditorPart {

	private AbstractAttributeEditor summaryEditor;

	public TaskEditorSummaryPart() {
		setPartName(Messages.TaskEditorSummaryPart_Summary);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute) {
		addAttribute(composite, toolkit, attribute, EditorUtil.HEADER_COLUMN_MARGIN);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, int indent) {
		addAttribute(composite, toolkit, attribute, indent, true);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, int indent,
			boolean showLabel) {
		addAttribute(composite, toolkit, attribute, indent, true, false);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, int indent,
			boolean showLabel, boolean decorationEnabled) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			// having editable controls in the header looks odd
			editor.setReadOnly(true);
			editor.setDecorationEnabled(decorationEnabled);

			boolean isPriority = isAttribute(attribute, TaskAttribute.PRIORITY);

			if (showLabel && !isPriority) {
				editor.createLabelControl(composite, toolkit);
				GridDataFactory.defaultsFor(editor.getLabelControl())
						.indent(indent, 0)
						.applyTo(editor.getLabelControl());
			}

			if (isPriority) {
				ITaskMapping mapping = getTaskEditorPage().getConnector().getTaskMapping(getTaskData());
				if (mapping != null) {
					PriorityLevel priorityLevel = mapping.getPriorityLevel();
					if (priorityLevel != null) {
						Image image = CommonImages.getImage(TasksUiInternal.getPriorityImage(getTaskEditorPage().getTask()));
						if (image != null) {
							Label label = toolkit.createLabel(composite, null);
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

	protected Control addAttributeWithIcon(Composite composite, FormToolkit toolkit, TaskAttribute attribute,
			boolean forceReadOnly) {
		if (attribute != null) {
			//can't be added via a factory because these attributes already have a default editor?
			AbstractAttributeEditor editor = new PriorityAttributeEditor(getModel(), attribute);
			if (editor != null) {
				editor.setDecorationEnabled(false);
				if (forceReadOnly) {
					editor.setReadOnly(true);
				}
				editor.createControl(composite, toolkit);
				getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
			}
			return editor.getControl();
		} else {
			//some connectors don't have priorities.  in this case we just show no icon.
			//this can't be handled within the attribute editor, as it asserts that the attribute cannot be null
			return null;
		}
	}

	private boolean isAttribute(TaskAttribute attribute, String id) {
		return attribute.getId().equals(
				attribute.getTaskData().getAttributeMapper().mapToRepositoryKey(attribute.getParentAttribute(), id));
	}

	private void addSummaryText(Composite composite, final FormToolkit toolkit) {
		TaskAttribute summaryAttrib = getTaskData().getRoot().getMappedAttribute(TaskAttribute.SUMMARY);
		summaryEditor = createAttributeEditor(summaryAttrib);
		if (summaryEditor != null) {
			if (summaryAttrib.getMetaData().isReadOnly()) {
				summaryEditor.setReadOnly(true);
			}
			if (summaryEditor instanceof RichTextAttributeEditor) {
				// create composite to hold rounded border
				Composite roundedBorder = EditorUtil.createBorder(composite, toolkit, !summaryEditor.isReadOnly());
				summaryEditor.createControl(roundedBorder, toolkit);
				EditorUtil.setHeaderFontSizeAndStyle(summaryEditor.getControl());
			} else {
				final Composite border = toolkit.createComposite(composite);
				GridDataFactory.fillDefaults()
						.align(SWT.FILL, SWT.BEGINNING)
						.hint(EditorUtil.MAXIMUM_WIDTH, SWT.DEFAULT)
						.grab(true, false)
						.applyTo(border);
				// leave some padding for the border of the attribute editor
				border.setLayout(GridLayoutFactory.fillDefaults().margins(1, 4).create());
				summaryEditor.createControl(border, toolkit);
				GridDataFactory.fillDefaults()
						.align(SWT.FILL, SWT.CENTER)
						.grab(true, false)
						.applyTo(summaryEditor.getControl());
				toolkit.paintBordersFor(border);
			}
			getTaskEditorPage().getAttributeEditorToolkit().adapt(summaryEditor);
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = EditorUtil.createSectionClientLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 3;
		composite.setLayout(layout);

		// add priority as an icon 
		TaskAttribute priorityAttribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.PRIORITY);
		final Control priorityEditor = addAttributeWithIcon(composite, toolkit, priorityAttribute, false);
		if (priorityEditor != null) {
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).span(1, 2).applyTo(priorityEditor);
			// forward focus to the summary editor		
			priorityEditor.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					if (summaryEditor != null && summaryEditor.getControl() != null) {
						summaryEditor.getControl().setFocus();
						//only forward it on first view
						priorityEditor.removeFocusListener(this);
					}
				}
			});
			layout.numColumns++;
		}

		addSummaryText(composite, toolkit);

		if (Boolean.parseBoolean(getModel().getTaskRepository().getProperty(
				TaskEditorExtensions.REPOSITORY_PROPERTY_AVATAR_SUPPORT))) {
			TaskAttribute userAssignedAttribute = getTaskData().getRoot().getMappedAttribute(
					TaskAttribute.USER_ASSIGNED);
			if (userAssignedAttribute != null) {
				UserAttributeEditor editor = new UserAttributeEditor(getModel(), userAssignedAttribute);
				editor.createControl(composite, toolkit);
				GridDataFactory.fillDefaults()
						.align(SWT.CENTER, SWT.CENTER)
						.span(1, 2)
						.indent(0, 2)
						.applyTo(editor.getControl());
				layout.marginRight = 1;
				layout.numColumns++;
			}
		}

		if (needsHeader()) {
			createHeaderLayout(composite, toolkit);
		}

		toolkit.paintBordersFor(composite);

		setControl(composite);
	}

	protected Composite createHeaderLayout(Composite composite, FormToolkit toolkit) {
		Composite headerComposite = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		headerComposite.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(headerComposite);

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
		TaskAttribute statusAtribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.STATUS);
		addAttribute(headerComposite, toolkit, statusAtribute, 0, true, true);

		ITaskMapping mapping = getTaskEditorPage().getConnector().getTaskMapping(getTaskData());
		if (mapping != null && mapping.getResolution() != null && mapping.getResolution().length() > 0) {
			TaskAttribute resolutionAtribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
			addAttribute(headerComposite, toolkit, resolutionAtribute, 0, false);
		}

//		TaskAttribute keyAttribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.TASK_KEY);
//		addAttribute(headerComposite, toolkit, keyAttribute);

		// right align controls
//		Composite spacer = toolkit.createComposite(headerComposite, SWT.NONE);
//		GridDataFactory.fillDefaults().hint(0, 10).grab(true, false).applyTo(spacer);

		TaskAttribute dateCreation = getTaskData().getRoot().getMappedAttribute(TaskAttribute.DATE_CREATION);
		addAttribute(headerComposite, toolkit, dateCreation);

		TaskAttribute dateModified = getTaskData().getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
		addAttribute(headerComposite, toolkit, dateModified);

		// ensure layout does not wrap
		layout.numColumns = headerComposite.getChildren().length;

		// ensure that the composite does not show a bunch of blank space
		if (layout.numColumns == 0) {
			layout.numColumns = 1;
			toolkit.createLabel(headerComposite, " "); //$NON-NLS-1$
		}

		return headerComposite;
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
