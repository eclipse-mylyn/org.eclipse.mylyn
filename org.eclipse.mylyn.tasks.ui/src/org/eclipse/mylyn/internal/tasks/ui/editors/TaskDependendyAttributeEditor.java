/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;

/**
 * @author Steffen Pingel
 */
public class TaskDependendyAttributeEditor extends RichTextAttributeEditor {

	public TaskDependendyAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute,
			TaskRepository taskRepository) {
		super(manager, taskRepository, taskAttribute, SWT.SINGLE);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
		setSpellCheckingEnabled(false);
	}

	@Override
	public String getValue() {
		return getAttributeMapper().getValueLabel(getTaskAttribute());
	}

	@Override
	public void setValue(String value) {
		// TODO EDITOR
	}

}
