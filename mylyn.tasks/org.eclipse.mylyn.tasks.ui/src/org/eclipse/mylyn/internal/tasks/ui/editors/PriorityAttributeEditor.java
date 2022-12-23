/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.text.MessageFormat;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class PriorityAttributeEditor extends AbstractAttributeEditor {

	private PriorityEditor editor;

	private ITaskMapping mapping;

	public PriorityAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		boolean noOptions = getAttributeMapper().getOptions(getTaskAttribute()).size() == 0;
		setReadOnly(isReadOnly() || noOptions);
	}

	@Override
	public void createControl(final Composite parent, FormToolkit toolkit) {
		AbstractRepositoryConnector connector = TasksUi
				.getRepositoryConnector(getModel().getTaskRepository().getConnectorKind());
		mapping = connector.getTaskMapping(getModel().getTaskData());
		editor = new PriorityEditor(getTaskAttribute()) {
			@Override
			protected void valueChanged(String value) {
				setValue(value);
			};
		};
		editor.setReadOnly(isReadOnly());
		editor.createControl(parent, toolkit);
		setControl(editor.getControl());
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
		if (editor.getControl() != null && !editor.getControl().isDisposed()) {
			editor.setLabelByValue(getAttributeMapper().getOptions(getTaskAttribute()));
			updateEditor();
		}
	}

	@Override
	public boolean shouldAutoRefresh() {
		return true;
	}

	public void setValue(String value) {
		String oldValue = getAttributeMapper().getValue(getTaskAttribute());
		if (!oldValue.equals(value)) {
			getAttributeMapper().setValue(getTaskAttribute(), value);
			attributeChanged();
		}
	}

	private void updateEditor() {
		editor.select(getValue(), mapping.getPriorityLevel());
		editor.setToolTipText(MessageFormat.format(Messages.PriorityAttributeEditor_Priority_Tooltip, getValueLabel()));
	}

}
