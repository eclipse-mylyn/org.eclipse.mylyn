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

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.tasks.ui.actions.NextTaskDropDownAction;
import org.eclipse.mylar.internal.tasks.ui.actions.PreviousTaskDropDownAction;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskNavigateDropDownAction.TaskNavigateAction;
import org.eclipse.mylar.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.ui.TaskListManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PartInitException;

/**
 * @author Wes Coelho
 */
public class TaskHistoryTest extends TestCase {

	protected TaskListManager manager = TasksUiPlugin.getTaskListManager();

	protected TaskListView taskView = null;

	protected Task task1 = null;

	protected Task task2 = null;

	protected Task task3 = null;

	protected Task task4 = null;

	protected Task task5 = null;

	protected void setUp() throws Exception {
		super.setUp();
		
		try {
			TasksUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					"org.eclipse.mylar.tasks.ui.views.TaskListView");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("View not initialized");
		}

		assertNotNull(TaskListView.getFromActivePerspective());
		taskView = TaskListView.getFromActivePerspective();

		resetHistory();

		task1 = new Task(TasksUiPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
		manager.getTaskList().moveToRoot(task1);
		
		task2 = new Task(TasksUiPlugin.getTaskListManager().genUniqueTaskHandle(), "task 2", true);
		manager.getTaskList().moveToRoot(task2);
		
		task3 = new Task(TasksUiPlugin.getTaskListManager().genUniqueTaskHandle(), "task 3", true);
		manager.getTaskList().moveToRoot(task3);
		
		task4 = new Task(TasksUiPlugin.getTaskListManager().genUniqueTaskHandle(), "task 4", true);
		manager.getTaskList().moveToRoot(task4);

		task5 = new Task(TasksUiPlugin.getTaskListManager().genUniqueTaskHandle(), "task 5", true);
		manager.getTaskList().moveToRoot(task5);
	}

	private void resetHistory() {
		taskView.clearTaskHistory();
		ContextCorePlugin.getContextManager().resetActivityHistory();
	}

	/**
	 * Tests the next task and previous task navigation.
	 */
	public void testBasicHistoryNavigation() {
		(new TaskActivateAction()).run(task1);
		taskView.addTaskToHistory(task1);
		(new TaskActivateAction()).run(task2);
		taskView.addTaskToHistory(task2);
		(new TaskActivateAction()).run(task3);
		taskView.addTaskToHistory(task3);

		assertTrue(task3.isActive());
		assertFalse(task2.isActive());

		taskView.getPreviousTaskAction().run();
		assertTrue(task2.isActive());
 
		taskView.getPreviousTaskAction().run();
		assertTrue(task1.isActive());

		taskView.getPreviousTaskAction().run();
		assertTrue(task1.isActive());

		taskView.getNextTaskAction().run();
		assertTrue(task2.isActive());

		taskView.getNextTaskAction().run();
		assertTrue(task3.isActive());

		taskView.getNextTaskAction().run();
		assertTrue(task3.isActive());

		taskView.getPreviousTaskAction().run();
		assertTrue(task2.isActive());

		taskView.getNextTaskAction().run();
		assertTrue(task3.isActive());

		(new TaskActivateAction()).run(task4);
		taskView.addTaskToHistory(task4); // Simulate clicking on it rather
											// than navigating next or previous
		assertTrue(task4.isActive());

		taskView.getNextTaskAction().run();
		assertTrue(task4.isActive());

		taskView.getPreviousTaskAction().run();
		assertTrue(task3.isActive());

		taskView.getNextTaskAction().run();
		assertTrue(task4.isActive());

	}

	/**
	 * Tests navigation to previous/next tasks that are chosen from a list
	 * rather than being sequentially navigated
	 */
	public void testArbitraryHistoryNavigation() {

		resetHistory();

		// Simulate activating the tasks by clicking rather than
		// navigating previous/next
		(new TaskActivateAction()).run(task1);
		taskView.addTaskToHistory(task1);
		(new TaskActivateAction()).run(task2);
		taskView.addTaskToHistory(task2);
		(new TaskActivateAction()).run(task3);
		taskView.addTaskToHistory(task3);
		(new TaskActivateAction()).run(task4);
		taskView.addTaskToHistory(task4);

		assertTrue(task4.isActive());
		TaskActivationHistory taskHistory = taskView.getTaskActivationHistory();
		List<ITask> prevHistoryList = taskHistory.getPreviousTasks();

		// Check that the previous history list looks right
		assertTrue(prevHistoryList.size() >= 3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task1);

		// Get a task from the list and activate it
		PreviousTaskDropDownAction prevAction = new PreviousTaskDropDownAction(taskView, taskHistory);
		TaskNavigateAction navigateAction = prevAction.new TaskNavigateAction(task2);
		navigateAction.run();
		taskHistory.navigatedToTask(task2);

		assertTrue(task2.isActive());

		// Now check that the next and prev lists look right
		prevHistoryList = taskHistory.getPreviousTasks();
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task1);
		List<ITask> nextHistoryList = taskHistory.getNextTasks();
		assertTrue(nextHistoryList.get(0) == task3);
		assertTrue(nextHistoryList.get(1) == task4);

		// Navigate to a next item
		NextTaskDropDownAction nextAction = new NextTaskDropDownAction(taskView, taskHistory);
		navigateAction = nextAction.new TaskNavigateAction(task4);
		navigateAction.run();
		taskHistory.navigatedToTask(task4);

		assertTrue(task4.isActive());

		// Check that the prev and next lists look right
		nextHistoryList = taskHistory.getNextTasks();
		prevHistoryList = taskHistory.getPreviousTasks();
		assertTrue(nextHistoryList.size() == 0);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task1);

		// Check that a deactivated task appears first on the history list
		(new TaskActivateAction()).run(task5);
		(new TaskDeactivateAction()).run(task5);
		taskView.addTaskToHistory(task5);
		prevHistoryList = taskHistory.getPreviousTasks();
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task5);

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
