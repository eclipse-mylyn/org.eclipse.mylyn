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

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.ui.ScheduledTaskListSynchJob;
import org.eclipse.mylyn.internal.tasks.ui.TaskListSynchronizationScheduler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskReadAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskUnreadAction;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskListElement;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.Task;
import org.eclipse.mylyn.tasks.core.TaskCategory;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.UncategorizedCategory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
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

	public void testUniqueTaskID() {
		LocalTask task1 = manager.createNewLocalTask("label");
		manager.getTaskList().addTask(task1);
		LocalTask task2 = manager.createNewLocalTask("label");
		manager.getTaskList().addTask(task2);
		assertEquals(2, manager.getTaskList().getLastTaskNum());
		manager.getTaskList().deleteTask(task2);
		LocalTask task3 = manager.createNewLocalTask("label");
		manager.getTaskList().addTask(task3);
		assertTrue(task3.getHandleIdentifier() + " should end with 3", task3.getHandleIdentifier().endsWith("3"));
		assertEquals(3, manager.getTaskList().getLastTaskNum());

		assertEquals(2, manager.getTaskList().getAllTasks().size());
		manager.saveTaskList();
		manager.resetTaskList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getLastTaskNum());
		manager.readExistingOrCreateNewList();
		assertEquals(2, manager.getTaskList().getAllTasks().size());
		assertEquals(3, manager.getTaskList().getLastTaskNum());
		Task task4 =manager.createNewLocalTask("label");
		assertTrue(task4.getHandleIdentifier() + " should end with 4", task4.getHandleIdentifier().endsWith("4"));
	}

	public void testSingleTaskDeletion() {
		MockRepositoryTask task = new MockRepositoryTask("1");
		manager.getTaskList().moveToRoot(task);
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
		AbstractRepositoryQuery changedQuery = manager.getTaskList().getRepositoryQueries("http://bar.baz").iterator()
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
		AbstractRepositoryTask task = new MockRepositoryTask("http://a", "123");
		AbstractRepositoryTask task2 = new MockRepositoryTask("http://other", "other");
		manager.getTaskList().addTask(task);
		manager.getTaskList().addTask(task2);

		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(), task.getRepositoryKind(), task
				.getRepositoryUrl(), task.getTaskId(), task.getTaskKind());
		TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(task.getHandleIdentifier(), taskData);
		assertNotNull(TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData(task.getHandleIdentifier()));

		RepositoryTaskData taskData2 = new RepositoryTaskData(new MockAttributeFactory(), task2.getRepositoryKind(),
				task2.getRepositoryUrl(), task2.getTaskId(), task2.getTaskKind());
		taskData2.setNewComment("TEST");
		TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(task2.getHandleIdentifier(), taskData2);
		assertNotNull(TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData(task2.getHandleIdentifier()));

		manager.refactorRepositoryUrl("http://a", "http://b");
		assertNull(manager.getTaskList().getTask("http://a-123"));
		assertNotNull(manager.getTaskList().getTask("http://b-123"));
		assertNotNull(TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData("http://b-123"));
		RepositoryTaskData otherData = TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData(
				task2.getHandleIdentifier());
		assertNotNull(otherData);
		assertEquals("TEST", otherData.getNewComment());
	}

	public void testMigrateTaskHandlesWithExplicitSet() {
		AbstractRepositoryTask task = new MockRepositoryTask("http://a", "123");
		task.setTaskUrl("http://a/task/123");
		manager.getTaskList().addTask(task);
		manager.refactorRepositoryUrl("http://a", "http://b");
		assertNull(manager.getTaskList().getTask("http://a-123"));
		assertNotNull(manager.getTaskList().getTask("http://b-123"));
		assertEquals("http://b/task/123", task.getTaskUrl());
	}

	public void testIsActiveToday() {
		ITask task = new Task("1", "task-1");
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

	public void testScheduledForToday() {
		ITask task = new Task("1", "task-1");
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

	public void testSchedulePastEndOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		manager.setSecheduledIn(calendar, 1);
		assertEquals("Should be October", Calendar.OCTOBER, calendar.get(Calendar.MONTH));
	}

	public void testIsCompletedToday() {
		ITask task = new Task("1", "task 1");
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
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		Task task1 = new Task("t1", "t1");

		TaskCategory cat1 = new TaskCategory("cat1");
		manager.getTaskList().addCategory(cat1);
		TaskCategory cat2 = new TaskCategory("cat2");
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
		Task task1 = new Task("t1", "t1");
		manager.getTaskList().moveToRoot(task1);
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		assertEquals(UncategorizedCategory.HANDLE, task1.getContainer().getHandleIdentifier());

		TaskCategory cat1 = new TaskCategory("c1");
		manager.getTaskList().addCategory(cat1);

		manager.getTaskList().moveToContainer(cat1, task1);
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		assertEquals(cat1, task1.getContainer());

		manager.getTaskList().moveToRoot(task1);
		assertEquals(1, manager.getTaskList().getRootTasks().size());
		assertEquals(0, cat1.getChildren().size());
		assertEquals(UncategorizedCategory.HANDLE, task1.getContainer().getHandleIdentifier());
	}

	public void testEmpty() {
		manager.resetTaskList();
		assertTrue(manager.getTaskList().isEmpty());
		manager.getTaskList().internalAddRootTask(new Task("", ""));
		assertFalse(manager.getTaskList().isEmpty());
	}

	public void testCategoryPersistance() {
		MockRepositoryTask task = new MockRepositoryTask("1");
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		manager.getTaskList().moveToContainer(category, task);
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
		ITask task = new MockRepositoryTask("delete");
		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addTask(task, category);
		manager.getTaskList().addCategory(category);
		assertEquals(0, manager.getTaskList().getUncategorizedCategory().getChildren().size());
		manager.getTaskList().deleteCategory(category);
		assertEquals(1, manager.getTaskList().getUncategorizedCategory().getChildren().size());
	}

	public void testRenameCategory() {

		assertNotNull(manager.getTaskList());

		TaskCategory category = new TaskCategory("cat");
		manager.getTaskList().addCategory(category);
		assertEquals(3, manager.getTaskList().getCategories().size());
		String newDesc = "newDescription";
		manager.getTaskList().renameContainer(category, newDesc);
		AbstractTaskContainer container = manager.getTaskList().getContainerForHandle(newDesc);
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
		ITask task = new Task("task-1", "label");
		manager.getTaskList().addTask(task);
		manager.getTaskList().internalAddRootTask(task);
		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
	}

	public void testDeleteFromCategory() {
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(2, manager.getTaskList().getCategories().size());

		ITask task = new Task("task-1", "label");
		TaskCategory category = new TaskCategory("handleAndDescription");
		manager.getTaskList().addTask(task);
		assertEquals(1, manager.getTaskList().getArchiveContainer().getChildren().size());

		manager.getTaskList().addCategory(category);
		manager.getTaskList().moveToContainer(category, task);
		assertEquals(3, manager.getTaskList().getCategories().size());
		assertEquals(1, category.getChildren().size());
		assertEquals(0, manager.getTaskList().getArchiveContainer().getChildren().size());
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getRootTasks().size());

		manager.getTaskList().deleteTask(task);
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		assertEquals(0, manager.getTaskList().getRootTasks().size());
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
		taskList.moveToRoot(task);
		MockRepositoryQuery query = new MockRepositoryQuery("query");
		taskList.addQuery(query);
		taskList.addTask(task, query);
		assertEquals(1, taskList.getAllTasks().size());
		assertEquals(1, taskList.getRootTasks().size());
		taskList.deleteTask(task);
		assertEquals(0, taskList.getAllTasks().size());
		assertEquals(0, taskList.getRootTasks().size());
	}

	public void testArchiveRepositoryTaskExternalization() {
		MockRepositoryTask repositoryTask = new MockRepositoryTask("1");
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

	public void testRepositoryTasksAndCategoriesMultiRead() {
		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.getTaskList().addCategory(cat1);

		MockRepositoryTask reportInCat1 = new MockRepositoryTask("123");
		manager.getTaskList().moveToContainer(cat1, reportInCat1);
		assertEquals(cat1, reportInCat1.getContainer());

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
		// manager.getTaskList().clear();
		// manager.setTaskList(new TaskList());
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
		// manager.getTaskList().clear();
		// manager.setTaskList(new TaskList());
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
		Task task1 = manager.createNewLocalTask("task 1");
		manager.getTaskList().moveToRoot(task1);
		rootTasks.add(task1);

		Task sub1 = manager.createNewLocalTask("sub 1");
		manager.getTaskList().addTask(sub1, task1);
		manager.getTaskList().moveToContainer(manager.getTaskList().getArchiveContainer(), sub1);

		Task task2 = manager.createNewLocalTask("task 2");
		manager.getTaskList().moveToRoot(task2);
		rootTasks.add(task2);

		Set<TaskCategory> categories = new HashSet<TaskCategory>();
		Set<ITask> cat1Contents = new HashSet<ITask>();
		TaskCategory cat1 = new TaskCategory("Category 1");
		manager.getTaskList().addCategory(cat1);
		categories.add(cat1);
		Task task3 =manager.createNewLocalTask("task 3");
		manager.getTaskList().moveToContainer(cat1, task3);
		cat1Contents.add(task3);
		assertEquals(cat1, task3.getContainer());
		Task sub2 = manager.createNewLocalTask("sub 2");
		manager.getTaskList().addTask(sub2, task3);
		manager.getTaskList().moveToContainer(manager.getTaskList().getArchiveContainer(), sub2);

		Task task4 = manager.createNewLocalTask("task 4");
		manager.getTaskList().moveToContainer(cat1, task4);
		cat1Contents.add(task4);

		MockRepositoryTask reportInCat1 = new MockRepositoryTask("123");
		manager.getTaskList().moveToContainer(cat1, reportInCat1);
		assertEquals(cat1, reportInCat1.getContainer());
		cat1Contents.add(reportInCat1);

		MockRepositoryTask reportInRoot = new MockRepositoryTask("124");
		manager.getTaskList().moveToRoot(reportInRoot);
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
		assertTrue(rootTasks.containsAll(manager.getTaskList().getRootTasks()));

		Set<ITask> readList = manager.getTaskList().getRootTasks();
		for (ITask task : readList) {
			if (task.equals(task1)) {
				assertEquals(task1.getSummary(), task.getSummary());
				assertEquals(1, task.getChildren().size());
			}
			if (task.equals(reportInRoot)) {
				assertEquals(reportInRoot.getSummary(), task.getSummary());
			}
		}

		Set<AbstractTaskContainer> readCats = manager.getTaskList().getTaskContainers();
		assertTrue(manager.getTaskList().getCategories().contains(cat1));
		Iterator<AbstractTaskContainer> iterator = readCats.iterator();
		AbstractTaskContainer readCat1 = iterator.next();
		assertEquals(cat1, readCat1);
		assertEquals(cat1Contents, readCat1.getChildren());
	}

	public void testExternalizationOfHandlesWithDash() {
		Set<ITask> rootTasks = new HashSet<ITask>();

// String handle = AbstractRepositoryTask.getHandle("http://url/repo-location",
// 1);
		Task task1 = manager.createNewLocalTask("task 1");
		manager.getTaskList().moveToRoot(task1);
		rootTasks.add(task1);

		manager.saveTaskList();
		assertNotNull(manager.getTaskList());
		manager.resetTaskList();
		assertTrue(manager.readExistingOrCreateNewList());

		assertNotNull(manager.getTaskList());
		assertEquals(rootTasks, manager.getTaskList().getRootTasks());
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
		assertTrue(ScheduledTaskListSynchJob.getCount() + " smaller than " + counter, ScheduledTaskListSynchJob
				.getCount() >= counter);
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
		
		
		assertEquals(3, query1.getHits().size());

		
		taskList.addTask(hit1twin, query2);
		taskList.addTask(hit2twin, query2);
		taskList.addTask(hit3twin, query2);

		assertEquals(3, query2.getHits().size());


		Set<AbstractRepositoryQuery> queriesReturned = taskList.getQueriesForHandle(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(queriesReturned);
		assertEquals(2, queriesReturned.size());
		assertTrue(queriesReturned.contains(query1));
		assertTrue(queriesReturned.contains(query2));

		Set<String> handles = new HashSet<String>();
		handles.add(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "2"));
		Collection<ITask> hitsReturned = taskList.getTasks(handles);
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
		
		assertEquals(3, query1.getHits().size());
		query1.clear();
		assertEquals(0, query1.getHits().size());
		taskList.addTask(hit1, query1);
		taskList.addTask(hit2, query1);
		assertEquals(2, query1.getHits().size());
		hit1.setNotified(true);
		
		taskList.addTask(hit1twin, query1);
		taskList.addTask(hit2twin, query1);
		taskList.addTask(hit3twin, query1);
		assertEquals(3, query1.getHits().size());
		assertTrue(query1.getHits().contains(hit1twin));
		assertTrue(query1.getHits().contains(hit2twin));
		assertTrue(query1.getHits().contains(hit3twin));
		for (AbstractRepositoryTask hit : query1.getHits()) {
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
		Set<AbstractRepositoryTask> tasksReturned = taskList.getRepositoryTasks(repositoryUrl);
		assertNotNull(tasksReturned);
		assertEquals(1, tasksReturned.size());
		assertTrue(tasksReturned.contains(task1));
	}

	public void testAllTasksDeactivation() {
		Task task1 = new Task("task1", "description1");
		Task task2 = new Task("task2", "description2");
		TaskList taskList = manager.getTaskList();
		taskList.addTask(task1);
		taskList.addTask(task2);
		assertTrue(taskList.getActiveTasks().isEmpty());

		manager.activateTask(task2);
		assertEquals(Collections.singletonList(task2), taskList.getActiveTasks());

		manager.deactivateAllTasks();
		assertTrue(taskList.getActiveTasks().isEmpty());
	}

	public void testMarkTaskRead() {
		String repositoryUrl = "http://mylar.eclipse.org/bugs222";
		MockRepositoryTask task1 = new MockRepositoryTask(repositoryUrl, "1");
		MockRepositoryTask task2 = new MockRepositoryTask(repositoryUrl, "2");
		task1.setSyncState(RepositoryTaskSyncState.INCOMING);
		task2.setSyncState(RepositoryTaskSyncState.INCOMING);
		List<ITaskListElement> elements = new ArrayList<ITaskListElement>();
		elements.add(task1);
		elements.add(task2);
		MarkTaskReadAction readAction = new MarkTaskReadAction(elements);
		readAction.run();
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task1.getSyncState());
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task2.getSyncState());

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
		for (ITaskListElement element : query.getChildren()) {
			if (element instanceof MockRepositoryTask) {
				MockRepositoryTask mockTask = (MockRepositoryTask) element;
				assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, mockTask.getSyncState());
			}
		}

	}

	public void testMarkUnRead() {
		String repositoryUrl = "http://mylar.eclipse.org/bugs222";
		MockRepositoryTask task1 = new MockRepositoryTask(repositoryUrl, "1");
		MockRepositoryTask task2 = new MockRepositoryTask(repositoryUrl, "2");
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task1.getSyncState());
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task2.getSyncState());
		List<ITaskListElement> elements = new ArrayList<ITaskListElement>();
		elements.add(task1);
		elements.add(task2);
		MarkTaskUnreadAction unreadAction = new MarkTaskUnreadAction(elements);
		unreadAction.run();
		assertEquals(RepositoryTaskSyncState.INCOMING, task1.getSyncState());
		assertEquals(RepositoryTaskSyncState.INCOMING, task2.getSyncState());

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
		for (ITaskListElement element : query.getChildren()) {
			if (element instanceof MockRepositoryTask) {
				MockRepositoryTask mockTask = (MockRepositoryTask) element;
				assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, mockTask.getSyncState());
			} else {
				fail();
			}
		}

		unreadAction = new MarkTaskUnreadAction(elements);
		unreadAction.run();
		assertEquals(2, query.getChildren().size());
		for (ITaskListElement element : query.getChildren()) {
			if (element instanceof MockRepositoryTask) {
				MockRepositoryTask mockTask = (MockRepositoryTask) element;
				assertEquals(RepositoryTaskSyncState.INCOMING, mockTask.getSyncState());
			} else {
				fail();
			}
		}
	}
}