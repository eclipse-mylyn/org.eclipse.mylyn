/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.tasks.core.data.AbstractAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.AttributeManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.graphics.Color;
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

	protected static final int MAXIMUM_HEIGHT = 140;

	protected static final int MAXIMUM_WIDTH = 500;

	// XXX why is this required?
	protected static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private Control control;

	private boolean decorationEnabled;

	private Label labelControl;

	private LayoutHint layoutHint;

	private final AttributeManager manager;

	private final TaskAttribute taskAttribute;

	private boolean readOnly;

	public AbstractAttributeEditor(AttributeManager manager, TaskAttribute taskAttribute) {
		Assert.isNotNull(manager);
		Assert.isNotNull(taskAttribute);

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

	public void dispose() {
	}

	public AttributeManager getAttributeEditorManager() {
		return manager;
	}

	protected AbstractAttributeMapper getAttributeMapper() {
		return getTaskAttribute().getTaskData().getAttributeMapper();
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

	public TaskAttribute getTaskAttribute() {
		return taskAttribute;
	}

	public boolean hasLabel() {
		// TODO EDITOR
		return true;
	}

	public boolean isDecorationEnabled() {
		return decorationEnabled;
	}

	protected void setControl(Control control) {
		this.control = control;
	}

	public void setDecorationEnabled(boolean decorationEnabled) {
		this.decorationEnabled = decorationEnabled;
	}

	protected void setLayoutHint(LayoutHint layoutHint) {
		this.layoutHint = layoutHint;
	}

	public void decorate(Color color) {
		if (isDecorationEnabled()) {
			getControl().setBackground(color);
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
