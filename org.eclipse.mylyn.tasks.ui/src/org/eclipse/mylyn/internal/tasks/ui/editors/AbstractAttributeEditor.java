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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractAttributeEditor {

	// XXX why is this required?
	protected static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private final RepositoryTaskAttribute taskAttribute;

	private LayoutHint layoutHint;

	private final AbstractAttributeEditorManager manager;

	private Control control;

	private Label labelControl;

	public AbstractAttributeEditor(AbstractAttributeEditorManager manager, RepositoryTaskAttribute taskAttribute) {
		if (manager == null) {
			throw new IllegalArgumentException();
		}
		if (taskAttribute == null) {
			throw new IllegalArgumentException();
		}

		this.manager = manager;
		this.taskAttribute = taskAttribute;
	}

	protected void attributeChanged() {
		getAttributeEditorManager().attributeChanged(getTaskAttribute());
	}

	public abstract void createControl(Composite parent, FormToolkit toolkit);

	public void createLabelControl(Composite composite, FormToolkit toolkit) {
		if (manager.hasOutgoingChanges(getTaskAttribute())) {
			labelControl = toolkit.createLabel(composite, "*" + getLabel());
		} else {
			labelControl = toolkit.createLabel(composite, getLabel());
		}
		labelControl.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
	}

	protected void decorate(Control control) {
		manager.decorate(getTaskAttribute(), control);
	}

	public void dispose() {
	}

	public AbstractAttributeEditorManager getAttributeEditorManager() {
		return manager;
	}

	protected AbstractAttributeMapper getAttributeMapper() {
		return getTaskAttribute().getTaskData().getAttributeFactory().getAttributeMapper();
	}

	public Control getControl() {
		return control;
	}

	public String getLabel() {
		return getAttributeMapper().getLabel(getTaskAttribute());
	}

	public Label getLabelControl() {
		return labelControl;
	}

	public LayoutHint getLayoutHint() {
		return layoutHint;
	}

	public RepositoryTaskAttribute getTaskAttribute() {
		return taskAttribute;
	}

	public boolean hasLabel() {
		// TODO EDITOR
		return true;
	}

	protected void setControl(Control control) {
		this.control = control;
	}

	protected void setLayoutHint(LayoutHint layoutHint) {
		this.layoutHint = layoutHint;
	}

}
