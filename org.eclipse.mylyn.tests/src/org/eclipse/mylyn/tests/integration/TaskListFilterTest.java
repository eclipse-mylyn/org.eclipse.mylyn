/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.context.tests.UiTestUtil;
import org.eclipse.mylyn.internal.context.ui.TaskListInterestFilter;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskWorkingSetFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class TaskListFilterTest extends TestCase {

	private TaskListView view;

	private final TaskListManager manager = TasksUiPlugin.getTaskListManager();

	private Set<AbstractTaskListFilter> previousFilters;

	private AbstractTask taskCompleted;

	private AbstractTask taskIncomplete;

	private AbstractTask taskOverdue;

	private AbstractTask taskDueToday;

	private AbstractTask taskCompletedToday;

	private AbstractTask taskScheduledLastWeek;

	private AbstractTask taskCompleteAndOverdue;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		view = TaskListView.openInActivePerspective();
		assertNotNull(view);
		previousFilters = view.getFilters();
		view.clearFilters(false);

		manager.getTaskList().reset();
		assertEquals(0, manager.getTaskList().getAllTasks().size());

		taskCompleted = new LocalTask("1", "completed");
		taskCompleted.setCompleted(true);
		taskCompleted.setCompletionDate(TaskActivityUtil.snapForwardNumDays(Calendar.getInstance(), -1).getTime());
		manager.getTaskList().addTask(taskCompleted);

		taskIncomplete = new LocalTask("2", "t-incomplete");
		manager.getTaskList().addTask(taskIncomplete);

		taskOverdue = new LocalTask("3", "t-overdue");
		taskOverdue.setScheduledForDate(TaskActivityUtil.snapForwardNumDays(Calendar.getInstance(), -1).getTime());
		manager.getTaskList().addTask(taskOverdue);

		taskDueToday = new LocalTask("4", "t-today");
		taskDueToday.setScheduledForDate(TaskActivityUtil.snapEndOfWorkDay(Calendar.getInstance()).getTime());
		manager.getTaskList().addTask(taskDueToday);

		taskCompletedToday = new LocalTask("5", "t-donetoday");
		taskCompletedToday.setScheduledForDate(TaskActivityUtil.snapEndOfWorkDay(Calendar.getInstance()).getTime());
		taskCompletedToday.setCompleted(true);
		manager.getTaskList().addTask(taskCompletedToday);

		taskScheduledLastWeek = new LocalTask("6", "t-scheduledLastWeek");
		manager.getTaskList().addTask(taskScheduledLastWeek);
		Calendar lastWeek = Calendar.getInstance();
		lastWeek.add(Calendar.WEEK_OF_MONTH, -1);
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(taskScheduledLastWeek, lastWeek.getTime(), true);

		taskCompleteAndOverdue = new LocalTask("7", "t-completeandoverdue");
		manager.getTaskList().addTask(taskCompleteAndOverdue);
		Calendar cal = TaskActivityUtil.getCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		TasksUiPlugin.getTaskActivityManager().setDueDate(taskCompleteAndOverdue, cal.getTime());
		taskCompleteAndOverdue.setCompleted(true);
		taskCompleteAndOverdue.setCompletionDate(cal.getTime());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		view.clearFilters(true);
		for (AbstractTaskListFilter filter : previousFilters) {
			view.addFilter(filter);
		}
	}

	public void testSearchScheduledWorkingSet() throws InterruptedException {
		TaskCategory category = new TaskCategory("category");
		manager.getTaskList().addCategory(category);
		manager.getTaskList().moveTask(taskOverdue, category);
		manager.getTaskList().moveTask(taskIncomplete, category);
		view.getViewer().refresh();
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
		IWorkingSet[] workingSets = { workingSet };
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setWorkingSets(workingSets);
		view.getFilteredTree().setFilterText("over");
		view.getFilteredTree().getRefreshPolicy().runRefreshNow();

		items = UiTestUtil.getAllData(view.getViewer().getTree());
		assertFalse(items.contains(taskCompleted));
		assertTrue(items.contains(taskOverdue));
		workingSets = new IWorkingSet[0];
		view.removeFilter(workingSetFilter);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setWorkingSets(workingSets);
	}

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

	public void testNoFilters() {
		assertEquals("should have working set filter and orphan/archive filter: " + view.getFilters(), 2,
				view.getFilters().size());
		view.getViewer().refresh();

		assertEquals("should only have Uncategorized folder present in stock task list: "
				+ view.getViewer().getTree().getItems(), 1, view.getViewer().getTree().getItemCount());
	}
}
