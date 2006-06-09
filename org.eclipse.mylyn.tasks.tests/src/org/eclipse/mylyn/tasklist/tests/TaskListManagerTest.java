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

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import org.eclipse.mylar.internal.tasklist.ScheduledTaskListSynchJob;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.tasklist.tests.mockconnector.MockRepositoryQuery;
import org.eclipse.mylar.tasklist.tests.mockconnector.MockRepositoryTask;

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
		manager.resetTaskList();

		repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();		
		manager.resetTaskList();
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
		MylarTaskListPlugin.getRepositoryManager().removeRepository(repository);
	}
	
	public void testSingleTaskDeletion() {
		MockRepositoryTask task = new MockRepositoryTask("mock-1");
		manager.getTaskList().moveToRoot(task);
		manager.saveTaskList();

		manager.resetTaskList();
		manager.readExistingOrCreateNewList(); 
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		
		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		
		manager.resetTaskList();
		manager.readExistingOrCreateNewList(); 
		assertEquals(0, manager.getTaskList().getAllTasks().size());	
	}
	
	public void testMigrateTaskContextFiles() throws IOException {
		File fileA = MylarPlugin.getContextManager().getFileForContext("http://a-1");
		fileA.createNewFile();
		fileA.deleteOnExit();
		assertTrue(fileA.exists());
		manager.refactorRepositoryUrl("http://a", "http://b");
		File fileB = MylarPlugin.getContextManager().getFileForContext("http://b-1");
		assertTrue(fileB.exists());
		assertFalse(fileA.exists());
	}
	
	public void testMigrateQueryHandles() {
		AbstractRepositoryQuery query = new MockRepositoryQuery("mquery", manager.getTaskList());
		query.setRepositoryUrl("http://a");
		manager.getTaskList().addQuery(query);
		manager.refactorRepositoryUrl("http://a", "http://b");
		assertFalse(manager.getTaskList().getRepositoryQueries("http://b").isEmpty());
		assertTrue(manager.getTaskList().getRepositoryQueries("http://a").isEmpty());
	}
	
	public void testMigrateTaskHandles() {
		AbstractRepositoryTask task = new MockRepositoryTask("http://a-123");
		manager.getTaskList().addTask(task);
		manager.refactorRepositoryUrl("http://a", "http://b");
		assertNull(manager.getTaskList().getTask("http://a-123"));
		assertNotNull(manager.getTaskList().getTask("http://b-123"));
	}
	
	public void testIsActiveToday() {
		ITask task = new Task("1", "task-1", true);
		assertFalse(manager.isReminderToday(task));
		
		task.setReminderDate(new Date());
		assertFalse(manager.isReminderToday(task));
		
		task.setReminded(true);
		assertFalse(manager.isReminderToday(task));
		task.setReminded(true);
		
		Calendar inAnHour = Calendar.getInstance();
		inAnHour.set(Calendar.HOUR_OF_DAY, inAnHour.get(Calendar.HOUR_OF_DAY)+1);
		inAnHour.getTime();
		task.setReminderDate(inAnHour.getTime());
		Calendar tomorrow = Calendar.getInstance();
		manager.setSecheduledIn(tomorrow, 1);
		assertEquals(-1, inAnHour.compareTo(tomorrow));
		
		assertTrue(manager.isReminderToday(task)); 
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

		Collection<ITask> allTasks = manager.getTaskList().getAllTasks();
		Set<ITask> allRootTasks = manager.getTaskList().getRootTasks();
		Set<AbstractTaskContainer> allCategories = manager.getTaskList().getCategories();
		Set<ITaskListElement> allRoots = manager.getTaskList().getRootElements();
		assertEquals(0, allRootTasks.size());

		manager.saveTaskList();
//		manager.getTaskList().clear();
		manager.resetTaskList();
//		TaskList list = new TaskList();
//		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		assertEquals(allRootTasks.size(), manager.getTaskList().getRootTasks().size());
		assertEquals(allCategories, manager.getTaskList().getCategories());
		assertEquals(allRoots.size(), manager.getTaskList().getRootElements().size());
		assertEquals(allTasks.size(), manager.getTaskList().getAllTasks().size());

		// rewrite and test again
		manager.saveTaskList();
//		manager.getTaskList().clear();
		manager.resetTaskList();
//		list = new TaskList();
//		manager.setTaskList(list);
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

		manager.getTaskList().moveToRoot(bugTask);
		manager.saveTaskList();
//		manager.getTaskList().clear();
		manager.resetTaskList();
//		TaskList list = new TaskList();
//		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		BugzillaTask readReport = (BugzillaTask) manager.getTaskList().getRootTasks().iterator().next();
		assertEquals(readReport.getDescription(), readReport.getDescription());
		assertEquals(readReport.getRepositoryUrl(), readReport.getRepositoryUrl());
	}

	public void testDeleteQuery() {
		AbstractRepositoryQuery query = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1", manager.getTaskList());
		manager.getTaskList().addQuery(query);

		AbstractRepositoryQuery readQuery = manager.getTaskList().getQueries().iterator().next();
		assertEquals(query, readQuery);

		manager.getTaskList().deleteQuery(query);
		assertEquals(0, manager.getTaskList().getQueries().size());
	}
	
	public void testDeleteQueryAfterRename() {
		AbstractRepositoryQuery query = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1", manager.getTaskList());
		manager.getTaskList().addQuery(query);

		AbstractRepositoryQuery readQuery = manager.getTaskList().getQueries().iterator().next();
		assertEquals(query, readQuery);
		manager.getTaskList().renameContainer(query, "newName");
		manager.getTaskList().deleteQuery(query);
		assertEquals(0, manager.getTaskList().getQueries().size());
	}
	
	public void testCreateQueryWithSameName() {
		AbstractRepositoryQuery query = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1", manager.getTaskList());
		manager.getTaskList().addQuery(query);
		assertEquals(1, manager.getTaskList().getQueries().size());
		AbstractRepositoryQuery readQuery = manager.getTaskList().getQueries().iterator().next();
		assertEquals(query, readQuery);
		
		manager.getTaskList().addQuery(new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1", manager.getTaskList()));
		assertEquals(1, manager.getTaskList().getQueries().size());
	}
	
	public void testMoveCategories() {
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		Task task1 = new Task("t1", "t1", true);
		
		TaskCategory cat1 = new TaskCategory("cat1", manager.getTaskList());
		manager.getTaskList().addCategory(cat1);
		TaskCategory cat2 = new TaskCategory("cat2", manager.getTaskList());
		manager.getTaskList().addCategory(cat2);
		
		manager.getTaskList().moveToContainer(cat1, task1);
		assertEquals(1, manager.getTaskList().getContainerForHandle("cat1").getChildren().size());
		assertEquals(0, manager.getTaskList().getContainerForHandle("cat2").getChildren().size());

		manager.getTaskList().moveToContainer(cat2, task1);
		assertEquals(0, manager.getTaskList().getContainerForHandle("cat1").getChildren().size());
		assertEquals(1, manager.getTaskList().getContainerForHandle("cat2").getChildren().size());				
	}
	
	public void testMoveToRoot() {
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		Task task1 = new Task("t1", "t1", true);
		manager.getTaskList().moveToRoot(task1);
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		assertEquals(TaskList.LABEL_ROOT, task1.getContainer().getHandleIdentifier());

		TaskCategory cat1 = new TaskCategory("c1", manager.getTaskList());
		manager.getTaskList().addCategory(cat1);

		manager.getTaskList().moveToContainer(cat1, task1);
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		assertEquals(cat1, task1.getContainer());

		manager.getTaskList().moveToRoot(task1);
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		assertEquals(0, cat1.getChildren().size());
		assertEquals(TaskList.LABEL_ROOT, task1.getContainer().getHandleIdentifier());
	}

	public void testEmpty() {
		manager.resetTaskList();
		assertTrue(manager.getTaskList().isEmpty());
		manager.getTaskList().internalAddRootTask(new Task("", "", true));
		assertFalse(manager.getTaskList().isEmpty());
	}

	public void testCategoryPersistance() {
		BugzillaTask task = new BugzillaTask("b1", "b 1", true);
		TaskCategory category = new TaskCategory("cat", manager.getTaskList());
		manager.getTaskList().addCategory(category);
		manager.getTaskList().moveToContainer(category, task);
		assertNotNull(manager.getTaskList());
		assertEquals(2, manager.getTaskList().getCategories().size());

		manager.saveTaskList();
		manager.resetTaskList();
		manager.readExistingOrCreateNewList(); 
		assertEquals(""+manager.getTaskList().getCategories(), 2, manager.getTaskList().getCategories().size());
		assertEquals(1, manager.getTaskList().getAllTasks().size());
	}
	
	public void testDeleteCategory() {
		assertNotNull(manager.getTaskList());
		assertEquals(1, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat", manager.getTaskList());
		manager.getTaskList().addCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		manager.getTaskList().deleteCategory(category);		
		assertEquals(1, manager.getTaskList().getCategories().size());
	}
	
	public void testDeleteCategoryMovesTasksToRoot() {
		ITask task = new MockRepositoryTask("mock-delete");
		TaskCategory category = new TaskCategory("mock-cat", manager.getTaskList());
		manager.getTaskList().addTask(task, category);
		manager.getTaskList().addCategory(category);
		assertEquals(0, manager.getTaskList().getRootCategory().getChildren().size());
		manager.getTaskList().deleteCategory(category);
		assertEquals(1, manager.getTaskList().getRootCategory().getChildren().size());
	}
	
	public void testRenameCategory() {

		assertNotNull(manager.getTaskList());
		
		TaskCategory category = new TaskCategory("cat", manager.getTaskList());
		manager.getTaskList().addCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		String newDesc = "newDescription";
		manager.getTaskList().renameContainer(category, newDesc);
		AbstractTaskContainer container = manager.getTaskList().getContainerForHandle(newDesc);
		assertNotNull(container);
		assertEquals(newDesc, container.getDescription());
		manager.getTaskList().deleteCategory(container);
		assertEquals(1, manager.getTaskList().getCategories().size());
	}
	
	public void testDeleteCategoryAfterRename() {
		String newDesc = "newDescription";
		assertNotNull(manager.getTaskList());
		assertEquals(1, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat", manager.getTaskList());
		manager.getTaskList().addCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		manager.getTaskList().renameContainer(category, newDesc);
		manager.getTaskList().deleteCategory(category);		
		assertEquals(1, manager.getTaskList().getCategories().size());
	}
	
	public void testCreateSameCategoryName() {
		assertNotNull(manager.getTaskList());
		assertEquals(1, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat", manager.getTaskList());
		manager.getTaskList().addCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		TaskCategory category2 = new TaskCategory("cat", manager.getTaskList());
		manager.getTaskList().addCategory(category2);
		assertEquals(2, manager.getTaskList().getCategories().size());
		AbstractTaskContainer container = manager.getTaskList().getContainerForHandle("cat");
		assertEquals(container, category);
	}

	public void testDelete() {
		ITask task = new Task("handle", "label", true);
		manager.getTaskList().addTask(task);
		manager.getTaskList().internalAddRootTask(task);
		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	public void testBugzillaCustomQueryExternalization() {
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1", manager.getTaskList());
		query.setCustomQuery(true);
		manager.getTaskList().addQuery(query);
		manager.saveTaskList();

		manager.resetTaskList();
//		manager.getTaskList().clear();
//		TaskList list = new TaskList();
//		manager.setTaskList(list);
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getQueries().size());
		BugzillaRepositoryQuery readQuery = (BugzillaRepositoryQuery) manager.getTaskList().getQueries().iterator().next();
		assertTrue(readQuery.isCustomQuery());
	}

	public void testQueryExternalization() {
		AbstractRepositoryQuery query = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl", "label", "1", manager.getTaskList());
		long time = 1234;
		Date oldDate = new Date(time);	
		query.setLastRefresh(oldDate);
		assertEquals("repositoryUrl", query.getRepositoryUrl());
		assertEquals("queryUrl", query.getQueryUrl());
		assertEquals(time, query.getLastSynchronized().getTime());
		manager.getTaskList().addQuery(query);
		manager.saveTaskList();
		assertNotNull(manager.getTaskList());

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getQueries().size());
		AbstractRepositoryQuery readQuery = manager.getTaskList().getQueries().iterator().next();
		assertEquals(query.getQueryUrl(), readQuery.getQueryUrl());
		assertEquals(query.getRepositoryUrl(), readQuery.getRepositoryUrl());
		assertEquals("repositoryUrl", readQuery.getRepositoryUrl());
		assertEquals(time, readQuery.getLastSynchronized().getTime());
	}

	public void testArchiveRepositoryTaskExternalization() {
		BugzillaTask repositoryTask = new BugzillaTask("handle", "label", true);
		repositoryTask.setKind("kind");
		manager.getTaskList().addTask(repositoryTask);
		assertEquals(1, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		manager.saveTaskList();

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(0, manager.getTaskList().getRootTasks().size());
	} 

	public void testRepositoryTaskExternalization() {
		BugzillaTask repositoryTask = new BugzillaTask("handle", "label", true);
		repositoryTask.setKind("kind");
		manager.getTaskList().moveToRoot(repositoryTask);
		manager.saveTaskList();

		manager.resetTaskList();
//		manager.getTaskList().clear();
//		TaskList list = new TaskList();
//		manager.setTaskList(list);
		manager.readExistingOrCreateNewList(); 
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		AbstractRepositoryTask readTask = (AbstractRepositoryTask) manager.getTaskList().getRootTasks().iterator()
				.next();

		assertEquals(repositoryTask.getHandleIdentifier(), readTask.getHandleIdentifier());
		assertEquals(repositoryTask.getDescription(), readTask.getDescription());
		assertEquals(repositoryTask.getTaskType(), readTask.getTaskType());
	}

	public void testRepositoryTasksAndCategoriesMultiRead() {
		TaskCategory cat1 = new TaskCategory("Category 1", manager.getTaskList());
		manager.getTaskList().addCategory(cat1);

		BugzillaTask reportInCat1 = new BugzillaTask("123", "label 123", true);
		manager.getTaskList().moveToContainer(cat1, reportInCat1);
		assertEquals(cat1, reportInCat1.getContainer());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
//		manager.getTaskList().clear();
//		manager.setTaskList(new TaskList());
		manager.readExistingOrCreateNewList();

		// read once
		Set<AbstractTaskContainer> readCats = manager.getTaskList().getTaskContainers();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));
		Iterator<AbstractTaskContainer> iterator = readCats.iterator();
		AbstractTaskContainer readCat1 = iterator.next();
		assertEquals(cat1, readCat1);
		assertEquals(1, readCat1.getChildren().size());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
//		manager.getTaskList().clear();
//		manager.setTaskList(new TaskList());
		manager.readExistingOrCreateNewList();

		// read again
		readCats = manager.getTaskList().getTaskContainers();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));

		iterator = readCats.iterator();
		readCat1 = iterator.next();
		assertEquals(cat1, readCat1);
		assertEquals(1, readCat1.getChildren().size());
	}

	public void testCreationAndExternalization() {
		Set<ITask> rootTasks = new HashSet<ITask>();
		Task task1 = new Task(manager.genUniqueTaskHandle(), "task 1", true);
		manager.getTaskList().moveToRoot(task1);
		rootTasks.add(task1);
		Task sub1 = new Task(manager.genUniqueTaskHandle(), "sub 1", true);
		task1.addSubTask(sub1);
		sub1.setParent(task1);
		Task task2 = new Task(manager.genUniqueTaskHandle(), "task 2", true);
		manager.getTaskList().moveToRoot(task2);
		rootTasks.add(task2);

		Set<TaskCategory> categories = new HashSet<TaskCategory>();
		Set<ITask> cat1Contents = new HashSet<ITask>();
		TaskCategory cat1 = new TaskCategory("Category 1", manager.getTaskList());
		manager.getTaskList().addCategory(cat1);
		categories.add(cat1);
		Task task3 = new Task(manager.genUniqueTaskHandle(), "task 3", true);
		manager.getTaskList().moveToContainer(cat1, task3);
		cat1Contents.add(task3);
		assertEquals(cat1, task3.getContainer());
		Task sub2 = new Task(manager.genUniqueTaskHandle(), "sub 2", true);
		task3.addSubTask(sub2);
		sub2.setParent(task3);
		Task task4 = new Task(manager.genUniqueTaskHandle(), "task 4", true);
		manager.getTaskList().moveToContainer(cat1, task4);
		cat1Contents.add(task4);

		BugzillaTask reportInCat1 = new BugzillaTask("123", "label 123", true);
		manager.getTaskList().moveToContainer(cat1, reportInCat1);
		assertEquals(cat1, reportInCat1.getContainer());
		cat1Contents.add(reportInCat1);

		BugzillaTask reportInRoot = new BugzillaTask("124", "label 124", true);
		manager.getTaskList().moveToRoot(reportInRoot);
		rootTasks.add(reportInRoot);

		assertEquals("" + manager.getTaskList().getRootElements(), 5, manager.getTaskList().getRootElements().size());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
//		manager.getTaskList().clear();
//		TaskList list = new TaskList();
//		manager.setTaskList(list);
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

		Set<AbstractTaskContainer> readCats = manager.getTaskList().getTaskContainers();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));
		Iterator<AbstractTaskContainer> iterator = readCats.iterator();
		AbstractTaskContainer readCat1 = iterator.next();
		assertEquals(cat1, readCat1);
		assertEquals(cat1Contents, readCat1.getChildren());
	}

	public void testScheduledRefreshJob() throws InterruptedException {
		int counter = 3;
		ScheduledTaskListSynchJob job = new ScheduledTaskListSynchJob(10, manager);
		job.run(new NullProgressMonitor());
//		job.schedule();
		Thread.sleep(2000);
		assertTrue(job.getCount() + " smaller than " + counter, job.getCount() >= counter);
		job.cancel();
	}

	public void testgetQueriesAndHitsForHandle() {

		BugzillaQueryHit hit1 = new BugzillaQueryHit("description1", "P1", "repositoryURL", 1, null, "status");
		BugzillaQueryHit hit2 = new BugzillaQueryHit("description2", "P1", "repositoryURL", 2, null, "status");
		BugzillaQueryHit hit3 = new BugzillaQueryHit("description3", "P1", "repositoryURL", 3, null, "status");

		BugzillaQueryHit hit1twin = new BugzillaQueryHit("description1", "P1", "repositoryURL", 1, null, "status");
		BugzillaQueryHit hit2twin = new BugzillaQueryHit("description2", "P1", "repositoryURL", 2, null, "status");
		BugzillaQueryHit hit3twin = new BugzillaQueryHit("description3", "P1", "repositoryURL", 3, null, "status");

		BugzillaRepositoryQuery query1 = new BugzillaRepositoryQuery("url","url", "queryl", "10", manager.getTaskList());

		BugzillaRepositoryQuery query2 = new BugzillaRepositoryQuery("url2", "url2", "query2", "10", manager.getTaskList());
		

		query1.addHit(hit1);
		query1.addHit(hit2);
		query1.addHit(hit3);
		assertEquals(query1.getHits().size(), 3);

		query2.addHit(hit1twin);
		query2.addHit(hit2twin);
		query2.addHit(hit3twin);
		assertEquals(query2.getHits().size(), 3);

		manager.getTaskList().addQuery(query1);
		manager.getTaskList().addQuery(query2);

		TaskList taskList = manager.getTaskList();
		Set<AbstractRepositoryQuery> queriesReturned = taskList.getQueriesForHandle(AbstractRepositoryTask.getHandle(
				"repositoryURL", 1));
		assertNotNull(queriesReturned);
		assertEquals(2, queriesReturned.size()); 
		assertTrue(queriesReturned.contains(query1));
		assertTrue(queriesReturned.contains(query2));

		Set<AbstractQueryHit> hitsReturned = taskList.getQueryHitsForHandle(AbstractRepositoryTask.getHandle(
				"repositoryURL", 2));
		assertNotNull(hitsReturned);
		assertEquals(2, hitsReturned.size());
		assertTrue(hitsReturned.contains(hit2));
		assertTrue(hitsReturned.contains(hit2twin));

	}
		
	public void testgetRepositoryTasks() {

		String repositoryUrl = "https://bugs.eclipse.org/bugs";
		
		String bugNumber = "106939";
		
		BugzillaTask task1 = new BugzillaTask(repositoryUrl+"-"+bugNumber, "label", false);		
		manager.getTaskList().addTask(task1);
		
		BugzillaTask task2 = new BugzillaTask("https://unresolved-"+bugNumber, "label", false);		
		manager.getTaskList().addTask(task2);
		
		TaskList taskList = manager.getTaskList();
		assertEquals(2, taskList.getAllTasks().size());
		Set<AbstractRepositoryTask> tasksReturned = taskList.getRepositoryTasks(repositoryUrl);
		assertNotNull(tasksReturned);
		assertEquals(1, tasksReturned.size()); 
		assertTrue(tasksReturned.contains(task1));
	}
	
	
}