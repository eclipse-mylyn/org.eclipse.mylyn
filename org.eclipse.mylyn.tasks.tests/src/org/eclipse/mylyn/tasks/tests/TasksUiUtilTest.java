/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;

/**
 * @author Shawn Minto
 */
public class TasksUiUtilTest extends TestCase {

	private TaskListManager manager;

	private TaskCategory cat1;

	private AbstractTask cat1task1;

	private AbstractTask cat1task2;

	private IWorkbenchPage activePage;

	@Override
	public void setUp() throws Exception {
		manager = TasksUiPlugin.getTaskListManager();

		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		manager.resetTaskList();

		TasksUiPlugin.getDefault().getLocalTaskRepository();

		cat1 = new TaskCategory("First Category");
		manager.getTaskList().addCategory(cat1);

		cat1task1 = TasksUiInternal.createNewLocalTask("task 1");
		cat1task1.setPriority(PriorityLevel.P1.toString());
		cat1task1.setCompletionDate(new Date());
		manager.getTaskList().addTask(cat1task1, cat1);

		cat1task2 = TasksUiInternal.createNewLocalTask("task 2");
		cat1task2.setPriority(PriorityLevel.P2.toString());
		manager.getTaskList().addTask(cat1task2, cat1);

		assertEquals(cat1.getChildren().size(), 2);

		activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		activePage.closeAllEditors(false);
		assertEquals(0, activePage.getEditorReferences().length);
	}

	@Override
	public void tearDown() throws Exception {
		activePage.closeAllEditors(false);
	}

	public void testOpenTaskFromTask() {
		TasksUiUtil.openTask(cat1task1);
		assertEquals(1, activePage.getEditorReferences().length);
		assertTrue(activePage.getEditorReferences()[0].getEditor(true) instanceof TaskEditor);

		TasksUiUtil.openTask(cat1task2);
		assertEquals(2, activePage.getEditorReferences().length);
		assertTrue(activePage.getEditorReferences()[0].getEditor(true) instanceof TaskEditor);
		assertTrue(activePage.getEditorReferences()[1].getEditor(true) instanceof TaskEditor);
	}

	public void testOpenTaskFromString() {
		TasksUiUtil.openTask((String) null);
		assertEquals(1, activePage.getEditorReferences().length);
		assertTrue(activePage.getEditorReferences()[0].getEditor(true) instanceof WebBrowserEditor);
	}

	public void testOpenUrl() {
		TasksUiUtil.openUrl(null);
		assertEquals(1, activePage.getEditorReferences().length);
		IEditorPart editor = activePage.getEditorReferences()[0].getEditor(true);
		assertTrue(editor instanceof WebBrowserEditor);
		assertTrue(editor.getEditorInput() instanceof WebBrowserEditorInput);
		assertEquals(null, ((WebBrowserEditorInput) editor.getEditorInput()).getURL());

		TasksUiUtil.openUrl("http://eclipse.org/mylyn");
		assertEquals(2, activePage.getEditorReferences().length);
		editor = activePage.getEditorReferences()[0].getEditor(true);
		assertTrue(editor instanceof WebBrowserEditor);
		assertEquals(null, ((WebBrowserEditorInput) editor.getEditorInput()).getURL());

		IEditorPart editor2 = activePage.getEditorReferences()[1].getEditor(true);
		assertTrue(editor2 instanceof WebBrowserEditor);
		assertNotNull(((WebBrowserEditorInput) editor2.getEditorInput()).getURL());
		assertEquals("http://eclipse.org/mylyn", ((WebBrowserEditorInput) editor2.getEditorInput()).getURL().toString());
	}
}