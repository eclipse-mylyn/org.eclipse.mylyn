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
/*
 * Created on Dec 21, 2004
 */
package org.eclipse.mylar.tasklist.tests;

import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTaskExternalizer;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.Task;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskList;
import org.eclipse.mylar.internal.tasklist.TaskListManager;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;

/**
 * @author Mik Kersten
 */
public class TaskListManagerTest extends TestCase {

	private TaskListManager manager;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getTaskListManager();
		manager.createNewTaskList();

		repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				new URL(IBugzillaConstants.ECLIPSE_BUGZILLA_URL));
		MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.createNewTaskList();
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
		MylarTaskListPlugin.getRepositoryManager().removeRepository(repository);
	}

	public void testRepositoryUrlHandles() {

		String repository = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
		String id = "123";
		String handle = TaskRepositoryManager.getHandle(repository, id);
		BugzillaTask bugTask = new BugzillaTask(handle, "label 124", true);
		assertEquals(repository, bugTask.getRepositoryUrl());

		manager.moveToRoot(bugTask);

		manager.saveTaskList();
		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		BugzillaTask readReport = (BugzillaTask) manager.getTaskList().getRootTasks().get(0);
		assertEquals(readReport.getDescription(), readReport.getDescription());
		assertEquals(readReport.getRepositoryUrl(), readReport.getRepositoryUrl());
	}

	public void testMoves() {
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		Task task1 = new Task("t1", "t1", true);
		manager.moveToRoot(task1);
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		assertNull(task1.getCategory());

		TaskCategory cat1 = new TaskCategory("c1");
		manager.addCategory(cat1);

		manager.moveToCategory(cat1, task1);
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		assertEquals(cat1, task1.getCategory());

		manager.moveToRoot(task1);
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		assertEquals(0, cat1.getChildren().size());
		assertNull(task1.getCategory());
	}

	public void testPlans() {
		// File file = new File("foo" + MylarTaskListPlugin.FILE_EXTENSION);
		// file.deleteOnExit();
		// TaskListManager manager = new TaskListManager(file);

		Task task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
		task1.addPlan("default");
		manager.moveToRoot(task1);

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();
		assertNotNull(manager.getTaskList());

		List<ITask> readList = manager.getTaskList().getRootTasks();
		ITask task = readList.get(0);
		assertEquals(1, task.getPlans().size());
		assertTrue(task.getPlans().get(0).equals("default"));
	}

	public void testQueryExternalization() {
		AbstractRepositoryQuery query = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1");
		assertEquals("repositoryUrl", query.getRepositoryUrl());
		assertEquals("queryUrl", query.getQueryUrl());
		manager.addQuery(query);
		manager.saveTaskList();
		assertNotNull(manager.getTaskList());

		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getQueries().size());
		AbstractRepositoryQuery readQuery = manager.getTaskList().getQueries().get(0);
		assertEquals(query.getQueryUrl(), readQuery.getQueryUrl());
		assertEquals(query.getRepositoryUrl(), readQuery.getRepositoryUrl());
		assertEquals("repositoryUrl", readQuery.getRepositoryUrl());
	}

	public void testCreationAndExternalization() {
		Task task1 = new Task(manager.genUniqueTaskHandle(), "task 1", true);
		manager.moveToRoot(task1);
		Task sub1 = new Task(manager.genUniqueTaskHandle(), "sub 1", true);
		task1.addSubTask(sub1);
		sub1.setParent(task1);
		Task task2 = new Task(manager.genUniqueTaskHandle(), "task 2", true);
		manager.moveToRoot(task2);

		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.addCategory(cat1);
		Task task3 = new Task(manager.genUniqueTaskHandle(), "task 3", true);
		manager.moveToCategory(cat1, task3);
		assertEquals(cat1, task3.getCategory());
		Task sub2 = new Task(manager.genUniqueTaskHandle(), "sub 2", true);
		task3.addSubTask(sub2);
		sub2.setParent(task3);
		Task task4 = new Task(manager.genUniqueTaskHandle(), "task 4", true);
		manager.moveToCategory(cat1, task4);

		TaskCategory cat2 = new TaskCategory("Category 2");
		manager.addCategory(cat2);
		Task task5 = new Task(manager.genUniqueTaskHandle(), "task 5", true);
		manager.moveToCategory(cat2, task5);
		Task task6 = new Task(manager.genUniqueTaskHandle(), "task 6", true);
		manager.moveToCategory(cat2, task6);

		BugzillaTask report = new BugzillaTask("123", "label 123", true);
		manager.moveToCategory(cat2, report);
		assertEquals(cat2, report.getCategory());

		BugzillaTask report2 = new BugzillaTask("124", "label 124", true);
		manager.moveToRoot(report2);

		assertEquals(5, manager.getTaskList().getRoots().size());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		assertNotNull(manager.getTaskList());
		assertEquals(3, manager.getTaskList().getRootTasks().size()); // contains
																		// archived
																		// reports
																		// category

		List<ITask> readList = manager.getTaskList().getRootTasks();
		assertTrue(readList.get(0).getDescription().equals("task 1"));
		assertTrue(readList.get(0).getChildren().get(0).getDescription().equals("sub 1"));
		assertTrue(readList.get(1).getDescription().equals("task 2"));
		assertTrue(readList.get(2) instanceof BugzillaTask);
		BugzillaTask readReport = (BugzillaTask) readList.get(2);
		assertEquals(report2.getDescription(), readReport.getDescription());

		List<TaskCategory> readCats = manager.getTaskList().getTaskCategories();
		assertTrue(readCats.get(0).getDescription().equals(BugzillaTaskExternalizer.BUGZILLA_ARCHIVE_LABEL));

		assertEquals(3, manager.getTaskList().getCategories().size());

		TaskCategory readCat = readCats.get(1);
		readList = readCat.getChildren();
		assertTrue(readList.get(0).getDescription().equals("task 3"));
		assertEquals(readCat, readList.get(0).getCategory());
		assertTrue(readList.get(0).getChildren().get(0).getDescription().equals("sub 2"));
		assertTrue(readList.get(1).getDescription().equals("task 4"));

		TaskCategory readCat2 = readCats.get(2);
		readList = readCat2.getChildren();
		assertTrue(readList.get(0).getDescription().equals("task 5"));
		assertTrue(readList.get(1).getDescription().equals("task 6"));
		assertTrue(readList.get(2) instanceof BugzillaTask);
		assertEquals(readCat2, readList.get(2).getCategory());
	}

}
