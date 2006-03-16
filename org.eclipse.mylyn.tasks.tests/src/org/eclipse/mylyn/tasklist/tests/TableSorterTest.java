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

package org.eclipse.mylar.tasklist.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.tasklist.ui.views.TaskListTableSorter;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.mylar.provisional.tasklist.TaskList;

/**
 * @author Mik Kersten
 */
public class TableSorterTest extends TestCase {

	public void testRootTaskSorting() {
		TaskListTableSorter sorter = new TaskListTableSorter(null, "description");
		 		
		Task task = new Task("1", "", true);
		TaskCategory category = new TaskCategory("cat", new TaskList());
		
		assertEquals(-1, sorter.compare(null, task, category));
		assertEquals(1, sorter.compare(null, category, task));
	}
}
