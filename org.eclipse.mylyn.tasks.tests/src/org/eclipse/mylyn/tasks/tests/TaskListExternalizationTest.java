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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class TaskListExternalizationTest extends TestCase {

	private TaskList taskList;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);

		TaskTestUtil.resetTaskListAndRepositories();

		repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		taskList = TasksUiPlugin.getTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskList();
	}

	public void testTaskAttributes() throws Exception {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		task1.setAttribute("key", "value");
		assertEquals(1, task1.getAttributes().size());

		TaskTestUtil.saveAndReadTasklist();

		task1 = taskList.getTask(task1.getHandleIdentifier());
		assertNotNull(task1);
		assertEquals(1, task1.getAttributes().size());
		assertEquals("value", task1.getAttribute("key"));
	}

	public void testTaskAttributeDelete() throws Exception {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		task1.setAttribute("key", "value");
		task1.setAttribute("key", null);
		assertEquals(0, task1.getAttributes().size());
		assertEquals(null, task1.getAttribute("key"));

		TaskTestUtil.saveAndReadTasklist();

		task1 = taskList.getTask(task1.getHandleIdentifier());
		assertNotNull(task1);
		assertEquals(0, task1.getAttributes().size());
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

		TasksUiPlugin.getExternalizationManager().requestSave();
		TasksUiPlugin.getDefault().reloadDataDirectory();

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

		TasksUiPlugin.getExternalizationManager().requestSave();
		TasksUiPlugin.getDefault().reloadDataDirectory();
		assertTrue(taskList.getDefaultCategory().getChildren().contains(task1));
	}

}
