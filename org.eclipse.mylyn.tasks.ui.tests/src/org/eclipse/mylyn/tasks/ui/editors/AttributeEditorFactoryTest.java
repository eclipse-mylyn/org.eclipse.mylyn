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

package org.eclipse.mylyn.tasks.ui.editors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.mylyn.internal.tasks.ui.editors.BooleanAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.DoubleAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.IntegerAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.LongAttributeEditor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AttributeEditorFactoryTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final TaskData data = new TaskData(mock(TaskAttributeMapper.class), "kind", "url", "id");

	private final AttributeEditorFactory factory = new AttributeEditorFactory(mock(TaskDataModel.class),
			new TaskRepository("kind", "url"));

	@Test
	public void createBooleanAttributeEditor() {
		AbstractAttributeEditor editor = factory.createEditor(TaskAttribute.TYPE_BOOLEAN, data.getRoot());
		assertEquals(BooleanAttributeEditor.class, editor.getClass());
	}

	@Test
	public void createDoubleAttributeEditor() {
		AbstractAttributeEditor editor = factory.createEditor(TaskAttribute.TYPE_DOUBLE, data.getRoot());
		assertEquals(DoubleAttributeEditor.class, editor.getClass());
	}

	@Test
	public void createLongAttributeEditor() {
		AbstractAttributeEditor editor = factory.createEditor(TaskAttribute.TYPE_LONG, data.getRoot());
		assertEquals(LongAttributeEditor.class, editor.getClass());
	}

	@Test
	public void createIntegerAttributeEditor() {
		AbstractAttributeEditor editor = factory.createEditor(TaskAttribute.TYPE_INTEGER, data.getRoot());
		assertEquals(IntegerAttributeEditor.class, editor.getClass());
	}

	@Test
	public void createAttributeEditorForUnknownType() {
		thrown.expect(IllegalArgumentException.class);
		factory.createEditor("unknown type", data.getRoot());
	}

}
