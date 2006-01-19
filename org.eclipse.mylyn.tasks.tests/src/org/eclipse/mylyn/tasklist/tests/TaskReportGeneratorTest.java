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

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.TaskListManager;
import org.eclipse.mylar.tasklist.internal.planner.CompletedTaskCollector;
import org.eclipse.mylar.tasklist.internal.planner.TaskReportGenerator;

/**
 * @author Mik Kersten
 */
public class TaskReportGeneratorTest extends TestCase {

	private TaskListManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getTaskListManager();
		manager.createNewTaskList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.createNewTaskList();
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	public void testCompletedTasksRetrieved() throws InvocationTargetException, InterruptedException {
		Task task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
		manager.moveToRoot(task1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator genertor = new TaskReportGenerator(manager.getTaskList());
		genertor.addCollector(collector);
		genertor.run(new NullProgressMonitor());
		assertEquals(0, collector.getTasks().size());

		task1.setCompleted(true);
		genertor.run(new NullProgressMonitor());
		assertEquals(1, collector.getTasks().size());
		assertEquals(task1, collector.getTasks().get(0));
	}

}
