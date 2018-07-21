/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Date;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListInterestSorter;
import org.eclipse.swt.widgets.Control;

import junit.framework.TestCase;

public class TaskListInterestSorterTest extends TestCase {

	private TaskListInterestSorter sorter;

	private AbstractTask task1;

	private AbstractTask task2;

	public static class EmptyViewer extends Viewer {
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

	public class FallbackSorter extends ViewerSorter {
		@Override
		public int compare(Viewer v, Object o1, Object o2) {
			if (o1 == task1) {
				return 1;
			}
			return -1;
		}
	}

	@Override
	public void setUp() {
		sorter = new TaskListInterestSorter();
		task1 = new LocalTask("1", "one");
		task2 = new LocalTask("2", "two");
	}

	public void testSortDefault() {
		assertEquals(0, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(0, sorter.compare(new EmptyViewer(), task2, task1));
	}

	public void testSortFallback() {
		sorter.setconfiguredSorter(new FallbackSorter());

		assertEquals(1, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(-1, sorter.compare(new EmptyViewer(), task2, task1));
	}

	public void testSortCompleted() {
		Date now = new Date();

		task1.setCompletionDate(now);

		assertEquals(1, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(-1, sorter.compare(new EmptyViewer(), task2, task1));

		task2.setCompletionDate(now);

		assertEquals(0, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(0, sorter.compare(new EmptyViewer(), task2, task1));
	}

	public void testSortScheduled() {
		DateRange today = TaskActivityUtil.getCurrentWeek().getToday();
		DateRange tomorrow = TaskActivityUtil.getCurrentWeek().getToday().next();
		DateRange yesterday = TaskActivityUtil.getCurrentWeek().getToday().previous();

		task1.setScheduledForDate(today);

		assertEquals(-1, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(1, sorter.compare(new EmptyViewer(), task2, task1));

		task2.setScheduledForDate(tomorrow);

		assertEquals(-1, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(1, sorter.compare(new EmptyViewer(), task2, task1));

		task2.setScheduledForDate(yesterday);

		assertEquals(0, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(0, sorter.compare(new EmptyViewer(), task2, task1));
	}

	public void testSortDue() {
		DateRange today = TaskActivityUtil.getCurrentWeek().getToday();
		DateRange tomorrow = TaskActivityUtil.getCurrentWeek().getToday().next();
		DateRange yesterday = TaskActivityUtil.getCurrentWeek().getToday().previous();

		task1.setDueDate(new Date(today.getStartDate().getTimeInMillis()));

		assertEquals(-1, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(1, sorter.compare(new EmptyViewer(), task2, task1));

		task2.setDueDate(new Date(tomorrow.getStartDate().getTimeInMillis()));

		assertEquals(-1, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(1, sorter.compare(new EmptyViewer(), task2, task1));

		task2.setDueDate(new Date(yesterday.getStartDate().getTimeInMillis()));

		assertEquals(0, sorter.compare(new EmptyViewer(), task1, task2));
		assertEquals(0, sorter.compare(new EmptyViewer(), task2, task1));
	}

}
