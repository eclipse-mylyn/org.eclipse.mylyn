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

	/**
	 * @since 3.0
	 */
	public AbstractAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		Assert.isNotNull(manager);
		Assert.isNotNull(taskAttribute);
		this.manager = manager;
		this.taskAttribute = taskAttribute;
		setDecorationEnabled(true);
		setReadOnly(taskAttribute.getMetaData().isReadOnly());
	}

	/**
	 * @since 3.0
	 */
	protected void attributeChanged() {
		getModel().attributeChanged(getTaskAttribute());
	}

	/**
	 * @since 3.0
	 */
	public abstract void createControl(Composite parent, FormToolkit toolkit);

	/**
	 * @since 3.0
	 */
	public void createLabelControl(Composite composite, FormToolkit toolkit) {
		labelControl = toolkit.createLabel(composite, getLabel());
		labelControl.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
	}

	/**
	 * @since 3.0
	 */
	public void dispose() {
	}

	/**
	 * @since 3.0
	 */
	public TaskDataModel getModel() {
		return manager;
	}

	/**
	 * @since 3.0
	 */
	protected TaskAttributeMapper getAttributeMapper() {
		return getModel().getTaskData().getAttributeMapper();
	}

	/**
	 * @since 3.0
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * @since 3.0
	 */
	public String getLabel() {
		String label = getAttributeMapper().getLabel(getTaskAttribute());
		return TasksUiInternal.escapeLabelText(label);
	}

	/**
	 * @since 3.0
	 */
	public Label getLabelControl() {
		return labelControl;
	}

	/**
	 * @since 3.0
	 */
	public LayoutHint getLayoutHint() {
		return layoutHint;
	}

	/**
	 * @since 3.0
	 */
	public TaskAttribute getTaskAttribute() {
		return taskAttribute;
	}

	/**
	 * @since 3.0
	 */
	public boolean hasLabel() {
		// TODO EDITOR
		return true;
	}

	/**
	 * @since 3.0
	 */
	public boolean isDecorationEnabled() {
		return decorationEnabled;
	}

	/**
	 * @since 3.0
	 */
	protected void setControl(Control control) {
		this.control = control;
	}

	/**
	 * @since 3.0
	 */
	public void setDecorationEnabled(boolean decorationEnabled) {
		this.decorationEnabled = decorationEnabled;
	}

	/**
	 * @since 3.1
	 */
	public void setLayoutHint(LayoutHint layoutHint) {
		this.layoutHint = layoutHint;
	}

	/**
	 * @since 3.0
	 */
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

	/**
	 * @since 3.0
	 */
	protected void decorateOutgoing(Color color) {
		if (labelControl != null) {
			labelControl.setText("*" + labelControl.getText()); //$NON-NLS-1$
		}
	}

	/**
	 * @since 3.0
	 */
	protected void decorateIncoming(Color color) {
		if (getControl() != null) {
			getControl().setBackground(color);
		}
	}

	/**
	 * @since 3.0
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @since 3.0
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Refreshes the state of the widget from the data model. The default implementation throws
	 * <code>UnsupportedOperationException</code>.
	 * 
	 * <p>
	 * Subclasses should overwrite this method.
	 * 
	 * @since 3.1
	 * @throws UnsupportedOperationException
	 *             if this method is not supported by the editor
	 */
	public void refresh() {
		throw new UnsupportedOperationException();
	}

}
