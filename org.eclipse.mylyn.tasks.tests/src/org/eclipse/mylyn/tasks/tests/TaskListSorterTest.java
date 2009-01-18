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
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListInterestSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListSorter;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.swt.widgets.Control;

/**
 * @author Mik Kersten
 * @author George Lindholm
 * @author Frank Becker
 */
public class TaskListSorterTest extends TestCase {

	public void testRootTaskSorting() {
		TaskListSorter sorter = new TaskListSorter();
		sorter.getComparator().setSortByIndex(TaskComparator.SortByIndex.SUMMARY);

		AbstractTask task = new LocalTask("1", "");
		UncategorizedTaskContainer uncategorizedTaskContainer = new UncategorizedTaskContainer();
		UnsubmittedTaskContainer unsubmittedTaskContainer = new UnsubmittedTaskContainer("connectorKind",
				"repositoryUrl");
		TaskCategory category = new TaskCategory("cat");
		RepositoryQuery repositoryQuery = new RepositoryQuery("connectorKind", "queryName");
		TaskGroup taskGroup = new TaskGroup("parentHandle", "summary", "groupBy");
		UnmatchedTaskContainer unmatchedTaskContainer = new UnmatchedTaskContainer("connectorKind", "repositoryUrl");

		checkToRootElements(sorter, uncategorizedTaskContainer, unsubmittedTaskContainer);
		checkToRootElements(sorter, uncategorizedTaskContainer, category);
		checkToRootElements(sorter, uncategorizedTaskContainer, repositoryQuery);
		checkToRootElements(sorter, uncategorizedTaskContainer, taskGroup);
		checkToRootElements(sorter, uncategorizedTaskContainer, unmatchedTaskContainer);

		checkToRootElements(sorter, unsubmittedTaskContainer, category);
		checkToRootElements(sorter, unsubmittedTaskContainer, repositoryQuery);
		checkToRootElements(sorter, unsubmittedTaskContainer, taskGroup);
		checkToRootElements(sorter, unsubmittedTaskContainer, unmatchedTaskContainer);

		checkToRootElements(sorter, category, repositoryQuery);
		checkToRootElements(sorter, category, taskGroup);
		checkToRootElements(sorter, category, unmatchedTaskContainer);

		checkToRootElements(sorter, repositoryQuery, taskGroup);
		checkToRootElements(sorter, repositoryQuery, unmatchedTaskContainer);

		checkToRootElements(sorter, taskGroup, unmatchedTaskContainer);

		checkToRootElements(sorter, task, uncategorizedTaskContainer);
		checkToRootElements(sorter, task, unsubmittedTaskContainer);
		checkToRootElements(sorter, task, category);
		checkToRootElements(sorter, task, repositoryQuery);
		checkToRootElements(sorter, task, taskGroup);
		checkToRootElements(sorter, task, unmatchedTaskContainer);
	}

	private void checkToRootElements(TaskListSorter sorter, AbstractTaskContainer e1, AbstractTaskContainer e2) {
		assertEquals(-1, sorter.compare(null, e1, e2));
		assertEquals(1, sorter.compare(null, e2, e1));
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
		MockTask[] tasks = new MockTask[6];
		tasks[0] = new MockTask("local", "4", "c");
		tasks[1] = new MockTask("local", "1", "b");
		tasks[2] = new MockTask("local", "11", "a");
		tasks[3] = new MockTask("local", "11", "d");
		tasks[4] = new MockTask("local", "3", "c");
		tasks[5] = new MockTask("local", "5", "a");
		Date start = new Date();
		tasks[5].setCreationDate(start);
		tasks[4].setCreationDate(new Date(start.getTime() - 1));
		tasks[3].setCreationDate(new Date(start.getTime() - 2));
		tasks[2].setCreationDate(new Date(start.getTime() - 3));
		tasks[1].setCreationDate(new Date(start.getTime() - 4));
		tasks[0].setCreationDate(new Date(start.getTime() - 5));

		TaskListSorter sorter = new TaskListSorter();
		sorter.getComparator().setSortByIndex(TaskComparator.SortByIndex.SUMMARY);
		sorter.getComparator().setSortByIndex2(TaskComparator.SortByIndex.DATE_CREATED);
		sorter.sort(new EmptyViewer(), tasks);

		assertEquals("11", tasks[0].getTaskKey());
		assertEquals("a", tasks[0].getSummary());
		assertEquals("5", tasks[1].getTaskKey());
		assertEquals("1", tasks[2].getTaskKey());
		assertEquals("4", tasks[3].getTaskKey());
		assertEquals("3", tasks[4].getTaskKey());
		assertEquals("11", tasks[5].getTaskKey());
		assertEquals("d", tasks[5].getSummary());
	}

	public void testModuleSummaryOrderSorting() {
		MockTask[] tasks = new MockTask[5];
		tasks[0] = new MockTask("local", "MYLN:4", "c");
		tasks[1] = new MockTask("local", "MYLN:1", "b");
		tasks[2] = new MockTask("local", "MYLN:11", "a");
		tasks[3] = new MockTask("local", "MYLN:11", "b");
		tasks[4] = new MockTask("local", "MYLN:5", "a");
		Date start = new Date();
		tasks[4].setCreationDate(start);
		tasks[3].setCreationDate(new Date(start.getTime() - 1));
		tasks[2].setCreationDate(new Date(start.getTime() - 2));
		tasks[1].setCreationDate(new Date(start.getTime() - 3));
		tasks[0].setCreationDate(new Date(start.getTime() - 4));

		TaskListSorter sorter = new TaskListSorter();
		sorter.getComparator().setSortByIndex(TaskComparator.SortByIndex.SUMMARY);
		sorter.getComparator().setSortByIndex2(TaskComparator.SortByIndex.DATE_CREATED);
		sorter.sort(new EmptyViewer(), tasks);

		assertEquals("MYLN:11", tasks[0].getTaskKey());
		assertEquals("a", tasks[0].getSummary());
		assertEquals("MYLN:5", tasks[1].getTaskKey());
		assertEquals("MYLN:1", tasks[2].getTaskKey());
		assertEquals("MYLN:11", tasks[3].getTaskKey());
		assertEquals("b", tasks[3].getSummary());
		assertEquals("MYLN:4", tasks[4].getTaskKey());
	}

	public void testLocalTaskSort() {
		final TaskListSorter sorter = new TaskListSorter();
		ITask task1 = new LocalTask("1", "task1");
		ITask task2 = new LocalTask("2", "task2");
		ITask task3 = new LocalTask("3", "task3");
		ITask[] tasks = { task1, task2, task3 };
		Date start = new Date();
		task3.setCreationDate(start);
		task2.setCreationDate(new Date(start.getTime() - 1));
		task1.setCreationDate(new Date(start.getTime() - 2));
		sorter.sort(new EmptyViewer(), tasks);
	}

}
