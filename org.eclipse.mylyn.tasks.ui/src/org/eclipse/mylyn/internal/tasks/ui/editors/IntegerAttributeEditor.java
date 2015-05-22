/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorToolkit;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Thomas Ehrnhoefer
 */
public class IntegerAttributeEditor extends TextAttributeEditor {

	public IntegerAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		super.createControl(parent, toolkit);
		AttributeEditorToolkit.createValidator(this, getText(), getAttributeTypeValidator());
	}

	IInputValidator getAttributeTypeValidator() {
		return new IInputValidator() {
			public String isValid(String newText) {
				if (StringUtils.isNotBlank(newText)) {
					try {
						Integer.parseInt(newText);
					} catch (NumberFormatException e) {
						return Messages.IntegerAttributeEditor_this_field_requires_integer_value;
					}
				}
				return null;
			}
		};
	}
}
