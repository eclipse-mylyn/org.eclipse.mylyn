/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     George Lindholm - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Calendar;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListInterestSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListTableSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.swt.widgets.Control;

/**
 * @author Mik Kersten
 * @author George Lindholm
 */
public class TableSorterTest extends TestCase {

	public void testRootTaskSorting() {
		TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective(),
				TaskListTableSorter.SortByIndex.SUMMARY);

		ITask task = new LocalTask("1", "");
		TaskCategory category = new TaskCategory("cat");

		assertEquals(-1, sorter.compare(null, task, category));
		assertEquals(1, sorter.compare(null, category, task));
	}

	public class EmptyViewer extends Viewer {
		public EmptyViewer() {
		}

		@Override
		public Control getControl() {
			return null;
		}

		@Override
		public Object getInput() {
			return null;
		}

		@Override
		public ISelection getSelection() {
			return null;
		}

		@Override
		public void refresh() {
		}

		@Override
		public void setInput(Object input) {
		}

		@Override
		public void setSelection(ISelection selection, boolean reveal) {
		}
	}

	public void testScheduledTaskSorting() {
		final TaskListInterestSorter sorter = new TaskListInterestSorter();
		MockTask task1 = new MockTask("local", "MYLN:1", "1");
		MockTask task2 = new MockTask("local", "MYLN:2", "2");

		Calendar start1 = TaskActivityUtil.getCalendar();
		start1.add(Calendar.WEEK_OF_YEAR, -1);
		TaskActivityUtil.snapStartOfWorkWeek(start1);
		Calendar end1 = TaskActivityUtil.getCalendar();
		end1.setTimeInMillis(start1.getTimeInMillis());
		TaskActivityUtil.snapEndOfWeek(end1);
		WeekDateRange range1 = new WeekDateRange(start1, end1);
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(task1, range1);

		Calendar start2 = TaskActivityUtil.getCalendar();
		start2.add(Calendar.HOUR_OF_DAY, -1);
		Calendar end2 = TaskActivityUtil.getCalendar();
		DateRange range2 = new DateRange(start2, end2);
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(task2, range2);

		assertTrue(sorter.compare(new EmptyViewer(), task1, task2) < 0);
	}

	public void testSummaryOrderSorting() {
		final TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective());

		final MockTask[] tasks = new MockTask[5];
		tasks[0] = new MockTask("local", "4", "c");
		tasks[1] = new MockTask("local", "1", "b");
		tasks[2] = new MockTask("local", "11", "a");
		tasks[3] = new MockTask("local", "3", "c");
		tasks[4] = new MockTask("local", "5", "a");

		sorter.sort(new EmptyViewer(), tasks);

		assertTrue("1".equals(tasks[0].getTaskKey()) && "b".equals(tasks[0].getSummary()));
		assertTrue("3".equals(tasks[1].getTaskKey()) && "c".equals(tasks[1].getSummary()));
		assertTrue("11".equals(tasks[4].getTaskKey()) && "a".equals(tasks[4].getSummary()));
	}

	public void testModuleSummaryOrderSorting() {
		final TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective());

		final MockTask[] tasks = new MockTask[5];
		tasks[0] = new MockTask("local", "MYLN:4", "c");
		tasks[1] = new MockTask("local", "MYLN:1", "b");
		tasks[2] = new MockTask("local", "MYLN:11", "a");
		tasks[3] = new MockTask("local", "MYLN:11", "b");
		tasks[4] = new MockTask("local", "MYLN:5", "a");

		sorter.sort(new EmptyViewer(), tasks);

		assertTrue("MYLN:1".equals(tasks[0].getTaskKey()) && "b".equals(tasks[0].getSummary()));
		assertTrue("MYLN:4".equals(tasks[1].getTaskKey()) && "c".equals(tasks[1].getSummary()));
		assertTrue("MYLN:11".equals(tasks[4].getTaskKey()) && "b".equals(tasks[4].getSummary()));
	}

	public void testLocalTaskSort() {
		final TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective());
		ITask task1 = new LocalTask("1", "task1");
		ITask task2 = new LocalTask("2", "task2");
		ITask task3 = new LocalTask("3", "task3");
		ITask[] tasks = { task1, task2, task3 };
		sorter.sort(new EmptyViewer(), tasks);
	}

}
