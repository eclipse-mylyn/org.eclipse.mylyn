/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.monitor.ui.ActivityContextManager;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TasksUiPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.preferences";

	private static final String FOLDER_SELECTION_MESSAGE = "Specify the folder for tasks";

	private static final String TITLE_FOLDER_SELECTION = "Folder Selection";

	private static final String LABEL_ACTIVITY_TIMEOUT = "Stop time accumulation after";

	private static final String LABEL_ACTIVITY_TIMEOUT2 = "minutes of inactivity.";

	private static final String END_HOUR_LABEL = "Work day end (24hr): ";

	private static final String START_HOUR_LABEL = "Work day start (24hr): ";

	private static final String GROUP_WORK_WEEK_LABEL = "Scheduling";

	private static final String GROUP_TASK_TIMING = "Task Timing";

	private static final String FORWARDSLASH = "/";

	private static final String BACKSLASH_MULTI = "\\\\";

	private static final int MS_MINUTES = 60 * 1000;

	private Button useRichEditor;

	private Button useWebBrowser;

	private Text synchScheduleTime = null;

	private Button enableBackgroundSynch;

	private Text taskDirectoryText = null;

	private Button browse = null;

	private Button notificationEnabledButton = null;

//	private final Button backupNow = null;

//	private Text backupScheduleTimeText;

//	private Text backupFolderText;

//	private Spinner hourDayStart;

//	private Spinner hourDayEnd;

	private int taskDataDirectoryAction = -1;

	private final FormToolkit toolkit;

	private Spinner timeoutMinutes;

	private Button timeoutEnabledButton;

	private ExpandableComposite advancedComposite;

	private Combo weekStartCombo;

	public TasksUiPreferencePage() {
		super();
		setPreferenceStore(TasksUiPlugin.getDefault().getPreferenceStore());
		toolkit = new FormToolkit(Display.getCurrent());

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		if (getContainer() instanceof IWorkbenchPreferenceContainer) {
			String message = "See <a>''{0}''</a> for configuring Task List colors.";
			new PreferenceLinkArea(container, SWT.NONE, "org.eclipse.ui.preferencePages.ColorsAndFonts", message,
					(IWorkbenchPreferenceContainer) getContainer(), null);
		}

		createTaskRefreshScheduleGroup(container);
		createSchedulingGroup(container);
		createOpenWith(container);
		Composite advanced = createAdvancedSection(container);
		createTaskActivityGroup(advanced);
		createTaskDataControl(advanced);

		updateRefreshGroupEnablements();
		return container;
	}

	private Composite createAdvancedSection(Composite container) {
		advancedComposite = toolkit.createExpandableComposite(container, ExpandableComposite.COMPACT
				| ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		advancedComposite.setFont(container.getFont());
		advancedComposite.setBackground(container.getBackground());
		advancedComposite.setText("Advanced");
		advancedComposite.setLayout(new GridLayout(1, false));
		advancedComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		advancedComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				getControl().getShell().pack();
			}
		});

		Composite advanced = new Composite(advancedComposite, SWT.NONE);
		advanced.setLayout(new GridLayout(1, false));
		advancedComposite.setClient(advanced);
		return advanced;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED,
				notificationEnabledButton.getSelection());
		//getPreferenceStore().setValue(TasksUiPreferenceConstants.BACKUP_SCHEDULE, backupScheduleTimeText.getText());

		getPreferenceStore().setValue(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH, useRichEditor.getSelection());

		getPreferenceStore().setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED,
				enableBackgroundSynch.getSelection());
		long miliseconds = 60000 * Long.parseLong(synchScheduleTime.getText());
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS,
				"" + miliseconds);

		getPreferenceStore().setValue(ITasksUiPreferenceConstants.WEEK_START_DAY, getWeekStartValue());
		//getPreferenceStore().setValue(TasksUiPreferenceConstants.PLANNING_STARTHOUR, hourDayStart.getSelection());
//		getPreferenceStore().setValue(TasksUiPreferenceConstants.PLANNING_ENDHOUR, hourDayEnd.getSelection());
		MonitorUiPlugin.getDefault().getPreferenceStore().setValue(ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED,
				timeoutEnabledButton.getSelection());
		MonitorUiPlugin.getDefault().getPreferenceStore().setValue(ActivityContextManager.ACTIVITY_TIMEOUT,
				timeoutMinutes.getSelection() * (60 * 1000));
		//backupNow.setEnabled(true);

		String taskDirectory = taskDirectoryText.getText();
		taskDirectory = taskDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);

		if (!taskDirectory.equals(TasksUiPlugin.getDefault().getDataDirectory())) {
			if (taskDataDirectoryAction == IDialogConstants.OK_ID) {
				Exception exception = null;
				try {
					TasksUiPlugin.getDefault().setDataDirectory(taskDirectory, new NullProgressMonitor());
				} catch (CoreException e) {
					exception = e;
					StatusHandler.log(e.getStatus());
					MessageDialog.openError(getShell(), "Task Data Directory Error",
							"Error applying Task List data directory changes. The previous setting will be restored.");

				} catch (OperationCanceledException ce) {
					exception = ce;
				}
				if (exception != null && !taskDirectoryText.isDisposed()) {
					String originalDirectory = TasksUiPlugin.getDefault().getDefaultDataDirectory();
					if (!taskDirectory.equals(originalDirectory)) {
						taskDirectoryText.setText(originalDirectory);
					}
				}

			} else if (taskDataDirectoryAction == IDialogConstants.CANCEL_ID) {
				// shouldn't get here
			}
		}

		return true;
	}

	private int getWeekStartValue() {
		return weekStartCombo.getSelectionIndex() + 1;
	}

	@Override
	public boolean performCancel() {
		taskDirectoryText.setText(TasksUiPlugin.getDefault().getDefaultDataDirectory());
		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED));
		//backupScheduleTimeText.setText(getPreferenceStore().getString(TasksUiPreferenceConstants.BACKUP_SCHEDULE));
		//backupFolderText.setText(TasksUiPlugin.getDefault().getBackupFolderPath());

		useRichEditor.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH));
		// synchQueries.setSelection(getPreferenceStore().getBoolean(
		// TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		enableBackgroundSynch.setSelection(getPreferenceStore().getBoolean(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		synchScheduleTime.setText(getMinutesString());

		weekStartCombo.select(getPreferenceStore().getInt(ITasksUiPreferenceConstants.WEEK_START_DAY) - 1);
		//hourDayStart.setSelection(getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_STARTHOUR));
//		hourDayEnd.setSelection(getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
		//backupNow.setEnabled(true);
		int minutes = MonitorUiPlugin.getDefault().getPreferenceStore().getInt(ActivityContextManager.ACTIVITY_TIMEOUT)
				/ MS_MINUTES;
		timeoutMinutes.setSelection(minutes);
		timeoutEnabledButton.setSelection(MonitorUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED));
		return true;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		String taskDirectory = TasksUiPlugin.getDefault().getDefaultDataDirectory();
		if (!taskDirectory.equals(TasksUiPlugin.getDefault().getDataDirectory())) {
			checkForExistingTasklist(taskDirectory);
			if (taskDataDirectoryAction != IDialogConstants.CANCEL_ID) {
				taskDirectoryText.setText(taskDirectory);
//				backupFolderText.setText(taskDirectory + FORWARDSLASH + ITasksCoreConstants.DEFAULT_BACKUP_FOLDER_NAME);
//				backupNow.setEnabled(false);
			}
		} else {
			taskDirectoryText.setText(taskDirectory);
//			backupFolderText.setText(taskDirectory + FORWARDSLASH + ITasksCoreConstants.DEFAULT_BACKUP_FOLDER_NAME);
//			backupNow.setEnabled(true);
		}

		notificationEnabledButton.setSelection(getPreferenceStore().getDefaultBoolean(
				ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED));
		//backupScheduleTimeText.setText(getPreferenceStore().getDefaultString(TasksUiPreferenceConstants.BACKUP_SCHEDULE));

		useRichEditor.setSelection(getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH));

		// synchQueries.setSelection(getPreferenceStore().getDefaultBoolean(
		// TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		enableBackgroundSynch.setSelection(getPreferenceStore().getDefaultBoolean(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		// userRefreshOnly.setSelection(!enableBackgroundSynch.getSelection());
		long miliseconds = getPreferenceStore().getDefaultLong(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		synchScheduleTime.setText("" + minutes);
		weekStartCombo.select(getPreferenceStore().getDefaultInt(ITasksUiPreferenceConstants.WEEK_START_DAY) - 1);
		//	hourDayStart.setSelection(getPreferenceStore().getDefaultInt(TasksUiPreferenceConstants.PLANNING_STARTHOUR));
//		hourDayEnd.setSelection(getPreferenceStore().getDefaultInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
		int activityTimeoutMinutes = MonitorUiPlugin.getDefault().getPreferenceStore().getDefaultInt(
				ActivityContextManager.ACTIVITY_TIMEOUT)
				/ MS_MINUTES;
		timeoutMinutes.setSelection(activityTimeoutMinutes);
		timeoutEnabledButton.setSelection(MonitorUiPlugin.getDefault().getPreferenceStore().getDefaultBoolean(
				ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED));
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
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
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
				ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED));

	}

	private void createOpenWith(Composite parent) {
		Group container = new Group(parent, SWT.SHADOW_ETCHED_IN);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		container.setText("Task Editing");
		useRichEditor = new Button(container, SWT.RADIO);
		useRichEditor.setText("Rich Editor (Recommended)");
		useRichEditor.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH));
		useWebBrowser = new Button(container, SWT.RADIO);
		useWebBrowser.setText("Web Browser");
		useWebBrowser.setSelection(!getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH));
	}

	private void createTaskDataControl(Composite parent) {

		Group taskDataGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		taskDataGroup.setText("Task Data");
		taskDataGroup.setLayout(new GridLayout(1, false));
		taskDataGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite dataDirComposite = new Composite(taskDataGroup, SWT.NULL);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		dataDirComposite.setLayout(gridLayout);
		dataDirComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(dataDirComposite, SWT.NULL);
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
				if (dir == null || dir.equals("")) {
					return;
				}
				dir = dir.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
				checkForExistingTasklist(dir);

				if (taskDataDirectoryAction != IDialogConstants.CANCEL_ID) {
					taskDirectoryText.setText(dir);
//					backupFolderText.setText(dir + FORWARDSLASH + ITasksCoreConstants.DEFAULT_BACKUP_FOLDER_NAME);
//					backupNow.setEnabled(false);
				}
			}

		});

//		Composite backupComposite = new Composite(taskDataGroup, SWT.NULL);
//		gridLayout = new GridLayout(5, false);
//		gridLayout.marginWidth = 0;
//		gridLayout.marginHeight = 0;
//		backupComposite.setLayout(gridLayout);
//		backupComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

//		label = new Label(backupComposite, SWT.NULL);
//		label.setText("Backup every");
//		backupScheduleTimeText = new Text(backupComposite, SWT.BORDER | SWT.RIGHT);
//		final GridData gridData_1 = new GridData();
//		gridData_1.widthHint = 13;
//		backupScheduleTimeText.setLayoutData(gridData_1);
//
//		backupScheduleTimeText.setText("" + getPreferenceStore().getInt(TasksUiPreferenceConstants.BACKUP_SCHEDULE));
//		backupScheduleTimeText.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				updateRefreshGroupEnablements();
//			}
//		});
//
//		label = new Label(backupComposite, SWT.NONE);
//		label.setText("days to");

//		String backupDirectory = TasksUiPlugin.getDefault().getBackupFolderPath();// getPreferenceStore().getString(TaskListPreferenceConstants.BACKUP_FOLDER);
//		backupDirectory = backupDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
//		backupFolderText = new Text(backupComposite, SWT.BORDER);
//		backupFolderText.setText(backupDirectory);
//		backupFolderText.setEditable(false);
//		backupFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		backupNow = new Button(backupComposite, SWT.NONE);
//		backupNow.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//		backupNow.setText("Backup Now");
//		backupNow.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				TasksUiPlugin.getBackupManager().backupNow(true);
//			}
//		});
	}

	private void createSchedulingGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(GROUP_WORK_WEEK_LABEL);
		group.setLayout(new GridLayout(5, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label weekStartLabel = new Label(group, SWT.NONE);
		weekStartLabel.setText("Week Start:");
		weekStartCombo = new Combo(group, SWT.READ_ONLY);
		// Note: Calendar.SUNDAY = 1
		weekStartCombo.add("SUNDAY");
		weekStartCombo.add("MONDAY");
		weekStartCombo.select(getPreferenceStore().getInt(ITasksUiPreferenceConstants.WEEK_START_DAY) - 1);

//		 Label workWeekBeginLabel = new Label(group, SWT.NONE);
//		 workWeekBeginLabel.setText(START_DAY_LABEL);
//		 workWeekBegin = new Combo(group, SWT.READ_ONLY);
//		 // Calendar.SUNDAY = 1
//		 workWeekBegin.add("SUNDAY");
//		 workWeekBegin.add("MONDAY");
//		 workWeekBegin.add("TUESDAY");
//		 workWeekBegin.add("WEDNESDAY");
//		 workWeekBegin.add("THURSDAY");
//		 workWeekBegin.add("FRIDAY");
//		 workWeekBegin.add("SATURDAY");
//		 workWeekBegin.select(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_STARTDAY)
//		 - 1);

//		 Label workWeekEndLabel = new Label(group, SWT.NONE);
//		 workWeekEndLabel.setText(END_DAY_LABEL);
//		 workWeekEnd = new Combo(group, SWT.READ_ONLY);
//		 workWeekEnd.add("SUNDAY");
//		 workWeekEnd.add("MONDAY");
//		 workWeekEnd.add("TUESDAY");
//		 workWeekEnd.add("WEDNESDAY");
//		 workWeekEnd.add("THURSDAY");
//		 workWeekEnd.add("FRIDAY");
//		 workWeekEnd.add("SATURDAY");
//		 workWeekEnd.select(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_ENDDAY)
//		 - 1);

//		Label hourDayStartLabel = new Label(group, SWT.NONE);
//		hourDayStartLabel.setText(START_HOUR_LABEL);
//		hourDayStart = new Spinner(group, SWT.BORDER);
//		hourDayStart.setDigits(0);
//		hourDayStart.setIncrement(1);
//		hourDayStart.setMaximum(23);
//		hourDayStart.setMinimum(0);
//		hourDayStart.setSelection(getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_STARTHOUR));
//		hourDayStart.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateRefreshGroupEnablements();
//			}
//
//		});
//
//		Label spacer = new Label(group, SWT.NONE);
//		GridDataFactory.fillDefaults().hint(40, SWT.DEFAULT).applyTo(spacer);
//
//		Label hourDayEndLabel = new Label(group, SWT.NONE);
//		hourDayEndLabel.setText(END_HOUR_LABEL);
//
//		hourDayEnd = new Spinner(group, SWT.BORDER);
//		hourDayEnd.setDigits(0);
//		hourDayEnd.setIncrement(1);
//		hourDayEnd.setMaximum(23);
//		hourDayEnd.setMinimum(0);
//		hourDayEnd.setSelection(getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
//		hourDayEnd.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateRefreshGroupEnablements();
//			}
//
//		});

	}

	private void createTaskActivityGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(GROUP_TASK_TIMING);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		boolean timeoutEnabled = MonitorUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED);

		timeoutEnabledButton = new Button(group, SWT.CHECK);
		timeoutEnabledButton.setText("Enable inactivity timeouts");
		timeoutEnabledButton.setSelection(timeoutEnabled);
		timeoutEnabledButton.setToolTipText("If disabled, time accumulates while a task is active with no timeout due to inactivity.");
		timeoutEnabledButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}
		});
		GridDataFactory.swtDefaults().span(3, 1).applyTo(timeoutEnabledButton);

		Label timeoutLabel = new Label(group, SWT.NONE);
		timeoutLabel.setText(LABEL_ACTIVITY_TIMEOUT);
		timeoutMinutes = new Spinner(group, SWT.BORDER);
		timeoutMinutes.setDigits(0);
		timeoutMinutes.setIncrement(5);
		timeoutMinutes.setMaximum(60);
		timeoutMinutes.setMinimum(1);
		long minutes = MonitorUiPlugin.getDefault().getPreferenceStore().getLong(
				ActivityContextManager.ACTIVITY_TIMEOUT)
				/ MS_MINUTES;
		timeoutMinutes.setSelection((int) minutes);
		timeoutMinutes.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

		});

		timeoutLabel = new Label(group, SWT.NONE);
		timeoutLabel.setText(LABEL_ACTIVITY_TIMEOUT2);

//		Label spacer = new Label(group, SWT.NONE);
//		GridDataFactory.fillDefaults().hint(40, SWT.DEFAULT).applyTo(spacer);
//
//		Label hourDayEndLabel = new Label(group, SWT.NONE);
//		hourDayEndLabel.setText(END_HOUR_LABEL);
//
//		hourDayEnd = new Spinner(group, SWT.BORDER);
//		hourDayEnd.setDigits(0);
//		hourDayEnd.setIncrement(1);
//		hourDayEnd.setMaximum(23);
//		hourDayEnd.setMinimum(0);
//		hourDayEnd.setSelection(getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
//		hourDayEnd.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateRefreshGroupEnablements();
//			}
//
//		});

	}

//	private void createSchedulingGroup(Composite container) {
//		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
//		group.setText(GROUP_WORK_WEEK_LABEL);
//		group.setLayout(new GridLayout(5, false));
//		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		// Label workWeekBeginLabel = new Label(group, SWT.NONE);
//		// workWeekBeginLabel.setText(START_DAY_LABEL);
//		// workWeekBegin = new Combo(group, SWT.READ_ONLY);
//		// // Calendar.SUNDAY = 1
//		// workWeekBegin.add("SUNDAY");
//		// workWeekBegin.add("MONDAY");
//		// workWeekBegin.add("TUESDAY");
//		// workWeekBegin.add("WEDNESDAY");
//		// workWeekBegin.add("THURSDAY");
//		// workWeekBegin.add("FRIDAY");
//		// workWeekBegin.add("SATURDAY");
//		// workWeekBegin.select(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_STARTDAY)
//		// - 1);
//		//		
//		// Label workWeekEndLabel = new Label(group, SWT.NONE);
//		// workWeekEndLabel.setText(END_DAY_LABEL);
//		// workWeekEnd = new Combo(group, SWT.READ_ONLY);
//		// workWeekEnd.add("SUNDAY");
//		// workWeekEnd.add("MONDAY");
//		// workWeekEnd.add("TUESDAY");
//		// workWeekEnd.add("WEDNESDAY");
//		// workWeekEnd.add("THURSDAY");
//		// workWeekEnd.add("FRIDAY");
//		// workWeekEnd.add("SATURDAY");
//		// workWeekEnd.select(getPreferenceStore().getInt(TaskListPreferenceConstants.PLANNING_ENDDAY)
//		// - 1);
//
//		Label hourDayStartLabel = new Label(group, SWT.NONE);
//		hourDayStartLabel.setText(START_HOUR_LABEL);
//		hourDayStart = new Spinner(group, SWT.BORDER);
//		hourDayStart.setDigits(0);
//		hourDayStart.setIncrement(1);
//		hourDayStart.setMaximum(23);
//		hourDayStart.setMinimum(0);
//		hourDayStart.setSelection(getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_STARTHOUR));
//		hourDayStart.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateRefreshGroupEnablements();
//			}
//
//		});
//
//		Label spacer = new Label(group, SWT.NONE);
//		GridDataFactory.fillDefaults().hint(40, SWT.DEFAULT).applyTo(spacer);
//
//		Label hourDayEndLabel = new Label(group, SWT.NONE);
//		hourDayEndLabel.setText(END_HOUR_LABEL);
//
//		hourDayEnd = new Spinner(group, SWT.BORDER);
//		hourDayEnd.setDigits(0);
//		hourDayEnd.setIncrement(1);
//		hourDayEnd.setMaximum(23);
//		hourDayEnd.setMinimum(0);
//		hourDayEnd.setSelection(getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
//		hourDayEnd.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateRefreshGroupEnablements();
//			}
//
//		});
//
//	}

	public void updateRefreshGroupEnablements() {
		String errorMessage = null;

//		try {
//			long number = Integer.parseInt(backupScheduleTimeText.getText());
//			if (number <= 0) {
//				errorMessage = "Backup schedule time must be > 0";
//			} else if (backupFolderText.getText() == "") {
//				errorMessage = "Backup destination folder must be specified";
//			}
//		} catch (NumberFormatException e) {
//			errorMessage = "Backup schedule time must be valid integer";
//		}

		if (enableBackgroundSynch.getSelection()) {
			try {
				long number = Long.parseLong(synchScheduleTime.getText());
				if (number <= 0) {
					errorMessage = "Synchronize schedule time must be > 0";
				}
			} catch (NumberFormatException e) {
				errorMessage = "Synchronize schedule time must be valid integer";
			}
		}

//		if (hourDayEnd.getSelection() <= hourDayStart.getSelection()) {
//			errorMessage = "Planning: Work day start must be before end.";
//		}

		setErrorMessage(errorMessage);
		setValid(errorMessage == null);

		synchScheduleTime.setEnabled(enableBackgroundSynch.getSelection());

		timeoutMinutes.setEnabled(timeoutEnabledButton.getSelection());

	}

	private String getMinutesString() {
		long miliseconds = getPreferenceStore().getLong(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		return "" + minutes;
	}

	private void checkForExistingTasklist(String dir) {
		File newDataFolder = new File(dir);
		if (newDataFolder.exists()) {

			MessageDialog dialogConfirm = new MessageDialog(
					null,
					"Confirm Task List data directory change",
					null,
					"A new empty Task List will be created in the chosen directory if one does not already exists. Your previous directory and its contents will not be deleted.\n\nProceed?",
					MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL },
					IDialogConstants.CANCEL_ID);
			taskDataDirectoryAction = dialogConfirm.open();

			for (TaskEditor taskEditor : TasksUiInternal.getActiveRepositoryTaskEditors()) {
				TasksUiUtil.closeEditorInActivePage(taskEditor.getTaskEditorInput().getTask(), true);
			}

		} else {
			MessageDialog.openWarning(getControl().getShell(), "Change data directory",
					"Destination folder does not exist.");
		}
	}

	@Override
	public void dispose() {
		if (toolkit != null) {
			if (toolkit.getColors() != null) {
				toolkit.dispose();
			}
		}
		super.dispose();
	}
}
