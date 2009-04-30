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
import com.ibm.icu.text.SimpleDateFormat;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.internal.commons.core.ZipFileUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonsUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.ICoreRunnable;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

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
	private IDialogSettings getSettingsSection(IDialogSettings master) {
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
		final File sourceZipFile = new File(sourceZip);

		if (!sourceZipFile.exists()) {
			MessageDialog.openError(getShell(), Messages.TaskDataImportWizard_File_not_found, sourceZipFile.toString()
					+ Messages.TaskDataImportWizard_could_not_be_found);
			return false;
		} else if (!CoreUtil.TEST_MODE
				&& !MessageDialog.openConfirm(getShell(), Messages.TaskDataImportWizard_confirm_overwrite,
						Messages.TaskDataImportWizard_existing_task_data_about_to_be_erased_proceed)) {
			return false;
		}

		try {
			if (getContainer() != null) {
				CommonsUiUtil.run(getContainer(), new FileCopyJob(sourceZipFile));
			} else {
				CommonsUiUtil.busyCursorWhile(new FileCopyJob(sourceZipFile));
			}

			importPage.saveSettings();
		} catch (CoreException e) {
			Status status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
					"Problems encountered importing task data: {0}", e.getMessage()), e); //$NON-NLS-1$
			TasksUiInternal.logAndDisplayStatus(Messages.TaskDataImportWizard_task_data_import_failed, status);
		} catch (OperationCanceledException e) {
			// canceled
		}

		return true;
	}

	/** Job that performs the file copying and zipping */
	class FileCopyJob implements ICoreRunnable {

		private static final String PREFIX_BACKUP = ".backup-"; //$NON-NLS-1$

		private final String JOB_LABEL = Messages.TaskDataImportWizard_Importing_Data;

		private File sourceZipFile = null;

		private int numEntries = 0;

		private boolean restoreM2Tasklist = false;

		public FileCopyJob(File sourceZipFile) {
			this.sourceZipFile = sourceZipFile;
		}

		public void run(final IProgressMonitor monitor) throws CoreException {

			try {

				checkZipFile();

				if (numEntries > 0) {

					monitor.beginTask(JOB_LABEL, numEntries);
					Job.getJobManager().beginRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE, monitor);

					if (monitor.isCanceled()) {
						return;
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
				}
			} catch (IOException e) {
				Status status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e);
				throw new CoreException(status);
			} finally {
				Job.getJobManager().endRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE);
				monitor.done();
			}
		}

		private void checkZipFile() throws IOException {
			Enumeration<? extends ZipEntry> entries;
			ZipFile zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
			try {
				entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().startsWith(ITasksCoreConstants.OLD_TASK_LIST_FILE)) {
						restoreM2Tasklist = true;
					}
					numEntries++;
				}
			} finally {
				zipFile.close();
			}
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
