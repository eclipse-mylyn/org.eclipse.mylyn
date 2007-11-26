/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListTableSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryTask;
import org.eclipse.swt.widgets.Control;

/**
 * @author Mik Kersten
 * @author George Lindholm
 */
public class TableSorterTest extends TestCase {

	public void testRootTaskSorting() {
		TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective(),
				TaskListTableSorter.SortByIndex.SUMMARY);

		AbstractTask task = new LocalTask("1", "");
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

	public void testSummaryOrderSorting() {
		final TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective());

		final MockRepositoryTask[] tasks = new MockRepositoryTask[5];
		tasks[0] = new MockRepositoryTask("local", "4", "c");
		tasks[1] = new MockRepositoryTask("local", "1", "b");
		tasks[2] = new MockRepositoryTask("local", "11", "a");
		tasks[3] = new MockRepositoryTask("local", "3", "c");
		tasks[4] = new MockRepositoryTask("local", "5", "a");

		sorter.sort(new EmptyViewer(), tasks);

		assertTrue("1".equals(tasks[0].getTaskKey()) && "b".equals(tasks[0].getSummary()));
		assertTrue("3".equals(tasks[1].getTaskKey()) && "c".equals(tasks[1].getSummary()));
		assertTrue("11".equals(tasks[4].getTaskKey()) && "a".equals(tasks[4].getSummary()));
	}

	public void testModuleSummaryOrderSorting() {
		final TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective());

		final MockRepositoryTask[] tasks = new MockRepositoryTask[5];
		tasks[0] = new MockRepositoryTask("local", "MYLN:4", "c");
		tasks[1] = new MockRepositoryTask("local", "MYLN:1", "b");
		tasks[2] = new MockRepositoryTask("local", "MYLN:11", "a");
		tasks[3] = new MockRepositoryTask("local", "MYLN:11", "b");
		tasks[4] = new MockRepositoryTask("local", "MYLN:5", "a");

		sorter.sort(new EmptyViewer(), tasks);

		assertTrue("MYLN:1".equals(tasks[0].getTaskKey()) && "b".equals(tasks[0].getSummary()));
		assertTrue("MYLN:4".equals(tasks[1].getTaskKey()) && "c".equals(tasks[1].getSummary()));
		assertTrue("MYLN:11".equals(tasks[4].getTaskKey()) && "b".equals(tasks[4].getSummary()));
	}

	public void testLocalTaskSort() {
		final TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective());
		AbstractTask task1 = new LocalTask("1", "task1");
		AbstractTask task2 = new LocalTask("2", "task2");
		AbstractTask task3 = new LocalTask("3", "task3");
		AbstractTask[] tasks = { task1, task2, task3 };
		sorter.sort(new EmptyViewer(), tasks);
	}

}
