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
package org.eclipse.mylar.tests.tasklist;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.tasklist.BackgroundSaveTimer;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskListSaveManager;

/**
 * Tests the mechanism for saving the task data periodically. If this test fails
 * unexpectedly, try adjusting the timing.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (rewrite)
 */
public class BackgroundSaveTest extends TestCase {

	private BackgroundSaveTimer saveTimer;

	private TaskListSaveManager policy;

	protected void setUp() throws Exception {
		super.setUp();
		policy = MylarTaskListPlugin.getDefault().getTaskListSaveManager();

		saveTimer = new BackgroundSaveTimer(MylarTaskListPlugin.getDefault().getTaskListSaveManager());
		saveTimer.setSaveIntervalMillis(50);
		saveTimer.start();
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().setForceBackgroundSave(true);
	}

	protected void tearDown() throws Exception {
		saveTimer.stop();
		super.tearDown();
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().setForceBackgroundSave(false);
	}

	public void testBackgroundSave() throws InterruptedException {
		File file = MylarTaskListPlugin.getTaskListManager().getTaskListFile();
		policy.saveTaskListAndContexts();

		long fistModified = file.lastModified();
		Thread.sleep(500);

		assertTrue(file.lastModified() > fistModified);
	}
}
