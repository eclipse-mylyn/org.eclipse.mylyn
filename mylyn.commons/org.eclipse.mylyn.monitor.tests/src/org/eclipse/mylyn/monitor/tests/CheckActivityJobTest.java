/*******************************************************************************
 * Copyright (c) 2004, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.monitor.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.monitor.ui.CheckActivityJob;
import org.eclipse.mylyn.internal.monitor.ui.IActivityManagerCallback;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class CheckActivityJobTest extends TestCase {

	private StubCallback callback;

	private TestableCheckActivityJob job;

	@Override
	protected void setUp() throws Exception {
		callback = new StubCallback();
		job = new TestableCheckActivityJob(callback);
	}

	public void testInactivityTimeout() throws Exception {
		if (Platform.ARCH_X86_64.equals(Platform.getOSArch()) && Platform.OS_MACOSX.equals(Platform.getOS())) {
			System.err.println("Skipping CheckActivityJobTest.testInactivityTimeout() on Intel Macs");
			return;
		}
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
		assertTrue("expected less than 5 < activeTime < 40, got " + callback.activeTime + " (slept " + slept + " ms)",
				callback.activeTime > 5 && callback.activeTime < 40);
	}

	public void testResumeFromSleepNoTimeout() throws Exception {
		if (Platform.ARCH_X86_64.equals(Platform.getOSArch()) && Platform.OS_MACOSX.equals(Platform.getOS())) {
			System.err.println("Skipping CheckActivityJobTest.testResumeFromSleepNoTimeout() on Intel Macs");
			return;
		}
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
		assertTrue("expected more than 10 ms, got " + callback.activeTime, callback.activeTime > 10);
		assertEquals(3, callback.eventCount);
	}

	public void testResumeFromSleepTimeoutNoEvent() throws Exception {
		if (Platform.ARCH_X86_64.equals(Platform.getOSArch()) && Platform.OS_MACOSX.equals(Platform.getOS())) {
			System.err.println("Skipping CheckActivityJobTest.testResumeFromSleepTimeoutNoEvent() on Intel Macs");
			return;
		}
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
		assertTrue("expected less than 10 ms, got " + callback.activeTime, callback.activeTime < 10);
		assertEquals(1, callback.eventCount);
		assertEquals(callback.lastEventTime, callback.startTime);
	}

	public void testResumeFromSleepTimeoutEvent() throws Exception {
		if (Platform.ARCH_X86_64.equals(Platform.getOSArch()) && Platform.OS_MACOSX.equals(Platform.getOS())) {
			System.err.println("Skipping CheckActivityJobTest.testResumeFromSleepTimeoutEvent() on Intel Macs");
			return;
		}
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
		assertTrue("expected less than 10 < activeTime < 20, got " + callback.activeTime + " (slept " + slept + " ms)",
				callback.activeTime > 10 && callback.activeTime < 20);
		assertEquals(2, callback.eventCount);
	}

	public void testResumeFromSleepTimeoutEventDiscarded() throws Exception {
		if (Platform.ARCH_X86_64.equals(Platform.getOSArch()) && Platform.OS_MACOSX.equals(Platform.getOS())) {
			System.err
					.println("Skipping CheckActivityJobTest.testResumeFromSleepTimeoutEventDiscarded() on Intel Macs");
			return;
		}
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
		assertTrue("expected less than 5 < activeTime < 40, got " + callback.activeTime + " (slept " + slept + " ms)",
				callback.activeTime > 5 && callback.activeTime < 40);
	}

	private class TestableCheckActivityJob extends CheckActivityJob {

		public TestableCheckActivityJob(IActivityManagerCallback callback) {
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

	private static class StubCallback implements IActivityManagerCallback {

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
