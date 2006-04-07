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

package org.eclipse.mylar.tasklist.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.mylar.internal.tasklist.TaskListBackupManager;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;

import junit.framework.TestCase;

/**
 * @author Rob Elves
 */
public class TaskListBackupManagerTest extends TestCase {

	Task task1;

	protected void setUp() throws Exception {
		super.setUp();
		task1 = new Task("handle", "label", true);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task1);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		MylarTaskListPlugin.getTaskListManager().saveTaskList();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_AUTOMATICALLY, false);
	}

	public void testAutoBackupDisabled() throws InterruptedException {
		TaskListBackupManager backupManager = MylarTaskListPlugin.getDefault().getBackupManager();
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_AUTOMATICALLY, false);
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_SCHEDULE, 1);
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_LAST, 0f);
		assertEquals(0, MylarTaskListPlugin.getMylarCorePrefs().getLong(TaskListPreferenceConstants.BACKUP_LAST));
		backupManager.start(5);
		Thread.sleep(3000);
		assertEquals(0, MylarTaskListPlugin.getMylarCorePrefs().getLong(TaskListPreferenceConstants.BACKUP_LAST));
	}

	public void testAutoBackupEnabled() throws InterruptedException, InvocationTargetException, IOException {
		TaskListBackupManager backupManager = MylarTaskListPlugin.getDefault().getBackupManager();
		String backupFolder = MylarTaskListPlugin.getMylarCorePrefs().getDefaultString(
				TaskListPreferenceConstants.BACKUP_FOLDER);
		File backupFileFolder = new File(backupFolder);
		deleteBackupFolder(backupFileFolder);
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_FOLDER, backupFolder);
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_SCHEDULE, 1);
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_LAST, 0f);
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_AUTOMATICALLY, true);
		backupManager.backupNow(true);
		assertFalse(MylarTaskListPlugin.getMylarCorePrefs().getLong(TaskListPreferenceConstants.BACKUP_LAST) == 0);
		assertTrue(backupFileFolder.exists());
		assertTrue(backupFileFolder.isDirectory());
		assertTrue(backupFileFolder.listFiles().length == 1);

		// Test removal of old backups
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_MAXFILES, 0);
		backupManager.removeOldBackups(backupFileFolder);
		assertEquals(0, backupFileFolder.listFiles().length);

		// TODO: Test that OLDEST backups are deleted first.

	}

	private void deleteBackupFolder(File backupFileFolder) {
		if (backupFileFolder.exists()) {
			for (File file : backupFileFolder.listFiles()) {
				file.delete();
			}
			backupFileFolder.delete();
		}
	}

}
