/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class ScheduledPresentationTest extends TestCase {

	protected void setUp() throws Exception {
		ContextCorePlugin.getContextManager().getActivityMetaContext().reset();
		ContextCorePlugin.getContextManager().saveActivityContext();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
	}

	protected void tearDown() throws Exception {
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
	}

	// TODO: Test scheduling into day bins
	public void testDaysOfWeek() {
		List<ScheduledTaskContainer> days = TasksUiPlugin.getTaskListManager().getActivityWeekDays();
		assertNotNull(days);
		assertEquals(7, days.size());
	}

	public void testResetAndRollOver() {

		ScheduledTaskContainer pastWeeks = TasksUiPlugin.getTaskListManager().getActivityPast();
		ScheduledTaskContainer thisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		ScheduledTaskContainer nextWeek = TasksUiPlugin.getTaskListManager().getActivityNextWeek();
		ScheduledTaskContainer futureWeeks = TasksUiPlugin.getTaskListManager().getActivityFuture();

		assertEquals(0, thisWeek.getChildren().size());
		assertTrue(thisWeek.isPresent());
		assertTrue(nextWeek.isFuture());

		long pastStartTime = pastWeeks.getEnd().getTimeInMillis();
		long thisWeekStartTime = thisWeek.getStart().getTimeInMillis();
		long nextStartTime = nextWeek.getStart().getTimeInMillis();
		long futureStartTime = futureWeeks.getStart().getTimeInMillis();

		Calendar pastWeeksTaskStart = Calendar.getInstance();
		pastWeeksTaskStart.setTimeInMillis(pastStartTime - 10);
		assertTrue(pastWeeks.includes(pastWeeksTaskStart));

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

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);

		assertEquals(0, TaskActivityManager.getInstance()
				.getScheduledTasks(thisWeek.getStart(), thisWeek.getEnd())
				.size());

		TaskActivityManager.getInstance().setScheduledFor(task1, thisWeek.getStart().getTime());

//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, TaskActivityManager.getInstance()
				.getScheduledTasks(thisWeek.getStart(), thisWeek.getEnd())
				.size());
		// assertEquals(thisWeekTaskStop.getTime().getTime() -
		// thisWeekTaskStart.getTime().getTime(), thisWeek
		// .getTotalElapsed());

		// ROLL OVER
		Date oldStart = TasksUiPlugin.getTaskListManager().startTime;
		Calendar newStart = Calendar.getInstance();
		newStart.setTime(oldStart);
		newStart.add(Calendar.WEEK_OF_MONTH, 1);
		//TasksUiPlugin.getTaskListManager().snapToStartOfWeek(newStart);

		TasksUiPlugin.getTaskListManager().resetAndRollOver(newStart.getTime());

		//ScheduledTaskContainer newPastWeeks = TasksUiPlugin.getTaskListManager().getActivityPast();
		ScheduledTaskContainer newPreviousWeek = TasksUiPlugin.getTaskListManager().getActivityPrevious();
		ScheduledTaskContainer newThisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		ScheduledTaskContainer newNextWeek = TasksUiPlugin.getTaskListManager().getActivityNextWeek();
		// DateRangeContainer newFutureWeeks =
		// MylarTaskListPlugin.getTaskListManager().getActivityFuture();

		assertTrue(newPreviousWeek.includes(thisWeekTaskStart));
		assertTrue(newThisWeek.includes(nextWeekTaskStart));
		assertTrue(newNextWeek.includes(futureWeekTaskStart));

		assertFalse(newThisWeek.includes(thisWeekTaskStart));
		assertFalse(newThisWeek.isPresent());
		assertTrue(newThisWeek.isFuture());

		assertEquals(0, TaskActivityManager.getInstance().getScheduledTasks(newThisWeek.getStart(),
				newThisWeek.getEnd()).size());
		assertEquals(1, TaskActivityManager.getInstance().getScheduledTasks(newPreviousWeek.getStart(),
				newPreviousWeek.getEnd()).size());

		TasksUiPlugin.getTaskListManager().resetAndRollOver(oldStart);
	}

	public void testScheduledTaskContainer() {

		Calendar startDate = GregorianCalendar.getInstance();
		startDate.setTimeInMillis(1000);
		Calendar endDate = GregorianCalendar.getInstance();
		endDate.setTimeInMillis(2000);

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		AbstractTask task2 = new LocalTask("task 2", "Task 2");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task2);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
				InteractionContextManager.ACTIVITY_DELTA_ADDED, 2f, startDate.getTime(), endDate.getTime());

		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task2.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
				InteractionContextManager.ACTIVITY_DELTA_ADDED, 2f, startDate.getTime(), endDate.getTime());

		TaskActivityManager.getInstance().parseInteractionEvent(event1);
		TaskActivityManager.getInstance().parseInteractionEvent(event2);

		ScheduledTaskContainer container = TasksUiPlugin.getTaskListManager().getActivityPast();
		assertEquals(2, container.getChildren().size());
		assertEquals(1000, container.getElapsed(task1));
		assertEquals(1000, container.getElapsed(task2));

	}

}
