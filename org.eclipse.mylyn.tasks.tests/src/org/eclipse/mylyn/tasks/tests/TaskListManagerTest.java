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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.RefactorRepositoryUrlOperation;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockAttributeFactory;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Mik Kersten
 */
public class TaskListManagerTest extends TestCase {

	private TaskListManager manager;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		manager = TasksUiPlugin.getTaskListManager();
		for (TaskRepository repository : TasksUiPlugin.getRepositoryManager().getAllRepositories()) {
			TasksUiPlugin.getRepositoryManager().removeRepository(repository,
					TasksUiPlugin.getDefault().getRepositoriesFilePath());
		}
		manager.resetTaskList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
//		manager.readExistingOrCreateNewList();
		TasksUiPlugin.getExternalizationManager().saveNow(new NullProgressMonitor());
		TasksUiPlugin.getDefault().reloadDataDirectory();

		repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TasksUiPlugin.getRepositoryManager().removeRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
		manager.resetTaskList();
		TasksUiPlugin.getExternalizationManager().saveNow(null);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
	}

	public void testUncategorizedTasksNotLost() throws CoreException {
		MockRepositoryQuery query = new MockRepositoryQuery("Test");
		manager.getTaskList().addQuery(query);
		MockTask task = new MockTask("1");
		manager.getTaskList().addTask(task, query);
		manager.getTaskList().addTask(task, manager.getTaskList().getDefaultCategory());
		assertTrue(query.contains(task.getHandleIdentifier()));
		assertTrue(manager.getTaskList().getDefaultCategory().contains(task.getHandleIdentifier()));

		TasksUiPlugin.getExternalizationManager().requestSave();
		manager.resetTaskList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertFalse(manager.getTaskList().getDefaultCategory().contains(task.getHandleIdentifier()));
		TasksUiPlugin.getDefault().reloadDataDirectory();

		assertTrue(manager.getTaskList().getDefaultCategory().contains(task.getHandleIdentifier()));

	}

	public void testQueryAndCategoryNameClash() {
		TaskCategory category = new TaskCategory("TestClash");
		manager.getTaskList().addCategory(category);
		assertTrue(manager.getTaskList().getCategories().contains(category));
		assertEquals(2, manager.getTaskList().getCategories().size());

		MockRepositoryQuery query = new MockRepositoryQuery("TestClash");
		manager.getTaskList().addQuery(query);
		assertTrue(manager.getTaskList().getCategories().contains(category));
		assertEquals(2, manager.getTaskList().getCategories().size());

		manager.getTaskList().deleteCategory(category);
	}

	public void testUniqueTaskID() {
		LocalTask task1 = TasksUiInternal.createNewLocalTask("label");
		manager.getTaskList().addTask(task1);
		LocalTask task2 = TasksUiInternal.createNewLocalTask("label");
		manager.getTaskList().addTask(task2);
		assertEquals(2, manager.getTaskList().getLastLocalTaskId());
		manager.getTaskList().deleteTask(task2);
		LocalTask task3 = TasksUiInternal.createNewLocalTask("label");
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
		ITask task4 = TasksUiInternal.createNewLocalTask("label");
		assertTrue(task4.getHandleIdentifier() + " should end with 4", task4.getHandleIdentifier().endsWith("4"));
	}

	public void testSingleTaskDeletion() {
		MockTask task = new MockTask("1");
		task.setLastReadTimeStamp("now");
		manager.getTaskList().addTask(task,
				manager.getTaskList().getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		manager.getTaskList().addTask(task,
				manager.getTaskList().getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));
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

	private void runRepositoryUrlOperation(String oldUrl, String newUrl) {
		try {
			new RefactorRepositoryUrlOperation(oldUrl, newUrl).run(new NullProgressMonitor());
		} catch (Exception e) {
			fail();
		}
	}

	public void testMigrateTaskContextFiles() throws IOException {
		File fileA = ContextCore.getContextManager().getFileForContext("http://a-1");
		fileA.createNewFile();
		fileA.deleteOnExit();
		assertTrue(fileA.exists());
		runRepositoryUrlOperation("http://a", "http://b");
		File fileB = ContextCore.getContextManager().getFileForContext("http://b-1");
		assertTrue(fileB.exists());
		assertFalse(fileA.exists());
	}

	public void testMigrateQueryUrlHandles() {
		RepositoryQuery query = new MockRepositoryQuery("mquery");
		query.setRepositoryUrl("http://foo.bar");
		query.setUrl("http://foo.bar/b");
		manager.getTaskList().addQuery(query);
		assertTrue(manager.getTaskList().getRepositoryQueries("http://foo.bar").size() > 0);
		runRepositoryUrlOperation("http://foo.bar", "http://bar.baz");
		assertTrue(manager.getTaskList().getRepositoryQueries("http://foo.bar").size() == 0);
		assertTrue(manager.getTaskList().getRepositoryQueries("http://bar.baz").size() > 0);
		IRepositoryQuery changedQuery = manager.getTaskList().getRepositoryQueries("http://bar.baz").iterator().next();
		assertEquals("http://bar.baz/b", changedQuery.getUrl());
	}

	public void testMigrateQueryHandles() {
		RepositoryQuery query = new MockRepositoryQuery("mquery");
		query.setRepositoryUrl("http://a");
		manager.getTaskList().addQuery(query);
		runRepositoryUrlOperation("http://a", "http://b");
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
// runRepositoryUrlOperation("http://a", "http://b");
// assertNotNull(manager.getTaskList().getQueryHit("http://b-123"));
// assertEquals("http://b-123", hit.getHandleIdentifier());
// }

	public void testMigrateTaskHandles() {
		AbstractTask task = new MockTask("http://a", "123");
		AbstractTask task2 = new MockTask("http://other", "other");
		manager.getTaskList().addTask(task);
		manager.getTaskList().addTask(task2);

		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(), task.getConnectorKind(),
				task.getRepositoryUrl(), task.getTaskId(), task.getTaskKind());
		TasksUiPlugin.getTaskDataStorageManager().setNewTaskData(taskData);
		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId()));

		RepositoryTaskData taskData2 = new RepositoryTaskData(new MockAttributeFactory(), task2.getConnectorKind(),
				task2.getRepositoryUrl(), task2.getTaskId(), task2.getTaskKind());
		taskData2.setNewComment("TEST");
		TasksUiPlugin.getTaskDataStorageManager().setNewTaskData(taskData2);
		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task2.getRepositoryUrl(),
				task2.getTaskId()));
		assertEquals("TEST", TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task2.getRepositoryUrl(),
				task2.getTaskId()).getNewComment());

		runRepositoryUrlOperation("http://a", "http://b");
		assertNull(manager.getTaskList().getTask("http://a-123"));
		assertNotNull(manager.getTaskList().getTask("http://b-123"));
		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData("http://b", "123"));
		RepositoryTaskData otherData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
				task2.getRepositoryUrl(), task2.getTaskId());
		assertNotNull(otherData);
		assertEquals("TEST", otherData.getNewComment());
	}

	public void testMigrateTaskHandlesWithExplicitSet() {
		AbstractTask task = new MockTask("http://a", "123");
		task.setUrl("http://a/task/123");
		manager.getTaskList().addTask(task);
		runRepositoryUrlOperation("http://a", "http://b");
		assertNull(manager.getTaskList().getTask("http://a-123"));
		assertNotNull(manager.getTaskList().getTask("http://b-123"));
		assertEquals("http://b/task/123", task.getUrl());
	}

	public void testRefactorMetaContextHandles() {
		String firstUrl = "http://repository1.com/bugs";
		String secondUrl = "http://repository2.com/bugs";
		AbstractTask task1 = new MockTask(firstUrl, "1");
		AbstractTask task2 = new MockTask(firstUrl, "2");
		manager.getTaskList().addTask(task1);
		manager.getTaskList().addTask(task2);
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.MINUTE, 5);

		Calendar startDate2 = Calendar.getInstance();
		startDate2.add(Calendar.MINUTE, 15);
		Calendar endDate2 = Calendar.getInstance();
		endDate2.add(Calendar.MINUTE, 25);

		ContextCore.getContextManager().resetActivityHistory();
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertEquals(0, metaContext.getInteractionHistory().size());

		ContextCore.getContextManager().processActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.ATTENTION,
						IInteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task1.getHandleIdentifier(),
						"origin", null, IInteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, startDate.getTime(),
						endDate.getTime()));

		ContextCore.getContextManager().processActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.ATTENTION,
						IInteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task2.getHandleIdentifier(),
						"origin", null, IInteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, startDate2.getTime(),
						endDate2.getTime()));

		assertEquals(2, metaContext.getInteractionHistory().size());
		assertEquals(60 * 1000 * 5, TasksUiPlugin.getTaskActivityManager().getElapsedTime(task1));
		assertEquals(2 * 60 * 1000 * 5, TasksUiPlugin.getTaskActivityManager().getElapsedTime(task2));
		runRepositoryUrlOperation(firstUrl, secondUrl);
		metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertEquals(2, metaContext.getInteractionHistory().size());
		assertEquals(60 * 1000 * 5, TasksUiPlugin.getTaskActivityManager().getElapsedTime(new MockTask(secondUrl, "1")));
		assertEquals(2 * 60 * 1000 * 5, TasksUiPlugin.getTaskActivityManager().getElapsedTime(
				new MockTask(secondUrl, "2")));
		assertEquals(secondUrl + "-1", metaContext.getInteractionHistory().get(0).getStructureHandle());
	}

	public void testIsActiveToday() {
		AbstractTask task = new LocalTask("1", "task-1");
		assertFalse(TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task));

		task.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday());
		assertTrue(TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task));

		task.setReminded(true);
		assertTrue(TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task));
		task.setReminded(true);

//		Calendar inAnHour = Calendar.getInstance();
//		inAnHour.set(Calendar.HOUR_OF_DAY, inAnHour.get(Calendar.HOUR_OF_DAY) + 1);
//		inAnHour.getTime();
//		task.setScheduledForDate(inAnHour.getTime());
//		Calendar tomorrow = Calendar.getInstance();
//		TaskActivityUtil.snapToNextDay(tomorrow);
//		assertEquals(-1, inAnHour.compareTo(tomorrow));
//		assertTrue(TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task));
	}

	public void testScheduledForToday() {
		AbstractTask task = new LocalTask("1", "task-1");
		task.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday());
		assertTrue(TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task));
		task.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday().next());
		assertFalse(TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task));
	}

	public void testSchedulePastEndOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		TaskActivityUtil.snapForwardNumDays(calendar, 1);
		assertEquals("Should be October", Calendar.OCTOBER, calendar.get(Calendar.MONTH));
	}

	public void testIsCompletedToday() {
		ITask task = new LocalTask("1", "task 1");
		task.setCompletionDate(new Date());
		assertTrue(TasksUiPlugin.getTaskActivityManager().isCompletedToday(task));

		MockTask mockTask = new MockTask("1");
		mockTask.setOwner("unknown");
		manager.getTaskList().addTask(mockTask);
		mockTask.setCompletionDate(new Date());
		assertFalse("completed: " + mockTask.getCompletionDate(), TasksUiPlugin.getTaskActivityManager()
				.isCompletedToday(mockTask));

		mockTask = new MockTask("2");
		manager.getTaskList().addTask(mockTask);
		mockTask.setCompletionDate(new Date());
		repository.setAuthenticationCredentials("testUser", "testPassword");
		mockTask.setOwner("testUser");
		assertTrue(TasksUiPlugin.getTaskActivityManager().isCompletedToday(mockTask));

	}

	public void testMoveCategories() {
//		assertEquals(0, manager.getTaskList()
//				.getOrphanContainer(LocalRepositoryConnector.REPOSITORY_URL)
//				.getChildren()
//				.size());

		assertTrue(manager.getTaskList().getDefaultCategory().isEmpty());

		AbstractTask task1 = new LocalTask("t1", "t1");

		TaskCategory cat1 = new TaskCategory("cat1");
		manager.getTaskList().addCategory(cat1);
		TaskCategory cat2 = new TaskCategory("cat2");
		manager.getTaskList().addCategory(cat2);

		manager.getTaskList().addTask(task1, cat1);
		assertEquals(1, manager.getTaskList().getContainerForHandle("cat1").getChildren().size());
		assertEquals(0, manager.getTaskList().getContainerForHandle("cat2").getChildren().size());

		manager.getTaskList().addTask(task1, cat2);
		assertEquals(0, manager.getTaskList().getContainerForHandle("cat1").getChildren().size());
		assertEquals(1, manager.getTaskList().getContainerForHandle("cat2").getChildren().size());
	}

	public void testMoveToRoot() {
//		assertEquals(0, manager.getTaskList()
//				.getOrphanContainer(LocalRepositoryConnector.REPOSITORY_URL)
//				.getChildren()
//				.size());
		assertTrue(manager.getTaskList().getDefaultCategory().isEmpty());

		AbstractTask task1 = new LocalTask("t1", "t1");
		manager.getTaskList().addTask(task1,
				manager.getTaskList().getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(manager.getTaskList().getDefaultCategory(), TaskCategory.getParentTaskCategory(task1));

		TaskCategory cat1 = new TaskCategory("c1");
		manager.getTaskList().addCategory(cat1);

		manager.getTaskList().addTask(task1, cat1);
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(cat1, TaskCategory.getParentTaskCategory(task1));

		manager.getTaskList().addTask(task1,
				manager.getTaskList().getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(0, cat1.getChildren().size());
		assertEquals(manager.getTaskList().getDefaultCategory(), TaskCategory.getParentTaskCategory(task1));
	}

	public void testCategoryPersistance() {
		MockTask task = new MockTask("1");
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		manager.getTaskList().addTask(task, category);
		assertNotNull(manager.getTaskList());
		assertEquals(2, manager.getTaskList().getCategories().size());

		manager.saveTaskList();
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals("" + manager.getTaskList().getCategories(), 2, manager.getTaskList().getCategories().size());
		assertEquals(1, manager.getTaskList().getAllTasks().size());
	}

	public void testDeleteCategory() {
		assertNotNull(manager.getTaskList());
		assertEquals(1, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		manager.getTaskList().deleteCategory(category);
		assertEquals(1, manager.getTaskList().getCategories().size());
	}

	public void testDeleteCategoryMovesTasksToRoot() {
		AbstractTask task = new MockTask("delete");
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		manager.getTaskList().addTask(task, category);
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		manager.getTaskList().deleteCategory(category);
		manager.getTaskList().getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL);
	}

	public void testRenameCategory() {

		assertNotNull(manager.getTaskList());

		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		String newDesc = "newDescription";
		manager.getTaskList().renameContainer(category, newDesc);
		AbstractTaskCategory container = manager.getTaskList().getContainerForHandle(newDesc);
		assertNotNull(container);
		assertEquals(newDesc, container.getSummary());
		manager.getTaskList().deleteCategory(container);
		assertEquals(1, manager.getTaskList().getCategories().size());
	}

	public void testDeleteCategoryAfterRename() {
		String newDesc = "newDescription";
		assertNotNull(manager.getTaskList());
		assertEquals(1, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		manager.getTaskList().renameContainer(category, newDesc);
		manager.getTaskList().deleteCategory(category);
		assertEquals(1, manager.getTaskList().getCategories().size());
	}

	public void testCreateSameCategoryName() {
		assertNotNull(manager.getTaskList());
		assertEquals(1, manager.getTaskList().getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		TaskCategory category2 = new TaskCategory("cat");
		manager.getTaskList().addCategory(category2);
		assertEquals(2, manager.getTaskList().getCategories().size());
		ITaskElement container = manager.getTaskList().getContainerForHandle("cat");
		assertEquals(container, category);
	}

	public void testDeleteRootTask() {
		AbstractTask task = new LocalTask("1", "label");
		manager.getTaskList().addTask(task);
		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
	}

	public void testDeleteFromCategory() {
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		//assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(1, manager.getTaskList().getCategories().size());

		AbstractTask task = new LocalTask("1", "label");
		TaskCategory category = new TaskCategory("handleAndDescription");
		manager.getTaskList().addTask(task);
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());

		manager.getTaskList().addCategory(category);
		manager.getTaskList().addTask(task, category);
		assertEquals(2, manager.getTaskList().getCategories().size());
		assertEquals(1, category.getChildren().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(1, manager.getTaskList().getAllTasks().size());

		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
		assertEquals(0, category.getChildren().size());
	}

	public void testDeleteRepositoryTask() {
		String repositoryUrl = "http://somewhere.com";
		repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, repositoryUrl);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		MockTask task = new MockTask(repositoryUrl, "1");
		TaskList taskList = TasksUiPlugin.getTaskList();
		taskList.addTask(task, manager.getTaskList().getDefaultCategory());
		MockRepositoryQuery query = new MockRepositoryQuery("query");
		taskList.addQuery(query);
		taskList.addTask(task, query);
		assertEquals(1, taskList.getAllTasks().size());
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		taskList.deleteTask(task);
		assertEquals(0, taskList.getAllTasks().size());
		assertEquals(0, manager.getTaskList().getDefaultCategory().getChildren().size());
	}

	public void testCreate() {
		MockTask repositoryTask = new MockTask("1");
		repositoryTask.setLastReadTimeStamp("now");
		manager.getTaskList().addTask(repositoryTask, manager.getTaskList().getDefaultCategory());
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());
		manager.saveTaskList();

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getAllTasks().size());
	}

	public void testCreateAndMove() throws CoreException {
		MockTask repositoryTask = new MockTask("1");
		repositoryTask.setLastReadTimeStamp("now");
		manager.getTaskList().addTask(repositoryTask);
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		TasksUiPlugin.getExternalizationManager().requestSave();
		TasksUiPlugin.getDefault().reloadDataDirectory();
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		assertEquals(1, manager.getTaskList()
				.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL)
				.getChildren()
				.size());

	}

	public void testArchiveRepositoryTaskExternalization() {
		MockTask repositoryTask = new MockTask("1");
		repositoryTask.setLastReadTimeStamp("now");
		manager.getTaskList().addTask(repositoryTask);
		assertEquals(1, manager.getTaskList()
				.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL)
				.getChildren()
				.size());
		manager.saveTaskList();

		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList()
				.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL)
				.getChildren()
				.size());
	}

	public void testRepositoryTasksAndCategoriesMultiRead() {
		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.getTaskList().addCategory(cat1);

		MockTask reportInCat1 = new MockTask("123");
		manager.getTaskList().addTask(reportInCat1, cat1);
		assertEquals(cat1, TaskCategory.getParentTaskCategory(reportInCat1));

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
			ITaskElement readCat1 = iterator.next();
			if (cat1.equals(readCat1)) {
				found = true;
				assertEquals(1, readCat1.getChildren().size());
			}
		}
		if (!found) {
			fail(" Category not found afer tasklist read");
		}

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
			ITaskElement readCat1 = iterator.next();
			if (cat1.equals(readCat1)) {
				found = true;
				assertEquals(1, readCat1.getChildren().size());
			}
		}
		if (!found) {
			fail(" Category not found afer tasklist read");
		}
	}

	public void testSubTaskExternalization() {
		Set<AbstractTask> rootTasks = new HashSet<AbstractTask>();
		AbstractTask task1 = new LocalTask("1", "task1");
		manager.getTaskList().addTask(task1);
		rootTasks.add(task1);

		AbstractTask sub2 = new LocalTask("2", "sub 2");
		manager.getTaskList().addTask(sub2, task1);
		assertEquals(1, task1.getChildren().size());
		assertTrue(rootTasks.containsAll(manager.getTaskList().getDefaultCategory().getChildren()));

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		assertNotNull(manager.getTaskList());

		// XXX: This should pass once sub tasks are handled properly
//		assertTrue(rootTasks.containsAll(manager.getTaskList().getOrphanContainer(
//				LocalRepositoryConnector.REPOSITORY_URL).getChildren()));

		Collection<ITask> readList = manager.getTaskList().getDefaultCategory().getChildren();
		for (ITask task : readList) {
			if (task.equals(task1)) {
				assertEquals(task1.getSummary(), task.getSummary());
				assertEquals(1, task.getChildren().size());
			}
		}
	}

	public void testCreationAndExternalization() throws CoreException {
		Set<AbstractTask> rootTasks = new HashSet<AbstractTask>();
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		rootTasks.add(task1);
		assertEquals(1, manager.getTaskList().getAllTasks().size());

		AbstractTask sub1 = TasksUiInternal.createNewLocalTask("sub 1");
		manager.getTaskList().addTask(sub1, task1);
		assertEquals(4, manager.getTaskList().getRootElements().size());

		//manager.getTaskList().moveToContainer(sub1, manager.getTaskList().getArchiveContainer());

		AbstractTask task2 = TasksUiInternal.createNewLocalTask("task 2");
		rootTasks.add(task2);
		assertEquals(3, manager.getTaskList().getAllTasks().size());

		Set<TaskCategory> categories = new HashSet<TaskCategory>();
		Set<AbstractTask> cat1Contents = new HashSet<AbstractTask>();
		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.getTaskList().addCategory(cat1);
		categories.add(cat1);
		assertEquals(5, manager.getTaskList().getRootElements().size());

		AbstractTask task3 = TasksUiInternal.createNewLocalTask("task 3");
		manager.getTaskList().addTask(task3, cat1);
		cat1Contents.add(task3);
		assertEquals(4, manager.getTaskList().getAllTasks().size());
		assertEquals(cat1, TaskCategory.getParentTaskCategory(task3));
		AbstractTask sub2 = TasksUiInternal.createNewLocalTask("sub 2");
		assertEquals(5, manager.getTaskList().getAllTasks().size());
		manager.getTaskList().addTask(sub2, task3);
		//manager.getTaskList().moveToContainer(sub2, manager.getTaskList().getArchiveContainer());

		AbstractTask task4 = TasksUiInternal.createNewLocalTask("task 4");
		manager.getTaskList().addTask(task4, cat1);
		cat1Contents.add(task4);
		assertEquals(6, manager.getTaskList().getAllTasks().size());

		MockTask reportInCat1 = new MockTask("123");
		manager.getTaskList().addTask(reportInCat1, cat1);
		assertEquals(cat1, TaskCategory.getParentTaskCategory(reportInCat1));
		cat1Contents.add(reportInCat1);
		assertEquals(7, manager.getTaskList().getAllTasks().size());

		assertEquals(5, manager.getTaskList().getRootElements().size());

		TasksUiPlugin.getExternalizationManager().requestSave();
		TasksUiPlugin.getDefault().reloadDataDirectory();

		Collection<ITask> readList = manager.getTaskList().getDefaultCategory().getChildren();
		for (ITask task : readList) {
			if (task.equals(task1)) {
				assertEquals(task1.getSummary(), task.getSummary());
				assertEquals(1, task.getChildren().size());
			}
		}

		Set<AbstractTaskCategory> readCats = manager.getTaskList().getTaskContainers();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));
		Iterator<AbstractTaskCategory> iterator = readCats.iterator();
		boolean found = false;
		while (iterator.hasNext()) {
			ITaskElement readCat1 = iterator.next();
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

	public void testExternalizationOfHandlesWithDash() throws CoreException {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		manager.getTaskList().addTask(task1, manager.getTaskList().getDefaultCategory());

		TasksUiPlugin.getExternalizationManager().requestSave();
		TasksUiPlugin.getDefault().reloadDataDirectory();
		assertTrue(manager.getTaskList().getDefaultCategory().getChildren().contains(task1));
	}

	public void testgetQueriesAndHitsForHandle() {
		TaskList taskList = manager.getTaskList();

		MockTask hit1 = new MockTask("1");
		MockTask hit2 = new MockTask("2");
		MockTask hit3 = new MockTask("3");

		MockTask hit1twin = new MockTask("1");
		MockTask hit2twin = new MockTask("2");
		MockTask hit3twin = new MockTask("3");

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

		Set<AbstractTaskContainer> queriesReturned = hit1.getParentContainers();
		assertNotNull(queriesReturned);
		assertEquals(2, queriesReturned.size());
		assertTrue(queriesReturned.contains(query1));
		assertTrue(queriesReturned.contains(query2));
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
		ITaskList taskList = manager.getTaskList();

		MockTask hit1 = new MockTask("1");
		MockTask hit2 = new MockTask("2");
		MockTask hit3 = new MockTask("3");

		MockTask hit1twin = new MockTask("1");
		MockTask hit2twin = new MockTask("2");
		MockTask hit3twin = new MockTask("3");

		MockRepositoryQuery query1 = new MockRepositoryQuery("query1");
		taskList.addQuery(query1);

		taskList.addTask(hit1, query1);
		taskList.addTask(hit2, query1);
		taskList.addTask(hit3, query1);

		taskList.addTask(hit1twin, query1);
		taskList.addTask(hit2twin, query1);
		taskList.addTask(hit3twin, query1);

		assertEquals(3, query1.getChildren().size());
		for (ITask child : query1.getChildren()) {
			taskList.removeFromContainer(query1, child);
		}
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
		for (ITask hit : query1.getChildren()) {
			if (hit.equals(hit1twin)) {
				assertTrue(((AbstractTask) hit).isNotified());
			} else {
				assertFalse(((AbstractTask) hit).isNotified());
			}
		}
	}

	public void testgetRepositoryTasks() {

		String repositoryUrl = "https://bugs.eclipse.org/bugs";

		String bugNumber = "106939";

		MockTask task1 = new MockTask(repositoryUrl, bugNumber);
		manager.getTaskList().addTask(task1);

		MockTask task2 = new MockTask("https://unresolved", bugNumber);
		manager.getTaskList().addTask(task2);

		TaskList taskList = manager.getTaskList();
		assertEquals(2, taskList.getAllTasks().size());
		Set<ITask> tasksReturned = taskList.getTasks(repositoryUrl);
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
		assertNull(manager.getActiveTask());

		manager.activateTask(task2);
		assertEquals(task2, manager.getActiveTask());

		manager.deactivateAllTasks();
		assertNull(manager.getActiveTask());
	}

	public void testMarkTaskRead() {
		// TODO reimplement
//		String repositoryUrl = "http://mylyn.eclipse.org/bugs222";
//		MockTask task1 = new MockTask(repositoryUrl, "1");
//		MockTask task2 = new MockTask(repositoryUrl, "2");
//		task1.setSynchronizationState(SynchronizationState.INCOMING);
//		task2.setSynchronizationState(SynchronizationState.INCOMING);
//		List<ITaskElement> elements = new ArrayList<ITaskElement>();
//		elements.add(task1);
//		elements.add(task2);
//		MarkTaskReadAction readAction = new MarkTaskReadAction(elements);
//		readAction.run();
//		assertEquals(SynchronizationState.SYNCHRONIZED, task1.getSynchronizationState());
//		assertEquals(SynchronizationState.SYNCHRONIZED, task2.getSynchronizationState());
//
//		manager.getTaskList().reset();
//		MockTask hit1 = new MockTask("1");
//		MockTask hit2 = new MockTask("2");
//		MockRepositoryQuery query = new MockRepositoryQuery("summary");
//		manager.getTaskList().addQuery(query);
//		manager.getTaskList().addTask(hit1, query);
//		manager.getTaskList().addTask(hit2, query);
//
//		elements.clear();
//		elements.add(query);
//		readAction = new MarkTaskReadAction(elements);
//		readAction.run();
//		assertEquals(2, query.getChildren().size());
//		for (ITaskElement element : query.getChildren()) {
//			if (element instanceof MockTask) {
//				MockTask mockTask = (MockTask) element;
//				assertEquals(SynchronizationState.SYNCHRONIZED, mockTask.getSynchronizationState());
//			}
//		}

	}

	public void testMarkUnRead() {
		// TODO reimplement
//		String repositoryUrl = "http://mylyn.eclipse.org/bugs222";
//		MockTask task1 = new MockTask(repositoryUrl, "1");
//		MockTask task2 = new MockTask(repositoryUrl, "2");
//		assertEquals(SynchronizationState.SYNCHRONIZED, task1.getSynchronizationState());
//		assertEquals(SynchronizationState.SYNCHRONIZED, task2.getSynchronizationState());
//		List<ITaskElement> elements = new ArrayList<ITaskElement>();
//		elements.add(task1);
//		elements.add(task2);
//		MarkTaskUnreadAction unreadAction = new MarkTaskUnreadAction(elements);
//		unreadAction.run();
//		assertEquals(SynchronizationState.INCOMING, task1.getSynchronizationState());
//		assertEquals(SynchronizationState.INCOMING, task2.getSynchronizationState());
//
//		manager.getTaskList().reset();
//		MockTask hit1 = new MockTask("1");
//		MockTask hit2 = new MockTask("2");
//		MockRepositoryQuery query = new MockRepositoryQuery("summary");
//		manager.getTaskList().addQuery(query);
//		manager.getTaskList().addTask(hit1, query);
//		manager.getTaskList().addTask(hit2, query);
//
//		elements.clear();
//		elements.add(query);
//		MarkTaskReadAction readAction = new MarkTaskReadAction(elements);
//		readAction.run();
//		assertEquals(2, query.getChildren().size());
//		for (ITaskElement element : query.getChildren()) {
//			if (element instanceof MockTask) {
//				MockTask mockTask = (MockTask) element;
//				assertEquals(SynchronizationState.SYNCHRONIZED, mockTask.getSynchronizationState());
//			} else {
//				fail();
//			}
//		}
//
//		unreadAction = new MarkTaskUnreadAction(elements);
//		unreadAction.run();
//		assertEquals(2, query.getChildren().size());
//		for (ITaskElement element : query.getChildren()) {
//			if (element instanceof MockTask) {
//				MockTask mockTask = (MockTask) element;
//				assertEquals(SynchronizationState.INCOMING, mockTask.getSynchronizationState());
//			} else {
//				fail();
//			}
//		}
	}

	public void testQueryHitsNotDropped() {
		MockTask task1 = new MockTask("1");
		MockTask task2 = new MockTask("2");
		task1.setLastReadTimeStamp("today");
		task2.setLastReadTimeStamp("today");
		MockRepositoryQuery query = new MockRepositoryQuery("summary");
		manager.getTaskList().addQuery(query);
		manager.getTaskList().addTask(task1, query);
		manager.getTaskList().addTask(task2, query);
		//assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(2, query.getChildren().size());
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				MockRepositoryConnector.REPOSITORY_URL);
		Set<RepositoryQuery> queries = new HashSet<RepositoryQuery>();
		queries.add(query);
		TasksUiInternal.synchronizeQueries(new MockRepositoryConnector(), repository, queries, null, true);
		//assertEquals(2, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(0, query.getChildren().size());
	}

}
