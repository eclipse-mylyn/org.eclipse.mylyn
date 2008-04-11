/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataExportJob;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataExportWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Rob Elves
 */
public class TaskListBackupManager implements IPropertyChangeListener {

	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd-HHmmss";

	private static final String TITLE_TASKLIST_BACKUP = "Tasklist Backup";

	private static final String BACKUP_JOB_NAME = "Scheduled task data backup";

	public static final String BACKUP_FAILURE_MESSAGE = "Could not backup task data. Check backup preferences.\n";

	private static final long SECOND = 1000;

	private static final long MINUTE = 60 * SECOND;

	private static final long HOUR = 60 * MINUTE;

	private static final long DAY = 24 * HOUR;

	private Timer timer;

	//private static final Pattern zipPattern = Pattern.compile("^" + TaskDataExportWizard.ZIP_FILE_PREFIX + ".*");

	public TaskListBackupManager() {
		int days = TasksUiPlugin.getDefault().getPreferenceStore().getInt(TasksUiPreferenceConstants.BACKUP_SCHEDULE);
		if (days > 0) {
			start(2 * MINUTE);
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

		int maxBackups = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
				TasksUiPreferenceConstants.BACKUP_MAXFILES);

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
					if (backupFileArray[x] != null) {
						backupFileArray[x].delete();
					}
				}
			}
		}
	}

	/** public for testing purposes */
	public synchronized static void removeOldBackups(File folder, Pattern pattern, int maxBackups) {

		if (maxBackups <= 0) {
			maxBackups = 1;
		}

		File[] files = folder.listFiles();
		ArrayList<File> backupFiles = new ArrayList<File>();
		for (File file : files) {
			Matcher matcher = pattern.matcher(file.getName());
			if (matcher.find()) {
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

			SortedMap<Long, File> filesMap = new TreeMap<Long, File>();
			for (File file2 : backupFileArray) {
				String name = file2.getName();
				if (name.startsWith(ITasksUiConstants.PREFIX_TASKLIST)) {
					try {
						String dateString = name.substring(ITasksUiConstants.PREFIX_TASKLIST.length() + 1,
								ITasksUiConstants.PREFIX_TASKLIST.length() + TIMESTAMP_FORMAT.length() + 1);
						SimpleDateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH);
						Date date = format.parse(dateString);
						filesMap.put(new Long(date.getTime()), file2);
					} catch (Exception e) {
						continue;
					}
				}
			}

			if (filesMap.size() > 0) {

				Calendar rangeStart = TaskActivityUtil.getCalendar();
				rangeStart.setTimeInMillis(filesMap.lastKey());
				TaskActivityUtil.snapStartOfHour(rangeStart);
				int startHour = rangeStart.get(Calendar.HOUR_OF_DAY);
				Calendar rangeEnd = TaskActivityUtil.getCalendar();
				rangeEnd.setTimeInMillis(rangeStart.getTimeInMillis());
				rangeEnd.add(Calendar.HOUR_OF_DAY, 1);
				// Keep one backup for last 8 hours of today
				for (int x = 1; x <= startHour && x < 9; x++) {
					SortedMap<Long, File> subMap = filesMap.subMap(rangeStart.getTimeInMillis(),
							rangeEnd.getTimeInMillis());
					if (subMap.size() > 1) {
						while (subMap.size() > 1) {
							File toDelete = subMap.remove(subMap.firstKey());
							toDelete.delete();
						}
					}
					rangeStart.add(Calendar.HOUR_OF_DAY, -1);
					rangeEnd.add(Calendar.HOUR_OF_DAY, -1);
				}

				// Keep one backup a day for the past 12 days
				rangeEnd.setTimeInMillis(rangeStart.getTimeInMillis());
				rangeStart.add(Calendar.DAY_OF_YEAR, -1);
				for (int x = 1; x <= 12; x++) {
					SortedMap<Long, File> subMap = filesMap.subMap(rangeStart.getTimeInMillis(),
							rangeEnd.getTimeInMillis());
					if (subMap.size() > 1) {
						while (subMap.size() > 1) {
							File toDelete = subMap.remove(subMap.firstKey());
							toDelete.delete();
						}
					}
					rangeStart.add(Calendar.DAY_OF_YEAR, -1);
					rangeEnd.add(Calendar.DAY_OF_YEAR, -1);
				}

				// Remove all older backups
				SortedMap<Long, File> subMap = filesMap.subMap(0l, rangeStart.getTimeInMillis());
				if (subMap.size() > 0) {
					while (subMap.size() > 0) {
						File toDelete = subMap.remove(subMap.firstKey());
						toDelete.delete();
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
				int days = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
						TasksUiPreferenceConstants.BACKUP_SCHEDULE);
				long waitPeriod = days * DAY;
				final long now = new Date().getTime();

				if ((now - lastBackup) > waitPeriod) {
					if (Platform.isRunning() && !PlatformUI.getWorkbench().isClosing()
							&& PlatformUI.getWorkbench().getDisplay() != null
							&& !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								backupNow(false);
							}
						});
					}
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
				MessageDialog.openError(null, BACKUP_JOB_NAME,
						"Error occured during scheduled tasklist backup.\nCheck settings on Tasklist preferences page.");
			} catch (InterruptedException e) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

	}
}
