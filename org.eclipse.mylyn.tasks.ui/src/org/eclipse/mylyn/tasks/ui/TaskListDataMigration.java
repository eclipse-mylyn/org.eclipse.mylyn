/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.eclipse.mylar.internal.context.core.util.ZipFileUtil;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;

/**
 * Migrate 0.6 -> 0.7 mylar data format
 * 
 * @author Rob Elves
 */
public class TaskListDataMigration implements IRunnableWithProgress {

	private File dataDirectory = null;

	public TaskListDataMigration(File sourceFolder) {
		this.dataDirectory = sourceFolder;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			monitor.beginTask("Task Data Migration", IProgressMonitor.UNKNOWN);
			doMigration(monitor);
		} finally {

		}
	}

	public void doMigration(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Mylar Data Migration", 4);
			migrateTaskList(new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			monitor.worked(1);
			migrateRepositoriesData(new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			monitor.worked(1);
			migrateTaskContextData(new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			monitor.worked(1);
			migrateActivityData(new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}

	public boolean migrateTaskList(IProgressMonitor monitor) {
		File oldTasklistFile = new File(dataDirectory, TasksUiPlugin.OLD_TASK_LIST_FILE);
		File newTasklistFile = new File(dataDirectory, TasksUiPlugin.DEFAULT_TASK_LIST_FILE);
		if (!oldTasklistFile.exists())
			return false;
		if (newTasklistFile.exists()) {
			if (!newTasklistFile.delete()) {
				MylarStatusHandler.fail(null, "Could not overwrite tasklist", false);
				return false;
			}
		}
		ArrayList<File> filesToZip = new ArrayList<File>();
		filesToZip.add(oldTasklistFile);
		try {
			monitor.beginTask("Migrate Tasklist Data", 1);
			ZipFileUtil.createZipFile(newTasklistFile, filesToZip, new SubProgressMonitor(monitor, 1));
			if (!oldTasklistFile.delete()) {
				MylarStatusHandler.fail(null, "Could not remove old tasklist.", false);
				return false;
			}
			monitor.worked(1);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Error occurred while migrating old tasklist: " + e.getMessage(), true);
			return false;
		} finally {
			monitor.done();
		}
		return true;
	}

	public boolean migrateRepositoriesData(IProgressMonitor monitor) {
		File oldRepositoriesFile = new File(dataDirectory, TaskRepositoryManager.OLD_REPOSITORIES_FILE);
		File newRepositoriesFile = new File(dataDirectory, TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE);
		if (!oldRepositoriesFile.exists())
			return false;
		if (newRepositoriesFile.exists()) {
			if (!newRepositoriesFile.delete()) {
				MylarStatusHandler.fail(null,
						"Could not overwrite repositories file. Check read/write permission on data directory.", false);
				return false;
			}
		}
		ArrayList<File> filesToZip = new ArrayList<File>();
		filesToZip.add(oldRepositoriesFile);
		try {
			monitor.beginTask("Migrate Repository Data", 1);
			ZipFileUtil.createZipFile(newRepositoriesFile, filesToZip, new SubProgressMonitor(monitor, 1));
			if (!oldRepositoriesFile.delete()) {
				MylarStatusHandler
						.fail(
								null,
								"Could not remove old repositories file. Check read/write permission on data directory.",
								false);
				return false;
			}
			monitor.worked(1);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Error occurred while migrating old repositories data: " + e.getMessage(), true);
			return false;
		} finally {
			monitor.done();
		}
		return true;
	}

	public boolean migrateTaskContextData(IProgressMonitor monitor) {
		ArrayList<File> contextFiles = new ArrayList<File>();
		for (File file : dataDirectory.listFiles()) {
			if (file.getName().startsWith("http") || file.getName().startsWith("local")) {
				if (!file.getName().endsWith(".zip")) {
					contextFiles.add(file);
				}
			}
		}

		try {
			monitor.beginTask("Task Context Migration", contextFiles.size());

			File contextsFolder = new File(dataDirectory, MylarContextManager.CONTEXTS_DIRECTORY);
			if (!contextsFolder.exists()) {
				if (!contextsFolder.mkdir()) {
					MylarStatusHandler.fail(null,
							"Could not create contexts folder. Check read/write permission on data directory.", false);
					return false;
				}
			}
			for (File file : contextFiles) {
				ArrayList<File> filesToZip = new ArrayList<File>();
				filesToZip.add(file);
				File newContextFile = new File(contextsFolder, file.getName()+".zip");
				if (newContextFile.exists()) {
					if (!newContextFile.delete()) {
						MylarStatusHandler.fail(null,
								"Could not overwrite context file. Check read/write permission on data directory.",
								false);
						return false;
					}
				}
				ZipFileUtil.createZipFile(newContextFile, filesToZip, new SubProgressMonitor(monitor, 1));
				if (!file.delete()) {
					MylarStatusHandler.fail(null,
							"Could not remove old context file. Check read/write permission on data directory.", false);
					return false;
				}
				monitor.worked(1);
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Error occurred while migrating old repositories data: " + e.getMessage(), true);
			return false;
		} finally {
			monitor.done();
		}
		return true;
	}
		
	public boolean migrateActivityData(IProgressMonitor monitor) {
		File oldActivityFile = new File(dataDirectory, MylarContextManager.OLD_CONTEXT_HISTORY_FILE_NAME+MylarContextManager.OLD_CONTEXT_FILE_EXTENSION);
		if (!oldActivityFile.exists())
			return false;
		
		File contextsFolder = new File(dataDirectory, MylarContextManager.CONTEXTS_DIRECTORY);
		if (!contextsFolder.exists()) {
			if (!contextsFolder.mkdir()) {
				MylarStatusHandler.fail(null,
						"Could not create contexts folder. Check read/write permission on data directory.", false);
				return false;
			}
		}
		
		File newActivityFile = new File(contextsFolder, MylarContextManager.CONTEXT_HISTORY_FILE_NAME+MylarContextManager.CONTEXT_FILE_EXTENSION);
		
		if (newActivityFile.exists()) {
			if (!newActivityFile.delete()) {
				MylarStatusHandler.fail(null,
						"Could not overwrite activity file. Check read/write permission on data directory.", false);
				return false;
			}
		}
		ArrayList<File> filesToZip = new ArrayList<File>();
		filesToZip.add(oldActivityFile);
		try {
			monitor.beginTask("Migrate Activity Data", 1);
			ZipFileUtil.createZipFile(newActivityFile, filesToZip, new SubProgressMonitor(monitor, 1));
			if (!oldActivityFile.delete()) {
				MylarStatusHandler
						.fail(
								null,
								"Could not remove old activity file. Check read/write permission on data directory.",
								false);
				return false;
			}
			monitor.worked(1);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Error occurred while migrating old activity data: " + e.getMessage(), true);
			return false;
		} finally {
			monitor.done();
		}
		return true;
	}

}

//public boolean migrateActivityData(IProgressMonitor monitor) {
//File oldActivityFile = new File(dataDirectory, MylarContextManager.OLD_CONTEXT_HISTORY_FILE_NAME+MylarContextManager.OLD_CONTEXT_FILE_EXTENSION);
//if (!oldActivityFile.exists())
//	return false;
//		
//File newActivityFile = new File(dataDirectory, MylarContextManager.CONTEXT_HISTORY_FILE_NAME+MylarContextManager.CONTEXT_FILE_EXTENSION);
//
//if (newActivityFile.exists()) {
//	if (!newActivityFile.delete()) {
//		MylarStatusHandler.fail(null,
//				"Could not overwrite activity file. Check read/write permission on data directory.", false);
//		return false;
//	}
//}
//ArrayList<File> filesToZip = new ArrayList<File>();
//filesToZip.add(oldActivityFile);
//try {
//	monitor.beginTask("Migrate Activity Data", 1);
//	ZipFileUtil.createZipFile(newActivityFile, filesToZip, new SubProgressMonitor(monitor, 1));
//	if (!oldActivityFile.delete()) {
//		MylarStatusHandler
//				.fail(
//						null,
//						"Could not remove old activity file. Check read/write permission on data directory.",
//						false);
//		return false;
//	}
//	monitor.worked(1);
//} catch (Exception e) {
//	MylarStatusHandler.fail(e, "Error occurred while migrating old activity data: " + e.getMessage(), true);
//	return false;
//} finally {
//	monitor.done();
//}
//return true;
//}