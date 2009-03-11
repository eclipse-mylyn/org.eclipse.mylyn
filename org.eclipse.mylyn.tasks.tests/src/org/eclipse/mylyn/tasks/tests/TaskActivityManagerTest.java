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

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Shawn Minto
 * @author Robert Elves
 */
public class TaskActivityManagerTest extends TestCase {

	private class MockTaskActivationListener extends TaskActivationAdapter {

		private boolean hasActivated = false;

		private boolean hasPreActivated = false;

		private boolean hasDeactivated = false;

		private boolean hasPreDeactivated = false;

		public void reset() {
			hasActivated = false;
			hasPreActivated = false;

			hasDeactivated = false;
			hasPreDeactivated = false;

		}

		@Override
		public void preTaskActivated(ITask task) {
			assertFalse(hasActivated);
			hasPreActivated = true;
		}

		@Override
		public void preTaskDeactivated(ITask task) {
			assertFalse(hasDeactivated);
			hasPreDeactivated = true;
		}

		@Override
		public void taskActivated(ITask task) {
			assertTrue(hasPreActivated);
			hasActivated = true;
		}

		@Override
		public void taskDeactivated(ITask task) {
			assertTrue(hasPreDeactivated);
			hasDeactivated = true;
		}

	}

	private TaskActivityManager taskActivityManager;

	private TaskList taskList;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		taskActivityManager = TasksUiPlugin.getTaskActivityManager();
		taskActivityManager.deactivateActiveTask();
		taskList = TasksUiPlugin.getTaskList();

		TaskTestUtil.resetTaskListAndRepositories();

		repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
	}

	public void testWeekEnd() {
		AbstractTask task = new LocalTask("12", "task-12");
		assertFalse(taskActivityManager.isScheduledForToday(task));

		// test end of next week
		Calendar end = TaskActivityUtil.getNextWeek().getEndDate();
		Calendar start = TaskActivityUtil.getCalendar();
		start.setTimeInMillis(end.getTimeInMillis());
		TaskActivityUtil.snapStartOfDay(start);
		taskActivityManager.setScheduledFor(task, new DateRange(start, end));
		assertTrue(taskActivityManager.isScheduledForNextWeek(task));
		taskActivityManager.setScheduledFor(task, TaskActivityUtil.getNextWeek());
		assertTrue(taskActivityManager.isScheduledForNextWeek(task));
		assertEquals(0, taskActivityManager.getScheduledTasks(new DateRange(start, end)).size());

		// test end of two weeks
		end = TaskActivityUtil.getNextWeek().next().getEndDate();
		start = TaskActivityUtil.getCalendar();
		start.setTimeInMillis(end.getTimeInMillis());
		TaskActivityUtil.snapStartOfDay(start);
		taskActivityManager.setScheduledFor(task, new DateRange(start, end));
		assertEquals(1, taskActivityManager.getScheduledTasks(new DateRange(start, end)).size());
		assertEquals(1, taskActivityManager.getScheduledTasks(TaskActivityUtil.getNextWeek().next()).size());

	}

	public void testTaskActivation() {
		MockTask task = new MockTask("test:activation");
		MockTaskActivationListener listener = new MockTaskActivationListener();
		try {
			taskActivityManager.addActivationListener(listener);
			try {
				taskActivityManager.activateTask(task);
				assertTrue(listener.hasPreActivated);
				assertTrue(listener.hasActivated);
				assertFalse(listener.hasPreDeactivated);
				assertFalse(listener.hasDeactivated);

				listener.reset();
			} finally {
				taskActivityManager.deactivateTask(task);
			}
			assertFalse(listener.hasPreActivated);
			assertFalse(listener.hasActivated);
			assertTrue(listener.hasPreDeactivated);
			assertTrue(listener.hasDeactivated);
		} finally {
			taskActivityManager.removeActivationListener(listener);
		}
	}

	public void testIsActiveToday() {
		AbstractTask task = new LocalTask("1", "task-1");
		assertFalse(taskActivityManager.isScheduledForToday(task));

		task.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday());
		assertTrue(taskActivityManager.isScheduledForToday(task));

		task.setReminded(true);
		assertTrue(taskActivityManager.isScheduledForToday(task));
		task.setReminded(true);

//		Calendar inAnHour = Calendar.getInstance();
//		inAnHour.set(Calendar.HOUR_OF_DAY, inAnHour.get(Calendar.HOUR_OF_DAY) + 1);
//		inAnHour.getTime();
//		task.setScheduledForDate(inAnHour.getTime());
//		Calendar tomorrow = Calendar.getInstance();
//		TaskActivityUtil.snapToNextDay(tomorrow);
//		assertEquals(-1, inAnHour.compareTo(tomorrow));
//		assertTrue(taskActivityManager.isScheduledForToday(task));
	}

	public void testScheduledForToday() {
		AbstractTask task = new LocalTask("1", "task-1");
		task.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday());
		assertTrue(taskActivityManager.isScheduledForToday(task));
		task.setScheduledForDate(TaskActivityUtil.getCurrentWeek().getToday().next());
		assertFalse(taskActivityManager.isScheduledForToday(task));
	}

	public void testSchedulePastEndOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		TaskActivityUtil.snapForwardNumDays(calendar, 1);
		assertEquals("Should be October", Calendar.OCTOBER, calendar.get(Calendar.MONTH));
	}

	public void testIsCompletedToday() {
		ITask task = new LocalTask("1", "task 1");
		task.setCompletionDate(new Date());
		assertTrue(taskActivityManager.isCompletedToday(task));

		MockTask mockTask = new MockTask("1");
		mockTask.setOwner("unknown");
		taskList.addTask(mockTask);
		mockTask.setCompletionDate(new Date());
		assertFalse("completed: " + mockTask.getCompletionDate(), taskActivityManager.isCompletedToday(mockTask));

		mockTask = new MockTask("2");
		taskList.addTask(mockTask);
		mockTask.setCompletionDate(new Date());
		mockTask.setOwner("testUser");
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("testUser", ""), false);
		assertTrue(taskActivityManager.isCompletedToday(mockTask));
	}

	public void testAllTasksDeactivation() {
		AbstractTask task1 = new LocalTask("task1", "description1");
		AbstractTask task2 = new LocalTask("task2", "description2");
		taskList.addTask(task1);
		taskList.addTask(task2);
		assertNull(taskActivityManager.getActiveTask());

		taskActivityManager.activateTask(task2);
		assertEquals(task2, taskActivityManager.getActiveTask());

		taskActivityManager.deactivateActiveTask();
		assertNull(taskActivityManager.getActiveTask());
	}

}