/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataExportOperation;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataSnapshotOperation;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Rob Elves
 */
public class TaskListBackupManager implements IPropertyChangeListener {

	private static final String OLD_MYLYN_2_BACKUP_FILE_PREFIX = "mylyndata-"; //$NON-NLS-1$

	// Mylyn 3.0 Backup file name
	private static final String BACKUP_FILE_PREFIX = "mylyn-v3-data-"; //$NON-NLS-1$

	private static final Pattern MYLYN_BACKUP_REGEXP = Pattern.compile("^(" + BACKUP_FILE_PREFIX + ")?(" //$NON-NLS-1$ //$NON-NLS-2$
			+ OLD_MYLYN_2_BACKUP_FILE_PREFIX + ")?"); //$NON-NLS-1$

	private static final Pattern DATE_FORMAT_OLD = Pattern.compile("\\d{4}-\\d{2}-\\d{2}"); //$NON-NLS-1$

	private static final Pattern DATE_FORMAT = Pattern.compile("\\d{4}-\\d{2}-\\d{2}-\\d{6}"); //$NON-NLS-1$

	private static final long SECOND = 1000;

	private static final long MINUTE = 60 * SECOND;

	private static final long STANDARD_DELAY = 30 * MINUTE;

	private String backupFolderPath;

	private Job runBackup;

	public TaskListBackupManager(String backupFolderPath) {
		this.backupFolderPath = backupFolderPath;
		start(STANDARD_DELAY);
	}

	public void start(long delay) {

		if (runBackup != null) {
			stop();
		}

		runBackup = new Job("Task Data Snapshot") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					if (TasksUiPlugin.getTaskList().getAllTasks().size() > 0) {
						backupNow(false, monitor);
					}
					return Status.OK_STATUS;
				} finally {
					schedule(STANDARD_DELAY);
				}
			}
		};
		runBackup.setPriority(Job.BUILD);
		runBackup.setSystem(true);
		runBackup.schedule(delay);
	}

	public void stop() {
		if (runBackup != null) {
			if (!runBackup.cancel()) {
				try {
					runBackup.join();
				} catch (InterruptedException e) {
					// ignore
				}
			}
			runBackup = null;
		}
	}

	public static String getBackupFileName() {
		SimpleDateFormat format = new SimpleDateFormat(ITasksCoreConstants.FILENAME_TIMESTAMP_FORMAT, Locale.ENGLISH);
		String date = format.format(new Date());
		String backupFileName = BACKUP_FILE_PREFIX + date + ".zip"; //$NON-NLS-1$
		return backupFileName;
	}

	public void backupNow(boolean synchronous) {
		backupNow(synchronous, null);
	}

	public void backupNow(boolean synchronous, IProgressMonitor monitor) {

		monitor = Policy.monitorFor(monitor);

		File backupFolder = new File(backupFolderPath);
		if (!backupFolder.exists()) {
			backupFolder.mkdir();
		}

		final TaskDataExportOperation backupJob = new TaskDataSnapshotOperation(backupFolderPath, getBackupFileName());
		try {
			if (!synchronous && Platform.isRunning()) {
				backupJob.run(monitor);
				removeOldBackups();
			} else {
				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				service.run(false, true, backupJob);
			}
		} catch (InvocationTargetException e) {
			Status status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					Messages.TaskListBackupManager_Error_occured_during_scheduled_tasklist_backup, e);
			TasksUiInternal.logAndDisplayStatus(Messages.TaskListBackupManager_Scheduled_task_data_backup, status);
		} catch (InterruptedException e) {
			return;
		}
	}

	public SortedMap<Long, File> getBackupFiles() {

		SortedMap<Long, File> filesMap = new TreeMap<Long, File>();
		String destination = backupFolderPath;

		File backupFolder = new File(destination);
		if (!backupFolder.exists()) {
			return filesMap;
		}

		File[] files = backupFolder.listFiles();
		if (files == null) {
			return filesMap;
		}

		for (File file : files) {
			Matcher matcher = MYLYN_BACKUP_REGEXP.matcher(file.getName());
			if (matcher.find()) {
				Date date = null;
				try {
					SimpleDateFormat format = null;
					String dateText = null;
					Matcher dateFormatMatcher = DATE_FORMAT.matcher(file.getName());
					if (dateFormatMatcher.find()) {
						format = new SimpleDateFormat(ITasksCoreConstants.FILENAME_TIMESTAMP_FORMAT, Locale.ENGLISH);
						dateText = dateFormatMatcher.group();
					} else {
						dateFormatMatcher = DATE_FORMAT_OLD.matcher(file.getName());
						if (dateFormatMatcher.find()) {
							format = new SimpleDateFormat(ITasksCoreConstants.OLD_FILENAME_TIMESTAMP_FORMAT,
									Locale.ENGLISH);
							dateText = dateFormatMatcher.group();
						}
					}
					if (format != null && dateText != null && dateText.length() > 0) {
						date = format.parse(dateText);
					} else {
						continue;
					}
				} catch (IndexOutOfBoundsException e) {
					continue;
				} catch (ParseException e) {
					continue;
				}
				if (date != null && date.getTime() > 0) {
					filesMap.put(new Long(date.getTime()), file);
				}
			}
		}

		return filesMap;
	}

	/** public for testing purposes */
	public synchronized void removeOldBackups() {

		SortedMap<Long, File> filesMap = getBackupFiles();

		if (filesMap.size() > 0) {

			Calendar rangeStart = TaskActivityUtil.getCalendar();
			rangeStart.setTimeInMillis(filesMap.lastKey());
			TaskActivityUtil.snapStartOfHour(rangeStart);
			int startHour = rangeStart.get(Calendar.HOUR_OF_DAY);
			Calendar rangeEnd = TaskActivityUtil.getCalendar();
			rangeEnd.setTimeInMillis(rangeStart.getTimeInMillis());
			rangeEnd.add(Calendar.HOUR_OF_DAY, 1);
			// Keep one backup for last 8 hours of today
			for (int x = 0; x <= startHour && x < 9; x++) {
				SortedMap<Long, File> subMap = filesMap.subMap(rangeStart.getTimeInMillis(), rangeEnd.getTimeInMillis());
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
			TaskActivityUtil.snapStartOfDay(rangeEnd);
			rangeStart.add(Calendar.DAY_OF_YEAR, -1);
			for (int x = 1; x <= 12; x++) {
				SortedMap<Long, File> subMap = filesMap.subMap(rangeStart.getTimeInMillis(), rangeEnd.getTimeInMillis());
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

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(ITasksUiPreferenceConstants.PREF_DATA_DIR)) {
			backupFolderPath = TasksUiPlugin.getDefault().getBackupFolderPath();
		}
	}

}
