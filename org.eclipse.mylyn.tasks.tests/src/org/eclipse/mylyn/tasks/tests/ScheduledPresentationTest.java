/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.DayDateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListInterestFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Rob Elves
 */
public class ScheduledPresentationTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
//		ContextCorePlugin.getContextManager().getActivityMetaContext().reset();
//		ContextCorePlugin.getContextManager().saveActivityMetaContext();
		TaskTestUtil.resetTaskList();
		TasksUiPlugin.getExternalizationManager().requestSave();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskList();
		TasksUiPlugin.getExternalizationManager().requestSave();
	}

	public void testWeekStartChange() {
		TaskListInterestFilter filter = new TaskListInterestFilter();
		TasksUiPlugin.getTaskActivityManager().setWeekStartDay(Calendar.MONDAY);
		DateRange lastDay = TaskActivityUtil.getCurrentWeek().getDayOfWeek(Calendar.SUNDAY);
		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskList().addTask(task1);
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(task1, lastDay);
		assertTrue(filter.isInterestingForThisWeek(null, task1));

		TasksUiPlugin.getTaskActivityManager().setWeekStartDay(Calendar.SUNDAY);
		assertFalse(filter.isInterestingForThisWeek(null, task1));

	}

	public void testScheduledDisplayed() {
		DateRange lastDay = TaskActivityUtil.getDayOf(TaskActivityUtil.getEndOfCurrentWeek().getTime());
		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskList().addTask(task1);
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(task1, lastDay);

		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("testuser",
				"testpassword"), false);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		MockTask mockTask = new MockTask(MockRepositoryConnector.REPOSITORY_URL, "123", "mock task");
		mockTask.setOwner("anotheruser");

		// Scheduled for THIS WEEK and DUE TODAY and owned by somebody else  (bug#316657)
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(mockTask, TaskActivityUtil.getCurrentWeek());
		TasksUiPlugin.getTaskActivityManager().setDueDate(mockTask, TaskActivityUtil.getCalendar().getTime());
		TasksUiPlugin.getTaskList().addTask(mockTask);

		Map<ITask, ScheduledTaskContainer> results = new HashMap<ITask, ScheduledTaskContainer>();
		results.put(mockTask, null);
		results.put(task1, null);

		populateResults(results, false);
		assertNotNull("Task scheduled but not visible in scheduled presentation", results.get(mockTask));
		assertEquals("This Week", results.get(mockTask).getSummary());
		assertNotNull(results.get(task1));

		// Scheduled for NEXT WEEK AND INCOMING
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(mockTask, TaskActivityUtil.getNextWeek());
		mockTask.setSynchronizationState(SynchronizationState.INCOMING);
		results.put(mockTask, null);
		results.put(task1, null);

		// Should be revealed in Next Week since tasklist NOT in FOCUSED MODE
		populateResults(results, false);
		assertNotNull("Task scheduled but not visible in scheduled presentation", results.get(mockTask));
		assertEquals("Next Week", results.get(mockTask).getSummary());
		assertNotNull(results.get(task1));

		results.put(mockTask, null);
		results.put(task1, null);

		// Should be revealed in INCOMING since tasklist in FOCUSED MODE
		populateResults(results, true);
		assertNotNull("Task scheduled but not visible in scheduled presentation", results.get(mockTask));
		assertEquals("Incoming", results.get(mockTask).getSummary());
		assertNotNull(results.get(task1));

		results.put(mockTask, null);
		results.put(task1, null);

		// Scheduled and Due for a day next week 
		mockTask.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
		TasksUiPlugin.getTaskActivityManager().setDueDate(mockTask,
				TaskActivityUtil.getNextWeek().getDayOfWeek(3).getStartDate().getTime());
		populateResults(results, false);
		assertNotNull("Task scheduled but not visible in scheduled presentation", results.get(mockTask));
		assertEquals("Next Week", results.get(mockTask).getSummary());

		results.put(mockTask, null);
		results.put(task1, null);

		// Overscheduled
		DayDateRange dayDateRange = TaskActivityUtil.getDayOf(new Date(86400000));
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(mockTask, dayDateRange);
		mockTask.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
		results.put(mockTask, null);
		populateResults(results, false);
		assertNotNull("Task scheduled but not visible in scheduled presentation", results.get(mockTask));
		assertTrue(results.get(mockTask).getSummary().contains("Today"));

		// Scheduled for a specific DAY next week
		results.put(mockTask, null);
		TasksUiPlugin.getTaskActivityManager()
				.setScheduledFor(mockTask, TaskActivityUtil.getNextWeek().getDayOfWeek(3));
		mockTask.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
		populateResults(results, false);
		assertNotNull("Task scheduled but not visible in scheduled presentation", results.get(mockTask));
		assertEquals(TaskActivityUtil.getNextWeek().getDayOfWeek(3), results.get(mockTask).getDateRange());

		// Scheduled for a specific DAY next week with INCOMING_NEW
		results.put(mockTask, null);
		mockTask.setSynchronizationState(SynchronizationState.INCOMING_NEW);
		populateResults(results, true);
		assertNotNull("Task scheduled but not visible in scheduled presentation", results.get(mockTask));
		assertEquals("Incoming", results.get(mockTask).getSummary());

		// Scheduled for NEXT WEEK AND DUE on date next week
		mockTask.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
		TasksUiPlugin.getTaskActivityManager().setDueDate(mockTask,
				TaskActivityUtil.getNextWeek().getDayOfWeek(3).getStartDate().getTime());
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(mockTask, TaskActivityUtil.getNextWeek());
		mockTask.setOwner("testuser");
		results.put(mockTask, null);
		results.put(task1, null);

		// Should be revealed in date bin NOT Next Week day bin 
		populateResults(results, false);
		assertNotNull("Task scheduled but not visible in scheduled presentation", results.get(mockTask));
		assertFalse("Next Week".equals(results.get(mockTask).getSummary()));
	}

	private void populateResults(Map<ITask, ScheduledTaskContainer> results, boolean focused) {
		TaskListView.getFromActivePerspective().setFocusedMode(focused);
		TaskScheduleContentProvider provider = new TaskScheduleContentProvider(TaskListView.getFromActivePerspective());
		Object[] bins = provider.getElements(TaskListView.getFromActivePerspective().getViewSite());
		TaskListInterestFilter filter = new TaskListInterestFilter();
		for (Object object : bins) {
			if (focused) {
				if (!filter.select(null, object)) {
					continue;
				}
			}
			for (ITask task : ((ScheduledTaskContainer) object).getChildren()) {
				if (results.containsKey(task)) {
					results.put(task, (ScheduledTaskContainer) object);
				}
			}
		}
		if (focused) {
			TaskListView.getFromActivePerspective().setFocusedMode(!focused);
		}
	}

//	public void testResetAndRollOver() {
//
//		ScheduledTaskContainer pastWeeks = TasksUiPlugin.getTaskActivityManager().getActivityPast();
//		ScheduledTaskContainer thisWeek = TasksUiPlugin.getTaskActivityManager().getActivityThisWeek();
//		ScheduledTaskContainer nextWeek = TasksUiPlugin.getTaskActivityManager().getActivityNextWeek();
//		ScheduledTaskContainer futureWeeks = TasksUiPlugin.getTaskActivityManager().getActivityFuture();
//
//		assertEquals(0, thisWeek.getChildren().size());
//		assertTrue(thisWeek.isPresent());
//		assertTrue(nextWeek.isFuture());
//
//		long pastStartTime = pastWeeks.getEnd().getTimeInMillis();
//		long thisWeekStartTime = thisWeek.getStart().getTimeInMillis();
//		long nextStartTime = nextWeek.getStart().getTimeInMillis();
//		long futureStartTime = futureWeeks.getStart().getTimeInMillis();
//
//		Calendar pastWeeksTaskStart = Calendar.getInstance();
//		pastWeeksTaskStart.setTimeInMillis(pastStartTime - 10);
//		assertTrue(pastWeeks.includes(pastWeeksTaskStart));
//
//		Calendar thisWeekTaskStart = Calendar.getInstance();
//		thisWeekTaskStart.setTimeInMillis(thisWeekStartTime + 10);
//		assertTrue(thisWeek.includes(thisWeekTaskStart));
//
//		Calendar thisWeekTaskStop = Calendar.getInstance();
//		thisWeekTaskStop.setTimeInMillis(thisWeek.getEnd().getTimeInMillis() - 10);
//		assertTrue(thisWeek.includes(thisWeekTaskStop));
//
//		Calendar nextWeekTaskStart = Calendar.getInstance();
//		nextWeekTaskStart.setTimeInMillis(nextStartTime + 10);
//		assertTrue(nextWeek.includes(nextWeekTaskStart));
//
//		Calendar futureWeekTaskStart = Calendar.getInstance();
//		futureWeekTaskStart.setTimeInMillis(futureStartTime + 10);
//		assertTrue(futureWeeks.includes(futureWeekTaskStart));
//
//		AbstractTask task1 = new LocalTask("task 1", "Task 1");
//		TasksUiPlugin.getTaskList().addTask(task1);
//
//		assertEquals(0, TasksUiPlugin.getTaskActivityManager()
//				.getScheduledTasks(thisWeek.getStart(), thisWeek.getEnd())
//				.size());
//
//		TasksUiPlugin.getTaskActivityManager().setScheduledFor(task1, thisWeek.getStart().getTime());
//
////		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
//		assertEquals(1, TasksUiPlugin.getTaskActivityManager()
//				.getScheduledTasks(thisWeek.getStart(), thisWeek.getEnd())
//				.size());
//		// assertEquals(thisWeekTaskStop.getTime().getTime() -
//		// thisWeekTaskStart.getTime().getTime(), thisWeek
//		// .getTotalElapsed());
//
//		// ROLL OVER
//		Date oldStart = TasksUiPlugin.getTaskActivityManager().getStartTime();
//		Calendar newStart = Calendar.getInstance();
//		newStart.setTime(oldStart);
//		newStart.add(Calendar.WEEK_OF_MONTH, 1);
//		//TasksUiPlugin.getTaskListManager().snapToStartOfWeek(newStart);
//
//		TasksUiPlugin.getTaskActivityMonitor().reloadActivityTime();
//
//		//ScheduledTaskContainer newPastWeeks = TasksUiPlugin.getTaskListManager().getActivityPast();
//		ScheduledTaskContainer newPreviousWeek = TasksUiPlugin.getTaskActivityManager().getActivityPrevious();
//		ScheduledTaskContainer newThisWeek = TasksUiPlugin.getTaskActivityManager().getActivityThisWeek();
//		ScheduledTaskContainer newNextWeek = TasksUiPlugin.getTaskActivityManager().getActivityNextWeek();
//		// DateRangeContainer newFutureWeeks =
//		// MylarTaskListPlugin.getTaskListManager().getActivityFuture();
//
//		assertTrue(newPreviousWeek.includes(thisWeekTaskStart));
//		assertTrue(newThisWeek.includes(nextWeekTaskStart));
//		assertTrue(newNextWeek.includes(futureWeekTaskStart));
//
//		assertFalse(newThisWeek.includes(thisWeekTaskStart));
//		assertFalse(newThisWeek.isPresent());
//		assertTrue(newThisWeek.isFuture());
//
//		assertEquals(0, TasksUiPlugin.getTaskActivityManager().getScheduledTasks(newThisWeek.getStart(),
//				newThisWeek.getEnd()).size());
//		assertEquals(1, TasksUiPlugin.getTaskActivityManager().getScheduledTasks(newPreviousWeek.getStart(),
//				newPreviousWeek.getEnd()).size());
//
//		TasksUiPlugin.getTaskActivityMonitor().reloadActivityTime();
//	}

}
