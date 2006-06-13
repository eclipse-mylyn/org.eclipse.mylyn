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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String FOLDER_SELECTION_MESSAGE = "Specify the folder for tasks";

	private static final String TITLE_FOLDER_SELECTION = "Folder Selection";

	private static final String FORWARDSLASH = "/";

	private static final String BACKSLASH_MULTI = "\\\\";

	private Button reportEditor;

	private Button disableInternal;

	private Button reportInternal;

	private Text synchScheduleTime = null;

	private Button enableBackgroundSynch;

	private Button synchQueries = null;

	private Text taskDirectoryText = null;

	private Button browse = null;

	private Button backupNow = null;

	private Button notificationEnabledButton = null;

	private Text backupScheduleTimeText;

	private Text backupFolderText;

	public TaskListPreferencePage() {
		super();
		setPreferenceStore(MylarTaskListPlugin.getMylarCorePrefs());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		String message = "See <a>''{0}''</a> for configuring Task List colors.";
		new PreferenceLinkArea(container, SWT.NONE, "org.eclipse.ui.preferencePages.ColorsAndFonts", message,
				(IWorkbenchPreferenceContainer) getContainer(), null);
		
		createOpenWith(container);
		createTaskRefreshScheduleGroup(container);
		createTaskDataControl(container);
		createNotificationsGroup(container);
		
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
			// NOTE: order matters
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().copyDataDirContentsTo(taskDirectory);
			MylarPlugin.getDefault().setDataDirectory(taskDirectory);
		}
		getPreferenceStore().setValue(TaskListPreferenceConstants.NOTIFICATIONS_ENABLED,
				notificationEnabledButton.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.BACKUP_SCHEDULE, backupScheduleTimeText.getText());

		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL,
				disableInternal.getSelection());

		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP,
				synchQueries.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED,
				enableBackgroundSynch.getSelection());
		long miliseconds = 60000 * Long.parseLong(synchScheduleTime.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS,
				"" + miliseconds);
		return true;
	}

	@Override
	public boolean performCancel() {
		taskDirectoryText.setText(MylarPlugin.getDefault().getDefaultDataDirectory());
		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
		backupScheduleTimeText.setText(getPreferenceStore().getString(TaskListPreferenceConstants.BACKUP_SCHEDULE));
		backupFolderText.setText(MylarTaskListPlugin.getDefault().getBackupFolderPath());

		reportEditor.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		disableInternal.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL));
		synchQueries.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		enableBackgroundSynch.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		synchScheduleTime.setText(getMinutesString());
		return true;
	}

	public void performDefaults() {
		super.performDefaults();

		taskDirectoryText.setText(MylarPlugin.getDefault().getDefaultDataDirectory());
		notificationEnabledButton.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
		backupScheduleTimeText.setText(getPreferenceStore().getDefaultString(
				TaskListPreferenceConstants.BACKUP_SCHEDULE));
		backupFolderText.setText(MylarTaskListPlugin.getDefault().getBackupFolderPath());

		reportEditor.setSelection(getPreferenceStore()
				.getDefaultBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL));

		synchQueries.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		enableBackgroundSynch.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
//		userRefreshOnly.setSelection(!enableBackgroundSynch.getSelection());
		long miliseconds = getPreferenceStore().getDefaultLong(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		synchScheduleTime.setText("" + minutes);

		updateRefreshGroupEnablements();
	}

	private void createTaskRefreshScheduleGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Repository Synchronization");
		GridLayout gridLayout = new GridLayout(1, false);
//		gridLayout.marginLeft = 0;
		group.setLayout(gridLayout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		userRefreshOnly = new Button(group, SWT.RADIO);
//		GridData gridData = new GridData();
//		gridData.horizontalSpan = 2;
//		userRefreshOnly.setLayoutData(gridData);
//		userRefreshOnly.setText("Disabled");
//		userRefreshOnly.setSelection(!getPreferenceStore().getBoolean(
//				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
//		userRefreshOnly.addSelectionListener(new SelectionListener() {
//			public void widgetSelected(SelectionEvent e) {
//				updateRefreshGroupEnablements();
//			}
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//			}
//		});
		Composite enableSynch = new Composite(group, SWT.NULL);
		gridLayout = new GridLayout(4, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		enableSynch.setLayout(gridLayout);
		enableBackgroundSynch = new Button(enableSynch, SWT.CHECK);
//		enableBackgroundSynch.setLayoutData(gridData);
		enableBackgroundSynch.setText("Synchronize every");
		enableBackgroundSynch.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		enableBackgroundSynch.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		synchScheduleTime = new Text(enableSynch, SWT.BORDER | SWT.RIGHT);
		GridData gridData = new GridData();
		gridData.widthHint = 20;
		synchScheduleTime.setLayoutData(gridData);
		synchScheduleTime.setText(getMinutesString());
		synchScheduleTime.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateRefreshGroupEnablements();
			}
		});
		Label label = new Label(enableSynch, SWT.NONE);
		label.setText("minutes");

		synchQueries = new Button(group, SWT.CHECK);
		synchQueries.setText("Synchronize on startup");
		synchQueries.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));

	}

	private void createOpenWith(Composite parent) {
		Group container = new Group(parent, SWT.SHADOW_ETCHED_IN);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		container.setText("Open Repository Tasks with");
		reportEditor = new Button(container, SWT.RADIO);
		reportEditor.setText("Editor if available (Recommended)");
		reportEditor.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal = new Button(container, SWT.RADIO);
		reportInternal.setText("Internal browser");
		reportInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		disableInternal = new Button(container, SWT.CHECK);
		disableInternal.setText("Disable internal browser");
		disableInternal.setEnabled(!reportInternal.getSelection());
		disableInternal.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL));
		reportInternal.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (reportInternal.getSelection()) {
					disableInternal.setSelection(false);
					disableInternal.setEnabled(false);
				} else {
					disableInternal.setEnabled(true);
				}
			}

		});
	}

	private void createTaskDataControl(Composite parent) {
		Group taskDataGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		taskDataGroup.setText("Task Data");
		taskDataGroup.setLayout(new GridLayout(1, false));
		taskDataGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(taskDataGroup, SWT.NULL);
		label.setText("Default data directory will move with workspace (Recommended)");

		Composite dataDirComposite = new Composite(taskDataGroup, SWT.NULL);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		dataDirComposite.setLayout(gridLayout);
		dataDirComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(dataDirComposite, SWT.NULL);
		label.setText("Data directory: ");

		String taskDirectory = MylarPlugin.getDefault().getDataDirectory();
		taskDirectory = taskDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
		taskDirectoryText = new Text(dataDirComposite, SWT.BORDER);
		taskDirectoryText.setText(taskDirectory);
		taskDirectoryText.setEditable(false);
		taskDirectoryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browse = new Button(dataDirComposite, SWT.TRAIL);
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

		Composite backupComposite = new Composite(taskDataGroup, SWT.NULL);
		gridLayout = new GridLayout(5, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		backupComposite.setLayout(gridLayout);
		backupComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(backupComposite, SWT.NULL);
		label.setText("Backup every");
		backupScheduleTimeText = new Text(backupComposite, SWT.BORDER | SWT.RIGHT);
		final GridData gridData_1 = new GridData();
		gridData_1.widthHint = 13;
		backupScheduleTimeText.setLayoutData(gridData_1);

		backupScheduleTimeText.setText("" + getPreferenceStore().getInt(TaskListPreferenceConstants.BACKUP_SCHEDULE));
		backupScheduleTimeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateRefreshGroupEnablements();
			}
		});

		label = new Label(backupComposite, SWT.NONE);
		label.setText("days to");

		String backupDirectory = MylarTaskListPlugin.getDefault().getBackupFolderPath();// getPreferenceStore().getString(TaskListPreferenceConstants.BACKUP_FOLDER);
		backupDirectory = backupDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
		backupFolderText = new Text(backupComposite, SWT.BORDER);
		backupFolderText.setText(backupDirectory);
		backupFolderText.setEditable(false);
		backupFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		backupNow = new Button(backupComposite, SWT.NONE);
		backupNow.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		backupNow.setText("Backup Now");
		backupNow.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				MylarTaskListPlugin.getDefault().getBackupManager().backupNow(true);
			}
		});
	}

	private void createNotificationsGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Notifications");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		notificationEnabledButton = new Button(group, SWT.CHECK);
		notificationEnabledButton.setText("Reminder popups enabled");
		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
	}

	public void updateRefreshGroupEnablements() {
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

		if (enableBackgroundSynch.getSelection()) {
			try {
				long number = Long.parseLong(synchScheduleTime.getText());
				if (number <= 0) {
					this.setErrorMessage("Refresh schedule time must be > 0");
					this.setValid(false);
				} else {
					this.setErrorMessage(null);
					this.setValid(true);
				}
			} catch (NumberFormatException e) {
				this.setErrorMessage("Refresh schedule time must be valid integer");
				this.setValid(false);
			}
		} else {
			this.setValid(true);
			this.setErrorMessage(null);
		}
		synchScheduleTime.setEnabled(enableBackgroundSynch.getSelection());
	}

	private String getMinutesString() {
		long miliseconds = getPreferenceStore().getLong(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		return "" + minutes;
	}

}
