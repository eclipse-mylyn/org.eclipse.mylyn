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
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataExportOperation;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * Wizard for exporting tasklist data files to the file system. This wizard uses a single page: TaskDataExportWizardPage
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 */
public class TaskDataExportWizard extends Wizard implements IExportWizard {

	/**
	 * The name of the dialog store's section associated with the task data export wizard
	 */
	private final static String SETTINGS_SECTION = "org.eclipse.mylyn.tasklist.ui.exportWizard"; //$NON-NLS-1$

	private TaskDataExportWizardPage exportPage = null;

	public TaskDataExportWizard() {
		IDialogSettings masterSettings = TasksUiPlugin.getDefault().getDialogSettings();
		setDialogSettings(getSettingsSection(masterSettings));
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.TaskDataExportWizard_Export);
	}

	/**
	 * Finds or creates a dialog settings section that is used to make the dialog control settings persistent
	 */
	private IDialogSettings getSettingsSection(IDialogSettings master) {
		IDialogSettings settings = master.getSection(SETTINGS_SECTION);
		if (settings == null) {
			settings = master.addNewSection(SETTINGS_SECTION);
		}
		return settings;
	}

	@Override
	public void addPages() {
		exportPage = new TaskDataExportWizardPage();
		addPage(exportPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

	/**
	 * Called when the user clicks finish. Saves the task data. Waits until all overwrite decisions have been made
	 * before starting to save files. If any overwrite is canceled, no files are saved and the user must adjust the
	 * dialog.
	 */
	@Override
	public boolean performFinish() {
		String destDir = exportPage.getDestinationDirectory();

		final File destZipFile = new File(destDir + File.separator + TaskListBackupManager.getBackupFileName());

		TaskDataExportOperation job = new TaskDataExportOperation(exportPage.getDestinationDirectory(),
				destZipFile.getName());

		try {
			if (getContainer() != null) {
				getContainer().run(true, true, job);
			} else {
				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				service.run(true, true, job);
			}
		} catch (InvocationTargetException e) {
			Status status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e);
			TasksUiInternal.logAndDisplayStatus(Messages.TaskDataExportWizard_export_failed, status);
		} catch (InterruptedException e) {
			// user canceled
		}

		exportPage.saveSettings();
		return true;
	}
}
