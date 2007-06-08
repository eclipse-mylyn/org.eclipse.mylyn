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

package org.eclipse.mylyn.tasks.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.DegreeOfInterest;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.ScalingFactors;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.DateRangeActivityDelegate;
import org.eclipse.mylyn.tasks.core.DateRangeContainer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.Task;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class TaskActivityTest extends TestCase {

	private long currentStartMili = 1200;

	private long currentEndMili = 1900;

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		// MylarTaskListPlugin.getTaskListManager().readExistingOrCreateNewList();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
		super.tearDown();
	}

	// TODO: Test scheduling into day bins
	public void testDaysOfWeek() {
		List<DateRangeContainer> days = TasksUiPlugin.getTaskListManager().getActivityWeekDays();
		assertNotNull(days);
		assertEquals(7, days.size());
	}

	public void testDateRangeContainer() {

		Calendar startDate = GregorianCalendar.getInstance();
		startDate.setTimeInMillis(1000);
		Calendar endDate = GregorianCalendar.getInstance();
		endDate.setTimeInMillis(2000);

		DateRangeContainer testContainer = new DateRangeContainer(startDate, endDate, "test date range container");
		assertTrue(testContainer.includes(startDate));
		assertTrue(testContainer.includes(endDate));
		Calendar midTime = GregorianCalendar.getInstance();
		midTime.setTimeInMillis(1500);
		assertTrue(testContainer.includes(midTime));

		ITask task1 = new Task("task 1", "Task 1");
		ITask task2 = new Task("task 2", "Task 2");

		Calendar currentTaskStart = GregorianCalendar.getInstance();
		currentTaskStart.setTimeInMillis(currentStartMili);
		Calendar currentTaskEnd = GregorianCalendar.getInstance();
		currentTaskEnd.setTimeInMillis(currentEndMili);
		testContainer
				.addTask(new DateRangeActivityDelegate(testContainer, task1, currentTaskStart, currentTaskEnd, 10));
		// assertEquals(currentEndMili - currentStartMili,
		// testContainer.getTotalElapsed());
		assertEquals(10, testContainer.getTotalElapsed());
		testContainer
				.addTask(new DateRangeActivityDelegate(testContainer, task2, currentTaskStart, currentTaskEnd, 10));
		assertEquals(20, testContainer.getTotalElapsed());
		// assertEquals(2 * (currentEndMili - currentStartMili),
		// testContainer.getTotalElapsed());
		assertEquals(2, testContainer.getDateRangeDelegates().size());
		testContainer
				.addTask(new DateRangeActivityDelegate(testContainer, task2, currentTaskStart, currentTaskEnd, 10));

		assertEquals(30, testContainer.getTotalElapsed());
		// assertEquals(3 * (currentEndMili - currentStartMili),
		// testContainer.getTotalElapsed());
		// assertEquals(2 * (currentEndMili - currentStartMili),
		// testContainer.getElapsed(new DateRangeActivityDelegate(
		// testContainer, task2, currentTaskStart, currentTaskEnd)));
		assertEquals(20, testContainer.getElapsed(new DateRangeActivityDelegate(testContainer, task2, currentTaskStart,
				currentTaskEnd)));
		assertEquals(2, testContainer.getDateRangeDelegates().size());
	}

	// public void testNegativeInactivity() {
	// Calendar startDate = GregorianCalendar.getInstance();
	// startDate.setTimeInMillis(1000);
	// Calendar endDate = GregorianCalendar.getInstance();
	// endDate.setTimeInMillis(2000);
	// ITask task1 = new Task("task 1", "Task 1", true);
	// DateRangeContainer testContainer = new DateRangeContainer(startDate,
	// endDate, "test date range container", taskList);
	// testContainer.addTask(new DateRangeActivityDelegate(testContainer, task1,
	// startDate, endDate, 3000));
	// assertEquals(0, testContainer.getTotalElapsed());
	// }

	public void testTaskListManagerActivity() {

		ITask task1 = new Task("task 1", "Task 1");
		ITask task2 = new Task("task 2", "Task 2");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task2);

		// test this week
		DateRangeContainer thisWeekActivity = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(thisWeekActivity);
		assertEquals(0, thisWeekActivity.getChildren().size());
		assertEquals(0, thisWeekActivity.getTotalElapsed());
		Calendar thisWeekCalendarStart = GregorianCalendar.getInstance();
		thisWeekCalendarStart.setTime(thisWeekActivity.getStart().getTime());
		Calendar thisWeekCalendarStop = GregorianCalendar.getInstance();
		thisWeekCalendarStop.setTime(thisWeekActivity.getStart().getTime());
		thisWeekCalendarStop.add(Calendar.MILLISECOND, 2);
		assertTrue(thisWeekActivity.includes(thisWeekCalendarStart));

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, thisWeekCalendarStart.getTime(), thisWeekCalendarStart.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, thisWeekCalendarStop.getTime(),
				thisWeekCalendarStop.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, thisWeekActivity.getChildren().size());
		assertEquals(0, thisWeekActivity.getTotalElapsed());
		// assertEquals(thisWeekCalendarStop.getTime().getTime() -
		// thisWeekCalendarStart.getTime().getTime(),
		// thisWeekActivity.getTotalElapsed());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, thisWeekActivity.getChildren().size());
		// assertEquals(2 * (thisWeekCalendarStop.getTime().getTime() -
		// thisWeekCalendarStart.getTime().getTime()),
		// thisWeekActivity.getTotalElapsed());
		// assertEquals(2 * (thisWeekCalendarStop.getTime().getTime() -
		// thisWeekCalendarStart.getTime().getTime()),
		// thisWeekActivity.getElapsed(new
		// DateRangeActivityDelegate(thisWeekActivity, task1, null, null)));

		// multiple tasks in category
		event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2.getHandleIdentifier(),
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f,
				thisWeekCalendarStart.getTime(), thisWeekCalendarStart.getTime());
		event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2.getHandleIdentifier(),
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f,
				thisWeekCalendarStop.getTime(), thisWeekCalendarStop.getTime());
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(2, thisWeekActivity.getChildren().size());

		// test Past
		DateRangeContainer pastActivity = TasksUiPlugin.getTaskListManager().getActivityPast();
		assertNotNull(pastActivity);
		assertEquals(0, pastActivity.getChildren().size());

		InteractionEvent event3 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, pastActivity.getStart().getTime(), pastActivity.getStart().getTime());
		InteractionEvent event4 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, pastActivity.getEnd().getTime(), pastActivity
						.getEnd().getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event3);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event4);
		assertEquals(1, pastActivity.getChildren().size());

		// test Future
		DateRangeContainer futureActivity = TasksUiPlugin.getTaskListManager().getActivityFuture();
		assertNotNull(futureActivity);
		assertEquals(0, futureActivity.getChildren().size());

		InteractionEvent event5 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, futureActivity.getStart().getTime(), futureActivity.getStart().getTime());
		InteractionEvent event6 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, futureActivity.getEnd().getTime(), futureActivity
						.getEnd().getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event5);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event6);
		// No longer adding activity to future bins (days of week, next week, or
		// future)
		assertEquals(0, futureActivity.getChildren().size());

		// test Next week activity
		DateRangeContainer activityNextWeek = TasksUiPlugin.getTaskListManager().getActivityNextWeek();
		assertNotNull(activityNextWeek);
		assertEquals(0, activityNextWeek.getChildren().size());

		InteractionEvent event7 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, activityNextWeek.getStart().getTime(), activityNextWeek.getStart().getTime());
		InteractionEvent event8 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, activityNextWeek.getEnd().getTime(),
				activityNextWeek.getEnd().getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event7);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event8);
		// No longer adding activity to future bins (days of week, next week, or
		// future)
		assertEquals(0, activityNextWeek.getChildren().size());

		// test Previous week activity
		DateRangeContainer activityPreviousWeek = TasksUiPlugin.getTaskListManager().getActivityPrevious();
		assertNotNull(activityPreviousWeek);
		assertEquals(0, activityPreviousWeek.getChildren().size());

		InteractionEvent event9 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, activityPreviousWeek.getStart().getTime(), activityPreviousWeek.getStart().getTime());
		InteractionEvent event10 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, activityPreviousWeek.getEnd().getTime(),
				activityPreviousWeek.getEnd().getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event9);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event10);
		assertEquals(1, activityPreviousWeek.getChildren().size());
	}

	/**
	 * Some 'attention' events when all tasks are inactive
	 * 
	 * @author Yuri Baburov (burchik@gmail.com)
	 */
	public void testTaskListManagerActivity2() {
		ITask task1 = new Task("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		DateRangeContainer thisWeekActivity = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(thisWeekActivity);
		assertEquals(0, thisWeekActivity.getChildren().size());
		assertEquals(0, thisWeekActivity.getTotalElapsed());
		thisWeekActivity.getStart().setTimeInMillis(1149490800000L); // Start
		// of
		// the
		// week
		// Jun 5
		// 2006
		// - Jun
		// 11
		// 2006,
		// NOVST
		thisWeekActivity.getEnd().setTimeInMillis(1150095600000L); // End of
		// the week

		Date time1 = new Date(1149911820812L); // Sat Jun 10 10:57:00 NOVST
		// 2006 - task 1 - activated
		Date time2 = new Date(1149911820812L); // Sat Jun 10 10:57:00 NOVST
		// 2006 - task 1 - deactivated
		Date time3 = new Date(1149911840812L); // Sat Jun 10 10:57:20 NOVST
		// 2006 - attention -
		// deactivated
		Date time4 = new Date(1149911941765L); // Sat Jun 10 10:59:01 NOVST
		// 2006 - attention - activated
		Date time5 = new Date(1149911948953L); // Sat Jun 10 10:59:08 NOVST
		// 2006 - task 1 - activated
		Date time6 = new Date(1149911988781L); // Sat Jun 10 10:59:48 NOVST
		// 2006 - task 1 - deactivated

		String task1handle = task1.getHandleIdentifier();
		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time1, time1);
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time2, time2);
		InteractionEvent event3 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", "attention",
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time3, time3);
		InteractionEvent event4 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", "attention",
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time4, time4);
		InteractionEvent event5 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time5, time5);
		InteractionEvent event6 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time6, time6);

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event3);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event4);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event5);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event6);
		assertEquals(1, thisWeekActivity.getChildren().size());
		long expectedTotalTime = 0;// time6.getTime() - time5.getTime() +
		// time2.getTime() - time1.getTime();
		assertEquals(expectedTotalTime, thisWeekActivity.getTotalElapsed());
		assertEquals(expectedTotalTime, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));
		assertEquals(expectedTotalTime, thisWeekActivity.getElapsed(new DateRangeActivityDelegate(thisWeekActivity,
				task1, null, null)));
	}

	/**
	 * Task with some inner 'attention' events
	 * 
	 * @author Yuri Baburov (burchik@gmail.com)
	 */
	public void testTaskListManagerActivity3() {
		ITask task1 = new Task("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		DateRangeContainer thisWeekActivity = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(thisWeekActivity);
		assertEquals(0, thisWeekActivity.getChildren().size());
		assertEquals(0, thisWeekActivity.getTotalElapsed());
		thisWeekActivity.getStart().setTimeInMillis(1149490800000L); // Start
		// of
		// the
		// week
		// Jun 5
		// 2006
		// - Jun
		// 11
		// 2006,
		// NOVST
		thisWeekActivity.getEnd().setTimeInMillis(1150095600000L); // End of
		// the week

		Date time1 = new Date(1150007053171L); // Sun Jun 11 13:24:13 NOVST
		// 2006 - task 1 - activated
		Date time2 = new Date(1150007263468L); // Sun Jun 11 13:27:43 NOVST
		// 2006 - attention -
		// deactivated
		Date time3 = new Date(1150021535953L); // Sun Jun 11 17:25:35 NOVST
		// 2006 - attention - activated
		Date time4 = new Date(1150021658500L); // Sun Jun 11 17:27:38 NOVST
		// 2006 - attention -
		// deactivated
		Date time5 = new Date(1150031089250L); // Sun Jun 11 20:04:49 NOVST
		// 2006 - attention - activated
		Date time6 = new Date(1150031111578L); // Sun Jun 11 20:05:11 NOVST
		// 2006 - attention -
		// deactivated
		Date time7 = new Date(1150031111578L); // Sun Jun 11 20:05:11 NOVST
		// 2006 - task 1 - deactivated

		String task1handle = task1.getHandleIdentifier();
		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time1, time1);

		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", "attention",
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time1, time2);
		InteractionEvent event3 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", "attention",
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time3, time4);
		// InteractionEvent event4 = new
		// InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
		// "attention",
		// "originId", "navigatedRelation",
		// MylarContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time4, time4);
		InteractionEvent event5 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", "attention",
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time5, time6);
		// InteractionEvent event6 = new
		// InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
		// "attention",
		// "originId", "navigatedRelation",
		// MylarContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time6, time6);
		InteractionEvent event7 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time7, time7);

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event3);
		// TasksUiPlugin.getTaskListManager().parseInteractionEvent(event4);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event5);
		// TasksUiPlugin.getTaskListManager().parseInteractionEvent(event6);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event7);
		assertEquals(1, thisWeekActivity.getChildren().size());
		long expectedTotalTime = time6.getTime() - time5.getTime() + time4.getTime() - time3.getTime()
				+ time2.getTime() - time1.getTime();
		assertEquals(expectedTotalTime, thisWeekActivity.getTotalElapsed());
		assertEquals(expectedTotalTime, thisWeekActivity.getElapsed(new DateRangeActivityDelegate(thisWeekActivity,
				task1, null, null)));
	}

	public void testTaskListManagerInactivity() {

		ITask task1 = new Task("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);

		DateRangeContainer activityThisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(activityThisWeek);
		assertEquals(0, activityThisWeek.getChildren().size());

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, activityThisWeek.getStart().getTime(), activityThisWeek.getStart().getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, activityThisWeek.getEnd().getTime(),
				activityThisWeek.getEnd().getTime());

		Calendar activityStart = GregorianCalendar.getInstance();
		Calendar activityEnd = GregorianCalendar.getInstance();
		activityEnd.add(Calendar.HOUR_OF_DAY, 1);

		// InteractionEvent inactivityEvent1 = new
		// InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
		// MylarContextManager.ACTIVITY_HANDLE_ATTENTION, "originId",
		// "navigatedRelation",
		// MylarContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f,
		// inactivityStart.getTime(), inactivityStart.getTime());
		// InteractionEvent inactivityEvent2 = new
		// InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
		// MylarContextManager.ACTIVITY_HANDLE_ATTENTION, "originId",
		// "navigatedRelation",
		// MylarContextManager.ACTIVITY_DELTA_ACTIVATED, 2f,
		// inactivityEnd.getTime(), inactivityEnd.getTime());

		InteractionEvent activityEvent = new InteractionEvent(InteractionEvent.Kind.COMMAND,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, InteractionContextManager.ACTIVITY_HANDLE_ATTENTION,
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null, InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f,
				activityStart.getTime(), activityEnd.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(activityEvent);
		// TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
		// TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent2);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, activityThisWeek.getChildren().size());

		// long expectedTotalTime =
		// (activityThisWeek.getEnd().getTime().getTime() -
		// activityThisWeek.getStart().getTime()
		// .getTime())
		// - (inactivityEnd.getTime().getTime() -
		// inactivityStart.getTime().getTime());
		//		

		long expectedTotalTime = (activityEnd.getTime().getTime() - activityStart.getTime().getTime());

		assertEquals(expectedTotalTime, activityThisWeek.getTotalElapsed());
		assertEquals(expectedTotalTime, activityThisWeek.getElapsed(new DateRangeActivityDelegate(activityThisWeek,
				task1, null, null)));

	}

	public void testInterleavedActivation() {

		ITask task1 = new Task("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);

		DateRangeContainer activityThisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(activityThisWeek);
		assertEquals(0, activityThisWeek.getChildren().size());

		Calendar taskActivationStart = GregorianCalendar.getInstance();
		taskActivationStart.add(Calendar.MILLISECOND, 15);
		Calendar taskActivationStop = GregorianCalendar.getInstance();
		taskActivationStop.add(Calendar.MILLISECOND, 25);

		Calendar inactivityStart1 = GregorianCalendar.getInstance();
		inactivityStart1.add(Calendar.MILLISECOND, 5);
		Calendar inactivityStop1 = GregorianCalendar.getInstance();
		inactivityStop1.add(Calendar.MILLISECOND, 10);

		Calendar inactivityStart2 = GregorianCalendar.getInstance();
		inactivityStart2.add(Calendar.MILLISECOND, 18);
		Calendar inactivityStop2 = GregorianCalendar.getInstance();
		inactivityStop2.add(Calendar.MILLISECOND, 25);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, taskActivationStart.getTime(), taskActivationStart.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, taskActivationStop.getTime(), taskActivationStop
						.getTime());

		InteractionEvent inactivityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				InteractionContextManager.ACTIVITY_HANDLE_ATTENTION, "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, inactivityStart1.getTime(), inactivityStop1
						.getTime());
		InteractionEvent inactivityEvent2 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				InteractionContextManager.ACTIVITY_HANDLE_ATTENTION, "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, inactivityStart2.getTime(), inactivityStop2.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent2);
		assertEquals(1, activityThisWeek.getChildren().size());

		// long expectedTotalTime = taskActivationStop.getTimeInMillis() -
		// taskActivationStart.getTimeInMillis();
		long expectedTotalTime = 0;
		assertEquals(expectedTotalTime, activityThisWeek.getTotalElapsed());
		assertEquals(expectedTotalTime, activityThisWeek.getElapsed(new DateRangeActivityDelegate(activityThisWeek,
				task1, null, null)));
	}

	public void testInterleavedActivation2() {

		ITask task1 = new Task("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);

		DateRangeContainer activityThisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(activityThisWeek);
		assertEquals(0, activityThisWeek.getChildren().size());

		Calendar taskActivationStart = GregorianCalendar.getInstance();
		taskActivationStart.add(Calendar.MILLISECOND, 10);
		Calendar taskActivationStop = GregorianCalendar.getInstance();
		taskActivationStop.add(Calendar.MILLISECOND, 25);

		Calendar inactivityStart = GregorianCalendar.getInstance();
		inactivityStart.add(Calendar.MILLISECOND, 15);
		Calendar inactivityStop = GregorianCalendar.getInstance();
		inactivityStop.add(Calendar.MILLISECOND, 20);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, taskActivationStart.getTime(), taskActivationStart.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, taskActivationStop.getTime(), taskActivationStop
						.getTime());

		InteractionEvent inactivityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				InteractionContextManager.ACTIVITY_HANDLE_ATTENTION, "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, inactivityStart.getTime(), inactivityStop.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);

		assertEquals(1, activityThisWeek.getChildren().size());

		long expectedTotalTime = 2 * (inactivityStart.getTimeInMillis() - taskActivationStart.getTimeInMillis());
		assertEquals(expectedTotalTime, activityThisWeek.getTotalElapsed());
		assertEquals(expectedTotalTime, activityThisWeek.getElapsed(new DateRangeActivityDelegate(activityThisWeek,
				task1, null, null)));
	}

	public void testResetAndRollOver() {

		DateRangeContainer pastWeeks = TasksUiPlugin.getTaskListManager().getActivityPast();
		DateRangeContainer previousWeek = TasksUiPlugin.getTaskListManager().getActivityPrevious();
		DateRangeContainer thisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		DateRangeContainer nextWeek = TasksUiPlugin.getTaskListManager().getActivityNextWeek();
		DateRangeContainer futureWeeks = TasksUiPlugin.getTaskListManager().getActivityFuture();

		assertTrue(thisWeek.isPresent());
		assertTrue(nextWeek.isFuture());

		long pastStartTime = pastWeeks.getEnd().getTimeInMillis();
		long previousStartTime = previousWeek.getStart().getTimeInMillis();
		long thisWeekStartTime = thisWeek.getStart().getTimeInMillis();
		long nextStartTime = nextWeek.getStart().getTimeInMillis();
		long futureStartTime = futureWeeks.getStart().getTimeInMillis();

		Calendar pastWeeksTaskStart = Calendar.getInstance();
		pastWeeksTaskStart.setTimeInMillis(pastStartTime - 10);
		assertTrue(pastWeeks.includes(pastWeeksTaskStart));

		Calendar previousWeekTaskStart = Calendar.getInstance();
		previousWeekTaskStart.setTimeInMillis(previousStartTime + 10);
		assertTrue(previousWeek.includes(previousWeekTaskStart));

		Calendar thisWeekTaskStart = Calendar.getInstance();
		thisWeekTaskStart.setTimeInMillis(thisWeekStartTime + 10);
		assertTrue(thisWeek.includes(thisWeekTaskStart));

		Calendar thisWeekTaskStop = Calendar.getInstance();
		thisWeekTaskStop.setTimeInMillis(thisWeek.getEnd().getTimeInMillis() - 10);
		assertTrue(thisWeek.includes(thisWeekTaskStop));

		Calendar nextWeekTaskStart = Calendar.getInstance();
		nextWeekTaskStart.setTimeInMillis(nextStartTime + 10);
		assertTrue(nextWeek.includes(nextWeekTaskStart));

		Calendar futureWeekTaskStart = Calendar.getInstance();
		futureWeekTaskStart.setTimeInMillis(futureStartTime + 10);
		assertTrue(futureWeeks.includes(futureWeekTaskStart));

		ITask task1 = new Task("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, thisWeekTaskStart.getTime(), thisWeekTaskStart.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, thisWeekTaskStop.getTime(), thisWeekTaskStop
						.getTime());

		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				InteractionContextManager.ACTIVITY_HANDLE_ATTENTION, "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, thisWeekTaskStart.getTime(), thisWeekTaskStop
						.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, thisWeek.getChildren().size());
		// assertEquals(thisWeekTaskStop.getTime().getTime() -
		// thisWeekTaskStart.getTime().getTime(), thisWeek
		// .getTotalElapsed());

		// ROLL OVER

		TasksUiPlugin.getTaskListManager().startTime = new Date(nextWeek.getStart().getTimeInMillis() + 10);
		TasksUiPlugin.getTaskListManager().resetAndRollOver();

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(activityEvent1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);

		DateRangeContainer newPastWeeks = TasksUiPlugin.getTaskListManager().getActivityPast();
		DateRangeContainer newPreviousWeek = TasksUiPlugin.getTaskListManager().getActivityPrevious();
		DateRangeContainer newThisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		DateRangeContainer newNextWeek = TasksUiPlugin.getTaskListManager().getActivityNextWeek();
		// DateRangeContainer newFutureWeeks =
		// MylarTaskListPlugin.getTaskListManager().getActivityFuture();

		assertTrue(newPastWeeks.includes(previousWeekTaskStart));
		assertTrue(newPreviousWeek.includes(thisWeekTaskStart));
		assertTrue(newThisWeek.includes(nextWeekTaskStart));
		assertTrue(newNextWeek.includes(futureWeekTaskStart));

		assertFalse(newThisWeek.includes(thisWeekTaskStart));
		assertFalse(newThisWeek.isPresent());
		assertTrue(newThisWeek.isFuture());

		assertEquals(1, newPreviousWeek.getChildren().size());
		assertEquals(thisWeekTaskStop.getTime().getTime() - thisWeekTaskStart.getTime().getTime(), newPreviousWeek
				.getTotalElapsed());
	}

	public void testAfterReloading() {
		ITask task1 = new Task("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);

		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.SECOND, 20);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,
				2f, startTime.getTime(), startTime.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, startTime.getTime(), startTime.getTime());

		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, InteractionContextManager.ACTIVITY_HANDLE_ATTENTION,
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null, InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f,
				startTime.getTime(), endTime.getTime());

		ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(activityEvent1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(activityEvent1);
		ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(event2);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(20000, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));

		ContextCorePlugin.getContextManager().saveActivityContext();
		ContextCorePlugin.getContextManager().loadActivityMetaContext();
		TasksUiPlugin.getTaskListManager().resetAndRollOver();

		assertEquals(20000, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));

	}

	public void testCollapsedTiming() {
		Calendar startTime1 = Calendar.getInstance();
		Calendar endTime1 = Calendar.getInstance();
		endTime1.add(Calendar.SECOND, 20);

		Calendar startTime2 = Calendar.getInstance();
		startTime2.setTimeInMillis(endTime1.getTimeInMillis());
		Calendar endTime2 = Calendar.getInstance();
		endTime2.setTimeInMillis(startTime2.getTimeInMillis() + 20 * 1000);

		InteractionContext mockContext = new InteractionContext("doitest", new ScalingFactors());
		DegreeOfInterest doi = new DegreeOfInterest(mockContext, InteractionContextManager.getScalingFactors());
		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, InteractionContextManager.ACTIVITY_HANDLE_ATTENTION,
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null, InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f,
				startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent2 = new InteractionEvent(InteractionEvent.Kind.COMMAND,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, InteractionContextManager.ACTIVITY_HANDLE_ATTENTION,
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null, InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f,
				startTime2.getTime(), endTime2.getTime());

		doi.addEvent(activityEvent1);
		doi.addEvent(activityEvent2);
		assertEquals(2, doi.getCollapsedEvents().size());
		InteractionEvent collapsed = doi.getCollapsedEvents().get(1);
		assertEquals(40 * 1000, collapsed.getEndDate().getTime() - collapsed.getDate().getTime());
	}

	public void testCollapsedExternalization() {

		Calendar startTime1 = Calendar.getInstance();
		Calendar endTime1 = Calendar.getInstance();
		endTime1.add(Calendar.SECOND, 20);

		Calendar startTime2 = Calendar.getInstance();
		startTime2.add(Calendar.DAY_OF_MONTH, 1);
		Calendar endTime2 = Calendar.getInstance();
		endTime2.add(Calendar.DAY_OF_MONTH, 1);
		endTime2.add(Calendar.SECOND, 20);

		ITask task1 = new Task("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		metaContext.reset();
		assertEquals(0, metaContext.getInteractionHistory().size());
		
		TasksUiPlugin.getTaskListManager().activateTask(task1);

		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, InteractionContextManager.ACTIVITY_HANDLE_ATTENTION,
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null, InteractionContextManager.ACTIVITY_DELTA_ACTIVATED,  endTime1.getTime().getTime() - startTime1.getTime().getTime(),
				startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent2 = new InteractionEvent(InteractionEvent.Kind.COMMAND,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, InteractionContextManager.ACTIVITY_HANDLE_ATTENTION,
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null, InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, endTime2.getTime().getTime() - startTime2.getTime().getTime(),
				startTime2.getTime(), endTime2.getTime());

		metaContext.parseEvent(activityEvent1);
		metaContext.parseEvent(activityEvent2);		
		TasksUiPlugin.getTaskListManager().deactivateAllTasks();
		assertEquals(4, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		TasksUiPlugin.getTaskListManager().saveTaskList();		
		ContextCorePlugin.getContextManager().saveActivityContext();
		ContextCorePlugin.getContextManager().getActivityMetaContext().reset();
		assertEquals(0, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		ContextCorePlugin.getContextManager().loadActivityMetaContext();
		
		// Only three remain as the two attention events have compressed into one
		assertEquals(3, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		assertEquals(0, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));
		
		TasksUiPlugin.getTaskListManager().resetAndRollOver();
		assertEquals((endTime1.getTimeInMillis() - startTime1.getTimeInMillis())
				+ (endTime2.getTimeInMillis() - startTime2.getTimeInMillis()), TasksUiPlugin.getTaskListManager()
				.getElapsedTime(task1));
	}

}
