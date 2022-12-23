/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Manuel Doninger - fixes for bug 349924
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

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

import junit.framework.TestCase;

/**
 * @author Shawn Minto
 * @author Robert Elves
 * @author Manuel Doninger
 * @author Steffen Pingel
 */
public class TaskActivityManagerTest extends TestCase {

	public static class MockTaskActivationListenerExtension extends TaskActivationAdapter {

		public static MockTaskActivationListenerExtension INSTANCE;

		public static int INSTANCE_COUNT;

		public boolean hasActivated = false;

		public boolean hasPreActivated = false;

		public boolean hasDeactivated = false;

		public boolean hasPreDeactivated = false;

		public MockTaskActivationListenerExtension() {
			INSTANCE = this;
			INSTANCE_COUNT++;
		}

		public void reset() {
			hasActivated = false;
			hasPreActivated = false;

			hasDeactivated = false;
			hasPreDeactivated = false;

		}

		@Override
		public void preTaskActivated(ITask task) {
			hasPreActivated = true;
		}

		@Override
		public void preTaskDeactivated(ITask task) {
			hasPreDeactivated = true;
		}

		@Override
		public void taskActivated(ITask task) {
			hasActivated = true;
		}

		@Override
		public void taskDeactivated(ITask task) {
			hasDeactivated = true;
		}

	}

	private class MockTaskActivationListener extends TaskActivationAdapter {

		private boolean hasActivated = false;

		private boolean hasPreActivated = false;

		private boolean hasDeactivated = false;

		private boolean hasPreDeactivated = false;

		private int timesCalledCanDeactivate;

		private final boolean canDeactivate;

		public MockTaskActivationListener() {
			this(true);
		}

		public MockTaskActivationListener(boolean canDeactivate) {
			this.canDeactivate = canDeactivate;
		}

		public void reset() {
			hasActivated = false;
			hasPreActivated = false;

			hasDeactivated = false;
			hasPreDeactivated = false;

			timesCalledCanDeactivate = 0;
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

		@Override
		public boolean canDeactivateTask(ITask task) {
			timesCalledCanDeactivate++;
			return canDeactivate;
		}

		public void verifyCalledCanDeactivate(int times) {
			assertEquals(times, timesCalledCanDeactivate);
		}

	}

	private TaskActivityManager taskActivityManager;

	private TaskList taskList;

	private TaskRepository repository;

	private AbstractTask task1;

	private AbstractTask task2;

	@Override
	protected void setUp() throws Exception {
		taskActivityManager = TasksUiPlugin.getTaskActivityManager();
		taskActivityManager.deactivateActiveTask();
		taskActivityManager.clear();
		taskList = TasksUiPlugin.getTaskList();

		TaskTestUtil.resetTaskListAndRepositories();

		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
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

	public void testTaskActivationExtension() {
		if (MockTaskActivationListenerExtension.INSTANCE != null) {
			MockTaskActivationListenerExtension.INSTANCE.reset();
		}

		MockTask task = new MockTask("test:activation");
		try {
			taskActivityManager.activateTask(task);
			assertNotNull("Expected creation of task activation listener instance",
					MockTaskActivationListenerExtension.INSTANCE);
			assertTrue(MockTaskActivationListenerExtension.INSTANCE.hasPreActivated);
			assertTrue(MockTaskActivationListenerExtension.INSTANCE.hasActivated);
			assertFalse(MockTaskActivationListenerExtension.INSTANCE.hasPreDeactivated);
			assertFalse(MockTaskActivationListenerExtension.INSTANCE.hasDeactivated);

			MockTaskActivationListenerExtension.INSTANCE.reset();
		} finally {
			taskActivityManager.deactivateTask(task);
		}
		assertFalse(MockTaskActivationListenerExtension.INSTANCE.hasPreActivated);
		assertFalse(MockTaskActivationListenerExtension.INSTANCE.hasActivated);
		assertTrue(MockTaskActivationListenerExtension.INSTANCE.hasPreDeactivated);
		assertTrue(MockTaskActivationListenerExtension.INSTANCE.hasDeactivated);
	}

	public void testTaskActivationExtensionInstanceCount() {
		MockTask task = new MockTask("test:activation");
		try {
			taskActivityManager.activateTask(task);
			assertNotNull("Expected creation of task activation listener instance",
					MockTaskActivationListenerExtension.INSTANCE);
			assertEquals(1, MockTaskActivationListenerExtension.INSTANCE_COUNT);
		} finally {
			taskActivityManager.deactivateTask(task);
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
		initializeTasks();
		taskActivityManager.activateTask(task2);
		assertEquals(task2, taskActivityManager.getActiveTask());

		taskActivityManager.deactivateActiveTask();
		assertNull(taskActivityManager.getActiveTask());
	}

	public void testActivateNonActiveTaskCanDeactivate() {
		initializeTasks();
		assertActivateNonActiveTaskCanDeactivate(true, task2);
	}

	public void testActivateNonActiveTaskCannotDeactivate() {
		initializeTasks();
		assertActivateNonActiveTaskCanDeactivate(false, task1);
	}

	private void assertActivateNonActiveTaskCanDeactivate(final boolean canDeactivate, ITask expectedActiveTask) {
		MockTaskActivationListener listener = new MockTaskActivationListener(canDeactivate);

		try {
			taskActivityManager.addActivationListener(listener);

			taskActivityManager.activateTask(task1);
			assertEquals(task1, taskActivityManager.getActiveTask());

			taskActivityManager.activateTask(task2);
			assertEquals(expectedActiveTask, taskActivityManager.getActiveTask());
			listener.verifyCalledCanDeactivate(1);
		} finally {
			taskActivityManager.removeActivationListener(listener);
		}
	}

	public void testDeactivateTaskCanDeactivate() {
		initializeTasks();
		assertDeactivateTaskCanDeactivate(true, null);
	}

	public void testDeactivateTaskCannotDeactivate() {
		initializeTasks();
		assertDeactivateTaskCanDeactivate(false, task1);
	}

	private void assertDeactivateTaskCanDeactivate(final boolean canDeactivate, ITask expectedActiveTask) {
		MockTaskActivationListener listener = new MockTaskActivationListener(canDeactivate);

		try {
			taskActivityManager.addActivationListener(listener);

			taskActivityManager.activateTask(task1);
			assertEquals(task1, taskActivityManager.getActiveTask());

			taskActivityManager.deactivateTask(task1);
			assertEquals(expectedActiveTask, taskActivityManager.getActiveTask());
			listener.verifyCalledCanDeactivate(1);
		} finally {
			taskActivityManager.removeActivationListener(listener);
		}
	}

	public void testDeactivateTaskOnInactiveTask() {
		initializeTasks();
		taskActivityManager.deactivateTask(task1);
		assertNull(taskActivityManager.getActiveTask());
	}

	public void testMoveActivity() {
		initializeTasks();
		Calendar end = TaskActivityUtil.getNextWeek().getEndDate();
		Calendar start = TaskActivityUtil.getCalendar();
		start.setTimeInMillis(end.getTimeInMillis());
		TaskActivityUtil.snapStartOfDay(start);
		taskActivityManager.setScheduledFor(task1, new DateRange(start, end));
		taskActivityManager.setDueDate(task1, new Date(start.getTimeInMillis() + 1));
		assertEquals(Collections.singleton(task1), taskActivityManager.getScheduledTasks(start, end));
		assertEquals(Collections.singleton(task1), taskActivityManager.getDueTasks(start, end));
		taskActivityManager.activateTask(task1);

		taskActivityManager.moveActivity(task1, task2);
		assertEquals(Collections.singleton(task2), taskActivityManager.getScheduledTasks(start, end));
		assertEquals(Collections.singleton(task2), taskActivityManager.getDueTasks(start, end));
		assertTrue(task2.isActive());
		assertEquals(task2, taskActivityManager.getActiveTask());
	}

	private void initializeTasks() {
		task1 = new LocalTask("task1", "description1");
		task2 = new LocalTask("task2", "description2");
		taskList.addTask(task1);
		taskList.addTask(task2);
		assertNull(taskActivityManager.getActiveTask());
	}

}