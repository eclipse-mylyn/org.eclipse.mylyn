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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.EditorAreaHelper;
import org.eclipse.ui.internal.WorkbenchPage;

/**
 * @author Steffen Pingel
 */
public class TaskEditorRichTextPart extends AbstractTaskEditorPart {

	private RichTextAttributeEditor editor;

	private TaskAttribute attribute;

	private Composite composite;

	private int sectionStyle;

	private ToggleToMaximizePartAction toggleToMaximizePartAction;

	public TaskEditorRichTextPart() {
		setSectionStyle(ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
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
		TaskAttribute attribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
		if (attribute != null) {
			attribute.setValue(strBuilder.toString());
			getTaskEditorPage().getModel().attributeChanged(attribute);
		}
		editor.getViewer().getTextWidget().setCaretOffset(strBuilder.length());
	}

	public int getSectionStyle() {
		return sectionStyle;
	}

	public void setSectionStyle(int sectionStyle) {
		this.sectionStyle = sectionStyle;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		if (attribute == null) {
			return;
		}
		AbstractAttributeEditor attributEditor = createAttributeEditor(attribute);
		if (!(attributEditor instanceof RichTextAttributeEditor)) {
			String clazz;
			if (attributEditor != null) {
				clazz = attributEditor.getClass().getName();
			} else {
				clazz = "<null>";
			}
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"Expected an instance of RichTextAttributeEditor, got \"" + clazz + "\""));
			return;
		}

		Section section = createSection(parent, toolkit, sectionStyle);

		composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		editor = (RichTextAttributeEditor) attributEditor;

		AbstractRenderingEngine renderingEngine = getTaskEditorPage().getAttributeEditorToolkit().getRenderingEngine(
				attribute);
		if (renderingEngine != null) {
			PreviewAttributeEditor previewEditor = new PreviewAttributeEditor(getModel(), attribute,
					getTaskEditorPage().getTaskRepository(), renderingEngine, editor);
			previewEditor.createControl(composite, toolkit);
			GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(
					previewEditor.getControl());
		} else {
			editor.createControl(composite, toolkit);
			if (editor.isReadOnly()) {
				composite.setLayout(new FillWidthLayout(EditorUtil.getLayoutAdvisor(getTaskEditorPage()), 0, 0, 0, 3));
			} else {
				final GridData gd = new GridData();
				// wrap text at this margin, see comment below
				int width = getEditorWidth();
				// the goal is to make the text viewer as big as the text so it does not require scrolling when first drawn 
				// on screen
				Point size = editor.getViewer().getTextWidget().computeSize(width, SWT.DEFAULT, true);
				gd.widthHint = EditorUtil.MAXIMUM_WIDTH;
				gd.minimumWidth = EditorUtil.MAXIMUM_WIDTH;
				gd.horizontalAlignment = SWT.FILL;
				gd.grabExcessHorizontalSpace = true;
				// limit height to be avoid dynamic resizing of the text widget: 
				// MAXIMUM_HEIGHT < height < MAXIMUM_HEIGHT * 3
				//gd.minimumHeight = AbstractAttributeEditor.MAXIMUM_HEIGHT;
				gd.heightHint = Math.min(Math.max(EditorUtil.MAXIMUM_HEIGHT, size.y), EditorUtil.MAXIMUM_HEIGHT * 3);
				if (getExpandVertically()) {
					gd.verticalAlignment = SWT.FILL;
					gd.grabExcessVerticalSpace = true;
				}
				editor.getControl().setLayoutData(gd);
				editor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				// shrink the text control if the editor width is reduced, otherwise the text field will always keep it's original 
				// width and will cause the editor to have a horizonal scroll bar 
//				composite.addControlListener(new ControlAdapter() {
//					@Override
//					public void controlResized(ControlEvent e) {
//						int width = sectionComposite.getSize().x;
//						Point size = descriptionTextViewer.getTextWidget().computeSize(width, SWT.DEFAULT, true);
//						// limit width to parent widget
//						gd.widthHint = width;
//						// limit height to avoid dynamic resizing of the text widget
//						gd.heightHint = Math.min(Math.max(DESCRIPTION_HEIGHT, size.y), DESCRIPTION_HEIGHT * 4);
//						sectionComposite.layout();
//					}
//				});
			}
		}

		getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
	}

	private int getEditorWidth() {
		int widthHint = 0;
		if (getManagedForm() != null && getManagedForm().getForm() != null) {
			widthHint = getManagedForm().getForm().getClientArea().width - 90;
		}
		if (widthHint <= 0 && getTaskEditorPage().getEditor().getEditorSite() != null
				&& getTaskEditorPage().getEditor().getEditorSite().getPage() != null) {
			EditorAreaHelper editorManager = ((WorkbenchPage) getTaskEditorPage().getEditor().getEditorSite().getPage()).getEditorPresentation();
			if (editorManager != null && editorManager.getLayoutPart() != null) {
				widthHint = editorManager.getLayoutPart().getControl().getBounds().width - 90;
			}
		}
		if (widthHint <= 0) {
			widthHint = EditorUtil.MAXIMUM_WIDTH;
		}
		return widthHint;
	}

	public TaskAttribute getAttribute() {
		return attribute;
	}

	protected Composite getComposite() {
		return composite;
	}

	protected RichTextAttributeEditor getEditor() {
		return editor;
	}

	public void setAttribute(TaskAttribute attribute) {
		this.attribute = attribute;
	}

	@Override
	public void setFocus() {
		if (editor != null) {
			editor.getControl().setFocus();
		}
	}

	// TODO 3.1 move to AbstractTaskEditorPart? 
	protected Action getMaximizePartAction() {
		if (toggleToMaximizePartAction == null) {
			toggleToMaximizePartAction = new ToggleToMaximizePartAction();
		}
		return toggleToMaximizePartAction;
	}

	private class ToggleToMaximizePartAction extends Action {

		private static final String COMMAND_ID = "org.eclipse.mylyn.tasks.ui.command.maximizePart";

		private static final String MAXIMIZE = "Maximize";

		private static final int SECTION_HEADER_HEIGHT = 50;

		private int originalHeight = -1;

		public ToggleToMaximizePartAction() {
			super("", SWT.TOGGLE);
			setImageDescriptor(CommonImages.PART_MAXIMIZE);
			setToolTipText(MAXIMIZE);
			setActionDefinitionId(COMMAND_ID);
			setChecked(false);
		}

		@Override
		public void run() {
			toogleToMaximizePart();
		}

		private void toogleToMaximizePart() {
			if (!(getEditor().getControl().getLayoutData() instanceof GridData)) {
				return;
			}

			GridData gd = (GridData) getEditor().getControl().getLayoutData();

			if (originalHeight == -1) {
				originalHeight = gd.heightHint;
			}

			try {
				getTaskEditorPage().setReflow(false);

				int heightHint;
				if (isChecked()) {
					heightHint = getManagedForm().getForm().getClientArea().height - SECTION_HEADER_HEIGHT;
				} else {
					heightHint = originalHeight;
				}

				// ignore when not necessary
				if (gd.heightHint == heightHint) {
					return;
				}
				gd.heightHint = heightHint;
				gd.minimumHeight = heightHint;
			} finally {
				getTaskEditorPage().setReflow(true);
			}

			getTaskEditorPage().reflow();
			EditorUtil.ensureVisible(getEditor().getControl());
		}
	}

}
