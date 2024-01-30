/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.activity.tests;

import java.util.Arrays;

import org.eclipse.mylyn.commons.activity.ui.spi.AbstractUserActivityMonitor;
import org.eclipse.mylyn.internal.commons.activity.ui.UserActivityManager;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class UserActivityManagerTest extends TestCase {

	private class StubUserActivityMonitor extends AbstractUserActivityMonitor {

		private boolean started;

		private boolean fail;

		private boolean enabled = true;

		private final int priority;

		public StubUserActivityMonitor(int priority) {
			this.priority = priority;
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public void start() {
			started = true;
		}

		@Override
		public void stop() {
			started = false;
		}

		@Override
		public long getLastInteractionTime() {
			if (fail) {
				throw new LinkageError();
			}
			return super.getLastInteractionTime();
		}

		@Override
		public int getPriority() {
			return priority;
		}

	}

	private StubUserActivityMonitor monitor1;

	private StubUserActivityMonitor monitor2;

	private UserActivityManager manager;

	@Override
	protected void setUp() throws Exception {
		monitor1 = new StubUserActivityMonitor(0);
		monitor2 = new StubUserActivityMonitor(1);
		manager = new UserActivityManager(Arrays.asList(monitor1, monitor2));
	}

	@Override
	protected void tearDown() throws Exception {
		if (manager != null) {
			manager.stop();
		}
	}

	public void testStartStop() {
		manager.start();
		assertTrue(monitor1.started);
		assertTrue(monitor2.started);
		manager.stop();
		assertFalse(monitor1.started);
		assertFalse(monitor2.started);
	}

	public void testGetInactivityTimeout() {
		monitor1.setLastEventTime(1);
		monitor2.setLastEventTime(2);
		assertEquals(1, manager.getLastInteractionTime());
		assertEquals(1, manager.getLastInteractionTime());
	}

	public void testGetInactivityTimeoutFailure() {
		monitor1.setLastEventTime(1);
		monitor2.setLastEventTime(2);
		manager.start();
		assertEquals(1, manager.getLastInteractionTime());
		monitor1.fail = true;
		assertEquals(2, manager.getLastInteractionTime());
		manager.stop();
		// the first monitor should have been disabled
		assertTrue(monitor1.started);
		assertFalse(monitor2.started);
	}

	public void testEnabled() {
		monitor1.enabled = false;
		monitor1.setLastEventTime(1);
		monitor2.setLastEventTime(2);
		assertEquals(2, manager.getLastInteractionTime());
		monitor2.enabled = false;
		assertEquals(-1, manager.getLastInteractionTime());
	}

}
