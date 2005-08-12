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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Ken Sueda and Mik Kersten
 */
public class MylarTaskPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
//	private Button closeEditors = null;
	private Button reportEditor = null;
	private Button reportInternal = null;
	private Button reportExternal = null;
	private Button multipleActive = null;
	private Combo saveCombo = null;
	
	public MylarTaskPreferencePage() {
		super();
		setPreferenceStore(MylarTasklistPlugin.getPrefs());	
	}
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout (layout);
		
		createUserbooleanControl(container);
		createBugzillaReportOption(container);
		createSaveTaskListSection(container);
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
	
	private void createSaveTaskListSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		container.setLayoutData(gridData);
		GridLayout gl = new GridLayout(2, false);
		container.setLayout(gl);		
		
		Label l = new Label(container, SWT.NONE);
		l.setText("Automatically save task list every:");
		saveCombo = new Combo(container, SWT.DROP_DOWN);
		saveCombo.setItems(MylarTasklistPlugin.getDefault().getSaveOptions());
		saveCombo.setText(getPreferenceStore().getString(MylarTasklistPlugin.SAVE_TASKLIST_MODE));
	}
	@Override
	public boolean performOk() {
//		getPreferenceStore().setValue(MylarPlugin.TASKLIST_EDITORS_CLOSE, closeEditors.getSelection());		
		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL, reportExternal.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS, multipleActive.getSelection());
		getPreferenceStore().setValue(MylarTasklistPlugin.SAVE_TASKLIST_MODE, saveCombo.getText());
		return true;
	}
	@Override
	public boolean performCancel() {
//		closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE));		
		reportEditor.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_INTERNAL));
		reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL));
		multipleActive.setSelection(getPreferenceStore().getBoolean(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS));
		saveCombo.setText(getPreferenceStore().getString(MylarTasklistPlugin.SAVE_TASKLIST_MODE));
		return true;
	}
	
	public void performDefaults() {
		super.performDefaults();
//		closeEditors.setSelection(getPreferenceStore().getDefaultBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE));		
		reportEditor.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_INTERNAL));
		reportExternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.REPORT_OPEN_EXTERNAL));
		multipleActive.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasklistPlugin.MULTIPLE_ACTIVE_TASKS));
		saveCombo.setText(getPreferenceStore().getDefaultString(MylarTasklistPlugin.SAVE_TASKLIST_MODE));
	}
}
