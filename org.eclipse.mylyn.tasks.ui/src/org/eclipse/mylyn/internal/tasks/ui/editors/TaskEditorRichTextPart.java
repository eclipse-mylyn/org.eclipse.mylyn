/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class TaskEditorRichTextPart extends AbstractTaskEditorPart {

	private RichTextAttributeEditor editor;

	private final TaskAttribute attribute;

	private Composite composite;

//	/**
//	 * A listener for selection of the textbox where a new comment is entered in.
//	 */
//	private class NewCommentListener implements Listener {
//		public void handleEvent(Event event) {
//			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
//					new RepositoryTaskSelection(getTaskData().getId(), getTaskData().getRepositoryUrl(),
//							getTaskData().getRepositoryKind(), getSectionLabel(SECTION_NAME.NEWCOMMENT_SECTION), false,
//							getTaskData().getSummary()))));
//		}
//	}

	public TaskEditorRichTextPart(TaskAttribute attribute) {
		Assert.isNotNull(attribute);
		this.attribute = attribute;
	}

	public void appendText(String text) {
		if (editor == null) {
			return;
		}

		StringBuilder strBuilder = new StringBuilder();
		String oldText = editor.getViewer().getDocument().get();
		if (strBuilder.length() != 0) {
			strBuilder.append("\n");
		}
		strBuilder.append(oldText);
		strBuilder.append(text);
		editor.getViewer().getDocument().set(strBuilder.toString());
		TaskAttribute attribute = getTaskData().getMappedAttribute(TaskAttribute.COMMENT_NEW);
		if (attribute != null) {
			attribute.setValue(strBuilder.toString());
			getTaskEditorPage().getAttributeManager().attributeChanged(attribute);
		}
		editor.getViewer().getTextWidget().setCaretOffset(strBuilder.length());
	}

	protected RichTextAttributeEditor getEditor() {
		return editor;
	}

	protected Composite getComposite() {
		return composite;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);

		composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		editor = new RichTextAttributeEditor(getAttributeManager(), getTaskEditorPage().getTaskRepository(), attribute);

		AbstractRenderingEngine renderingEngine = getTaskEditorPage().getAttributeEditorToolkit().getRenderingEngine(
				attribute);
		if (renderingEngine != null) {
			PreviewAttributeEditor previewEditor = new PreviewAttributeEditor(getAttributeManager(), attribute,
					getTaskEditorPage().getTaskRepository(), renderingEngine, editor);
			previewEditor.createControl(composite, toolkit);
			GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(
					previewEditor.getControl());
		} else {
			editor.createControl(composite, toolkit);
			if (editor.isReadOnly()) {
				GridDataFactory.fillDefaults().hint(AbstractAttributeEditor.MAXIMUM_WIDTH, SWT.DEFAULT).applyTo(
						editor.getControl());
			} else {
				final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				// wrap text at this margin, see comment below
				gd.widthHint = AbstractAttributeEditor.MAXIMUM_WIDTH;
				gd.minimumHeight = AbstractAttributeEditor.MAXIMUM_HEIGHT;
				gd.grabExcessHorizontalSpace = true;
				editor.getControl().setLayoutData(gd);
				editor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				// the goal is to make the text viewer as big as the text so it does not require scrolling when first drawn 
				// on screen: when the descriptionTextViewer calculates its height it wraps the text according to the widthHint 
				// which does not reflect the actual size of the widget causing the widget to be taller 
				// (actual width > gd.widhtHint) or shorter (actual width < gd.widthHint) therefore the widthHint is tweaked 
				// once in the listener  
				composite.addControlListener(new ControlAdapter() {
					private boolean first;

					@Override
					public void controlResized(ControlEvent e) {
						if (!first) {
							first = true;
							int width = composite.getSize().x;
							Point size = editor.getViewer().getTextWidget().computeSize(width, SWT.DEFAULT, true);
							// limit width to parent widget
							gd.widthHint = width;
							// limit height to avoid dynamic resizing of the text widget
							gd.heightHint = Math.min(Math.max(AbstractAttributeEditor.MAXIMUM_HEIGHT, size.y),
									AbstractAttributeEditor.MAXIMUM_HEIGHT * 4);
							composite.layout();
						}
					}
				});
			}
		}

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
	}

	@Override
	public void setFocus() {
		if (editor != null) {
			editor.getControl().setFocus();
		}
	}

}
