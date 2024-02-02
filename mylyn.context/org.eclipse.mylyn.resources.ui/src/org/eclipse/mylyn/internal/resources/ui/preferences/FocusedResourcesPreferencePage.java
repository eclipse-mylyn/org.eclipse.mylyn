/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiExtensionPointReader;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Mik Kersten
 */
public class FocusedResourcesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final Color GRAY = new Color(Display.getDefault(), 100, 100, 100);

	private static final String LABEL_AUTOMATIC = Messages.FocusedResourcesPreferencePage__automatic_;

	private Table ignoreTable;

	private Button addButton;

	private Button removeButton;

	private Button resourceMonitoringButton;

	private Group monitoringExclusionsGroup;

	@Override
	public void init(IWorkbench workbench) {
		setDescription(Messages.FocusedResourcesPreferencePage_Configure_file_change_monitoring_Description);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return ResourcesUiBridgePlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);

		resourceMonitoringButton = new Button(composite, SWT.CHECK | SWT.WRAP);
		resourceMonitoringButton.setText(Messages.FocusedResourcesPreferencePage__Enable_file_change_monitoring_Label);
		boolean resourceModificationsEnabled = getPreferenceStore()
				.getBoolean(ResourcesUiPreferenceInitializer.PREF_RESOURCE_MONITOR_ENABLED);
		resourceMonitoringButton.setSelection(resourceModificationsEnabled);
		resourceMonitoringButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		resourceMonitoringButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}
		});

		createExcludesTable(composite);

		updateEnablement();

		Dialog.applyDialogFont(composite);
		return composite;
	}

	private void updateEnablement() {
		boolean enabled = resourceMonitoringButton.getSelection();
		if (enabled != monitoringExclusionsGroup.isEnabled()) {
			CommonUiUtil.setEnabled(monitoringExclusionsGroup, enabled);
		}
		if (enabled) {
			if (ignoreTable.getSelectionCount() > 0) {
				removeButton.setEnabled(true);
			} else {
				removeButton.setEnabled(false);
			}
		}
	}

	private void createExcludesTable(Composite parent) {
		monitoringExclusionsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		monitoringExclusionsGroup.setText(Messages.FocusedResourcesPreferencePage_Resource_Monitoring_Exclusions);
		GridLayout layout = new GridLayout(1, false);
		monitoringExclusionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		monitoringExclusionsGroup.setLayout(layout);

		Composite composite = new Composite(monitoringExclusionsGroup, SWT.NULL);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

		Label l1 = new Label(composite, SWT.NULL);
		l1.setText(
				Messages.FocusedResourcesPreferencePage_Matching_file_or_directory_names_will_not_be_added_automatically_to_the_context);
		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		l1.setLayoutData(data);

		ignoreTable = new Table(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200).applyTo(ignoreTable);
		ignoreTable.addListener(SWT.Selection, e -> updateEnablement());

		Composite buttons = new Composite(composite, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		addButton = new Button(buttons, SWT.PUSH);
		addButton.setText(Messages.FocusedResourcesPreferencePage_Add_);
		addButton.addListener(SWT.Selection, e -> addIgnore());

		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText(Messages.FocusedResourcesPreferencePage_Remove);
		removeButton.setEnabled(false);
		removeButton.addListener(SWT.Selection, e -> removeIgnore());
		fillTable(ResourcesUiPreferenceInitializer.getExcludedResourcePatterns(),
				ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns());
		setButtonLayoutData(addButton);
		setButtonLayoutData(removeButton);
	}

	/**
	 * Do anything necessary because the OK button has been pressed.
	 * 
	 * @return whether it is okay to close the preference page
	 */
	@Override
	public boolean performOk() {
		ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.setValue(ResourcesUiPreferenceInitializer.PREF_RESOURCE_MONITOR_ENABLED,
						resourceMonitoringButton.getSelection());

		Set<String> patterns = new HashSet<>();
		TableItem[] items = ignoreTable.getItems();
		for (TableItem item : items) {
			if (!item.getText().endsWith(LABEL_AUTOMATIC)) {
				patterns.add(item.getText());
			}
		}
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(patterns);
		return true;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		boolean resourceModificationsEnabled = ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(ResourcesUiPreferenceInitializer.PREF_RESOURCE_MONITOR_ENABLED);
		resourceMonitoringButton.setSelection(resourceModificationsEnabled);
		ignoreTable.removeAll();
		fillTable(ResourcesUiExtensionPointReader.getDefaultResourceExclusions(),
				ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns());
	}

	/**
	 * @param ignore
	 */
	private void fillTable(Set<String> ignored, Set<String> forced) {
		for (String pattern : ignored) {
			TableItem item = new TableItem(ignoreTable, SWT.NONE);
			item.setText(pattern);
		}
		for (String pattern : forced) {
			TableItem item = new TableItem(ignoreTable, SWT.NONE);
			item.setText(pattern + " " + LABEL_AUTOMATIC); //$NON-NLS-1$
			item.setForeground(GRAY);
		}
	}

	private void addIgnore() {
		InputDialog dialog = new InputDialog(getShell(), Messages.FocusedResourcesPreferencePage_Add__IGNORED_RESOURCE,
				Messages.FocusedResourcesPreferencePage_Enter_pattern_____any_string_, null, null); //
		dialog.open();
		if (dialog.getReturnCode() != Window.OK) {
			return;
		}
		String pattern = dialog.getValue();
		if (pattern.equals("")) { //$NON-NLS-1$
			return;
		}
		TableItem item = new TableItem(ignoreTable, SWT.NONE);
		item.setText(pattern);
		item.setChecked(true);
	}

	private void removeIgnore() {
		int[] selection = ignoreTable.getSelectionIndices();
		ignoreTable.remove(selection);
		updateEnablement();
	}

}
