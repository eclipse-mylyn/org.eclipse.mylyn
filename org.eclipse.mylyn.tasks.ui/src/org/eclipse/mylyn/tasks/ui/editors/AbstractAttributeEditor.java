/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 * @author Sam Davis
 * @since 3.0
 */
public abstract class AbstractAttributeEditor {

	/**
	 * The key used to associate the editor control with the corresponding task attribute. This enables lookup of the
	 * model element from the widget hierarchy.
	 * 
	 * @since 3.5
	 * @see Control#getData(String)
	 * @see #getControl()
	 * @see #getTaskAttribute()
	 */
	public static final String KEY_TASK_ATTRIBUTE = "org.eclipse.mylyn.tasks.ui.editors.TaskAttribute"; //$NON-NLS-1$

	private Control control;

	private boolean decorationEnabled;

	private Label labelControl;

	private LayoutHint layoutHint;

	private final TaskDataModel manager;

	private final TaskAttribute taskAttribute;

	private boolean readOnly;

	private String description;

	private boolean refreshInProgress;

	private final TaskDataModelListener modelListener = new TaskDataModelListener() {
		@Override
		public void attributeChanged(TaskDataModelEvent event) {
			if (shouldAutoRefresh() && getTaskAttribute().equals(event.getTaskAttribute())) {
				try {
					refreshInProgress = true;
					refresh();
				} catch (UnsupportedOperationException e) {
				} finally {
					refreshInProgress = false;
				}
			}
		}
	};

	private final DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			getModel().removeModelListener(modelListener);
		}
	};

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
		setDescription(taskAttribute.getMetaData().getValue(TaskAttribute.META_DESCRIPTION));
	}

	/**
	 * @since 3.0
	 */
	protected void attributeChanged() {
		if (!refreshInProgress) {
			getModel().attributeChanged(getTaskAttribute());
		}
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
	 * @deprecated Method is never called
	 */
	@Deprecated
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
		return CommonUiUtil.toLabel(label);
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
		if (this.control != null && !this.control.isDisposed()) {
			this.control.removeDisposeListener(disposeListener);
			getModel().removeModelListener(modelListener);
		}
		this.control = control;
		if (control != null) {
			control.setData(KEY_TASK_ATTRIBUTE, taskAttribute);
			control.addDisposeListener(disposeListener);
			getModel().addModelListener(modelListener);
		}
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

	/**
	 * Subclasses that implement refresh should override this method to return true, so that they will be automatically
	 * refreshed when the model changes.
	 * 
	 * @return whether the editor should be automatically refreshed when the model changes
	 * @since 3.6
	 */
	protected boolean shouldAutoRefresh() {
		return false;
	}

	/**
	 * @since 3.5
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @since 3.5
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @since 3.6
	 */
	protected void updateLabel() {
		Label labelControl = getLabelControl();
		if (labelControl != null && !labelControl.isDisposed()) {
			labelControl.setText(getLabel());
		}
	}

}
