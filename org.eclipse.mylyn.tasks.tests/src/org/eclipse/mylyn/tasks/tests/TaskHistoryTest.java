/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskHistoryDropDownAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Wes Coelho
 * @author Rob Elves
 */
public class TaskHistoryTest extends TestCase {

	private final TaskListManager manager = TasksUiPlugin.getTaskListManager();

	private TaskActivationHistory history;

	private AbstractTask task1;

	private AbstractTask task2;

	private AbstractTask task3;

	private AbstractTask task4;

	private AbstractTask task5;

	private ActivateTaskHistoryDropDownAction previousTaskAction;

	@Override
	protected void setUp() throws Exception {
		resetHistory();

		task1 = TasksUiInternal.createNewLocalTask("task 1");
		manager.getTaskList().addTask(task1);

		task2 = TasksUiInternal.createNewLocalTask("task 2");
		manager.getTaskList().addTask(task2);

		task3 = TasksUiInternal.createNewLocalTask("task 3");
		manager.getTaskList().addTask(task3);

		task4 = TasksUiInternal.createNewLocalTask("task 4");
		manager.getTaskList().addTask(task4);

		task5 = TasksUiInternal.createNewLocalTask("task 5");
		manager.getTaskList().addTask(task5);

		history = TasksUi.getTaskActivityManager().getTaskActivationHistory();

		previousTaskAction = new ActivateTaskHistoryDropDownAction();
	}

	private void resetHistory() {
		manager.deactivateAllTasks();
		TasksUi.getTaskActivityManager().clear();
		ContextCore.getContextManager().resetActivityHistory();
	}

	/**
	 * Tests the next task and previous task navigation.
	 */
	public void testBasicHistoryNavigation() {
		// NOTE: legacy test
		(new TaskActivateAction()).run(task1);
		history.addTask(task1);
		(new TaskActivateAction()).run(task2);
		history.addTask(task2);
		(new TaskActivateAction()).run(task3);
		history.addTask(task3);

		assertTrue(task3.isActive());
		assertFalse(task2.isActive());
		assertFalse(task1.isActive());

		previousTaskAction.run();
		assertTrue(task2.isActive());

		previousTaskAction.run();
		assertTrue(task1.isActive());

		previousTaskAction.run();
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
	 * Tests navigation to previous/next tasks that are chosen from a list rather than being sequentially navigated
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
		List<AbstractTask> prevHistoryList = history.getPreviousTasks();

		// Check that the previous history list looks right
		assertTrue(prevHistoryList.size() >= 3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task1);

		// Get a task from the list and activate it
		(new TaskActivateAction()).run(task2);
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
		(new TaskActivateAction()).run(task4);
		assertTrue(task4.isActive());
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task4);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task1);

		// Hit previous task button
		previousTaskAction.run();
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

}
