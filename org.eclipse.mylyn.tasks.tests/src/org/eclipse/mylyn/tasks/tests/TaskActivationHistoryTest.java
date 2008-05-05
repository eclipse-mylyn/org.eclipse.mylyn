/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkingSet;

/**
 * @author Frank Becker
 * 
 * 	TODO merge with TaskHistoryTest
 */
public class TaskActivationHistoryTest extends TestCase {

	protected TaskListManager manager = TasksUiPlugin.getTaskListManager();

	protected TaskActivationHistory history;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		history = TasksUiPlugin.getTaskActivityManager().getTaskActivationHistory();
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
		manager.getTaskList().addQuery(query1);
		manager.getTaskList().addQuery(query2);
		createWorkingSet(query1);
		(new TaskActivateAction()).run(task11);
		history.addTask(task11);
		manager.getTaskList().addTask(task11, query1);
		(new TaskActivateAction()).run(task12);
		history.addTask(task12);
		manager.getTaskList().addTask(task12, query1);

		(new TaskActivateAction()).run(task21);
		history.addTask(task21);
		manager.getTaskList().addTask(task21, query2);
		(new TaskActivateAction()).run(task22);
		history.addTask(task22);
		manager.getTaskList().addTask(task22, query2);

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

}
