/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Abner Ballardo - fix for bug 276113
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylyn.commons.core.CommonMessages;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.internal.monitor.ui.ActivityContextManager;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.RestoreTaskListAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
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
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author David Green
 */
public class TasksUiPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.preferences"; //$NON-NLS-1$

	private static final String FORWARDSLASH = "/"; //$NON-NLS-1$

	private static final String BACKSLASH_MULTI = "\\\\"; //$NON-NLS-1$

	private static final int MS_MINUTES = 60 * 1000;

	private static BiMap<String, Integer> SCHEDULE_TIME_MAP = HashBiMap
			.create(ImmutableMap.of(ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR_THIS_WEEK, 0,
					ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR_TOMORROW, 1,
					ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR_TODAY, 2,
					ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR_NOT_SCHEDULED, 3));

	private Button useRichEditor;

	private Button editorHighlightsCurrentLine;

	private Button useWebBrowser;

	private Text fullSyncScheduleTime;

	private Text relevantTasksSyncScheduleTime;

	private Button enableFullTaskListSynch;

	private Button enableRelevantTasksSynch;

	private Text taskDirectoryText;

	private Button browse;

	private Button notificationEnabledButton;

	private final FormToolkit toolkit;

	private Spinner timeoutMinutes;

	private Button timeoutEnabledButton;

	private ExpandableComposite advancedComposite;

	private Combo weekStartCombo;

	private Combo scheduleNewTasksCombo;

	private Button activityTrackingEnabledButton;

	private Label timeoutLabel1;

	private Label timeoutLabel2;

	private Button taskListTooltipEnabledButton;

	private Button showTaskTrimButton;

	private Button taskListServiceMessageEnabledButton;

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

		createTaskRefreshScheduleGroup(container);
		createSchedulingGroup(container);
		createTaskNavigationGroup(container);
		createTaskListGroup(container);
		createTaskEditorGroup(container);
		Group taskActivityGroup = createTaskActivityGroup(container);
		if (!TasksUiPlugin.getTaskActivityMonitor().isEnabled()) {
			// hide controls but create them to avoid NPEs
			taskActivityGroup.setVisible(false);
			((GridData) taskActivityGroup.getLayoutData()).exclude = true;
		}

		Composite advanced = createAdvancedSection(container);
		createTaskDataControl(advanced);

		if (getContainer() instanceof IWorkbenchPreferenceContainer) {
			String message = Messages.TasksUiPreferencePage_See_X_for_configuring_Task_List_colors;
			new PreferenceLinkArea(advanced, SWT.NONE, "org.eclipse.ui.preferencePages.ColorsAndFonts", message, //$NON-NLS-1$
					(IWorkbenchPreferenceContainer) getContainer(), null);
		}

		createLinks(advanced);
		updateRefreshGroupEnablements();
		applyDialogFont(container);
		return container;
	}

	private void createLinks(Composite container) {
		Hyperlink link = new Hyperlink(container, SWT.NULL);
		link.setForeground(CommonColors.HYPERLINK_WIDGET);
		link.setUnderlined(true);
		link.setText(Messages.TasksUiPreferencePage_Use_the_Restore_dialog_to_recover_missing_tasks);
		link.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				getShell().close();
				new RestoreTaskListAction().run();
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}

		});
	}

	private Composite createAdvancedSection(Composite container) {
		advancedComposite = toolkit.createExpandableComposite(container,
				ExpandableComposite.COMPACT | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		advancedComposite.setFont(container.getFont());
		advancedComposite.setBackground(container.getBackground());
		advancedComposite.setText(Messages.TasksUiPreferencePage_Advanced);
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

		getPreferenceStore().setValue(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH, useRichEditor.getSelection());
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.EDITOR_CURRENT_LINE_HIGHLIGHT,
				editorHighlightsCurrentLine.getSelection());

		getPreferenceStore().setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED,
				enableFullTaskListSynch.getSelection());
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.RELEVANT_SYNCH_SCHEDULE_ENABLED,
				enableRelevantTasksSynch.getSelection());

		String miliseconds = toMillisecondsString(fullSyncScheduleTime.getText());
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS, miliseconds);
		miliseconds = toMillisecondsString(relevantTasksSyncScheduleTime.getText());
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.RELEVANT_TASKS_SCHEDULE_MILISECONDS, miliseconds);

		getPreferenceStore().setValue(ITasksUiPreferenceConstants.TASK_LIST_TOOL_TIPS_ENABLED,
				taskListTooltipEnabledButton.getSelection());
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.SERVICE_MESSAGES_ENABLED,
				taskListServiceMessageEnabledButton.getSelection());
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.SHOW_TRIM, showTaskTrimButton.getSelection());

		getPreferenceStore().setValue(ITasksUiPreferenceConstants.WEEK_START_DAY, getWeekStartValue());
		getPreferenceStore().setValue(ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR, getScheduleNewTasksValue());

		MonitorUiPlugin.getDefault().getPreferenceStore().setValue(ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED,
				timeoutEnabledButton.getSelection());
		MonitorUiPlugin.getDefault().getPreferenceStore().setValue(ActivityContextManager.ACTIVITY_TIMEOUT,
				timeoutMinutes.getSelection() * (60 * 1000));

		MonitorUiPlugin.getDefault().getPreferenceStore().setValue(MonitorUiPlugin.ACTIVITY_TRACKING_ENABLED,
				activityTrackingEnabledButton.getSelection());

		String taskDirectory = taskDirectoryText.getText();
		taskDirectory = taskDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);

		if (!taskDirectory.equals(TasksUiPlugin.getDefault().getDataDirectory())) {
			if (checkForExistingTasklist(taskDirectory)) {
				Exception exception = null;
				try {
					TasksUiPlugin.getDefault().setDataDirectory(taskDirectory);
				} catch (CoreException e) {
					exception = e;
					StatusHandler.log(e.getStatus());
					MessageDialog.openError(getShell(), Messages.TasksUiPreferencePage_Task_Data_Directory_Error,
							Messages.TasksUiPreferencePage_Error_applying_Task_List_data_directory_changes);

				} catch (OperationCanceledException ce) {
					exception = ce;
				}
				if (exception != null && !taskDirectoryText.isDisposed()) {
					String originalDirectory = TasksUiPlugin.getDefault().getDefaultDataDirectory();
					if (!taskDirectory.equals(originalDirectory)) {
						taskDirectoryText.setText(originalDirectory);
					}
				}
			} else {
				taskDirectoryText.setFocus();
				return false;
			}
		}

		return true;
	}

	private String toMillisecondsString(String minutesString) {
		return Long.toString(60 * 1000 * Long.parseLong(minutesString));
	}

	private int getWeekStartValue() {
		return weekStartCombo.getSelectionIndex() + 1;
	}

	private String getScheduleNewTasksValue() {
		int index = scheduleNewTasksCombo.getSelectionIndex();
		return SCHEDULE_TIME_MAP.inverse().get(index);
	}

	@Override
	public boolean performCancel() {
		taskDirectoryText.setText(TasksUiPlugin.getDefault().getDefaultDataDirectory());
		notificationEnabledButton
				.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED));

		useRichEditor.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH));
		editorHighlightsCurrentLine.setSelection(
				getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.EDITOR_CURRENT_LINE_HIGHLIGHT));

		String repositorySyncMinutes = toMinutesString(
				getPreferenceStore().getLong(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS));
		String relevantSyncMinutes = toMinutesString(
				getPreferenceStore().getLong(ITasksUiPreferenceConstants.RELEVANT_TASKS_SCHEDULE_MILISECONDS));
		boolean shouldSyncTaskList = getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);
		boolean shouldSyncRelevantTasks = getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.RELEVANT_SYNCH_SCHEDULE_ENABLED);

		enableFullTaskListSynch.setSelection(shouldSyncTaskList);
		enableRelevantTasksSynch.setSelection(shouldSyncRelevantTasks);
		fullSyncScheduleTime.setText(repositorySyncMinutes);
		relevantTasksSyncScheduleTime.setText(relevantSyncMinutes);

		taskListTooltipEnabledButton
				.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.TASK_LIST_TOOL_TIPS_ENABLED));
		taskListServiceMessageEnabledButton
				.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.SERVICE_MESSAGES_ENABLED));
		showTaskTrimButton.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.SHOW_TRIM));

		weekStartCombo.select(getPreferenceStore().getInt(ITasksUiPreferenceConstants.WEEK_START_DAY) - 1);
		String scheduleFor = getPreferenceStore().getString(ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR);
		scheduleNewTasksCombo.select(SCHEDULE_TIME_MAP.getOrDefault(scheduleFor, 0));

		int minutes = MonitorUiPlugin.getDefault().getPreferenceStore().getInt(ActivityContextManager.ACTIVITY_TIMEOUT)
				/ MS_MINUTES;
		timeoutMinutes.setSelection(minutes);
		timeoutEnabledButton.setSelection(MonitorUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED));

		activityTrackingEnabledButton.setSelection(MonitorUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(MonitorUiPlugin.ACTIVITY_TRACKING_ENABLED));

		return true;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		taskDirectoryText.setText(TasksUiPlugin.getDefault().getDefaultDataDirectory());

		notificationEnabledButton.setSelection(
				getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED));

		useRichEditor
				.setSelection(getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH));
		editorHighlightsCurrentLine.setSelection(
				getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.EDITOR_CURRENT_LINE_HIGHLIGHT));

		taskListTooltipEnabledButton.setSelection(
				getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.TASK_LIST_TOOL_TIPS_ENABLED));
		taskListServiceMessageEnabledButton.setSelection(
				getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.SERVICE_MESSAGES_ENABLED));
		showTaskTrimButton.setSelection(getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.SHOW_TRIM));

		enableFullTaskListSynch.setSelection(
				getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		enableRelevantTasksSynch.setSelection(
				getPreferenceStore().getDefaultBoolean(ITasksUiPreferenceConstants.RELEVANT_SYNCH_SCHEDULE_ENABLED));
		fullSyncScheduleTime.setText(toMinutesString(getPreferenceStore()
				.getDefaultLong(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS)));
		relevantTasksSyncScheduleTime.setText(toMinutesString(
				getPreferenceStore().getDefaultLong(ITasksUiPreferenceConstants.RELEVANT_TASKS_SCHEDULE_MILISECONDS)));

		weekStartCombo.select(getPreferenceStore().getDefaultInt(ITasksUiPreferenceConstants.WEEK_START_DAY) - 1);
		String defaultScheduleFor = getPreferenceStore()
				.getDefaultString(ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR);
		scheduleNewTasksCombo.select(SCHEDULE_TIME_MAP.getOrDefault(defaultScheduleFor, 0));

		int activityTimeoutMinutes = MonitorUiPlugin.getDefault()
				.getPreferenceStore()
				.getDefaultInt(ActivityContextManager.ACTIVITY_TIMEOUT) / MS_MINUTES;
		timeoutMinutes.setSelection(activityTimeoutMinutes);
		timeoutEnabledButton.setSelection(MonitorUiPlugin.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED));

		activityTrackingEnabledButton.setSelection(MonitorUiPlugin.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(MonitorUiPlugin.ACTIVITY_TRACKING_ENABLED));

		updateRefreshGroupEnablements();
	}

	private void createTaskRefreshScheduleGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.TasksUiPreferencePage_Synchronization);

		group.setLayout(new GridLayout(1, false));

		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite backgroundSync = new Composite(group, SWT.NULL);
		GridLayoutFactory.swtDefaults().numColumns(4).equalWidth(false).margins(0, 0).applyTo(backgroundSync);

		//Enabled background synchronization
		enableFullTaskListSynch = new Button(backgroundSync, SWT.CHECK);
		enableFullTaskListSynch.setText(Messages.TasksUiPreferencePage_Synchronize_Task_List);
		enableFullTaskListSynch.setSelection(
				getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		enableFullTaskListSynch.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		GridDataFactory.defaultsFor(enableFullTaskListSynch).span(4, 1).applyTo(enableFullTaskListSynch);

		//Synchronize Task List Fully
		new Label(backgroundSync, SWT.NONE).setText(Messages.TasksUiPreferencePage_Synchronize_Queries);
		fullSyncScheduleTime = createSynchronizationScheduleTextBox(backgroundSync,
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		new Label(backgroundSync, SWT.NONE).setText(Messages.TasksUiPreferencePage_minutes);
		new Label(backgroundSync, SWT.NONE);

		//Synchronize Relevant Tasks
		enableRelevantTasksSynch = new Button(backgroundSync, SWT.CHECK);
		enableRelevantTasksSynch.setText(Messages.TasksUiPreferencePage_Synchronize_Relevant_Tasks);
		enableRelevantTasksSynch.setSelection(
				getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.RELEVANT_SYNCH_SCHEDULE_ENABLED));
		enableRelevantTasksSynch.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		relevantTasksSyncScheduleTime = createSynchronizationScheduleTextBox(backgroundSync,
				ITasksUiPreferenceConstants.RELEVANT_TASKS_SCHEDULE_MILISECONDS);
		new Label(backgroundSync, SWT.NONE).setText(Messages.TasksUiPreferencePage_minutes);
		Label help = new Label(backgroundSync, SWT.NONE);
		help.setImage(CommonImages.getImage(CommonImages.QUESTION));
		help.setToolTipText(Messages.TasksUiPreferencePage_RelevantTasksHelp);

		//notification
		notificationEnabledButton = new Button(group, SWT.CHECK);
		notificationEnabledButton
				.setText(Messages.TasksUiPreferencePage_Display_notifications_for_overdue_tasks_and_incoming_changes);
		notificationEnabledButton
				.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED));
		GridDataFactory.defaultsFor(notificationEnabledButton).span(4, 1).applyTo(notificationEnabledButton);

	}

	private Text createSynchronizationScheduleTextBox(Composite backgroundSync, String preferenceKey) {
		Text text = new Text(backgroundSync, SWT.BORDER | SWT.RIGHT);
		GridData gridDataRepo = new GridData();
		gridDataRepo.widthHint = 25;
		text.setLayoutData(gridDataRepo);
		long querySyncMilliseconds = getPreferenceStore().getLong(preferenceKey);
		text.setText(toMinutesString(querySyncMilliseconds));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateRefreshGroupEnablements();
			}
		});
		return text;
	}

	private void createTaskEditorGroup(Composite parent) {
		Group container = new Group(parent, SWT.SHADOW_ETCHED_IN);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		container.setText(Messages.TasksUiPreferencePage_Task_Editing);
		useRichEditor = new Button(container, SWT.RADIO);
		useRichEditor.setText(Messages.TasksUiPreferencePage_Rich_Editor__Recommended_);
		useRichEditor.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH));
		useWebBrowser = new Button(container, SWT.RADIO);
		useWebBrowser.setText(Messages.TasksUiPreferencePage_Web_Browser);
		useWebBrowser.setSelection(!getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH));

		editorHighlightsCurrentLine = new Button(container, SWT.CHECK);
		editorHighlightsCurrentLine.setText(Messages.TasksUiPreferencePage_highlight_current_line);
		editorHighlightsCurrentLine.setSelection(
				getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.EDITOR_CURRENT_LINE_HIGHLIGHT));
	}

	private void createTaskDataControl(Composite parent) {

		Group taskDataGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		taskDataGroup.setText(Messages.TasksUiPreferencePage_Task_Data);
		taskDataGroup.setLayout(new GridLayout(1, false));
		taskDataGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite dataDirComposite = new Composite(taskDataGroup, SWT.NULL);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		dataDirComposite.setLayout(gridLayout);
		dataDirComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(dataDirComposite, SWT.NULL);
		label.setText(Messages.TasksUiPreferencePage_Data_directory_);

		String taskDirectory = TasksUiPlugin.getDefault().getDataDirectory();
		taskDirectory = taskDirectory.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
		taskDirectoryText = new Text(dataDirComposite, SWT.BORDER);
		taskDirectoryText.setText(taskDirectory);
		taskDirectoryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browse = new Button(dataDirComposite, SWT.TRAIL);
		browse.setText(Messages.TasksUiPreferencePage_Browse_);
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(Messages.TasksUiPreferencePage_Folder_Selection);
				dialog.setMessage(Messages.TasksUiPreferencePage_Specify_the_folder_for_tasks);
				String dir = taskDirectoryText.getText();
				dir = dir.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
				dialog.setFilterPath(dir);

				dir = dialog.open();
				if (dir == null || dir.equals("")) { //$NON-NLS-1$
					return;
				}
				dir = dir.replaceAll(BACKSLASH_MULTI, FORWARDSLASH);
				taskDirectoryText.setText(dir);
			}

		});

	}

	private void createSchedulingGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.TasksUiPreferencePage_Scheduling);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label weekStartLabel = new Label(group, SWT.NONE);
		weekStartLabel.setText(Messages.TasksUiPreferencePage_Week_Start);
		weekStartCombo = new Combo(group, SWT.READ_ONLY);
		weekStartCombo.add(CommonMessages.Sunday);
		weekStartCombo.add(CommonMessages.Monday);
		weekStartCombo.add(CommonMessages.Tuesday);
		weekStartCombo.add(CommonMessages.Wednesday);
		weekStartCombo.add(CommonMessages.Thursday);
		weekStartCombo.add(CommonMessages.Friday);
		weekStartCombo.add(CommonMessages.Saturday);
		weekStartCombo.select(getPreferenceStore().getInt(ITasksUiPreferenceConstants.WEEK_START_DAY) - 1);

		Label scheduleNewTasksLabel = new Label(group, SWT.NONE);
		scheduleNewTasksLabel.setText(Messages.TasksUiPreferencePage_ScheduleNewTasks);
		scheduleNewTasksCombo = new Combo(group, SWT.READ_ONLY);
		scheduleNewTasksCombo.add(Messages.TasksUiPreferencePage_ThisWeek);
		scheduleNewTasksCombo.add(Messages.TasksUiPreferencePage_Tomorrow);
		scheduleNewTasksCombo.add(Messages.TasksUiPreferencePage_Today);
		scheduleNewTasksCombo.add(Messages.TasksUiPreferencePage_Unscheduled);
		String scheduleFor = getPreferenceStore().getString(ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR);
		scheduleNewTasksCombo.select(SCHEDULE_TIME_MAP.getOrDefault(scheduleFor, 0));
	}

	private void createTaskNavigationGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.TasksUiPreferencePage_Task_Navigation_Group_Label);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());

		showTaskTrimButton = new Button(group, SWT.CHECK);
		showTaskTrimButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		showTaskTrimButton.setText(Messages.TasksUiPreferencePage_Show_active_task_trim_Button_Label);
		showTaskTrimButton.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.SHOW_TRIM));
	}

	private void createTaskListGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.TasksUiPreferencePage_Task_List_Group);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		taskListTooltipEnabledButton = new Button(group, SWT.CHECK);
		taskListTooltipEnabledButton.setText(Messages.TasksUiPreferencePage_Show_tooltip_on_hover_Label);
		taskListTooltipEnabledButton
				.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.TASK_LIST_TOOL_TIPS_ENABLED));

		taskListServiceMessageEnabledButton = new Button(group, SWT.CHECK);
		taskListServiceMessageEnabledButton
				.setText(Messages.TasksUiPreferencePage_Notification_for_new_connectors_available_Label);
		taskListServiceMessageEnabledButton
				.setSelection(getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.SERVICE_MESSAGES_ENABLED));
	}

	private Group createTaskActivityGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.TasksUiPreferencePage_Task_Timing);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		boolean activityTrackingEnabled = MonitorUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(MonitorUiPlugin.ACTIVITY_TRACKING_ENABLED);

		boolean timeoutEnabled = MonitorUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED);

		activityTrackingEnabledButton = new Button(group, SWT.CHECK);
		activityTrackingEnabledButton.setText(Messages.TasksUiPreferencePage_Enable_Time_Tracking);
		activityTrackingEnabledButton.setSelection(activityTrackingEnabled);
		activityTrackingEnabledButton.setToolTipText(Messages.TasksUiPreferencePage_Track_Time_Spent);
		activityTrackingEnabledButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}
		});
		GridDataFactory.swtDefaults().span(3, 1).applyTo(activityTrackingEnabledButton);

		timeoutEnabledButton = new Button(group, SWT.CHECK);
		timeoutEnabledButton.setText(Messages.TasksUiPreferencePage_Enable_inactivity_timeouts);
		timeoutEnabledButton.setSelection(timeoutEnabled);
		timeoutEnabledButton.setToolTipText(Messages.TasksUiPreferencePage_If_disabled);
		timeoutEnabledButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}
		});
		GridDataFactory.swtDefaults().span(3, 1).applyTo(timeoutEnabledButton);

		timeoutLabel1 = new Label(group, SWT.NONE);
		timeoutLabel1.setText(Messages.TasksUiPreferencePage_Stop_time_accumulation_after);
		timeoutMinutes = new Spinner(group, SWT.BORDER);
		timeoutMinutes.setDigits(0);
		timeoutMinutes.setIncrement(5);
		timeoutMinutes.setMaximum(60);
		timeoutMinutes.setMinimum(1);
		long minutes = MonitorUiPlugin.getDefault()
				.getPreferenceStore()
				.getLong(ActivityContextManager.ACTIVITY_TIMEOUT) / MS_MINUTES;
		timeoutMinutes.setSelection((int) minutes);
		timeoutMinutes.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

		});

		timeoutLabel2 = new Label(group, SWT.NONE);
		timeoutLabel2.setText(Messages.TasksUiPreferencePage_minutes_of_inactivity);

		return group;
	}

	public void updateRefreshGroupEnablements() {
		String errorMessage = null;

		boolean synchronizeTaskList = enableFullTaskListSynch.getSelection();
		if (synchronizeTaskList) {
			errorMessage = validateSynchronizeSchedule(fullSyncScheduleTime);
			if (errorMessage == null) {
				errorMessage = validateSynchronizeSchedule(relevantTasksSyncScheduleTime);
			}
		}

		setErrorMessage(errorMessage);
		setValid(errorMessage == null);

		if (activityTrackingEnabledButton.getSelection()) {
			timeoutEnabledButton.setEnabled(true);
			timeoutMinutes.setEnabled(timeoutEnabledButton.getSelection());
			timeoutLabel1.setEnabled(timeoutEnabledButton.getSelection());
			timeoutLabel2.setEnabled(timeoutEnabledButton.getSelection());
		} else {
			timeoutEnabledButton.setEnabled(false);
			timeoutMinutes.setEnabled(false);
			timeoutLabel1.setEnabled(false);
			timeoutLabel2.setEnabled(false);
		}

		boolean synchronizeRelevantTasks = synchronizeTaskList && enableRelevantTasksSynch.getSelection();
		enableRelevantTasksSynch.setEnabled(synchronizeTaskList);
		fullSyncScheduleTime.setEnabled(synchronizeTaskList);
		relevantTasksSyncScheduleTime.setEnabled(synchronizeRelevantTasks);
	}

	private String validateSynchronizeSchedule(Text synchronizeText) {
		String errorMessage = null;
		try {
			long number = Long.parseLong(synchronizeText.getText());
			if (number <= 0) {
				errorMessage = Messages.TasksUiPreferencePage_Synchronize_schedule_time_must_be_GT_0;
			}
		} catch (NumberFormatException e) {
			errorMessage = Messages.TasksUiPreferencePage_Synchronize_schedule_time_must_be_valid_integer;
		}
		return errorMessage;
	}

	private String toMinutesString(long miliseconds) {
		long minutes = miliseconds / 60000;
		return Long.toString(minutes);
	}

	private boolean checkForExistingTasklist(String dir) {
		File newDataFolder = new File(dir);
		if (!newDataFolder.exists() && !newDataFolder.mkdirs()) {
			MessageDialog.openWarning(getControl().getShell(), Messages.TasksUiPreferencePage_Change_data_directory,
					Messages.TasksUiPreferencePage_Destination_folder_cannot_be_created);
			return false;
		}

		MessageDialog dialogConfirm = new MessageDialog(null,
				Messages.TasksUiPreferencePage_Confirm_Task_List_data_directory_change, null,
				Messages.TasksUiPreferencePage_A_new_empty_Task_List_will_be_created_in_the_chosen_directory_if_one_does_not_already_exists,
				MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL },
				IDialogConstants.CANCEL_ID);
		int taskDataDirectoryAction = dialogConfirm.open();
		if (taskDataDirectoryAction != IDialogConstants.OK_ID) {
			return false;
		}

		for (TaskEditor taskEditor : TasksUiInternal.getActiveRepositoryTaskEditors()) {
			TasksUiInternal.closeTaskEditorInAllPages(taskEditor.getTaskEditorInput().getTask(), true);
		}
		return true;
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
