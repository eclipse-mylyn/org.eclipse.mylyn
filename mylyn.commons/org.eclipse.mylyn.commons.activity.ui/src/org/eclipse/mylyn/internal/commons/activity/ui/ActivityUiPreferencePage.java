/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.activity.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class ActivityUiPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final int MS_MINUTES = 60 * 1000;

	private Spinner timeoutMinutes;

	private Button timeoutEnabledButton;

	private Button activityTrackingEnabledButton;

	private Label timeoutLabel1;

	private Label timeoutLabel2;

	public ActivityUiPreferencePage() {
		setPreferenceStore(ActivityUiPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createTaskActivityGroup(container);

		updateControls();
		applyDialogFont(container);
		return container;
	}

	public void init(IWorkbench workbench) {
		// ignore
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(IActivityUiConstants.ACTIVITY_TIMEOUT_ENABLED,
				timeoutEnabledButton.getSelection());
		getPreferenceStore().setValue(IActivityUiConstants.ACTIVITY_TIMEOUT,
				timeoutMinutes.getSelection() * (60 * 1000));
		getPreferenceStore().setValue(IActivityUiConstants.ACTIVITY_TRACKING_ENABLED,
				activityTrackingEnabledButton.getSelection());
		return true;
	}

	@Override
	public boolean performCancel() {
		int minutes = getPreferenceStore().getInt(IActivityUiConstants.ACTIVITY_TIMEOUT) / MS_MINUTES;
		timeoutMinutes.setSelection(minutes);
		timeoutEnabledButton.setSelection(getPreferenceStore().getBoolean(IActivityUiConstants.ACTIVITY_TIMEOUT_ENABLED));
		activityTrackingEnabledButton.setSelection(getPreferenceStore().getBoolean(
				IActivityUiConstants.ACTIVITY_TRACKING_ENABLED));
		return true;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		int activityTimeoutMinutes = getPreferenceStore().getDefaultInt(IActivityUiConstants.ACTIVITY_TIMEOUT)
				/ MS_MINUTES;
		timeoutMinutes.setSelection(activityTimeoutMinutes);
		timeoutEnabledButton.setSelection(getPreferenceStore().getDefaultBoolean(
				IActivityUiConstants.ACTIVITY_TIMEOUT_ENABLED));

		activityTrackingEnabledButton.setSelection(getPreferenceStore().getDefaultBoolean(
				IActivityUiConstants.ACTIVITY_TRACKING_ENABLED));
	}

	private Group createTaskActivityGroup(Composite container) {
		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.TasksUiPreferencePage_Task_Timing);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		boolean activityTrackingEnabled = getPreferenceStore().getBoolean(
				IActivityUiConstants.ACTIVITY_TRACKING_ENABLED);

		boolean timeoutEnabled = getPreferenceStore().getBoolean(IActivityUiConstants.ACTIVITY_TIMEOUT_ENABLED);

		activityTrackingEnabledButton = new Button(group, SWT.CHECK);
		activityTrackingEnabledButton.setText(Messages.TasksUiPreferencePage_Enable_Time_Tracking);
		activityTrackingEnabledButton.setSelection(activityTrackingEnabled);
		activityTrackingEnabledButton.setToolTipText(Messages.TasksUiPreferencePage_Track_Time_Spent);
		activityTrackingEnabledButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateControls();
			}
		});
		GridDataFactory.swtDefaults().span(3, 1).applyTo(activityTrackingEnabledButton);

		timeoutEnabledButton = new Button(group, SWT.CHECK);
		timeoutEnabledButton.setText(Messages.TasksUiPreferencePage_Enable_inactivity_timeouts);
		timeoutEnabledButton.setSelection(timeoutEnabled);
		timeoutEnabledButton.setToolTipText(Messages.TasksUiPreferencePage_If_disabled);
		timeoutEnabledButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateControls();
			}
		});
		GridDataFactory.swtDefaults().span(3, 1).applyTo(timeoutEnabledButton);

		timeoutLabel1 = new Label(group, SWT.NONE);
		timeoutLabel1.setText(Messages.TasksUiPreferencePage_Stop_time_accumulation_after);
		timeoutMinutes = new Spinner(group, SWT.BORDER);
		timeoutMinutes.setDigits(0);
		timeoutMinutes.setIncrement(5);
		timeoutMinutes.setMaximum(60);
		timeoutMinutes.setMinimum(1);
		long minutes = getPreferenceStore().getLong(IActivityUiConstants.ACTIVITY_TIMEOUT) / MS_MINUTES;
		timeoutMinutes.setSelection((int) minutes);
		timeoutMinutes.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateControls();
			}

		});

		timeoutLabel2 = new Label(group, SWT.NONE);
		timeoutLabel2.setText(Messages.TasksUiPreferencePage_minutes_of_inactivity);

		return group;
	}

	public void updateControls() {
		if (activityTrackingEnabledButton.getSelection()) {
			timeoutEnabledButton.setEnabled(true);
			timeoutMinutes.setEnabled(timeoutEnabledButton.getSelection());
			timeoutLabel1.setEnabled(timeoutEnabledButton.getSelection());
			timeoutLabel2.setEnabled(timeoutEnabledButton.getSelection());
		} else {
			timeoutEnabledButton.setEnabled(false);
			timeoutMinutes.setEnabled(false);
			timeoutLabel1.setEnabled(false);
			timeoutLabel2.setEnabled(false);
		}
	}

}
