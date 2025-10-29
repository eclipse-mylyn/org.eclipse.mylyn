/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

import junit.framework.TestCase;

/**
 * @author Rob Elves
 */
// FIXME re-enable tests
public class TaskPlanningEditorTest extends TestCase {

//	private static final String MOCK_LABEL = "label";
//
//	private static final String DESCRIPTION = "summary";
//
//	private static final String NEW_DESCRIPTION = "new summary";

	@Override
	protected void setUp() throws Exception {
		TasksUiPlugin.getDefault().getLocalTaskRepository();
	}

	@Override
	protected void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		TasksUiPlugin.getRepositoryManager().clearRepositories();
		TaskTestUtil.resetTaskList();
	}

// 	public void testDirtyOnEdit() {
//		LocalTask task = new LocalTask("1", MOCK_LABEL);
//		task.setSummary(DESCRIPTION);
//		TasksUiPlugin.getTaskList().addTask(task);
//		TasksUiUtil.openTask(task);
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		assertNotNull(page);
//		assertEquals(TaskEditor.class, page.getActiveEditor().getClass());
//		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
//		assertEquals(TaskPlanningEditor.class, taskEditor.getActivePageInstance().getClass());
//		TaskPlanningEditor editor = (TaskPlanningEditor) taskEditor.getActivePageInstance();
//		assertFalse(editor.isDirty());
//		editor.setNotes("notes");
//		assertTrue(editor.isDirty());
//		editor.doSave(new NullProgressMonitor());
//		assertFalse(editor.isDirty());
//		editor.setDescription(NEW_DESCRIPTION);
//		assertTrue(editor.isDirty());
//		editor.doSave(new NullProgressMonitor());
//		assertEquals(NEW_DESCRIPTION, task.getSummary());
//		assertFalse(editor.isDirty());
//	}
//
//	public void testNotDirtyOnRename() {
//		LocalTask task = new LocalTask("1", MOCK_LABEL);
//		task.setSummary(DESCRIPTION);
//		TasksUiPlugin.getTaskList().addTask(task);
//		TasksUiUtil.openTask(task);
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		assertNotNull(page);
//		assertEquals(TaskEditor.class, page.getActiveEditor().getClass());
//		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
//		assertEquals(TaskPlanningEditor.class, taskEditor.getActivePageInstance().getClass());
//		TaskPlanningEditor editor = (TaskPlanningEditor) taskEditor.getActivePageInstance();
//		assertFalse(editor.isDirty());
//		assertEquals(DESCRIPTION, editor.getDescription());
//		task.setSummary(NEW_DESCRIPTION);
//		editor.updateTaskData(task);
//		//assertEquals(NEW_DESCRIPTION, editor.getFormTitle());
//		assertEquals(NEW_DESCRIPTION, editor.getDescription());
//		assertFalse(editor.isDirty());
//	}
//
//	/**
//	 * Test that if editor is dirty and external rename happens editor remains dirty
//	 */
//	public void testRenameInDirtyState() {
//		LocalTask task = new LocalTask("1", MOCK_LABEL);
//		task.setSummary(DESCRIPTION);
//		TasksUiPlugin.getTaskList().addTask(task);
//		TasksUiUtil.openTask(task);
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		assertNotNull(page);
//		assertEquals(TaskEditor.class, page.getActiveEditor().getClass());
//		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
//		assertEquals(TaskPlanningEditor.class, taskEditor.getActivePageInstance().getClass());
//		TaskPlanningEditor editor = (TaskPlanningEditor) taskEditor.getActivePageInstance();
//		assertFalse(editor.isDirty());
//		editor.setDescription(NEW_DESCRIPTION);
//		assertTrue(editor.isDirty());
//
//		task.setSummary(NEW_DESCRIPTION + "2");
//		editor.updateTaskData(task);
//		assertEquals(NEW_DESCRIPTION, editor.getDescription());
//		assertTrue(editor.isDirty());
//	}

}
