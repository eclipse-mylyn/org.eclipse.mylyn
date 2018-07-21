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

package org.eclipse.mylyn.tasks.ui.editors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.handlers.IHandlerService;
import org.junit.Before;
import org.junit.Test;

public class AbstractTaskEditorPageTest {
	public class TestAbstractTaskEditorPage extends AbstractTaskEditorPage {
		private TestAbstractTaskEditorPage(TaskEditor editor, String connectorKind) {
			super(editor, connectorKind);
		}

		@Override
		protected TaskDataModel createModel(final TaskEditorInput input) throws CoreException {
			TaskDataModel model = spy(
					new TaskDataModel(input.getTaskRepository(), input.getTask(), mock(ITaskDataWorkingCopy.class)));
			when(model.getTaskData()).thenReturn(taskData);
			return model;
		}
	}

	private TaskRepository repository;

	private ITask task;

	private TaskData taskData;

	private AbstractTaskEditorPage page;

	private IManagedForm form;

	private TaskAttribute attribute;

	@SuppressWarnings("restriction")
	@Before
	public void setUp() {
		repository = TaskTestUtil.createMockRepository();
		task = TaskTestUtil.createMockTask("1");
		taskData = TaskTestUtil.createMockTaskData("1");
		attribute = taskData.getRoot().createAttribute("test");
		page = spy(new TestAbstractTaskEditorPage(mock(TaskEditor.class), task.getConnectorKind()));
		form = mock(IManagedForm.class);
		when(page.getManagedForm()).thenReturn(form);
		page.init(createSite(), new TaskEditorInput(repository, task));
	}

	private IEditorSite createSite() {
		IEditorSite site = mock(IEditorSite.class);
		IHandlerService service = mock(IHandlerService.class);
		when(site.getService(any())).thenReturn(service);
		return site;
	}

	@Test
	public void testInitModelListener() {
		page.getModel().attributeChanged(attribute);
		verify(form).dirtyStateChanged();
	}

	@Test
	public void testInitModelListenerDirtyForm() {
		when(form.isDirty()).thenReturn(true);
		page.getModel().attributeChanged(attribute);
		verify(form, never()).dirtyStateChanged();
	}

}
