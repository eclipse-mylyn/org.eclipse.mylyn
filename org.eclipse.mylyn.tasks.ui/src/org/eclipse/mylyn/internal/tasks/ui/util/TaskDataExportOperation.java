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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.commons.core.ZipFileUtil;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Job that performs exporting (copying or zipping) of the Task List data Assumes that check with user for overwrite
 * already done. Overwrites destination if exists!
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Rob Elves
 * 
 *         TODO: Move into internal.tasks.core
 */
@SuppressWarnings("restriction")
public class TaskDataExportOperation implements IRunnableWithProgress {

	private static final String EXPORT_JOB_LABEL = Messages.TaskDataExportOperation_exporting_task_data;

	private final boolean zip;

	private final boolean exportTaskList;

	private final boolean exportActivationHistory;

	private final boolean exportTaskContexts;

	private final String destinationDirectory;

	private final String zipFileName;

	private File destZipFile = null;

	private final Collection<AbstractTask> tasks;

	private boolean exportAll = false;

	// List of files to add to the zip archive
	private final List<File> filesToZip = new ArrayList<File>();

	// Map of file paths used to avoid duplicates
	private final Map<String, String> filesToZipMap = new HashMap<String, String>();

	/** export all data */
	public TaskDataExportOperation(String destinationDirectory, boolean zipIt, String zipFileName) {
		this.zipFileName = zipFileName;
		this.zip = zipIt;
		this.destinationDirectory = destinationDirectory;
		this.tasks = Collections.emptySet();
		this.exportActivationHistory = true;
		this.exportTaskContexts = true;
		this.exportTaskList = true;
		this.exportAll = true;
	}

	/** export specified data */
	public TaskDataExportOperation(String destinationDirectory, boolean exportTaskList,
			boolean exportActivationHistory, boolean exportTaskContexts, boolean zipIt, String zipFileName,
			Collection<AbstractTask> taskContextsToExport) {
		this.zipFileName = zipFileName;
		this.zip = zipIt;
		this.exportTaskList = exportTaskList;
		this.exportActivationHistory = exportActivationHistory;
		this.exportTaskContexts = exportTaskContexts;
		this.destinationDirectory = destinationDirectory;
		this.tasks = taskContextsToExport;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}

			int totalWork = filesToZip.size();
			if (totalWork == 0) {
				totalWork = IProgressMonitor.UNKNOWN;
			}
			monitor.beginTask(EXPORT_JOB_LABEL, totalWork + 1);

			Job.getJobManager().beginRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE, new SubProgressMonitor(monitor, 1));
			if (exportAll) {
				exportAll();
			} else {
				exportChosen();
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
					StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not create zip file", //$NON-NLS-1$
							e));
				}
			}
		} finally {
			Job.getJobManager().endRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE);
			monitor.done();
		}
	}

	private void exportAll() {
		String dataRoot = TasksUiPlugin.getDefault().getDataDirectory();
		File dataFolder = new File(dataRoot);
		// add all files in dataRoot
		for (File file : dataFolder.listFiles()) {
			filesToZip.add(file);
		}
	}

	private void exportChosen() {
		int jobSize = 1; // 1 for repositories.xml
		if (exportTaskList) {
			jobSize++;
		}
		if (exportActivationHistory) {
			jobSize++;
		}
		if (exportTaskContexts) {
			jobSize += tasks.size();
		}

		String sourceContextsPath = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
				+ ITasksCoreConstants.CONTEXTS_DIRECTORY;
		File contextsDirectory = new File(sourceContextsPath);

		// Task Repositories data file
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
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Could not export repositories file", new Exception())); //$NON-NLS-1$
				}
			}
		}

		if (exportTaskList) {
			String sourceTaskListPath = TasksUiPlugin.getDefault().getDataDirectory() + File.separator
					+ ITasksCoreConstants.DEFAULT_TASK_LIST_FILE;
			File sourceTaskListFile = new File(sourceTaskListPath);
			if (sourceTaskListFile.exists()) {
				File destTaskListFile = new File(destinationDirectory + File.separator
						+ ITasksCoreConstants.DEFAULT_TASK_LIST_FILE);

				if (zip) {
					filesToZip.add(sourceTaskListFile);
				} else if (!destTaskListFile.equals(sourceTaskListFile)) {
					if (destTaskListFile.exists()) {
						destTaskListFile.delete();
					}
					if (!copy(sourceTaskListFile, destTaskListFile)) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Could not export task list file", new Exception())); //$NON-NLS-1$
					}
				}
			}

		}

		if (exportActivationHistory) {
			try {
				File sourceActivationHistoryFile = new File(contextsDirectory,
						InteractionContextManager.CONTEXT_HISTORY_FILE_NAME
								+ InteractionContextManager.CONTEXT_FILE_EXTENSION);

				if (sourceActivationHistoryFile.exists()) {

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
					}
				}
			} catch (RuntimeException e) {
				// FIXME what is caught here?
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not export activity history context file", e)); //$NON-NLS-1$
			}
		}

		if (exportTaskContexts) {
			// Prevent many repeated error messages
			boolean errorDisplayed = false;
			for (ITask task : tasks) {

				if (!ContextCore.getContextManager().hasContext(task.getHandleIdentifier())) {
					continue; // Tasks without a context have no file to
					// copy
				}

				File sourceTaskContextFile = ContextCorePlugin.getContextStore().getFileForContext(
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
						Exception e = new Exception("Export Exception: " + sourceTaskContextFile.getPath() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
								+ destTaskFile.getPath());
						StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Could not export one or more task context files", e)); //$NON-NLS-1$
						errorDisplayed = true;
					}
				}
			}
		}
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
