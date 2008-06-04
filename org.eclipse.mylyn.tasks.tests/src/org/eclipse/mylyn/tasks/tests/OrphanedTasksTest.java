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

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Rob Elves
 */
public class OrphanedTasksTest extends TestCase {

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		TasksUiPlugin.getTaskListManager().resetTaskList();
		taskList = TasksUiPlugin.getTaskList();
		assertTrue(taskList.getDefaultCategory().isEmpty());

		TaskRepository taskRepository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(taskRepository);
	}

	@Override
	protected void tearDown() throws Exception {
	}

	/**
	 * y New local tasks should automatically be created in the Local orphaned folder
	 */
	public void testAddLocalTask() {
		Set<ITask> tasks = TasksUiPlugin.getTaskList().getTasks(LocalRepositoryConnector.REPOSITORY_URL);
		assertTrue(tasks.isEmpty());
		ITask localTask = TasksUiInternal.createNewLocalTask("Task 1");
		assertNotNull(localTask);
		assertEquals(1, ((AbstractTask) localTask).getParentContainers().size());
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
		taskList.removeFromContainer(category, newTask);
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
		MockTask mockTask = new MockTask("1");
		mockTask.setLastReadTimeStamp("now");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		taskList.removeFromContainer(mockQuery, mockTask);

		assertFalse(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
	}

	/**
	 * Query removed with task in category, just query removed task remains.
	 */
	public void testTaskRemovedFromQueryButInCategory() {
		MockTask mockTask = new MockTask("1");
		mockTask.setLastReadTimeStamp("now");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);
		taskList.addTask(mockTask, category);
		assertTrue(category.contains(mockTask.getHandleIdentifier()));
		taskList.removeFromContainer(mockQuery, mockTask);

		assertFalse(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertEquals(2, taskList.getCategories().size());
		assertTrue(category.contains(mockTask.getHandleIdentifier()));

		//* Repository tasks within a removed category that are not in a query should be orphaned.

		taskList.removeFromContainer(category, mockTask);
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
	}

	/**
	 * Repository tasks that exists in a query are not orphaned.
	 */
	public void testRepositoryTaskInDeletedCategory() {
		MockTask mockTask = new MockTask("1");
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
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).isEmpty());
		assertEquals(1, taskList.getCategories().size());
	}

	/**
	 * Repository tasks in deleted queries are orphaned.
	 */
	public void testRepositoryTaskInDeletedQuery() {
		MockTask mockTask = new MockTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		assertTrue(taskList.getQueries().size() > 0);
		taskList.addTask(mockTask, mockQuery);
		assertTrue(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).isEmpty());
		taskList.deleteQuery(mockQuery);
		assertTrue(taskList.getQueries().size() == 0);
		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).isEmpty());
	}

	/**
	 * If a task exists in a category and is a query hit it should not be removed from the category
	 * 
	 * @throws Exception
	 */
	public void testQueryRemovedTaskInCategory() throws Exception {
		MockTask mockTask = new MockTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);
		assertTrue(taskList.getDefaultCategory().isEmpty());
		taskList.addTask(mockTask, taskList.getDefaultCategory());
		assertEquals(1, taskList.getCategories().size());
		assertFalse(taskList.getDefaultCategory().isEmpty());
		// save tasklist, restore tasklist
		TasksUiPlugin.getExternalizationManager().requestSaveAndWait(true);
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
		MockTask mockTask = new MockTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query 1");
		MockRepositoryQuery mockQuery2 = new MockRepositoryQuery("mock query 2");
		taskList.addQuery(mockQuery);
		taskList.addQuery(mockQuery2);
		taskList.addTask(mockTask, mockQuery);
		taskList.addTask(mockTask, mockQuery2);

		taskList.removeFromContainer(mockQuery2, mockTask);
		assertTrue(mockQuery2.isEmpty());
		assertFalse(mockQuery.isEmpty());
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).isEmpty());
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
		taskList.addTask(newTask, category);
		assertFalse(taskList.getDefaultCategory().contains(newTask.getHandleIdentifier()));
	}

	public void testAddRepositoryTask() {
		MockTask mockTask = new MockTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		TasksUiPlugin.getTaskList().addQuery(mockQuery);
		TasksUiPlugin.getTaskList().addTask(mockTask, mockQuery);
		Set<ITask> tasks = TasksUiPlugin.getTaskList().getTasks(MockRepositoryConnector.REPOSITORY_URL);
		assertFalse(tasks.isEmpty());

		RepositoryQuery query = (RepositoryQuery) mockTask.getParentContainers().iterator().next();
		assertEquals(mockQuery, query);
		assertFalse(query.isEmpty());
		assertTrue(TasksUiPlugin.getTaskList().getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).isEmpty());
	}

	public void testMoveRepositoryTask() {
		TaskList tasklist = TasksUiPlugin.getTaskList();
		assertTrue(tasklist.getAllTasks().isEmpty());

		MockTask mockTask = new MockTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		TasksUiPlugin.getTaskList().addQuery(mockQuery);
		TasksUiPlugin.getTaskList().addTask(mockTask, mockQuery);

		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);

		taskList.addTask(mockTask, category);
		assertTrue(taskList.getDefaultCategory().isEmpty());
		assertTrue(category.contains(mockTask.getHandleIdentifier()));
		assertTrue(mockQuery.contains(mockTask.getHandleIdentifier()));

	}

	public void testRefactorOrphanedHandle() {
		MockTask mockTask = new MockTask("1");
		mockTask.setLastReadTimeStamp("now");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		taskList.removeFromContainer(mockQuery, mockTask);

		assertFalse(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));

		taskList.refactorRepositoryUrl(MockRepositoryConnector.REPOSITORY_URL, MockRepositoryConnector.REPOSITORY_URL
				+ "new");
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL + "new").contains(
				mockTask.getHandleIdentifier()));
	}

	public void testOrphanedSubtasks() {
		MockTask mockTask = new MockTask("1");
		MockTask mockTask2 = new MockTask("2");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		TasksUiPlugin.getTaskList().addQuery(mockQuery);
		TasksUiPlugin.getTaskList().addTask(mockTask, mockQuery);
		TasksUiPlugin.getTaskList().addTask(mockTask2, mockTask);

		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask2.getHandleIdentifier()));

		assertNotNull(taskList.getTask(mockTask.getHandleIdentifier()));
		assertNotNull(taskList.getTask(mockTask2.getHandleIdentifier()));
		assertNotNull(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL));
		taskList.removeFromContainer(mockQuery, mockTask);
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		assertTrue(mockTask.contains(mockTask2.getHandleIdentifier()));
		taskList.deleteTask(mockTask);
		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		// mockTask2 should be orphaned when the parent task is deleted
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask2.getHandleIdentifier()));
	}

	/**
	 * If a task with subtasks falls out of a query, if its subtasks are subtasks of another task that is still around,
	 * they shouldn't be in the archive.
	 */
	public void testOrphanedSubtaskWithOtherParent() {
		MockTask mockTask = new MockTask("1");
		MockTask mockTask2 = new MockTask("2");
		MockTask mockTask3 = new MockTask("3");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		TasksUiPlugin.getTaskList().addQuery(mockQuery);
		TasksUiPlugin.getTaskList().addTask(mockTask, mockQuery);
		TasksUiPlugin.getTaskList().addTask(mockTask2, mockTask);
		TasksUiPlugin.getTaskList().addTask(mockTask3, mockQuery);
		TasksUiPlugin.getTaskList().addTask(mockTask2, mockTask3);
		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask2.getHandleIdentifier()));
		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask3.getHandleIdentifier()));

		taskList.removeFromContainer(mockQuery, mockTask);
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask.getHandleIdentifier()));
		assertTrue(mockTask.contains(mockTask2.getHandleIdentifier()));

		// True since mockTask is contained and has mockTask2 as a subtask
//		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
//				mockTask2.getHandleIdentifier()));

		taskList.removeFromContainer(mockQuery, mockTask3);
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
				mockTask3.getHandleIdentifier()));
		assertTrue(mockTask3.contains(mockTask2.getHandleIdentifier()));
		// True since mockTask is contained and has mockTask2 as a subtask
//		assertFalse(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL).contains(
//				mockTask2.getHandleIdentifier()));

	}
}
