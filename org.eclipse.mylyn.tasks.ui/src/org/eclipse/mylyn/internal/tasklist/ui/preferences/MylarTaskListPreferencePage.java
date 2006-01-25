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
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class MylarTaskListPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text taskDirectoryText = null;

	private Text taskURLPrefixText = null;

	private Button browse = null;

	private Button copyExistingDataCheckbox = null;

	private Button reportEditor = null;

	private Button reportInternal = null;

	private Button refreshQueries = null;

//	private Button refreshQueries;
	
	public MylarTaskListPreferencePage() {
		super();
		setPreferenceStore(MylarTaskListPlugin.getPrefs());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createCreationGroup(container);
		createTaskDirectoryControl(container);
		createBugzillaReportOption(container);
		createUserbooleanControl(container);
		return container;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	private void createUserbooleanControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		container.setLayoutData(gridData);
		GridLayout gl = new GridLayout(1, false);
		container.setLayout(gl);
		
//		refreshQueries = new Button(container, SWT.CHECK)
//		refreshQueries.setText("Automatically refresh Bugzilla reports and queries on startup", BooleanFieldEditor.DEFAULT,
//				getFieldEditorParent());
//		
		
		// closeEditors = new Button(container, SWT.CHECK);
		// closeEditors.setText("Close all editors on task deactivation
		// (defaults to close only editors of interesting resources)");
		// closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.AUTO_MANAGE_EDITORS));

		refreshQueries = new Button(container, SWT.CHECK);
		refreshQueries.setText("Automatically perform a repository refresh on startup");
		refreshQueries.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
	}

	private void createBugzillaReportOption(Composite parent) {
		Group container = new Group(parent, SWT.SHADOW_ETCHED_IN);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		container.setText("Open Repository Tasks With");
		reportEditor = new Button(container, SWT.RADIO);
		reportEditor.setText("Bug editor");
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
			if (copyExistingDataCheckbox.getSelection()) {
				MylarTaskListPlugin.getDefault().getTaskListSaveManager().copyDataDirContentsTo(taskDirectory);
			}
			MylarPlugin.getDefault().setDataDirectory(taskDirectory);
			// getPreferenceStore().setValue(MylarPlugin.PREF_DATA_DIR,
			// taskDirectory);
			// MylarTaskListPlugin.getDefault().setDataDirectory(MylarPlugin.getDefault().getDataDirectory());
		}

		getPreferenceStore().setValue(TaskListPreferenceConstants.COPY_TASK_DATA,
				copyExistingDataCheckbox.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		// getPreferenceStore().setValue(MylarTaskListPlugin.REPORT_OPEN_EXTERNAL,
		// reportExternal.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.DEFAULT_URL_PREFIX, taskURLPrefixText.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP, refreshQueries.getSelection());

		return true;
	}

	@Override
	public boolean performCancel() {
		// closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.AUTO_MANAGE_EDITORS));
		reportEditor.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		// reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTaskListPlugin.REPORT_OPEN_EXTERNAL));
		refreshQueries.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		// saveCombo.setText(getPreferenceStore().getString(MylarTaskListPlugin.SAVE_TASKLIST_MODE));
		return true;
	}

	public void performDefaults() {
		super.performDefaults();
		copyExistingDataCheckbox.setSelection(true);
		// IPath rootPath =
		// ResourcesPlugin.getWorkspace().getRoot().getLocation();
		// String taskDirectory = rootPath.toString() + "/" +
		// MylarPlugin.DATA_DIR_NAME;
		taskDirectoryText.setText(MylarPlugin.getDefault().getDefaultDataDirectory());

		// copyExistingDataCheckbox.setSelection(getPreferenceStore().getDefaultBoolean(MylarTaskListPlugin.COPY_TASK_DATA));
		reportEditor
				.setSelection(getPreferenceStore().getDefaultBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		// reportExternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTaskListPlugin.REPORT_OPEN_EXTERNAL));
		taskURLPrefixText.setText(getPreferenceStore().getDefaultString(TaskListPreferenceConstants.DEFAULT_URL_PREFIX));

		refreshQueries.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
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
		taskDirComposite.setText("Mylar Data Directory (task list and contexts)");
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

		copyExistingDataCheckbox = new Button(taskDirComposite, SWT.CHECK);
		copyExistingDataCheckbox.setText("Copy existing data to new location");
		copyExistingDataCheckbox.setSelection(getPreferenceStore()
				.getBoolean(TaskListPreferenceConstants.COPY_TASK_DATA));

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

	private Button createButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.TRAIL);
		button.setText(text);
		button.setVisible(true);
		return button;
	}
}
