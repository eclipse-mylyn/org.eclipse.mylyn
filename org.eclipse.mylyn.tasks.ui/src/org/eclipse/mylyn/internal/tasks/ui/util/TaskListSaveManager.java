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

package org.eclipse.mylar.internal.tasks.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListChangeListener;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class TaskListSaveManager implements ITaskListChangeListener, IBackgroundSaveListener {

	private final static int DEFAULT_SAVE_INTERVAL = 5 * 60 * 1000;

	private static final String FILE_SUFFIX_BACKUP = "-backup.xml.zip";

	private BackgroundSaveTimer saveTimer = null;

	private boolean initializationWarningDialogShow = false;

	/**
	 * Fort testing.
	 */
	private boolean forceBackgroundSave = false;

	public TaskListSaveManager() {
		saveTimer = new BackgroundSaveTimer(this);
		saveTimer.setSaveIntervalMillis(DEFAULT_SAVE_INTERVAL);
		saveTimer.start();
	}

	/**
	 * Called periodically by the save timer
	 */
	public void saveRequested() {
		if (TasksUiPlugin.getDefault() != null && TasksUiPlugin.getDefault().isShellActive() || forceBackgroundSave) {
			try {
				saveTaskList(true);
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not auto save task list", false);
			}
		}
	}

	public void saveTaskList(boolean saveContext) {
		if (TasksUiPlugin.getDefault() != null && TasksUiPlugin.getDefault().isInitialized()) {
			TasksUiPlugin.getTaskListManager().saveTaskList();
			if (saveContext) {
				for (ITask task : new ArrayList<ITask>(TasksUiPlugin.getTaskListManager().getTaskList()
						.getActiveTasks())) {
					ContextCorePlugin.getContextManager().saveContext(task.getHandleIdentifier());
				}
			}
		} else if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()){
			MylarStatusHandler.log("Possible task list initialization failure, not saving list.", this);
			if (!initializationWarningDialogShow) {
				initializationWarningDialogShow = true;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getDisplay() != null) {
							MessageDialog
									.openInformation(
											PlatformUI.getWorkbench().getDisplay().getActiveShell(),
											TasksUiPlugin.TITLE_DIALOG,
											"If task list is blank, Mylar Task List may have failed to initialize.\n\n"
													+ "First, try restarting to see if that corrects the problem.\n\n"
													+ "Then, check the Error Log view for messages, and the FAQ for solutions.\n\n"
													+ TasksUiPlugin.URL_HOMEPAGE);
						}
					}
				});
			}
		}
	}

	/**
	 * Copies all files in the current data directory to the specified folder.
	 * Will overwrite.
	 */
	public void copyDataDirContentsTo(String targetFolderPath) {
		File mainDataDir = new File(TasksUiPlugin.getDefault().getDataDirectory());

		for (File currFile : mainDataDir.listFiles()) {
			if (currFile.isFile()) {
				File destFile = new File(targetFolderPath + File.separator + currFile.getName());
				copy(currFile, destFile);
			} else if (currFile.isDirectory()) {
				File destDir = new File(targetFolderPath + File.separator + currFile.getName());
				if (!destDir.exists()) {
					if (!destDir.mkdir()) {
						MylarStatusHandler.log("Unable to create destination context folder: "
								+ destDir.getAbsolutePath(), this);
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

	public void createTaskListBackupFile() {
		String path = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
				+ TasksUiPlugin.DEFAULT_TASK_LIST_FILE;
		File taskListFile = new File(path);
		String backup = path.substring(0, path.indexOf('.')) + FILE_SUFFIX_BACKUP;
		copy(taskListFile, new File(backup));
	}

	public String getBackupFilePath() {
		String path = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
				+ TasksUiPlugin.DEFAULT_TASK_LIST_FILE;
		return path.substring(0, path.indexOf('.')) + FILE_SUFFIX_BACKUP;
	}

	public void reverseBackup() {
		String path = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
				+ TasksUiPlugin.DEFAULT_TASK_LIST_FILE;
		File taskListFile = new File(path);
		String backup = path.substring(0, path.indexOf('.')) + FILE_SUFFIX_BACKUP;
		copy(new File(backup), taskListFile);
	}

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

	public void taskActivated(ITask task) {
		// ignore
	}

	public void tasksActivated(List<ITask> tasks) {
		// ignore
	}

	public void taskDeactivated(ITask task) {
		saveTaskList(true);
	}

	public void localInfoChanged(ITask task) {
		saveTaskList(false);
	}

	public void repositoryInfoChanged(ITask task) {
		// ignore
	}

	public void tasklistRead() {
		// ignore
	}

	/**
	 * For testing.
	 */
	public void setForceBackgroundSave(boolean on) {
		forceBackgroundSave = on;
		saveTimer.setForceSyncExec(on);
	}

	public void taskMoved(ITask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
		saveTaskList(false);
	}

	public void taskDeleted(ITask task) {
		saveTaskList(false);
	}

	public void containerAdded(AbstractTaskContainer container) {
		saveTaskList(false);
	}

	public void containerDeleted(AbstractTaskContainer container) {
		saveTaskList(false);
	}

	public void taskAdded(ITask task) {
		saveTaskList(false);
	}

	/** For testing only * */
	public BackgroundSaveTimer getSaveTimer() {
		return saveTimer;
	}

	public void containerInfoChanged(AbstractTaskContainer container) {
		saveTaskList(false);
	}
}
