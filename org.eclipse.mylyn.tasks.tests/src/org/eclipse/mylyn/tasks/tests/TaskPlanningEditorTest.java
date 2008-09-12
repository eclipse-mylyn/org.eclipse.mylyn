/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskPlanningEditor;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class TaskPlanningEditorTest extends TestCase {

	private static final String MOCK_LABEL = "label";

	private static final String DESCRIPTION = "summary";

	private static final String NEW_DESCRIPTION = "new summary";

	@Override
	protected void setUp() throws Exception {
		TasksUiPlugin.getDefault().getLocalTaskRepository();
	}

	@Override
	protected void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
		super.tearDown();
	}

	public void testDirtyOnEdit() {
		LocalTask task = new LocalTask("1", MOCK_LABEL);
		task.setSummary(DESCRIPTION);
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUiUtil.openTask(task);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertTrue(page.getActiveEditor() instanceof TaskEditor);
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertTrue(taskEditor.getActivePageInstance() instanceof TaskPlanningEditor);
		TaskPlanningEditor editor = (TaskPlanningEditor) taskEditor.getActivePageInstance();
		assertFalse(editor.isDirty());
		editor.setNotes("notes");
		assertTrue(editor.isDirty());
		editor.doSave(new NullProgressMonitor());
		assertFalse(editor.isDirty());
		editor.setDescription(NEW_DESCRIPTION);
		assertTrue(editor.isDirty());
		editor.doSave(new NullProgressMonitor());
		assertEquals(NEW_DESCRIPTION, task.getSummary());
		assertFalse(editor.isDirty());
	}

	public void testNotDirtyOnRename() {
		LocalTask task = new LocalTask("1", MOCK_LABEL);
		task.setSummary(DESCRIPTION);
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUiUtil.openTask(task);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertTrue(page.getActiveEditor() instanceof TaskEditor);
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertTrue(taskEditor.getActivePageInstance() instanceof TaskPlanningEditor);
		TaskPlanningEditor editor = (TaskPlanningEditor) taskEditor.getActivePageInstance();
		assertFalse(editor.isDirty());
		assertEquals(DESCRIPTION, editor.getDescription());
		task.setSummary(NEW_DESCRIPTION);
		editor.updateTaskData(task);
		//assertEquals(NEW_DESCRIPTION, editor.getFormTitle());
		assertEquals(NEW_DESCRIPTION, editor.getDescription());
		assertFalse(editor.isDirty());
	}

	/**
	 * Test that if editor is dirty and external rename happens editor remains dirty
	 */
	public void testRenameInDirtyState() {
		LocalTask task = new LocalTask("1", MOCK_LABEL);
		task.setSummary(DESCRIPTION);
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUiUtil.openTask(task);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertTrue(page.getActiveEditor() instanceof TaskEditor);
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertTrue(taskEditor.getActivePageInstance() instanceof TaskPlanningEditor);
		TaskPlanningEditor editor = (TaskPlanningEditor) taskEditor.getActivePageInstance();
		assertFalse(editor.isDirty());
		editor.setDescription(NEW_DESCRIPTION);
		assertTrue(editor.isDirty());
		task.setSummary(NEW_DESCRIPTION + "2");
		editor.updateTaskData(task);
		//assertEquals(NEW_DESCRIPTION+"2", editor.getFormTitle());
		assertEquals(NEW_DESCRIPTION + "2", editor.getDescription());
		assertTrue(editor.isDirty());
	}

}
