/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Raphael Ackermann
 * @author Steffen Pingel
 */
public class TaskEditorSummaryPart extends AbstractTaskEditorPart {

	private static final int COLUMN_MARGIN = 6;

	private Composite headerComposite;

	private AbstractAttributeEditor summaryEditor;

	private Font textFont;

	public TaskEditorSummaryPart() {
		setPartName(Messages.TaskEditorSummaryPart_Summary);
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

	protected Control addAttributeWithIcon(Composite composite, FormToolkit toolkit, TaskAttribute attribute,
			boolean forceReadOnly) {
		if (attribute != null) {
			//can't be added via a factory because these attributes already have a default editor?
			AbstractAttributeEditor editor = new SingleSelectionAttributeEditorWithIcon(getModel(), attribute);
			if (editor != null) {
				editor.setDecorationEnabled(true);
				if (forceReadOnly) {
					editor.setReadOnly(true);
				}
				editor.createControl(composite, toolkit);
				getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
			}
			return editor.getControl();
		} else {
			//some connectors don't have priorities.  in this case we just show the defalut icon.
			//this can't be handled within the attribute editor, as it asserts that the attribute cannot be null
			Label imageOnly = toolkit.createLabel(composite, ""); //$NON-NLS-1$
			imageOnly.setImage(CommonImages.getImage(CommonImages.PRIORITY_3_24));
			return imageOnly;
		}
	}

	private boolean isAttribute(TaskAttribute attribute, String id) {
		return attribute.getId().equals(
				attribute.getTaskData().getAttributeMapper().mapToRepositoryKey(attribute.getParentAttribute(), id));
	}

	private void addSummaryText(Composite composite, final FormToolkit toolkit) {
		summaryEditor = createAttributeEditor(getTaskData().getRoot().getMappedAttribute(TaskAttribute.SUMMARY));
		if (summaryEditor != null) {
			// create composite to hold rounded border
			final Composite roundedBorder = toolkit.createComposite(composite);
			roundedBorder.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.setForeground(toolkit.getColors().getBorderColor());
					Point size = roundedBorder.getSize();
					e.gc.drawRoundRectangle(0, 3, size.x - 1, size.y - 7, 5, 5);
				}
			});
			roundedBorder.setLayout(GridLayoutFactory.fillDefaults().margins(4, 6).create());
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(EditorUtil.MAXIMUM_WIDTH, 30).grab(true,
					false).applyTo(roundedBorder);

			summaryEditor.createControl(roundedBorder, toolkit);
			final Control summaryControl = summaryEditor.getControl();
			GridDataFactory.fillDefaults()
					.align(SWT.FILL, SWT.CENTER)
					.hint(EditorUtil.MAXIMUM_WIDTH, SWT.DEFAULT)
					.grab(true, false)
					.applyTo(summaryControl);
			// change text color, style, size.
			final StyledText text = ((RichTextAttributeEditor) summaryEditor).getViewer().getTextWidget();
			setFontSizeAndStyle(composite, text, 1.2f);
			Color color = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(
					CommonThemes.COLOR_COMPLETED);
			text.setForeground(color);

			//create rounded border on the composite
			getTaskEditorPage().getAttributeEditorToolkit().adapt(summaryEditor);
		}
	}

	private void setFontSizeAndStyle(Composite composite, StyledText text, float size) {
		Font initialFont = text.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (FontData element : fontData) {
			element.setHeight((int) (element.getHeight() * size));
			element.setStyle(element.getStyle() | SWT.BOLD);
		}
		textFont = new Font(composite.getDisplay(), fontData);
		text.setFont(textFont);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (textFont != null) {
			textFont.dispose();
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 5;
		composite.setLayout(layout);

		// add priority as an icon 
		TaskAttribute priorityAttribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.PRIORITY);
		final Control priorityEditor = addAttributeWithIcon(composite, toolkit, priorityAttribute, false);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(priorityEditor);
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

		addSummaryText(composite, toolkit);

		if (needsHeader()) {
			createHeaderLayout(composite, toolkit);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(headerComposite);
		}

		toolkit.paintBordersFor(composite);

		setControl(composite);
	}

	protected Composite createHeaderLayout(Composite composite, FormToolkit toolkit) {
		headerComposite = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
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
		TaskAttribute statusAtribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.STATUS);
		addAttribute(headerComposite, toolkit, statusAtribute, 0);

		ITaskMapping mapping = getTaskEditorPage().getConnector().getTaskMapping(getTaskData());
		if (mapping != null && mapping.getResolution() != null && mapping.getResolution().length() > 0) {
			TaskAttribute resolutionAtribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
			addAttribute(headerComposite, toolkit, resolutionAtribute, 0, false);
		}

		TaskAttribute keyAttribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.TASK_KEY);
		addAttribute(headerComposite, toolkit, keyAttribute);

		// right align controls
		Composite spacer = toolkit.createComposite(headerComposite, SWT.NONE);
		GridDataFactory.fillDefaults().hint(0, 10).grab(true, false).applyTo(spacer);

		TaskAttribute dateCreation = getTaskData().getRoot().getMappedAttribute(TaskAttribute.DATE_CREATION);
		addAttribute(headerComposite, toolkit, dateCreation);

		TaskAttribute dateModified = getTaskData().getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
		addAttribute(headerComposite, toolkit, dateModified);

		// ensure layout does not wrap
		layout.numColumns = headerComposite.getChildren().length;
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
