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
package org.eclipse.mylar.internal.tasklist.ui.preferences;

import java.text.DateFormat;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.provisional.core.MylarPlugin;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 * @author Rob Elves
 */
public class MylarTaskListPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String FOLDER_SELECTION_MESSAGE = "Specify the folder for tasks";

	private static final String TITLE_FOLDER_SELECTION = "Folder Selection";

	private static final String FORWARDSLASH = "/";

	private static final String BACKSLASH_MULTI = "\\\\";

	private static final int SPINNER_MAX_BACKUPS = 100;

	private static final int SPINNER_MIN_BACKUPS = 1;

	// private static final String LABEL_BACKUP_ERROR = "Backup Error";

	private static final String GROUP_LABEL_BACKUP = "Backup";

	private static final String LABEL_LAST_ARCHIVED_NEVER = "never";

	private static final String LAST_BACKUP_ON_LABEL = "   Last backup: ";

	private Text taskDirectoryText = null;

//	private Text taskURLPrefixText = null;

	private Button browse = null;

	private Button backupNow = null;

	private Button notificationEnabledButton = null;

	private Button backupAutomaticallyButton;

	private Text backupScheduleTimeText;

	private Text backupFolderText;

	private Label lastUpdate;

	private Spinner maxFilesSpinner;

	public MylarTaskListPreferencePage() {
		super();
		setPreferenceStore(MylarTaskListPlugin.getMylarCorePrefs());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		String message = "See <a>''{0}''</a> for configuring Mylar colors.";
		new PreferenceLinkArea(container, SWT.NONE, "org.eclipse.ui.preferencePages.ColorsAndFonts", message,
				(IWorkbenchPreferenceContainer) getContainer(), null);

//		createCreationGroup(container);
		// createTaskActivityGroup(container);
		createNotificationsGroup(container);
		createTaskBackupScheduleGroup(container);
		createTaskDirectoryControl(container);
		updateRefreshGroupEnablements();
		return container;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean performOk() {
		String taskDirectory = taskDirectoryText.getText();
		taskDirectory = taskDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
		if (!taskDirectory.equals(MylarPlugin.getDefault().getDataDirectory())) {
			// Order matters:
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
			// if (copyExistingDataCheckbox.getSelection()) {
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().copyDataDirContentsTo(taskDirectory);
			// }
			MylarPlugin.getDefault().setDataDirectory(taskDirectory);
		}

//		getPreferenceStore().setValue(TaskListPreferenceConstants.DEFAULT_URL_PREFIX, taskURLPrefixText.getText());

		getPreferenceStore().setValue(TaskListPreferenceConstants.NOTIFICATIONS_ENABLED,
				notificationEnabledButton.getSelection());

		getPreferenceStore().setValue(TaskListPreferenceConstants.BACKUP_AUTOMATICALLY,
				backupAutomaticallyButton.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.BACKUP_SCHEDULE, backupScheduleTimeText.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.BACKUP_FOLDER, backupFolderText.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.BACKUP_MAXFILES, maxFilesSpinner.getSelection());
		return true;
	}

	@Override
	public boolean performCancel() {

		taskDirectoryText.setText(MylarPlugin.getDefault().getDefaultDataDirectory());
//		taskURLPrefixText.setText(getPreferenceStore().getString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX));

		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));

		backupAutomaticallyButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.BACKUP_AUTOMATICALLY));
		backupScheduleTimeText.setText(getPreferenceStore().getString(TaskListPreferenceConstants.BACKUP_SCHEDULE));
		backupFolderText.setText(getPreferenceStore().getString(TaskListPreferenceConstants.BACKUP_FOLDER));
		maxFilesSpinner.setSelection(getPreferenceStore().getInt(TaskListPreferenceConstants.BACKUP_MAXFILES));
		return true;
	}

	public void performDefaults() {
		super.performDefaults();

		taskDirectoryText.setText(MylarPlugin.getDefault().getDefaultDataDirectory());

//		taskURLPrefixText.setText(getPreferenceStore().getDefaultString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX));

		notificationEnabledButton.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));

		backupAutomaticallyButton.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.BACKUP_AUTOMATICALLY));
		backupScheduleTimeText.setText(getPreferenceStore().getDefaultString(
				TaskListPreferenceConstants.BACKUP_SCHEDULE));
		backupFolderText.setText(getPreferenceStore().getDefaultString(TaskListPreferenceConstants.BACKUP_FOLDER));
		maxFilesSpinner.setSelection(getPreferenceStore().getDefaultInt(TaskListPreferenceConstants.BACKUP_MAXFILES));
		updateRefreshGroupEnablements();
	}

	private void createTaskBackupScheduleGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(GROUP_LABEL_BACKUP);
		GridLayout groupLayout = new GridLayout(1, false);
		groupLayout.verticalSpacing = 0;
		groupLayout.marginLeft = 0;
		groupLayout.marginHeight = 0;
		group.setLayout(groupLayout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
		Composite topGroup = new Composite(group, SWT.NULL);
		topGroup.setLayout(new GridLayout(6, false));
		topGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite backupTop = new Composite(topGroup, SWT.NONE);
		backupTop.setLayout(new GridLayout(3, false));
		GridData archiveData = new GridData();
//		archiveData.horizontalSpan = 3;
		backupTop.setLayoutData(archiveData);

		backupAutomaticallyButton = new Button(backupTop, SWT.CHECK);
		backupAutomaticallyButton.setText("Backup every");
		backupAutomaticallyButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.BACKUP_AUTOMATICALLY));
		backupAutomaticallyButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		{
			backupScheduleTimeText = new Text(backupTop, SWT.BORDER | SWT.RIGHT);
			final GridData gridData_1 = new GridData();
			gridData_1.widthHint = 13;
			backupScheduleTimeText.setLayoutData(gridData_1);

			backupScheduleTimeText.setText(""
					+ getPreferenceStore().getInt(TaskListPreferenceConstants.BACKUP_SCHEDULE));
			backupScheduleTimeText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					updateRefreshGroupEnablements();
				}
			});

		}

		Label label = new Label(backupTop, SWT.NONE);
		label.setText("days to");

//		label = new Label(topGroup, SWT.LEFT);
//		label.setText("to");

		String backupDirectory = getPreferenceStore().getString(TaskListPreferenceConstants.BACKUP_FOLDER);
		backupDirectory = backupDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
		backupFolderText = new Text(topGroup, SWT.BORDER);

		backupFolderText.setText(backupDirectory);
		backupFolderText.setEditable(false);
		backupFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browse = new Button(topGroup, SWT.TRAIL);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(TITLE_FOLDER_SELECTION);
				dialog.setMessage("Specify the backup output folder");
				String dir = backupFolderText.getText();
				dir = dir.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
				dialog.setFilterPath(dir);
				dir = dialog.open();
				if (dir == null || dir.equals(""))
					return;
				backupFolderText.setText(dir);
				updateRefreshGroupEnablements();
			}
		});

		Composite extrasComp = new Composite(group, SWT.NONE);
		extrasComp.setLayout(new GridLayout(4, false));
		GridData extrasGD = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		extrasGD.horizontalSpan = 3;
		extrasComp.setLayoutData(extrasGD);

		final Label maxFiles = new Label(extrasComp, SWT.NONE);
		maxFiles.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		maxFiles.setText(" Files to keep: ");
		
		maxFilesSpinner = new Spinner(extrasComp, SWT.NONE);
		maxFilesSpinner.setIncrement(1);
		maxFilesSpinner.setMinimum(SPINNER_MIN_BACKUPS);
		maxFilesSpinner.setMaximum(SPINNER_MAX_BACKUPS);
		maxFilesSpinner.setPageIncrement(1);
		maxFilesSpinner.setSelection(getPreferenceStore().getInt(TaskListPreferenceConstants.BACKUP_MAXFILES));
		// 3.2 only: 
		//		maxFilesSpinner.setValues(getPreferenceStore().getInt(TaskListPreferenceConstants.BACKUP_MAXFILES),
		//				SPINNER_MIN_BACKUPS, SPINNER_MAX_BACKUPS, 0, 1, 1);
		
		lastUpdate = new Label(extrasComp, SWT.NONE);
		lastUpdate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setLastBackup();

		backupNow = new Button(extrasComp, SWT.NONE);
		backupNow.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		backupNow.setText("Backup Now");
		backupNow.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// try {
				getPreferenceStore().setValue(TaskListPreferenceConstants.BACKUP_FOLDER, backupFolderText.getText());
				MylarTaskListPlugin.getDefault().getBackupManager().backupNow(true);
				setLastBackup();
				// } catch (InvocationTargetException ex) {
				// MessageDialog.openError(getShell(), LABEL_BACKUP_ERROR,
				// TaskListBackupManager.BACKUP_FAILURE_MESSAGE +
				// ex.getCause().getMessage());
				// } catch (IOException ex) {
				// MessageDialog.openError(getShell(), LABEL_BACKUP_ERROR,
				// TaskListBackupManager.BACKUP_FAILURE_MESSAGE +
				// ex.getCause().getMessage());
				// }
			}
		});

	}

	private void setLastBackup() {
		long lastExported = getPreferenceStore().getLong(TaskListPreferenceConstants.BACKUP_LAST);
		String dateText = "";
		if (lastExported > 0) {
			dateText = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastExported);
		} else {
			dateText = LABEL_LAST_ARCHIVED_NEVER;
		}
		lastUpdate.setText(LAST_BACKUP_ON_LABEL + dateText);
	}

	private void createTaskDirectoryControl(Composite parent) {
		Group taskDirComposite = new Group(parent, SWT.SHADOW_ETCHED_IN);
		taskDirComposite.setText("Data directory (moves with workspace if default)");
		taskDirComposite.setLayout(new GridLayout(2, false));
		taskDirComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		String taskDirectory = MylarPlugin.getDefault().getDataDirectory();
		// String taskDirectory =
		// getPreferenceStore().getString(MylarPlugin.PREF_DATA_DIR);
		taskDirectory = taskDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
		taskDirectoryText = new Text(taskDirComposite, SWT.BORDER);
		taskDirectoryText.setText(taskDirectory);
		taskDirectoryText.setEditable(false);
		taskDirectoryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browse = new Button(taskDirComposite, SWT.TRAIL);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(TITLE_FOLDER_SELECTION);
				dialog.setMessage(FOLDER_SELECTION_MESSAGE);
				String dir = taskDirectoryText.getText();
				dir = dir.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
				dialog.setFilterPath(dir);

				dir = dialog.open();
				if (dir == null || dir.equals(""))
					return;
				taskDirectoryText.setText(dir);
			}
		});

//		Label noteLabel = new Label(taskDirComposite, SWT.NULL);
//		noteLabel.setText("Note: moves with workspace if default");
				
		// copyExistingDataCheckbox = new Button(taskDirComposite, SWT.CHECK);
		// copyExistingDataCheckbox.setText("Copy existing data to new
		// location");
		// copyExistingDataCheckbox.setSelection(getPreferenceStore()
		// .getBoolean(TaskListPreferenceConstants.COPY_TASK_DATA));

	}

	// private void createTaskActivityGroup(Composite parent) {
	// Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	// group.setText("Work Schedule");
	// group.setLayout(new GridLayout(2, false));
	// group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	//
	// Label workLabel = new Label(group, SWT.NONE);
	// workLabel.setText("Your week starts on");
	// Combo workWeekBegin = new Combo(group, SWT.READ_ONLY);
	// workWeekBegin.add("MONDAY");//, Calendar.MONDAY
	// workWeekBegin.add("TUESDAY");//, Calendar.TUESDAY
	// workWeekBegin.add("WEDNESDAY");//, Calendar.WEDNESDAY
	// workWeekBegin.add("THURSDAY");//, Calendar.MONDAY
	// workWeekBegin.add("FRIDAY");//, Calendar.TUESDAY
	// workWeekBegin.add("SATURDAY");//, Calendar.WEDNESDAY
	// workWeekBegin.add("SUNDAY");//, Calendar.WEDNESDAY
	//		
	// Label startHourLabel = new Label(group, SWT.NONE);
	// startHourLabel.setText("Your day begins at (24hr)");
	//		
	// Spinner startHour = new Spinner(group, SWT.NULL | SWT.BORDER);
	// startHour.setSelection(1);
	// startHour.setDigits(0);
	// startHour.setMaximum(24);
	// startHour.setMinimum(0);
	// startHour.setIncrement(1);
	// // startHour.addModifyListener(new ModifyListener() {
	// // public void modifyText(ModifyEvent e) {
	// // // do something
	// // }
	// // });
	// }

//	private void createCreationGroup(Composite parent) {
//		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
//		group.setText("Task Creation");
//		group.setLayout(new GridLayout(1, false));
//		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		Label urlLabel = new Label(group, SWT.LEFT);
//		urlLabel.setText("Web link prefix (e.g. https://bugs.eclipse.org/bugs/show_bug.cgi?id=)");
//		GridData data = new GridData();
//		data.horizontalSpan = 2;
//		data.horizontalAlignment = GridData.BEGINNING;
//		urlLabel.setLayoutData(data);
//		urlLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

//		String taskURLPrefix = getPreferenceStore().getString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX);
//		taskURLPrefixText = new Text(group, SWT.BORDER);
//		taskURLPrefixText.setText(taskURLPrefix);
//		taskURLPrefixText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//	}

	private void createNotificationsGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Notifications");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		notificationEnabledButton = new Button(group, SWT.CHECK);
		notificationEnabledButton.setText("Reminder popups enabled");
		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));

		// final Label morningNotificationLabel = new Label(parent, SWT.NONE);
		// morningNotificationLabel.setText("Start hour of Day (0-24):");
		//
		// morningNotificationHour = new Text(parent, SWT.BORDER | SWT.RIGHT);
		// final GridData notificationGridData = new GridData();
		// notificationGridData.widthHint = 35;
		// morningNotificationHour.setLayoutData(notificationGridData);

		// Label notificationEnabledLabel = createLabel(group, "Notifications
		// enabled: ");
		// notificationEnabledLabel.setLayoutData(new
		// GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		//		
		// String taskURLPrefix =
		// getPreferenceStore().getString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX);
		// taskURLPrefixText = new Text(group, SWT.BORDER);
		// taskURLPrefixText.setText(taskURLPrefix);
		// taskURLPrefixText.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
	}

	public void updateRefreshGroupEnablements() {
		if (backupAutomaticallyButton.getSelection()) {
			try {
				long number = Integer.parseInt(backupScheduleTimeText.getText());
				if (number <= 0) {
					this.setErrorMessage("Archive schedule time must be > 0");
					this.setValid(false);
				} else if (backupFolderText.getText() == "") {
					this.setErrorMessage("Archive destination folder must be specified");
					this.setValid(false);
				} else {
					this.setErrorMessage(null);
					this.setValid(true);
				}
			} catch (NumberFormatException e) {
				this.setErrorMessage("Archive schedule time must be valid integer");
				this.setValid(false);
			}
		} else {
			this.setValid(true);
			this.setErrorMessage(null);
		}
		backupScheduleTimeText.setEnabled(backupAutomaticallyButton.getSelection());
		backupNow.setEnabled(backupFolderText.getText() != "");
	}

}
