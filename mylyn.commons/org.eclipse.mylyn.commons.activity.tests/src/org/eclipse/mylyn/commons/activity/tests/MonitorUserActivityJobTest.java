/*******************************************************************************
 * Copyright (c) 2010, 2025 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.activity.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.commons.activity.ui.IUserActivityManagerCallback;
import org.eclipse.mylyn.internal.commons.activity.ui.MonitorUserActivityJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class MonitorUserActivityJobTest {

	private StubCallback callback;

	private TestableCheckActivityJob job;

	@BeforeEach
	void setUp() throws Exception {
		callback = new StubCallback();
		job = new TestableCheckActivityJob(callback);
	}

	@Test
	@DisabledOnOs(OS.MAC) // TODO Is this still the case
	public void testInactivityTimeout() throws Exception {
		callback.lastEventTime = System.currentTimeMillis() - 201;
		job.setInactivityTimeout(200);
		job.run();
		assertFalse(job.isActive());
		job.run();
		assertFalse(job.isActive());
		callback.lastEventTime = System.currentTimeMillis();
		job.run();
		assertTrue(job.isActive());
		assertEquals(0, callback.activeTime);
		Thread.sleep(6);
		job.run();
		long slept = System.currentTimeMillis() - callback.lastEventTime;
		assertTrue(job.isActive());
		assertTrue(callback.activeTime > 5 && callback.activeTime < 40,
				"expected less than 5 < activeTime < 40, got " + callback.activeTime + " (slept " + slept + " ms)");
	}

	@Test
	@DisabledOnOs(OS.MAC) // TODO Is this still the case
	public void testResumeFromSleepNoTimeout() throws Exception {
		job.setInactivityTimeout(0);
		job.run();
		assertTrue(job.isActive());
		job.run();
		assertTrue(job.isActive());
		assertEquals(1, callback.eventCount);
		job.run();
		assertEquals(2, callback.eventCount);
		assertTrue(job.isActive());
		Thread.sleep(11);
		job.run();
		assertTrue(job.isActive());
		assertTrue(callback.activeTime > 10, "expected more than 10 ms, got " + callback.activeTime);
		assertEquals(3, callback.eventCount);
	}

	@Test
	@DisabledOnOs(OS.MAC) // TODO Is this still the case
	public void testResumeFromSleepTimeoutNoEvent() throws Exception {
		callback.lastEventTime = System.currentTimeMillis();
		job.setInactivityTimeout(20);
		job.setTick(20);
		job.run();
		assertTrue(job.isActive());
		job.run();
		assertTrue(job.isActive());
		assertEquals(1, callback.eventCount);
		Thread.sleep(61);
		// resume from sleep past timeout
		job.run();
		assertFalse(job.isActive());
		job.run();
		assertFalse(job.isActive());
		assertTrue(callback.activeTime < 10, "expected less than 10 ms, got " + callback.activeTime);
		assertEquals(1, callback.eventCount);
		assertEquals(callback.lastEventTime, callback.startTime);
	}

	@Test
	@DisabledOnOs(OS.MAC) // TODO Is this still the case
	public void testResumeFromSleepTimeoutEvent() throws Exception {
		callback.lastEventTime = System.currentTimeMillis();
		job.setInactivityTimeout(20);
		job.setTick(20);
		job.run();
		assertTrue(job.isActive());
		job.run();
		assertTrue(job.isActive());
		assertEquals(1, callback.eventCount);
		Thread.sleep(41);
		// resume from sleep past timeout
		job.run();
		assertTrue(callback.inactive);
		assertFalse(job.isActive());
		Thread.sleep(11);
		// should still discard events
		job.run();
		assertFalse(job.isActive());
		// start activity
		callback.lastEventTime = System.currentTimeMillis();
		job.run();
		assertTrue(job.isActive());
		assertEquals(1, callback.eventCount);
		Thread.sleep(11);
		job.run();
		// check if time sleeping was logged
		long slept = System.currentTimeMillis() - callback.lastEventTime;
		assertEquals(2, callback.eventCount);
		assertTrue(callback.activeTime > 10 && callback.activeTime < 20,
				"expected less than 10 < activeTime < 20, got " + callback.activeTime + " (slept " + slept + " ms)");
	}

	@Test
	@DisabledOnOs(OS.MAC) // TODO Is this still the case
	public void testResumeFromSleepTimeoutEventDiscarded() throws Exception {
		// record one tick
		callback.lastEventTime = System.currentTimeMillis();
		job.setInactivityTimeout(20);
		job.setTick(20);
		job.run();
		assertTrue(job.isActive());
		job.run();
		assertTrue(job.isActive());
		assertEquals(1, callback.eventCount);
		// resume from sleep past timeout
		callback.activeTime = 0;
		Thread.sleep(61);
		callback.lastEventTime = System.currentTimeMillis();
		job.run();
		assertFalse(callback.inactive);
		assertTrue(job.isActive());
		assertEquals(0, callback.activeTime);
		// record another tick
		Thread.sleep(6);
		job.run();
		long slept = System.currentTimeMillis() - callback.lastEventTime;
		assertTrue(job.isActive());
		// check if time sleeping was logged
		assertEquals(2, callback.eventCount);
		assertTrue(callback.activeTime > 5 && callback.activeTime < 40,
				"expected less than 5 < activeTime < 40, got " + callback.activeTime + " (slept " + slept + " ms)");
	}

	private class TestableCheckActivityJob extends MonitorUserActivityJob {

		public TestableCheckActivityJob(IUserActivityManagerCallback callback) {
			super(callback);
		}

		public IStatus run() {
			return super.run(new NullProgressMonitor());
		}

		@Override
		protected boolean isEnabled() {
			return true;
		}

		public void setTick(long tick) {
			this.tick = tick;
		}

		@Override
		public void reschedule() {
			// ignore, job is called explicitly from test
		}

	}

	private static class StubCallback implements IUserActivityManagerCallback {

		private boolean inactive;

		private long lastEventTime;

		private long activeTime;

		private long eventCount;

		private long startTime;

		@Override
		public void addMonitoredActivityTime(long startTime, long endTime) {
			this.startTime = startTime;
			activeTime += endTime - startTime;
			eventCount++;
		}

		@Override
		public void inactive() {
			inactive = true;
		}

		@Override
		public long getLastEventTime() {
			return lastEventTime;
		}

		@Override
		public void active() {
		}

	}

}
