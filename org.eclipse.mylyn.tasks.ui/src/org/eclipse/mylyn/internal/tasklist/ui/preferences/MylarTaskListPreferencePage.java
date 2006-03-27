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

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.internal.tasklist.TaskListAutoArchiveManager;
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
 * @author Rob Elves
 */
public class MylarTaskListPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String LABEL_LAST_ARCHIVED_NEVER = "never";

	private static final String LAST_ARCHIVED_ON_LABEL = "Last archived on: ";

	private Text taskDirectoryText = null;

	private Text taskURLPrefixText = null;

	private Button browse = null;

	private Button archiveNow = null;

	private Button notificationEnabledButton = null;

	private Button archiveAutomaticallyButton;

	private Text archiveScheduleTimeText;

	private Text archiveFolderText;

	private Label lastUpdate;

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
//		createTaskActivityGroup(container);
		createNotificationsGroup(container);
		createTaskArchiveScheduleGroup(container);
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
		taskDirectory = taskDirectory.replaceAll("\\\\", "/");
		if (!taskDirectory.equals(MylarPlugin.getDefault().getDataDirectory())) {
			// Order matters:
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
			// if (copyExistingDataCheckbox.getSelection()) {
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().copyDataDirContentsTo(taskDirectory);
			// }
			MylarPlugin.getDefault().setDataDirectory(taskDirectory);
		}

		getPreferenceStore().setValue(TaskListPreferenceConstants.DEFAULT_URL_PREFIX, taskURLPrefixText.getText());

		getPreferenceStore().setValue(TaskListPreferenceConstants.NOTIFICATIONS_ENABLED,
				notificationEnabledButton.getSelection());

		getPreferenceStore().setValue(TaskListPreferenceConstants.ARCHIVE_AUTOMATICALLY,
				archiveAutomaticallyButton.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.ARCHIVE_SCHEDULE, archiveScheduleTimeText.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.ARCHIVE_FOLDER, archiveFolderText.getText());

		return true;
	}

	@Override
	public boolean performCancel() {

		taskDirectoryText.setText(MylarPlugin.getDefault().getDefaultDataDirectory());

		taskURLPrefixText.setText(getPreferenceStore().getString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX));

		notificationEnabledButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));

		archiveAutomaticallyButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.ARCHIVE_AUTOMATICALLY));
		archiveScheduleTimeText.setText(getPreferenceStore().getString(TaskListPreferenceConstants.ARCHIVE_SCHEDULE));
		archiveFolderText.setText(getPreferenceStore().getString(TaskListPreferenceConstants.ARCHIVE_FOLDER));

		return true;
	}

	public void performDefaults() {
		super.performDefaults();

		taskDirectoryText.setText(MylarPlugin.getDefault().getDefaultDataDirectory());

		taskURLPrefixText
				.setText(getPreferenceStore().getDefaultString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX));

		notificationEnabledButton.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.NOTIFICATIONS_ENABLED));

		archiveAutomaticallyButton.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.ARCHIVE_AUTOMATICALLY));
		archiveScheduleTimeText.setText(getPreferenceStore().getDefaultString(
				TaskListPreferenceConstants.ARCHIVE_SCHEDULE));
		archiveFolderText.setText(getPreferenceStore().getDefaultString(TaskListPreferenceConstants.ARCHIVE_FOLDER));
		updateRefreshGroupEnablements();
	}

	private void createTaskArchiveScheduleGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText("Auto Archive");
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite archiveTop = new Composite(group, SWT.NONE);
		archiveTop.setLayout(new GridLayout(3, false));
		GridData archiveData = new GridData();
		archiveData.horizontalSpan = 3;
		archiveTop.setLayoutData(archiveData);

		archiveAutomaticallyButton = new Button(archiveTop, SWT.CHECK);
		archiveAutomaticallyButton.setText("Automatically archive tasks every");
		archiveAutomaticallyButton.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.ARCHIVE_AUTOMATICALLY));
		archiveAutomaticallyButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateRefreshGroupEnablements();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		{
			archiveScheduleTimeText = new Text(archiveTop, SWT.BORDER | SWT.RIGHT);
			final GridData gridData_1 = new GridData();
			gridData_1.widthHint = 15;
			archiveScheduleTimeText.setLayoutData(gridData_1);

			archiveScheduleTimeText.setText(""
					+ getPreferenceStore().getInt(TaskListPreferenceConstants.ARCHIVE_SCHEDULE));
			archiveScheduleTimeText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					updateRefreshGroupEnablements();
				}
			});

		}

		Label label = new Label(archiveTop, SWT.NONE);
		label.setText("days");

		label = new Label(group, SWT.LEFT);
		label.setText("to");

		String archiveDirectory = getPreferenceStore().getString(TaskListPreferenceConstants.ARCHIVE_FOLDER);
		archiveDirectory = archiveDirectory.replaceAll("\\\\", "/");
		archiveFolderText = new Text(group, SWT.BORDER);

		archiveFolderText.setText(archiveDirectory);
		archiveFolderText.setEditable(false);
		archiveFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browse = new Button(group, SWT.TRAIL);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Folder Selection");
				dialog.setMessage("Specify the archive output folder");
				String dir = archiveFolderText.getText();
				dir = dir.replaceAll("\\\\", "/");
				dialog.setFilterPath(dir);
				dir = dialog.open();
				if (dir == null || dir.equals(""))
					return;
				archiveFolderText.setText(dir);
				updateRefreshGroupEnablements();
			}
		});

		final Label spacer = new Label(group, SWT.NONE);
		GridData spacerData = new GridData();
		spacerData.horizontalSpan = 1;
		spacer.setLayoutData(spacerData);

		lastUpdate = new Label(group, SWT.NONE);
		lastUpdate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setLastArchived();

		archiveNow = new Button(group, SWT.NONE);
		archiveNow.setText("Archive Now");
		archiveNow.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getPreferenceStore().setValue(TaskListPreferenceConstants.ARCHIVE_FOLDER,
							archiveFolderText.getText());
					TaskListAutoArchiveManager.archiveNow();
					setLastArchived();
				} catch (InvocationTargetException ex) {
					MessageDialog.openError(getShell(), "Archive Error",
							TaskListAutoArchiveManager.ARCHIVE_FAILURE_MESSAGE + ex.getCause().getMessage());
				}
			}
		});

	}

	private void setLastArchived() {
		long lastExported = getPreferenceStore().getLong(TaskListPreferenceConstants.ARCHIVE_LAST);
		String dateText = "";
		if (lastExported > 0) {
			dateText = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastExported);
		} else {
			dateText = LABEL_LAST_ARCHIVED_NEVER;
		}
		lastUpdate.setText(LAST_ARCHIVED_ON_LABEL + dateText);// LAST_ARCHIVED_ON_LABEL+
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

		browse = new Button(taskDirComposite, SWT.TRAIL);
		browse.setText("Browse...");
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

//	private void createTaskActivityGroup(Composite parent) {
//		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
//		group.setText("Work Schedule");
//		group.setLayout(new GridLayout(2, false));
//		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		Label workLabel = new Label(group, SWT.NONE);
//		workLabel.setText("Your week starts on");
//		Combo workWeekBegin = new Combo(group, SWT.READ_ONLY);
//		workWeekBegin.add("MONDAY");//, Calendar.MONDAY
//		workWeekBegin.add("TUESDAY");//, Calendar.TUESDAY
//		workWeekBegin.add("WEDNESDAY");//, Calendar.WEDNESDAY
//		workWeekBegin.add("THURSDAY");//, Calendar.MONDAY
//		workWeekBegin.add("FRIDAY");//, Calendar.TUESDAY
//		workWeekBegin.add("SATURDAY");//, Calendar.WEDNESDAY
//		workWeekBegin.add("SUNDAY");//, Calendar.WEDNESDAY
//		
//		Label startHourLabel = new Label(group, SWT.NONE);
//		startHourLabel.setText("Your day begins at (24hr)");
//		
//		Spinner startHour = new Spinner(group, SWT.NULL | SWT.BORDER);
//		startHour.setSelection(1);
//		startHour.setDigits(0);
//		startHour.setMaximum(24);
//		startHour.setMinimum(0);
//		startHour.setIncrement(1);
////		startHour.addModifyListener(new ModifyListener() {
////			public void modifyText(ModifyEvent e) {
////				// do something
////			}
////		});
//	}
	
	private void createCreationGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Task Creation");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label urlLabel = new Label(group, SWT.LEFT);
		urlLabel.setText("Web link prefix (e.g. https://bugs.eclipse.org/bugs/show_bug.cgi?id=)");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		urlLabel.setLayoutData(data);

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
		if (archiveAutomaticallyButton.getSelection()) {
			try {
				long number = Integer.parseInt(archiveScheduleTimeText.getText());
				if (number <= 0) {
					this.setErrorMessage("Archive schedule time must be > 0");
					this.setValid(false);
				} else if (archiveFolderText.getText() == "") {
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
		archiveScheduleTimeText.setEnabled(archiveAutomaticallyButton.getSelection());
		archiveNow.setEnabled(archiveFolderText.getText() != "");
	}

}
