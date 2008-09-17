/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskHistoryDropDownAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkingSet;

/**
 * @author Frank Becker
 * @author Wesley Coelho
 */
public class TaskActivationHistoryTest extends TestCase {

	private TaskActivationHistory history;

	private TaskList taskList;

	private AbstractTask task1;

	private AbstractTask task2;

	private AbstractTask task3;

	private AbstractTask task4;

	private AbstractTask task5;

	private ActivateTaskHistoryDropDownAction previousTaskAction;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		history = TasksUiPlugin.getTaskActivityManager().getTaskActivationHistory();
		taskList = TasksUiPlugin.getTaskList();

		TasksUiPlugin.getTaskActivityManager().deactivateActiveTask();
		TasksUiPlugin.getTaskActivityManager().clear();
		ContextCorePlugin.getContextManager().resetActivityMetaContext();

		task1 = TasksUiInternal.createNewLocalTask("task 1");
		taskList.addTask(task1);
		task2 = TasksUiInternal.createNewLocalTask("task 2");
		taskList.addTask(task2);
		task3 = TasksUiInternal.createNewLocalTask("task 3");
		taskList.addTask(task3);
		task4 = TasksUiInternal.createNewLocalTask("task 4");
		taskList.addTask(task4);
		task5 = TasksUiInternal.createNewLocalTask("task 5");
		taskList.addTask(task5);

		previousTaskAction = new ActivateTaskHistoryDropDownAction();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testWithWorkinSet() {
		MockRepositoryQuery query1 = new MockRepositoryQuery("Query 1");
		MockRepositoryQuery query2 = new MockRepositoryQuery("Query 2");

		AbstractTask task11 = TasksUiInternal.createNewLocalTask("Query1 Task 1");
		AbstractTask task12 = TasksUiInternal.createNewLocalTask("Query1 Task 2");
		AbstractTask task21 = TasksUiInternal.createNewLocalTask("Query2 Task 1");
		AbstractTask task22 = TasksUiInternal.createNewLocalTask("Query2 Task 2");
		taskList.addQuery(query1);
		taskList.addQuery(query2);
		createWorkingSet(query1);
		(new TaskActivateAction()).run(task11);
		history.addTask(task11);
		taskList.addTask(task11, query1);
		(new TaskActivateAction()).run(task12);
		history.addTask(task12);
		taskList.addTask(task12, query1);

		(new TaskActivateAction()).run(task21);
		history.addTask(task21);
		taskList.addTask(task21, query2);
		(new TaskActivateAction()).run(task22);
		history.addTask(task22);
		taskList.addTask(task22, query2);

		List<AbstractTask> prevHistoryList = history.getPreviousTasks();

		// Check that the previous history list looks right
		assertTrue(prevHistoryList.size() >= 3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task22);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task21);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task12);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task11);

		prevHistoryList = history.getPreviousTasks(TasksUiInternal.getContainersFromWorkingSet(TaskListView.getActiveWorkingSets()));

		// Check that the previous history list looks right
		assertTrue(prevHistoryList.size() >= 2);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task12);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task11);

		prevHistoryList = history.getPreviousTasks(new HashSet<AbstractTaskContainer>());

		// Check that the previous history list looks right
		assertTrue(prevHistoryList.size() >= 3);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task22);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 2) == task21);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 3) == task12);
		assertTrue(prevHistoryList.get(prevHistoryList.size() - 4) == task11);
	}

	private IWorkingSet createWorkingSet(IAdaptable element) {
		IWorkingSetManager workingSetManager1 = ContextUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager1.createWorkingSet("Task Working Set", new IAdaptable[] { element });
		workingSet.setId(TaskWorkingSetUpdater.ID_TASK_WORKING_SET);
		assertTrue(Arrays.asList(workingSet.getElements()).contains(element));
		Set<IWorkingSet> sets = TaskListView.getActiveWorkingSets();
		sets.add(workingSet);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setWorkingSets(
				sets.toArray(new WorkingSet[sets.size()]));
		return workingSet;
	}

	/**
	 * Tests the next task and previous task navigation.
	 */
	public void testBasicHistoryNavigation() {
		// NOTE: legacy test
		TasksUi.getTaskActivityManager().activateTask(task1);
		history.addTask(task1);
		TasksUi.getTaskActivityManager().activateTask(task2);
		history.addTask(task2);
		TasksUi.getTaskActivityManager().activateTask(task3);
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
		// TasksUi.getTaskActivityManager().activateTask(task4);
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
		// Simulate activating the tasks by clicking rather than
		// navigating previous/next
		TasksUi.getTaskActivityManager().activateTask(task1);
		history.addTask(task1);
		TasksUi.getTaskActivityManager().activateTask(task2);
		history.addTask(task2);
		TasksUi.getTaskActivityManager().activateTask(task3);
		history.addTask(task3);
		TasksUi.getTaskActivityManager().activateTask(task4);
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
		TasksUi.getTaskActivityManager().activateTask(task2);
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
		TasksUi.getTaskActivityManager().activateTask(task4);
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

		TasksUi.getTaskActivityManager().deactivateTask(task3);

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
		// TasksUi.getTaskActivityManager().activateTask(task5);
		// (new TaskDeactivateAction()).run(task5);
		// taskView.addTaskToHistory(task5);
		// prevHistoryList = taskHistory.getPreviousTasks();
		// assertTrue(prevHistoryList.get(prevHistoryList.size() - 1) == task5);
	}

}
