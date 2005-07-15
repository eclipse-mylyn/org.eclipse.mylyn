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
package org.eclipse.mylar.tasks.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Ken Sueda
 */
public class MylarTaskPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Button closeEditors = null;
	private Button reportEditor = null;
	private Button reportInternal = null;
	private Button reportExternal = null;
	
	public MylarTaskPreferencePage() {
		super();
		setPreferenceStore(MylarTasksPlugin.getPrefs());	
	}
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout (layout);
		
		createUserbooleanControl(container);
		createBugzillaReportOption(container);
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
		closeEditors = new Button(container, SWT.CHECK);
		closeEditors.setText("Close all editors on task deactivation (defaults to close only editors of interesting resources)");
		closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.CLOSE_EDITORS));		
	}
	
	private void createBugzillaReportOption(Composite parent) {
		Group container= new Group(parent, SWT.SHADOW_ETCHED_IN);		
		container.setLayout(new RowLayout());
		container.setText("Open Bugzilla reports with");
		reportEditor = new Button(container, SWT.RADIO);
		reportEditor.setText("Bug editor");
		reportEditor.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REPORT_OPEN_EDITOR));
		reportInternal = new Button(container, SWT.RADIO);
		reportInternal.setText("Internal browser");
		reportInternal.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REPORT_OPEN_INTERNAL));
		reportExternal = new Button(container, SWT.RADIO);
		reportExternal.setText("External browser");
		reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REPORT_OPEN_EXTERNAL));
		reportExternal.setEnabled(false);
	}
	
	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(MylarPlugin.CLOSE_EDITORS, closeEditors.getSelection());		
		getPreferenceStore().setValue(MylarTasksPlugin.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(MylarTasksPlugin.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		getPreferenceStore().setValue(MylarTasksPlugin.REPORT_OPEN_EXTERNAL, reportExternal.getSelection());
		return true;
	}
	@Override
	public boolean performCancel() {
		closeEditors.setSelection(getPreferenceStore().getBoolean(MylarPlugin.CLOSE_EDITORS));		
		reportEditor.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REPORT_OPEN_INTERNAL));
		reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REPORT_OPEN_EXTERNAL));
		return true;
	}
	
	public void performDefaults() {
		super.performDefaults();
		closeEditors.setSelection(getPreferenceStore().getDefaultBoolean(MylarPlugin.CLOSE_EDITORS));		
		reportEditor.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasksPlugin.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasksPlugin.REPORT_OPEN_INTERNAL));
		reportExternal.setSelection(getPreferenceStore().getDefaultBoolean(MylarTasksPlugin.REPORT_OPEN_EXTERNAL));		  
	}
}
