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

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractAttributeEditor {

	private Control control;

	private boolean decorationEnabled;

	private Label labelControl;

	private LayoutHint layoutHint;

	private final TaskDataModel manager;

	private final TaskAttribute taskAttribute;

	private boolean readOnly;

	public AbstractAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		Assert.isNotNull(manager);
		Assert.isNotNull(taskAttribute);
		this.manager = manager;
		this.taskAttribute = taskAttribute;
		setDecorationEnabled(true);
		setReadOnly(taskAttribute.getMetaData().isReadOnly());
	}

	protected void attributeChanged() {
		getModel().attributeChanged(getTaskAttribute());
	}

	public abstract void createControl(Composite parent, FormToolkit toolkit);

	public void createLabelControl(Composite composite, FormToolkit toolkit) {
		labelControl = toolkit.createLabel(composite, getLabel());
		labelControl.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
	}

	public void dispose() {
	}

	public TaskDataModel getModel() {
		return manager;
	}

	protected TaskAttributeMapper getAttributeMapper() {
		return getModel().getTaskData().getAttributeMapper();
	}

	public Control getControl() {
		return control;
	}

	public String getLabel() {
		String label = getAttributeMapper().getLabel(getTaskAttribute());
		return TasksUiInternal.escapeLabelText(label);
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
			if (manager.hasBeenRead() && manager.hasIncomingChanges(getTaskAttribute())) {
				decorateIncoming(color);
			}
			if (manager.hasOutgoingChanges(getTaskAttribute())) {
				decorateOutgoing(color);
			}
		}
	}

	protected void decorateOutgoing(Color color) {
		if (labelControl != null) {
			labelControl.setText("*" + labelControl.getText());
		}
	}

	protected void decorateIncoming(Color color) {
		if (getControl() != null) {
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
