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
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class ScheduledPresentationTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		ContextCore.getContextManager().getActivityMetaContext().reset();
		ContextCore.getContextManager().saveActivityContext();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
	}

	// TODO: Test scheduling into day bins
	public void testDaysOfWeek() {
		List<ScheduledTaskContainer> days = TasksUiPlugin.getTaskActivityManager().getActivityWeekDays();
		assertNotNull(days);
		assertEquals(7, days.size());
	}

	public void testResetAndRollOver() {

		ScheduledTaskContainer pastWeeks = TasksUiPlugin.getTaskActivityManager().getActivityPast();
		ScheduledTaskContainer thisWeek = TasksUiPlugin.getTaskActivityManager().getActivityThisWeek();
		ScheduledTaskContainer nextWeek = TasksUiPlugin.getTaskActivityManager().getActivityNextWeek();
		ScheduledTaskContainer futureWeeks = TasksUiPlugin.getTaskActivityManager().getActivityFuture();

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

		assertEquals(0, TasksUiPlugin.getTaskActivityManager()
				.getScheduledTasks(thisWeek.getStart(), thisWeek.getEnd())
				.size());

		TasksUiPlugin.getTaskActivityManager().setScheduledFor(task1, thisWeek.getStart().getTime());

//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(1, TasksUiPlugin.getTaskActivityManager()
				.getScheduledTasks(thisWeek.getStart(), thisWeek.getEnd())
				.size());
		// assertEquals(thisWeekTaskStop.getTime().getTime() -
		// thisWeekTaskStart.getTime().getTime(), thisWeek
		// .getTotalElapsed());

		// ROLL OVER
		Date oldStart = TasksUiPlugin.getTaskActivityManager().getStartTime();
		Calendar newStart = Calendar.getInstance();
		newStart.setTime(oldStart);
		newStart.add(Calendar.WEEK_OF_MONTH, 1);
		//TasksUiPlugin.getTaskListManager().snapToStartOfWeek(newStart);

		TasksUiPlugin.getTaskListManager().resetAndRollOver(newStart.getTime());

		//ScheduledTaskContainer newPastWeeks = TasksUiPlugin.getTaskListManager().getActivityPast();
		ScheduledTaskContainer newPreviousWeek = TasksUiPlugin.getTaskActivityManager().getActivityPrevious();
		ScheduledTaskContainer newThisWeek = TasksUiPlugin.getTaskActivityManager().getActivityThisWeek();
		ScheduledTaskContainer newNextWeek = TasksUiPlugin.getTaskActivityManager().getActivityNextWeek();
		// DateRangeContainer newFutureWeeks =
		// MylarTaskListPlugin.getTaskListManager().getActivityFuture();

		assertTrue(newPreviousWeek.includes(thisWeekTaskStart));
		assertTrue(newThisWeek.includes(nextWeekTaskStart));
		assertTrue(newNextWeek.includes(futureWeekTaskStart));

		assertFalse(newThisWeek.includes(thisWeekTaskStart));
		assertFalse(newThisWeek.isPresent());
		assertTrue(newThisWeek.isFuture());

		assertEquals(0, TasksUiPlugin.getTaskActivityManager().getScheduledTasks(newThisWeek.getStart(),
				newThisWeek.getEnd()).size());
		assertEquals(1, TasksUiPlugin.getTaskActivityManager().getScheduledTasks(newPreviousWeek.getStart(),
				newPreviousWeek.getEnd()).size());

		TasksUiPlugin.getTaskListManager().resetAndRollOver(oldStart);
	}

	public void testScheduledTaskContainer() {

		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(1000);
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(2000);

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		AbstractTask task2 = new LocalTask("task 2", "Task 2");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task2);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				IInteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task1.getHandleIdentifier(),
				IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
				IInteractionContextManager.ACTIVITY_DELTA_ADDED, 2f, startDate.getTime(), endDate.getTime());

		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				IInteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task2.getHandleIdentifier(),
				IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
				IInteractionContextManager.ACTIVITY_DELTA_ADDED, 2f, startDate.getTime(), endDate.getTime());

		TasksUiPlugin.getTaskActivityManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskActivityManager().parseInteractionEvent(event2);

		ScheduledTaskContainer container = TasksUiPlugin.getTaskActivityManager().getActivityPast();
		assertEquals(2, container.getChildren().size());
		assertEquals(1000, container.getElapsed(task1));
		assertEquals(1000, container.getElapsed(task2));

	}

}
