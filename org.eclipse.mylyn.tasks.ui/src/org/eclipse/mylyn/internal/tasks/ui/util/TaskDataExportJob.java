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

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.core.util.ZipFileUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.WorkspaceAwareContextStore;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Job that performs exporting (copying or zipping) of Mylar Task List data
 * Assumes that check with user for overwrite already done. Overwrites
 * destination if exists!
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskDataExportJob implements IRunnableWithProgress {

	private static final String JOB_LABEL = "Exporting Mylar Task Data";

	private boolean zip;

	private boolean exportTaskList;

	private boolean exportActivationHistory;

	private boolean exportTaskContexts;

	private String destinationDirectory;

	private String zipFileName;

	private File destZipFile = null;

	private Collection<ITask> tasks;

	/** export all data */
	public TaskDataExportJob(String destinationDirectory, boolean zipIt, String zipFileName) {
		this(destinationDirectory, true, true, true, zipIt, zipFileName, TasksUiPlugin.getTaskListManager()
				.getTaskList().getAllTasks());
	}

	/** export specified data */
	public TaskDataExportJob(String destinationDirectory, boolean exportTaskList, boolean exportActivationHistory,
			boolean exportTaskContexts, boolean zipIt, String zipFileName, Collection<ITask> taskContextsToExport) {
		this.zipFileName = zipFileName;
		this.zip = zipIt;
		this.exportTaskList = exportTaskList;
		this.exportActivationHistory = exportActivationHistory;
		this.exportTaskContexts = exportTaskContexts;
		this.destinationDirectory = destinationDirectory;
		this.tasks = taskContextsToExport;
	}

	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		// Collection<ITask> tasks =
		// MylarTaskListPlugin.getTaskListManager().getTaskList().getAllTasks();
		int jobSize = 1; // 1 for repositories.xml
		if (exportTaskList)
			jobSize++;
		if (exportActivationHistory)
			jobSize++;
		if (exportTaskContexts)
			jobSize += tasks.size();
		monitor.beginTask(JOB_LABEL, jobSize);

		// List of files to add to the zip archive
		List<File> filesToZip = new ArrayList<File>();

		// Map of file paths used to avoid duplicates
		Map<String, String> filesToZipMap = new HashMap<String, String>();

		// Create folders in zip file before contained files
		String sourceContextsPath = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
				+ WorkspaceAwareContextStore.CONTEXTS_DIRECTORY;
		File contextsDirectory = new File(sourceContextsPath);
		// if(contextsDirectory.exists()) {
		// filesToZip.add(contextsDirectory);
		// }

		if (true) {
			// Repositories always exported
			TasksUiPlugin.getRepositoryManager().saveRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

			String sourceRepositoriesPath = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
					+ TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE;
			File sourceRepositoriesFile = new File(sourceRepositoriesPath);
			if (sourceRepositoriesFile.exists()) {
				File destRepositoriesFile = new File(destinationDirectory + File.separator
						+ TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE);

				if (zip) {
					filesToZip.add(sourceRepositoriesFile);
				} else if (!destRepositoriesFile.equals(sourceRepositoriesFile)) {
					if (destRepositoriesFile.exists()) {
						destRepositoriesFile.delete();
					}
					if (!copy(sourceRepositoriesFile, destRepositoriesFile)) {
						MylarStatusHandler.fail(new Exception("Export Exception"),
								"Could not export repositories file.", false);
					}
					monitor.worked(1);
				}
			}

		}

		if (exportTaskList) {
			TasksUiPlugin.getTaskListManager().saveTaskList();

			String sourceTaskListPath = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
					+ ITasksUiConstants.DEFAULT_TASK_LIST_FILE;
			File sourceTaskListFile = new File(sourceTaskListPath);
			if (sourceTaskListFile.exists()) {
				File destTaskListFile = new File(destinationDirectory + File.separator
						+ ITasksUiConstants.DEFAULT_TASK_LIST_FILE);

				if (zip) {
					filesToZip.add(sourceTaskListFile);
				} else if (!destTaskListFile.equals(sourceTaskListFile)) {
					if (destTaskListFile.exists()) {
						destTaskListFile.delete();
					}
					if (!copy(sourceTaskListFile, destTaskListFile)) {
						MylarStatusHandler.fail(new Exception("Export Exception"), "Could not export task list file.",
								false);
					}
					monitor.worked(1);
				}
			}

		}

		if (exportActivationHistory) {
			try {
				File sourceActivationHistoryFile = new File(contextsDirectory,
						InteractionContextManager.CONTEXT_HISTORY_FILE_NAME + InteractionContextManager.CONTEXT_FILE_EXTENSION);

				if (sourceActivationHistoryFile.exists()) {

					ContextCorePlugin.getContextManager().saveActivityContext();

					File destActivationHistoryFile = new File(destinationDirectory + File.separator
							+ InteractionContextManager.CONTEXT_HISTORY_FILE_NAME
							+ InteractionContextManager.CONTEXT_FILE_EXTENSION);

					if (zip) {
						filesToZip.add(sourceActivationHistoryFile);
					} else if (!destActivationHistoryFile.equals(sourceActivationHistoryFile)) {
						if (destActivationHistoryFile.exists()) {
							destActivationHistoryFile.delete();
						}
						copy(sourceActivationHistoryFile, destActivationHistoryFile);
						monitor.worked(1);
					}
				}
			} catch (RuntimeException e) {
				MylarStatusHandler.fail(e, "Could not export activity history context file", true);
			}
		}

		if (exportTaskContexts) {
			// Prevent many repeated error messages
			boolean errorDisplayed = false;
			for (ITask task : tasks) {

				if (!ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
					continue; // Tasks without a context have no file to
					// copy
				}

				File sourceTaskContextFile = ContextCorePlugin.getContextManager().getFileForContext(
						task.getHandleIdentifier());

				File destTaskFile = new File(destinationDirectory + File.separator + sourceTaskContextFile.getName());

				if (zip) {
					if (!filesToZipMap.containsKey(task.getHandleIdentifier())) {
						filesToZip.add(sourceTaskContextFile);
						filesToZipMap.put(task.getHandleIdentifier(), null);
					}
				} else if (!sourceTaskContextFile.equals(destTaskFile)) {
					if (destTaskFile.exists()) {
						destTaskFile.delete();
					}
					if (!copy(sourceTaskContextFile, destTaskFile) && !errorDisplayed) {
						MylarStatusHandler.fail(new Exception("Export Exception: " + sourceTaskContextFile.getPath()
								+ " -> " + destTaskFile.getPath()), "Could not export one or more task context files.",
								true);
						errorDisplayed = true;
					}
					monitor.worked(1);
				}
			}
		}

		if (zip && filesToZip.size() > 0) {
			try {
				destZipFile = new File(destinationDirectory + File.separator + zipFileName);
				if (destZipFile.exists()) {
					destZipFile.delete();
				}
				ZipFileUtil.createZipFile(destZipFile, filesToZip, TasksUiPlugin.getDefault().getDataDirectory(),
						monitor);
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not create zip file.", true);
			}
		}
		monitor.done();

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

}
