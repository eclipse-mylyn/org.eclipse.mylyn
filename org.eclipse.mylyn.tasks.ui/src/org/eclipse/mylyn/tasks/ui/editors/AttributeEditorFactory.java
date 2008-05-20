/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.ui.editors.BooleanAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.DateAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.LongTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.MultiSelectionAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.PersonAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.SingleSelectionAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskDependendyAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TextAttributeEditor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.swt.SWT;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public class AttributeEditorFactory {

	private final TaskDataModel model;

	private final TaskRepository taskRepository;

	public AttributeEditorFactory(TaskDataModel model, TaskRepository taskRepository) {
		this.model = model;
		this.taskRepository = taskRepository;
	}

	public AbstractAttributeEditor createEditor(String type, TaskAttribute taskAttribute) {
		Assert.isNotNull(type);

		if (TaskAttribute.TYPE_BOOLEAN.equals(type)) {
			return new BooleanAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_DATE.equals(type)) {
			return new DateAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_PERSON.equals(type)) {
			return new PersonAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_LONG_RICH_TEXT.equals(type)) {
			return new RichTextAttributeEditor(model, taskRepository, taskAttribute);
		} else if (TaskAttribute.TYPE_LONG_TEXT.equals(type)) {
			return new LongTextAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_MULTI_SELECT.equals(type)) {
			return new MultiSelectionAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_SHORT_RICH_TEXT.equals(type)) {
			return new RichTextAttributeEditor(model, taskRepository, taskAttribute, SWT.SINGLE);
		} else if (TaskAttribute.TYPE_SHORT_TEXT.equals(type)) {
			return new TextAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_SINGLE_SELECT.equals(type)) {
			return new SingleSelectionAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_TASK_DEPENDENCY.equals(type)) {
			return new TaskDependendyAttributeEditor(model, taskAttribute, taskRepository);
		}

		throw new IllegalArgumentException("Unsupported editor type: \"" + type + "\"");
	}

}
