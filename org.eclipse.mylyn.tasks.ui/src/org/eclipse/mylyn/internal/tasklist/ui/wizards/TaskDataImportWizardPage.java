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
package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard Page for the Task Data Import Wizard
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Rob Elves (Adaption to Import wizard)
 */
public class TaskDataImportWizardPage extends WizardPage {

	private final static String PAGE_TITLE = "Import Mylar Task Data";

	private static final String DESCRIPTION = "WARNING: importing overwrites current task list, use with caution.";

	public final static String PAGE_NAME = PAGE_TITLE;

	private Button taskListCheckBox = null;

	private Button taskActivationHistoryCheckBox = null;

	private Button taskContextsCheckBox = null;

	private Button browseButton = null;

	private Button browseButtonZip = null;

	private Text sourceDirText = null;

	private Text sourceZipText = null;

	private Button overwriteCheckBox = null;

	private Group importFromZipGroup;

	private Group importFromFolderGroup;

	private Button importViaFolderButton;

	private Button importViaZipButton;

	// Key values for the dialog settings object
	private final static String SETTINGS_SAVED = "Import Settings saved";

	private final static String TASKLIST_SETTING = "Import TaskList setting";

	private final static String ACTIVATION_HISTORY_SETTING = "Import Activation history setting";

	private final static String CONTEXTS_SETTING = "Import Contexts setting";

	private final static String SOURCE_DIR_SETTING = "Import Source directory setting";

	private final static String SOURCE_ZIP_SETTING = "Import Source zip file setting";

	private final static String OVERWRITE_SETTING = "Import Overwrite setting";

	private final static String IMPORT_METHOD_SETTING = "Import method setting";

	public TaskDataImportWizardPage() {
		super("org.eclipse.mylar.tasklist.importPage", PAGE_TITLE, MylarTaskListPlugin.imageDescriptorFromPlugin(
				MylarTaskListPlugin.PLUGIN_ID, "icons/wizban/banner-import.gif"));
		setPageComplete(false);
		setDescription(DESCRIPTION);
	}

	public String getName() {
		return PAGE_NAME;
	}

	public void createControl(Composite parent) {
		try {
			Composite container = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1, false);
			container.setLayout(layout);

//			Label warningLabel = new Label(container, SWT.NONE);
//			warningLabel.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED));
//			warningLabel.setText(DESCRIPTION); 

			createFileSelectionControl(container);
			createImportDirectoryControl(container);
			createImportFromZipControl(container);

			overwriteCheckBox = createCheckBox(container, "Overwrite existing files without warning");

			initSettings();

			setControl(container);

			setPageComplete(validate());
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "Could not create import wizard page", true);
		}
	}

	/**
	 * Create widgets for selecting the data files to import
	 */
	private void createFileSelectionControl(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		GridLayout gl = new GridLayout(1, false);
		group.setLayout(gl);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gridData);
		group.setText("Select data to import:");

		taskListCheckBox = createCheckBox(group, "Task List");
		taskActivationHistoryCheckBox = createCheckBox(group, "Task Activation History");
		taskContextsCheckBox = createCheckBox(group, "Task Contexts");

		importViaFolderButton = new Button(group, SWT.RADIO);
		importViaFolderButton.setText("Import task data from .mylar folder");
		importViaZipButton = new Button(group, SWT.RADIO);
		importViaZipButton.setText("Import task data from zip file");

		SelectionListener radioListener = new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				importFromFolderGroup.setEnabled(importViaFolderButton.getSelection());
				browseButton.setEnabled(importViaFolderButton.getSelection());
				importFromZipGroup.setEnabled(importViaZipButton.getSelection());
				browseButtonZip.setEnabled(importViaZipButton.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore

			}
		};

		importViaFolderButton.addSelectionListener(radioListener);
		importViaZipButton.addSelectionListener(radioListener);
	}

	/**
	 * Create widgets for specifying the source directory
	 */
	private void createImportDirectoryControl(Composite parent) {
		importFromFolderGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		importFromFolderGroup.setText("Import from folder");
		importFromFolderGroup.setLayout(new GridLayout(2, false));
		importFromFolderGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		sourceDirText = new Text(importFromFolderGroup, SWT.BORDER);
		sourceDirText.setEditable(false);
		sourceDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sourceDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged();
			}
		});

		browseButton = new Button(importFromFolderGroup, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Folder Selection");
				dialog.setMessage("Specify the source folder for task data");
				String dir = sourceDirText.getText();
				dialog.setFilterPath(dir);
				dir = dialog.open();
				if (dir == null || dir.equals(""))
					return;
				sourceDirText.setText(dir);
			}
		});
	}

	/**
	 * Create widgets for specifying the source zip
	 */
	private void createImportFromZipControl(Composite parent) {
		importFromZipGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		importFromZipGroup.setText("Import from zip file");
		importFromZipGroup.setLayout(new GridLayout(2, false));
		importFromZipGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		sourceZipText = new Text(importFromZipGroup, SWT.BORDER);
		sourceZipText.setEditable(false);
		sourceZipText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sourceZipText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged();
			}
		});

		browseButtonZip = new Button(importFromZipGroup, SWT.PUSH);
		browseButtonZip.setText("Browse...");
		browseButtonZip.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setText("Zip File Selection");
				// dialog.setText("Specify the source zip file for task data");
				String dir = sourceZipText.getText();
				dialog.setFilterPath(dir);
				dir = dialog.open();
				if (dir == null || dir.equals(""))
					return;
				sourceZipText.setText(dir);
			}
		});

		importFromZipGroup.setEnabled(false);
		browseButtonZip.setEnabled(false);

	}

	/**
	 * Initializes controls with values from the Dialog Settings object
	 */
	protected void initSettings() {
		IDialogSettings settings = getDialogSettings();

		if (settings.get(SETTINGS_SAVED) == null) {
			// Set default values
			taskListCheckBox.setSelection(true);
			taskActivationHistoryCheckBox.setSelection(true);
			taskContextsCheckBox.setSelection(true);
			sourceDirText.setText("");
			overwriteCheckBox.setSelection(true);
			importFromFolderGroup.setEnabled(true);
			importViaFolderButton.setSelection(true);

		} else {
			// Retrieve previous values from the dialog settings
			taskListCheckBox.setSelection(settings.getBoolean(TASKLIST_SETTING));
			taskActivationHistoryCheckBox.setSelection(settings.getBoolean(ACTIVATION_HISTORY_SETTING));
			taskContextsCheckBox.setSelection(settings.getBoolean(CONTEXTS_SETTING));
			importViaFolderButton.setSelection(settings.getBoolean(IMPORT_METHOD_SETTING));
			importViaZipButton.setSelection(!importViaFolderButton.getSelection());

			importFromFolderGroup.setEnabled(importViaFolderButton.getSelection());
			importFromZipGroup.setEnabled(importViaZipButton.getSelection());
			browseButton.setEnabled(importFromFolderGroup.isEnabled());
			browseButtonZip.setEnabled(importFromZipGroup.isEnabled());

			String directory = settings.get(SOURCE_DIR_SETTING);
			if (directory != null) {
				sourceDirText.setText(settings.get(SOURCE_DIR_SETTING));
			}
			String zipFile = settings.get(SOURCE_ZIP_SETTING);
			if (zipFile != null) {
				sourceZipText.setText(settings.get(SOURCE_ZIP_SETTING));
			}
			overwriteCheckBox.setSelection(settings.getBoolean(OVERWRITE_SETTING));
		}
	}

	/**
	 * Saves the control values in the dialog settings to be used as defaults
	 * the next time the page is opened
	 */
	public void saveSettings() {
		IDialogSettings settings = getDialogSettings();

		settings.put(TASKLIST_SETTING, taskListCheckBox.getSelection());
		settings.put(ACTIVATION_HISTORY_SETTING, taskActivationHistoryCheckBox.getSelection());
		settings.put(CONTEXTS_SETTING, taskContextsCheckBox.getSelection());
		settings.put(SOURCE_DIR_SETTING, sourceDirText.getText());
		settings.put(SOURCE_ZIP_SETTING, sourceZipText.getText());
		settings.put(OVERWRITE_SETTING, overwriteCheckBox.getSelection());
		settings.put(IMPORT_METHOD_SETTING, importViaFolderButton.getSelection());

		settings.put(SETTINGS_SAVED, SETTINGS_SAVED);
	}

	/** Convenience method for creating a new checkbox */
	protected Button createCheckBox(Composite parent, String text) {
		Button newButton = new Button(parent, SWT.CHECK);
		newButton.setText(text);

		newButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				controlChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// No action required
			}
		});

		return newButton;
	}

	/** Called to indicate that a control's value has changed */
	public void controlChanged() {
		setPageComplete(validate());
	}

	/** Returns true if the information entered by the user is valid */
	protected boolean validate() {

		// Check that at least one type of data has been selected
		if (!taskListCheckBox.getSelection() && !taskActivationHistoryCheckBox.getSelection()
				&& !taskContextsCheckBox.getSelection()) {
			return false;
		}

		// Check that a destination dir has been specified
		if (sourceDirText.getText().equals("") && sourceZipText.getText().equals("")) {
			return false;
		}

		return true;
	}

	/** Returns the directory where data files are to be restored from */
	public String getSourceDirectory() {
		return sourceDirText.getText();
	}

	public String getSourceZipFile() {
		return sourceZipText.getText();
	}

	/** True if the user wants to import the task list */
	public boolean importTaskList() {
		return taskListCheckBox.getSelection();
	}

	/** True if the user wants to import task activation history */
	public boolean importActivationHistory() {
		return taskActivationHistoryCheckBox.getSelection();
	}

	/** True if the user wants to import task context files */
	public boolean importTaskContexts() {
		return taskContextsCheckBox.getSelection();
	}

	/** True if the user wants to overwrite files by default */
	public boolean overwrite() {
		return overwriteCheckBox.getSelection();
	}

	/** True if the user wants to import from a zip file */
	public boolean zip() {
		return importViaZipButton.getSelection();
	}

	/** For testing only. Sets controls to the specified values */
	public void setParameters(boolean overwrite, boolean importTaskList, boolean importActivationHistory,
			boolean importTaskContexts, boolean zip, String sourceDir, String sourceZip) {
		overwriteCheckBox.setSelection(overwrite);
		taskListCheckBox.setSelection(importTaskList);
		taskActivationHistoryCheckBox.setSelection(importActivationHistory);
		taskContextsCheckBox.setSelection(importTaskContexts);
		sourceDirText.setText(sourceDir);
		sourceZipText.setText(sourceZip);
		importViaZipButton.setSelection(zip);
	}
}
