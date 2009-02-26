/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
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
		setTitle(Messages.ContextUiPreferencePage_Context);
		setDescription(Messages.ContextUiPreferencePage_CONFIGURE_TASK_FOCUSED_UI_MANAGEMENT_AND_AUTOMATION);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite entryTable = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 4;
		entryTable.setLayout(layout);

		createUiManagementSection(entryTable);
//		createExclusionFilterControl(entryTable);

		applyDialogFont(entryTable);
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
		getPreferenceStore().setValue(IContextUiPreferenceContstants.AUTO_FOCUS_NAVIGATORS,
				autoFocusNavigatorsButton.getSelection());

		getPreferenceStore().setValue(IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS,
				manageEditorsButton.getSelection());
		getPreferenceStore().setValue(IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES,
				managePerspectivesButton.getSelection());
		getPreferenceStore().setValue(IContextUiPreferenceContstants.AUTO_MANAGE_EXPANSION,
				manageExpansionButton.getSelection());
		getPreferenceStore().setValue(IContextUiPreferenceContstants.AUTO_MANAGE_EDITOR_CLOSE,
				mapCloseToRemoveButton.getSelection());

		TasksUiPlugin.getDefault().getPreferenceStore().setValue(ITasksUiPreferenceConstants.AUTO_EXPAND_TASK_LIST,
				manageExpansionButton.getSelection());

		return true;
	}

	/**
	 * Handle Cancel Undo all changes back to what is stored in preference store
	 */
	@Override
	public boolean performCancel() {
		autoFocusNavigatorsButton.setSelection(getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_FOCUS_NAVIGATORS));
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

	private void createUiManagementSection(Composite parent) {
		Group groupViews = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupViews.setLayout(new GridLayout(1, false));
		groupViews.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupViews.setText(Messages.ContextUiPreferencePage_Views);

		autoFocusNavigatorsButton = new Button(groupViews, SWT.CHECK);
		autoFocusNavigatorsButton.setText(Messages.ContextUiPreferencePage_Auto_focus_navigator_views_on_task_activation);
		autoFocusNavigatorsButton.setSelection(getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_FOCUS_NAVIGATORS));

		manageExpansionButton = new Button(groupViews, SWT.CHECK);
		manageExpansionButton.setText(Messages.ContextUiPreferencePage_Auto_expand_tree_views_when_focused);
		manageExpansionButton.setSelection(getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_MANAGE_EXPANSION));

		Group groupEditors = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupEditors.setLayout(new GridLayout(1, false));
		groupEditors.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupEditors.setText(Messages.ContextUiPreferencePage_Editors);

		manageEditorsButton = new Button(groupEditors, SWT.CHECK);
		manageEditorsButton.setText(Messages.ContextUiPreferencePage_Manage_open_editors_to_match_task_context);
		manageEditorsButton.setSelection(getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS));

		if (getContainer() instanceof IWorkbenchPreferenceContainer) {
			String message = "<a>''{0}''</a> " + Messages.ContextUiPreferencePage_will_be_toggled_with_activation; //$NON-NLS-1$
			new PreferenceLinkArea(groupEditors, SWT.NONE, "org.eclipse.ui.preferencePages.Editors", message, //$NON-NLS-1$
					(IWorkbenchPreferenceContainer) getContainer(), null);
		}

		mapCloseToRemoveButton = new Button(groupEditors, SWT.CHECK);
		mapCloseToRemoveButton.setText(Messages.ContextUiPreferencePage_Remove_file_from_context_when_editor_is_closed);
		mapCloseToRemoveButton.setSelection(getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_MANAGE_EDITOR_CLOSE));

		Group groupPerspectives = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupPerspectives.setLayout(new GridLayout(1, false));
		groupPerspectives.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupPerspectives.setText(Messages.ContextUiPreferencePage_Perspectives);

		managePerspectivesButton = new Button(groupPerspectives, SWT.CHECK);
		managePerspectivesButton.setText(Messages.ContextUiPreferencePage_Open_last_used_perspective_on_task_activation);
		managePerspectivesButton.setSelection(getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES));

		return;
	}
}
