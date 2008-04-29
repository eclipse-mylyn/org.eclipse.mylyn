/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.IStatusHandler;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * @author Shawn Minto
 */
public class StatusHandlerTest extends TestCase {

	private class MockDefaultStatusHandler implements IStatusHandler {

		public void displayStatus(String title, IStatus status) {

		}

		public void fail(IStatus status, boolean informUser) {

		}

	}

	private class MockStatusHandler implements IStatusHandler {

		public void displayStatus(String title, IStatus status) {

		}

		public void fail(IStatus status, boolean informUser) {

		}

	}

	public void testDefaultStatusHandler() {
		// save the old status handlers
		IStatusHandler oldDefault = StatusHandler.getDefaultStatusHandler();
		Set<IStatusHandler> oldHandlers = new HashSet<IStatusHandler>(StatusHandler.getStatusHandlers());
		try {
			MockDefaultStatusHandler newDefault = new MockDefaultStatusHandler();
			MockStatusHandler newStatusHandler = new MockStatusHandler();
			StatusHandler.setDefaultStatusHandler(newDefault);

			// remove all of the old status handlers
			StatusHandler.removeStatusHandler(oldDefault);
			for (IStatusHandler oldHandler : oldHandlers) {
				StatusHandler.removeStatusHandler(oldHandler);
			}

			assertEquals(newDefault, StatusHandler.getDefaultStatusHandler());
			assertEquals(1, StatusHandler.getStatusHandlers().size());

			StatusHandler.addStatusHandler(newStatusHandler);
			assertEquals(newDefault, StatusHandler.getDefaultStatusHandler());
			assertEquals(1, StatusHandler.getStatusHandlers().size());

			assertEquals(newStatusHandler, StatusHandler.getStatusHandlers().iterator().next());

			StatusHandler.removeStatusHandler(newStatusHandler);
			assertEquals(newDefault, StatusHandler.getDefaultStatusHandler());
			assertEquals(1, StatusHandler.getStatusHandlers().size());
			assertEquals(newDefault, StatusHandler.getStatusHandlers().iterator().next());

			StatusHandler.removeStatusHandler(newDefault);
		} finally {
			// add the old status handlers back		
			StatusHandler.setDefaultStatusHandler(oldDefault);
			for (IStatusHandler oldHandler : oldHandlers) {
				StatusHandler.addStatusHandler(oldHandler);
			}
		}
	}

}
