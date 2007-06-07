/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.tasks.ui.views.TaskListTableSorter;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskCategory;

/**
 * @author Mik Kersten
 */
public class TableSorterTest extends TestCase {

	public void testRootTaskSorting() {
		TaskListTableSorter sorter = new TaskListTableSorter(TaskListView.getFromActivePerspective(), TaskListTableSorter.SortByIndex.SUMMARY);
		 		
		Task task = new Task("1", "");
		TaskCategory category = new TaskCategory("cat");
		
		assertEquals(-1, sorter.compare(null, task, category));
		assertEquals(1, sorter.compare(null, category, task));
	}
}
