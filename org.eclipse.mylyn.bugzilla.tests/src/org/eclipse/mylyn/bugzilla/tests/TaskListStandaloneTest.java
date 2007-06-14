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

package org.eclipse.mylyn.bugzilla.tests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.ui.tasklist.BugzillaTaskExternalizer;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskListExternalizer;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListStandaloneTest extends TestCase {

	private TaskListManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		manager = TasksUiPlugin.getTaskListManager();

		manager.resetTaskList();
		assertEquals("should be empty: " + manager.getTaskList().getDefaultCategory().getChildren(), 0,
				manager.getTaskList().getDefaultCategory().getChildren().size());
	}

	@Override
	protected void tearDown() throws Exception {
		manager.resetTaskList();
		manager.saveTaskList();
		super.tearDown();
	}

	public void testDueDateExternalization() {
		AbstractTask task = new LocalTask("1", "task 1");
		Date dueDate = new Date();
		task.setDueDate(dueDate);
		manager.getTaskList().moveToContainer(task, manager.getTaskList().getDefaultCategory());
		assertEquals(1, manager.getTaskList().getAllTasks().size());

		manager.saveTaskList();
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		Set<AbstractTask> readList = manager.getTaskList().getDefaultCategory().getChildren();
		AbstractTask readTask = readList.iterator().next();
		assertTrue(readTask.getSummary().equals("task 1"));
		assertTrue(readTask.getDueDate().compareTo(dueDate) == 0);
	}

	public void testPastReminder() {
		AbstractTask task = new LocalTask("1", "1");
		long now = new Date().getTime();
		task.setScheduledForDate(new Date(now - 1000));
		assertTrue(task.isPastReminder());

		task.setScheduledForDate(new Date(now + 1000));
		assertFalse(task.isPastReminder());

		task.setScheduledForDate(new Date(now - 1000));
		task.setCompleted(true);
		assertTrue(task.isPastReminder());
	}

	public void testDates() {
		Date start = Calendar.getInstance().getTime();
		Date creation = new Date();
		AbstractTask task = new LocalTask("1", "task 1");

		manager.getTaskList().moveToContainer(task, manager.getTaskList().getDefaultCategory());
		assertDatesCloseEnough(task.getCreationDate(), start);

		task.setCompleted(true);
		assertDatesCloseEnough(task.getCompletionDate(), start);

		task.setScheduledForDate(start);
		assertDatesCloseEnough(task.getScheduledForDate(), start);

		assertEquals(2, manager.getTaskList().getRootElements().size());
		manager.saveTaskList();

		assertNotNull(manager.getTaskList());
		// TaskList list = new TaskList();
		// manager.setTaskList(list);
		// assertEquals(0, manager.getTaskList().getRootTasks().size());
		// manager.readOrCreateTaskList();
		// assertNotNull(manager.getTaskList());
		assertEquals(1, manager.getTaskList().getDefaultCategory().getChildren().size());

		Set<AbstractTask> readList = manager.getTaskList().getDefaultCategory().getChildren();
		AbstractTask readTask = readList.iterator().next();
		assertTrue(readTask.getSummary().equals("task 1"));

		assertEquals("should be: " + creation, task.getCreationDate(), readTask.getCreationDate());
		assertEquals(task.getCompletionDate(), readTask.getCompletionDate());
		assertEquals(task.getScheduledForDate(), readTask.getScheduledForDate());
	}

	// Task retention when connector missing upon startup
	public void testOrphanedTasks() {
		List<ITaskListExternalizer> originalExternalizers = manager.getTaskListWriter().getExternalizers();
		List<ITaskListExternalizer> externalizers;
		externalizers = new ArrayList<ITaskListExternalizer>();
		externalizers.add(new BugzillaTaskExternalizer());
		// make some tasks
		// save them
		BugzillaTask task = new BugzillaTask("http://bugs", "1", "1");
		manager.getTaskList().addTask(task);
		manager.saveTaskList();

		// reload tasklist and check that they persist
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getAllTasks().size());

		// removed/disable externalizers
		externalizers.clear();
		manager.getTaskListWriter().setDelegateExternalizers(externalizers);

		// reload tasklist ensure task didn't load
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(0, manager.getTaskList().getAllTasks().size());
		// Save the task list (tasks with missing connectors should get
		// persisted)
		manager.saveTaskList();

		// re-enable connector
		externalizers.add(new BugzillaTaskExternalizer());
		manager.getTaskListWriter().setDelegateExternalizers(externalizers);

		// re-load tasklist
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		// ensure that task now gets loaded
		assertEquals(1, manager.getTaskList().getAllTasks().size());
		manager.getTaskListWriter().setDelegateExternalizers(originalExternalizers);
	}

	// Query retention when connector missing/fails to load
	public void testOrphanedQueries() {
		List<ITaskListExternalizer> originalExternalizers = manager.getTaskListWriter().getExternalizers();
		List<ITaskListExternalizer> externalizers;
		externalizers = new ArrayList<ITaskListExternalizer>();
		externalizers.add(new BugzillaTaskExternalizer());
		// make a query
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(IBugzillaConstants.TEST_BUGZILLA_222_URL,
				"http://queryurl", "summary");

		manager.getTaskList().addQuery(query);
		manager.saveTaskList();

		// reload tasklist and check that they persist
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(1, manager.getTaskList().getQueries().size());

		// removed/disable externalizers
		externalizers.clear();
		manager.getTaskListWriter().setDelegateExternalizers(externalizers);

		// reload tasklist ensure query didn't load
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();
		assertEquals(0, manager.getTaskList().getQueries().size());
		// Save the task list (queries with missing connectors should get
		// persisted)
		manager.saveTaskList();

		// re-enable connector
		externalizers.add(new BugzillaTaskExternalizer());
		manager.getTaskListWriter().setDelegateExternalizers(externalizers);

		// re-load tasklist
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		// ensure that task now gets loaded
		assertEquals(1, manager.getTaskList().getQueries().size());
		manager.getTaskListWriter().setDelegateExternalizers(originalExternalizers);
	}

	public void assertDatesCloseEnough(Date first, Date second) {
		assertTrue(second.getTime() - first.getTime() < 100);
	}
}
