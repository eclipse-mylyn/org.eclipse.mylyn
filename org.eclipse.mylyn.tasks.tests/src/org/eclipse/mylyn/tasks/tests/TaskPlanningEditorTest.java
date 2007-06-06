/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.tasks.ui.editors.TaskPlanningEditor;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.mylar.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import junit.framework.TestCase;

/**
 * @author Rob Elves
 */
public class TaskPlanningEditorTest extends TestCase {

	private static final String MOCK_LABEL = "label";

	private static final String MOCK_HANDLE = "handle";

	private static final String DESCRIPTION = "summary";

	private static final String NEW_DESCRIPTION = "new summary";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
		Task task = new Task(MOCK_HANDLE, MOCK_LABEL);
		task.setSummary(DESCRIPTION);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		TasksUiUtil.openEditor(task, false, true);
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
		Task task = new Task(MOCK_HANDLE, MOCK_LABEL);
		task.setSummary(DESCRIPTION);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		TasksUiUtil.openEditor(task, false, true);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertTrue(page.getActiveEditor() instanceof TaskEditor);
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertTrue(taskEditor.getActivePageInstance() instanceof TaskPlanningEditor);
		TaskPlanningEditor editor = (TaskPlanningEditor) taskEditor.getActivePageInstance();
		assertFalse(editor.isDirty());
		assertEquals(DESCRIPTION, editor.getDescription());
		TasksUiPlugin.getTaskListManager().getTaskList().renameTask(task, NEW_DESCRIPTION);
		assertEquals(NEW_DESCRIPTION, task.getSummary());
		editor.updateTaskData(task);
		//assertEquals(NEW_DESCRIPTION, editor.getFormTitle());
		assertEquals(NEW_DESCRIPTION, editor.getDescription());
		assertFalse(editor.isDirty());
	}
	
	/** 
	 * Test that if editor is dirty and external rename happens
	 * editor remains dirty
	 */
	public void testRenameInDirtyState() {		
		Task task = new Task(MOCK_HANDLE, MOCK_LABEL);
		task.setSummary(DESCRIPTION);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		TasksUiUtil.openEditor(task, false, true);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertTrue(page.getActiveEditor() instanceof TaskEditor);
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertTrue(taskEditor.getActivePageInstance() instanceof TaskPlanningEditor);
		TaskPlanningEditor editor = (TaskPlanningEditor) taskEditor.getActivePageInstance();
		assertFalse(editor.isDirty());
		editor.setDescription(NEW_DESCRIPTION);
		assertTrue(editor.isDirty());
		TasksUiPlugin.getTaskListManager().getTaskList().renameTask(task, NEW_DESCRIPTION+"2");
		assertEquals(NEW_DESCRIPTION+"2", task.getSummary());
		editor.updateTaskData(task);
		//assertEquals(NEW_DESCRIPTION+"2", editor.getFormTitle());
		assertEquals(NEW_DESCRIPTION+"2", editor.getDescription());
		assertTrue(editor.isDirty());
	}
	
}
