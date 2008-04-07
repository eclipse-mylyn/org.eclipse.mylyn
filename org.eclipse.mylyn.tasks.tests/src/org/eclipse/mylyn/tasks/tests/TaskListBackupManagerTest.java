/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class TaskListBackupManagerTest extends TestCase {

	private AbstractTask task1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		task1 = new LocalTask("handle", "label");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		TasksUiPlugin.getTaskListManager().activateTask(task1);
		TasksUiPlugin.getTaskListManager().deactivateTask(task1);
		TasksUiPlugin.getTaskListManager().saveTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAutoBackupDisabled() throws InterruptedException {
		TaskListBackupManager backupManager = TasksUiPlugin.getDefault().getBackupManager();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_SCHEDULE, 1);
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_LAST, 0f);
		assertEquals(0, TasksUiPlugin.getDefault().getPreferenceStore().getLong(TasksUiPreferenceConstants.BACKUP_LAST));
		backupManager.start(5);
		Thread.sleep(3000);
		assertEquals(0, TasksUiPlugin.getDefault().getPreferenceStore().getLong(TasksUiPreferenceConstants.BACKUP_LAST));
	}

	public void testAutoBackupEnabled() throws InterruptedException, InvocationTargetException, IOException {
		TaskListBackupManager backupManager = TasksUiPlugin.getDefault().getBackupManager();
		String backupFolder = TasksUiPlugin.getDefault().getBackupFolderPath();
		File backupFileFolder = new File(backupFolder);
		deleteBackupFolder(backupFileFolder);
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_SCHEDULE, 1);
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_LAST, 0f);
		backupManager.backupNow(true);
		assertFalse(TasksUiPlugin.getDefault().getPreferenceStore().getLong(TasksUiPreferenceConstants.BACKUP_LAST) == 0);
		assertTrue(backupFileFolder.exists());
		assertTrue(backupFileFolder.isDirectory());
		assertTrue(backupFileFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.matches("mylyndata-.*")) {
					return true;
				}
				return false;
			}

		}).length == 1);

		// Test removal of old backups
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_MAXFILES, 0);
		TasksUiPlugin.getDefault().getBackupManager().removeOldBackups(backupFileFolder);
		assertTrue(backupFileFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.matches("mylyndata-.*")) {
					return true;
				}
				return false;
			}

		}).length == 0);

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
