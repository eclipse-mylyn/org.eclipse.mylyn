/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Steffen Pingel
 */
public class BuildsPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final long MILLIS_PER_MINUTE = 60 * 1000;

	private Text intervalText;

	private Button enableRefreshButton;

	private Label intervalLabel;

	public BuildsPreferencesPage() {
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return BuildsUiPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		enableRefreshButton = new Button(composite, SWT.CHECK);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(enableRefreshButton);
		enableRefreshButton.setText("&Automatically refresh builds");
		enableRefreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}
		});

		intervalLabel = new Label(composite, SWT.NONE);
		intervalLabel.setText("&Refresh build status every (in minutes):");

		intervalText = new Text(composite, SWT.BORDER | SWT.RIGHT);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(intervalText);
		intervalText.setTextLimit(3);

		reset();
		Dialog.applyDialogFont(composite);
		return composite;
	}

	private void updateEnablement() {
		intervalLabel.setEnabled(enableRefreshButton.getSelection());
		intervalText.setEnabled(enableRefreshButton.getSelection());
	}

	public void init(IWorkbench workbench) {
		// ignore
	}

	public void reset() {
		intervalText.setText(String.valueOf(getPreferenceStore().getLong(BuildsUiInternal.PREF_AUTO_REFRESH_INTERVAL)
				/ MILLIS_PER_MINUTE));
		enableRefreshButton.setSelection(getPreferenceStore().getBoolean(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED));
		updateEnablement();
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED, enableRefreshButton.getSelection());
		getPreferenceStore().setValue(BuildsUiInternal.PREF_AUTO_REFRESH_INTERVAL, getRefreshInterval());
		return super.performOk();
	}

	private long getRefreshInterval() {
		try {
			return Math.max(Integer.parseInt(intervalText.getText()) * MILLIS_PER_MINUTE,
					BuildsUiInternal.MIN_REFRESH_INTERVAL);
		} catch (NumberFormatException e) {
			//ignore
		}
		return BuildsUiInternal.DEFAULT_REFRESH_INTERVAL;
	}

	@Override
	protected void performDefaults() {
		intervalText.setText(String.valueOf(getPreferenceStore().getDefaultLong(
				BuildsUiInternal.PREF_AUTO_REFRESH_INTERVAL)
				/ MILLIS_PER_MINUTE));
		enableRefreshButton.setSelection(getPreferenceStore().getDefaultBoolean(
				BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED));
		updateEnablement();
	}

}
