/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class PriorityAttributeEditor extends AbstractAttributeEditor {

	private PriorityEditor editor;

	private ITaskMapping mapping;

	private final TaskDataModelListener modelListener = new TaskDataModelListener() {
		@Override
		public void attributeChanged(TaskDataModelEvent event) {
			if (getTaskAttribute().equals(event.getTaskAttribute())) {
				refresh();
			}
		}
	};

	public PriorityAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		boolean noOptions = getAttributeMapper().getOptions(getTaskAttribute()).size() == 0;
		setReadOnly(isReadOnly() || noOptions);
	}

	@Override
	public void createControl(final Composite parent, FormToolkit toolkit) {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(getModel().getTaskRepository()
				.getConnectorKind());
		mapping = connector.getTaskMapping(getModel().getTaskData());
		editor = new PriorityEditor() {
			@Override
			protected void valueChanged(String value) {
				setValue(value);
			};
		};
		editor.setReadOnly(isReadOnly());
		editor.createControl(parent, toolkit);
		setControl(editor.getControl());
		getControl().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				getModel().removeModelListener(modelListener);
			}

		});
		getModel().addModelListener(modelListener);
		refresh();
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public String getValueLabel() {
		return getAttributeMapper().getValueLabel(getTaskAttribute());
	}

	@Override
	public void refresh() {
		editor.setLabelByValue(getAttributeMapper().getOptions(getTaskAttribute()));
		updateEditor();
	}

	public void setValue(String value) {
		String oldValue = getAttributeMapper().getValue(getTaskAttribute());
		if (!oldValue.equals(value)) {
			getAttributeMapper().setValue(getTaskAttribute(), value);
			attributeChanged();
			updateEditor();
		}
	}

	private void updateEditor() {
		editor.select(getValue(), mapping.getPriorityLevel());
		editor.setToolTipText(getValueLabel());
	}

}
