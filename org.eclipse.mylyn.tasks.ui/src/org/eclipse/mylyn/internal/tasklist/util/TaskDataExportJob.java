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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.ZipFileUtil;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * Job that performs exporting (copying or zipping) of Mylar Task List data
 * Assumes that check with user for overwrite already done. Overwrites destination if exists!
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

	private File destTaskListFile = null;

	private File destActivationHistoryFile = null;

	/** export all data */
	public TaskDataExportJob(String destinationDirectory, boolean zipIt, String zipFileName) {
		this(destinationDirectory, true, true, true, zipIt, zipFileName);
	}

	/** export specified data */
	public TaskDataExportJob(String destinationDirectory, boolean exportTaskList, boolean exportActivationHistory,
			boolean exportTaskContexts, boolean zipIt, String zipFileName) {
		this.zipFileName = zipFileName;
		this.zip = zipIt;
		this.exportTaskList = exportTaskList;
		this.exportActivationHistory = exportActivationHistory;
		this.exportTaskContexts = exportTaskContexts;
		this.destinationDirectory = destinationDirectory;
	}

	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		Collection<ITask> tasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getAllTasks();
		int jobSize = 0;
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

		if (exportTaskList) {
			MylarTaskListPlugin.getTaskListManager().saveTaskList();

			String sourceTaskListPath = MylarPlugin.getDefault().getDataDirectory() + File.separator
					+ MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE;
			File sourceTaskListFile = new File(sourceTaskListPath);
			if (sourceTaskListFile.exists()) {
				destTaskListFile = new File(destinationDirectory + File.separator
						+ MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE);				

				if (zip) {
					filesToZip.add(sourceTaskListFile);
				} else if(!destTaskListFile.equals(sourceTaskListFile)) {
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
				File sourceActivationHistoryFile = new File(MylarPlugin.getDefault().getDataDirectory()
						+ File.separator + MylarContextManager.CONTEXT_HISTORY_FILE_NAME
						+ MylarContextManager.CONTEXT_FILE_EXTENSION);

				if (sourceActivationHistoryFile.exists()) {

					MylarPlugin.getContextManager().saveActivityHistoryContext();

					destActivationHistoryFile = new File(destinationDirectory + File.separator
							+ MylarContextManager.CONTEXT_HISTORY_FILE_NAME
							+ MylarContextManager.CONTEXT_FILE_EXTENSION);
					

					if (zip) {
						filesToZip.add(sourceActivationHistoryFile);
					} else if(!destActivationHistoryFile.equals(sourceActivationHistoryFile)){
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

				if (!MylarPlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
					continue; // Tasks without a context have no file to
					// copy
				}

				File sourceTaskFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());

				File destTaskFile = new File(destinationDirectory + File.separator + sourceTaskFile.getName());
				

				if (zip) {
					if (!filesToZipMap.containsKey(task.getHandleIdentifier())) {
						filesToZip.add(sourceTaskFile);
						filesToZipMap.put(task.getHandleIdentifier(), null);
					}
				} else if(!sourceTaskFile.equals(destTaskFile)) {
					if (destTaskFile.exists()) {
						destTaskFile.delete();
					}
					if (!copy(sourceTaskFile, destTaskFile) && !errorDisplayed) {						
						MylarStatusHandler.fail(new Exception("Export Exception: " + sourceTaskFile.getPath() + " -> "
								+ destTaskFile.getPath()), "Could not export one or more task context files.", true);
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
				ZipFileUtil.createZipFile(destZipFile, filesToZip, monitor);
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
