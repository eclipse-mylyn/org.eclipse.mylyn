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
import com.ibm.icu.text.DateFormat;
import java.util.SortedMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard Page for the Task Data Import Wizard
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Rob Elves (Adaption to Import wizard)
 */
public class TaskDataImportWizardPage extends WizardPage {

	private Button browseButtonZip = null;

	private Text sourceZipText = null;

	private Button importViaBackupButton;

	private Button importViaZipButton;

	private Table backupFilesTable;

	// Key values for the dialog settings object
	private final static String SETTINGS_SAVED = Messages.TaskDataImportWizardPage_Import_Settings_saved;

	private final static String SOURCE_ZIP_SETTING = Messages.TaskDataImportWizardPage_Import_Source_zip_file_setting;

	private final static String IMPORT_ZIPMETHOD_SETTING = Messages.TaskDataImportWizardPage_Import_method_zip;

	private final static String IMPORT_BACKUPMETHOD_SETTING = Messages.TaskDataImportWizardPage_Import_method_backup;

	public TaskDataImportWizardPage() {
		super("org.eclipse.mylyn.tasklist.importPage"); //$NON-NLS-1$
		setPageComplete(false);
		setMessage(Messages.TaskDataImportWizardPage_Importing_overwrites_current_tasks_and_repositories,
				IMessageProvider.WARNING);
		setImageDescriptor(CommonImages.BANNER_IMPORT);
		setTitle(Messages.TaskDataImportWizardPage_Restore_tasks_from_history);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 15;
		container.setLayout(layout);
		createImportFromZipControl(container);
		createImportFromBackupControl(container);
		addRadioListeners();
		initSettings();
		Dialog.applyDialogFont(container);
		setControl(container);
		setPageComplete(validate());
	}

	private void addRadioListeners() {
		SelectionListener radioListener = new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				browseButtonZip.setEnabled(importViaZipButton.getSelection());
				backupFilesTable.setEnabled(importViaBackupButton.getSelection());
				sourceZipText.setEnabled(importViaZipButton.getSelection());
				controlChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore

			}
		};

		importViaZipButton.addSelectionListener(radioListener);
		importViaBackupButton.addSelectionListener(radioListener);
	}

	/**
	 * Create widgets for specifying the source zip
	 */
	private void createImportFromZipControl(Composite parent) {

		importViaZipButton = new Button(parent, SWT.RADIO);
		importViaZipButton.setText(Messages.TaskDataImportWizardPage_From_zip_file);

		sourceZipText = new Text(parent, SWT.BORDER);
		sourceZipText.setEditable(true);
		GridDataFactory.fillDefaults().grab(true, false).hint(250, SWT.DEFAULT).applyTo(sourceZipText);
		sourceZipText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged();
			}
		});

		browseButtonZip = new Button(parent, SWT.PUSH);
		browseButtonZip.setText(Messages.TaskDataImportWizardPage_Browse_);
		browseButtonZip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setText(Messages.TaskDataImportWizardPage_Zip_File_Selection);
				String dir = sourceZipText.getText();
				dialog.setFilterPath(dir);
				dir = dialog.open();
				if (dir == null || dir.equals("")) { //$NON-NLS-1$
					return;
				}
				sourceZipText.setText(dir);
			}
		});

	}

	private void createImportFromBackupControl(Composite container) {

		importViaBackupButton = new Button(container, SWT.RADIO);
		importViaBackupButton.setText(Messages.TaskDataImportWizardPage_From_snapshot);
		addBackupFileView(container);
	}

	private void addBackupFileView(Composite composite) {
		backupFilesTable = new Table(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, true).applyTo(backupFilesTable);

		TableColumn filenameColumn = new TableColumn(backupFilesTable, SWT.LEFT);
		filenameColumn.setWidth(200);

		SortedMap<Long, File> backupFilesMap = TasksUiPlugin.getBackupManager().getBackupFiles();

		for (Long time : backupFilesMap.keySet()) {
			File file = backupFilesMap.get(time);
			TableItem item = new TableItem(backupFilesTable, SWT.NONE);
			item.setData(file.getAbsolutePath());
			item.setText(DateFormat.getDateTimeInstance().format(time));
		}

		backupFilesTable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
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
			importViaZipButton.setSelection(true);
			sourceZipText.setEnabled(true);
			backupFilesTable.setEnabled(false);

		} else {
			// Retrieve previous values from the dialog settings
			importViaZipButton.setSelection(settings.getBoolean(IMPORT_ZIPMETHOD_SETTING));
			importViaBackupButton.setSelection(settings.getBoolean(IMPORT_BACKUPMETHOD_SETTING));
			browseButtonZip.setEnabled(importViaZipButton.getSelection());
			sourceZipText.setEnabled(importViaZipButton.getSelection());

			backupFilesTable.setEnabled(importViaBackupButton.getSelection());
			String zipFile = settings.get(SOURCE_ZIP_SETTING);
			if (zipFile != null) {
				sourceZipText.setText(settings.get(SOURCE_ZIP_SETTING));
			}
		}
	}

	/**
	 * Saves the control values in the dialog settings to be used as defaults the next time the page is opened
	 */
	public void saveSettings() {
		IDialogSettings settings = getDialogSettings();

		settings.put(IMPORT_ZIPMETHOD_SETTING, importViaZipButton.getSelection());
		settings.put(IMPORT_BACKUPMETHOD_SETTING, importViaBackupButton.getSelection());
		settings.put(SETTINGS_SAVED, SETTINGS_SAVED);
	}

	/** Called to indicate that a control's value has changed */
	public void controlChanged() {
		setPageComplete(validate());
	}

	/** Returns true if the information entered by the user is valid */
	protected boolean validate() {
		if (importViaZipButton.getSelection() && sourceZipText.getText().equals("")) { //$NON-NLS-1$
			return false;
		}
		if (importViaBackupButton.getSelection() && backupFilesTable.getSelection().length == 0) {
			return false;
		}
		return true;
	}

	public String getSourceZipFile() {
		if (importViaZipButton.getSelection()) {
			return sourceZipText.getText();
		} else {
			if (backupFilesTable.getSelectionIndex() != -1) {
				return (String) (backupFilesTable.getSelection()[0].getData());
			}
		}
		return Messages.TaskDataImportWizardPage__unspecified_;
	}

	/** For testing only. Sets controls to the specified values */
	public void setSource(boolean zip, String sourceZip) {
		sourceZipText.setText(sourceZip);
		importViaZipButton.setSelection(zip);
	}
}
