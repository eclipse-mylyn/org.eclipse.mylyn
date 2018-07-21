/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorToolkit;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Thomas Ehrnhoefer
 * @author Steffen Pingel
 */
public class LongAttributeEditor extends TextAttributeEditor {

	public LongAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
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
						Long.parseLong(newText);
					} catch (NumberFormatException e) {
						return Messages.LongAttributeEditor_This_field_requires_a_long_value;
					}
				}
				return null;
			}
		};
	}

}
