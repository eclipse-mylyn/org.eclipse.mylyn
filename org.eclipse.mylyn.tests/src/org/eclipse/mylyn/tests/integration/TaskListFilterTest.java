/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.tests.UiTestUtil;
import org.eclipse.mylyn.internal.context.ui.TaskListInterestFilter;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class TaskListFilterTest extends TestCase {

	private TaskListView view;

	private TaskListManager manager = TasksUiPlugin.getTaskListManager();

	private Set<AbstractTaskListFilter> previousFilters;

	private AbstractTask taskCompleted;

	private AbstractTask taskIncomplete;

	private AbstractTask taskOverdue;

	private AbstractTask taskDueToday;

	private AbstractTask taskCompletedToday;

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
		manager.getTaskList().addTask(taskCompleted, manager.getTaskList().getDefaultCategory());

		taskIncomplete = new LocalTask("2", "t-incomplete");
		manager.getTaskList().addTask(taskIncomplete, manager.getTaskList().getDefaultCategory());

		taskOverdue = new LocalTask("3", "t-overdue");
		taskOverdue.setScheduledForDate(TaskActivityUtil.snapForwardNumDays(Calendar.getInstance(), -1).getTime());
		manager.getTaskList().addTask(taskOverdue, manager.getTaskList().getDefaultCategory());

		taskDueToday = new LocalTask("4", "t-today");
		taskDueToday.setScheduledForDate(TaskActivityUtil.snapEndOfWorkDay(Calendar.getInstance()).getTime());
		manager.getTaskList().addTask(taskDueToday, manager.getTaskList().getDefaultCategory());

		taskCompletedToday = new LocalTask("5", "t-donetoday");
		taskCompletedToday.setScheduledForDate(TaskActivityUtil.snapEndOfWorkDay(Calendar.getInstance()).getTime());
		taskCompletedToday.setCompleted(true);
		manager.getTaskList().addTask(taskCompletedToday, manager.getTaskList().getDefaultCategory());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		view.clearFilters(true);
		for (AbstractTaskListFilter filter : previousFilters) {
			view.addFilter(filter);
		}
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
		view.removeFilter(interestFilter);
	}

	public void testNoFilters() {
		assertEquals("should have working set filter: " + view.getFilters(), 1, view.getFilters().size());
		view.getViewer().refresh();
		assertEquals(2, view.getViewer().getTree().getItemCount());
	}
}
