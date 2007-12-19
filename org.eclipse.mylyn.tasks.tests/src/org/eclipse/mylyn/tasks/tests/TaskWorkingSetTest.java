/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.internal.Workbench;

public class TaskWorkingSetTest extends TestCase {

	public void testRenameQuery() {
		MockRepositoryQuery query = new MockRepositoryQuery("description");
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		taskList.addQuery(query);

		IWorkingSetManager workingSetManager = Workbench.getInstance().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.createWorkingSet(TaskWorkingSetUpdater.ID_TASK_WORKING_SET, new IAdaptable[] { query });
		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));
		
		TasksUiPlugin.getTaskListManager().getTaskList().deleteQuery(query);
		query = new MockRepositoryQuery("newDescription");
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));
	}

	public void testEditQuery() {
		MockRepositoryQuery query = new MockRepositoryQuery("description");
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		taskList.addQuery(query);

		IWorkingSetManager workingSetManager = Workbench.getInstance().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.createWorkingSet(TaskWorkingSetUpdater.ID_TASK_WORKING_SET, new IAdaptable[] { query });
		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));
		
		TasksUiPlugin.getTaskListManager().getTaskList().deleteQuery(query);
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));
	}

}
