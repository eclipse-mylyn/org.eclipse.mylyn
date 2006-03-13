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

package org.eclipse.mylar.tasklist.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.internal.tasklist.ui.TaskListUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.TaskPriorityFilter;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListChangeListener;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;

/**
 * Tests TaskListView's filtering mechanism.
 * 
 * @author Ken Sueda
 * 
 */
public class TaskListUiTest extends TestCase {

	private TaskCategory cat1 = null;

	private Task cat1task1 = null;

	private Task cat1task2 = null;

	private Task cat1task3 = null;

	private Task cat1task4 = null;

	private Task cat1task5 = null;

	private Task cat1task1sub1 = null;

	private TaskCategory cat2 = null;

	private Task cat2task1 = null;

	private Task cat2task2 = null;

	private Task cat2task3 = null;

	private Task cat2task4 = null;

	private Task cat2task5 = null;

	private Task cat2task1sub1 = null;

	private final static int CHECK_COMPLETE_FILTER = 1;

	private final static int CHECK_INCOMPLETE_FILTER = 2;

	private final static int CHECK_PRIORITY_FILTER = 3;

	public void setUp() throws PartInitException {
		try {
			MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					"org.eclipse.mylar.tasks.ui.views.TaskListView");
			TaskListManager manager = MylarTaskListPlugin.getTaskListManager();
			cat1 = new TaskCategory("First Category", manager.getTaskList());

			cat1task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
			cat1task1.setPriority(Task.PriorityLevel.P1.toString());
			cat1task1.setCompleted(true);
			cat1task1.setContainer(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task1);

			cat1task1sub1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "sub task 1", true);
			cat1task1sub1.setPriority(Task.PriorityLevel.P1.toString());
			cat1task1sub1.setCompleted(true);
			cat1task1sub1.setParent(cat1task1);
			cat1task1.addSubTask(cat1task1sub1);

			cat1task2 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 2", true);
			cat1task2.setPriority(Task.PriorityLevel.P2.toString());
			cat1task2.setContainer(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task2);

			cat1task3 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 3", true);
			cat1task3.setPriority(Task.PriorityLevel.P3.toString());
			cat1task3.setCompleted(true);
			cat1task3.setContainer(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task3);

			cat1task4 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 4", true);
			cat1task4.setPriority(Task.PriorityLevel.P4.toString());
			cat1task4.setContainer(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task4);

			cat1task5 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 5", true);
			cat1task5.setPriority(Task.PriorityLevel.P5.toString());
			cat1task5.setCompleted(true);
			cat1task5.setContainer(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task5);

			manager.getTaskList().addCategory(cat1);
			assertEquals(cat1.getChildren().size(), 5);

			cat2 = new TaskCategory("Second Category", manager.getTaskList());

			cat2task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
			cat2task1.setPriority(Task.PriorityLevel.P1.toString());
			cat2task1.setContainer(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task1);

			cat2task1sub1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "sub task 1", true);
			cat2task1sub1.setPriority(Task.PriorityLevel.P1.toString());
			cat2task1sub1.setParent(cat2task1);
			cat2task1.addSubTask(cat2task1sub1);

			cat2task2 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 2", true);
			cat2task2.setPriority(Task.PriorityLevel.P2.toString());
			cat2task2.setCompleted(true);
			cat2task2.setContainer(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task2);

			cat2task3 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 3", true);
			cat2task3.setPriority(Task.PriorityLevel.P3.toString());
			cat2task3.setContainer(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task3);

			cat2task4 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 4", true);
			cat2task4.setPriority(Task.PriorityLevel.P4.toString());
			cat2task4.setCompleted(true);
			cat2task4.setContainer(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task4);

			cat2task5 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 5", true);
			cat2task5.setPriority(Task.PriorityLevel.P5.toString());
			cat2task5.setContainer(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task5);

			manager.getTaskList().addCategory(cat2);
			manager.saveTaskList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tearDown() {
		// clear everything
	}

	public void testUiFilter() {
		try {
			assertNotNull(TaskListView.getDefault());
			TreeViewer viewer = TaskListView.getDefault().getViewer();
			TaskListView.getDefault().addFilter(TaskListView.getDefault().getCompleteFilter());
			viewer.refresh();
			viewer.expandAll();
			TreeItem[] items = viewer.getTree().getItems();
			assertTrue(checkFilter(CHECK_COMPLETE_FILTER, items));
			TaskListView.getDefault().removeFilter(TaskListView.getDefault().getCompleteFilter());

			TaskPriorityFilter filter = (TaskPriorityFilter) TaskListView.getDefault().getPriorityFilter();
			filter.displayPrioritiesAbove("P2");
			TaskListView.getDefault().addFilter(filter);
			viewer.refresh();
			viewer.expandAll();
			items = viewer.getTree().getItems();

			// check priority tasks
			assertTrue(checkFilter(CHECK_PRIORITY_FILTER, items));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests that TaskEditors remove all listeners when closed
	 */
	public void testListenersRemoved() {

		int numListenersBefore = 0;
		int numListenersDuring = 0;
		int numListenersAfter = 0;

		TaskListManager manager = MylarTaskListPlugin.getTaskListManager();
		List<ITaskListChangeListener> listeners = manager.getTaskList().getChangeListeners();
		numListenersBefore = listeners.size();

		// open a task in editor
//		cat1task1.setForceSyncOpen(true);
		TaskListUiUtil.openEditor(cat1task1, false);
//		cat1task1.openTaskInEditor(false);
//		cat1task2.setForceSyncOpen(true);
//		cat1task2.openTaskInEditor(false);
		TaskListUiUtil.openEditor(cat1task2, false);

		listeners = manager.getTaskList().getChangeListeners();
		numListenersDuring = listeners.size();

		assertEquals(numListenersDuring, numListenersBefore + 2);

		MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(
				false);

		listeners = manager.getTaskList().getChangeListeners();
		numListenersAfter = listeners.size();
		assertEquals(numListenersBefore, numListenersAfter);

	}

	public boolean checkFilter(int type, TreeItem[] items) {
		switch (type) {
		case CHECK_COMPLETE_FILTER:
			return checkCompleteIncompleteFilter(items, false);
		case CHECK_INCOMPLETE_FILTER:
			return checkCompleteIncompleteFilter(items, true);
		case CHECK_PRIORITY_FILTER:
			return checkPriorityFilter(items);
		default:
			return false;
		}
	}

	public boolean checkCompleteIncompleteFilter(TreeItem[] items, boolean checkComplete) {
		assertEquals(2, items.length);
		int count = 0;
		for (int i = 0; i < items.length; i++) {
			assertTrue(items[i].getData() instanceof TaskCategory);
			TreeItem[] sub = items[i].getItems();
			for (int j = 0; j < sub.length; j++) {
				assertTrue(sub[j].getData() instanceof ITask);
				ITask task = (ITask) sub[j].getData();
				if (checkComplete) {
					assertTrue(task.isCompleted());
				} else {
					assertFalse(task.isCompleted());
				}
				count++;
			}
		}
		assertTrue(count == 5);
		return true;
	}

	public boolean checkPriorityFilter(TreeItem[] items) {
		assertTrue(items.length == 2);
		int p2Count = 0;
		int p1Count = 0;
		for (int i = 0; i < items.length; i++) {
			assertTrue(items[i].getData() instanceof TaskCategory);
			TreeItem[] sub = items[i].getItems();
			for (int j = 0; j < sub.length; j++) {
				assertTrue(sub[j].getData() instanceof ITask);
				ITask task = (ITask) sub[j].getData();
				assertTrue(task.getPriority().equals("P2") || task.getPriority().equals("P1"));
				if (task.getPriority().equals("P2")) {
					p2Count++;
				} else {
					p1Count++;
				}
			}
		}
		assertEquals(2, p1Count);
		assertEquals(2, p2Count);
		return true;
	}

}
