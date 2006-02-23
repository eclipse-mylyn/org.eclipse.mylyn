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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.ScheduledTaskListRefreshJob;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

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

	public void testLegacyTaskListReading() throws IOException {
		File originalFile = manager.getTaskListFile();
		File legacyListFile = new File("temptasklist.xml");
		legacyListFile.deleteOnExit();
		TaskTestUtil.copy(TaskTestUtil.getLocalFile("testdata/legacy/tasklist_0_4_8.xml"), legacyListFile);

		assertEquals(362445, legacyListFile.length());
		assertTrue(legacyListFile.exists());

		manager.setTaskListFile(legacyListFile);
		manager.readExistingOrCreateNewList();
		manager.setTaskListFile(originalFile);

		Set<ITask> allTasks = manager.getTaskList().getAllTasks();
		Set<ITask> allRootTasks = manager.getTaskList().getRootTasks();
		Set<ITaskContainer> allCategories = manager.getTaskList().getCategories();
		Set<ITaskListElement> allRoots = manager.getTaskList().getRootElements();
		assertEquals(0, allRootTasks.size());

		manager.saveTaskList();
		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		assertEquals(allRootTasks.size(), manager.getTaskList().getRootTasks().size());
		assertEquals(allCategories, manager.getTaskList().getCategories());
		assertEquals(allRoots.size(), manager.getTaskList().getRootElements().size());
		assertEquals(allTasks.size(), manager.getTaskList().getAllTasks().size());

		// rewrite and test again
		manager.saveTaskList();
		list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		assertEquals(allRootTasks.size(), manager.getTaskList().getRootTasks().size());
		assertEquals(allCategories, manager.getTaskList().getCategories());
		assertEquals(allRoots.size(), manager.getTaskList().getRootElements().size());
		assertEquals(allTasks.size(), manager.getTaskList().getAllTasks().size());

		manager.deactivateTask(manager.getTaskList().getActiveTask());
	}

	public void testRepositoryUrlHandles() {

		String repository = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
		String id = "123";
		String handle = AbstractRepositoryTask.getHandle(repository, id);
		BugzillaTask bugTask = new BugzillaTask(handle, "label 124", true);
		assertEquals(repository, bugTask.getRepositoryUrl());

		manager.moveToRoot(bugTask);

		manager.saveTaskList();
		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		BugzillaTask readReport = (BugzillaTask) manager.getTaskList().getRootTasks().iterator().next();
		assertEquals(readReport.getDescription(), readReport.getDescription());
		assertEquals(readReport.getRepositoryUrl(), readReport.getRepositoryUrl());
	}

	public void testMoves() {
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		Task task1 = new Task("t1", "t1", true);
		manager.moveToRoot(task1);
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		assertEquals(TaskList.LABEL_ROOT, task1.getCategory().getHandleIdentifier());

		TaskCategory cat1 = new TaskCategory("c1");
		manager.addCategory(cat1);

		manager.moveToCategory(cat1, task1);
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		assertEquals(cat1, task1.getCategory());

		manager.moveToRoot(task1);
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		assertEquals(0, cat1.getChildren().size());
		assertEquals(TaskList.LABEL_ROOT, task1.getCategory().getHandleIdentifier());
	}

	public void testPlans() {
		Task task1 = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), "task 1", true);
		task1.addPlan("default");
		manager.moveToRoot(task1);

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();
		assertNotNull(manager.getTaskList());

		Set<ITask> readList = manager.getTaskList().getRootTasks();
		ITask task = readList.iterator().next();
		assertEquals(1, task.getPlans().size());
		assertTrue(task.getPlans().get(0).equals("default"));
	}

	public void testEmpty() {
		TaskList list = new TaskList();
		assertTrue(list.isEmpty());
		list.internalAddRootTask(new Task("", "", true));
		assertFalse(list.isEmpty());
	}

	public void testCategories() {
		BugzillaTask task = new BugzillaTask("b1", "b 1", true);
		TaskCategory category = new TaskCategory("cat");
		manager.addCategory(category);
		manager.moveToCategory(category, task);
		assertNotNull(manager.getTaskList());

		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();
		assertEquals(2, manager.getTaskList().getCategories().size());
		assertEquals(1, manager.getTaskList().getAllTasks().size());
	}

	public void testDelete() {
		ITask task = new Task("handle", "label", true);
		manager.getTaskList().addTaskToArchive(task);
		manager.getTaskList().internalAddRootTask(task);
		manager.deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	public void testBugzillaCustomQueryExternalization() {
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1");
		query.setCustomQuery(true);
		manager.addQuery(query);
		manager.saveTaskList();
		assertNotNull(manager.getTaskList());

		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getQueries().size());
		BugzillaRepositoryQuery readQuery = (BugzillaRepositoryQuery) manager.getTaskList().getQueries().get(0);
		assertTrue(readQuery.isCustomQuery());
	}

	public void testQueryExternalization() {
		AbstractRepositoryQuery query = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1");
		assertEquals("repositoryUrl", query.getRepositoryUrl());
		assertEquals("queryUrl", query.getQueryUrl());
		manager.addQuery(query);
		manager.saveTaskList();
		assertNotNull(manager.getTaskList());

		manager.setTaskList(new TaskList());
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getQueries().size());
		AbstractRepositoryQuery readQuery = manager.getTaskList().getQueries().get(0);
		assertEquals(query.getQueryUrl(), readQuery.getQueryUrl());
		assertEquals(query.getRepositoryUrl(), readQuery.getRepositoryUrl());
		assertEquals("repositoryUrl", readQuery.getRepositoryUrl());
	}

	public void testArchiveRepositoryTaskExternalization() {
		BugzillaTask repositoryTask = new BugzillaTask("handle", "label", true);
		repositoryTask.setKind("kind");
		manager.getTaskList().addTaskToArchive(repositoryTask);
		// repositoryTask.setCategory(manager.getTaskList().getArchiveCategory());
		assertEquals(1, manager.getTaskList().getArchiveTasks().size());
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		manager.saveTaskList();

		manager.setTaskList(new TaskList());
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getArchiveTasks().size());
		assertEquals(0, manager.getTaskList().getRootTasks().size());
	}

	public void testRepositoryTaskExternalization() {
		BugzillaTask repositoryTask = new BugzillaTask("handle", "label", true);
		repositoryTask.setKind("kind");
		manager.moveToRoot(repositoryTask);
		manager.saveTaskList();

		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		AbstractRepositoryTask readTask = (AbstractRepositoryTask) manager.getTaskList().getRootTasks().iterator()
				.next();

		assertEquals(repositoryTask.getHandleIdentifier(), readTask.getHandleIdentifier());
		assertEquals(repositoryTask.getDescription(), readTask.getDescription());
		assertEquals(repositoryTask.getKind(), readTask.getKind());
	}

	public void testRepositoryTasksAndCategoriesMultiRead() {
		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.addCategory(cat1);

		BugzillaTask reportInCat1 = new BugzillaTask("123", "label 123", true);
		manager.moveToCategory(cat1, reportInCat1);
		assertEquals(cat1, reportInCat1.getCategory());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.setTaskList(new TaskList());
		manager.readExistingOrCreateNewList();

		// read once
		Set<TaskCategory> readCats = manager.getTaskList().getTaskCategories();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));
		Iterator<TaskCategory> iterator = readCats.iterator();
		TaskCategory readCat1 = iterator.next();
		assertEquals(cat1, readCat1);
		assertEquals(1, readCat1.getChildren().size());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.setTaskList(new TaskList());
		manager.readExistingOrCreateNewList();

		// read again
		readCats = manager.getTaskList().getTaskCategories();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));

		iterator = readCats.iterator();
		readCat1 = iterator.next();
		assertEquals(cat1, readCat1);
		assertEquals(1, readCat1.getChildren().size());
	}

	public void testCreationAndExternalization() {
		Set<ITask> rootTasks = new HashSet<ITask>();
		Task task1 = new Task(manager.genUniqueTaskHandle(), "task 1", true);
		manager.moveToRoot(task1);
		rootTasks.add(task1);
		Task sub1 = new Task(manager.genUniqueTaskHandle(), "sub 1", true);
		task1.addSubTask(sub1);
		sub1.setParent(task1);
		Task task2 = new Task(manager.genUniqueTaskHandle(), "task 2", true);
		manager.moveToRoot(task2);
		rootTasks.add(task2);

		Set<TaskCategory> categories = new HashSet<TaskCategory>();
		Set<ITask> cat1Contents = new HashSet<ITask>();
		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.addCategory(cat1);
		categories.add(cat1);
		Task task3 = new Task(manager.genUniqueTaskHandle(), "task 3", true);
		manager.moveToCategory(cat1, task3);
		cat1Contents.add(task3);
		assertEquals(cat1, task3.getCategory());
		Task sub2 = new Task(manager.genUniqueTaskHandle(), "sub 2", true);
		task3.addSubTask(sub2);
		sub2.setParent(task3);
		Task task4 = new Task(manager.genUniqueTaskHandle(), "task 4", true);
		manager.moveToCategory(cat1, task4);
		cat1Contents.add(task4);

		BugzillaTask reportInCat1 = new BugzillaTask("123", "label 123", true);
		manager.moveToCategory(cat1, reportInCat1);
		assertEquals(cat1, reportInCat1.getCategory());
		cat1Contents.add(reportInCat1);

		BugzillaTask reportInRoot = new BugzillaTask("124", "label 124", true);
		manager.moveToRoot(reportInRoot);
		rootTasks.add(reportInRoot);

		assertEquals("" + manager.getTaskList().getRootElements(), 5, manager.getTaskList().getRootElements().size());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		TaskList list = new TaskList();
		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		assertNotNull(manager.getTaskList());
		assertEquals(rootTasks, manager.getTaskList().getRootTasks());

		Set<ITask> readList = manager.getTaskList().getRootTasks();
		for (ITask task : readList) {
			if (task.equals(task1)) {
				assertEquals(task1.getDescription(), task.getDescription());
				assertEquals(1, task.getChildren().size());
			}
			if (task.equals(reportInRoot)) {
				assertEquals(reportInRoot.getDescription(), task.getDescription());
			}
		}

		Set<TaskCategory> readCats = manager.getTaskList().getTaskCategories();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));
		Iterator<TaskCategory> iterator = readCats.iterator();
		TaskCategory readCat1 = iterator.next();
		assertEquals(cat1, readCat1);
		assertEquals(cat1Contents, readCat1.getChildren());
	}

	public void testScheduledRefreshJob() throws InterruptedException {
		int counter = 3;
		ScheduledTaskListRefreshJob job = new ScheduledTaskListRefreshJob(500, manager);
		job.run(new NullProgressMonitor());
		Thread.sleep(1500);
		assertEquals(counter, job.getCount());
	}

	public void testgetQueriesAndHitsForHandle() {

		BugzillaQueryHit hit1 = new BugzillaQueryHit("description1", "P1", "repositoryURL", 1, null, "status");
		BugzillaQueryHit hit2 = new BugzillaQueryHit("description2", "P1", "repositoryURL", 2, null, "status");
		BugzillaQueryHit hit3 = new BugzillaQueryHit("description3", "P1", "repositoryURL", 3, null, "status");

		BugzillaQueryHit hit1twin = new BugzillaQueryHit("description1", "P1", "repositoryURL", 1, null, "status");
		BugzillaQueryHit hit2twin = new BugzillaQueryHit("description2", "P1", "repositoryURL", 2, null, "status");
		BugzillaQueryHit hit3twin = new BugzillaQueryHit("description3", "P1", "repositoryURL", 3, null, "status");

		AbstractRepositoryQuery query1 = new AbstractRepositoryQuery() {
			@Override
			public String getRepositoryKind() {
				// ignore
				return "newkind";
			}
		};

		AbstractRepositoryQuery query2 = new AbstractRepositoryQuery() {
			@Override
			public String getRepositoryKind() {
				// ignore
				return "newkind";
			}
		};

		query1.addHit(hit1);
		query1.addHit(hit2);
		query1.addHit(hit3);
		assertEquals(query1.getHits().size(), 3);

		query2.addHit(hit1twin);
		query2.addHit(hit2twin);
		query2.addHit(hit3twin);
		assertEquals(query2.getHits().size(), 3);

		manager.addQuery(query1);
		manager.addQuery(query2);

		TaskList taskList = manager.getTaskList();
		Set<AbstractRepositoryQuery> queriesReturned = taskList.getQueriesForHandle(AbstractRepositoryTask.getHandle(
				"repositoryURL", 1));
		assertNotNull(queriesReturned);
		assertEquals(queriesReturned.size(), 2);
		assertTrue(queriesReturned.contains(query1));
		assertTrue(queriesReturned.contains(query2));

		Set<AbstractQueryHit> hitsReturned = taskList.getQueryHitsForHandle(AbstractRepositoryTask.getHandle(
				"repositoryURL", 2));
		assertNotNull(hitsReturned);
		assertEquals(hitsReturned.size(), 2);
		assertTrue(hitsReturned.contains(hit2));
		assertTrue(hitsReturned.contains(hit2twin));

	}

}
