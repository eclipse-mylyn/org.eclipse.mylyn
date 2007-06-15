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
package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataExportJob;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * Wizard for exporting tasklist data files to the file system. This wizard uses
 * a single page: TaskDataExportWizardPage
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 */
public class TaskDataExportWizard extends Wizard implements IExportWizard {

	/**
	 * The name of the dialog store's section associated with the task data
	 * export wizard
	 */
	private final static String SETTINGS_SECTION = "org.eclipse.mylyn.tasklist.ui.exportWizard";

	public final static String ZIP_FILE_PREFIX = "mylardata";

	private final static String ZIP_FILE_EXTENSION = ".zip";

	private final static String WINDOW_TITLE = "Export";

	private TaskDataExportWizardPage exportPage = null;

	public static String getZipFileName() {
		String fomratString = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(fomratString, Locale.ENGLISH);
		String date = format.format(new Date());
		return ZIP_FILE_PREFIX + "-" + date + ZIP_FILE_EXTENSION;
	}

	public TaskDataExportWizard() {
		IDialogSettings masterSettings = TasksUiPlugin.getDefault().getDialogSettings();
		setDialogSettings(getSettingsSection(masterSettings));
		setNeedsProgressMonitor(true);
		setWindowTitle(WINDOW_TITLE);
	}

	/**
	 * Finds or creates a dialog settings section that is used to make the
	 * dialog control settings persistent
	 */
	public IDialogSettings getSettingsSection(IDialogSettings master) {
		IDialogSettings settings = master.getSection(SETTINGS_SECTION);
		if (settings == null) {
			settings = master.addNewSection(SETTINGS_SECTION);
		}
		return settings;
	}

	@Override
	public void addPages() {
		exportPage = new TaskDataExportWizardPage();
		exportPage.setWizard(this);
		addPage(exportPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

	@Override
	public boolean canFinish() {
		return exportPage.isPageComplete();
	}

	/**
	 * Called when the user clicks finish. Saves the task data. Waits until all
	 * overwrite decisions have been made before starting to save files. If any
	 * overwrite is canceled, no files are saved and the user must adjust the
	 * dialog.
	 */
	@Override
	public boolean performFinish() {
		boolean overwrite = exportPage.overwrite();
		boolean zip = exportPage.zip();
		
		Collection<AbstractTask> taskContextsToExport = TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks();

		// Get file paths to check for existence
		String destDir = exportPage.getDestinationDirectory();
		final File destDirFile = new File(destDir);
		if (!destDirFile.exists() || !destDirFile.isDirectory()) {
			// This should never happen
			StatusManager.fail(new Exception("File Export Exception"),
					"Could not export data because specified location does not exist or is not a folder", true);
			return false;
		}

		final File destTaskListFile = new File(destDir + File.separator + ITasksUiConstants.DEFAULT_TASK_LIST_FILE);
		final File destActivationHistoryFile = new File(destDir + File.separator
				+ InteractionContextManager.CONTEXT_HISTORY_FILE_NAME + InteractionContextManager.CONTEXT_FILE_EXTENSION);
		final File destZipFile = new File(destDir + File.separator + getZipFileName());

		// Prompt the user to confirm if ANY of the save repositoryOperations will cause
		// an overwrite
		if (!overwrite) {

			if (zip) {
				if (destZipFile.exists()) {
					if (!MessageDialog.openConfirm(getShell(), "Confirm File Replace", "The zip file "
							+ destZipFile.getPath() + " already exists. Do you want to overwrite it?")) {
						return false;
					}
				}
			} else {
				if (exportPage.exportTaskList() && destTaskListFile.exists()) {
					if (!MessageDialog.openConfirm(getShell(), "Confirm File Replace", "The task list file "
							+ destTaskListFile.getPath() + " already exists. Do you want to overwrite it?")) {
						return false;
					}
				}

				if (exportPage.exportActivationHistory() && destActivationHistoryFile.exists()) {
					if (!MessageDialog.openConfirm(getShell(), "Confirm File Replace",
							"The task activation history file " + destActivationHistoryFile.getPath()
									+ " already exists. Do you want to overwrite it?")) {
						return false;
					}
				}

				if (exportPage.exportTaskContexts()) {
					for (AbstractTask task : taskContextsToExport) {
						File contextFile = ContextCorePlugin.getContextManager()
								.getFileForContext(task.getHandleIdentifier());
						File destTaskFile = new File(destDir + File.separator + contextFile.getName());
						if (destTaskFile.exists()) {
							if (!MessageDialog.openConfirm(getShell(), "Confirm File Replace",
									"Task context files already exist in " + destDir
											+ ". Do you want to overwrite them?")) {
								return false;
							} else {
								break;
							}
						}
					}
				}
			}
		}

		// FileCopyJob job = new FileCopyJob(destZipFile, destTaskListFile,
		// destActivationHistoryFile);
		TaskDataExportJob job = new TaskDataExportJob(exportPage.getDestinationDirectory(), exportPage.exportTaskList(), exportPage
				.exportActivationHistory(), exportPage.exportTaskContexts(), exportPage.zip(), destZipFile.getName(), taskContextsToExport);
		IProgressService service = PlatformUI.getWorkbench().getProgressService();

		try {
			service.run(true, false, job);
		} catch (InvocationTargetException e) {
			StatusManager.fail(e, "Could not export files", true);
		} catch (InterruptedException e) {
			StatusManager.fail(e, "Could not export files", true);
		}

		exportPage.saveSettings();
		return true;
	}

	// /** Job that performs the file copying and zipping */
	// class FileCopyJob implements IRunnableWithProgress {
	//
	// private static final String JOB_LABEL = "Exporting Data";
	//
	// private File destZipFile = null;
	//
	// private File destTaskListFile = null;
	//
	// private File destActivationHistoryFile = null;
	//
	// private boolean zip;
	//
	// private boolean exportTaskList;
	//
	// private boolean exportActivationHistory;
	//
	// private boolean exportTaskContexts;
	//
	// private String destinationDirectory;
	//
	// public FileCopyJob(File destZipFile, File destTaskListFile, File
	// destActivationHistoryFile) {
	// this.destZipFile = destZipFile;
	// this.destTaskListFile = destTaskListFile;
	// this.destActivationHistoryFile = destActivationHistoryFile;
	//
	// // Get parameters here to avoid accessing the UI thread
	// this.zip = exportPage.zip();
	// this.exportTaskList = exportPage.exportTaskList();
	// this.exportActivationHistory = exportPage.exportActivationHistory();
	// this.exportTaskContexts = exportPage.exportTaskContexts();
	// this.destinationDirectory = exportPage.getDestinationDirectory();
	// }
	//
	// public void run(final IProgressMonitor monitor) throws
	// InvocationTargetException, InterruptedException {
	// List<ITask> tasks = getAllTasks();
	// monitor.beginTask(JOB_LABEL, tasks.size() + 2);
	//
	// // List of files to add to the zip archive
	// List<File> filesToZip = new ArrayList<File>();
	//
	// // Map of file paths used to avoid duplicates
	// Map<String, String> filesToZipMap = new HashMap<String, String>();
	//
	// if (exportTaskList) {
	// MylarTaskListPlugin.getTaskListManager().saveTaskList();
	//
	// String sourceTaskListPath = ContextCorePlugin.getDefault().getDataDirectory() +
	// File.separator
	// + MylarTaskListPlugin.DEFAULT_TASK_LIST_FILE;
	// File sourceTaskListFile = new File(sourceTaskListPath);
	//
	// if (zip) {
	// filesToZip.add(sourceTaskListFile);
	// } else {
	// if (!copy(sourceTaskListFile, destTaskListFile)) {
	// MylarStatusHandler.fail(new Exception("Export Exception"), "Could not
	// export task list file.",
	// false);
	// }
	// monitor.worked(1);
	// }
	//
	// }
	//
	// if (exportActivationHistory) {
	// try {
	// File sourceActivationHistoryFile = new
	// File(ContextCorePlugin.getDefault().getDataDirectory()
	// + File.separator + MylarContextManager.CONTEXT_HISTORY_FILE_NAME
	// + MylarContextManager.CONTEXT_FILE_EXTENSION);
	//
	// ContextCorePlugin.getContextManager().saveActivityHistoryContext();
	//
	// if (zip) {
	// filesToZip.add(sourceActivationHistoryFile);
	// } else {
	// copy(sourceActivationHistoryFile, destActivationHistoryFile);
	// monitor.worked(1);
	// }
	// } catch (RuntimeException e) {
	// MylarStatusHandler.fail(e, "Could not export activity history context
	// file", true);
	// }
	// }
	//
	// if (exportTaskContexts) {
	// boolean errorDisplayed = false; // Prevent many repeated error
	// // messages
	// for (ITask task : tasks) {
	//
	// if
	// (!ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier()))
	// {
	// continue; // Tasks without a context have no file to
	// // copy
	// }
	//
	// File contextFile =
	// ContextCorePlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
	//
	// File destTaskFile = new File(destinationDirectory + File.separator +
	// contextFile.getName());
	// File sourceTaskFile = contextFile;
	// // new File(ContextCorePlugin.getDefault().getDataDirectory() +
	// // File.separator + task.getContextPath()
	// // + MylarContextManager.CONTEXT_FILE_EXTENSION);
	//
	// if (zip) {
	// if (!filesToZipMap.containsKey(task.getHandleIdentifier())) {
	// filesToZip.add(sourceTaskFile);
	// filesToZipMap.put(task.getHandleIdentifier(), null);
	// }
	// } else {
	// if (!copy(sourceTaskFile, destTaskFile) && !errorDisplayed) {
	// MylarStatusHandler.fail(new Exception("Export Exception: " +
	// sourceTaskFile.getPath()
	// + " -> " + destTaskFile.getPath()),
	// "Could not export one or more task context files.", true);
	// errorDisplayed = true;
	// }
	// monitor.worked(1);
	// }
	// }
	// }
	//
	// if (zip) {
	// try {
	// if (destZipFile.exists()) {
	// destZipFile.delete();
	// }
	// ZipFileUtil.createZipFile(destZipFile, filesToZip, monitor);
	// } catch (Exception e) {
	// MylarStatusHandler.fail(e, "Could not create zip file.", true);
	// }
	// }
	// monitor.done();
	//
	// }
	// }

	// /** Returns all tasks in the task list root or a category in the task
	// list */
	// protected List<ITask> getAllTasks() {
	// List<ITask> allTasks = new ArrayList<ITask>();
	// TaskList taskList =
	// MylarTaskListPlugin.getTaskListManager().getTaskList();
	//
	// allTasks.addAll(taskList.getRootTasks());
	//
	// for (AbstractTaskContainer category : taskList.getCategories()) {
	// allTasks.addAll(category.getChildren());
	// }
	//
	// return allTasks;
	// }

	// // Note: Copied from MylarTaskListPlugin
	// private boolean copy(File src, File dst) {
	// try {
	// InputStream in = new FileInputStream(src);
	// OutputStream out = new FileOutputStream(dst);
	//
	// // Transfer bytes from in to out
	// byte[] buf = new byte[1024];
	// int len;
	// while ((len = in.read(buf)) > 0) {
	// out.write(buf, 0, len);
	// }
	// in.close();
	// out.close();
	// return true;
	// } catch (IOException ioe) {
	// return false;
	// }
	// }

}
