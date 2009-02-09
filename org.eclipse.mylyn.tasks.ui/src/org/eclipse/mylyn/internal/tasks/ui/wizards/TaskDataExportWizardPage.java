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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Wizard Page for the Task Data Export Wizard
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskDataExportWizardPage extends WizardPage {

	private static final String PAGE_ID = "org.eclipse.mylyn.tasklist.exportPage"; //$NON-NLS-1$

	private Button browseButton = null;

	private Text destDirText = null;

	// Key values for the dialog settings object
	private final static String SETTINGS_SAVED = "Settings saved"; //$NON-NLS-1$

	private final static String DEST_DIR_SETTING = "Destination directory setting"; //$NON-NLS-1$

	public TaskDataExportWizardPage() {
		super(PAGE_ID);
		setPageComplete(false);
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(TasksUiPlugin.ID_PLUGIN,
				"icons/wizban/banner-export.gif")); //$NON-NLS-1$
		setTitle(Messages.TaskDataExportWizardPage_Export_Mylyn_Task_Data);
	}

	/**
	 * Create the widgets on the page
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		createExportDirectoryControl(container);

		initSettings();

		Dialog.applyDialogFont(container);
		setControl(container);
		setPageComplete(validate());
	}

	/**
	 * Create widgets for specifying the destination directory
	 */
	private void createExportDirectoryControl(Composite parent) {
		parent.setLayout(new GridLayout(3, false));
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(parent, SWT.NONE).setText(Messages.TaskDataExportWizardPage_File);
		Label l = new Label(parent, SWT.NONE);
		l.setText(TaskListBackupManager.getBackupFileName());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		l.setLayoutData(gd);
		new Label(parent, SWT.NONE).setText(Messages.TaskDataExportWizardPage_Folder);

		destDirText = new Text(parent, SWT.BORDER);
		destDirText.setEditable(false);
		destDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged();
			}
		});

		browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText(Messages.TaskDataExportWizardPage_Browse_);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(Messages.TaskDataExportWizardPage_Folder_Selection);
				dialog.setMessage(Messages.TaskDataExportWizardPage_Specify_the_destination_folder_for_task_data);
				String dir = destDirText.getText();
				dialog.setFilterPath(dir);
				dir = dialog.open();
				if (dir == null || dir.equals("")) { //$NON-NLS-1$
					return;
				}
				destDirText.setText(dir);
				controlChanged();
			}
		});
	}

	/**
	 * Initializes controls with values from the Dialog Settings object
	 */
	protected void initSettings() {
		IDialogSettings settings = getDialogSettings();

		if (settings.get(SETTINGS_SAVED) == null) {
			destDirText.setText(""); //$NON-NLS-1$
		} else {
			String directory = settings.get(DEST_DIR_SETTING);
			if (directory != null) {
				destDirText.setText(settings.get(DEST_DIR_SETTING));
			}
		}
	}

	/**
	 * Saves the control values in the dialog settings to be used as defaults the next time the page is opened
	 */
	public void saveSettings() {
		IDialogSettings settings = getDialogSettings();
		settings.put(DEST_DIR_SETTING, destDirText.getText());
		settings.put(SETTINGS_SAVED, SETTINGS_SAVED);
	}

	/** Called to indicate that a control's value has changed */
	public void controlChanged() {
		setPageComplete(validate());
	}

	/** Returns true if the information entered by the user is valid */
	protected boolean validate() {
		setMessage(null);

		// Check that a destination dir has been specified
		if (destDirText.getText().equals("")) { //$NON-NLS-1$
			setMessage(Messages.TaskDataExportWizardPage_Please_choose_an_export_destination, IStatus.WARNING);
			return false;
		}

		return true;
	}

	/** Returns the directory where data files are to be saved */
	public String getDestinationDirectory() {
		return destDirText.getText();
	}

	/** For testing only. Sets controls to the specified values */
	public void setDestinationDirectory(String destinationDir) {
		destDirText.setText(destinationDir);
	}
}
