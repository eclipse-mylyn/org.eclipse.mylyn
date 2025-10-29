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
 *     See git history
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

@SuppressWarnings("nls")
public class LongAttributeEditorTest {

	private final TaskData data = new TaskData(mock(TaskAttributeMapper.class), "kind", "url", "id");

	private final LongAttributeEditor editor = new LongAttributeEditor(mock(TaskDataModel.class), data.getRoot());

	@Test
	public void validateEmptyInput() {
		IInputValidator validator = editor.getAttributeTypeValidator();
		assertNull(validator.isValid(""));
	}

	@Test
	public void validateNumbers() {
		IInputValidator validator = editor.getAttributeTypeValidator();
		assertNull(validator.isValid("1"));
		assertNull(validator.isValid("0"));
		assertNull(validator.isValid("-2"));
		assertNull(validator.isValid(Long.toString(Long.MAX_VALUE)));
		assertNull(validator.isValid(Long.toString(Long.MIN_VALUE)));
	}

	@Test
	public void validateText() {
		IInputValidator validator = editor.getAttributeTypeValidator();
		assertEquals("This field requires a long value.", validator.isValid("abc"));
		assertEquals("This field requires a long value.", validator.isValid("1.0"));
		assertEquals("This field requires a long value.", validator.isValid(Long.toString(Long.MAX_VALUE) + "1"));
		assertEquals("This field requires a long value.", validator.isValid(Long.toString(Long.MIN_VALUE) + "1"));
	}

}
