/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class TaskListBackupManagerTest extends TestCase {

	private AbstractTask task1;

	private TaskListBackupManager backupManager;

	@Override
	protected void setUp() throws Exception {
		backupManager = TasksUiPlugin.getBackupManager();
		CommonTestUtil.deleteFolder(new File(TasksUiPlugin.getDefault().getBackupFolderPath()));
		TaskTestUtil.resetTaskList();
	}

	public void testAutoBackupDisabled() throws Exception {
		backupManager.start(5);
		Thread.sleep(1000);
		assertEquals(0, backupManager.getBackupFiles().size());

		task1 = new LocalTask("handle", "label");
		TasksUiPlugin.getTaskList().addTask(task1);
		backupManager.stop();
		backupManager.start(5);
		Thread.sleep(1000);
		assertEquals(1, backupManager.getBackupFiles().size());
	}

	public void testAutoBackupEnabled() throws Exception {
		task1 = new LocalTask("handle", "label");
		TasksUiPlugin.getTaskList().addTask(task1);
		backupManager.backupNow(true, null);
		assertEquals(1, backupManager.getBackupFiles().size());

		// make sure a new date stamp is generated
		Thread.sleep(1000);
		backupManager.backupNow(true, null);
		assertEquals(2, backupManager.getBackupFiles().size());

		// test removal of old backups
		TasksUiPlugin.getBackupManager().removeOldBackups();
		assertEquals(1, backupManager.getBackupFiles().size());
	}

}
