/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UnfiledCategory;
import org.eclipse.mylyn.internal.tasks.ui.ScheduledTaskListSynchJob;
import org.eclipse.mylyn.internal.tasks.ui.TaskListSynchronizationScheduler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskReadAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskUnreadAction;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.tests.connector.MockAttributeFactory;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryTask;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class TaskListManagerTest extends TestCase {

	private TaskListManager manager;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = TasksUiPlugin.getTaskListManager();
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());

		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
		TasksUiPlugin.getRepositoryManager().removeRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	public void testQueryAndCategoryNameClash() {
		TaskCategory category = new TaskCategory("TestClash");
		manager.getTaskList().addCategory(category);
		assertTrue(manager.getTaskList().getCategories().contains(category));
		assertEquals(3, manager.getTaskList().getCategories().size());

		MockRepositoryQuery query = new MockRepositoryQuery("TestClash");
		manager.getTaskList().addQuery(query);
		assertTrue(manager.getTaskList().getCategories().contains(category));
		assertEquals(3, manager.getTaskList().getCategories().size());

		manager.getTaskList().deleteCategory(category);
	}

	public void testUniqueTaskID() {
		LocalTask task1 = manager.createNewLocalTask("label");
		manager.getTaskList().addTask(task1);
		LocalTask task2 = manager.createNewLocalTask("label");
		manager.getTaskList().addTask(task2);
		assertEquals(2, manager.getTaskList().getLastLocalTaskId());
		manager.getTaskList().deleteTask(task2);
		LocalTask task3 = manager.createNewLocalTask("label");
		manager.getTaskList().addTask(task3);
		assertTrue(task3.getHandleIdentifier() + " should end with 3", task3.getHandleIdentifier().endsWith("3"));
		assertEquals(3, manager.getTaskList().getLastLocalTaskId());

		assertEquals(2, manager.getTaskList().getAllTasks().size());
		manager.saveTaskList();
		manager.resetTaskList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getLastLocalTaskId());
		manager.readExistingOrCreateNewList();
		assertEquals(2, manager.getTaskList().getAllTasks().size());
		assertEquals(3, manager.getTaskList().getLastLocalTaskId());
		AbstractTask task4 = manager.createNewLocalTask("label");
		assertTrue(task4.getHandleIdentifier() + " should end with 4", task4.getHandleIdentifier().endsWith("4"));
	}

	public void testSingleTaskDeletion() {
		MockRepositoryTask task = new MockRepositoryTask("1");
		manager.getTaskList().moveToContainer(task, manager.getTaskList().getDefaultCategory());
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		manager.getTaskList().moveToContainer(task, manager.getTaskList().getDefaultCategory());
		assertEquals(1, manager.getTaskList().getAllTasks().size());

		manager.saveTaskList();

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getAllTasks().size());

		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		manager.saveTaskList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	public void testMigrateTaskContextFiles() throws IOException {
		File fileA = ContextCorePlugin.getContextManager().getFileForContext("http://a-1");
		fileA.createNewFile();
		fileA.deleteOnExit();
		assertTrue(fileA.exists());
		manager.refactorRepositoryUrl("http://a", "http://b");
		File fileB = ContextCorePlugin.getContextManager().getFileForContext("http://b-1");
		assertTrue(fileB.exists());
		assertFalse(fileA.exists());
	}

	public void testMigrateQueryUrlHandles() {
		AbstractRepositoryQuery query = new MockRepositoryQuery("mquery");
		query.setRepositoryUrl("http://foo.bar");
		query.setUrl("http://foo.bar/b");
		manager.getTaskList().addQuery(query);
		assertTrue(manager.getTaskList().getRepositoryQueries("http://foo.bar").size() > 0);
		manager.refactorRepositoryUrl("http://foo.bar", "http://bar.baz");
		assertTrue(manager.getTaskList().getRepositoryQueries("http://foo.bar").size() == 0);
		assertTrue(manager.getTaskList().getRepositoryQueries("http://bar.baz").size() > 0);
		AbstractRepositoryQuery changedQuery = manager.getTaskList()
				.getRepositoryQueries("http://bar.baz")
				.iterator()
				.next();
		assertEquals("http://bar.baz/b", changedQuery.getUrl());
	}

	public void testMigrateQueryHandles() {
		AbstractRepositoryQuery query = new MockRepositoryQuery("mquery");
		query.setRepositoryUrl("http://a");
		manager.getTaskList().addQuery(query);
		manager.refactorRepositoryUrl("http://a", "http://b");
		assertFalse(manager.getTaskList().getRepositoryQueries("http://b").isEmpty());
		assertTrue(manager.getTaskList().getRepositoryQueries("http://a").isEmpty());
	}

// public void testMigrateQueryHits() {
// AbstractRepositoryQuery query = new MockRepositoryQuery("mquery",
// manager.getTaskList());
// query.setRepositoryUrl("http://a");
// manager.getTaskList().addQuery(query);
// AbstractQueryHit hit = new MockQueryHit(manager.getTaskList(), "http://a",
// "", "123");
// query.addHit(hit);
// manager.refactorRepositoryUrl("http://a", "http://b");
// assertNotNull(manager.getTaskList().getQueryHit("http://b-123"));
// assertEquals("http://b-123", hit.getHandleIdentifier());
// }

	public void testMigrateTaskHandles() {
		AbstractTask task = new MockRepositoryTask("http://a", "123");
		AbstractTask task2 = new MockRepositoryTask("http://other", "other");
		manager.getTaskList().addTask(task);
		manager.getTaskList().addTask(task2);

		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(), task.getConnectorKind(),
				task.getRepositoryUrl(), task.getTaskId(), task.getTaskKind());
		TasksUiPlugin.getTaskDataManager().setNewTaskData(taskData);
		assertNotNull(TasksUiPlugin.getTaskDataManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId()));

		RepositoryTaskData taskData2 = new RepositoryTaskData(new MockAttributeFactory(), task2.getConnectorKind(),
				task2.getRepositoryUrl(), task2.getTaskId(), task2.getTaskKind());
		taskData2.setNewComment("TEST");
		TasksUiPlugin.getTaskDataManager().setNewTaskData(taskData2);
		assertNotNull(TasksUiPlugin.getTaskDataManager().getNewTaskData(task2.getRepositoryUrl(), task2.getTaskId()));
		assertEquals("TEST", TasksUiPlugin.getTaskDataManager().getNewTaskData(task2.getRepositoryUrl(),
				task2.getTaskId()).getNewComment());

		manager.refactorRepositoryUrl("http://a", "http://b");
		assertNull(manager.getTaskList().getTask("http://a-123"));
		assertNotNull(manager.getTaskList().getTask("http://b-123"));
		assertNotNull(TasksUiPlugin.getTaskDataManager().getNewTaskData("http://b", "123"));
		RepositoryTaskData otherData = TasksUiPlugin.getTaskDataManager().getNewTaskData(task2.getRepositoryUrl(),
				task2.getTaskId());
		assertNotNull(otherData);
		assertEquals("TEST", otherData.getNewComment());
	}

	public void testMigrateTaskHandlesWithExplicitSet() {
		AbstractTask task = new MockRepositoryTask("http://a", "123");
		task.setUrl("http://a/task/123");
		manager.getTaskList().addTask(task);
		manager.refactorRepositoryUrl("http://a", "http://b");
		assertNull(manager.getTaskList().getTask("http://a-123"));
		assertNotNull(manager.getTaskList().getTask("http://b-123"));
		assertEquals("http://b/task/123", task.getUrl());
	}

	@SuppressWarnings("deprecation")
	public void testRefactorMetaContextHandles() {
		String firstUrl = "http://repository1.com/bugs";
		String secondUrl = "http://repository2.com/bugs";
		AbstractTask task1 = new MockRepositoryTask(firstUrl, "1");
		AbstractTask task2 = new MockRepositoryTask(firstUrl, "2");
		manager.getTaskList().addTask(task1);
		manager.getTaskList().addTask(task2);
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.MINUTE, 5);

		Calendar startDate2 = Calendar.getInstance();
		startDate2.add(Calendar.MINUTE, 15);
		Calendar endDate2 = Calendar.getInstance();
		endDate2.add(Calendar.MINUTE, 25);

		ContextCorePlugin.getContextManager().resetActivityHistory();
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertEquals(0, metaContext.getInteractionHistory().size());

		ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.ATTENTION,
						InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task1.getHandleIdentifier(), "origin",
						null, InteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, startDate.getTime(),
						endDate.getTime()));

		ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.ATTENTION,
						InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task2.getHandleIdentifier(), "origin",
						null, InteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, startDate2.getTime(),
						endDate2.getTime()));

		assertEquals(2, metaContext.getInteractionHistory().size());
		assertEquals(60 * 1000 * 5, manager.getElapsedTime(task1));
		assertEquals(2 * 60 * 1000 * 5, manager.getElapsedTime(task2));
		manager.refactorRepositoryUrl(firstUrl, secondUrl);
		metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertEquals(2, metaContext.getInteractionHistory().size());
		assertEquals(60 * 1000 * 5, manager.getElapsedTime(new MockRepositoryTask(secondUrl, "1")));
		assertEquals(2 * 60 * 1000 * 5, manager.getElapsedTime(new MockRepositoryTask(secondUrl, "2")));
		assertEquals(secondUrl + "-1", metaContext.getInteractionHistory().get(0).getStructureHandle());
	}

	@SuppressWarnings("deprecation")
	public void testIsActiveToday() {
		AbstractTask task = new LocalTask("1", "task-1");
		assertFalse(manager.isScheduledForToday(task));

		task.setScheduledForDate(new Date());
		assertTrue(manager.isScheduledForToday(task));

		task.setReminded(true);
		assertTrue(manager.isScheduledForToday(task));
		task.setReminded(true);

		Calendar inAnHour = Calendar.getInstance();
		inAnHour.set(Calendar.HOUR_OF_DAY, inAnHour.get(Calendar.HOUR_OF_DAY) + 1);
		inAnHour.getTime();
		task.setScheduledForDate(inAnHour.getTime());
		Calendar tomorrow = Calendar.getInstance();
		manager.snapToNextDay(tomorrow);
		assertEquals(-1, inAnHour.compareTo(tomorrow));

		assertTrue(manager.isScheduledForToday(task));
	}

	@SuppressWarnings("deprecation")
	public void testScheduledForToday() {
		AbstractTask task = new LocalTask("1", "task-1");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 2);
		task.setScheduledForDate(cal.getTime());
		assertTrue(manager.isScheduledForToday(task));
		manager.setSecheduledIn(cal, 1);
		task.setScheduledForDate(cal.getTime());
		assertFalse(manager.isScheduledForToday(task));
		cal = Calendar.getInstance();
		manager.setScheduledEndOfDay(cal);
		task.setScheduledForDate(cal.getTime());
		assertTrue(manager.isScheduledForToday(task));
	}

	@SuppressWarnings("deprecation")
	public void testSchedulePastEndOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		manager.setSecheduledIn(calendar, 1);
		assertEquals("Should be October", Calendar.OCTOBER, calendar.get(Calendar.MONTH));
	}

	@SuppressWarnings("deprecation")
	public void testIsCompletedToday() {
		AbstractTask task = new LocalTask("1", "task 1");
		task.setCompleted(true);
		task.setCompletionDate(new Date());
		assertTrue(manager.isCompletedToday(task));

		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		mockTask.setOwner("unknown");
		manager.getTaskList().addTask(mockTask);
		mockTask.setCompleted(true);
		mockTask.setCompletionDate(new Date());
		assertFalse("completed: " + mockTask.getCompletionDate(), manager.isCompletedToday(mockTask));

		mockTask = new MockRepositoryTask("2");
		manager.getTaskList().addTask(mockTask);
		mockTask.setCompleted(true);
		mockTask.setCompletionDate(new Date());
		repository.setAuthenticationCredentials("testUser", "testPassword");
		mockTask.setOwner("testUser");
		assertTrue(manager.isCompletedToday(mockTask));

	}

	public void testMoveCategories() {
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		AbstractTask task1 = new LocalTask("t1", "t1");

		TaskCategory cat1 = new TaskCategory("cat1");
		manager.getTaskList().addCategory(cat1);
		TaskCategory cat2 = new TaskCategory("cat2");
		manager.getTaskList().addCategory(cat2);

		manager.getTaskList().moveToContainer(task1, cat1);
		assertEquals(1, manager.getTaskList().getContainerForHandle("cat1").getChildren().size());
		assertEquals(0, manager.getTaskList().getContainerForHandle("cat2").getChildren().size());

		manager.getTaskList().moveToContainer(task1, cat2);
		assertEquals(0, manager.getTaskList().getContainerForHandle("cat1").getChildren().size());
		assertEquals(1, manager.getTaskList().getContainerForHandle("cat2").getChildren().size());
	}

	public void testMoveToRoot() {
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		AbstractTask task1 = new LocalTask("t1", "t1");
		manager.getTaskList().moveToContainer(task1, manager.getTaskList().getDefaultCategory());
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(UnfiledCategory.HANDLE, task1.getParentContainers().iterator().next().getHandleIdentifier());

		TaskCategory cat1 = new TaskCategory("c1");
		manager.getTaskList().addCategory(cat1);

		manager.getTaskList().moveToContainer(task1, cat1);
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(cat1, task1.getParentContainers().iterator().next());

		manager.getTaskList().moveToContainer(task1, manager.getTaskList().getDefaultCategory());
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(0, cat1.getChildren().size());
		assertEquals(UnfiledCategory.HANDLE, task1.getParentContainers().iterator().next().getHandleIdentifier());
	}

	public void testEmpty() {
		manager.resetTaskList();
		assertTrue(manager.getTaskList().isEmpty());
		manager.getTaskList().internalAddRootTask(new LocalTask("", ""));
		assertFalse(manager.getTaskList().isEmpty());
	}

	public void testCategoryPersistance() {
		MockRepositoryTask task = new MockRepositoryTask("1");
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		manager.getTaskList().moveToContainer(task, category);
		assertNotNull(manager.getTaskList());
		assertEquals(3, manager.getTaskList().getCategories().size());

		manager.saveTaskList();
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals("" + manager.getTaskList().getCategories(), 3, manager.getTaskList().getCategories().size());
		assertEquals(1, manager.getTaskList().getAllTasks().size());
	}

	public void testDeleteCategory() {
		assertNotNull(manager.getTaskList());
		assertEquals(2, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(3, manager.getTaskList().getCategories().size());
		manager.getTaskList().deleteCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
	}

	public void testDeleteCategoryMovesTasksToRoot() {
		AbstractTask task = new MockRepositoryTask("delete");
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addTask(task, category);
		manager.getTaskList().addCategory(category);
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		manager.getTaskList().deleteCategory(category);
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
	}

	public void testRenameCategory() {

		assertNotNull(manager.getTaskList());

		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(3, manager.getTaskList().getCategories().size());
		String newDesc = "newDescription";
		manager.getTaskList().renameContainer(category, newDesc);
		AbstractTaskCategory container = manager.getTaskList().getContainerForHandle(newDesc);
		assertNotNull(container);
		assertEquals(newDesc, container.getSummary());
		manager.getTaskList().deleteCategory(container);
		assertEquals(2, manager.getTaskList().getCategories().size());
	}

	public void testDeleteCategoryAfterRename() {
		String newDesc = "newDescription";
		assertNotNull(manager.getTaskList());
		assertEquals(2, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(3, manager.getTaskList().getCategories().size());
		manager.getTaskList().renameContainer(category, newDesc);
		manager.getTaskList().deleteCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
	}

	public void testCreateSameCategoryName() {
		assertNotNull(manager.getTaskList());
		assertEquals(2, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(3, manager.getTaskList().getCategories().size());
		TaskCategory category2 = new TaskCategory("cat");
		manager.getTaskList().addCategory(category2);
		assertEquals(3, manager.getTaskList().getCategories().size());
		AbstractTaskContainer container = manager.getTaskList().getContainerForHandle("cat");
		assertEquals(container, category);
	}

	public void testDeleteRootTask() {
		AbstractTask task = new LocalTask("1", "label");
		manager.getTaskList().addTask(task);
		manager.getTaskList().internalAddRootTask(task);
		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
	}

	public void testDeleteFromCategory() {
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(2, manager.getTaskList().getCategories().size());

		AbstractTask task = new LocalTask("1", "label");
		TaskCategory category = new TaskCategory("handleAndDescription");
		manager.getTaskList().addTask(task);
		assertEquals(1, manager.getTaskList().getArchiveContainer().getChildren().size());

		manager.getTaskList().addCategory(category);
		manager.getTaskList().moveToContainer(task, category);
		assertEquals(3, manager.getTaskList().getCategories().size());
		assertEquals(1, category.getChildren().size());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());

		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(0, category.getChildren().size());
	}

	public void testDeleteRepositoryTask() {
		String repositoryUrl = "http://somewhere.com";
		repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, repositoryUrl);
		TasksUiPlugin.getRepositoryManager().addRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
		MockRepositoryTask task = new MockRepositoryTask(repositoryUrl, "1");
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		taskList.moveToContainer(task, manager.getTaskList().getDefaultCategory());
		MockRepositoryQuery query = new MockRepositoryQuery("query");
		taskList.addQuery(query);
		taskList.addTask(task, query);
		assertEquals(1, taskList.getAllTasks().size());
		assertEquals(1, taskList.getDefaultCategory().getChildren().size());
		taskList.deleteTask(task);
		assertEquals(0, taskList.getAllTasks().size());
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
	}

	public void testCreate() {
		MockRepositoryTask repositoryTask = new MockRepositoryTask("1");
		manager.getTaskList().addTask(repositoryTask, manager.getTaskList().getDefaultCategory());
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		manager.saveTaskList();

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getAllTasks().size());
	}

	public void testCreateAndMove() {
		MockRepositoryTask repositoryTask = new MockRepositoryTask("1");
		repositoryTask.setTaskKind("kind");
		manager.getTaskList().addTask(repositoryTask, manager.getTaskList().getDefaultCategory());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		manager.saveTaskList();

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
	}

	public void testArchiveRepositoryTaskExternalization() {
		MockRepositoryTask repositoryTask = new MockRepositoryTask("1");
		repositoryTask.setTaskKind("kind");
		manager.getTaskList().addTask(repositoryTask);
		assertEquals(1, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		manager.saveTaskList();

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
	}

	public void testRepositoryTasksAndCategoriesMultiRead() {
		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.getTaskList().addCategory(cat1);

		MockRepositoryTask reportInCat1 = new MockRepositoryTask("123");
		manager.getTaskList().moveToContainer(reportInCat1, cat1);
		assertEquals(cat1, reportInCat1.getParentContainers().iterator().next());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		// read once
		Set<AbstractTaskCategory> readCats = manager.getTaskList().getTaskContainers();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));
		Iterator<AbstractTaskCategory> iterator = readCats.iterator();

		boolean found = false;
		while (iterator.hasNext()) {
			AbstractTaskContainer readCat1 = iterator.next();
			if (cat1.equals(readCat1)) {
				found = true;
				assertEquals(1, readCat1.getChildren().size());
			}
		}
		if (!found)
			fail(" Category not found afer tasklist read");

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		// read again
		readCats = manager.getTaskList().getTaskContainers();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));

		iterator = readCats.iterator();
		found = false;
		while (iterator.hasNext()) {
			AbstractTaskContainer readCat1 = iterator.next();
			if (cat1.equals(readCat1)) {
				found = true;
				assertEquals(1, readCat1.getChildren().size());
			}
		}
		if (!found)
			fail(" Category not found afer tasklist read");
	}

	public void testSubTaskExternalization() {
		Set<AbstractTask> rootTasks = new HashSet<AbstractTask>();
		AbstractTask task1 = new LocalTask("1", "task1");
		manager.getTaskList().moveToContainer(task1, manager.getTaskList().getDefaultCategory());
		rootTasks.add(task1);

		AbstractTask sub2 = new LocalTask("2", "sub 2");
		manager.getTaskList().addTask(sub2, task1);
		assertEquals(1, task1.getChildren().size());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		assertNotNull(manager.getTaskList());
		assertTrue(rootTasks.containsAll(manager.getTaskList().getDefaultCategory().getChildren()));

		Set<AbstractTask> readList = manager.getTaskList().getDefaultCategory().getChildren();
		for (AbstractTask task : readList) {
			if (task.equals(task1)) {
				assertEquals(task1.getSummary(), task.getSummary());
				assertEquals(1, task.getChildren().size());
			}
		}
	}

	public void testCreationAndExternalization() {
		Set<AbstractTask> rootTasks = new HashSet<AbstractTask>();
		AbstractTask task1 = manager.createNewLocalTask("task 1");
		manager.getTaskList().moveToContainer(task1, manager.getTaskList().getDefaultCategory());
		rootTasks.add(task1);

		AbstractTask sub1 = manager.createNewLocalTask("sub 1");
		manager.getTaskList().addTask(sub1, task1);

		manager.getTaskList().moveToContainer(sub1, manager.getTaskList().getArchiveContainer());

		AbstractTask task2 = manager.createNewLocalTask("task 2");
		manager.getTaskList().moveToContainer(task2, manager.getTaskList().getDefaultCategory());
		rootTasks.add(task2);

		Set<TaskCategory> categories = new HashSet<TaskCategory>();
		Set<AbstractTask> cat1Contents = new HashSet<AbstractTask>();
		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.getTaskList().addCategory(cat1);
		categories.add(cat1);
		AbstractTask task3 = manager.createNewLocalTask("task 3");
		manager.getTaskList().moveToContainer(task3, cat1);
		cat1Contents.add(task3);
		assertEquals(cat1, task3.getParentContainers().iterator().next());
		AbstractTask sub2 = manager.createNewLocalTask("sub 2");
		manager.getTaskList().addTask(sub2, task3);
		manager.getTaskList().moveToContainer(sub2, manager.getTaskList().getArchiveContainer());

		AbstractTask task4 = manager.createNewLocalTask("task 4");
		manager.getTaskList().moveToContainer(task4, cat1);
		cat1Contents.add(task4);

		MockRepositoryTask reportInCat1 = new MockRepositoryTask("123");
		manager.getTaskList().moveToContainer(reportInCat1, cat1);
		assertEquals(cat1, reportInCat1.getParentContainers().iterator().next());
		cat1Contents.add(reportInCat1);

		MockRepositoryTask reportInRoot = new MockRepositoryTask("124");
		manager.getTaskList().moveToContainer(reportInRoot, manager.getTaskList().getDefaultCategory());
		rootTasks.add(reportInRoot);

		assertEquals(3, manager.getTaskList().getRootElements().size());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
		// manager.getTaskList().clear();
		// TaskList list = new TaskList();
		// manager.setTaskList(list);
		manager.readExistingOrCreateNewList();

		assertNotNull(manager.getTaskList());
		assertTrue(rootTasks.containsAll(manager.getTaskList().getDefaultCategory().getChildren()));

		Set<AbstractTask> readList = manager.getTaskList().getDefaultCategory().getChildren();
		for (AbstractTask task : readList) {
			if (task.equals(task1)) {
				assertEquals(task1.getSummary(), task.getSummary());
				assertEquals(1, task.getChildren().size());
			}
			if (task.equals(reportInRoot)) {
				assertEquals(reportInRoot.getSummary(), task.getSummary());
			}
		}

		Set<AbstractTaskCategory> readCats = manager.getTaskList().getTaskContainers();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));
		Iterator<AbstractTaskCategory> iterator = readCats.iterator();
		boolean found = false;
		while (iterator.hasNext()) {
			AbstractTaskContainer readCat1 = iterator.next();
			if (cat1.equals(readCat1)) {
				found = true;
				assertEquals(cat1Contents, readCat1.getChildren());
			}
		}
		if (!found)
			fail(" Category not found afer tasklist read");
	}

	public void testExternalizationOfHandlesWithDash() {
		Set<AbstractTask> rootTasks = new HashSet<AbstractTask>();

// String handle = AbstractTask.getHandle("http://url/repo-location",
// 1);
		AbstractTask task1 = manager.createNewLocalTask("task 1");
		manager.getTaskList().moveToContainer(task1, manager.getTaskList().getDefaultCategory());
		rootTasks.add(task1);

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
		assertTrue(manager.readExistingOrCreateNewList());

		assertNotNull(manager.getTaskList());
		assertEquals(rootTasks, manager.getTaskList().getDefaultCategory().getChildren());
	}

	public void testScheduledRefreshJob() throws InterruptedException {
		int counter = 3;
		ScheduledTaskListSynchJob.resetCount();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, true);
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS, 1000L);
		assertEquals(0, ScheduledTaskListSynchJob.getCount());
		TaskListSynchronizationScheduler manager = new TaskListSynchronizationScheduler(false);
		manager.startSynchJob();
		Thread.sleep(3000);
		assertTrue(ScheduledTaskListSynchJob.getCount() + " smaller than " + counter,
				ScheduledTaskListSynchJob.getCount() >= counter);
		manager.cancelAll();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
	}

	public void testgetQueriesAndHitsForHandle() {
		TaskList taskList = manager.getTaskList();

		MockRepositoryTask hit1 = new MockRepositoryTask("1");
		MockRepositoryTask hit2 = new MockRepositoryTask("2");
		MockRepositoryTask hit3 = new MockRepositoryTask("3");

		MockRepositoryTask hit1twin = new MockRepositoryTask("1");
		MockRepositoryTask hit2twin = new MockRepositoryTask("2");
		MockRepositoryTask hit3twin = new MockRepositoryTask("3");

		MockRepositoryQuery query1 = new MockRepositoryQuery("query1");
		MockRepositoryQuery query2 = new MockRepositoryQuery("query2");

		taskList.addQuery(query1);
		taskList.addQuery(query2);
		taskList.addTask(hit1, query1);
		taskList.addTask(hit2, query1);
		taskList.addTask(hit3, query1);

		assertEquals(3, query1.getChildren().size());

		taskList.addTask(hit1twin, query2);
		taskList.addTask(hit2twin, query2);
		taskList.addTask(hit3twin, query2);

		assertEquals(3, query2.getChildren().size());

		Set<AbstractRepositoryQuery> queriesReturned = taskList.getQueriesForHandle(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(queriesReturned);
		assertEquals(2, queriesReturned.size());
		assertTrue(queriesReturned.contains(query1));
		assertTrue(queriesReturned.contains(query2));

		Set<String> handles = new HashSet<String>();
		handles.add(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "2"));
		Collection<AbstractTask> hitsReturned = taskList.getTasks(handles);
		assertNotNull(hitsReturned);
		assertEquals(1, hitsReturned.size());
		assertTrue(hitsReturned.contains(hit2));
		assertTrue(hitsReturned.contains(hit2twin));

	}

// public void testQueryHitHasParent() {
// TaskList taskList = manager.getTaskList();
//
// MockQueryHit hit1 = new MockQueryHit(taskList,
// MockRepositoryConnector.REPOSITORY_URL, "description1", "1");
// assertNull(hit1.getParent());
// MockRepositoryQuery query1 = new MockRepositoryQuery("query1",
// manager.getTaskList());
// query1.addHit(hit1);
// assertEquals(query1, hit1.getParent());
//
// }

	public void testUpdateQueryHits() {
		TaskList taskList = manager.getTaskList();

		MockRepositoryTask hit1 = new MockRepositoryTask("1");
		MockRepositoryTask hit2 = new MockRepositoryTask("2");
		MockRepositoryTask hit3 = new MockRepositoryTask("3");

		MockRepositoryTask hit1twin = new MockRepositoryTask("1");
		MockRepositoryTask hit2twin = new MockRepositoryTask("2");
		MockRepositoryTask hit3twin = new MockRepositoryTask("3");

		MockRepositoryQuery query1 = new MockRepositoryQuery("query1");
		taskList.addQuery(query1);

		taskList.addTask(hit1, query1);
		taskList.addTask(hit2, query1);
		taskList.addTask(hit3, query1);

		taskList.addTask(hit1twin, query1);
		taskList.addTask(hit2twin, query1);
		taskList.addTask(hit3twin, query1);

		assertEquals(3, query1.getChildren().size());
		query1.clear();
		assertEquals(0, query1.getChildren().size());
		taskList.addTask(hit1, query1);
		taskList.addTask(hit2, query1);
		assertEquals(2, query1.getChildren().size());
		hit1.setNotified(true);

		taskList.addTask(hit1twin, query1);
		taskList.addTask(hit2twin, query1);
		taskList.addTask(hit3twin, query1);
		assertEquals(3, query1.getChildren().size());
		assertTrue(query1.getChildren().contains(hit1twin));
		assertTrue(query1.getChildren().contains(hit2twin));
		assertTrue(query1.getChildren().contains(hit3twin));
		for (AbstractTask hit : query1.getChildren()) {
			if (hit.equals(hit1twin)) {
				assertTrue(hit.isNotified());
			} else {
				assertFalse(hit.isNotified());
			}
		}
	}

	public void testgetRepositoryTasks() {

		String repositoryUrl = "https://bugs.eclipse.org/bugs";

		String bugNumber = "106939";

		MockRepositoryTask task1 = new MockRepositoryTask(repositoryUrl, bugNumber);
		manager.getTaskList().addTask(task1);

		MockRepositoryTask task2 = new MockRepositoryTask("https://unresolved", bugNumber);
		manager.getTaskList().addTask(task2);

		TaskList taskList = manager.getTaskList();
		assertEquals(2, taskList.getAllTasks().size());
		Set<AbstractTask> tasksReturned = taskList.getRepositoryTasks(repositoryUrl);
		assertNotNull(tasksReturned);
		assertEquals(1, tasksReturned.size());
		assertTrue(tasksReturned.contains(task1));
	}

	public void testAllTasksDeactivation() {
		AbstractTask task1 = new LocalTask("task1", "description1");
		AbstractTask task2 = new LocalTask("task2", "description2");
		TaskList taskList = manager.getTaskList();
		taskList.addTask(task1);
		taskList.addTask(task2);
		assertNull(taskList.getActiveTask());

		manager.activateTask(task2);
		assertEquals(task2, taskList.getActiveTask());

		manager.deactivateAllTasks();
		assertNull(taskList.getActiveTask());
	}

	public void testMarkTaskRead() {
		String repositoryUrl = "http://mylyn.eclipse.org/bugs222";
		MockRepositoryTask task1 = new MockRepositoryTask(repositoryUrl, "1");
		MockRepositoryTask task2 = new MockRepositoryTask(repositoryUrl, "2");
		task1.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
		task2.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
		List<AbstractTaskContainer> elements = new ArrayList<AbstractTaskContainer>();
		elements.add(task1);
		elements.add(task2);
		MarkTaskReadAction readAction = new MarkTaskReadAction(elements);
		readAction.run();
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task1.getSynchronizationState());
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task2.getSynchronizationState());

		manager.getTaskList().reset();
		MockRepositoryTask hit1 = new MockRepositoryTask("1");
		MockRepositoryTask hit2 = new MockRepositoryTask("2");
		MockRepositoryQuery query = new MockRepositoryQuery("summary");
		manager.getTaskList().addQuery(query);
		manager.getTaskList().addTask(hit1, query);
		manager.getTaskList().addTask(hit2, query);

		elements.clear();
		elements.add(query);
		readAction = new MarkTaskReadAction(elements);
		readAction.run();
		assertEquals(2, query.getChildren().size());
		for (AbstractTaskContainer element : query.getChildren()) {
			if (element instanceof MockRepositoryTask) {
				MockRepositoryTask mockTask = (MockRepositoryTask) element;
				assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, mockTask.getSynchronizationState());
			}
		}

	}

	public void testMarkUnRead() {
		String repositoryUrl = "http://mylyn.eclipse.org/bugs222";
		MockRepositoryTask task1 = new MockRepositoryTask(repositoryUrl, "1");
		MockRepositoryTask task2 = new MockRepositoryTask(repositoryUrl, "2");
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task1.getSynchronizationState());
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task2.getSynchronizationState());
		List<AbstractTaskContainer> elements = new ArrayList<AbstractTaskContainer>();
		elements.add(task1);
		elements.add(task2);
		MarkTaskUnreadAction unreadAction = new MarkTaskUnreadAction(elements);
		unreadAction.run();
		assertEquals(RepositoryTaskSyncState.INCOMING, task1.getSynchronizationState());
		assertEquals(RepositoryTaskSyncState.INCOMING, task2.getSynchronizationState());

		manager.getTaskList().reset();
		MockRepositoryTask hit1 = new MockRepositoryTask("1");
		MockRepositoryTask hit2 = new MockRepositoryTask("2");
		MockRepositoryQuery query = new MockRepositoryQuery("summary");
		manager.getTaskList().addQuery(query);
		manager.getTaskList().addTask(hit1, query);
		manager.getTaskList().addTask(hit2, query);

		elements.clear();
		elements.add(query);
		MarkTaskReadAction readAction = new MarkTaskReadAction(elements);
		readAction.run();
		assertEquals(2, query.getChildren().size());
		for (AbstractTaskContainer element : query.getChildren()) {
			if (element instanceof MockRepositoryTask) {
				MockRepositoryTask mockTask = (MockRepositoryTask) element;
				assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, mockTask.getSynchronizationState());
			} else {
				fail();
			}
		}

		unreadAction = new MarkTaskUnreadAction(elements);
		unreadAction.run();
		assertEquals(2, query.getChildren().size());
		for (AbstractTaskContainer element : query.getChildren()) {
			if (element instanceof MockRepositoryTask) {
				MockRepositoryTask mockTask = (MockRepositoryTask) element;
				assertEquals(RepositoryTaskSyncState.INCOMING, mockTask.getSynchronizationState());
			} else {
				fail();
			}
		}
	}
}
