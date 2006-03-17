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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class MylarTaskListPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text taskDirectoryText = null;

	private Text taskURLPrefixText = null;

	private Button browse = null;

	// private Button copyExistingDataCheckbox = null;

	private Button reportEditor = null;

	private Button reportInternal = null;

	private Button refreshQueries = null;

	private Button notificationEnabledButton = null;

	private Text refreshScheduleTime = null;

	private Button userRefreshOnly;

	private Button enableBackgroundRefresh;

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

		createCreationGroup(container);
		createNotificationsGroup(container);
		createBugzillaReportOption(container);
		createTaskRefreshScheduleGroup(container);
		createTaskDirectoryControl(container);
		updateRefreshGroupEnablements();
		return container;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	private void createBugzillaReportOption(Composite parent) {
		Group container = new Group(parent, SWT.SHADOW_ETCHED_IN);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		container.setText("Open Repository Tasks With");
		reportEditor = new Button(container, SWT.RADIO);
		reportEditor.setText("Editor if available (Recommended)");
		reportEditor.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal = new Button(container, SWT.RADIO);
		reportInternal.setText("Internal browser");
		reportInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		// reportExternal = new Button(container, SWT.RADIO);
		// reportExternal.setText("External browser");
		// reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTaskListPlugin.REPORT_OPEN_EXTERNAL));
		// reportExternal.setEnabled(false);
	}

	@Override
	public boolean performOk() {
		String taskDirectory = taskDirectoryText.getText();
		taskDirectory = taskDirectory.replaceAll("\\\\", "/");
		if (!taskDirectory.equals(MylarPlugin.getDefault().getDataDirectory())) {
			// Order matters:
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
			// if (copyExistingDataCheckbox.getSelection()) {
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().copyDataDirContentsTo(taskDirectory);
			// }
			MylarPlugin.getDefault().setDataDirectory(taskDirectory);
		}

		// getPreferenceStore().setValue(TaskListPreferenceConstants.COPY_TASK_DATA,
		// copyExistingDataCheckbox.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		// getPreferenceStore().setValue(MylarTaskListPlugin.REPORT_OPEN_EXTERNAL,
		// reportExternal.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.DEFAULT_URL_PREFIX, taskURLPrefixText.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP,
				refreshQueries.getSelection());

		getPreferenceStore().setValue(TaskListPreferenceConstants.NOTIFICATIONS_ENABLED,
				notificationEnabledButton.getSelection());

		// Set refresh schedule preferences and start/stop as necessary
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED,
				enableBackgroundRefresh.getSelection());
		long miliseconds = 60000 * Long.parseLong(refreshScheduleTime.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS,
				"" + miliseconds);

		return true;
	}

	@Override
	public boolean performCancel() {
		// closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.AUTO_MANAGE_EDITORS));
		reportEditor.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		// reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTaskListPlugin.REPORT_OPEN_EXTERNAL));
		refreshQueries.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		// saveCombo.setText(getPreferenceStore().getString(MylarTaskListPlugin.SAVE_TASKLIST_MODE));

		enableBackgroundRefresh.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		refreshScheduleTime.setText(getMinutesString());
		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
		return true;
	}

	public void performDefaults() {
		super.performDefaults();

		taskDirectoryText.setText(MylarPlugin.getDefault().getDefaultDataDirectory());
		// copyExistingDataCheckbox.setSelection(getPreferenceStore().getDefaultBoolean(MylarTaskListPlugin.COPY_TASK_DATA));
		reportEditor.setSelection(getPreferenceStore()
				.getDefaultBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		// reportExternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTaskListPlugin.REPORT_OPEN_EXTERNAL));
		taskURLPrefixText
				.setText(getPreferenceStore().getDefaultString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX));

		refreshQueries.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		notificationEnabledButton.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
		enableBackgroundRefresh.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		userRefreshOnly.setSelection(!enableBackgroundRefresh.getSelection());
		refreshScheduleTime.setText(getPreferenceStore().getDefaultString(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS));

	}

	private String getMinutesString() {
		long miliseconds = getPreferenceStore().getLong(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		return "" + minutes;
	}

	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(data);
		return label;
	}

	private void createTaskDirectoryControl(Composite parent) {
		Group taskDirComposite = new Group(parent, SWT.SHADOW_ETCHED_IN);
		taskDirComposite.setText("Mylar Data Directory (moves with workspace if default)");
		taskDirComposite.setLayout(new GridLayout(2, false));
		taskDirComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		String taskDirectory = MylarPlugin.getDefault().getDataDirectory();
		// String taskDirectory =
		// getPreferenceStore().getString(MylarPlugin.PREF_DATA_DIR);
		taskDirectory = taskDirectory.replaceAll("\\\\", "/");
		taskDirectoryText = new Text(taskDirComposite, SWT.BORDER);
		taskDirectoryText.setText(taskDirectory);
		taskDirectoryText.setEditable(false);
		taskDirectoryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browse = createButton(taskDirComposite, "Browse...");
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Folder Selection");
				dialog.setMessage("Specify the folder for tasks");
				String dir = taskDirectoryText.getText();
				dir = dir.replaceAll("\\\\", "/");
				dialog.setFilterPath(dir);

				dir = dialog.open();
				if (dir == null || dir.equals(""))
					return;
				taskDirectoryText.setText(dir);
			}
		});

		// copyExistingDataCheckbox = new Button(taskDirComposite, SWT.CHECK);
		// copyExistingDataCheckbox.setText("Copy existing data to new
		// location");
		// copyExistingDataCheckbox.setSelection(getPreferenceStore()
		// .getBoolean(TaskListPreferenceConstants.COPY_TASK_DATA));

	}

	private void createCreationGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Task Creation");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label urlLabel = createLabel(group, "Web link prefix (e.g. https://bugs.eclipse.org/bugs/show_bug.cgi?id=)");
		urlLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		String taskURLPrefix = getPreferenceStore().getString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX);
		taskURLPrefixText = new Text(group, SWT.BORDER);
		taskURLPrefixText.setText(taskURLPrefix);
		taskURLPrefixText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createNotificationsGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Notifications");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		notificationEnabledButton = new Button(group, SWT.CHECK);
		notificationEnabledButton.setText("Notifications enabled");
		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));
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

	// reference:
	// org.eclipse.team.internal.ui.synchronize.ConfigureSynchronizeScheduleComposite
	private void createTaskRefreshScheduleGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Repository Refresh");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		{
			userRefreshOnly = new Button(group, SWT.RADIO);
			final GridData gridData = new GridData();
			gridData.horizontalSpan = 2;
			userRefreshOnly.setLayoutData(gridData);
			userRefreshOnly.setText("Do not schedule background synchronization");
			userRefreshOnly.setSelection(!getPreferenceStore().getBoolean(
					TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
			userRefreshOnly.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					updateRefreshGroupEnablements();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

		}
		{
			enableBackgroundRefresh = new Button(group, SWT.RADIO);
			final GridData gridData = new GridData();
			gridData.horizontalSpan = 2;
			enableBackgroundRefresh.setLayoutData(gridData);
			enableBackgroundRefresh.setText("Use the following schedule (Experimental)");
			enableBackgroundRefresh.setSelection(getPreferenceStore().getBoolean(
					TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
			enableBackgroundRefresh.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					updateRefreshGroupEnablements();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

		}
		final Composite composite = new Composite(group, SWT.NONE);
		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		composite.setLayoutData(gridData);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 3;
		composite.setLayout(gridLayout_1);
		{
			final Label label = new Label(composite, SWT.NONE);
			label.setText("Every: ");
		}
		{
			refreshScheduleTime = new Text(composite, SWT.BORDER | SWT.RIGHT);
			final GridData gridData_1 = new GridData();
			gridData_1.widthHint = 35;
			refreshScheduleTime.setLayoutData(gridData_1);

			refreshScheduleTime.setText(getMinutesString());
			refreshScheduleTime.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					updateRefreshGroupEnablements();
				}
			});

		}
		{
			final Label label = new Label(composite, SWT.NONE);
			label.setText("minutes");
		}

		refreshQueries = new Button(group, SWT.CHECK);
		refreshQueries.setText("Automatically perform a repository refresh on startup");
		refreshQueries.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));

	}

	public void updateRefreshGroupEnablements() {
		if (enableBackgroundRefresh.getSelection()) {
			try {
				long number = Long.parseLong(refreshScheduleTime.getText());
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
		refreshScheduleTime.setEnabled(enableBackgroundRefresh.getSelection());
		// hoursOrMinutes.setEnabled(enableBackgroundRefresh.getSelection());
	}

	private Button createButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.TRAIL);
		button.setText(text);
		button.setVisible(true);
		return button;
	}
}
