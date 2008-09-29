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

package org.eclipse.mylyn.bugzilla.deprecated;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskListFactory;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;

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
		assertEquals("should be empty: " + TasksUiPlugin.getTaskList().getDefaultCategory().getChildren(), 0,
				TasksUiPlugin.getTaskList().getDefaultCategory().getChildren().size());
	}

	@Override
	protected void tearDown() throws Exception {
//		manager.resetTaskList();
		TasksUiPlugin.getExternalizationManager().requestSave();
		super.tearDown();
	}

	public void testDueDateExternalization() throws CoreException {
		AbstractTask task = new LocalTask("1", "task 1");
		Date dueDate = new Date();
		task.setDueDate(dueDate);
		TasksUiPlugin.getTaskList().addTask(task);
		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());

		TasksUiPlugin.getExternalizationManager().requestSave();
		manager.resetTaskList();
		TasksUiPlugin.getDefault().reloadDataDirectory();
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
// old API		assertTrue(task.isPastReminder());
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
		assertDatesCloseEnough(task.getCreationDate(), start);

		task.setCompletionDate(creation);
		assertDatesCloseEnough(task.getCompletionDate(), start);

		assertEquals(1, TasksUiPlugin.getTaskList().getRootElements().size());
		TasksUiPlugin.getExternalizationManager().requestSave();

		assertNotNull(TasksUiPlugin.getTaskList());
		// TaskList list = new TaskList();
		// manager.setTaskList(list);
		// assertEquals(0, TasksUiPlugin.getTaskList().getRootTasks().size());
		// manager.readOrCreateTaskList();
		// assertNotNull(TasksUiPlugin.getTaskList());
		assertEquals(1, TasksUiPlugin.getTaskList().getDefaultCategory().getChildren().size());

		Collection<ITask> readList = TasksUiPlugin.getTaskList().getDefaultCategory().getChildren();
		AbstractTask readTask = (AbstractTask) readList.iterator().next();
		assertTrue(readTask.getSummary().equals("task 1"));

		assertEquals("should be: " + creation, task.getCreationDate(), readTask.getCreationDate());
		assertEquals(task.getCompletionDate(), readTask.getCompletionDate());
		assertEquals(task.getScheduledForDate(), readTask.getScheduledForDate());
	}

	// Task retention when connector missing upon startup
	public void testOrphanedTasks() throws CoreException {
		List<AbstractTaskListFactory> originalExternalizers = manager.getTaskListWriter().getExternalizers();
		List<AbstractTaskListFactory> externalizers;
		externalizers = new ArrayList<AbstractTaskListFactory>();
		externalizers.add(new BugzillaTaskListFactory());
		// make some tasks
		// save them
		BugzillaTask task = new BugzillaTask("http://bugs", "1", "1");
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUiPlugin.getExternalizationManager().requestSave();

		// reload tasklist and check that they persist
		manager.resetTaskList();
		TasksUiPlugin.getDefault().reloadDataDirectory();
		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());

		// removed/disable externalizers
		externalizers.clear();
		manager.getTaskListWriter().setDelegateExternalizers(externalizers);

		// reload tasklist ensure task didn't load
		manager.resetTaskList();
		TasksUiPlugin.getDefault().reloadDataDirectory();
		assertEquals(0, TasksUiPlugin.getTaskList().getAllTasks().size());
		// Save the task list (tasks with missing connectors should get
		// persisted)
		TasksUiPlugin.getExternalizationManager().requestSave();

		// re-enable connector
		externalizers.add(new BugzillaTaskListFactory());
		manager.getTaskListWriter().setDelegateExternalizers(externalizers);

		// re-load tasklist
		manager.resetTaskList();
		TasksUiPlugin.getDefault().reloadDataDirectory();

		// ensure that task now gets loaded
		assertEquals(1, TasksUiPlugin.getTaskList().getAllTasks().size());
		manager.getTaskListWriter().setDelegateExternalizers(originalExternalizers);
	}

	// Query retention when connector missing/fails to load
	public void testOrphanedQueries() throws CoreException {
		List<AbstractTaskListFactory> originalExternalizers = manager.getTaskListWriter().getExternalizers();
		List<AbstractTaskListFactory> externalizers;
		externalizers = new ArrayList<AbstractTaskListFactory>();
		externalizers.add(new BugzillaTaskListFactory());
		// make a query
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(IBugzillaConstants.TEST_BUGZILLA_222_URL,
				"http://queryurl", "summary");

		TasksUiPlugin.getTaskList().addQuery(query);
		TasksUiPlugin.getExternalizationManager().requestSave();

		// reload tasklist and check that they persist
		manager.resetTaskList();
		TasksUiPlugin.getDefault().reloadDataDirectory();
		assertEquals(1, TasksUiPlugin.getTaskList().getQueries().size());

		// removed/disable externalizers
		externalizers.clear();
		manager.getTaskListWriter().setDelegateExternalizers(externalizers);

		// reload tasklist ensure query didn't load
		manager.resetTaskList();
		TasksUiPlugin.getDefault().reloadDataDirectory();
		assertEquals(0, TasksUiPlugin.getTaskList().getQueries().size());
		// Save the task list (queries with missing connectors should get
		// persisted)
		TasksUiPlugin.getExternalizationManager().requestSave();

		// re-enable connector
		externalizers.add(new BugzillaTaskListFactory());
		manager.getTaskListWriter().setDelegateExternalizers(externalizers);

		// re-load tasklist
		manager.resetTaskList();
		TasksUiPlugin.getDefault().reloadDataDirectory();

		// ensure that task now gets loaded
		assertEquals(1, TasksUiPlugin.getTaskList().getQueries().size());
		manager.getTaskListWriter().setDelegateExternalizers(originalExternalizers);
	}

	public void assertDatesCloseEnough(Date first, Date second) {
		assertTrue(second.getTime() - first.getTime() < 100);
	}
}
