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
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaQueryCategory;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.internal.TaskListManager;
import org.eclipse.mylar.tasklist.internal.planner.CompletedTaskCollector;
import org.eclipse.mylar.tasklist.internal.planner.TaskReportGenerator;

/**
 * @author Mik Kersten
 * @author Rob Elves
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
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList());
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		task1.setCompleted(true);
		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedBugzillaTasksRetrieved() throws InvocationTargetException, InterruptedException {
		BugzillaTask task1 = new BugzillaTask(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(),
				"bugzillatask 1", true, true);
		manager.moveToRoot(task1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList());
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		task1.setCompleted(true);
		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedTasksInCategoryRetrieved() throws InvocationTargetException, InterruptedException {
		Task task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
		manager.moveToRoot(task1);
		task1.setCompleted(true);
		TaskCategory cat1 = new TaskCategory("TaskReportGeneratorTest Category");
		manager.addCategory(cat1);

		ArrayList<Object> catagories = new ArrayList<Object>();
		catagories.add(cat1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList(), catagories);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		manager.moveToCategory(cat1, task1);

		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedBugzillaTasksInCategoryRetrieved() throws InvocationTargetException, InterruptedException {
		BugzillaTask task1 = new BugzillaTask(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1",
				true, true);
		manager.moveToRoot(task1);
		task1.setCompleted(true);
		TaskCategory cat1 = new TaskCategory("TaskReportGeneratorTest Category");
		manager.addCategory(cat1);

		ArrayList<Object> catagories = new ArrayList<Object>();
		catagories.add(cat1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList(), catagories);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		manager.moveToCategory(cat1, task1);

		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedBugzillaTasksInQueryRetrieved() throws InvocationTargetException, InterruptedException {
		BugzillaTask task1 = new BugzillaTask(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1",
				true, true);
		manager.moveToRoot(task1);
		task1.setCompleted(false);
		
		BugzillaQueryCategory bugQuery = new BugzillaQueryCategory("repositoryUrl", "queryUrl",
				"TaskReportGeneratorBugzillaQueryCategory", "maxHits");

		manager.addQuery(bugQuery);

		ArrayList<Object> catagories = new ArrayList<Object>();
		catagories.add(bugQuery);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList(), catagories);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		bugQuery.addHit(new BugzillaQueryHit("task1description", "low", "repositoryURL", 1, task1, "FIXED"));

		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());
		
		task1.setCompleted(true);
		
		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}
}
