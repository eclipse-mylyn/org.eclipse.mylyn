/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class OrphanedTasksTest extends TestCase {

	private TaskList taskList;

	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		assertTrue(taskList.getDefaultCategory().isEmpty());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * y New local tasks should automatically be created in the Local orphaned folder
	 */
	public void testAddLocalTask() {
		Set<AbstractTask> tasks = TasksUiPlugin.getTaskListManager().getTaskList().getRepositoryTasks(
				LocalRepositoryConnector.REPOSITORY_URL);
		assertTrue(tasks.isEmpty());
		AbstractTask localTask = TasksUiPlugin.getTaskListManager().createNewLocalTask("Task 1");
		assertNotNull(localTask);
		assertEquals(1, localTask.getParentContainers().size());
	}

	/**
	 * When a local tasks is removed from a category it should be placed in the Uncategorized folder
	 */
	public void testRemoveLocalTask() {
		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);
		LocalTask newTask = new LocalTask("" + taskList.getNextLocalTaskId(), "new local task");
		taskList.addTask(newTask, category);
		//assertEquals(1, taskList.getOrphanContainers().size());
		assertEquals(2, taskList.getCategories().size());
		taskList.removeFromCategory(category, newTask);
		//assertEquals(taskList.getOrphanContainers().size(), 1);
		assertFalse(category.contains(newTask.getHandleIdentifier()));
		assertTrue(taskList.getDefaultCategory().contains(newTask.getHandleIdentifier()));
		assertEquals(1, newTask.getParentContainers().size());
		assertEquals(taskList.getDefaultCategory(), TaskCategory.getParentTaskCategory(newTask));
	}

	/**
	 * Local tasks in a removed category should be orphaned.
	 */
	public void testLocalTaskOrphanedInDeletedTaskCategory() {
		assertTrue(taskList.getDefaultCategory().isEmpty());
		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);
		LocalTask newTask = new LocalTask("" + taskList.getNextLocalTaskId(), "new local task");
		taskList.addTask(newTask, category);
		assertTrue(taskList.getDefaultCategory().isEmpty());
		taskList.deleteCategory(category);
		assertTrue(taskList.getDefaultCategory().contains(newTask.getHandleIdentifier()));

	}

	/**
	 * Query removed with task in category, just query removed task remains.
	 */
	public void testTaskRemovedFromQuery() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		mockTask.setLastReadTimeStamp("now");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		taskList.removeFromQuery(mockQuery, mockTask);

		assertFalse(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
	}

	/**
	 * Query removed with task in category, just query removed task remains.
	 */
	public void testTaskRemovedFromQueryButInCategory() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		mockTask.setLastReadTimeStamp("now");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);
		taskList.addTask(mockTask, category);
		assertTrue(category.contains(mockTask.getHandleIdentifier()));
		taskList.removeFromQuery(mockQuery, mockTask);

		assertFalse(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertEquals(2, taskList.getCategories().size());
		assertTrue(category.contains(mockTask.getHandleIdentifier()));

		//* Repository tasks within a removed category that are not in a query should be orphaned.

		taskList.removeFromCategory(category, mockTask);
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
	}

	/**
	 * Repository tasks that exists in a query are not orphaned.
	 */
	public void testRepositoryTaskInDeletedCategory() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);
		assertEquals(1, taskList.getCategories().size());
		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);
		taskList.addTask(mockTask, category);
		assertEquals(2, taskList.getCategories().size());
		assertTrue(taskList.getDefaultCategory().isEmpty());
		taskList.deleteCategory(category);
		assert (taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).isEmpty());
		assertEquals(1, taskList.getCategories().size());
	}

	
	/**
	 * If a task exists in a category and is a query hit
	 * it should not be removed from the category
	 */
	public void testQueryRemovedTaskInCategory() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);
		assertTrue(taskList.getDefaultCategory().isEmpty());
		taskList.moveTask(mockTask, taskList.getDefaultCategory());
		assertEquals(1, taskList.getCategories().size());
		assertFalse(taskList.getDefaultCategory().isEmpty());
		// save tasklist, restore tasklist
		TasksUiPlugin.getTaskListManager().saveTaskList();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().readExistingOrCreateNewList();
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getQueries().size());
		assertFalse(taskList.getDefaultCategory().isEmpty());
		taskList.deleteQuery(mockQuery);
		TasksUiPlugin.getTaskListManager().saveTaskList();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().readExistingOrCreateNewList();
		assertEquals(1, taskList.getCategories().size());
		assertEquals(0, taskList.getQueries().size());
		assertFalse(taskList.getDefaultCategory().isEmpty());
	}
	
	
	/**
	 * Repository tasks that exist in another query are not orphaned
	 */
	public void testRemovalOfTaskInTwoQueries() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query 1");
		MockRepositoryQuery mockQuery2 = new MockRepositoryQuery("mock query 2");
		taskList.addQuery(mockQuery);
		taskList.addQuery(mockQuery2);
		taskList.addTask(mockTask, mockQuery);
		taskList.addTask(mockTask, mockQuery2);

		taskList.removeFromQuery(mockQuery2, mockTask);
		assertTrue(mockQuery2.isEmpty());
		assertFalse(mockQuery.isEmpty());
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).isEmpty());
	}

	/**
	 * Moving an orphan to a Category should result in the task only being present in the target Category
	 */
	public void testMoveLocalTask() {
		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);
		LocalTask newTask = new LocalTask("" + taskList.getNextLocalTaskId(), "new local task");
		taskList.addTask(newTask, null);
		assertTrue(taskList.getDefaultCategory().contains(newTask.getHandleIdentifier()));
		taskList.moveTask(newTask, category);
		assertFalse(taskList.getDefaultCategory().contains(newTask.getHandleIdentifier()));
	}

	public void testAddRepositoryTask() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(mockQuery);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(mockTask, mockQuery);
		Set<AbstractTask> tasks = TasksUiPlugin.getTaskListManager().getTaskList().getRepositoryTasks(
				MockRepositoryConnector.REPOSITORY_URL);
		assertFalse(tasks.isEmpty());
		AbstractRepositoryQuery query = TasksUiPlugin.getTaskListManager().getTaskList().getParentQueries(mockTask).iterator().next();
		assertEquals(mockQuery, query);
		assertFalse(query.isEmpty());
		assertTrue(TasksUiPlugin.getTaskListManager().getTaskList().getOrphanContainer(
				MockRepositoryConnector.REPOSITORY_URL).isEmpty());
	}

	public void testMoveRepositoryTask() {
		TaskList tasklist = TasksUiPlugin.getTaskListManager().getTaskList();
		assertTrue(tasklist.getAllTasks().isEmpty());

		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(mockQuery);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(mockTask, mockQuery);

		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);

		taskList.moveTask(mockTask, category);
		assertTrue(taskList.getDefaultCategory().isEmpty());
		assertTrue(category.contains(mockTask.getHandleIdentifier()));
		assertTrue(mockQuery.contains(mockTask.getHandleIdentifier()));

	}

	public void testRefactorOrphanedHandle() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		mockTask.setLastReadTimeStamp("now");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		taskList.removeFromQuery(mockQuery, mockTask);

		assertFalse(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));

		taskList.refactorRepositoryUrl(MockRepositoryConnector.REPOSITORY_URL, MockRepositoryConnector.REPOSITORY_URL
				+ "new");
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL + "new").contains(
				mockTask.getHandleIdentifier()));
	}

	public void testOrphanedSubtasks() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		MockRepositoryTask mockTask2 = new MockRepositoryTask("2");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(mockQuery);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(mockTask, mockQuery);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(mockTask2, mockTask);

		assertFalse(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		assertFalse(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask2.getHandleIdentifier()));

		assertNotNull(taskList.getTask(mockTask.getHandleIdentifier()));
		assertNotNull(taskList.getTask(mockTask2.getHandleIdentifier()));
		assertNotNull(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL));
		taskList.removeFromQuery(mockQuery, mockTask);
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		assertTrue(mockTask.contains(mockTask2.getHandleIdentifier()));
		taskList.deleteTask(mockTask);
		assertFalse(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		// mockTask2 should be orphaned when the parent task is deleted
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask2.getHandleIdentifier()));
	}

	/**
	 * If a task with subtasks falls out of a query, if its subtasks are subtasks of another task that is still around,
	 * they shouldn't be in the archive.
	 */
	public void testOrphanedSubtaskWithOtherParent() {
		MockRepositoryTask mockTask = new MockRepositoryTask("1");
		MockRepositoryTask mockTask2 = new MockRepositoryTask("2");
		MockRepositoryTask mockTask3 = new MockRepositoryTask("3");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(mockQuery);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(mockTask, mockQuery);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(mockTask2, mockTask);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(mockTask3, mockQuery);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(mockTask2, mockTask3);
		assertFalse(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		assertFalse(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask2.getHandleIdentifier()));
		assertFalse(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask3.getHandleIdentifier()));

		taskList.removeFromQuery(mockQuery, mockTask);
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		assertTrue(mockTask.contains(mockTask2.getHandleIdentifier()));
		assertFalse(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask2.getHandleIdentifier()));

		taskList.removeFromQuery(mockQuery, mockTask3);
		assertTrue(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask3.getHandleIdentifier()));
		assertTrue(mockTask3.contains(mockTask2.getHandleIdentifier()));
		assertFalse(taskList.getOrphanContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask2.getHandleIdentifier()));

	}
}
