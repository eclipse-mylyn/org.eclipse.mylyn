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
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.planner.CompletedTaskCollector;
import org.eclipse.mylar.internal.tasklist.planner.TaskReportGenerator;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;

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
		manager.resetTaskList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.resetTaskList();
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	public void testCompletedTasksRetrieved() throws InvocationTargetException, InterruptedException {
		Task task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
		manager.getTaskList().moveToRoot(task1);

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
	
	public void testCompletedTasksDateBoundsRetrieved() throws InvocationTargetException, InterruptedException {
		Task task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
		manager.getTaskList().moveToRoot(task1);
		task1.setCompleted(true);
		Thread.sleep(1000);
		long now = new Date().getTime();
		
		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(now));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList());
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		generator = new TaskReportGenerator(manager.getTaskList());
		collector = new CompletedTaskCollector(new Date(now - 8000));
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}
	
	public void testCompletedBugzillaTasksRetrieved() throws InvocationTargetException, InterruptedException {
		BugzillaTask task1 = new BugzillaTask(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(),
				"bugzillatask 1", true);
		manager.getTaskList().moveToRoot(task1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList());
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		TaskTestUtil.setBugTaskCompleted(task1, true);
		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedTasksInCategoryRetrieved() throws InvocationTargetException, InterruptedException {
		Task task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
		manager.getTaskList().moveToRoot(task1);
		task1.setCompleted(true);
		TaskCategory cat1 = new TaskCategory("TaskReportGeneratorTest Category", manager.getTaskList());
		manager.getTaskList().addCategory(cat1);

		Set<ITaskListElement> catagories = new HashSet<ITaskListElement>();
		catagories.add(cat1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList(), catagories);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		manager.getTaskList().moveToContainer(cat1, task1);

		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedBugzillaTasksInCategoryRetrieved() throws InvocationTargetException, InterruptedException {
		BugzillaTask task1 = new BugzillaTask(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1",
				true);
		manager.getTaskList().moveToRoot(task1);
		TaskTestUtil.setBugTaskCompleted(task1, true);
		TaskCategory cat1 = new TaskCategory("TaskReportGeneratorTest Category", manager.getTaskList());
		manager.getTaskList().addCategory(cat1);

		Set<ITaskListElement> catagories = new HashSet<ITaskListElement>();
		catagories.add(cat1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList(), catagories);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		manager.getTaskList().moveToContainer(cat1, task1);

		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedBugzillaTasksInQueryRetrieved() throws InvocationTargetException, InterruptedException {
		BugzillaTask task1 = new BugzillaTask(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1",
				true);
		manager.getTaskList().moveToRoot(task1);
		TaskTestUtil.setBugTaskCompleted(task1, false);

		BugzillaRepositoryQuery bugQuery = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl",
				"TaskReportGeneratorBugzillaQueryCategory", "maxHits", manager.getTaskList());

		manager.getTaskList().addQuery(bugQuery);

		Set<ITaskListElement> catagories = new HashSet<ITaskListElement>();
		catagories.add(bugQuery);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0));
		TaskReportGenerator generator = new TaskReportGenerator(manager.getTaskList(), catagories);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		bugQuery.addHit(new BugzillaQueryHit("task1description", "low", "repositoryURL", 1, task1, "FIXED"));

		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		TaskTestUtil.setBugTaskCompleted(task1, true);

		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}
}
