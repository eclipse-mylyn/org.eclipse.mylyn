/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.monitor.core.util.ZipFileUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Rob Elves
 */
public class TaskDataImportWizard extends Wizard implements IImportWizard {

	private final static String SETTINGS_SECTION = "org.eclipse.mylyn.tasklist.ui.importWizard";

	private final static String WINDOW_TITLE = "Import";

	private TaskDataImportWizardPage importPage = null;

	public TaskDataImportWizard() {
		super();
		IDialogSettings masterSettings = TasksUiPlugin.getDefault().getDialogSettings();
		setDialogSettings(getSettingsSection(masterSettings));
		setNeedsProgressMonitor(true);
		setWindowTitle(WINDOW_TITLE);
	}

	/**
	 * Finds or creates a dialog settings section that is used to make the dialog control settings persistent
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
		importPage = new TaskDataImportWizardPage();
		importPage.setWizard(this);
		addPage(importPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

	@Override
	public boolean canFinish() {
		return importPage.isPageComplete();
	}

	/**
	 * Called when the user clicks finish. Saves the task data. Waits until all overwrite decisions have been made
	 * before starting to save files. If any overwrite is canceled, no files are saved and the user must adjust the
	 * dialog.
	 */
	@Override
	public boolean performFinish() {

		TasksUiPlugin.getTaskListManager().deactivateTask(
				TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask());

		File sourceDirFile = null;
		File sourceZipFile = null;
		File sourceTaskListFile = null;
		File sourceRepositoriesFile = null;
		File sourceActivationHistoryFile = null;
		List<File> contextFiles = new ArrayList<File>();
		List<ZipEntry> zipFilesToExtract = new ArrayList<ZipEntry>();
		boolean overwrite = importPage.overwrite();
		// zip = true post 1.0.1, see history for folder import
		// boolean zip = importPage.zip();

		String sourceZip = importPage.getSourceZipFile();
		sourceZipFile = new File(sourceZip);

		if (!sourceZipFile.exists()) {
			MessageDialog.openError(getShell(), "File not found", sourceZipFile.toString() + " could not be found.");
			return false;
		}

		Enumeration<? extends ZipEntry> entries;
		ZipFile zipFile;

		try {
			zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (entry.isDirectory()) {
					// ignore directories (shouldn't be any)
					continue;
				}
				if (!importPage.importTaskList() && entry.getName().endsWith(ITasksUiConstants.OLD_TASK_LIST_FILE)) {
					continue;
				}

				if (!importPage.importActivationHistory()
						&& entry.getName().endsWith(
								InteractionContextManager.CONTEXT_HISTORY_FILE_NAME
										+ InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD)) {
					continue;
				}
				if (!importPage.importTaskContexts()
						&& entry.getName().matches(
								".*-\\d*" + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD + "$")) {
					continue;
				}

				File destContextFile = new File(TasksUiPlugin.getDefault().getDataDirectory() + File.separator
						+ entry.getName());

				if (!overwrite && destContextFile.exists()) {
					if (MessageDialog.openConfirm(getShell(), "File exists!", "Overwrite existing file?\n"
							+ destContextFile.getName())) {
						zipFilesToExtract.add(entry);
					} else {
						// no overwrite
					}
				} else {
					zipFilesToExtract.add(entry);
				}

			}

		} catch (IOException e) {
			StatusHandler.fail(e, "Could not import files", true);
		}

		FileCopyJob job = new FileCopyJob(sourceDirFile, sourceZipFile, sourceTaskListFile, sourceRepositoriesFile,
				sourceActivationHistoryFile, contextFiles, zipFilesToExtract);

		IProgressService service = PlatformUI.getWorkbench().getProgressService();

		try {
			service.run(true, false, job);
		} catch (InvocationTargetException e) {
			StatusHandler.fail(e, "Could not import files", true);
		} catch (InterruptedException e) {
			StatusHandler.fail(e, "Could not import files", true);
		}

		importPage.saveSettings();
		return true;
	}

	/** Job that performs the file copying and zipping */
	class FileCopyJob implements IRunnableWithProgress {

		private static final String JOB_LABEL = "Importing Data";

		private File sourceZipFile = null;

		private List<ZipEntry> zipEntriesToExtract;

		public FileCopyJob(File sourceFolder, File sourceZipFile, File sourceTaskListFile, File sourceRepositoriesFile,
				File sourceActivationHistoryFile, List<File> contextFiles, List<ZipEntry> zipEntries) {
			this.sourceZipFile = sourceZipFile;
			this.zipEntriesToExtract = zipEntries;
		}

		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

			// always a zip source since post 1.0.1

			monitor.beginTask(JOB_LABEL, zipEntriesToExtract.size() + 2);

			try {
				ZipFileUtil.extactEntries(sourceZipFile, zipEntriesToExtract, TasksUiPlugin.getDefault()
						.getDataDirectory());
				//ZipFileUtil.unzipFiles(sourceZipFile, TasksUiPlugin.getDefault().getDataDirectory());

			} catch (IOException ioe) {
				StatusHandler.fail(new Exception("Import Exception"), "Problem occured extracting from zip file.", true);
				return;
			}
			readTaskListData();
			monitor.done();
			return;

		}
	}

	/** Returns all tasks in the task list root or a category in the task list */
	protected List<AbstractTask> getAllTasks() {
		List<AbstractTask> allTasks = new ArrayList<AbstractTask>();
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();

		allTasks.addAll(taskList.getDefaultCategory().getChildren());

		for (AbstractTaskContainer category : taskList.getCategories()) {
			allTasks.addAll(category.getChildren());
		}

		return allTasks;
	}

	private void readTaskListData() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				TasksUiPlugin.getDefault().reloadDataDirectory(true);
			}
		});
	}

}
