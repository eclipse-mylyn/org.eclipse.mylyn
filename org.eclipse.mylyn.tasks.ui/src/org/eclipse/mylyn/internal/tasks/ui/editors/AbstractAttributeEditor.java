/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.tasks.core.AbstractAttributeMapper;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractAttributeEditor {

	// XXX why is this required?
	protected static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private final RepositoryTaskAttribute taskAttribute;

	private LayoutHint layoutHint;

	private final AbstractRepositoryTaskEditor editor;

	private Control control;

	public AbstractAttributeEditor(AbstractRepositoryTaskEditor editor, RepositoryTaskAttribute taskAttribute) {
		if (editor == null) {
			throw new IllegalArgumentException();
		}
		if (taskAttribute == null) {
			throw new IllegalArgumentException();
		}

		this.editor = editor;
		this.taskAttribute = taskAttribute;
	}

	protected void attributeChanged() {
		//editor.attributeChanged(getTaskAttribute());
	}

	public abstract void createControl(Composite parent, FormToolkit toolkit);

	protected void decorate(Control control) {
		if (hasChanged()) {
			control.setBackground(editor.getColorIncoming());
		}
	}

	public void dispose() {
	}

	protected AbstractAttributeMapper getAttributeMapper() {
		return getTaskAttribute().getTaskData().getAttributeFactory().getAttributeMapper();
	}

	public Control getControl() {
		return control;
	}

	public String getLabel() {
		return this.taskAttribute.getName();
	}

	public LayoutHint getLayoutHint() {
		return layoutHint;
	}

	public RepositoryTaskAttribute getTaskAttribute() {
		return taskAttribute;
	}

	private boolean hasChanged() {
		RepositoryTaskData oldTaskData = ((RepositoryTaskEditorInput) editor.getEditorInput()).getOldTaskData();
		if (oldTaskData == null) {
			return false;
		}

		if (hasOutgoingChange()) {
			return false;
		}

		RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(taskAttribute.getId());
		if (oldAttribute == null) {
			return true;
		}
		if (oldAttribute.getValue() != null && !oldAttribute.getValue().equals(taskAttribute.getValue())) {
			return true;
		} else if (oldAttribute.getValues() != null && !oldAttribute.getValues().equals(taskAttribute.getValues())) {
			return true;
		}
		return false;
	}

	private boolean hasOutgoingChange() {
		return ((RepositoryTaskEditorInput) editor.getEditorInput()).getOldEdits().contains(taskAttribute);
	}

	protected void setControl(Control control) {
		this.control = control;
	}

	public void setLayoutHint(LayoutHint layoutHint) {
		this.layoutHint = layoutHint;
	}

}
