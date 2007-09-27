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

	private Button autoFocusNavigatorsButton = null;

	private Button manageEditorsButton = null;

	private Button mapCloseToRemoveButton = null;
	
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
		setDescription("Configure the Task-Focused UI management and automation.");
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
				autoFocusNavigatorsButton.getSelection());

		getPreferenceStore().setValue(ContextUiPrefContstants.HIGHLIGHTER_PREFIX,
				ContextUiPlugin.getDefault().getHighlighterList().externalizeToString());
//		getPreferenceStore().setValue(ContextUiPrefContstants.INTEREST_FILTER_EXCLUSION,
//				exclusionFieldEditor.getStringValue());

		getPreferenceStore().setValue(ContextUiPrefContstants.AUTO_MANAGE_EDITORS, manageEditorsButton.getSelection());
		getPreferenceStore().setValue(ContextUiPrefContstants.AUTO_MANAGE_PERSPECTIVES,
				managePerspectivesButton.getSelection());
		getPreferenceStore().setValue(ContextUiPrefContstants.AUTO_MANAGE_EXPANSION,
				manageExpansionButton.getSelection());
		getPreferenceStore().setValue(ContextUiPrefContstants.AUTO_MANAGE_EDITOR_CLOSE_ACTION,
				mapCloseToRemoveButton.getSelection());
		
		return true;
	}

	/**
	 * Handle Cancel Undo all changes back to what is stored in preference store
	 */
	@Override
	public boolean performCancel() {
		autoFocusNavigatorsButton.setSelection(getPreferenceStore().getBoolean(
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
		
		// NOTE: duplicated policy from ContextUiPlugin.initializeDefaultPreferences
		autoFocusNavigatorsButton.setSelection(true);
		manageEditorsButton.setSelection(true);
		manageExpansionButton.setSelection(true);
		
		managePerspectivesButton.setSelection(false);
		mapCloseToRemoveButton.setSelection(false);
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
		Group groupViews = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupViews.setLayout(new GridLayout(1, false));
		groupViews.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupViews.setText("Views");

		autoFocusNavigatorsButton = new Button(groupViews, SWT.CHECK);
		autoFocusNavigatorsButton.setText("Auto focus navigator views on task activation");
		autoFocusNavigatorsButton.setSelection(getPreferenceStore().getBoolean(
				ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE));
		
		manageExpansionButton = new Button(groupViews, SWT.CHECK);
		manageExpansionButton.setText("Auto expand tree views when focused");
		manageExpansionButton.setSelection(getPreferenceStore().getBoolean(
				ContextUiPrefContstants.AUTO_MANAGE_EXPANSION));

		
		Group groupEditors = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupEditors.setLayout(new GridLayout(1, false));
		groupEditors.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupEditors.setText("Editors");
		
		manageEditorsButton = new Button(groupEditors, SWT.CHECK);
		manageEditorsButton.setText("Manage open editors to match task context");
		manageEditorsButton.setSelection(getPreferenceStore().getBoolean(ContextUiPrefContstants.AUTO_MANAGE_EDITORS));
		
		String prefName = WorkbenchMessages.WorkbenchPreference_reuseEditors;
		if (getContainer() instanceof IWorkbenchPreferenceContainer) {
			String message = "<a>''{0}''</a> \"" + prefName + "\" will be toggled with activation";
			new PreferenceLinkArea(groupEditors, SWT.NONE, "org.eclipse.ui.preferencePages.Editors", message,
					(IWorkbenchPreferenceContainer) getContainer(), null);
		}
		
		mapCloseToRemoveButton = new Button(groupEditors, SWT.CHECK);
		mapCloseToRemoveButton.setText("Remove file from context when editor is closed");
		mapCloseToRemoveButton.setSelection(getPreferenceStore().getBoolean(
				ContextUiPrefContstants.AUTO_MANAGE_EDITOR_CLOSE_ACTION));
		
		
		Group groupPerspectives = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupPerspectives.setLayout(new GridLayout(1, false));
		groupPerspectives.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupPerspectives.setText("Perspectives");
		
		managePerspectivesButton = new Button(groupPerspectives, SWT.CHECK);
		managePerspectivesButton.setText("Open last used perspective on task activation");
		managePerspectivesButton.setSelection(getPreferenceStore().getBoolean(
				ContextUiPrefContstants.AUTO_MANAGE_PERSPECTIVES));

		return;
	}
}
