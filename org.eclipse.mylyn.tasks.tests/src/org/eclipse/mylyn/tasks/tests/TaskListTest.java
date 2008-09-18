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

package org.eclipse.mylyn.tasks.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 */
public class TaskListTest extends TestCase {

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		taskList = new TaskList();
	}

	public void testGetCategories() {
		taskList.addCategory(new TaskCategory("a"));
		assertEquals(2, taskList.getCategories().size());
	}

	public void testLocalSubTaskAdd() {
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask = new LocalTask("2", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask, task);

		assertEquals(1, task.getChildren().size());
		assertEquals(subTask, task.getChildren().iterator().next());
	}

	public void testLocalTaskAddToSelf() {
		LocalTask task = new LocalTask("1", "summary");

		taskList.addTask(task);
		assertFalse(taskList.addTask(task, task));
		assertEquals(0, task.getChildren().size());
		assertEquals(1, task.getParentContainers().size());
	}

	public void testLocalSubTaskAddCycle() {
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask = new LocalTask("2", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask, task);
		taskList.addTask(task, subTask);

		assertEquals(2, taskList.getAllTasks().size());
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getCategories().iterator().next().getChildren().size());
		assertEquals(1, task.getChildren().size());
		assertEquals(0, subTask.getChildren().size());
		assertEquals(subTask, task.getChildren().iterator().next());
	}

	public void testLocalSubTaskAddDeepCycle() {
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask1 = new LocalTask("2", "subTask");
		LocalTask subTask2 = new LocalTask("3", "subTask");
		LocalTask subTask3 = new LocalTask("4", "subTask");
		LocalTask subTask4 = new LocalTask("5", "subTask");
		LocalTask subTask5 = new LocalTask("6", "subTask");
		LocalTask subTask6 = new LocalTask("7", "subTask");
		LocalTask subTask7 = new LocalTask("8", "subTask");
		LocalTask subTask8 = new LocalTask("9", "subTask");
		LocalTask subTask9 = new LocalTask("10", "subTask");
		LocalTask subTask10 = new LocalTask("11", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask1, task);
		taskList.addTask(subTask2, subTask1);
		taskList.addTask(subTask3, subTask2);
		taskList.addTask(subTask4, subTask3);
		taskList.addTask(subTask5, subTask4);
		taskList.addTask(subTask6, subTask5);
		taskList.addTask(subTask7, subTask6);
		taskList.addTask(subTask8, subTask7);
		taskList.addTask(subTask9, subTask8);
		taskList.addTask(subTask10, subTask9);

		taskList.addTask(task, subTask10);

		assertEquals(11, taskList.getAllTasks().size());
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getCategories().iterator().next().getChildren().size());
		assertEquals(1, task.getChildren().size());
		assertEquals(subTask1, task.getChildren().iterator().next());

		assertEquals(1, subTask1.getChildren().size());
		assertEquals(subTask2, subTask1.getChildren().iterator().next());

		assertEquals(1, subTask2.getChildren().size());
		assertEquals(subTask3, subTask2.getChildren().iterator().next());

		assertEquals(1, subTask3.getChildren().size());
		assertEquals(subTask4, subTask3.getChildren().iterator().next());

		assertEquals(1, subTask4.getChildren().size());
		assertEquals(subTask5, subTask4.getChildren().iterator().next());

		assertEquals(1, subTask5.getChildren().size());
		assertEquals(subTask6, subTask5.getChildren().iterator().next());

		assertEquals(1, subTask6.getChildren().size());
		assertEquals(subTask7, subTask6.getChildren().iterator().next());

		assertEquals(1, subTask7.getChildren().size());
		assertEquals(subTask8, subTask7.getChildren().iterator().next());

		assertEquals(1, subTask8.getChildren().size());
		assertEquals(subTask9, subTask8.getChildren().iterator().next());

		assertEquals(1, subTask9.getChildren().size());
		assertEquals(subTask10, subTask9.getChildren().iterator().next());

		assertEquals(0, subTask10.getChildren().size());
	}

	public void testLocalSubTaskAddMaxSubTaskDepthDeepCycle() {
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask1 = new LocalTask("2", "subTask");
		LocalTask subTask2 = new LocalTask("3", "subTask");
		LocalTask subTask3 = new LocalTask("4", "subTask");
		LocalTask subTask4 = new LocalTask("5", "subTask");
		LocalTask subTask5 = new LocalTask("6", "subTask");
		LocalTask subTask6 = new LocalTask("7", "subTask");
		LocalTask subTask7 = new LocalTask("8", "subTask");
		LocalTask subTask8 = new LocalTask("9", "subTask");
		LocalTask subTask9 = new LocalTask("10", "subTask");
		LocalTask subTask10 = new LocalTask("11", "subTask");
		LocalTask subTask11 = new LocalTask("12", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask1, task);
		taskList.addTask(subTask2, subTask1);
		taskList.addTask(subTask3, subTask2);
		taskList.addTask(subTask4, subTask3);
		taskList.addTask(subTask5, subTask4);
		taskList.addTask(subTask6, subTask5);
		taskList.addTask(subTask7, subTask6);
		taskList.addTask(subTask8, subTask7);
		taskList.addTask(subTask9, subTask8);
		taskList.addTask(subTask10, subTask9);
		taskList.addTask(subTask11, subTask10);

		taskList.addTask(task, subTask11);

		assertEquals(12, taskList.getAllTasks().size());
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getCategories().iterator().next().getChildren().size());
		assertEquals(1, task.getChildren().size());
		assertEquals(subTask1, task.getChildren().iterator().next());

		assertEquals(1, subTask1.getChildren().size());
		assertEquals(subTask2, subTask1.getChildren().iterator().next());

		assertEquals(1, subTask2.getChildren().size());
		assertEquals(subTask3, subTask2.getChildren().iterator().next());

		assertEquals(1, subTask3.getChildren().size());
		assertEquals(subTask4, subTask3.getChildren().iterator().next());

		assertEquals(1, subTask4.getChildren().size());
		assertEquals(subTask5, subTask4.getChildren().iterator().next());

		assertEquals(1, subTask5.getChildren().size());
		assertEquals(subTask6, subTask5.getChildren().iterator().next());

		assertEquals(1, subTask6.getChildren().size());
		assertEquals(subTask7, subTask6.getChildren().iterator().next());

		assertEquals(1, subTask7.getChildren().size());
		assertEquals(subTask8, subTask7.getChildren().iterator().next());

		assertEquals(1, subTask8.getChildren().size());
		assertEquals(subTask9, subTask8.getChildren().iterator().next());

		assertEquals(1, subTask9.getChildren().size());
		assertEquals(subTask10, subTask9.getChildren().iterator().next());

		assertEquals(1, subTask10.getChildren().size());
		assertEquals(subTask11, subTask10.getChildren().iterator().next());

		assertEquals(0, subTask11.getChildren().size());
	}

	public void testLocalSubTaskAddReallyDeepCycle() {
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask1 = new LocalTask("2", "subTask");
		LocalTask subTask2 = new LocalTask("3", "subTask");
		LocalTask subTask3 = new LocalTask("4", "subTask");
		LocalTask subTask4 = new LocalTask("5", "subTask");
		LocalTask subTask5 = new LocalTask("6", "subTask");
		LocalTask subTask6 = new LocalTask("7", "subTask");
		LocalTask subTask7 = new LocalTask("8", "subTask");
		LocalTask subTask8 = new LocalTask("9", "subTask");
		LocalTask subTask9 = new LocalTask("10", "subTask");
		LocalTask subTask10 = new LocalTask("11", "subTask");
		LocalTask subTask11 = new LocalTask("12", "subTask");
		LocalTask subTask12 = new LocalTask("13", "subTask");
		LocalTask subTask13 = new LocalTask("14", "subTask");
		LocalTask subTask14 = new LocalTask("15", "subTask");
		LocalTask subTask15 = new LocalTask("16", "subTask");
		LocalTask subTask16 = new LocalTask("17", "subTask");
		LocalTask subTask17 = new LocalTask("18", "subTask");
		LocalTask subTask18 = new LocalTask("19", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask1, task);
		taskList.addTask(subTask2, subTask1);
		taskList.addTask(subTask3, subTask2);
		taskList.addTask(subTask4, subTask3);
		taskList.addTask(subTask5, subTask4);
		taskList.addTask(subTask6, subTask5);
		taskList.addTask(subTask7, subTask6);
		taskList.addTask(subTask8, subTask7);
		taskList.addTask(subTask9, subTask8);
		taskList.addTask(subTask10, subTask9);
		taskList.addTask(subTask11, subTask10);
		taskList.addTask(subTask12, subTask11);
		taskList.addTask(subTask13, subTask12);
		taskList.addTask(subTask14, subTask13);
		taskList.addTask(subTask15, subTask14);
		taskList.addTask(subTask16, subTask15);
		taskList.addTask(subTask17, subTask16);
		taskList.addTask(subTask18, subTask17);

		taskList.addTask(task, subTask18);

		assertEquals(19, taskList.getAllTasks().size());
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getCategories().iterator().next().getChildren().size());
		assertEquals(1, task.getChildren().size());
		assertEquals(subTask1, task.getChildren().iterator().next());

		assertEquals(1, subTask1.getChildren().size());
		assertEquals(subTask2, subTask1.getChildren().iterator().next());

		assertEquals(1, subTask2.getChildren().size());
		assertEquals(subTask3, subTask2.getChildren().iterator().next());

		assertEquals(1, subTask3.getChildren().size());
		assertEquals(subTask4, subTask3.getChildren().iterator().next());

		assertEquals(1, subTask4.getChildren().size());
		assertEquals(subTask5, subTask4.getChildren().iterator().next());

		assertEquals(1, subTask5.getChildren().size());
		assertEquals(subTask6, subTask5.getChildren().iterator().next());

		assertEquals(1, subTask6.getChildren().size());
		assertEquals(subTask7, subTask6.getChildren().iterator().next());

		assertEquals(1, subTask7.getChildren().size());
		assertEquals(subTask8, subTask7.getChildren().iterator().next());

		assertEquals(1, subTask8.getChildren().size());
		assertEquals(subTask9, subTask8.getChildren().iterator().next());

		assertEquals(1, subTask9.getChildren().size());
		assertEquals(subTask10, subTask9.getChildren().iterator().next());

		assertEquals(1, subTask10.getChildren().size());
		assertEquals(subTask11, subTask10.getChildren().iterator().next());

		assertEquals(1, subTask11.getChildren().size());
		assertEquals(subTask12, subTask11.getChildren().iterator().next());

		assertEquals(1, subTask11.getChildren().size());
		assertEquals(subTask12, subTask11.getChildren().iterator().next());

		assertEquals(1, subTask12.getChildren().size());
		assertEquals(subTask13, subTask12.getChildren().iterator().next());

		assertEquals(1, subTask13.getChildren().size());
		assertEquals(subTask14, subTask13.getChildren().iterator().next());

		assertEquals(1, subTask14.getChildren().size());
		assertEquals(subTask15, subTask14.getChildren().iterator().next());

		assertEquals(1, subTask15.getChildren().size());
		assertEquals(subTask16, subTask15.getChildren().iterator().next());

		assertEquals(1, subTask16.getChildren().size());
		assertEquals(subTask17, subTask16.getChildren().iterator().next());

		assertEquals(1, subTask17.getChildren().size());
		assertEquals(subTask18, subTask17.getChildren().iterator().next());

		assertEquals(0, subTask18.getChildren().size());
	}

	public void testQueryAndCategoryNameClash() {
		TaskCategory category = new TaskCategory("TestClash");
		taskList.addCategory(category);
		assertTrue(taskList.getCategories().contains(category));
		assertEquals(2, taskList.getCategories().size());

		MockRepositoryQuery query = new MockRepositoryQuery("TestClash");
		taskList.addQuery(query);
		assertTrue(taskList.getCategories().contains(category));
		assertEquals(2, taskList.getCategories().size());
	}

	public void testMoveToRoot() {
		AbstractTask task1 = new LocalTask("t1", "t1");
		taskList.addTask(task1, taskList.getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));
		assertEquals(1, taskList.getDefaultCategory().getChildren().size());
		assertEquals(taskList.getDefaultCategory(), TaskCategory.getParentTaskCategory(task1));

		TaskCategory cat1 = new TaskCategory("c1");
		taskList.addCategory(cat1);

		taskList.addTask(task1, cat1);
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
		assertEquals(cat1, TaskCategory.getParentTaskCategory(task1));

		taskList.addTask(task1, taskList.getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));
		assertEquals(1, taskList.getDefaultCategory().getChildren().size());
		assertEquals(0, cat1.getChildren().size());
		assertEquals(taskList.getDefaultCategory(), TaskCategory.getParentTaskCategory(task1));
	}

	public void testDeleteCategory() {
		assertEquals(1, taskList.getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		taskList.addCategory(category);
		assertEquals(2, taskList.getCategories().size());
		taskList.deleteCategory(category);
		assertEquals(1, taskList.getCategories().size());
	}

	public void testDeleteCategoryMovesTasksToRoot() {
		AbstractTask task = new MockTask("delete");
		TaskCategory category = new TaskCategory("cat");
		taskList.addCategory(category);
		taskList.addTask(task, category);
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
		taskList.deleteCategory(category);
		taskList.getUnmatchedContainer(MockRepositoryConnector.REPOSITORY_URL);
	}

	@SuppressWarnings("deprecation")
	public void testRenameCategory() {
		TaskCategory category = new TaskCategory("handle", "cat");
		taskList.addCategory(category);
		assertEquals(2, taskList.getCategories().size());
		taskList.renameContainer(category, "newDescription");
		AbstractTaskCategory container = taskList.getContainerForHandle("handle");
		assertNotNull(container);
		assertEquals("newDescription", container.getSummary());
		taskList.deleteCategory(container);
		assertEquals(1, taskList.getCategories().size());
	}

	@SuppressWarnings("deprecation")
	public void testDeleteCategoryAfterRename() {
		String newDesc = "newDescription";
		assertNotNull(taskList);
		assertEquals(1, taskList.getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		taskList.addCategory(category);
		assertEquals(2, taskList.getCategories().size());
		taskList.renameContainer(category, newDesc);
		taskList.deleteCategory(category);
		assertEquals(1, taskList.getCategories().size());
	}

	public void testCreateSameCategoryName() {
		assertEquals(1, taskList.getCategories().size());
		TaskCategory category = new TaskCategory("cat");
		taskList.addCategory(category);
		assertEquals(2, taskList.getCategories().size());
		TaskCategory category2 = new TaskCategory("cat");
		try {
			taskList.addCategory(category2);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
		assertEquals(2, taskList.getCategories().size());
		ITaskContainer container = taskList.getContainerForHandle("cat");
		assertEquals(container, category);
	}

	public void testDeleteRootTask() {
		AbstractTask task = new LocalTask("1", "label");
		taskList.addTask(task);
		taskList.deleteTask(task);
		assertEquals(0, taskList.getAllTasks().size());
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
	}

	public void testDeleteFromCategory() {
		assertEquals(0, taskList.getAllTasks().size());
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
		//assertEquals(0, taskList.getArchiveContainer().getChildren().size());
		assertEquals(1, taskList.getCategories().size());

		AbstractTask task = new LocalTask("1", "label");
		TaskCategory category = new TaskCategory("handleAndDescription");
		taskList.addTask(task);
		assertEquals(1, taskList.getDefaultCategory().getChildren().size());

		taskList.addCategory(category);
		taskList.addTask(task, category);
		assertEquals(2, taskList.getCategories().size());
		assertEquals(1, category.getChildren().size());
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
		assertEquals(1, taskList.getAllTasks().size());

		taskList.deleteTask(task);
		assertEquals(0, taskList.getAllTasks().size());
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
		assertEquals(0, category.getChildren().size());
	}

	public void testDeleteRepositoryTask() {
		String repositoryUrl = "http://somewhere.com";
		MockTask task = new MockTask(repositoryUrl, "1");
		taskList.addTask(task, taskList.getDefaultCategory());
		MockRepositoryQuery query = new MockRepositoryQuery("query");
		taskList.addQuery(query);
		taskList.addTask(task, query);
		assertEquals(1, taskList.getAllTasks().size());
		assertEquals(1, taskList.getDefaultCategory().getChildren().size());
		taskList.deleteTask(task);
		assertEquals(0, taskList.getAllTasks().size());
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
	}

	public void testgetQueriesAndHitsForHandle() {
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

	public void testUpdateQueryHits() {
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

	public void testGetRepositoryTasks() {
		String repositoryUrl = "https://bugs.eclipse.org/bugs";
		String bugNumber = "106939";
		MockTask task1 = new MockTask(repositoryUrl, bugNumber);
		taskList.addTask(task1);
		MockTask task2 = new MockTask("https://unresolved", bugNumber);
		taskList.addTask(task2);

		assertEquals(2, taskList.getAllTasks().size());
		Set<ITask> tasksReturned = taskList.getTasks(repositoryUrl);
		assertNotNull(tasksReturned);
		assertEquals(1, tasksReturned.size());
		assertTrue(tasksReturned.contains(task1));
	}

}
