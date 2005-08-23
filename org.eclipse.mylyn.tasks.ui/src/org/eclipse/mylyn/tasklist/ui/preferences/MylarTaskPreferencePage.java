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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
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
 * @author Ken Sueda and Mik Kersten
 */
public class MylarTaskPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, SelectionListener {
	
	private Text taskDirectoryText;
	private Button browse;
	
	private Button reportEditor = null;
	private Button reportInternal = null;
	private Button reportExternal = null;
	private Button multipleActive = null;

	public MylarTaskPreferencePage() {
		super();
		setPreferenceStore(MylarTasklistPlugin.getPrefs());	
	}
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout (layout);
		
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
//		closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE));
		
		multipleActive = new Button(container, SWT.CHECK);
		multipleActive.setText("Enable multiple task contexts to be active");
		multipleActive.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS));
	}
	
	private void createBugzillaReportOption(Composite parent) {
		Group container= new Group(parent, SWT.SHADOW_ETCHED_IN);		
		container.setLayout(new RowLayout());
		container.setText("Open Bugzilla reports with");
		reportEditor = new Button(container, SWT.RADIO);
		reportEditor.setText("Bug editor");
		reportEditor.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EDITOR));
		reportInternal = new Button(container, SWT.RADIO);
		reportInternal.setText("Internal browser");
		reportInternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_INTERNAL));
		reportExternal = new Button(container, SWT.RADIO);
		reportExternal.setText("External browser");
		reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL));
		reportExternal.setEnabled(false);
	}

	@Override
	public boolean performOk() {
		String taskDirectory = taskDirectoryText.getText();
		taskDirectory = taskDirectory.replaceAll("\\\\", "/");		
		getPreferenceStore().setValue(MylarPlugin.MYLAR_DIR, taskDirectory);
		
		
		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL, reportExternal.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS, multipleActive.getSelection());
		return true;
	}
	@Override
	public boolean performCancel() {
//		closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE));		
		reportEditor.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_INTERNAL));
		reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL));
		multipleActive.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS));
//		saveCombo.setText(getPreferenceStore().getString(MylarTasklistPlugin.SAVE_TASKLIST_MODE));
		return true;
	}
	
	public void performDefaults() {
		super.performDefaults();

		IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String taskDirectory = rootPath.toString() + "/" +MylarPlugin.MYLAR_DIR_NAME;
		taskDirectoryText.setText(taskDirectory);
				
		reportEditor.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_INTERNAL));
		reportExternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL));
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
		Group taskDirComposite= new Group(parent, SWT.SHADOW_ETCHED_IN);
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
		if (!MylarPlugin.getContextManager().hasActiveContext()) {
			browse.setEnabled(true);
		} else {
			browse.setEnabled(false);
			createLabel(taskDirComposite, "NOTE: you have an task active, deactivate it before changing directories");
		}
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
				if(dir == null || dir.equals(""))
					return;
				taskDirectoryText.setText(dir);
			}
		});        
	}	
	
	private Button createButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.TRAIL);
		button.setText(text);
		button.setVisible(true);
		button.addSelectionListener(this);
		return button;
	}
	
	/**
	 * Handle selection of an item in the menu.
	 */
	public void widgetDefaultSelected(SelectionEvent se) {
		widgetSelected(se);
	}

	/**
	 * Handle selection of an item in the menu.
	 */
	public void widgetSelected(SelectionEvent se) {
		// don't care when the widget is selected
	}
}
