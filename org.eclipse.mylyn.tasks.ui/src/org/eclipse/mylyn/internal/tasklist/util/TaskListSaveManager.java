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

package org.eclipse.mylar.internal.tasklist.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListChangeListener;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class TaskListSaveManager implements ITaskListChangeListener, DisposeListener, IBackgroundSaveListener {

	private final static int DEFAULT_SAVE_INTERVAL = 5 * 60 * 1000;

	private static final String FILE_SUFFIX_BACKUP = "-backup.xml";

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
		if (MylarTaskListPlugin.getDefault() != null && MylarTaskListPlugin.getDefault().isShellActive()
				|| forceBackgroundSave) {
			try {
				saveTaskListAndContexts();
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not auto save task list", false);
			}
		}
	}

	public void saveTaskListAndContexts() { 
		if (MylarTaskListPlugin.getDefault() != null && MylarTaskListPlugin.getDefault().isInitialized()) {
			MylarTaskListPlugin.getTaskListManager().saveTaskList();
			for (ITask task : new ArrayList<ITask>(MylarTaskListPlugin.getTaskListManager().getTaskList()
					.getActiveTasks())) {
				MylarPlugin.getContextManager().saveContext(task.getHandleIdentifier());
			}
		} else {
			MylarStatusHandler.log("Possible task list initialization failure, not saving list.", this);
			if (PlatformUI.getWorkbench() != null && !initializationWarningDialogShow) {
				initializationWarningDialogShow = true;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getDisplay() != null) {
							MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
									MylarTaskListPlugin.TITLE_DIALOG,
									"If task list is blank, Mylar Task List may have failed to initialize.\n\n"
											+ "First, try restarting to see if that corrects the problem.\n\n"
											+ "Then, check the Error Log view for messages, and the FAQ for solutions.\n\n"
											+ MylarTaskListPlugin.URL_HOMEPAGE);
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
		File mainDataDir = new File(MylarPlugin.getDefault().getDataDirectory());

		for (File currFile : mainDataDir.listFiles()) {
			if (currFile.isFile()) {
				File destFile = new File(targetFolderPath + File.separator + currFile.getName());
				copy(currFile, destFile);
			}
		}
	}

	public void createTaskListBackupFile() {
		String path = MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE;
		File taskListFile = new File(path);
		String backup = path.substring(0, path.lastIndexOf('.')) + FILE_SUFFIX_BACKUP;
		copy(taskListFile, new File(backup));
	}

	public String getBackupFilePath() {
		String path = MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE;
		return path.substring(0, path.lastIndexOf('.')) + FILE_SUFFIX_BACKUP;
	}

	public void reverseBackup() {
		String path = MylarPlugin.getDefault().getDataDirectory() + File.separator
				+ MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE;
		File taskListFile = new File(path);
		String backup = path.substring(0, path.lastIndexOf('.')) + FILE_SUFFIX_BACKUP;
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
		saveTaskListAndContexts();
	}

	public void localInfoChanged(ITask task) {
		saveTaskListAndContexts();
	}

	public void repositoryInfoChanged(ITask task) {
		// ignore
	}

	public void tasklistRead() {
		// ignore
	}

	public void widgetDisposed(DisposeEvent e) {
		saveTaskListAndContexts();
	}

	/**
	 * For testing.
	 */
	public void setForceBackgroundSave(boolean on) {
		forceBackgroundSave = on;
		saveTimer.setForceSyncExec(on);
	}

	public void taskMoved(ITask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
		saveTaskListAndContexts();
	}

	public void taskDeleted(ITask task) {
		saveTaskListAndContexts();
	}

	public void containerAdded(AbstractTaskContainer container) {
		saveTaskListAndContexts();
	}

	public void containerDeleted(AbstractTaskContainer container) {
		saveTaskListAndContexts();
	}
	
	public void taskAdded(ITask task) {
		saveTaskListAndContexts();
	}
	
	/** For testing only * */
	public BackgroundSaveTimer getSaveTimer() {
		return saveTimer;
	}

	public void containerInfoChanged(AbstractTaskContainer container) {
		saveTaskListAndContexts();
	}
}
