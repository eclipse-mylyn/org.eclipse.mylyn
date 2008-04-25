/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class BooleanAttributeEditor extends AbstractAttributeEditor {

	public BooleanAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		// ignore
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		final Button button = toolkit.createButton(parent, super.getLabel(), SWT.CHECK);
		button.setEnabled(!isReadOnly());
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValue(button.getSelection());
			}
		});

		setControl(button);
	}

	@Override
	public String getLabel() {
		return "";
	}

	public boolean getValue() {
		return getAttributeMapper().getBooleanValue(getTaskAttribute());
	}

	public void setValue(boolean value) {
		getAttributeMapper().setBooleanValue(getTaskAttribute(), value);
		attributeChanged();
	}

}
