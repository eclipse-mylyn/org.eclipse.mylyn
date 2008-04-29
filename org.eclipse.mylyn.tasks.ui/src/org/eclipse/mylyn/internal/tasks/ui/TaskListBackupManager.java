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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
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
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.internal.context.core.ContextPreferenceContstants;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataExportOperation;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Rob Elves
 */
public class TaskListBackupManager implements IPropertyChangeListener {

	public static final String BACKUP_FILE_PREFIX = "mylyndata-";

	public static final Pattern MYLYN_BACKUP_REGEXP = Pattern.compile("^" + BACKUP_FILE_PREFIX + ".*");

	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd-HHmmss";

	private static final String TITLE_TASKLIST_BACKUP = "Tasklist Backup";

	private static final String BACKUP_JOB_NAME = "Scheduled task data backup";

	public static final String BACKUP_FAILURE_MESSAGE = "Could not backup task data. Check backup preferences.\n";

	private static final long SECOND = 1000;

	private static final long MINUTE = 60 * SECOND;

	private static final long HOUR = 60 * MINUTE;

	//private static final long DAY = 24 * HOUR;

	private Timer timer;

	private String backupFolderPath;

	public TaskListBackupManager(String backupFolderPath) {
		this.backupFolderPath = backupFolderPath;
		start(7000);//HOUR
	}

	public void start(long delay) {
		timer = new Timer();
		timer.schedule(new CheckBackupRequired(), delay, 30 * MINUTE);
	}

	public void stop() {
		timer.cancel();
	}

	private String getBackupFileName() {
		SimpleDateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH);
		String date = format.format(new Date());
		String backupFileName = BACKUP_FILE_PREFIX + date + ".zip";
		return backupFileName;
	}

	public void backupNow(boolean synchronous) {

		File backupFolder = new File(backupFolderPath);
		if (!backupFolder.exists()) {
			backupFolder.mkdir();
		}

//		String backkupFilePath = backupFolderPath + File.separator + getBackupFileName();
		TasksUiPlugin.getExternalizationManager().requestSave();

		if (!synchronous) {
			ExportJob export = new ExportJob(backupFolderPath, getBackupFileName());
			export.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {
					// TODO: Run this in separate job after snapshot has taken place
					removeOldBackups();
				}
			});
			export.schedule();

		} else {

			final TaskDataExportOperation backupJob = new TaskDataExportOperation(backupFolderPath, true, true, false,
					true, getBackupFileName(), new HashSet<AbstractTask>());

			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			try {
				service.run(false, true, backupJob);

//				TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_LAST,
//						new Date().getTime());
			} catch (InterruptedException e) {
				// ignore
			} catch (InvocationTargetException e) {
				MessageDialog.openError(null, TITLE_TASKLIST_BACKUP, BACKUP_FAILURE_MESSAGE);
			}

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

		ArrayList<File> backupFiles = new ArrayList<File>();
		for (File file : files) {
			Matcher matcher = MYLYN_BACKUP_REGEXP.matcher(file.getName());
			if (matcher.find()
					&& file.getName().length() == BACKUP_FILE_PREFIX.length() + TIMESTAMP_FORMAT.length() + 4) {
				backupFiles.add(file);
			}
		}

		File[] backupFileArray = backupFiles.toArray(new File[backupFiles.size()]);

		if (backupFileArray != null && backupFileArray.length > 0) {
//			Arrays.sort(backupFileArray, new Comparator<File>() {
//				public int compare(File file1, File file2) {
//					return new Long((file1).lastModified()).compareTo(new Long((file2).lastModified()));
//				}
//			});

			for (File backupFile : backupFileArray) {
				String name = backupFile.getName();
				if (name.startsWith(BACKUP_FILE_PREFIX)) {
					try {
						String dateString = name.substring(BACKUP_FILE_PREFIX.length(), BACKUP_FILE_PREFIX.length()
								+ TIMESTAMP_FORMAT.length());
						SimpleDateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH);
						Date date = format.parse(dateString);
						if (date.getTime() < 0) {
							continue;
						}
						filesMap.put(new Long(date.getTime()), backupFile);
					} catch (Throwable e) {
						continue;
					}
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

	class CheckBackupRequired extends TimerTask {

		@Override
		public void run() {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					backupNow(false);
				}
			});
//				if (!Platform.isRunning() || TasksUiPlugin.getDefault() == null) {
//					return;
//				} else {
//					long lastBackup = TasksUiPlugin.getDefault().getPreferenceStore().getLong(
//							TasksUiPreferenceConstants.BACKUP_LAST);
//					int days = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
//							TasksUiPreferenceConstants.BACKUP_SCHEDULE);
//					long waitPeriod = days * DAY;
//					final long now = new Date().getTime();
			//
//					if ((now - lastBackup) > waitPeriod) {
//						if (Platform.isRunning() && !PlatformUI.getWorkbench().isClosing()
//								&& PlatformUI.getWorkbench().getDisplay() != null
//								&& !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
//							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//								public void run() {
//									backupNow(false);
//								}
//							});
//						}
//					}
//				}
		}
	}

	static class ExportJob extends Job {

		final TaskDataExportOperation backupJob;

		public ExportJob(String destination, String filename) {
			super(BACKUP_JOB_NAME);

			backupJob = new TaskDataExportOperation(destination, true, true, false, true, filename,
					new HashSet<AbstractTask>());
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				if (Platform.isRunning()) {
					backupJob.run(monitor);
//					TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_LAST,
//							new Date().getTime());
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

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(ContextPreferenceContstants.PREF_DATA_DIR)) {
			if (event.getNewValue() instanceof String) {
				backupFolderPath = (String) event.getNewValue();
				//System.err.println(">>> backup path changed: " + backupFolderPath);
			}
		}
	}

//	/** public for testing purposes */
//	public void removeOldBackups(File folder) {
//
//		int maxBackups = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
//				TasksUiPreferenceConstants.BACKUP_MAXFILES);
//
//		File[] files = folder.listFiles();
//		ArrayList<File> backupFiles = new ArrayList<File>();
//		for (File file : files) {
//			if (file.getName().startsWith(TaskDataExportWizard.ZIP_FILE_PREFIX)) {
//				backupFiles.add(file);
//			}
//		}
//
//		File[] backupFileArray = backupFiles.toArray(new File[backupFiles.size()]);
//
//		if (backupFileArray != null && backupFileArray.length > 0) {
//			Arrays.sort(backupFileArray, new Comparator<File>() {
//				public int compare(File file1, File file2) {
//					return new Long((file1).lastModified()).compareTo(new Long((file2).lastModified()));
//				}
//
//			});
//
//			int toomany = backupFileArray.length - maxBackups;
//			if (toomany > 0) {
//				for (int x = 0; x < toomany; x++) {
//					if (backupFileArray[x] != null) {
//						backupFileArray[x].delete();
//					}
//				}
//			}
//		}
//	}

}
