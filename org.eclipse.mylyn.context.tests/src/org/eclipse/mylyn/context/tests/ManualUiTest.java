/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Aug 3, 2004
 */
package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * @author Mik Kersten
 */
public class ManualUiTest extends AbstractManualTest {

	public void testErrorDialog() {
		try {
			int i = 10 / 0;
			System.out.println(i);
		} catch (Throwable t) {
			StatusHandler.fail(t, "whoops", true);
		}
		StatusHandler.fail(null, "whoops", true);
		assertTrue(confirmWithUser("Did an error dialog show up correctly?"));
	}

}
