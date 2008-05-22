/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Calendar;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListInterestFilter;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Rob Elves
 */
public class ScheduledPresentationTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		ContextCorePlugin.getContextManager().getActivityMetaContext().reset();
		ContextCorePlugin.getContextManager().saveActivityMetaContext();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getExternalizationManager().requestSave();
	}

	@Override
	protected void tearDown() throws Exception {
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getExternalizationManager().requestSave();
	}

	public void testWeekStartChange() {
		TasksUiPlugin.getTaskActivityManager().setWeekStartDay(Calendar.MONDAY);
		DateRange lastDay = TaskActivityUtil.getCurrentWeek().getDayOfWeek(Calendar.SUNDAY);
		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskList().addTask(task1);
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(task1, lastDay);
		assertTrue(TaskListInterestFilter.isInterestingForThisWeek(null, task1));

		TasksUiPlugin.getTaskActivityManager().setWeekStartDay(Calendar.SUNDAY);
		assertFalse(TaskListInterestFilter.isInterestingForThisWeek(null, task1));

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

	public void testScheduledTaskContainer() {

		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(1000);
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(2000);

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		AbstractTask task2 = new LocalTask("task 2", "Task 2");
		TasksUiPlugin.getTaskList().addTask(task1);
		TasksUiPlugin.getTaskList().addTask(task2);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
				InteractionContextManager.ACTIVITY_DELTA_ADDED, 2f, startDate.getTime(), endDate.getTime());

		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task2.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
				InteractionContextManager.ACTIVITY_DELTA_ADDED, 2f, startDate.getTime(), endDate.getTime());

		TasksUiPlugin.getTaskActivityMonitor().parseInteractionEvent(event1, false);
		TasksUiPlugin.getTaskActivityMonitor().parseInteractionEvent(event2, false);
		Calendar start = TaskActivityUtil.getCalendar();
		start.setTimeInMillis(0);
		Calendar end = TaskActivityUtil.getCalendar();
		end.add(Calendar.MINUTE, 2);
		Set<AbstractTask> tasks = TasksUiPlugin.getTaskActivityManager().getActiveTasks(start, end);
		assertEquals(2, tasks.size());
		assertEquals(1000, TasksUiPlugin.getTaskActivityManager().getElapsedTime(task1));
		assertEquals(1000, TasksUiPlugin.getTaskActivityManager().getElapsedTime(task2));

	}
}
