/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;

//import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Rob Elves
 */
public class TaskListSaveManager implements ITaskListChangeListener, IBackgroundSaveListener {

	private final static int DEFAULT_SAVE_INTERVAL = 1 * 60 * 1000;

	private final BackgroundSaveTimer saveTimer;

	private final TaskListSaverJob taskListSaverJob;

	private final boolean initializationWarningDialogShow = false;

	private static final int MAX_TASKLIST_SNAPSHOTS = 8;

	public static final Pattern SNAPSHOT_REGEXP = Pattern.compile("^tasklist-.*");

	public TaskListSaveManager() {
		saveTimer = new BackgroundSaveTimer(this);
		saveTimer.setSaveIntervalMillis(DEFAULT_SAVE_INTERVAL);
		saveTimer.start();

		taskListSaverJob = new TaskListSaverJob();
		//taskListSaverJob.setRule(TasksUiPlugin.getTaskListManager().getTaskList());
		taskListSaverJob.schedule();
	}

	/**
	 * Called periodically by the save timer
	 */
	public void saveRequested() {
		if (TasksUiPlugin.getDefault() != null && Platform.isRunning()) {// &&
			// TasksUiPlugin.getDefault().isShellActive()
			try {
				taskListSaverJob.runRequested();
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not auto save task list", e));
			}
		}
	}

	/**
	 * Should only be used by TaskListManager and unit tests
	 * 
	 * @param saveContext
	 * @param async
	 */
	public void saveTaskList(boolean saveContext, boolean async) {
		if (TasksUiPlugin.getDefault() != null && TasksUiPlugin.getDefault().isInitialized()) {
			TaskListManager taskListManager = TasksUiPlugin.getTaskListManager();
			if (async) {
				if (saveContext) {
					AbstractTask task = taskListManager.getActiveTask();
					if (task != null) {
						taskListSaverJob.addTaskContext(task);
					}
				}
				taskListSaverJob.requestSave();
			} else {
				taskListSaverJob.waitSaveCompleted();
				IInteractionContextManager contextManager = ContextCore.getContextManager();
				if (saveContext) {
					AbstractTask task = taskListManager.getActiveTask();
					if (task != null) {
						contextManager.saveContext(task.getHandleIdentifier());
					}
				}
				internalSaveTaskList();
			}
		} /*else if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
									StatusHandler.fail(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
											"Possible task list initialization failure, not saving list"));
									if (!initializationWarningDialogShow) {
										initializationWarningDialogShow = true;
										PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
											public void run() {
												if (PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getDisplay() != null) {
													MessageDialog.openInformation(
															PlatformUI.getWorkbench().getDisplay().getActiveShell(),
															ITasksUiConstants.TITLE_DIALOG,
															"If task list is blank, Mylyn Task List may have failed to initialize.\n\n"
																	+ "First, try restarting to see if that corrects the problem.\n\n"
																	+ "Then, check the Error Log view for messages, and the FAQ for solutions.\n\n"
																	+ ITasksUiConstants.URL_HOMEPAGE);
												}
											}
										});
									}*/
		//}
	}

	private synchronized void internalSaveTaskList() {
		TaskListManager taskListManager = TasksUiPlugin.getTaskListManager();
		File current = taskListManager.getTaskListFile();
		SimpleDateFormat format = new SimpleDateFormat(TaskListBackupManager.TIMESTAMP_FORMAT, Locale.ENGLISH);
		String date = format.format(new Date());
		String backupFileName = ITasksCoreConstants.PREFIX_TASKLIST + "-" + date + ITasksCoreConstants.FILE_EXTENSION;

		String destination = TasksUiPlugin.getDefault().getBackupFolderPath();

		File backupFolder = new File(destination);
		if (!backupFolder.exists()) {
			backupFolder.mkdir();
		}

		File backup = new File(backupFolder, backupFileName);
		if (current.renameTo(backup)) {
			TasksUiPlugin.getBackupManager().removeOldBackups();

			String newTasklistPath = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
					+ ITasksCoreConstants.DEFAULT_TASK_LIST_FILE;
			File newTaskListFile = new File(newTasklistPath);
			taskListManager.setTaskListFile(newTaskListFile);
		} else {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"Unable to create task list snapshot " + backup.getAbsolutePath()));
		}

		taskListManager.getTaskListWriter().writeTaskList(taskListManager.getTaskList(),
				taskListManager.getTaskListFile());
	}

	/**
	 * Copies all files in the current data directory to the specified folder. Will overwrite.
	 */
	public void copyDataDirContentsTo(String targetFolderPath) {
		saveTaskList(true, false);

		File mainDataDir = new File(TasksUiPlugin.getDefault().getDataDirectory());

		for (File currFile : mainDataDir.listFiles()) {
			if (currFile.isFile()) {
				File destFile = new File(targetFolderPath + File.separator + currFile.getName());
				copy(currFile, destFile);
			} else if (currFile.isDirectory()) {
				File destDir = new File(targetFolderPath + File.separator + currFile.getName());
				if (!destDir.exists()) {
					if (!destDir.mkdir()) {
						StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
								"Unable to create destination context folder: " + destDir.getAbsolutePath()));
						continue;
					}
				}
				for (File file : currFile.listFiles()) {
					File destFile = new File(destDir, file.getName());
					if (destFile.exists()) {
						destFile.delete();
					}
					copy(file, destFile);
				}
			}
		}
	}

	// public void createTaskListBackupFile() {
	// String path = TasksUiPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + TasksUiPlugin.DEFAULT_TASK_LIST_FILE;
	// File taskListFile = new File(path);
	// String backup = path.substring(0, path.indexOf('.')) +
	// FILE_SUFFIX_BACKUP;
	// copy(taskListFile, new File(backup));
	// }
	//
	// public String getBackupFilePath() {
	// String path = TasksUiPlugin.getDefault().getDataDirectory() +
	// File.separator
	// + TasksUiPlugin.DEFAULT_TASK_LIST_FILE;
	// return path.substring(0, path.indexOf('.')) + FILE_SUFFIX_BACKUP;
	// }
	//
	// public void reverseBackup() {
	// String path = TasksUiPlugin.getDefault().getBackupFolderPath() +
	// File.separator
	// + TasksUiPlugin.DEFAULT_TASK_LIST_FILE;
	// File taskListFile = new File(path);
	// String backup = path.substring(0, path.indexOf('.')) +
	// FILE_SUFFIX_BACKUP;
	// copy(new File(backup), taskListFile);
	// }

	private boolean copy(File src, File dst) {
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

	public void taskActivated(AbstractTask task) {
		// ignore
	}

	public void tasksActivated(List<AbstractTask> tasks) {
		// ignore
	}

	public void taskDeactivated(AbstractTask task) {
		saveTaskList(true, true);
	}

	public void localInfoChanged(AbstractTask task) {
		saveTaskList(false, true);
	}

	public void repositoryInfoChanged(AbstractTask task) {
		// ignore
	}

	/** For testing only * */
	public BackgroundSaveTimer getSaveTimer() {
		return saveTimer;
	}

	public void containersChanged(Set<TaskContainerDelta> containers) {
		saveTaskList(false, true);
	}

	public void synchronizationCompleted() {
		// ignore
	}

	private class TaskListSaverJob extends Job {

		private final Queue<AbstractTask> taskQueue = new LinkedList<AbstractTask>();

		private volatile boolean saveRequested = false;

		private volatile boolean saveCompleted = true;

		TaskListSaverJob() {
			super("Task List Saver");
			setPriority(Job.LONG);
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			while (true) {
				if (saveRequested) {
					saveRequested = false;
					saveCompleted = false;
					IInteractionContextManager contextManager = ContextCore.getContextManager();
					while (!taskQueue.isEmpty()) {
						AbstractTask task = taskQueue.poll();
						if (task != null) {
							contextManager.saveContext(task.getHandleIdentifier());
						}
					}
					internalSaveTaskList();
				}

				if (!saveRequested) {
					synchronized (this) {
						saveCompleted = true;
						notifyAll();
						try {
							wait();
						} catch (InterruptedException ex) {
							// ignore
						}
					}
				}
			}
		}

		void addTaskContext(AbstractTask task) {
			taskQueue.add(task);
		}

		void requestSave() {
			saveRequested = true;
		}

		void runRequested() {
			synchronized (this) {
				notifyAll();
			}
		}

		void waitSaveCompleted() {
			while (!saveCompleted) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException ex) {
						// ignore
					}
				}
			}
		}
	}

	public void taskListRead() {
	}

}
