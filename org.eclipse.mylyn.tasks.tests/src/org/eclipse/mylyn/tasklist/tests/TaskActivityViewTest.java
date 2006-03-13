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

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.tasklist.DateRangeActivityDelegate;
import org.eclipse.mylar.provisional.tasklist.DateRangeContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskList;

/**
 * @author Rob Elves
 */
public class TaskActivityViewTest extends TestCase {

	private long currentStartMili = 1200;

	private long currentEndMili = 1900;

	private TaskList taskList;
	
	protected void setUp() throws Exception {
		super.setUp();
		taskList = MylarTaskListPlugin.getTaskListManager().getTaskList();
	}

	protected void tearDown() throws Exception {
//		MylarTaskListPlugin.getTaskListManager().readExistingOrCreateNewList();
		MylarTaskListPlugin.getTaskListManager().resetTaskList();
		super.tearDown();
	}

	public void testDateRangeContainer() {

		Calendar startDate = GregorianCalendar.getInstance();
		startDate.setTimeInMillis(1000);
		Calendar endDate = GregorianCalendar.getInstance();
		endDate.setTimeInMillis(2000);

		DateRangeContainer testContainer = new DateRangeContainer(startDate, endDate, "test date range container", taskList);
		assertTrue(testContainer.includes(startDate));
		assertTrue(testContainer.includes(endDate));
		Calendar midTime = GregorianCalendar.getInstance();
		midTime.setTimeInMillis(1500);
		assertTrue(testContainer.includes(midTime));

		ITask task1 = new Task("task 1", "Task 1", true);
		ITask task2 = new Task("task 2", "Task 2", true);

		Calendar currentTaskStart = GregorianCalendar.getInstance();
		currentTaskStart.setTimeInMillis(currentStartMili);
		Calendar currentTaskEnd = GregorianCalendar.getInstance();
		currentTaskEnd.setTimeInMillis(currentEndMili);
		testContainer.addTask(new DateRangeActivityDelegate(testContainer, task1, currentTaskStart, currentTaskEnd, 0));
		assertEquals(currentEndMili - currentStartMili, testContainer.getTotalElapsed());
		testContainer.addTask(new DateRangeActivityDelegate(testContainer, task2, currentTaskStart, currentTaskEnd, 0));
		assertEquals(2 * (currentEndMili - currentStartMili), testContainer.getTotalElapsed());
		assertEquals(2, testContainer.getChildren().size());
		testContainer.addTask(new DateRangeActivityDelegate(testContainer, task2, currentTaskStart, currentTaskEnd));
		assertEquals(3 * (currentEndMili - currentStartMili), testContainer.getTotalElapsed());
		assertEquals(2 * (currentEndMili - currentStartMili), testContainer.getElapsed(new DateRangeActivityDelegate(
				testContainer, task2, currentTaskStart, currentTaskEnd)));
		assertEquals(2, testContainer.getChildren().size());
	}
	
	public void testNegativeInactivity() {
		Calendar startDate = GregorianCalendar.getInstance();
		startDate.setTimeInMillis(1000);
		Calendar endDate = GregorianCalendar.getInstance();
		endDate.setTimeInMillis(2000);
		ITask task1 = new Task("task 1", "Task 1", true);
		DateRangeContainer testContainer = new DateRangeContainer(startDate, endDate, "test date range container", taskList);
		testContainer.addTask(new DateRangeActivityDelegate(testContainer, task1, startDate, endDate, 3000));
		assertEquals(0, testContainer.getTotalElapsed());
	}

	public void testTaskListManagerActivity() {

		ITask task1 = new Task("task 1", "Task 1", true);
		ITask task2 = new Task("task 2", "Task 2", true);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task1);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task2);

		// test this week
		DateRangeContainer thisWeekActivity = MylarTaskListPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(thisWeekActivity);
		assertEquals(0, thisWeekActivity.getChildren().size());

		Calendar thisWeekCalendarStart = GregorianCalendar.getInstance();
		Calendar thisWeekCalendarStop = GregorianCalendar.getInstance();
		thisWeekCalendarStop.add(Calendar.MILLISECOND, 2);
		assertTrue(thisWeekActivity.includes(thisWeekCalendarStart));

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f,
				thisWeekCalendarStart.getTime(), thisWeekCalendarStart.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f,
				thisWeekCalendarStop.getTime(), thisWeekCalendarStop.getTime());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, thisWeekActivity.getChildren().size());
		assertEquals(thisWeekCalendarStop.getTime().getTime() - thisWeekCalendarStart.getTime().getTime(),
				thisWeekActivity.getTotalElapsed());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, thisWeekActivity.getChildren().size());
		assertEquals(2 * (thisWeekCalendarStop.getTime().getTime() - thisWeekCalendarStart.getTime().getTime()),
				thisWeekActivity.getTotalElapsed());
		assertEquals(2 * (thisWeekCalendarStop.getTime().getTime() - thisWeekCalendarStart.getTime().getTime()),
				thisWeekActivity.getElapsed(new DateRangeActivityDelegate(thisWeekActivity, task1, null, null)));

		// multiple tasks in category
		event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2.getHandleIdentifier(),
				"originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f, thisWeekCalendarStart
						.getTime(), thisWeekCalendarStart.getTime());
		event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2.getHandleIdentifier(),
				"originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f, thisWeekCalendarStop
						.getTime(), thisWeekCalendarStop.getTime());
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(2, thisWeekActivity.getChildren().size());

		// test Past
		DateRangeContainer pastActivity = MylarTaskListPlugin.getTaskListManager().getActivityPast();
		assertNotNull(pastActivity);
		assertEquals(0, pastActivity.getChildren().size());

		InteractionEvent event3 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f,
				pastActivity.getStart().getTime(), pastActivity.getStart().getTime());
		InteractionEvent event4 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f,
				pastActivity.getEnd().getTime(), pastActivity.getEnd().getTime());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event3);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event4);
		assertEquals(1, pastActivity.getChildren().size());

		// test Future
		DateRangeContainer futureActivity = MylarTaskListPlugin.getTaskListManager().getActivityFuture();
		assertNotNull(futureActivity);
		assertEquals(0, futureActivity.getChildren().size());

		InteractionEvent event5 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f,
				futureActivity.getStart().getTime(), futureActivity.getStart().getTime());
		InteractionEvent event6 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f,
				futureActivity.getEnd().getTime(), futureActivity.getEnd().getTime());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event5);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event6);
		assertEquals(1, futureActivity.getChildren().size());

		// test Next week activity
		DateRangeContainer activityNextWeek = MylarTaskListPlugin.getTaskListManager().getActivityNextWeek();
		assertNotNull(activityNextWeek);
		assertEquals(0, activityNextWeek.getChildren().size());

		InteractionEvent event7 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f,
				activityNextWeek.getStart().getTime(), activityNextWeek.getStart().getTime());
		InteractionEvent event8 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f,
				activityNextWeek.getEnd().getTime(), activityNextWeek.getEnd().getTime());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event7);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event8);
		assertEquals(1, activityNextWeek.getChildren().size());

		// test Previous week activity
		DateRangeContainer activityPreviousWeek = MylarTaskListPlugin.getTaskListManager().getActivityPrevious();
		assertNotNull(activityPreviousWeek);
		assertEquals(0, activityPreviousWeek.getChildren().size());

		InteractionEvent event9 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f,
				activityPreviousWeek.getStart().getTime(), activityPreviousWeek.getStart().getTime());
		InteractionEvent event10 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f,
				activityPreviousWeek.getEnd().getTime(), activityPreviousWeek.getEnd().getTime());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event9);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event10);
		assertEquals(1, activityPreviousWeek.getChildren().size());
	}

	public void testTaskListManagerInactivity() {

		ITask task1 = new Task("task 1", "Task 1", true);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task1);

		DateRangeContainer activityThisWeek = MylarTaskListPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(activityThisWeek);
		assertEquals(0, activityThisWeek.getChildren().size());

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f,
				activityThisWeek.getStart().getTime(), activityThisWeek.getStart().getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f,
				activityThisWeek.getEnd().getTime(), activityThisWeek.getEnd().getTime());

		Calendar inactivityStart = GregorianCalendar.getInstance();
		Calendar inactivityEnd = GregorianCalendar.getInstance();
		inactivityEnd.add(Calendar.HOUR_OF_DAY, 1);

		InteractionEvent inactivityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				MylarContextManager.ACTIVITY_HANDLE, "originId", "navigatedRelation",
				MylarContextManager.ACTIVITY_DEACTIVATED, 2f, inactivityStart.getTime(), inactivityStart.getTime());
		InteractionEvent inactivityEvent2 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				MylarContextManager.ACTIVITY_HANDLE, "originId", "navigatedRelation",
				MylarContextManager.ACTIVITY_ACTIVATED, 2f, inactivityEnd.getTime(), inactivityEnd.getTime());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent2);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, activityThisWeek.getChildren().size());

		long expectedTotalTime = (activityThisWeek.getEnd().getTime().getTime() - activityThisWeek.getStart().getTime()
				.getTime())
				- (inactivityEnd.getTime().getTime() - inactivityStart.getTime().getTime());
		assertEquals(expectedTotalTime, activityThisWeek.getTotalElapsed());
		assertEquals(expectedTotalTime, activityThisWeek.getElapsed(new DateRangeActivityDelegate(activityThisWeek,
				task1, null, null)));

	}

	// attention:Deactivated -> task:Activated -> attention:Activated ->
	// task:Deactivated
	public void testInterleavedActivation() {

		ITask task1 = new Task("task 1", "Task 1", true);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task1);

		DateRangeContainer activityThisWeek = MylarTaskListPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(activityThisWeek);
		assertEquals(0, activityThisWeek.getChildren().size());

		Calendar taskActivationStart = GregorianCalendar.getInstance();
		taskActivationStart.add(Calendar.MILLISECOND, 15);
		Calendar taskActivationStop = GregorianCalendar.getInstance();
		taskActivationStop.add(Calendar.MILLISECOND, 25);

		Calendar inactivityStart = GregorianCalendar.getInstance();
		inactivityStart.add(Calendar.MILLISECOND, 5);
		Calendar inactivityStop = GregorianCalendar.getInstance();
		inactivityStop.add(Calendar.MILLISECOND, 18);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f,
				taskActivationStart.getTime(), taskActivationStart.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f,
				taskActivationStop.getTime(), taskActivationStop.getTime());

		InteractionEvent inactivityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				MylarContextManager.ACTIVITY_HANDLE, "originId", "navigatedRelation",
				MylarContextManager.ACTIVITY_DEACTIVATED, 2f, inactivityStart.getTime(), inactivityStart.getTime());
		InteractionEvent inactivityEvent2 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				MylarContextManager.ACTIVITY_HANDLE, "originId", "navigatedRelation",
				MylarContextManager.ACTIVITY_ACTIVATED, 2f, inactivityStop.getTime(), inactivityStop.getTime());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent2);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, activityThisWeek.getChildren().size());

		long expectedTotalTime = taskActivationStop.getTimeInMillis() - taskActivationStart.getTimeInMillis();
		assertEquals(expectedTotalTime, activityThisWeek.getTotalElapsed());
		assertEquals(expectedTotalTime, activityThisWeek.getElapsed(new DateRangeActivityDelegate(activityThisWeek,
				task1, null, null)));
	}

	// task:Activated -> attention:Deactivated -> task:Deactivated ->
	// attention:Activated
	public void testInterleavedActivation2() {

		ITask task1 = new Task("task 1", "Task 1", true);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task1);

		DateRangeContainer activityThisWeek = MylarTaskListPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(activityThisWeek);
		assertEquals(0, activityThisWeek.getChildren().size());

		Calendar taskActivationStart = GregorianCalendar.getInstance();
		taskActivationStart.add(Calendar.MILLISECOND, 10);
		Calendar taskActivationStop = GregorianCalendar.getInstance();
		taskActivationStop.add(Calendar.MILLISECOND, 20);

		Calendar inactivityStart = GregorianCalendar.getInstance();
		inactivityStart.add(Calendar.MILLISECOND, 15);
		Calendar inactivityStop = GregorianCalendar.getInstance();
		inactivityStop.add(Calendar.MILLISECOND, 25);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_ACTIVATED, 2f,
				taskActivationStart.getTime(), taskActivationStart.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1
				.getHandleIdentifier(), "originId", "navigatedRelation", MylarContextManager.ACTIVITY_DEACTIVATED, 2f,
				taskActivationStop.getTime(), taskActivationStop.getTime());

		InteractionEvent inactivityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				MylarContextManager.ACTIVITY_HANDLE, "originId", "navigatedRelation",
				MylarContextManager.ACTIVITY_DEACTIVATED, 2f, inactivityStart.getTime(), inactivityStart.getTime());
		InteractionEvent inactivityEvent2 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
				MylarContextManager.ACTIVITY_HANDLE, "originId", "navigatedRelation",
				MylarContextManager.ACTIVITY_ACTIVATED, 2f, inactivityStop.getTime(), inactivityStop.getTime());

		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(event2);
		MylarTaskListPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent2);
		assertEquals(1, activityThisWeek.getChildren().size());

		long expectedTotalTime = inactivityStart.getTimeInMillis() - taskActivationStart.getTimeInMillis();
		assertEquals(expectedTotalTime, activityThisWeek.getTotalElapsed());
		assertEquals(expectedTotalTime, activityThisWeek.getElapsed(new DateRangeActivityDelegate(activityThisWeek,
				task1, null, null)));
	}

}
