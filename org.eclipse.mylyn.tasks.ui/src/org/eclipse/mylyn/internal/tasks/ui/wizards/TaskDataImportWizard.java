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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
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
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.ZipFileUtil;
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
@SuppressWarnings("restriction")
public class TaskDataImportWizard extends Wizard implements IImportWizard {

	private TaskDataImportWizardPage importPage = null;

	public TaskDataImportWizard() {
		IDialogSettings masterSettings = TasksUiPlugin.getDefault().getDialogSettings();
		setDialogSettings(getSettingsSection(masterSettings));
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.TaskDataImportWizard_Import);
	}

	/**
	 * Finds or creates a dialog settings section that is used to make the dialog control settings persistent
	 */
	public IDialogSettings getSettingsSection(IDialogSettings master) {
		IDialogSettings settings = master.getSection("org.eclipse.mylyn.tasklist.ui.importWizard"); //$NON-NLS-1$
		if (settings == null) {
			settings = master.addNewSection("org.eclipse.mylyn.tasklist.ui.importWizard"); //$NON-NLS-1$
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

		String sourceZip = importPage.getSourceZipFile();
		File sourceZipFile = new File(sourceZip);

		if (!sourceZipFile.exists()) {
			MessageDialog.openError(getShell(), Messages.TaskDataImportWizard_File_not_found, sourceZipFile.toString()
					+ Messages.TaskDataImportWizard_could_not_be_found);
			return false;
		} else if (!CoreUtil.TEST_MODE
				&& !MessageDialog.openConfirm(getShell(), Messages.TaskDataImportWizard_confirm_overwrite,
						Messages.TaskDataImportWizard_existing_task_data_about_to_be_erased_proceed)) {
			return false;
		}

		Enumeration<? extends ZipEntry> entries;
		ZipFile zipFile;
		boolean restoreM2Tasklist = false;
		int numEntries = 0;

		try {
			zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().startsWith(ITasksCoreConstants.OLD_TASK_LIST_FILE)) {
					restoreM2Tasklist = true;
				}
				numEntries++;
			}
		} catch (IOException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not import files", e)); //$NON-NLS-1$
			return false;
		}

		FileCopyJob job = new FileCopyJob(sourceZipFile, numEntries);
		job.setRestoreM2Tasklist(restoreM2Tasklist);
		try {
			if (getContainer() != null) {
				getContainer().run(true, true, job);
			} else {
				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				service.run(true, true, job);
			}
		} catch (InvocationTargetException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not import files", e)); //$NON-NLS-1$
		} catch (InterruptedException e) {
			// User canceled
		}

		importPage.saveSettings();
		return true;
	}

	/** Job that performs the file copying and zipping */
	class FileCopyJob implements IRunnableWithProgress {

		private static final String PREFIX_BACKUP = ".backup-"; //$NON-NLS-1$

		private/*static*/final String JOB_LABEL = Messages.TaskDataImportWizard_Importing_Data;

		private File sourceZipFile = null;

		private int numEntries = 0;

		private boolean restoreM2Tasklist = false;

		public FileCopyJob(File sourceZipFile, int numEntries) {
			this.sourceZipFile = sourceZipFile;
			this.numEntries = numEntries;
		}

		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

			// always a zip source since post 1.0.1
			try {
				monitor.beginTask(JOB_LABEL, numEntries);
				Job.getJobManager().beginRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE, monitor);

				if (!sourceZipFile.exists()) {
					throw new InvocationTargetException(new IOException("Source file does not exist.")); //$NON-NLS-1$
				}

				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				ZipFileUtil.unzipFiles(sourceZipFile, TasksUiPlugin.getDefault().getDataDirectory(), monitor);

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
					TasksUiInternal.displayStatus(Messages.TaskDataImportWizard_Import_Error, e.getStatus());
				}
			}
		});
	}

}
