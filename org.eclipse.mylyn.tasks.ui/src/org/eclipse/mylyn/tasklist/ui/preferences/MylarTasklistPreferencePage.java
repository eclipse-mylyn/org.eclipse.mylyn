/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasklist.ui.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
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
public class MylarTasklistPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text taskDirectoryText = null;

	private Text taskURLPrefixText = null;

	private Text commitPrefixCompleted = null;

	private Text commitPrefixProgress = null;
	
	private Button browse = null;

	private Button copyExistingDataCheckbox = null;

	private Button reportEditor = null;

	private Button reportInternal = null;

	private Button multipleActive = null;

	public MylarTasklistPreferencePage() {
		super();
		setPreferenceStore(MylarTasklistPlugin.getPrefs());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createCreationGroup(container);
		createCommitGroup(container);
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
		//		closeEditors = new Button(container, SWT.CHECK);
		//		closeEditors.setText("Close all editors on task deactivation (defaults to close only editors of interesting resources)");
		//		closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.AUTO_MANAGE_EDITORS));

		multipleActive = new Button(container, SWT.CHECK);
		multipleActive.setText("Enable multiple task contexts to be active");
		multipleActive.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS));
	}

	private void createBugzillaReportOption(Composite parent) {
		Group container = new Group(parent, SWT.SHADOW_ETCHED_IN);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		container.setText("Open Bug Reports With");
		reportEditor = new Button(container, SWT.RADIO);
		reportEditor.setText("Bug editor");
		reportEditor.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EDITOR));
		reportInternal = new Button(container, SWT.RADIO);
		reportInternal.setText("Internal browser");
		reportInternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_INTERNAL));
		//		reportExternal = new Button(container, SWT.RADIO);
		//		reportExternal.setText("External browser");
		//		reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL));
		//		reportExternal.setEnabled(false);
	}

	@Override
	public boolean performOk() {
		String taskDirectory = taskDirectoryText.getText();
		taskDirectory = taskDirectory.replaceAll("\\\\", "/");
		if (!taskDirectory.equals(MylarPlugin.getDefault().getMylarDataDirectory())) {
			//Order matters:
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
			if (copyExistingDataCheckbox.getSelection()) {
				MylarTasklistPlugin.getDefault().copyDataDirContentsTo(taskDirectory);
			}
			getPreferenceStore().setValue(MylarPlugin.MYLAR_DIR, taskDirectory);
			MylarTasklistPlugin.getDefault().setDataDirectory(MylarPlugin.getDefault().getMylarDataDirectory());
		}

		getPreferenceStore().setValue(MylarTasklistPlugin.COPY_TASK_DATA, copyExistingDataCheckbox.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		//		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL, reportExternal.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.DEFAULT_URL_PREFIX, taskURLPrefixText.getText());
		getPreferenceStore().setValue(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS, multipleActive.getSelection());
		
		getPreferenceStore().setValue(MylarTasklistPlugin.COMMIT_PREFIX_COMPLETED, commitPrefixCompleted.getText());
		getPreferenceStore().setValue(MylarTasklistPlugin.COMMIT_PREFIX_PROGRESS, commitPrefixProgress.getText());
		return true;
	}

	@Override
	public boolean performCancel() {
		//		closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.AUTO_MANAGE_EDITORS));		
		reportEditor.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_INTERNAL));
		//		reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL));
		multipleActive.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS));
		//		saveCombo.setText(getPreferenceStore().getString(MylarTasklistPlugin.SAVE_TASKLIST_MODE));
		return true;
	}

	public void performDefaults() {
		super.performDefaults();

		IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String taskDirectory = rootPath.toString() + "/" + MylarPlugin.MYLAR_DIR_NAME;
		taskDirectoryText.setText(taskDirectory);

		copyExistingDataCheckbox.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.COPY_TASK_DATA));
		reportEditor.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_INTERNAL));
		//		reportExternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL));
		taskURLPrefixText.setText(getPreferenceStore().getDefaultString(MylarTasklistPlugin.DEFAULT_URL_PREFIX));

		commitPrefixCompleted.setText(getPreferenceStore().getDefaultString(MylarTasklistPlugin.COMMIT_PREFIX_COMPLETED));
		commitPrefixProgress.setText(getPreferenceStore().getDefaultString(MylarTasklistPlugin.COMMIT_PREFIX_PROGRESS));

		multipleActive.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS));
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
		taskDirComposite.setText("Task Directory");
		taskDirComposite.setLayout(new GridLayout(2, false));
		taskDirComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String taskDirectory = getPreferenceStore().getString(MylarPlugin.MYLAR_DIR);
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
		copyExistingDataCheckbox.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.COPY_TASK_DATA));

	}

	private void createCreationGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Task Creation");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label urlLabel = createLabel(group, "Web link prefix (e.g. https://bugs.eclipse.org/bugs/show_bug.cgi?id=)");
		urlLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		String taskURLPrefix = getPreferenceStore().getString(MylarTasklistPlugin.DEFAULT_URL_PREFIX);
		taskURLPrefixText = new Text(group, SWT.BORDER);
		taskURLPrefixText.setText(taskURLPrefix);
		taskURLPrefixText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createCommitGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Task Commit");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label completedLabel = createLabel(group, "Completed prefix: ");
		completedLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		String completedPrefix = getPreferenceStore().getString(MylarTasklistPlugin.COMMIT_PREFIX_COMPLETED);
		commitPrefixCompleted = new Text(group, SWT.BORDER);
		commitPrefixCompleted.setText(completedPrefix);
		commitPrefixCompleted.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label progressLabel = createLabel(group, "Uncompleted prefix: ");
		progressLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		String progressPrefix = getPreferenceStore().getString(MylarTasklistPlugin.COMMIT_PREFIX_PROGRESS);
		commitPrefixProgress = new Text(group, SWT.BORDER);
		commitPrefixProgress.setText(progressPrefix);
		commitPrefixProgress.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private Button createButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.TRAIL);
		button.setText(text);
		button.setVisible(true);
		return button;
	}
}
