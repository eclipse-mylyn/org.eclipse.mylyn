/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.monitor.tests;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.monitor.ui.ActivityContextManager;
import org.eclipse.mylyn.monitor.ui.AbstractUserActivityMonitor;

/**
 * @author Steffen Pingel
 */
public class ActivityContextManagerTest extends TestCase {

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
				throw new LinkageError();
			}
			return super.getLastInteractionTime();
		}

	}

	private StubMonitor monitor1;

	private StubMonitor monitor2;

	private ActivityContextManager manager;

	@Override
	protected void setUp() throws Exception {
		monitor1 = new StubMonitor();
		monitor2 = new StubMonitor();
		manager = new ActivityContextManager(Arrays.asList(new AbstractUserActivityMonitor[] { monitor1, monitor2 }));
	}

	@Override
	protected void tearDown() throws Exception {
		if (manager != null) {
			manager.stop();
		}
	};

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
