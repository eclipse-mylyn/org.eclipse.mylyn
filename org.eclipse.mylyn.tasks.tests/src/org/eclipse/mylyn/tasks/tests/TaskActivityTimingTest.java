/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.LegacyActivityAdaptor;
import org.eclipse.mylyn.internal.context.core.ScalingFactors;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class TaskActivityTimingTest extends TestCase {

	TaskListManager manager = null;

	protected void setUp() throws Exception {
		super.setUp();
		manager = TasksUiPlugin.getTaskListManager();
	}

	protected void tearDown() throws Exception {
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
		super.tearDown();
	}

	public void testActivityCaptured() {
		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		manager.getTaskList().addTask(task1);
		assertEquals(0, manager.getElapsedTime(task1));

		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.HOUR_OF_DAY, 2);

		Calendar start2 = Calendar.getInstance();
		start2.add(Calendar.DAY_OF_MONTH, 1);
		Calendar end2 = Calendar.getInstance();
		end2.setTime(start2.getTime());
		end2.add(Calendar.HOUR_OF_DAY, 2);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION, "structureKind",
				task1.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 2f, start.getTime(), end.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION, "structureKind",
				task1.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 2f, start2.getTime(), end2.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);

		long expectedTotalTime = end.getTime().getTime() - start.getTime().getTime();
		assertEquals(2 * expectedTotalTime, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));
		assertEquals(expectedTotalTime, TasksUiPlugin.getTaskListManager().getElapsedTime(task1, start, end));
		assertEquals(expectedTotalTime, TasksUiPlugin.getTaskListManager().getElapsedTime(task1, start2, end2));
		assertEquals(2 * expectedTotalTime, TasksUiPlugin.getTaskListManager().getElapsedTime(task1, start, end2));
	}

	public void testNegativeActivity() {

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		manager.getTaskList().addTask(task1);
		assertEquals(0, manager.getElapsedTime(task1));

		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.HOUR_OF_DAY, 2);

		Calendar start2 = Calendar.getInstance();
		start2.add(Calendar.DAY_OF_MONTH, 1);
		Calendar end2 = Calendar.getInstance();
		end2.setTime(start2.getTime());
		end2.add(Calendar.HOUR_OF_DAY, 2);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION, "structureKind",
				task1.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 2f, end.getTime(), start.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION, "structureKind",
				task1.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 2f, end2.getTime(), start2.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);

		assertEquals(0, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));
		assertEquals(0, TasksUiPlugin.getTaskListManager().getElapsedTime(task1, start, end));
		assertEquals(0, TasksUiPlugin.getTaskListManager().getElapsedTime(task1, start2, end2));
		assertEquals(0, TasksUiPlugin.getTaskListManager().getElapsedTime(task1, start, end2));

	}

	public void testNullTaskHandle() {
		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		manager.getTaskList().addTask(task1);
		assertEquals(0, manager.getElapsedTime(task1));

		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.HOUR_OF_DAY, 2);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION, "structureKind", null,
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 2f,
				start.getTime(), end.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION, "structureKind", "",
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 2f,
				start.getTime(), end.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		assertEquals(0, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));
	}

	public void testActivityNotLoggedTwice() {

		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		metaContext.reset();
		assertEquals(0, metaContext.getInteractionHistory().size());

		AbstractTask task1 = new LocalTask("local 1", "Task 1");
		AbstractTask task2 = new LocalTask("local 2", "Task 2");

		Calendar startTime1 = Calendar.getInstance();
		manager.snapToStartOfHour(startTime1);
		Calendar endTime1 = Calendar.getInstance();
		endTime1.setTime(startTime1.getTime());
		endTime1.add(Calendar.SECOND, 20);

		Calendar startTime2 = Calendar.getInstance();
		startTime2.setTime(endTime1.getTime());
		startTime2.add(Calendar.SECOND, 20);
		Calendar endTime2 = Calendar.getInstance();
		endTime2.setTime(startTime2.getTime());
		endTime2.add(Calendar.MINUTE, 2);

		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime2.getTime(), endTime2.getTime());

		InteractionEvent activityEvent3 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task2.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime1.getTime(),
				startTime1.getTime());

		// to activity events both within same hour will get collapsed
		metaContext.parseEvent(activityEvent1);
		metaContext.parseEvent(activityEvent2);
		// This is a bogus 3rd event with zero activity and shouldn't be recorded
		// this use to result in a second write of activity1 to the context
		metaContext.parseEvent(activityEvent3);
		metaContext = ContextCorePlugin.getContextManager().collapseActivityMetaContext(metaContext);
		assertEquals(1, metaContext.getInteractionHistory().size());
	}

	public void testDoubleBookKeeping() {
		AbstractTask task1 = new LocalTask("testDoubleBookKeeping", "testDoubleBookKeeping");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		{
			Calendar startActiveTime = Calendar.getInstance();
			Calendar endActiveTime = Calendar.getInstance();
			endActiveTime.setTime(startActiveTime.getTime());
			endActiveTime.add(Calendar.SECOND, 20);

			Calendar startTime = Calendar.getInstance();
			startTime.setTimeInMillis(startActiveTime.getTimeInMillis() + 2000);
			Calendar endTime = Calendar.getInstance();
			endTime.setTimeInMillis(endActiveTime.getTimeInMillis() - 2000);

			InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
					task1.getHandleIdentifier(), "originId", "navigatedRelation",
					InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, startActiveTime.getTime(),
					startActiveTime.getTime());
			InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
					task1.getHandleIdentifier(), "originId", "navigatedRelation",
					InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, endActiveTime.getTime(),
					endActiveTime.getTime());

			InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
					InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
					InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
					InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime.getTime(),
					endTime.getTime());

			ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(event1);
			TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
			ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(activityEvent1);
			TasksUiPlugin.getTaskListManager().parseInteractionEvent(activityEvent1);
			ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(event2);
			TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);

			long elapsed = TasksUiPlugin.getTaskListManager().getElapsedTime(task1);
			assertEquals(16000, elapsed);

			// 2nd activation - no activity
			ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(event1);
			TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
			ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(event2);
			TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);

			elapsed = TasksUiPlugin.getTaskListManager().getElapsedTime(task1);
			assertEquals(16000, elapsed);
			assertTrue(TasksUiPlugin.getTaskListManager().isActiveThisWeek(task1));
		}

		assertEquals(16000, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));
		///--- 2nd activity on same task
		{
			Calendar startActiveTime2 = Calendar.getInstance();
			Calendar endActiveTime2 = Calendar.getInstance();
			endActiveTime2.add(Calendar.SECOND, 20);

			Calendar startTime2 = Calendar.getInstance();
			startTime2.setTimeInMillis(startActiveTime2.getTimeInMillis() + 2000);
			Calendar endTime2 = Calendar.getInstance();
			endTime2.setTimeInMillis(endActiveTime2.getTimeInMillis() - 2000);

			InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
					task1.getHandleIdentifier(), "originId", "navigatedRelation",
					InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, startActiveTime2.getTime(),
					startActiveTime2.getTime());
			InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
					task1.getHandleIdentifier(), "originId", "navigatedRelation",
					InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, endActiveTime2.getTime(),
					endActiveTime2.getTime());

			InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
					InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
					InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
					InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime2.getTime(),
					endTime2.getTime());

			ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(event1);
			TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
			ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(activityEvent1);
			TasksUiPlugin.getTaskListManager().parseInteractionEvent(activityEvent1);
			ContextCorePlugin.getContextManager().getActivityMetaContext().parseEvent(event2);
			TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
		}

		assertEquals(32000, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));

		ContextCorePlugin.getContextManager().saveActivityContext();
		ContextCorePlugin.getContextManager().loadActivityMetaContext();
		TasksUiPlugin.getTaskListManager().resetAndRollOver();

		assertEquals(32000, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));

	}

	public void testAfterReloading() {
		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);

		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.SECOND, 20);

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
				task1.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, startTime.getTime(), startTime.getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
				task1.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, startTime.getTime(), startTime.getTime());

		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime.getTime(), endTime.getTime());

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

		Calendar startTime3 = Calendar.getInstance();
		startTime3.setTimeInMillis(endTime2.getTimeInMillis());
		Calendar endTime3 = Calendar.getInstance();
		endTime3.setTimeInMillis(startTime3.getTimeInMillis() + 20 * 1000);

		InteractionContext mockContext = new InteractionContext("doitest", new ScalingFactors());
		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, "handle",
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, "handle",
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime2.getTime(), endTime2.getTime());

		InteractionEvent activityEvent3 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, "handle",
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime3.getTime(), endTime3.getTime());

		mockContext.parseEvent(activityEvent1);
		mockContext.parseEvent(activityEvent2);
		mockContext.parseEvent(activityEvent3);
		assertEquals(3, mockContext.getInteractionHistory().size());
		mockContext = ContextCorePlugin.getContextManager().collapseActivityMetaContext(mockContext);
		assertEquals(1, mockContext.getInteractionHistory().size());

		assertEquals(60 * 1000, mockContext.getInteractionHistory().get(0).getEndDate().getTime()
				- mockContext.getInteractionHistory().get(0).getDate().getTime());
	}

	/**
	 * test that total collapsed time is same when events are separated in time
	 */
	public void testCollapsedTiming2() {
		Calendar startTime1 = Calendar.getInstance();
		Calendar endTime1 = Calendar.getInstance();
		endTime1.add(Calendar.SECOND, 20);

		Calendar startTime2 = Calendar.getInstance();
		startTime2.setTimeInMillis(endTime1.getTimeInMillis());
		Calendar endTime2 = Calendar.getInstance();
		endTime2.setTimeInMillis(startTime2.getTimeInMillis() + 20 * 1000);

		Calendar startTime3 = Calendar.getInstance();
		startTime3.setTimeInMillis(endTime2.getTimeInMillis());
		Calendar endTime3 = Calendar.getInstance();
		endTime3.setTimeInMillis(startTime3.getTimeInMillis() + 50 * 1000);

		InteractionContext mockContext = new InteractionContext("doitest", new ScalingFactors());
		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, "handle",
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent3 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, "handle",
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime3.getTime(), endTime3.getTime());

		mockContext.parseEvent(activityEvent1);
		mockContext.parseEvent(activityEvent3);
		assertEquals(2, mockContext.getInteractionHistory().size());
		mockContext = ContextCorePlugin.getContextManager().collapseActivityMetaContext(mockContext);
		assertEquals(1, mockContext.getInteractionHistory().size());
		assertEquals(70 * 1000, mockContext.getInteractionHistory().get(0).getEndDate().getTime()
				- mockContext.getInteractionHistory().get(0).getDate().getTime());
	}

	public void testCollapsedExternalization() {

		Calendar startTime1 = Calendar.getInstance();
		Calendar endTime1 = Calendar.getInstance();
		endTime1.setTime(startTime1.getTime());
		endTime1.add(Calendar.SECOND, 20);

		Calendar startTime2 = Calendar.getInstance();
		startTime2.add(Calendar.DAY_OF_MONTH, 1);
		Calendar endTime2 = Calendar.getInstance();
		endTime2.setTime(startTime2.getTime());
		endTime2.add(Calendar.SECOND, 20);

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		metaContext.reset();
		assertEquals(0, metaContext.getInteractionHistory().size());

		TasksUiPlugin.getTaskListManager().activateTask(task1);

		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime2.getTime(), endTime2.getTime());

		metaContext.parseEvent(activityEvent1);
		metaContext.parseEvent(activityEvent2);
		TasksUiPlugin.getTaskListManager().deactivateAllTasks();
		assertEquals(4, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		TasksUiPlugin.getTaskListManager().saveTaskList();
		ContextCorePlugin.getContextManager().saveActivityContext();
		ContextCorePlugin.getContextManager().getActivityMetaContext().reset();
		assertEquals(0, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		ContextCorePlugin.getContextManager().loadActivityMetaContext();

		assertEquals(4, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		assertEquals(0, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));

		TasksUiPlugin.getTaskListManager().resetAndRollOver();
		assertEquals((endTime1.getTimeInMillis() - startTime1.getTimeInMillis())
				+ (endTime2.getTimeInMillis() - startTime2.getTimeInMillis()), TasksUiPlugin.getTaskListManager()
				.getElapsedTime(task1));
	}

	public void testCollapsedTwoTasks() {
		// test collapsing of attention events when two or more
		// task attention events occur sequentially
		Calendar startTime1 = Calendar.getInstance();
		Calendar endTime1 = Calendar.getInstance();
		endTime1.add(Calendar.SECOND, 20);

		Calendar startTime2 = Calendar.getInstance();
		startTime2.setTime(endTime1.getTime());
		startTime2.add(Calendar.SECOND, 2);
		Calendar endTime2 = Calendar.getInstance();
		endTime2.setTime(startTime2.getTime());
		endTime2.add(Calendar.SECOND, 20);

		InteractionContext mockContext = new InteractionContext("doitest", new ScalingFactors());
		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, "handle1",
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, "handle2",
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime2.getTime(), endTime2.getTime());

		mockContext.parseEvent(activityEvent1);
		mockContext.parseEvent(activityEvent2);

		// Since these event times are within same hour, normally they would get collapsed
		// here we test that if the event belongs to two different tasks remain discrete 

		assertEquals(2, mockContext.getInteractionHistory().size());
		mockContext = ContextCorePlugin.getContextManager().collapseActivityMetaContext(mockContext);
		assertEquals(2, mockContext.getInteractionHistory().size());
	}

	public void testCollapeedByTheHour() {
		Calendar startTime1 = Calendar.getInstance();
		startTime1.set(Calendar.MINUTE, 2);
		startTime1.set(Calendar.SECOND, 0);
		startTime1.set(Calendar.MILLISECOND, 0);

		Calendar endTime1 = Calendar.getInstance();
		endTime1.setTime(startTime1.getTime());
		endTime1.add(Calendar.MINUTE, 2);

		Calendar startTime2 = Calendar.getInstance();
		startTime2.add(Calendar.HOUR_OF_DAY, 1);
		startTime2.set(Calendar.MINUTE, 2);
		startTime2.set(Calendar.SECOND, 0);
		startTime2.set(Calendar.MILLISECOND, 0);

		Calendar endTime2 = Calendar.getInstance();
		endTime2.setTime(startTime2.getTime());
		endTime2.add(Calendar.MINUTE, 3);

		Calendar startTime3 = Calendar.getInstance();
		startTime3.add(Calendar.HOUR_OF_DAY, 1);
		startTime3.set(Calendar.MINUTE, 20);
		startTime3.set(Calendar.SECOND, 0);
		startTime3.set(Calendar.MILLISECOND, 0);

		Calendar endTime3 = Calendar.getInstance();
		endTime3.setTime(startTime3.getTime());
		endTime3.add(Calendar.MINUTE, 5);

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);

		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime2.getTime(), endTime2.getTime());

		InteractionEvent activityEvent3 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime3.getTime(), endTime3.getTime());

		List<InteractionEvent> events = new ArrayList<InteractionEvent>();
		events.add(activityEvent1);
		events.add(activityEvent2);
		events.add(activityEvent3);
		List<InteractionEvent> collapsedEvents = ContextCorePlugin.getContextManager().collapseEventsByHour(events);

		assertEquals(2, collapsedEvents.size());
	}

	public void testTaskListManagerInactivity() {

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);

		ScheduledTaskContainer activityThisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(activityThisWeek);
		assertEquals(0, activityThisWeek.getChildren().size());

		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
				task1.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, activityThisWeek.getStart().getTime(),
				activityThisWeek.getStart().getTime());
		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
				task1.getHandleIdentifier(), "originId", "navigatedRelation",
				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, activityThisWeek.getEnd().getTime(),
				activityThisWeek.getEnd().getTime());

		Calendar activityStart = GregorianCalendar.getInstance();
		Calendar activityEnd = GregorianCalendar.getInstance();
		activityEnd.add(Calendar.HOUR_OF_DAY, 1);

		InteractionEvent activityEvent = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, activityStart.getTime(),
				activityEnd.getTime());

		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(activityEvent);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);

		long expectedTotalTime = (activityEnd.getTime().getTime() - activityStart.getTime().getTime());
		assertEquals(expectedTotalTime, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));
	}

	public void testElapsedSameAfterRead() {
		// test that granularity of elapsed time map is retained after
		// being re-read from disk

		Calendar startTime1 = Calendar.getInstance();
		startTime1.set(Calendar.MINUTE, 2);
		startTime1.set(Calendar.SECOND, 0);
		startTime1.set(Calendar.MILLISECOND, 0);

		Calendar endTime1 = Calendar.getInstance();
		endTime1.setTime(startTime1.getTime());
		endTime1.add(Calendar.MINUTE, 2);

		Calendar startTime2 = Calendar.getInstance();
		startTime2.add(Calendar.HOUR_OF_DAY, 1);
		startTime2.set(Calendar.MINUTE, 2);
		startTime2.set(Calendar.SECOND, 0);
		startTime2.set(Calendar.MILLISECOND, 0);

		Calendar endTime2 = Calendar.getInstance();
		endTime2.setTime(startTime2.getTime());
		endTime2.add(Calendar.MINUTE, 3);

		Calendar startTime3 = Calendar.getInstance();
		startTime3.add(Calendar.HOUR_OF_DAY, 1);
		startTime3.set(Calendar.MINUTE, 20);
		startTime3.set(Calendar.SECOND, 0);
		startTime3.set(Calendar.MILLISECOND, 0);

		Calendar endTime3 = Calendar.getInstance();
		endTime3.setTime(startTime3.getTime());
		endTime3.add(Calendar.MINUTE, 5);

		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		TasksUiPlugin.getTaskListManager().activateTask(task1);
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		metaContext.reset();
		assertEquals(0, metaContext.getInteractionHistory().size());

		InteractionEvent activityEvent1 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime1.getTime(), endTime1.getTime());

		InteractionEvent activityEvent2 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime2.getTime(), endTime2.getTime());

		InteractionEvent activityEvent3 = new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				InteractionContextManager.ACTIVITY_STRUCTURE_KIND, task1.getHandleIdentifier(),
				InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 1f, startTime3.getTime(), endTime3.getTime());

		metaContext.parseEvent(activityEvent1);
		manager.parseInteractionEvent(activityEvent1);
		metaContext.parseEvent(activityEvent2);
		manager.parseInteractionEvent(activityEvent2);
		metaContext.parseEvent(activityEvent3);
		manager.parseInteractionEvent(activityEvent3);

		assertEquals(1000 * 60 * 10, manager.getElapsedTime(task1));
		assertEquals(1000 * 60 * 2, manager.getElapsedTime(task1, startTime1, endTime1));
		assertEquals(1000 * 60 * 8, manager.getElapsedTime(task1, startTime2, endTime2));

		TasksUiPlugin.getTaskListManager().deactivateAllTasks();
		assertEquals(4, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		TasksUiPlugin.getTaskListManager().saveTaskList();
		ContextCorePlugin.getContextManager().saveActivityContext();
		ContextCorePlugin.getContextManager().getActivityMetaContext().reset();
		assertEquals(0, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		TasksUiPlugin.getTaskListManager().resetAndRollOver();
		assertEquals(0, manager.getElapsedTime(task1));
		assertEquals(0, manager.getElapsedTime(task1, startTime1, endTime1));
		assertEquals(0, manager.getElapsedTime(task1, startTime2, endTime2));

		ContextCorePlugin.getContextManager().loadActivityMetaContext();
		assertEquals(3, ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size());
		TasksUiPlugin.getTaskListManager().resetAndRollOver();

		startTime1 = Calendar.getInstance();
		startTime1.set(Calendar.MINUTE, 0);
		startTime1.set(Calendar.SECOND, 0);
		startTime1.set(Calendar.MILLISECOND, 0);

		assertEquals(1000 * 60 * 10, manager.getElapsedTime(task1));
		assertEquals(1000 * 60 * 2, manager.getElapsedTime(task1, startTime1, endTime1));
		assertEquals(1000 * 60 * 8, manager.getElapsedTime(task1, startTime2, endTime2));
	}

	/**
	 * @author Yuri Baburov (burchik@gmail.com)
	 * @author Rob Elves adaption to test LegacyActivityAdaptor
	 */
	public void testLegacyTimingMigration() {
		AbstractTask task1 = new LocalTask("task 1", "Task 1");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		ScheduledTaskContainer thisWeekActivity = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
		assertNotNull(thisWeekActivity);
		assertEquals(0, thisWeekActivity.getChildren().size());
		assertEquals(0, thisWeekActivity.getTotalElapsed());
		thisWeekActivity.getStart().setTimeInMillis(1149490800000L);
		// Start
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
		InteractionEvent event5 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", "attention",
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time5, time6);
		InteractionEvent event7 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time7, time7);

		LegacyActivityAdaptor legacyAdaptor = new LegacyActivityAdaptor();
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(legacyAdaptor.parseInteractionEvent(event1));
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(legacyAdaptor.parseInteractionEvent(event2));
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(legacyAdaptor.parseInteractionEvent(event3));
		// TasksUiPlugin.getTaskListManager().parseInteractionEvent(event4);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(legacyAdaptor.parseInteractionEvent(event5));
		// TasksUiPlugin.getTaskListManager().parseInteractionEvent(event6);
		TasksUiPlugin.getTaskListManager().parseInteractionEvent(legacyAdaptor.parseInteractionEvent(event7));
		long expectedTotalTime = time6.getTime() - time5.getTime() + time4.getTime() - time3.getTime()
				+ time2.getTime() - time1.getTime();
		assertEquals(expectedTotalTime, manager.getElapsedTime(task1));
	}

// DND: OLD ACTIVITY TESTS - Will be using to test activity report/view 
//	public void testInterleavedActivation() {
//
//		AbstractTask task1 = new LocalTask("task 1", "Task 1");
//		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
//
//		ScheduledTaskContainer activityThisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
//		assertNotNull(activityThisWeek);
//		assertEquals(0, activityThisWeek.getChildren().size());
//
//		Calendar taskActivationStart = GregorianCalendar.getInstance();
//		taskActivationStart.add(Calendar.MILLISECOND, 15);
//		Calendar taskActivationStop = GregorianCalendar.getInstance();
//		taskActivationStop.add(Calendar.MILLISECOND, 25);
//
//		Calendar inactivityStart1 = GregorianCalendar.getInstance();
//		inactivityStart1.add(Calendar.MILLISECOND, 5);
//		Calendar inactivityStop1 = GregorianCalendar.getInstance();
//		inactivityStop1.add(Calendar.MILLISECOND, 10);
//
//		Calendar inactivityStart2 = GregorianCalendar.getInstance();
//		inactivityStart2.add(Calendar.MILLISECOND, 18);
//		Calendar inactivityStop2 = GregorianCalendar.getInstance();
//		inactivityStop2.add(Calendar.MILLISECOND, 25);
//
//		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, taskActivationStart.getTime(),
//				taskActivationStart.getTime());
//		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, taskActivationStop.getTime(),
//				taskActivationStop.getTime());
//
//		InteractionEvent inactivityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, inactivityStart1.getTime(),
//				inactivityStop1.getTime());
//		InteractionEvent inactivityEvent2 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, inactivityStart2.getTime(),
//				inactivityStop2.getTime());
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent2);
//		assertEquals(1, activityThisWeek.getChildren().size());
//
//		// long expectedTotalTime = taskActivationStop.getTimeInMillis() -
//		// taskActivationStart.getTimeInMillis();
//		long expectedTotalTime = 0;
//		assertEquals(expectedTotalTime, activityThisWeek.getTotalElapsed());
//		assertEquals(expectedTotalTime, activityThisWeek.getElapsed(new ScheduledTaskDelegate(activityThisWeek, task1,
//				null, null)));
//	}
//
//	public void testInterleavedActivation2() {
//
//		AbstractTask task1 = new LocalTask("task 1", "Task 1");
//		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
//
//		ScheduledTaskContainer activityThisWeek = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
//		assertNotNull(activityThisWeek);
//		assertEquals(0, activityThisWeek.getChildren().size());
//
//		Calendar taskActivationStart = GregorianCalendar.getInstance();
//		taskActivationStart.add(Calendar.MILLISECOND, 10);
//		Calendar taskActivationStop = GregorianCalendar.getInstance();
//		taskActivationStop.add(Calendar.MILLISECOND, 25);
//
//		Calendar inactivityStart = GregorianCalendar.getInstance();
//		inactivityStart.add(Calendar.MILLISECOND, 15);
//		Calendar inactivityStop = GregorianCalendar.getInstance();
//		inactivityStop.add(Calendar.MILLISECOND, 20);
//
//		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, taskActivationStart.getTime(),
//				taskActivationStart.getTime());
//		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, taskActivationStop.getTime(),
//				taskActivationStop.getTime());
//
//		InteractionEvent inactivityEvent1 = new InteractionEvent(InteractionEvent.Kind.COMMAND, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ATTENTION_ADD, 2f, inactivityStart.getTime(),
//				inactivityStop.getTime());
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(inactivityEvent1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
//
//		assertEquals(1, activityThisWeek.getChildren().size());
//
//		long expectedTotalTime = 2 * (inactivityStart.getTimeInMillis() - taskActivationStart.getTimeInMillis());
//		assertEquals(expectedTotalTime, activityThisWeek.getTotalElapsed());
//		assertEquals(expectedTotalTime, activityThisWeek.getElapsed(new ScheduledTaskDelegate(activityThisWeek, task1,
//				null, null)));
//	}
//
//
//	/**
//	 * Some 'attention' events when all tasks are inactive
//	 * 
//	 * @author Yuri Baburov (burchik@gmail.com)
//	 */
//	public void testTaskListManagerActivity2() {
//		AbstractTask task1 = new LocalTask("task 1", "Task 1");
//		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
//		ScheduledTaskContainer thisWeekActivity = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
//		assertNotNull(thisWeekActivity);
//		assertEquals(0, thisWeekActivity.getChildren().size());
//		assertEquals(0, thisWeekActivity.getTotalElapsed());
//		thisWeekActivity.getStart().setTimeInMillis(1149490800000L); // Start
//		// of
//		// the
//		// week
//		// Jun 5
//		// 2006
//		// - Jun
//		// 11
//		// 2006,
//		// NOVST
//		thisWeekActivity.getEnd().setTimeInMillis(1150095600000L); // End of
//		// the week
//
//		Date time1 = new Date(1149911820812L); // Sat Jun 10 10:57:00 NOVST
//		// 2006 - task 1 - activated
//		Date time2 = new Date(1149911820812L); // Sat Jun 10 10:57:00 NOVST
//		// 2006 - task 1 - deactivated
//		Date time3 = new Date(1149911840812L); // Sat Jun 10 10:57:20 NOVST
//		// 2006 - attention -
//		// deactivated
//		Date time4 = new Date(1149911941765L); // Sat Jun 10 10:59:01 NOVST
//		// 2006 - attention - activated
//		Date time5 = new Date(1149911948953L); // Sat Jun 10 10:59:08 NOVST
//		// 2006 - task 1 - activated
//		Date time6 = new Date(1149911988781L); // Sat Jun 10 10:59:48 NOVST
//		// 2006 - task 1 - deactivated
//
//		String task1handle = task1.getHandleIdentifier();
//		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
//				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time1, time1);
//		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
//				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time2, time2);
//		InteractionEvent event3 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", "attention",
//				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time3, time3);
//		InteractionEvent event4 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", "attention",
//				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time4, time4);
//		InteractionEvent event5 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
//				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, time5, time5);
//		InteractionEvent event6 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task1handle,
//				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, time6, time6);
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event3);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event4);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event5);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event6);
//		assertEquals(1, thisWeekActivity.getChildren().size());
//		long expectedTotalTime = 0;// time6.getTime() - time5.getTime() +
//		// time2.getTime() - time1.getTime();
//		assertEquals(expectedTotalTime, thisWeekActivity.getTotalElapsed());
//		assertEquals(expectedTotalTime, TasksUiPlugin.getTaskListManager().getElapsedTime(task1));
//		assertEquals(expectedTotalTime, thisWeekActivity.getElapsed(new ScheduledTaskDelegate(thisWeekActivity, task1,
//				null, null)));
//	}
//
//	public void testTaskListManagerActivity() {
//
//		AbstractTask task1 = new LocalTask("task 1", "Task 1");
//		AbstractTask task2 = new LocalTask("task 2", "Task 2");
//		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
//		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task2);
//
//		// test this week
//		ScheduledTaskContainer thisWeekActivity = TasksUiPlugin.getTaskListManager().getActivityThisWeek();
//		assertNotNull(thisWeekActivity);
//		assertEquals(0, thisWeekActivity.getChildren().size());
//		assertEquals(0, thisWeekActivity.getTotalElapsed());
//		Calendar thisWeekCalendarStart = GregorianCalendar.getInstance();
//		thisWeekCalendarStart.setTime(thisWeekActivity.getStart().getTime());
//		Calendar thisWeekCalendarStop = GregorianCalendar.getInstance();
//		thisWeekCalendarStop.setTime(thisWeekActivity.getStart().getTime());
//		thisWeekCalendarStop.add(Calendar.MILLISECOND, 2);
//		assertTrue(thisWeekActivity.includes(thisWeekCalendarStart));
//
//		InteractionEvent event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, thisWeekCalendarStart.getTime(),
//				thisWeekCalendarStart.getTime());
//		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task1.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, thisWeekCalendarStop.getTime(),
//				thisWeekCalendarStop.getTime());
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
//		assertEquals(1, thisWeekActivity.getChildren().size());
//		assertEquals(0, thisWeekActivity.getTotalElapsed());
//		// assertEquals(thisWeekCalendarStop.getTime().getTime() -
//		// thisWeekCalendarStart.getTime().getTime(),
//		// thisWeekActivity.getTotalElapsed());
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
//		assertEquals(1, thisWeekActivity.getChildren().size());
//		// assertEquals(2 * (thisWeekCalendarStop.getTime().getTime() -
//		// thisWeekCalendarStart.getTime().getTime()),
//		// thisWeekActivity.getTotalElapsed());
//		// assertEquals(2 * (thisWeekCalendarStop.getTime().getTime() -
//		// thisWeekCalendarStart.getTime().getTime()),
//		// thisWeekActivity.getElapsed(new
//		// DateRangeActivityDelegate(thisWeekActivity, task1, null, null)));
//
//		// multiple tasks in category
//		event1 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2.getHandleIdentifier(),
//				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f,
//				thisWeekCalendarStart.getTime(), thisWeekCalendarStart.getTime());
//		event2 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", task2.getHandleIdentifier(),
//				"originId", "navigatedRelation", InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f,
//				thisWeekCalendarStop.getTime(), thisWeekCalendarStop.getTime());
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event1);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event2);
//		assertEquals(2, thisWeekActivity.getChildren().size());
//
//		// test Past
//		ScheduledTaskContainer pastActivity = TasksUiPlugin.getTaskListManager().getActivityPast();
//		assertNotNull(pastActivity);
//		assertEquals(0, pastActivity.getChildren().size());
//
//		InteractionEvent event3 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task2.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, pastActivity.getStart().getTime(),
//				pastActivity.getStart().getTime());
//		InteractionEvent event4 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task2.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, pastActivity.getEnd().getTime(),
//				pastActivity.getEnd().getTime());
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event3);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event4);
//		assertEquals(1, pastActivity.getChildren().size());
//
//		// test Future
//		ScheduledTaskContainer futureActivity = TasksUiPlugin.getTaskListManager().getActivityFuture();
//		assertNotNull(futureActivity);
//		assertEquals(0, futureActivity.getChildren().size());
//
//		InteractionEvent event5 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task2.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, futureActivity.getStart().getTime(),
//				futureActivity.getStart().getTime());
//		InteractionEvent event6 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task2.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, futureActivity.getEnd().getTime(),
//				futureActivity.getEnd().getTime());
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event5);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event6);
//		// No longer adding activity to future bins (days of week, next week, or
//		// future)
//		assertEquals(0, futureActivity.getChildren().size());
//
//		// test Next week activity
//		ScheduledTaskContainer activityNextWeek = TasksUiPlugin.getTaskListManager().getActivityNextWeek();
//		assertNotNull(activityNextWeek);
//		assertEquals(0, activityNextWeek.getChildren().size());
//
//		InteractionEvent event7 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task2.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, activityNextWeek.getStart().getTime(),
//				activityNextWeek.getStart().getTime());
//		InteractionEvent event8 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task2.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, activityNextWeek.getEnd().getTime(),
//				activityNextWeek.getEnd().getTime());
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event7);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event8);
//		// No longer adding activity to future bins (days of week, next week, or
//		// future)
//		assertEquals(0, activityNextWeek.getChildren().size());
//
//		// test Previous week activity
//		ScheduledTaskContainer activityPreviousWeek = TasksUiPlugin.getTaskListManager().getActivityPrevious();
//		assertNotNull(activityPreviousWeek);
//		assertEquals(0, activityPreviousWeek.getChildren().size());
//
//		InteractionEvent event9 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task2.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 2f, activityPreviousWeek.getStart().getTime(),
//				activityPreviousWeek.getStart().getTime());
//		InteractionEvent event10 = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind",
//				task2.getHandleIdentifier(), "originId", "navigatedRelation",
//				InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 2f, activityPreviousWeek.getEnd().getTime(),
//				activityPreviousWeek.getEnd().getTime());
//
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event9);
//		TasksUiPlugin.getTaskListManager().parseInteractionEvent(event10);
//		assertEquals(1, activityPreviousWeek.getChildren().size());
//	}

}
