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

/**
 * @author Wes Coelho
 * @author Rob Elves
 */
public class TaskHistoryTest extends TestCase {

	protected TaskListManager manager = TasksUiPlugin.getTaskListManager();

	protected TaskActivationHistory history;

	protected TaskListView taskView = null;

	protected Task task1 = null;

	protected Task task2 = null;

	protected Task task3 = null;

	protected Task task4 = null;

	protected Task task5 = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		taskView = TaskListView.openInActivePerspective();

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

		history = manager.getTaskActivationHistory();
	}

	private void resetHistory() {
		manager.getTaskActivationHistory().clear();
		ContextCorePlugin.getContextManager().resetActivityHistory();
	}

	/**
	 * Tests the next task and previous task navigation.
	 */
	public void testBasicHistoryNavigation() {
		(new TaskActivateAction()).run(task1);
		history.addTask(task1);
		(new TaskActivateAction()).run(task2);
		history.addTask(task2);
		(new TaskActivateAction()).run(task3);
		history.addTask(task3);

		assertTrue(task3.isActive());
		assertFalse(task2.isActive());

		taskView.getPreviousTaskAction().run();
		assertTrue(task2.isActive());

		taskView.getPreviousTaskAction().run();
		assertTrue(task1.isActive());

		taskView.getPreviousTaskAction().run();
		assertTrue(task1.isActive());

		// taskView.getPreviousTaskAction().run();
		// assertTrue(task1.isActive());
		//		
		// taskView.getPreviousTaskAction().run();
		// assertTrue(task1.isActive());

		// taskView.getNextTaskAction().run();
		// assertTrue(task2.isActive());
		//
		// taskView.getNextTaskAction().run();
		// assertTrue(task3.isActive());
		//
		// taskView.getNextTaskAction().run();
		// assertTrue(task3.isActive());
		//
		// taskView.getPreviousTaskAction().run();
		// assertTrue(task2.isActive());
		//
		// taskView.getNextTaskAction().run();
		// assertTrue(task3.isActive());
		//
		// (new TaskActivateAction()).run(task4);
		// history.addTask(task4); // Simulate clicking on it rather
		// // than navigating next or previous
		// assertTrue(task4.isActive());
		//
		// taskView.getNextTaskAction().run();
		// assertTrue(task4.isActive());
		//
		// taskView.getPreviousTaskAction().run();
		// assertTrue(task3.isActive());
		//
		// taskView.getNextTaskAction().run();
		// assertTrue(task4.isActive());

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
		history.addTask(task1);
		(new TaskActivateAction()).run(task2);
		history.addTask(task2);
		(new TaskActivateAction()).run(task3);
		history.addTask(task3);
		(new TaskActivateAction()).run(task4);
		history.addTask(task4);

		assertTrue(task4.isActive());
		//TaskActivationHistory taskHistory = taskView.getTaskActivationHistory();
		List<ITask> prevHistoryList = history.getPreviousTasks();

		// Check that the previous history list looks right
		assertTrue(prevHistoryList.size() >= 3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task1);

		// Get a task from the list and activate it
		PreviousTaskDropDownAction prevAction = new PreviousTaskDropDownAction(history);
		TaskNavigateAction navigateAction = prevAction.new TaskNavigateAction(task2);
		navigateAction.run();
		assertTrue(task2.isActive());

		// Now check that the next and prev lists look right
		prevHistoryList = history.getPreviousTasks();
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task4);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task1);

		// Activation of task outside of history navigation tool
		history.addTask(task3);
		prevHistoryList = history.getPreviousTasks();
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task4);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task1);
		
		// Pick a task from drop down history
		navigateAction = prevAction.new TaskNavigateAction(task4);
		navigateAction.run();
		assertTrue(task4.isActive());
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task4);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task1);
		
		// Hit previous task button
		taskView.getPreviousTaskAction().run();
		assertTrue(task3.isActive());
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task4);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task1);
		
		(new TaskDeactivateAction()).run(task3);
		
		// List<ITask> nextHistoryList = taskHistory.getNextTasks();
		// assertTrue(nextHistoryList.get(0) == task3);
		// assertTrue(nextHistoryList.get(1) == task4);
		//
		// // Navigate to a next item
		// NextTaskDropDownAction nextAction = new
		// NextTaskDropDownAction(taskView, taskHistory);
		// navigateAction = nextAction.new TaskNavigateAction(task4);
		// navigateAction.run();
		// taskHistory.navigatedToTask(task4);
		//
		// assertTrue(task4.isActive());
		//
		// // Check that the prev and next lists look right
		// nextHistoryList = taskHistory.getNextTasks();
		// prevHistoryList = taskHistory.getPreviousTasks();
		// assertTrue(nextHistoryList.size() == 0);
		// assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task3);
		// assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task2);
		// assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task1);
		//
		// // Check that a deactivated task appears first on the history list
		// (new TaskActivateAction()).run(task5);
		// (new TaskDeactivateAction()).run(task5);
		// taskView.addTaskToHistory(task5);
		// prevHistoryList = taskHistory.getPreviousTasks();
		// assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task5);

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
