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

package org.eclipse.mylar.monitor.tests;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.monitor.InteractionEventLogger;
import org.eclipse.mylar.internal.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.provisional.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class InteractionLoggerTest extends TestCase {

	private InteractionEventLogger logger = MylarMonitorPlugin.getDefault().getInteractionLogger();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		MylarMonitorPlugin.getDefault().stopMonitoring();
	}

	public void testClearHistory() throws IOException {
		logger.startObserving();
		File monitorFile = logger.getOutputFile();
		assertTrue(monitorFile.exists());
		logger.interactionObserved(InteractionEvent.makeCommand("a", "b"));
		logger.stopObserving();
		assertTrue(monitorFile.length() > 0);
		logger.clearInteractionHistory();
		assertEquals(monitorFile.length(), 0);
	}
}