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
package org.eclipse.mylar.internal.tasks.ui.preferences;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TasksPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "org.eclipse.mylar.tasks.ui.preferences";
	
	private static final int OVERWRITE = 0;

	private static final int LOAD_EXISTING = 1;

	private static final int CANCEL = 2;

	private static final String FOLDER_SELECTION_MESSAGE = "Specify the folder for tasks";

	private static final String TITLE_FOLDER_SELECTION = "Folder Selection";

	private static final String END_HOUR_LABEL = "Work day end (24hr): ";

	private static final String START_HOUR_LABEL = "Work day start (24hr): ";

	private static final String GROUP_WORK_WEEK_LABEL = "Scheduling";

	private static final String FORWARDSLASH = "/";

	private static final String BACKSLASH_MULTI = "\\\\";

	private Button reportEditor;

	private Button disableInternal;

	private Button activateOnOpen;

	private Button reportInternal;

	private Text synchScheduleTime = null;

	private Button enableBackgroundSynch;

	private Text taskDirectoryText = null;

	private Button browse = null;

	private Button backupNow = null;

	private Button notificationEnabledButton = null;

	private Button incomingOverlaysButton = null;
	
	private Text backupScheduleTimeText;

	private Text backupFolderText;

	private Spinner hourDayStart;

	private Spinner hourDayEnd;

	private int taskDataDirectoryAction = -1;

	public TasksPreferencePage() {
		super();
		setPreferenceStore(TasksUiPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		
		if (getContainer() instanceof IWorkbenchPreferenceContainer) {
			String message = "See <a>''{0}''</a> for configuring Task List colors.";
				new PreferenceLinkArea(container, SWT.NONE, "org.eclipse.ui.preferencePages.ColorsAndFonts", 
						message, (IWorkbenchPreferenceContainer) getContainer(), null);
		}
		
		createTaskRefreshScheduleGroup(container);
		createNotificationsGroup(container);
		createSchedulingGroup(container);
		createOpenWith(container);
		createTaskDataControl(container);

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

		if (!taskDirectory.equals(TasksUiPlugin.getDefault().getDataDirectory())) {
			if (taskDataDirectoryAction == OVERWRITE) {
//				TasksUiPlugin.getTaskListManager().saveTaskList();
				TasksUiPlugin.getTaskListManager().copyDataDirContentsTo(taskDirectory);
				TasksUiPlugin.getDefault().setDataDirectory(taskDirectory);
			} else if (taskDataDirectoryAction == LOAD_EXISTING) {
				TasksUiPlugin.getDefault().setDataDirectory(taskDirectory);
			} else if (taskDataDirectoryAction == CANCEL) {
				// shouldn't get here
			}
		}
		getPreferenceStore().setValue(TaskListPreferenceConstants.NOTIFICATIONS_ENABLED,
				notificationEnabledButton.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.BACKUP_SCHEDULE, backupScheduleTimeText.getText());

		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL,
				disableInternal.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.ACTIVATE_ON_OPEN, activateOnOpen.getSelection());

		// getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP,
		// synchQueries.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED,
				enableBackgroundSynch.getSelection());
		long miliseconds = 60000 * Long.parseLong(synchScheduleTime.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS,
				"" + miliseconds);

		getPreferenceStore().setValue(TaskListPreferenceConstants.PLANNING_STARTHOUR, hourDayStart.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.PLANNING_ENDHOUR, hourDayEnd.getSelection());
		backupNow.setEnabled(true);
		
		getPreferenceStore().setValue(TaskListPreferenceConstants.INCOMING_OVERLAID,
				incomingOverlaysButton.getSelection());
		TaskListView view = TaskListView.getFromActivePerspective();
		if (view != null) {
			view.setSynchronizationOverlaid(incomingOverlaysButton.getSelection());
		}
		
		return true;
	}

	@Override
	public boolean performCancel() {
		taskDirectoryText.setText(TasksUiPlugin.getDefault().getDefaultDataDirectory());
		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
		backupScheduleTimeText.setText(getPreferenceStore().getString(TaskListPreferenceConstants.BACKUP_SCHEDULE));
		backupFolderText.setText(TasksUiPlugin.getDefault().getBackupFolderPath());

		reportEditor.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		disableInternal.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL));
		activateOnOpen.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.ACTIVATE_ON_OPEN));
		// synchQueries.setSelection(getPreferenceStore().getBoolean(
		// TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		enableBackgroundSynch.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		synchScheduleTime.setText(getMinutesString());

		hourDayStart.setSelection(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_STARTHOUR));
		hourDayEnd.setSelection(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_ENDHOUR));
		backupNow.setEnabled(true);
		return true;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		String taskDirectory = TasksUiPlugin.getDefault().getDefaultDataDirectory();				
		if (!taskDirectory.equals(TasksUiPlugin.getDefault().getDataDirectory())) {
			checkForExistingTasklist(taskDirectory);
			if (taskDataDirectoryAction != CANCEL) {
				taskDirectoryText.setText(taskDirectory);				
				backupFolderText.setText(taskDirectory + FORWARDSLASH
						+ ITasksUiConstants.DEFAULT_BACKUP_FOLDER_NAME);
				backupNow.setEnabled(false);
			}
		} else {
			taskDirectoryText.setText(taskDirectory);				
			backupFolderText.setText(taskDirectory + FORWARDSLASH
					+ ITasksUiConstants.DEFAULT_BACKUP_FOLDER_NAME);
			backupNow.setEnabled(true);
		}

		notificationEnabledButton.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
		backupScheduleTimeText.setText(getPreferenceStore().getDefaultString(
				TaskListPreferenceConstants.BACKUP_SCHEDULE));		
		

		reportEditor.setSelection(getPreferenceStore()
				.getDefaultBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL));

		// synchQueries.setSelection(getPreferenceStore().getDefaultBoolean(
		// TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		enableBackgroundSynch.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		// userRefreshOnly.setSelection(!enableBackgroundSynch.getSelection());
		long miliseconds = getPreferenceStore().getDefaultLong(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		synchScheduleTime.setText("" + minutes);

		hourDayStart.setSelection(getPreferenceStore().getDefaultInt(TaskListPreferenceConstants.PLANNING_STARTHOUR));
		hourDayEnd.setSelection(getPreferenceStore().getDefaultInt(TaskListPreferenceConstants.PLANNING_ENDHOUR));
		updateRefreshGroupEnablements();
	}

	private void createTaskRefreshScheduleGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Synchronization");
		GridLayout gridLayout = new GridLayout(1, false);
		group.setLayout(gridLayout);
		
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite enableSynch = new Composite(group, SWT.NULL);
		gridLayout = new GridLayout(4, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		enableSynch.setLayout(gridLayout);
		enableBackgroundSynch = new Button(enableSynch, SWT.CHECK);
		enableBackgroundSynch.setText("Synchronize with repositories every");
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
		gridData.widthHint = 25;
		synchScheduleTime.setLayoutData(gridData);
		synchScheduleTime.setText(getMinutesString());
		synchScheduleTime.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateRefreshGroupEnablements();
			}
		});
		Label label = new Label(enableSynch, SWT.NONE);
		label.setText("minutes");

		notificationEnabledButton = new Button(group, SWT.CHECK);
		notificationEnabledButton.setText("Display notifications for overdue tasks and incoming changes");
		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
		
		// synchQueries = new Button(group, SWT.CHECK);
		// synchQueries.setText("Synchronize on startup");
		// synchQueries.setSelection(getPreferenceStore().getBoolean(
		// TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));

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

		activateOnOpen = new Button(container, SWT.CHECK);
		activateOnOpen.setText("Active on double-click");
		activateOnOpen.setEnabled(!reportInternal.getSelection());
		activateOnOpen.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.ACTIVATE_ON_OPEN));

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

		String taskDirectory = TasksUiPlugin.getDefault().getDataDirectory();
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
				dir = dir.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
				checkForExistingTasklist(dir);

				if (taskDataDirectoryAction != CANCEL) {
					taskDirectoryText.setText(dir);
					backupFolderText.setText(dir + FORWARDSLASH + ITasksUiConstants.DEFAULT_BACKUP_FOLDER_NAME);
					backupNow.setEnabled(false);
				}
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

		String backupDirectory = TasksUiPlugin.getDefault().getBackupFolderPath();// getPreferenceStore().getString(TaskListPreferenceConstants.BACKUP_FOLDER);
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
				TasksUiPlugin.getDefault().getBackupManager().backupNow(true);
			}
		});
	}

	private void createNotificationsGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Layout");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		incomingOverlaysButton = new Button(group, SWT.CHECK);
		incomingOverlaysButton.setText("Overlay synchronization state on task icons (for wide view)");
		incomingOverlaysButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.INCOMING_OVERLAID));
	}

	private void createSchedulingGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(GROUP_WORK_WEEK_LABEL);
		group.setLayout(new GridLayout(5, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Label workWeekBeginLabel = new Label(group, SWT.NONE);
		// workWeekBeginLabel.setText(START_DAY_LABEL);
		// workWeekBegin = new Combo(group, SWT.READ_ONLY);
		// // Calendar.SUNDAY = 1
		// workWeekBegin.add("SUNDAY");
		// workWeekBegin.add("MONDAY");
		// workWeekBegin.add("TUESDAY");
		// workWeekBegin.add("WEDNESDAY");
		// workWeekBegin.add("THURSDAY");
		// workWeekBegin.add("FRIDAY");
		// workWeekBegin.add("SATURDAY");
		// workWeekBegin.select(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_STARTDAY)
		// - 1);
		//		
		// Label workWeekEndLabel = new Label(group, SWT.NONE);
		// workWeekEndLabel.setText(END_DAY_LABEL);
		// workWeekEnd = new Combo(group, SWT.READ_ONLY);
		// workWeekEnd.add("SUNDAY");
		// workWeekEnd.add("MONDAY");
		// workWeekEnd.add("TUESDAY");
		// workWeekEnd.add("WEDNESDAY");
		// workWeekEnd.add("THURSDAY");
		// workWeekEnd.add("FRIDAY");
		// workWeekEnd.add("SATURDAY");
		// workWeekEnd.select(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_ENDDAY)
		// - 1);

		Label hourDayStartLabel = new Label(group, SWT.NONE);
		hourDayStartLabel.setText(START_HOUR_LABEL);
		hourDayStart = new Spinner(group, SWT.BORDER);
		hourDayStart.setDigits(0);
		hourDayStart.setIncrement(1);
		hourDayStart.setMaximum(23);
		hourDayStart.setMinimum(0);
		hourDayStart.setSelection(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_STARTHOUR));
		hourDayStart.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

		});

		Label spacer = new Label(group, SWT.NONE);
		GridDataFactory.fillDefaults().hint(40, SWT.DEFAULT).applyTo(spacer);

		Label hourDayEndLabel = new Label(group, SWT.NONE);
		hourDayEndLabel.setText(END_HOUR_LABEL);

		hourDayEnd = new Spinner(group, SWT.BORDER);
		hourDayEnd.setDigits(0);
		hourDayEnd.setIncrement(1);
		hourDayEnd.setMaximum(23);
		hourDayEnd.setMinimum(0);
		hourDayEnd.setSelection(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_ENDHOUR));
		hourDayEnd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

		});

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

		if (getErrorMessage() == null) {
			if (hourDayEnd.getSelection() <= hourDayStart.getSelection()) {
				setErrorMessage("Planning: Work day start must be before end.");
				this.setValid(false);
			} else {
				setErrorMessage(null);
				this.setValid(true);
			}
		}

		synchScheduleTime.setEnabled(enableBackgroundSynch.getSelection());
	}

	private String getMinutesString() {
		long miliseconds = getPreferenceStore().getLong(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		return "" + minutes;
	}

	private void checkForExistingTasklist(String dir) {
		File newDataFolder = new File(dir);
		if (newDataFolder.exists()) {
			for (String filename : newDataFolder.list()) {
				if (filename.equals(ITasksUiConstants.DEFAULT_TASK_LIST_FILE)) {

					MessageDialog dialogConfirm = new MessageDialog(
							null,
							"Tasklist found at destination",
							null,
							"Overwrite existing task data with current data or load from new destination?",
							MessageDialog.WARNING, new String[] { "Overwrite", "Load",
									IDialogConstants.CANCEL_LABEL }, CANCEL);
					taskDataDirectoryAction = dialogConfirm.open();
					break;
				}
			}
			if(taskDataDirectoryAction == -1) {
				taskDataDirectoryAction = OVERWRITE;
			}
		}
	}
}
