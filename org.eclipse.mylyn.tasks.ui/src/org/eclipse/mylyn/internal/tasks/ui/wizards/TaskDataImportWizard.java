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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.ZipFileUtil;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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

	@Override
	public boolean performFinish() {

		TasksUi.getTaskActivityManager().deactivateTask(TasksUi.getTaskActivityManager().getActiveTask());

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
		boolean restoreM2Tasklist = false;

		try {
			zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (entry.isDirectory()) {
					// ignore directories (shouldn't be any)
					continue;
				}
				if (!importPage.importTaskList() && entry.getName().startsWith(ITasksCoreConstants.OLD_TASK_LIST_FILE)) {
					continue;
				}

				if (importPage.importTaskList() && entry.getName().startsWith(ITasksCoreConstants.OLD_TASK_LIST_FILE)) {
					restoreM2Tasklist = true;
				}

				if (!importPage.importTaskList()
						&& entry.getName().startsWith(ITasksCoreConstants.DEFAULT_TASK_LIST_FILE)) {
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
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not import files", e));
		}

		FileCopyJob job = new FileCopyJob(sourceDirFile, sourceZipFile, sourceTaskListFile, sourceRepositoriesFile,
				sourceActivationHistoryFile, contextFiles, zipFilesToExtract);
		job.setRestoreM2Tasklist(restoreM2Tasklist);
		try {
			if (getContainer() != null) {
				getContainer().run(true, true, job);
			} else {
//			IProgressService service = PlatformUI.getWorkbench().getProgressService();
//			service.busyCursorWhile(updateRunnable);
				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				service.run(true, true, job);
			}
		} catch (InvocationTargetException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not import files", e));
		} catch (InterruptedException e) {
			// User cancelled
		}

		importPage.saveSettings();
		return true;
	}

	/** Job that performs the file copying and zipping */
	class FileCopyJob implements IRunnableWithProgress {

		private static final String PREFIX_BACKUP = ".backup-";

		private static final String JOB_LABEL = "Importing Data";

		private File sourceZipFile = null;

		private final List<ZipEntry> zipEntriesToExtract;

		private boolean restoreM2Tasklist = false;

		public FileCopyJob(File sourceFolder, File sourceZipFile, File sourceTaskListFile, File sourceRepositoriesFile,
				File sourceActivationHistoryFile, List<File> contextFiles, List<ZipEntry> zipEntries) {
			this.sourceZipFile = sourceZipFile;
			this.zipEntriesToExtract = zipEntries;
		}

		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

			// always a zip source since post 1.0.1
			try {
				monitor.beginTask(JOB_LABEL, zipEntriesToExtract.size() + 2);
				Job.getJobManager().beginRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE, monitor);

				if (!sourceZipFile.exists()) {
					throw new InvocationTargetException(new IOException("Source file does not exist."));
				}

				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				ZipFileUtil.extactEntries(sourceZipFile, zipEntriesToExtract, TasksUiPlugin.getDefault()
						.getDataDirectory());

				if (restoreM2Tasklist) {

					SimpleDateFormat format = new SimpleDateFormat(ITasksCoreConstants.FILENAME_TIMESTAMP_FORMAT,
							Locale.ENGLISH);
					String date = format.format(new Date());

					File taskListFile = new File(TasksUiPlugin.getDefault().getDataDirectory(),
							ITasksCoreConstants.DEFAULT_TASK_LIST_FILE);
					if (taskListFile.exists()) {
						taskListFile.renameTo(new File(taskListFile.getParentFile(), taskListFile.getName()
								+ PREFIX_BACKUP + date));
					}

					File taskListFileSnapshot = new File(TasksUiPlugin.getDefault().getDataDirectory(),
							AbstractExternalizationParticipant.SNAPSHOT_PREFIX
									+ ITasksCoreConstants.DEFAULT_TASK_LIST_FILE);
					if (taskListFileSnapshot.exists()) {
						taskListFileSnapshot.renameTo(new File(taskListFile.getParentFile(),
								taskListFileSnapshot.getName() + PREFIX_BACKUP + date));
					}

				}
				readTaskListData();
			} catch (IOException e) {
				throw new InvocationTargetException(e);
			} finally {
				Job.getJobManager().endRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE);
				monitor.done();
			}
			return;

		}

		public void setRestoreM2Tasklist(boolean restoreM2Tasklist) {
			this.restoreM2Tasklist = restoreM2Tasklist;
		}

	}

	private void readTaskListData() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					TasksUiPlugin.getDefault().reloadDataDirectory();
				} catch (CoreException e) {
					TasksUiInternal.displayStatus("Import Error: Please retry importing or use alternate source",
							e.getStatus());
				}
			}
		});
	}

}
