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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.junit.Test;

public class DoubleAttributeEditorTest {

	private final TaskData data = new TaskData(mock(TaskAttributeMapper.class), "kind", "url", "id");

	private final DoubleAttributeEditor editor = new DoubleAttributeEditor(mock(TaskDataModel.class), data.getRoot());

	@Test
	public void validateEmptyInput() {
		IInputValidator validator = editor.getAttributeTypeValidator();
		assertNull(validator.isValid(""));
	}

	@Test
	public void validateDoubles() {
		IInputValidator validator = editor.getAttributeTypeValidator();
		assertNull(validator.isValid("1.0"));
		assertNull(validator.isValid("0.123"));
		assertNull(validator.isValid("-2"));
	}

	@Test
	public void validateText() {
		IInputValidator validator = editor.getAttributeTypeValidator();
		assertEquals("This field requires a double value.", validator.isValid("abc"));
	}
}
