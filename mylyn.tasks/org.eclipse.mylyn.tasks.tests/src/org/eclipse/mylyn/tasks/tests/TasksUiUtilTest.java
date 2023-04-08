/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests;

import java.util.Date;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tests.util.TasksUiTestUtil;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import junit.framework.TestCase;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class TasksUiUtilTest extends TestCase {

	private TaskCategory cat1;

	private AbstractTask cat1task1;

	private AbstractTask cat1task2;

	private IWorkbenchPage activePage;

	private TaskList taskList;

	@Override
	public void setUp() throws Exception {
		taskList = TasksUiPlugin.getTaskList();

		TaskTestUtil.resetTaskListAndRepositories();
		TasksUiPlugin.getDefault().getLocalTaskRepository();

		cat1 = new TaskCategory("First Category");
		taskList.addCategory(cat1);

		cat1task1 = TasksUiInternal.createNewLocalTask("task 1");
		cat1task1.setPriority(PriorityLevel.P1.toString());
		cat1task1.setCompletionDate(new Date());
		taskList.addTask(cat1task1, cat1);

		cat1task2 = TasksUiInternal.createNewLocalTask("task 2");
		cat1task2.setPriority(PriorityLevel.P2.toString());
		taskList.addTask(cat1task2, cat1);

		assertEquals(cat1.getChildren().size(), 2);

		activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertTrue(activePage.closeAllEditors(false));
		assertEquals(0, activePage.getEditorReferences().length);

		TasksUiTestUtil.ensureTasksUiInitialization();
	}

	@Override
	public void tearDown() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
		activePage.closeAllEditors(false);
	}

	public void testOpenTaskFromTask() {
		TasksUiUtil.openTask(cat1task1);
		assertEquals(1, activePage.getEditorReferences().length);
		IEditorPart editor = activePage.getEditorReferences()[0].getEditor(true);
		assertNotNull(editor);
		assertEquals(TaskEditor.class, editor.getClass());

		TasksUiUtil.openTask(cat1task2);
		assertEquals(2, activePage.getEditorReferences().length);
		editor = activePage.getEditorReferences()[1].getEditor(true);
		assertNotNull(editor);
		assertEquals(TaskEditor.class, editor.getClass());
	}

	public void testOpenLocalTask() {
		ITask localTask = TasksUiInternal.createNewLocalTask("summary");
		TasksUiUtil.openTask(localTask);
		assertEquals(1, activePage.getEditorReferences().length);
		IEditorPart editor = activePage.getEditorReferences()[0].getEditor(true);
		assertEquals(TaskEditor.class, editor.getClass());
	}

	public void testOpenLocalTaskWebBrowserDefault() {
		ITask localTask = TasksUiInternal.createNewLocalTask("summary");
		try {
			TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.setValue(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH, false);
			TasksUiUtil.openTask(localTask);
			assertEquals(1, activePage.getEditorReferences().length);
			IEditorPart editor = activePage.getEditorReferences()[0].getEditor(true);
			assertEquals(TaskEditor.class, editor.getClass());
		} finally {
			TasksUiPlugin.getDefault().getPreferenceStore().setToDefault(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH);
		}
	}

}