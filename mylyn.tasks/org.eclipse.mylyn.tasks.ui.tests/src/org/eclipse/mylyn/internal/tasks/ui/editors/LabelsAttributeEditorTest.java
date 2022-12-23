/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class LabelsAttributeEditorTest {

	private TaskData taskData;

	private TaskDataModel model;

	private LabelsAttributeEditor editor;

	private TaskAttribute singleSelectAttribute;

	private TaskAttribute multiSelectAttribute;

	@Before
	public void setUp() {
		TaskRepository repository = TaskTestUtil.createMockRepository();
		taskData = new TaskData(new TaskAttributeMapper(repository), "kind", "url", "id");
		MockTask task = new MockTask("taskId");
		TaskDataState state = new TaskDataState("kind", "url", "taskId");
		state.setEditsData(taskData);
		state.setLocalTaskData(taskData);
		model = new TaskDataModel(repository, task, state);
	}

	@Test
	public void testSingleSelectEmpty() {
		createSingleSelect("");
		assertSingleSelectValue("");
		editor.setValue("test");
		assertSingleSelectValue("test");
		editor.setValue("");
		assertSingleSelectValue("");
	}

	@Test
	public void testSingleSelect() {
		createSingleSelect("test option");
		assertSingleSelectValue("test option");
		editor.setValue("testing option");
		assertSingleSelectValue("testing option");
	}

	@Test
	public void testSingleSelectWithSeparator() {
		createSingleSelect("one,two ,      three, four");
		assertSingleSelectValue("one,two ,      three, four");
		editor.setValue("one,two ,      three, four ,five     ,");
		assertSingleSelectValue("one,two ,      three, four ,five     ,");
	}

	@Test
	public void testMultiSelectEmpty() {
		createMultiSelect(ImmutableList.<String> of());
		assertMultiSelectValue("", ImmutableList.<String> of());
		editor.setValue("one,two");
		assertMultiSelectValue("one, two", ImmutableList.of("one", "two"));
		editor.setValue("");
		assertMultiSelectValue("", ImmutableList.<String> of());
	}

	@Test
	public void testMultiSelect() {
		createMultiSelect(ImmutableList.of("one", "two"));
		assertMultiSelectValue("one, two", ImmutableList.of("one", "two"));
		editor.setValue("one,two,three,four");
		assertMultiSelectValue("one, two, three, four", ImmutableList.of("one", "two", "three", "four"));
		editor.setValue("one");
		assertMultiSelectValue("one", ImmutableList.of("one"));
		editor.setValue("one,two ,      three,  four  ,five     ,,        ,");
		assertMultiSelectValue("one, two, three, four, five", ImmutableList.of("one", "two", "three", "four", "five"));
	}

	private void createSingleSelect(String value) {
		singleSelectAttribute = taskData.getRoot().createAttribute("singleSelect");
		singleSelectAttribute.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
		singleSelectAttribute.setValue(value);
		editor = new LabelsAttributeEditor(model, singleSelectAttribute);
		assertLayoutHint(false);
	}

	private void createMultiSelect(List<String> values) {
		multiSelectAttribute = taskData.getRoot().createAttribute("multiSelect");
		multiSelectAttribute.getMetaData().setType(TaskAttribute.TYPE_MULTI_SELECT);
		multiSelectAttribute.setValues(ImmutableList.copyOf(values));
		editor = new LabelsAttributeEditor(model, multiSelectAttribute);
		assertLayoutHint(true);
	}

	private void assertSingleSelectValue(String value) {
		assertEquals(value, editor.getValue());
		assertEquals(value, singleSelectAttribute.getValue());
	}

	private void assertLayoutHint(boolean isMultiSelect) {
		assertEquals(ColumnSpan.SINGLE, editor.getLayoutHint().columnSpan);
		assertEquals((isMultiSelect ? RowSpan.MULTIPLE : RowSpan.SINGLE), editor.getLayoutHint().rowSpan);
	}

	private void assertMultiSelectValue(String stringValue, List<String> values) {
		assertEquals(stringValue, editor.getValue());
		assertEquals(values, multiSelectAttribute.getValues());
	}

}
