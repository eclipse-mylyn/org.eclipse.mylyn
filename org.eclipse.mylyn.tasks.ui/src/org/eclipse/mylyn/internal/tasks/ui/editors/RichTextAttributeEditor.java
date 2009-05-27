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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * A text attribute editor that can switch between a editor, preview and source view.
 * 
 * @author Steffen Pingel
 * @see RichTextEditor
 */
public class RichTextAttributeEditor extends AbstractAttributeEditor {

	private final RichTextEditor editor;

	protected boolean ignoreNotification;

	public RichTextAttributeEditor(TaskDataModel manager, TaskRepository taskRepository, TaskAttribute taskAttribute) {
		this(manager, taskRepository, taskAttribute, SWT.MULTI);
	}

	public RichTextAttributeEditor(TaskDataModel manager, TaskRepository taskRepository, TaskAttribute taskAttribute,
			int style) {
		this(manager, taskRepository, taskAttribute, style, null, null);
	}

	public RichTextAttributeEditor(TaskDataModel manager, TaskRepository taskRepository, TaskAttribute taskAttribute,
			int style, IContextService contextService, AbstractTaskEditorExtension extension) {
		super(manager, taskAttribute);
		this.editor = new RichTextEditor(taskRepository, style, contextService, extension) {
			@Override
			public void valueChanged(String value) {
				if (!ignoreNotification) {
					RichTextAttributeEditor.this.setValue(value);
				}
			};
		};
		this.editor.setReadOnly(isReadOnly());
		if ((style & SWT.MULTI) != 0) {
			setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.MULTIPLE));
		} else {
			setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
		}
		setMode(Mode.DEFAULT);
		refresh();
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		editor.createControl(parent, toolkit);
		setControl(editor.getControl());
	}

	public SourceViewer getEditorViewer() {
		return editor.getEditorViewer();
	}

	public Mode getMode() {
		return editor.getMode();
	}

	public AbstractRenderingEngine getRenderingEngine() {
		return editor.getRenderingEngine();
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public SourceViewer getViewer() {
		return editor.getViewer();
	}

	public IAction getViewSourceAction() {
		return editor.getViewSourceAction();
	}

	public boolean hasBrowser() {
		return editor.hasBrowser();
	}

	public boolean hasPreview() {
		return editor.hasPreview();
	}

	public boolean isSpellCheckingEnabled() {
		return editor.isSpellCheckingEnabled();
	}

	public void setMode(Mode mode) {
		editor.setMode(mode);
	}

	public void setRenderingEngine(AbstractRenderingEngine renderingEngine) {
		editor.setRenderingEngine(renderingEngine);
	}

	public void setSpellCheckingEnabled(boolean spellCheckingEnabled) {
		editor.setSpellCheckingEnabled(spellCheckingEnabled);
	}

	public void setValue(String value) {
		getAttributeMapper().setValue(getTaskAttribute(), value);
		attributeChanged();
	}

	public void showBrowser() {
		editor.showBrowser();
	}

	public void showDefault() {
		editor.showDefault();
	}

	public void showEditor() {
		editor.showEditor();
	}

	public void showPreview() {
		editor.showPreview();
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		if (editor != null) {
			editor.setReadOnly(readOnly);
		}
	}

	@Override
	public void refresh() {
		try {
			ignoreNotification = true;
			editor.setText(getValue());
		} finally {
			ignoreNotification = false;
		}
	}

}
