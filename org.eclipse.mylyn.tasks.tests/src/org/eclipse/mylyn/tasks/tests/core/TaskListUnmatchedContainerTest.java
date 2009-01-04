/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskListUnmatchedContainerTest extends TestCase {

	private TaskList taskList;

	private UnmatchedTaskContainer unmatchedContainer;

	@Override
	protected void setUp() throws Exception {
		taskList = new TaskList();
		unmatchedContainer = new UnmatchedTaskContainer(MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		taskList.addUnmatchedContainer(unmatchedContainer);
	}

	@Override
	protected void tearDown() throws Exception {
	}

	/**
	 * When a local tasks is removed from a category it should be placed in the Uncategorized folder.
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
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		taskList.removeFromContainer(mockQuery, mockTask);

		assertFalse(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertTrue(unmatchedContainer.contains(mockTask.getHandleIdentifier()));
	}

	/**
	 * Query removed with task in category, just query removed task remains.
	 */
	public void testTaskRemovedFromQueryButInCategory() {
		MockTask mockTask = new MockTask("1");
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
		assertTrue(unmatchedContainer.contains(mockTask.getHandleIdentifier()));
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
		assertTrue(unmatchedContainer.isEmpty());
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
		assertTrue(unmatchedContainer.isEmpty());
		taskList.deleteQuery(mockQuery);
		assertTrue(taskList.getQueries().size() == 0);
		assertFalse(unmatchedContainer.isEmpty());
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
		assertTrue(unmatchedContainer.isEmpty());
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
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);
		Set<ITask> tasks = taskList.getTasks(MockRepositoryConnector.REPOSITORY_URL);
		assertFalse(tasks.isEmpty());

		RepositoryQuery query = (RepositoryQuery) mockTask.getParentContainers().iterator().next();
		assertEquals(mockQuery, query);
		assertFalse(query.isEmpty());
		assertTrue(unmatchedContainer.isEmpty());
	}

	public void testMoveRepositoryTask() {
		TaskList tasklist = taskList;
		assertTrue(tasklist.getAllTasks().isEmpty());

		MockTask mockTask = new MockTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		TaskCategory category = new TaskCategory("taskCategoryHandle");
		taskList.addCategory(category);

		taskList.addTask(mockTask, category);
		assertTrue(taskList.getDefaultCategory().isEmpty());
		assertTrue(category.contains(mockTask.getHandleIdentifier()));
		assertTrue(mockQuery.contains(mockTask.getHandleIdentifier()));

	}

	public void testRefactorOrphanedHandle() {
		MockTask mockTask = new MockTask("1");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);

		taskList.removeFromContainer(mockQuery, mockTask);

		assertFalse(mockQuery.contains(mockTask.getHandleIdentifier()));
		assertTrue(unmatchedContainer.contains(mockTask.getHandleIdentifier()));

		taskList.refactorRepositoryUrl(MockRepositoryConnector.REPOSITORY_URL, MockRepositoryConnector.REPOSITORY_URL
				+ "new");
		assertTrue(taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL + "new").contains(
				mockTask.getHandleIdentifier()));
	}

	public void testOrphanedSubtasks() {
		MockTask mockTask = new MockTask("1");
		MockTask mockTask2 = new MockTask("2");
		MockRepositoryQuery mockQuery = new MockRepositoryQuery("mock query");
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);
		taskList.addTask(mockTask2, mockTask);

		assertFalse(unmatchedContainer.contains(mockTask.getHandleIdentifier()));
		assertFalse(unmatchedContainer.contains(mockTask2.getHandleIdentifier()));

		assertNotNull(taskList.getTask(mockTask.getHandleIdentifier()));
		assertNotNull(taskList.getTask(mockTask2.getHandleIdentifier()));
		assertNotNull(unmatchedContainer);
		taskList.removeFromContainer(mockQuery, mockTask);
		assertTrue(unmatchedContainer.contains(mockTask.getHandleIdentifier()));
		assertTrue(mockTask.contains(mockTask2.getHandleIdentifier()));
		taskList.deleteTask(mockTask);
		assertFalse(unmatchedContainer.contains(mockTask.getHandleIdentifier()));
		// mockTask2 should be orphaned when the parent task is deleted
		assertTrue(unmatchedContainer.contains(mockTask2.getHandleIdentifier()));
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
		taskList.addQuery(mockQuery);
		taskList.addTask(mockTask, mockQuery);
		taskList.addTask(mockTask2, mockTask);
		taskList.addTask(mockTask3, mockQuery);
		taskList.addTask(mockTask2, mockTask3);
		assertFalse(unmatchedContainer.contains(mockTask.getHandleIdentifier()));
		assertFalse(unmatchedContainer.contains(mockTask2.getHandleIdentifier()));
		assertFalse(unmatchedContainer.contains(mockTask3.getHandleIdentifier()));

		taskList.removeFromContainer(mockQuery, mockTask);
		assertTrue(unmatchedContainer.contains(mockTask.getHandleIdentifier()));
		assertTrue(mockTask.contains(mockTask2.getHandleIdentifier()));

		// True since mockTask is contained and has mockTask2 as a subtask
//		assertFalse(unmatchedContainer.contains(
//				mockTask2.getHandleIdentifier()));

		taskList.removeFromContainer(mockQuery, mockTask3);
		assertTrue(unmatchedContainer.contains(mockTask3.getHandleIdentifier()));
		assertTrue(mockTask3.contains(mockTask2.getHandleIdentifier()));
		// True since mockTask is contained and has mockTask2 as a subtask
//		assertFalse(unmatchedContainer.contains(
//				mockTask2.getHandleIdentifier()));

	}
}
