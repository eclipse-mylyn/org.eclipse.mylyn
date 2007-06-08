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
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.tasks.core.Task;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class TaskListBackupManagerTest extends TestCase {

	private Task task1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		task1 = new Task("handle", "label");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		TasksUiPlugin.getTaskListManager().activateTask(task1);
		TasksUiPlugin.getTaskListManager().deactivateTask(task1);
		TasksUiPlugin.getTaskListManager().saveTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_AUTOMATICALLY,
		// false);
	}

	public void testAutoBackupDisabled() throws InterruptedException {
		TaskListBackupManager backupManager = TasksUiPlugin.getDefault().getBackupManager();
		// MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_AUTOMATICALLY,
		// false);
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_SCHEDULE, 1);
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_LAST, 0f);
		assertEquals(0, TasksUiPlugin.getDefault().getPreferenceStore()
				.getLong(TasksUiPreferenceConstants.BACKUP_LAST));
		backupManager.start(5);
		Thread.sleep(3000);
		assertEquals(0, TasksUiPlugin.getDefault().getPreferenceStore()
				.getLong(TasksUiPreferenceConstants.BACKUP_LAST));
	}

	public void testAutoBackupEnabled() throws InterruptedException, InvocationTargetException, IOException {
		TaskListBackupManager backupManager = TasksUiPlugin.getDefault().getBackupManager();
		String backupFolder = TasksUiPlugin.getDefault().getBackupFolderPath();
		// String backupFolder =
		// MylarTaskListPlugin.getMylarCorePrefs().getDefaultString(
		// TaskListPreferenceConstants.BACKUP_FOLDER);
		File backupFileFolder = new File(backupFolder);
		deleteBackupFolder(backupFileFolder);
		// MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_FOLDER,
		// backupFolder);
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_SCHEDULE, 1);
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_LAST, 0f);
		// MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.BACKUP_AUTOMATICALLY,
		// true);
		backupManager.backupNow(true);
		assertFalse(TasksUiPlugin.getDefault().getPreferenceStore().getLong(TasksUiPreferenceConstants.BACKUP_LAST) == 0);
		assertTrue(backupFileFolder.exists());
		assertTrue(backupFileFolder.isDirectory());
		assertTrue(backupFileFolder.listFiles().length == 1);

		// Test removal of old backups
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_MAXFILES, 0);
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
