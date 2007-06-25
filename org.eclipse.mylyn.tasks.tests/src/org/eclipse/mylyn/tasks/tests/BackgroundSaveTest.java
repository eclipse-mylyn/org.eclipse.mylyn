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
package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.util.BackgroundSaveTimer;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListSaveManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Tests the mechanism for saving the task data periodically. If this test fails unexpectedly, try adjusting the timing.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (rewrite)
 */
public class BackgroundSaveTest extends TestCase {

	private BackgroundSaveTimer saveTimer;

	private TaskListSaveManager saveManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getTaskListManager().saveTaskList();
		saveManager = new TaskListSaveManager();
//		saveManager = TasksUiPlugin.getDefault().getTaskListSaveManager();

		saveTimer = new BackgroundSaveTimer(saveManager);
		saveTimer.setSaveIntervalMillis(50);
		saveTimer.start();
//		saveManager.setForceBackgroundSave(true);
	}

	@Override
	protected void tearDown() throws Exception {
		saveTimer.stop();
		super.tearDown();
//		saveManager.setForceBackgroundSave(false);
	}

	public void testBackgroundSave() throws InterruptedException, IOException {
		if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("linux")) {
			System.out.println("> BackgroundSaveTest.testBackgroundSave() not run on Linux due to IO concurrency");
		} else {
			File file = TasksUiPlugin.getTaskListManager().getTaskListFile();
			long previouslyModified = file.lastModified();
//			TasksUiPlugin.getTaskListManager().saveTaskList();
			saveManager.saveTaskList(true, false);
			assertTrue(file.lastModified() > previouslyModified);
		}
	}
}
