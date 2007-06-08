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

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataExportJob;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataExportWizard;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Rob Elves
 */
public class TaskListBackupManager implements IPropertyChangeListener {

	private static final String TITLE_TASKLIST_BACKUP = "Tasklist Backup";
  
	private static final String BACKUP_JOB_NAME = "Scheduled task data backup";

	public static final String BACKUP_FAILURE_MESSAGE = "Could not backup task data. Check backup preferences.\n";

	private static final long SECOND = 1000;

	private static final long MINUTE = 60 * SECOND;
  
	private static final long HOUR = 60 * MINUTE;

	private static final long DAY = 24 * HOUR;

	private Timer timer;

	public TaskListBackupManager() {
//		boolean enabled = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(
//				TaskListPreferenceConstants.BACKUP_AUTOMATICALLY);
//		if (enabled) {
		int days = TasksUiPlugin.getDefault().getPreferenceStore().getInt(TasksUiPreferenceConstants.BACKUP_SCHEDULE);
		if (days > 0) {
			start(MINUTE);
		}
	}

	public void start(long delay) {
		timer = new Timer();
		timer.schedule(new CheckBackupRequired(), delay, HOUR);
	}

	public void stop() {
		timer.cancel();
	}

	public void propertyChange(PropertyChangeEvent event) {
//		if (event.getProperty().equals(TaskListPreferenceConstants.BACKUP_AUTOMATICALLY)) {
//			if ((Boolean) event.getNewValue() == true) {
//				start(MINUTE);
//			} else {
//				stop();
//			}
//		}
	}

	public void backupNow(boolean synchronous) {
//		String destination = MylarTaskListPlugin.getMylarCorePrefs().getString(
//				TaskListPreferenceConstants.BACKUP_FOLDER);
		String destination = TasksUiPlugin.getDefault().getBackupFolderPath();
		
		File backupFolder = new File(destination);
		if (!backupFolder.exists()) {
			backupFolder.mkdir();
		}

		removeOldBackups(backupFolder);

		String fileName = TaskDataExportWizard.getZipFileName();

		if (!synchronous) {

			ExportJob export = new ExportJob(destination, fileName);
			export.schedule();

		} else {

			final TaskDataExportJob backupJob = new TaskDataExportJob(destination, true, fileName);

			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			try {
				service.run(true, false, backupJob);
				TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_LAST,
						new Date().getTime());
			} catch (InterruptedException e) {
				// ignore
			} catch (InvocationTargetException e) {
				MessageDialog.openError(null, TITLE_TASKLIST_BACKUP, BACKUP_FAILURE_MESSAGE);
			}

		}
	}

	/** public for testing purposes */
	public void removeOldBackups(File folder) {

		int maxBackups = TasksUiPlugin.getDefault().getPreferenceStore().getInt(TasksUiPreferenceConstants.BACKUP_MAXFILES);

		File[] files = folder.listFiles();
		ArrayList<File> backupFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getName().startsWith(TaskDataExportWizard.ZIP_FILE_PREFIX)) {
				backupFiles.add(file);
			}
		}

		File[] backupFileArray = backupFiles.toArray(new File[backupFiles.size()]);

		if (backupFileArray != null && backupFileArray.length > 0) {
			Arrays.sort(backupFileArray, new Comparator<File>() {
				public int compare(File file1, File file2) {
					return new Long((file1).lastModified()).compareTo(new Long((file2).lastModified()));
				}

			});

			int toomany = backupFileArray.length - maxBackups;
			if (toomany > 0) {
				for (int x = 0; x < toomany; x++) {
					if(backupFileArray[x] != null) {
						backupFileArray[x].delete();
					}
				}
			}
		}
	}
	
//	public File getMostRecentBackup() {
//		String destination = TasksUiPlugin.getDefault().getBackupFolderPath();
//
//		File backupFolder = new File(destination);
//		ArrayList<File> backupFiles = new ArrayList<File>();
//		if (backupFolder.exists()) {
//			File[] files = backupFolder.listFiles();
//			for (File file : files) {
//				if (file.getName().startsWith(TaskDataExportWizard.ZIP_FILE_PREFIX)) {
//					backupFiles.add(file);
//				}
//			}
//		} 
//
//		File[] backupFileArray = backupFiles.toArray(new File[backupFiles.size()]);
//   
//		if (backupFileArray != null && backupFileArray.length > 0) {
//			Arrays.sort(backupFileArray, new Comparator<File>() {
//				public int compare(File file1, File file2) {
//					return (new Long((file1).lastModified()).compareTo(new Long((file2).lastModified()))) * -1;
//				}
//
//			});
//		}
//		if (backupFileArray != null && backupFileArray.length > 0) {
//			return backupFileArray[0];
//		}
//		
//		return null;
//	}

	class CheckBackupRequired extends TimerTask {
  
		@Override
		public void run() {
			if (!Platform.isRunning() || TasksUiPlugin.getDefault() == null) {
				return;
			} else {
				long lastBackup = TasksUiPlugin.getDefault().getPreferenceStore().getLong(
						TasksUiPreferenceConstants.BACKUP_LAST);
				int days = TasksUiPlugin.getDefault().getPreferenceStore().getInt(TasksUiPreferenceConstants.BACKUP_SCHEDULE);
				long waitPeriod = days * DAY;
				final long now = new Date().getTime();

				if ((now - lastBackup) > waitPeriod) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							backupNow(false);
						}
					});
				}
			}
		}
	}

	static class ExportJob extends Job {

		final TaskDataExportJob backupJob;

		public ExportJob(String destination, String filename) {
			super(BACKUP_JOB_NAME);
			backupJob = new TaskDataExportJob(destination, true, filename);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				if (Platform.isRunning()) {
					backupJob.run(monitor);
					TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_LAST,
							new Date().getTime());
				}
			} catch (InvocationTargetException e) {
				MessageDialog
						.openError(null, BACKUP_JOB_NAME,
								"Error occured during scheduled tasklist backup.\nCheck settings on Tasklist preferences page.");
			} catch (InterruptedException e) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

	}
}
