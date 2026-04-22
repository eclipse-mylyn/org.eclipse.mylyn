/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.monitor.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.eclipse.mylyn.internal.monitor.ui.ActivityContextManager;
import org.eclipse.mylyn.monitor.ui.AbstractUserActivityMonitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
public class ActivityContextManagerTest {

	private class StubMonitor extends AbstractUserActivityMonitor {

		private boolean started;

		private boolean fail;

		private boolean enabled = true;

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
				throw new LinkageError("Told to fail");
			}
			return super.getLastInteractionTime();
		}

	}

	private StubMonitor monitor1;

	private StubMonitor monitor2;

	private ActivityContextManager manager;

	@BeforeEach
	void setUp() throws Exception {
		monitor1 = new StubMonitor();
		monitor2 = new StubMonitor();
		manager = new ActivityContextManager(Arrays.asList(monitor1, monitor2));
	}

	@AfterEach
	void tearDown() throws Exception {
		if (manager != null) {
			manager.stop();
		}
	}

	@Test
	public void testStartStop() {
		manager.start();
		assertTrue(monitor1.started);
		assertTrue(monitor2.started);
		manager.stop();
		assertFalse(monitor1.started);
		assertFalse(monitor2.started);
	}

	@Test
	public void testGetInactivityTimeout() {
		monitor1.setLastEventTime(1);
		monitor2.setLastEventTime(2);
		assertEquals(1, manager.getLastInteractionTime());
		assertEquals(1, manager.getLastInteractionTime());
	}

	@Test
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

	@Test
	public void testEnabled() {
		monitor1.enabled = false;
		monitor1.setLastEventTime(1);
		monitor2.setLastEventTime(2);
		assertEquals(2, manager.getLastInteractionTime());
		monitor2.enabled = false;
		assertEquals(-1, manager.getLastInteractionTime());
	}

}
