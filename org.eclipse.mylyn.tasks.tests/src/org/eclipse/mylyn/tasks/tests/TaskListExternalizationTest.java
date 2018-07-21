/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

import junit.framework.TestCase;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 * @author Mike Wu
 */
public class TaskListExternalizationTest extends TestCase {

	private TaskList taskList;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);

		TaskTestUtil.resetTaskListAndRepositories();

		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		taskList = TasksUiPlugin.getTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskList();
	}

	public void testTaskAttributes() throws Exception {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		int initialAttributeCount = task1.getAttributes().size();
		task1.setAttribute("key", "value");
		assertEquals(initialAttributeCount + 1, task1.getAttributes().size());

		TaskTestUtil.saveAndReadTasklist();

		task1 = taskList.getTask(task1.getHandleIdentifier());
		assertNotNull(task1);
		assertEquals(initialAttributeCount + 1, task1.getAttributes().size());
		assertEquals("value", task1.getAttribute("key"));
	}

	public void testTaskAttributeDelete() throws Exception {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		int initialAttributeCount = task1.getAttributes().size();
		task1.setAttribute("key", "value");
		task1.setAttribute("key", null);
		assertEquals(initialAttributeCount, task1.getAttributes().size());
		assertEquals(null, task1.getAttribute("key"));

		TaskTestUtil.saveAndReadTasklist();

		task1 = taskList.getTask(task1.getHandleIdentifier());
		assertNotNull(task1);
		assertEquals(initialAttributeCount, task1.getAttributes().size());
		assertEquals(null, task1.getAttribute("key"));
	}

	public void testUncategorizedTasksNotLost() throws Exception {
		RepositoryQuery query = TaskTestUtil.createMockQuery("1");
		taskList.addQuery(query);
		TaskTask task = TaskTestUtil.createMockTask("1");
		taskList.addTask(task, query);
		taskList.addTask(task, taskList.getDefaultCategory());
		assertTrue(query.contains(task.getHandleIdentifier()));
		assertTrue(taskList.getDefaultCategory().contains(task.getHandleIdentifier()));

		TaskTestUtil.saveAndReadTasklist();

		assertTrue(taskList.getDefaultCategory().contains(task.getHandleIdentifier()));
	}

	public void testUniqueTaskId() throws Exception {
		LocalTask task1 = TasksUiInternal.createNewLocalTask("label");
		taskList.addTask(task1);
		LocalTask task2 = TasksUiInternal.createNewLocalTask("label");
		taskList.addTask(task2);
		assertEquals(2, taskList.getLastLocalTaskId());
		taskList.deleteTask(task2);
		LocalTask task3 = TasksUiInternal.createNewLocalTask("label");
		taskList.addTask(task3);
		assertTrue(task3.getHandleIdentifier() + " should end with 3", task3.getHandleIdentifier().endsWith("3"));
		assertEquals(3, taskList.getLastLocalTaskId());
		assertEquals(2, taskList.getAllTasks().size());

		TaskTestUtil.saveAndReadTasklist();
		assertEquals(2, taskList.getAllTasks().size());
		assertEquals(3, taskList.getLastLocalTaskId());
		ITask task4 = TasksUiInternal.createNewLocalTask("label");
		assertTrue(task4.getHandleIdentifier() + " should end with 4", task4.getHandleIdentifier().endsWith("4"));
	}

	public void testSingleTaskDeletion() throws Exception {
		TaskTask task = TaskTestUtil.createMockTask("1");
		taskList.addTask(task, taskList.getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));
		assertEquals(1, taskList.getAllTasks().size());
		taskList.deleteTask(task);
		assertEquals(0, taskList.getAllTasks().size());
		taskList.addTask(task, taskList.getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));
		assertEquals(1, taskList.getAllTasks().size());

		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, taskList.getAllTasks().size());

		taskList.deleteTask(task);
		assertEquals(0, taskList.getAllTasks().size());

		TaskTestUtil.saveAndReadTasklist();
		assertEquals(0, taskList.getAllTasks().size());
	}

	public void testCategoryPersistance() throws Exception {
		TaskTask task = TaskTestUtil.createMockTask("1");
		TaskCategory category = new TaskCategory("cat");
		taskList.addCategory(category);
		taskList.addTask(task, category);
		assertNotNull(taskList);
		assertEquals(2, taskList.getCategories().size());

		TaskTestUtil.saveAndReadTasklist();
		assertEquals("" + taskList.getCategories(), 2, taskList.getCategories().size());
		assertEquals(1, taskList.getAllTasks().size());
	}

	public void testCreate() throws Exception {
		TaskTask repositoryTask = TaskTestUtil.createMockTask("1");
		taskList.addTask(repositoryTask, taskList.getDefaultCategory());
		assertEquals(1, taskList.getDefaultCategory().getChildren().size());

		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, taskList.getAllTasks().size());
	}

	public void testCreateAndMove() throws Exception {
		TaskTask repositoryTask = TaskTestUtil.createMockTask("1");
		taskList.addTask(repositoryTask);
		assertEquals(1, taskList.getAllTasks().size());

		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, taskList.getAllTasks().size());
		assertEquals(1, taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).getChildren().size());
	}

	public void testArchiveRepositoryTaskExternalization() throws Exception {
		TaskTask repositoryTask = TaskTestUtil.createMockTask("1");
		taskList.addTask(repositoryTask);
		assertEquals(1, taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).getChildren().size());

		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).getChildren().size());
	}

	public void testRepositoryTasksAndCategoriesMultiRead() throws Exception {
		TaskCategory cat1 = new TaskCategory("Category 1");
		taskList.addCategory(cat1);

		TaskTask reportInCat1 = TaskTestUtil.createMockTask("123");
		taskList.addTask(reportInCat1, cat1);
		assertEquals(cat1, TaskCategory.getParentTaskCategory(reportInCat1));

		TaskTestUtil.saveAndReadTasklist();

		// read once
		Set<AbstractTaskCategory> readCats = taskList.getTaskCategories();
		assertTrue(taskList.getCategories().contains(cat1));
		Iterator<AbstractTaskCategory> iterator = readCats.iterator();

		boolean found = false;
		while (iterator.hasNext()) {
			ITaskContainer readCat1 = iterator.next();
			if (cat1.equals(readCat1)) {
				found = true;
				assertEquals(1, readCat1.getChildren().size());
			}
		}
		if (!found) {
			fail(" Category not found afer tasklist read");
		}

		TaskTestUtil.saveAndReadTasklist();

		// read again
		readCats = taskList.getTaskCategories();
		assertTrue(taskList.getCategories().contains(cat1));

		iterator = readCats.iterator();
		found = false;
		while (iterator.hasNext()) {
			ITaskContainer readCat1 = iterator.next();
			if (cat1.equals(readCat1)) {
				found = true;
				assertEquals(1, readCat1.getChildren().size());
			}
		}
		if (!found) {
			fail(" Category not found afer tasklist read");
		}
	}

	public void testSubTaskExternalization() throws Exception {
		Set<AbstractTask> rootTasks = new HashSet<AbstractTask>();
		AbstractTask task1 = new LocalTask("1", "task1");
		taskList.addTask(task1);
		rootTasks.add(task1);

		AbstractTask sub2 = new LocalTask("2", "sub 2");
		taskList.addTask(sub2, task1);
		assertEquals(1, task1.getChildren().size());
		assertTrue(rootTasks.containsAll(taskList.getDefaultCategory().getChildren()));

		TaskTestUtil.saveAndReadTasklist();

		// XXX: This should pass once sub tasks are handled properly
//		assertTrue(rootTasks.containsAll(taskList.getOrphanContainer(
//				LocalRepositoryConnector.REPOSITORY_URL).getChildren()));

		Collection<ITask> readList = taskList.getDefaultCategory().getChildren();
		for (ITask task : readList) {
			if (task.equals(task1)) {
				assertEquals(task1.getSummary(), task.getSummary());
				assertEquals(1, ((ITaskContainer) task).getChildren().size());
			}
		}
	}

	public void testCreationAndExternalization() throws Exception {
		Set<AbstractTask> rootTasks = new HashSet<AbstractTask>();
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		rootTasks.add(task1);
		assertEquals(1, taskList.getAllTasks().size());

		AbstractTask sub1 = TasksUiInternal.createNewLocalTask("sub 1");
		taskList.addTask(sub1, task1);
		// default category, mock orphans, mock unsubmitted
		int baseRootElementsCount = 3;
		assertEquals(baseRootElementsCount, taskList.getRootElements().size());

		//taskList.moveToContainer(sub1, taskList.getArchiveContainer());

		AbstractTask task2 = TasksUiInternal.createNewLocalTask("task 2");
		rootTasks.add(task2);
		assertEquals(3, taskList.getAllTasks().size());

		Set<TaskCategory> categories = new HashSet<TaskCategory>();
		Set<AbstractTask> cat1Contents = new HashSet<AbstractTask>();
		TaskCategory cat1 = new TaskCategory("Category 1");
		taskList.addCategory(cat1);
		categories.add(cat1);
		assertEquals(baseRootElementsCount + 1, taskList.getRootElements().size());

		AbstractTask task3 = TasksUiInternal.createNewLocalTask("task 3");
		taskList.addTask(task3, cat1);
		cat1Contents.add(task3);
		assertEquals(4, taskList.getAllTasks().size());
		assertEquals(cat1, TaskCategory.getParentTaskCategory(task3));
		AbstractTask sub2 = TasksUiInternal.createNewLocalTask("sub 2");
		assertEquals(5, taskList.getAllTasks().size());
		taskList.addTask(sub2, task3);
		//taskList.moveToContainer(sub2, taskList.getArchiveContainer());

		AbstractTask task4 = TasksUiInternal.createNewLocalTask("task 4");
		taskList.addTask(task4, cat1);
		cat1Contents.add(task4);
		assertEquals(6, taskList.getAllTasks().size());

		TaskTask reportInCat1 = TaskTestUtil.createMockTask("123");
		taskList.addTask(reportInCat1, cat1);
		assertEquals(cat1, TaskCategory.getParentTaskCategory(reportInCat1));
		cat1Contents.add(reportInCat1);
		assertEquals(7, taskList.getAllTasks().size());

		assertEquals(baseRootElementsCount + 1, taskList.getRootElements().size());

		TaskTestUtil.saveAndReadTasklist();

		Collection<ITask> readList = taskList.getDefaultCategory().getChildren();
		for (ITask task : readList) {
			if (task.equals(task1)) {
				assertEquals(task1.getSummary(), task.getSummary());
				assertEquals(1, ((AbstractTaskContainer) task).getChildren().size());
			}
		}

		Set<AbstractTaskCategory> readCats = taskList.getTaskCategories();
		assertTrue(taskList.getCategories().contains(cat1));
		Iterator<AbstractTaskCategory> iterator = readCats.iterator();
		boolean found = false;
		while (iterator.hasNext()) {
			ITaskContainer readCat1 = iterator.next();
			if (cat1.equals(readCat1)) {
				found = true;
				for (ITask task : readCat1.getChildren()) {
					assertTrue(cat1Contents.contains(task));
				}
			}
		}
		if (!found) {
			fail(" Category not found afer tasklist read");
		}
	}

	public void testExternalizationOfHandlesWithDash() throws Exception {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		taskList.addTask(task1, taskList.getDefaultCategory());

		TaskTestUtil.saveAndReadTasklist();
		assertTrue(taskList.getDefaultCategory().getChildren().contains(task1));
	}

	/**
	 * If a task exists in a category and is a query hit it should not be removed from the category
	 *
	 * @throws Exception
	 */
	public void testQueryRemovedTaskInCategory() throws Exception {
		TaskTask mockTask = TaskTestUtil.createMockTask("1");
		RepositoryQuery mockQuery = TaskTestUtil.createMockQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);
		assertTrue(taskList.getDefaultCategory().isEmpty());
		taskList.addTask(mockTask, taskList.getDefaultCategory());
		assertEquals(1, taskList.getCategories().size());
		assertFalse(taskList.getDefaultCategory().isEmpty());

		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getQueries().size());
		assertFalse(taskList.getDefaultCategory().isEmpty());

		taskList.deleteQuery(mockQuery);
		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, taskList.getCategories().size());
		assertEquals(0, taskList.getQueries().size());
		assertFalse(taskList.getDefaultCategory().isEmpty());
	}

	/**
	 * New local tasks should automatically be created in the Local orphaned folder.
	 */
	public void testAddLocalTask() {
		Set<ITask> tasks = taskList.getTasks(LocalRepositoryConnector.REPOSITORY_URL);
		assertTrue(tasks.isEmpty());
		ITask localTask = TasksUiInternal.createNewLocalTask("Task 1");
		assertNotNull(localTask);
		assertEquals(1, ((AbstractTask) localTask).getParentContainers().size());
	}

	@SuppressWarnings("null")
	public void testRemindedPersistance() throws Exception {
		String bugNumber = "106939";
		ITask task = TasksUi.getRepositoryModel().createTask(repository, bugNumber);
		TaskTask task1 = null;
		if (task instanceof TaskTask) {
			task1 = (TaskTask) task;
		}
		assertNotNull(task1);

		TasksUiPlugin.getTaskList().addTask(task1);

		task1.setReminded(true);
		TaskTestUtil.saveAndReadTasklist();

		TaskList taskList = TasksUiPlugin.getTaskList();
		assertEquals(1, taskList.getAllTasks().size());
		Set<ITask> tasksReturned = taskList.getTasks(MockRepositoryConnector.REPOSITORY_URL);
		assertNotNull(tasksReturned);
		assertEquals(1, tasksReturned.size());
		for (ITask taskRet : tasksReturned) {
			assertTrue(((AbstractTask) taskRet).isReminded());
		}
	}

	@SuppressWarnings("null")
	public void testOwnerPersistance() throws Exception {
		String bugNumber = "106939";
		ITask task = TasksUi.getRepositoryModel().createTask(repository, bugNumber);
		TaskTask task1 = null;
		if (task instanceof TaskTask) {
			task1 = (TaskTask) task;
		}
		assertNotNull(task1);

		TasksUiPlugin.getTaskList().addTask(task1);

		task1.setOwner("Joel User");
		task1.setOwnerId("joel.user");
		TaskTestUtil.saveAndReadTasklist();

		TaskList taskList = TasksUiPlugin.getTaskList();
		assertEquals(1, taskList.getAllTasks().size());
		Set<ITask> tasksReturned = taskList.getTasks(MockRepositoryConnector.REPOSITORY_URL);
		assertNotNull(tasksReturned);
		assertEquals(1, tasksReturned.size());
		for (ITask taskRet : tasksReturned) {
			assertEquals("Joel User", taskRet.getOwner());
			assertEquals("joel.user", taskRet.getOwnerId());
		}
	}

	public void testRepositoryTaskExternalization() throws Exception {
		TaskTask task = (TaskTask) TasksUi.getRepositoryModel().createTask(repository, "1");
		task.setTaskKind("kind");
		TasksUiPlugin.getTaskList().addTask(task);
		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, TasksUiPlugin.getTaskList()
				.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL)
				.getChildren()
				.size());
		ITask readTask = TasksUiPlugin.getTaskList()
				.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL)
				.getChildren()
				.iterator()
				.next();

		assertEquals(task.getHandleIdentifier(), readTask.getHandleIdentifier());
		assertEquals(task.getSummary(), readTask.getSummary());
		assertEquals(task.getTaskKind(), readTask.getTaskKind());
	}

	public void testQueryExternalization() throws Exception {
		RepositoryQuery query = (RepositoryQuery) TasksUi.getRepositoryModel().createRepositoryQuery(repository);
		assertEquals(MockRepositoryConnector.REPOSITORY_URL, query.getRepositoryUrl());
		assertEquals("<never>", query.getLastSynchronizedTimeStamp());
		query.setLastSynchronizedStamp("today");
		TasksUiPlugin.getTaskList().addQuery(query);

		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, TasksUiPlugin.getTaskList().getQueries().size());
		IRepositoryQuery readQuery = TasksUiPlugin.getTaskList().getQueries().iterator().next();
		assertEquals(query.getRepositoryUrl(), readQuery.getRepositoryUrl());
		assertEquals("today", query.getLastSynchronizedTimeStamp());
		assertEquals(MockRepositoryConnector.REPOSITORY_URL, readQuery.getRepositoryUrl());
	}

	public void testDeleteQuery() {
		RepositoryQuery query = new RepositoryQuery(MockRepositoryConnector.CONNECTOR_KIND, "queryUrl");
		query.setRepositoryUrl("repositoryUrl");
		TasksUiPlugin.getTaskList().addQuery(query);

		IRepositoryQuery readQuery = TasksUiPlugin.getTaskList().getQueries().iterator().next();
		assertEquals(query, readQuery);

		TasksUiPlugin.getTaskList().deleteQuery(query);
		assertEquals(0, TasksUiPlugin.getTaskList().getQueries().size());
	}

	public void testDeleteQueryAfterRename() {
		RepositoryQuery query = new RepositoryQuery(MockRepositoryConnector.CONNECTOR_KIND, "queryUrl");
		query.setRepositoryUrl("repositoryUrl");
		TasksUiPlugin.getTaskList().addQuery(query);

		IRepositoryQuery readQuery = TasksUiPlugin.getTaskList().getQueries().iterator().next();
		assertEquals(query, readQuery);
		query.setSummary("newName");
		TasksUiPlugin.getTaskList().deleteQuery(query);
		assertEquals(0, TasksUiPlugin.getTaskList().getQueries().size());
	}

	public void testCreateQueryWithSameName() {
		RepositoryQuery query = new RepositoryQuery(MockRepositoryConnector.CONNECTOR_KIND, "queryUrl");
		query.setRepositoryUrl("repositoryUrl");
		TasksUiPlugin.getTaskList().addQuery(query);
		assertEquals(1, TasksUiPlugin.getTaskList().getQueries().size());
		IRepositoryQuery readQuery = TasksUiPlugin.getTaskList().getQueries().iterator().next();
		assertEquals(query, readQuery);

		try {
			query = new RepositoryQuery(MockRepositoryConnector.CONNECTOR_KIND, "queryUrl");
			query.setRepositoryUrl("repositoryUrl");
			TasksUiPlugin.getTaskList().addQuery(query);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			if (!e.getMessage().equals("Handle queryUrl already exists in task list")) {
				throw e;
			}
		}
		assertEquals(1, TasksUiPlugin.getTaskList().getQueries().size());
	}

	public void testRepositoryUrlHandles() throws Exception {
		String taskId = "123";
		String repositoryUrl = "http://mock.eclipse.org";
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, repositoryUrl);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		ITask bugTask = new TaskTask("mock", repositoryUrl, taskId);
		bugTask.setSummary("Summary");
		assertEquals(repositoryUrl, bugTask.getRepositoryUrl());

		TasksUiPlugin.getTaskList().addTask(bugTask);
		TaskTestUtil.saveAndReadTasklist();

		ITask readReport = TasksUiPlugin.getTaskList().getTask(repositoryUrl, taskId);
		assertEquals("Summary", readReport.getSummary());
		assertEquals(repositoryUrl, readReport.getRepositoryUrl());
		TasksUiPlugin.getRepositoryManager().removeRepository(repository);
	}

	public void testDueDateExternalization() throws Exception {
		AbstractTask task = new LocalTask("1", "task 1");
		Date dueDate = new Date();
		task.setDueDate(dueDate);
		TasksUiPlugin.getTaskList().addTask(task);
		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());

		TaskTestUtil.saveAndReadTasklist();

		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());
		Collection<ITask> readList = TasksUiPlugin.getTaskList().getDefaultCategory().getChildren();
		ITask readTask = readList.iterator().next();
		assertTrue(readTask.getSummary().equals("task 1"));
		assertTrue(readTask.getDueDate().compareTo(dueDate) == 0);
	}

	public void testPastReminder() throws InterruptedException {
		AbstractTask task = new LocalTask("1", "1");

		task.setScheduledForDate(new DateRange(Calendar.getInstance()));
		Thread.sleep(2000);
		assertFalse(TasksUiPlugin.getTaskActivityManager().isPastReminder(task));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 2);
		task.setScheduledForDate(new DateRange(cal));
		assertFalse(TasksUiPlugin.getTaskActivityManager().isPastReminder(task));

		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, -2);
		task.setScheduledForDate(new DateRange(cal1, cal));
		assertFalse(TasksUiPlugin.getTaskActivityManager().isPastReminder(task));

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, -2);
		task.setScheduledForDate(new DateRange(cal2));
		task.setCompletionDate(new Date());
		assertFalse(TasksUiPlugin.getTaskActivityManager().isPastReminder(task));
	}

	public void testDates() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();

		Date start = Calendar.getInstance().getTime();
		Date creation = new Date();
		AbstractTask task = new LocalTask("1", "task 1");

		TasksUiPlugin.getTaskList().addTask(task);
		assertNull(task.getCreationDate());
		task.setCreationDate(start);
		assertEquals(start, task.getCreationDate());

		assertNull(task.getCompletionDate());
		task.setCompletionDate(creation);
		assertEquals(creation, task.getCompletionDate());

		assertEquals(1, TasksUiPlugin.getTaskList().getRootElements().size());
		TasksUiPlugin.getExternalizationManager().requestSave();

		assertNotNull(TasksUiPlugin.getTaskList());
		assertEquals(1, TasksUiPlugin.getTaskList().getDefaultCategory().getChildren().size());

		Collection<ITask> readList = TasksUiPlugin.getTaskList().getDefaultCategory().getChildren();
		AbstractTask readTask = (AbstractTask) readList.iterator().next();
		assertTrue(readTask.getSummary().equals("task 1"));

		assertEquals("should be: " + creation, task.getCreationDate(), readTask.getCreationDate());
		assertEquals(task.getCompletionDate(), readTask.getCompletionDate());
		assertEquals(task.getScheduledForDate(), readTask.getScheduledForDate());
	}

	// test case for bug 342086
	public void testDatesTimeZone() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();

		TimeZone.setDefault(TimeZone.getTimeZone("PRC"));
		AbstractTask task = new LocalTask("1", "task 1");
		Date creationDate = new Date();
		task.setCreationDate(creationDate);
		TasksUiPlugin.getTaskList().addTask(task);
		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());

		TaskTestUtil.saveNow();
		TimeZone.setDefault(TimeZone.getTimeZone("CST"));
		TaskTestUtil.resetTaskList();
		TasksUiPlugin.getDefault().initializeDataSources();

		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());
		Collection<ITask> readList = TasksUiPlugin.getTaskList().getDefaultCategory().getChildren();
		ITask readTask = readList.iterator().next();
		assertTrue(readTask.getSummary().equals("task 1"));
		assertTrue(readTask.getCreationDate().compareTo(creationDate) == 0);
	}

	// Task retention when connector missing upon startup
	public void testOrphanedTasks() throws Exception {
		// make some tasks
		// save them
		assertEquals(0, TasksUiPlugin.getTaskList().getAllTasks().size());
		ITask task = new TaskTask(MockRepositoryConnector.CONNECTOR_KIND, "http://bugs", "1");
		TasksUiPlugin.getTaskList().addTask(task);

		// reload tasklist and check that they persist
		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());

		// removed/disable externalizers
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
				.removeRepositoryConnector(MockRepositoryConnector.CONNECTOR_KIND);

		// reload tasklist ensure task didn't load
		TaskTestUtil.saveAndReadTasklist();
		assertEquals(0, TasksUiPlugin.getTaskList().getAllTasks().size());
		// Save the task list (tasks with missing connectors should get
		// persisted)
		TasksUiPlugin.getExternalizationManager().save(true);

		// re-enable connector
		TasksUiPlugin.getRepositoryManager().addRepositoryConnector(connector);

		// re-load tasklist
		TaskTestUtil.saveAndReadTasklist();

		// ensure that task now gets loaded
		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());
		assertNotNull("1", TasksUiPlugin.getTaskList().getTask("http://bugs", "1"));
	}

	// Query retention when connector missing/fails to load
	public void testOrphanedQueries() throws Exception {
		// make a query
		assertEquals(0, TasksUiPlugin.getTaskList().getQueries().size());
		RepositoryQuery query = new RepositoryQuery(MockRepositoryConnector.CONNECTOR_KIND, "bugzillaQuery");
		TasksUiPlugin.getTaskList().addQuery(query);
		TasksUiPlugin.getExternalizationManager().save(true);

		// reload tasklist and check that they persist
		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, TasksUiPlugin.getTaskList().getQueries().size());

		// removed/disable externalizers
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
				.removeRepositoryConnector(MockRepositoryConnector.CONNECTOR_KIND);

		// reload tasklist ensure query didn't load
		TaskTestUtil.saveAndReadTasklist();
		assertEquals(0, TasksUiPlugin.getTaskList().getQueries().size());
		// Save the task list (queries with missing connectors should get
		// persisted)
		TasksUiPlugin.getExternalizationManager().requestSave();

		// re-enable connector
		TasksUiPlugin.getRepositoryManager().addRepositoryConnector(connector);

		// re-load tasklist
		TaskTestUtil.saveAndReadTasklist();

		// ensure that query now gets loaded
		assertEquals(1, TasksUiPlugin.getTaskList().getQueries().size());
	}

}
