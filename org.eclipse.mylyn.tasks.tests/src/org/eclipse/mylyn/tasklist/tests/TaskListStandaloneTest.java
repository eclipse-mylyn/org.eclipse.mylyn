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

package org.eclipse.mylar.tasklist.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTaskExternalizer;
import org.eclipse.mylar.internal.tasks.ui.util.TaskListWriter;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListExternalizer;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.ui.TaskListManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListStandaloneTest extends TestCase {

	private TaskListManager manager;

	private File file;

	List<ITaskListExternalizer> externalizers;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getRepositoryManager().clearRepositories();
		externalizers = new ArrayList<ITaskListExternalizer>();

		externalizers.add(new BugzillaTaskExternalizer());

		TaskListWriter writer = new TaskListWriter();
		writer.setDelegateExternalizers(externalizers);

		file = new File("foo" + TasksUiPlugin.FILE_EXTENSION);
		file.deleteOnExit();
		manager = new TaskListManager(writer, file, 1);
		manager.resetTaskList();
		assertEquals("should be empty: " + manager.getTaskList().getRootTasks(), 0, manager.getTaskList()
				.getRootTasks().size());
	}

	@Override
	protected void tearDown() throws Exception {
		// manager.getTaskList().clear();
		manager.resetTaskList();
		// manager.setTaskList(new TaskList());
		super.tearDown();
	}

	public void testPastReminder() {
		ITask task = new Task("1", "1", true);
		long now = new Date().getTime();
		task.setReminderDate(new Date(now - 1000));
		assertTrue(task.isPastReminder());

		task.setReminderDate(new Date(now + 1000));
		assertFalse(task.isPastReminder());

		task.setReminderDate(new Date(now - 1000));
		task.setCompleted(true);
		assertTrue(task.isPastReminder());
	}

	public void testDates() {
		Date start = Calendar.getInstance().getTime();
		Date creation = new Date();
		Task task = new Task("1", "task 1", true);

		manager.getTaskList().moveToRoot(task);
		assertDatesCloseEnough(task.getCreationDate(), start);

		task.setCompleted(true);
		assertDatesCloseEnough(task.getCompletionDate(), start);

		task.setReminderDate(start);
		assertDatesCloseEnough(task.getReminderDate(), start);

		assertEquals(2, manager.getTaskList().getRootElements().size());
		manager.saveTaskList();

		assertNotNull(manager.getTaskList());
		// TaskList list = new TaskList();
		// manager.setTaskList(list);
		// assertEquals(0, manager.getTaskList().getRootTasks().size());
		// manager.readOrCreateTaskList();
		// assertNotNull(manager.getTaskList());
		assertEquals(1, manager.getTaskList().getRootTasks().size());

		Set<ITask> readList = manager.getTaskList().getRootTasks();
		ITask readTask = readList.iterator().next();
		assertTrue(readTask.getDescription().equals("task 1"));

		assertEquals("should be: " + creation, task.getCreationDate(), readTask.getCreationDate());
		assertEquals(task.getCompletionDate(), readTask.getCompletionDate());
		assertEquals(task.getReminderDate(), readTask.getReminderDate());
	}

	public void testTaskRetentionWhenConnectorMissing() {

		// make some tasks
		// save them
		BugzillaTask task = new BugzillaTask("http://bugs-1", "1", true);
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
	}
	
	public void assertDatesCloseEnough(Date first, Date second) {
		assertTrue(second.getTime() - first.getTime() < 100);
	}
}
