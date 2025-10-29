/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

import junit.framework.TestCase;

/**
 * Tests the mechanism for saving the task data periodically.
 *
 * @author Wesley Coelho
 * @author Mik Kersten (rewrite)
 */
@SuppressWarnings("nls")
public class BackgroundSaveTest extends TestCase {

	public void testBackgroundSave() throws InterruptedException, IOException {
		if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("linux")) {
			System.out.println("> BackgroundSaveTest.testBackgroundSave() not run on Linux due to IO concurrency");
		} else {
			LocalTask task = new LocalTask("1", "summary");
			String filePath = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
					+ ITasksCoreConstants.DEFAULT_TASK_LIST_FILE;

			final File file = new File(filePath);
			long previouslyModified = file.lastModified();
			TasksUiPlugin.getTaskList().addTask(task);
			TasksUiPlugin.getExternalizationManager().requestSave();
			Thread.sleep(5000);
			assertTrue(file.lastModified() > previouslyModified);
			TasksUiPlugin.getTaskList().deleteTask(task);
		}
	}
}
