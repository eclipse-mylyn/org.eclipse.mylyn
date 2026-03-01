/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskWorkingSetFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListInterestFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.internal.Workbench;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class TaskListFilterTest {

	private TaskListView view;

	private Set<AbstractTaskListFilter> previousFilters;

	private AbstractTask taskCompleted;

	private AbstractTask taskIncomplete;

	private AbstractTask taskOverdue;

	private AbstractTask taskDueToday;

	private AbstractTask taskCompletedToday;

	private AbstractTask taskScheduledLastWeek;

	private AbstractTask taskCompleteAndOverdue;

	private TaskList taskList;

	@BeforeEach
	protected void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();

		view = (TaskListView) TasksUiUtil.openTasksViewInActivePerspective();
		assertNotNull(view);
		previousFilters = view.getFilters();
		view.clearFilters();

		taskList = TasksUiPlugin.getTaskList();

		taskCompleted = new LocalTask("1", "completed");
		taskCompleted.setCompletionDate(TaskActivityUtil.snapForwardNumDays(Calendar.getInstance(), -1).getTime());
		taskList.addTask(taskCompleted);

		taskIncomplete = new LocalTask("2", "t-incomplete");
		taskList.addTask(taskIncomplete);

		taskOverdue = new LocalTask("3", "t-overdue");
		taskOverdue.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday().previous());
		taskList.addTask(taskOverdue);

		taskDueToday = new LocalTask("4", "t-today");
		taskDueToday.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday());
		taskList.addTask(taskDueToday);

		taskCompletedToday = new LocalTask("5", "t-donetoday");
		taskCompletedToday.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday());
		taskCompletedToday.setCompletionDate(new Date());
		taskList.addTask(taskCompletedToday);

		taskScheduledLastWeek = new LocalTask("6", "t-scheduledLastWeek");
		taskList.addTask(taskScheduledLastWeek);
		TasksUiPlugin.getTaskActivityManager()
		.setScheduledFor(taskScheduledLastWeek, TaskActivityUtil.getCurrentWeek().previous());

		taskCompleteAndOverdue = new LocalTask("7", "t-completeandoverdue");
		taskList.addTask(taskCompleteAndOverdue);
		Calendar cal = TaskActivityUtil.getCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		TasksUiPlugin.getTaskActivityManager().setDueDate(taskCompleteAndOverdue, cal.getTime());
		taskCompleteAndOverdue.setCompletionDate(cal.getTime());
	}

	@AfterEach
	protected void tearDown() throws Exception {
		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(new HashSet<>(0));
		view.clearFilters();
		for (AbstractTaskListFilter filter : previousFilters) {
			view.addFilter(filter);
		}
	}

	@Test
	public void testSearchScheduledWorkingSet() throws InterruptedException {
		TaskCategory category = new TaskCategory("category");
		taskList.addCategory(category);
		taskList.addTask(taskOverdue, category);
		taskList.addTask(taskIncomplete, category);
		view.getViewer().refresh();
		view.getViewer().expandAll();
		List<Object> items = UiTestUtil.getAllData(view.getViewer().getTree());
		assertTrue(items.contains(taskCompleted));
		assertTrue(items.contains(taskOverdue));
		IWorkingSetManager workingSetManager = Workbench.getInstance().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.createWorkingSet("Task Working Set", new IAdaptable[] { category });
		workingSet.setId(TaskWorkingSetUpdater.ID_TASK_WORKING_SET);
		assertTrue(Arrays.asList(workingSet.getElements()).contains(category));
		workingSetManager.addWorkingSet(workingSet);

		TaskWorkingSetFilter workingSetFilter = new TaskWorkingSetFilter();
		view.addFilter(workingSetFilter);
		HashSet<IWorkingSet> workingSets = new HashSet<>(1);
		workingSets.add(workingSet);
		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(workingSets);
		view.getFilteredTree().setFilterText("over");
		view.getFilteredTree().getRefreshPolicy().internalForceRefresh();

		items = UiTestUtil.getAllData(view.getViewer().getTree());
		assertFalse(items.contains(taskCompleted));
		assertTrue(items.contains(taskOverdue));
		workingSets = new HashSet<>(0);
		view.removeFilter(workingSetFilter);
		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(workingSets);
		taskList.removeFromContainer(category, taskOverdue);
		taskList.removeFromContainer(category, taskIncomplete);
		view.getFilteredTree().setFilterText("");
		view.getFilteredTree().getRefreshPolicy().internalForceRefresh();
	}

	@Test
	public void testInterestFilter() {
		TaskListInterestFilter interestFilter = new TaskListInterestFilter();
		view.addFilter(interestFilter);
		view.getViewer().refresh();
		view.getViewer().expandAll();
		List<Object> items = UiTestUtil.getAllData(view.getViewer().getTree());
		assertFalse(items.contains(taskCompleted));
		assertFalse(items.contains(taskIncomplete));
		assertTrue(items.contains(taskOverdue));
		assertTrue(items.contains(taskDueToday));
		assertTrue(items.contains(taskCompletedToday));
		assertTrue(items.contains(taskScheduledLastWeek));
		assertFalse(items.contains(taskCompleteAndOverdue));
		view.removeFilter(interestFilter);
	}

	@Test
	public void testNoFilters() {
		assertEquals(2, view.getFilters().size(),
				"should have working set filter and orphan/archive filter: " + view.getFilters());
		view.getViewer().refresh();

		assertEquals(1, view.getViewer().getTree().getItemCount(),
				"should only have Uncategorized folder present in stock task list: "
						+ view.getViewer().getTree().getItems());
	}
}
