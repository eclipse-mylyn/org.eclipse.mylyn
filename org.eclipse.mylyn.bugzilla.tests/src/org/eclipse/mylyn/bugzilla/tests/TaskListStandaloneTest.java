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

package org.eclipse.mylyn.bugzilla.tests;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListStandaloneTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getTaskList().reset();
		TasksUiPlugin.getExternalizationManager().save(true);
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		assertEquals("should be empty: " + TasksUiPlugin.getTaskList().getDefaultCategory().getChildren(), 0,
				TasksUiPlugin.getTaskList().getDefaultCategory().getChildren().size());
	}

	@Override
	protected void tearDown() throws Exception {
		TasksUiPlugin.getTaskList().reset();
		TasksUiPlugin.getExternalizationManager().save(true);
		super.tearDown();
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

	public void testDates() {
		Date start = Calendar.getInstance().getTime();
		Date creation = new Date();
		AbstractTask task = new LocalTask("1", "task 1");

		TasksUiPlugin.getTaskList().addTask(task);
		assertNull(task.getCreationDate());
		task.setCreationDate(start);
		assertEquals(start, task.getCreationDate());

		task.setCompletionDate(creation);
		assertNull(task.getCompletionDate());
		assertEquals(start, task.getCompletionDate());

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

	// Task retention when connector missing upon startup
	public void testOrphanedTasks() throws Exception {
		// make some tasks
		// save them
		assertEquals(0, TasksUiPlugin.getTaskList().getAllTasks().size());
		ITask task = new TaskTask(BugzillaCorePlugin.CONNECTOR_KIND, "http://bugs", "1");
		TasksUiPlugin.getTaskList().addTask(task);

		// reload tasklist and check that they persist
		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());

		// removed/disable externalizers
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().removeRepositoryConnector(
				BugzillaCorePlugin.CONNECTOR_KIND);

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
		RepositoryQuery query = new RepositoryQuery(BugzillaCorePlugin.CONNECTOR_KIND, "bugzillaQuery");
		TasksUiPlugin.getTaskList().addQuery(query);
		TasksUiPlugin.getExternalizationManager().save(true);

		// reload tasklist and check that they persist
		TaskTestUtil.saveAndReadTasklist();
		assertEquals(1, TasksUiPlugin.getTaskList().getQueries().size());

		// removed/disable externalizers
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().removeRepositoryConnector(
				BugzillaCorePlugin.CONNECTOR_KIND);

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
