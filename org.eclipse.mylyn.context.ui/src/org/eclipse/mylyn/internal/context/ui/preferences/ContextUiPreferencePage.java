/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Feb 13, 2005
 *
 */
package org.eclipse.mylyn.internal.context.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * @author Mik Kersten
 */
public class ContextUiPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, SelectionListener {

	private Button autoEnableExplorerFilter = null;

	private Button manageEditorsButton = null;

	private Button managePerspectivesButton = null;

	private Button manageExpansionButton = null;

	/**
	 * Constructor - set preference store to ContextUiPlugin store since the tasklist plugin needs access to the values
	 * stored from the preference page because it needs access to the highlighters on start up.
	 * 
	 */
	public ContextUiPreferencePage() {
		super();
		setPreferenceStore(ContextUiPlugin.getDefault().getPreferenceStore());
		setTitle("Context");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite entryTable = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 4;
		entryTable.setLayout(layout);

		createUiManagementSection(entryTable);
//		createExclusionFilterControl(entryTable);

		return entryTable;
	}

	public void init(IWorkbench workbench) {
		// don't have anything to initialize
	}

	public void widgetDefaultSelected(SelectionEvent se) {
		widgetSelected(se);
	}

	/**
	 * Handle selection of an item in the menu.
	 */
	public void widgetSelected(SelectionEvent se) {
		// don't care when the widget is selected
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE,
				autoEnableExplorerFilter.getSelection());

		getPreferenceStore().setValue(ContextUiPrefContstants.HIGHLIGHTER_PREFIX,
				ContextUiPlugin.getDefault().getHighlighterList().externalizeToString());
//		getPreferenceStore().setValue(ContextUiPrefContstants.INTEREST_FILTER_EXCLUSION,
//				exclusionFieldEditor.getStringValue());

		getPreferenceStore().setValue(ContextUiPrefContstants.AUTO_MANAGE_EDITORS, manageEditorsButton.getSelection());
		getPreferenceStore().setValue(ContextUiPrefContstants.AUTO_MANAGE_PERSPECTIVES,
				managePerspectivesButton.getSelection());
		getPreferenceStore().setValue(ContextUiPrefContstants.AUTO_MANAGE_EXPANSION,
				manageExpansionButton.getSelection());

		return true;
	}

	/**
	 * Handle Cancel Undo all changes back to what is stored in preference store
	 */
	@Override
	public boolean performCancel() {
		autoEnableExplorerFilter.setSelection(getPreferenceStore().getBoolean(
				ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE));
		return true;
	}

	/**
	 * Handle RestoreDefaults Note: changes to default are not stored in the preference store until OK or Apply is
	 * pressed
	 */
	@Override
	public void performDefaults() {
		super.performDefaults();
		return;
	}

//	private void createExclusionFilterControl(Composite parent) {
//		Group exclusionControl = new Group(parent, SWT.SHADOW_ETCHED_IN);
//		exclusionControl.setLayout(new GridLayout(1, false));
//		exclusionControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		Composite composite = new Composite(exclusionControl, SWT.NULL);
//		composite.setLayout(new GridLayout(1, false));
//		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		exclusionControl.setText("Interest Filter");
//
//		Label label = new Label(composite, SWT.LEFT);
//		label.setText("Exclusion pattern, matches will always be shown (e.g. build*.xml):");
//		exclusionFieldEditor = new StringFieldEditor("", "", StringFieldEditor.UNLIMITED, composite	);
//		String text = getPreferenceStore().getString(ContextUiPrefContstants.INTEREST_FILTER_EXCLUSION);
//		if (text != null)
//			exclusionFieldEditor.setStringValue(text);
//		return;
//	}

	private void createUiManagementSection(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);

		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("UI Management");

		managePerspectivesButton = new Button(group, SWT.CHECK);
		managePerspectivesButton.setText("Open last used perspective on task activation");
		managePerspectivesButton.setSelection(getPreferenceStore().getBoolean(
				ContextUiPrefContstants.AUTO_MANAGE_PERSPECTIVES));

		autoEnableExplorerFilter = new Button(group, SWT.CHECK);
		autoEnableExplorerFilter.setText("Auto toggle Focus on navigator views (Recommended)");
		autoEnableExplorerFilter.setSelection(getPreferenceStore().getBoolean(
				ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE));

		manageEditorsButton = new Button(group, SWT.CHECK);
		manageEditorsButton.setText("Manage open editors with task context (Recommended)");
		manageEditorsButton.setSelection(getPreferenceStore().getBoolean(ContextUiPrefContstants.AUTO_MANAGE_EDITORS));

		String prefName = WorkbenchMessages.WorkbenchPreference_reuseEditors;
		if (getContainer() instanceof IWorkbenchPreferenceContainer) {
			String message = "<a>''{0}''</a> \"" + prefName + "\" will turn off when task activated";
			new PreferenceLinkArea(group, SWT.NONE, "org.eclipse.ui.preferencePages.Editors", message,
					(IWorkbenchPreferenceContainer) getContainer(), null);
		}

		manageExpansionButton = new Button(group, SWT.CHECK);
		manageExpansionButton.setText("Automatically maintain expansion of focused tree views (Recommended)");
		manageExpansionButton.setSelection(getPreferenceStore().getBoolean(
				ContextUiPrefContstants.AUTO_MANAGE_EXPANSION));

		return;
	}
}
