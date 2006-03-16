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

import junit.framework.TestCase;

import org.eclipse.mylar.internal.core.util.TimerThread;
import org.eclipse.mylar.internal.tasklist.util.TaskActivityTimer;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;

/**
 * @author Mik Kersten
 */
public class TaskActivityTimingTest extends TestCase {

	// 5 seconds (minimum value since TimerThread sleeps
	// for 5 seconds anyway before checking

	private static final int SLOP = 100;

	private static final int SLEEP_INTERVAL = SLOP * 3;

	private static final int TIMEOUT = SLEEP_INTERVAL * 2;

	private static final int SLEEP_NOTIMEOUT = TIMEOUT - SLOP; // 1 second

	// Introdueced long sleep for testing inactivity where TimerThread
	// sleeps for 5 seconds so we must sleep at least that long before
	// it will have woken up and suspended.
	private static final int SLEEP_TIMEOUT = TIMEOUT + SLOP; // 5.5 seconds

	protected TaskListManager manager = MylarTaskListPlugin.getTaskListManager();

	protected Task task1 = null;

	protected int originalActivityTimeout = -1;

	protected void setUp() throws Exception {
		super.setUp();
		task1 = new Task("t1", "t1", true);
		originalActivityTimeout = MylarPlugin.getContextManager().getInactivityTimeout();
		MylarPlugin.getContextManager().setInactivityTimeout(TIMEOUT);
		manager.setTimerSleepInterval(SLEEP_INTERVAL);
	}

	public void tearDown() {
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		ITask remaining = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (remaining != null) {
			MylarTaskListPlugin.getTaskListManager().deactivateTask(remaining);
		}
		MylarPlugin.getContextManager().setInactivityTimeout(originalActivityTimeout);
		manager.setTimerSleepInterval(TimerThread.DEFAULT_SLEEP_INTERVAL);
	}

	public void testDeactivation() throws InterruptedException {
		assertEquals(0, task1.getElapsedTime());
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		assertEquals(0, task1.getElapsedTime());

		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		Thread.sleep(SLEEP_NOTIMEOUT);
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		assertTrue("elapsed: " + task1.getElapsedTime(), task1.getElapsedTime() >= SLEEP_NOTIMEOUT - 2*SLOP);
	}

	public void testTimerMap() throws InterruptedException {
		Task task0 = new Task("t0", "t0", true);
		manager.activateTask(task0);
		assertEquals(1, manager.getTimerMap().values().size());
		TaskActivityTimer timer0 = manager.getTimerMap().get(task0);
		assertTrue(timer0.isStarted());

		long elapsed = task1.getElapsedTime();
		assertEquals(0, elapsed);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		TaskActivityTimer timer1 = manager.getTimerMap().get(task1);
		// previous task was deactivated
		assertEquals(1, manager.getTimerMap().values().size());
		assertTrue(timer1.isStarted());
		Thread.sleep(SLEEP_TIMEOUT);

		manager.deactivateTask(task1);
		elapsed = task1.getElapsedTime();
		assertTrue("should be around TIMEOUT", (elapsed > (TIMEOUT - 500)) && (elapsed < (TIMEOUT + 500)));
		assertFalse(timer1.isStarted());
		assertEquals(0, manager.getTimerMap().values().size());

		Thread.sleep(SLEEP_TIMEOUT);
		long elapsedAfterInactivity = task1.getElapsedTime();
		assertEquals("no accumulation if task deactivated", elapsed, elapsedAfterInactivity);

		assertFalse(timer0.isStarted());
		assertEquals(0, manager.getTimerMap().values().size());
	}

	public void testElapsedTimeCapture() throws InterruptedException {
		long elapsed = task1.getElapsedTime();
		assertEquals(0, elapsed);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		Thread.sleep(SLEEP_TIMEOUT);

		elapsed = task1.getElapsedTime();
		assertTrue("should be bigger than timeout: " + elapsed + " > " + TIMEOUT, elapsed + SLOP >= TIMEOUT);

		// Task should be inactive so no time accumulated
		Thread.sleep(SLEEP_TIMEOUT);
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);

		long elapsedAfterDeactivation = task1.getElapsedTime();
		assertEquals(elapsed, elapsedAfterDeactivation);

		Thread.sleep(SLEEP_TIMEOUT);
		long elapsedAfterInactivity = task1.getElapsedTime();
		assertEquals("no accumulation if task inactive", elapsedAfterDeactivation, elapsedAfterInactivity);

		MylarPlugin.getContextManager().setInactivityTimeout(SLEEP_TIMEOUT * 2);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		Thread.sleep(SLEEP_TIMEOUT);
		// Should not have timed out
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		long elpasedAfterReactivation = task1.getElapsedTime();

		// adds some slop
		assertTrue("time: " + (elpasedAfterReactivation - elapsedAfterInactivity), elpasedAfterReactivation
				- elapsedAfterInactivity + 50 >= SLEEP_TIMEOUT);
	}

	public void testTimeout() throws InterruptedException {

		Task task0 = new Task("t0", "t0", true);
		assertEquals(task0.getElapsedTime(), 0);
		manager.activateTask(task0);
		assertEquals(1, manager.getTimerMap().values().size());
		TaskActivityTimer timer0 = manager.getTimerMap().get(task0);
		assertTrue(timer0.isStarted());

		Thread.sleep(SLEEP_TIMEOUT);

		// timeout should have occurred before SLEEP time
		long timeAfterSleep = task0.getElapsedTime();

		assertTrue(timeAfterSleep < SLEEP_TIMEOUT);
		timer0 = manager.getTimerMap().get(task0);
		assertNotNull(timer0);
		assertTrue(timer0.isSuspended());

		// Interaction should cause task timer to startup.
		mockInteraction();
		assertFalse(timer0.isSuspended());
		Thread.sleep(SLEEP_NOTIMEOUT);
		manager.deactivateTask(task0);

		assertTrue(task0.getElapsedTime() > timeAfterSleep);
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task0);

	}

	protected void mockInteraction() {
		MylarPlugin.getDefault().notifyInteractionObserved(
				new InteractionEvent(InteractionEvent.Kind.EDIT, "java", "A.java", "mock-id"));
	}
}
