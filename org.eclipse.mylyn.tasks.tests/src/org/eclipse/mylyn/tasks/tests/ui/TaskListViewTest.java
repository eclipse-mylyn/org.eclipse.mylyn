/*******************************************************************************
 * Copyright (c) 2010, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * @author Steffen Pingel
 */
public class TaskListViewTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
		TaskTestUtil.createMockRepository();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
	}

	public void testSelectedAndFocusTaskMultiple() {
		TaskList taskList = TasksUiPlugin.getTaskList();
		TaskCategory category1 = new TaskCategory(taskList.getUniqueHandleIdentifier());
		taskList.addCategory(category1);

		TaskTask task1 = TaskTestUtil.createMockTask("task1");
		TaskTask task2 = TaskTestUtil.createMockTask("task2");

		taskList.addTask(task1, category1);
		taskList.addTask(task2, category1);

		TaskListView view = (TaskListView) WorkbenchUtil.showViewInActiveWindow(ITasksUiConstants.ID_VIEW_TASKS);
		view.refresh();

		TreeSelection selection = new TreeSelection(new TreePath(new Object[] { category1, task1 }));
		// select multiple
		view.getViewer().setSelection(new StructuredSelection(new Object[] { task1, task2 }));
		view.selectedAndFocusTask(task1);
		// make sure only a single task is selected
		assertEquals(toString(selection), toString(((TreeSelection) view.getViewer().getSelection())));
	}

	public void testSelectedAndFocusTaskRestore() {
		TaskList taskList = TasksUiPlugin.getTaskList();
		TaskCategory category1 = new TaskCategory(taskList.getUniqueHandleIdentifier());
		taskList.addCategory(category1);
		// create a query since tasks can only be in one category
		RepositoryQuery category2 = new RepositoryQuery(MockRepositoryConnector.CONNECTOR_KIND,
				taskList.getUniqueHandleIdentifier());
		taskList.addQuery(category2);

		TaskTask task1 = TaskTestUtil.createMockTask("task1");

		taskList.addTask(task1, category1);
		taskList.addTask(task1, category2);

		TaskListView view = (TaskListView) WorkbenchUtil.showViewInActiveWindow(ITasksUiConstants.ID_VIEW_TASKS);
		view.refresh();

		TreeSelection selection = new TreeSelection(new TreePath(new Object[] { category1, task1 }));
		view.getViewer().setSelection(selection);
		view.selectedAndFocusTask(task1);
		assertEquals(toString(selection), toString(((TreeSelection) view.getViewer().getSelection())));

		// select different path and restore original path
		view.getViewer().setSelection(new TreeSelection(new TreePath(new Object[] { category2, task1 })));
		view.selectedAndFocusTask(task1);
		assertEquals(selection, view.getViewer().getSelection());
	}

	public void testWelcomeMessage() throws WorkbenchException {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.WELCOME_MESSAGE, Boolean.toString(false));
		// Closing the task list view somehow causes a widget disposed error in testBooleanAttribute,
		// so instead we need to open a new window to create a new task list view
		IWorkbenchWindow window = PlatformUI.getWorkbench().openWorkbenchWindow(null);
		try {
			window.getActivePage().showView(ITasksUiConstants.ID_VIEW_TASKS);
			assertTrue(TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.getBoolean(ITasksUiPreferenceConstants.WELCOME_MESSAGE));
		} finally {
			window.close();
		}
	}

	private String toString(TreeSelection s1) {
		StringBuilder sb = new StringBuilder();
		for (TreePath path : s1.getPaths()) {
			sb.append("[");
			for (int i = 0; i < path.getSegmentCount(); i++) {
				sb.append(path.getSegment(i));
				sb.append(", ");
			}
			sb.setLength(sb.length() - 2);
			sb.append("]");
		}
		return sb.toString();
	}

}
