/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.BooleanAttributeEditor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Benjamin Muskalla
 */
public class AttributeEditorTest extends TestCase {

	private class MockAttributeEditor extends AbstractAttributeEditor {

		private MockAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) throws CoreException {
			super(manager, taskAttribute);
			setControl(new Shell());
		}

		@Override
		public void createControl(Composite parent, FormToolkit toolkit) {
			// ignore
		}

		@Override
		public boolean needsValue() {
			return super.needsValue();
		}

		@Override
		public void refresh() {
			// ignore
		}

		@Override
		public Label getLabelControl() {
			return new Label(new Shell(), SWT.NONE);
		}

		@Override
		protected boolean shouldAutoRefresh() {
			return true;
		}
	}

	private class MockBooleanAttributeEditor extends BooleanAttributeEditor {

		public MockBooleanAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
			super(manager, taskAttribute);
		}

		@Override
		public boolean needsValue() {
			return super.needsValue();
		}

	}

	private TaskRepository repository;

	private TaskData taskData;

	private TaskDataModel manager;

	@Override
	protected void setUp() throws Exception {
		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		taskData = new TaskData(new TaskAttributeMapper(repository), MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "1");

		manager = createManager();
	}

	public void testDetermineNotRequired() throws Exception {
		TaskAttribute attribute = new TaskAttribute(taskData.getRoot(), "not.required.field");
		MockAttributeEditor editor = new MockAttributeEditor(manager, attribute);
		assertFalse(editor.needsValue());
		attribute.setValue("");
		assertFalse(editor.needsValue());
		attribute.setValue("abc");
		assertFalse(editor.needsValue());
	}

	public void testDetermineRequired() throws Exception {
		TaskAttribute attribute = new TaskAttribute(taskData.getRoot(), "a.required.field");
		attribute.getMetaData().setRequired(true);
		MockAttributeEditor editor = new MockAttributeEditor(manager, attribute);
		assertTrue(editor.needsValue());
		attribute.setValue("");
		assertTrue(editor.needsValue());
		attribute.setValue("abc");
		assertFalse(editor.needsValue());
	}

	public void testDecorateRequired() throws Exception {
		final StringBuilder eventLog = new StringBuilder();
		TaskAttribute attribute = new TaskAttribute(taskData.getRoot(), "a.required.field");
		MockAttributeEditor editor = new MockAttributeEditor(manager, attribute) {
			@Override
			protected void decorateRequired() {
				eventLog.append("decorateRequired");
			}

		};

		assertEquals("", eventLog.toString());
		Color someColor = WorkbenchUtil.getShell().getDisplay().getSystemColor(SWT.COLOR_CYAN);
		editor.decorate(someColor);
		assertEquals("", eventLog.toString());

		attribute.getMetaData().setRequired(true);
		editor.decorate(someColor);
		assertEquals("decorateRequired", eventLog.toString());
		eventLog.setLength(0);

		attribute.getMetaData().setRequired(false);
		editor.decorate(someColor);
		assertEquals("", eventLog.toString());
	}

	public void testDecorateRequiredOnChange() throws Exception {
		final StringBuilder eventLog = new StringBuilder();
		TaskAttribute attribute = new TaskAttribute(taskData.getRoot(), "a.required.field");
		MockAttributeEditor editor = new MockAttributeEditor(manager, attribute) {

			@Override
			protected void decorateRequired() {
				eventLog.append("decorateRequired");
			}

			@Override
			public boolean needsValue() {
				eventLog.append("asked");
				return super.needsValue();
			}

		};

		assertEquals("", eventLog.toString());
		Color someColor = WorkbenchUtil.getShell().getDisplay().getSystemColor(SWT.COLOR_CYAN);

		attribute.getMetaData().setRequired(true);
		editor.decorate(someColor);
		assertEquals("askeddecorateRequired", eventLog.toString());
		eventLog.setLength(0);

		attribute.setValue("someValue");
		manager.attributeChanged(attribute);

		assertEquals("asked", eventLog.toString());
	}

	public void testDecorateRequiredReal() throws Exception {
		TaskAttribute attribute = new TaskAttribute(taskData.getRoot(), "a.required.field");
		MockAttributeEditor editor = new MockAttributeEditor(manager, attribute);

		Color someColor = WorkbenchUtil.getShell().getDisplay().getSystemColor(SWT.COLOR_CYAN);

		attribute.getMetaData().setRequired(true);
		editor.decorate(someColor);

		attribute.getMetaData().setRequired(false);
		manager.attributeChanged(attribute);
	}

	public void testNoDecorateWithoutLabel() throws Exception {
		TaskAttribute attribute = new TaskAttribute(taskData.getRoot(), "a.required.field");
		MockAttributeEditor editor = new MockAttributeEditor(manager, attribute) {
			@Override
			public Label getLabelControl() {
				return null;
			}
		};

		Color someColor = WorkbenchUtil.getShell().getDisplay().getSystemColor(SWT.COLOR_CYAN);

		attribute.getMetaData().setRequired(true);
		editor.decorate(someColor);

		attribute.getMetaData().setRequired(false);
		manager.attributeChanged(attribute);
	}

	public void testBooleanAttribute() throws Exception {
		FormToolkit toolkit = new FormToolkit(Display.getDefault());
		TaskAttribute attribute = new TaskAttribute(taskData.getRoot(), "a.required.boolean");
		attribute.getMetaData().setType(TaskAttribute.TYPE_BOOLEAN);
		attribute.getMetaData().setRequired(true);
		final AtomicBoolean firedAttributeChanged = new AtomicBoolean();
		manager.addModelListener(new TaskDataModelListener() {
			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				firedAttributeChanged.set(true);
			}
		});

		MockBooleanAttributeEditor editor = new MockBooleanAttributeEditor(manager, attribute);
		assertFalse(editor.needsValue());
		assertFalse(editor.getValue());
		assertFalse(attribute.hasValue());
		editor.createControl(WorkbenchUtil.getShell(), toolkit);
		assertFalse(attribute.hasValue());
		processAllEvents();
		assertTrue(attribute.hasValue());
		assertEquals(Boolean.toString(false), attribute.getValue());
		assertFalse(firedAttributeChanged.get());

		attribute.setValue(Boolean.toString(true));
		editor = new MockBooleanAttributeEditor(manager, attribute);
		editor.createControl(WorkbenchUtil.getShell(), toolkit);
		processAllEvents();
		assertEquals(Boolean.toString(true), attribute.getValue());
		assertFalse(firedAttributeChanged.get());
	}

	/**
	 * wait for the async call in BooleanAttributeEditor to run
	 */
	private void processAllEvents() {
		while (Display.getDefault().readAndDispatch()) {
		}
	}

	private TaskDataModel createManager() throws Exception {
		MockTask task = new MockTask("taskId");
		TaskDataState state = new TaskDataState("kind", "url", "taskId");
		state.setEditsData(taskData);
		state.setLocalTaskData(taskData);
		TaskDataModel manager = new TaskDataModel(repository, task, state);
		return manager;
	}

}
