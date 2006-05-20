/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 * @author Rob Elves
 */
public class MylarRepositoriesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button reportEditor;

	private Button disableInternal;
	
	private Button reportInternal;

	private Text synchScheduleTime = null;

	private Button enableBackgroundSynch;

	private Button synchQueries = null;

	private Button userRefreshOnly;

	public MylarRepositoriesPreferencePage() {
		super();
		setPreferenceStore(MylarTaskListPlugin.getMylarCorePrefs());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createBugzillaReportOption(container);
		createTaskRefreshScheduleGroup(container);
		updateRefreshGroupEnablements();
		return container;
	}

	public void init(IWorkbench workbench) {
		// ignore

	}

	private void createBugzillaReportOption(Composite parent) {
		Group container = new Group(parent, SWT.SHADOW_ETCHED_IN);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		container.setText("Open Repository Tasks with");
		reportEditor = new Button(container, SWT.RADIO);
		reportEditor.setText("Editor if available (Recommended)");
		reportEditor.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal = new Button(container, SWT.RADIO);
		reportInternal.setText("Internal browser");
		reportInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		disableInternal = new Button(container, SWT.CHECK);
		disableInternal.setText("Disable internal browser");
		disableInternal.setEnabled(!reportInternal.getSelection());
		disableInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL));
		reportInternal.addListener(SWT.Selection, new Listener() {
		
			public void handleEvent(Event event) {
				if(reportInternal.getSelection())
				{
					disableInternal.setSelection(false);
					disableInternal.setEnabled(false);
				}
				else
				{
					disableInternal.setEnabled(true);
				}
			}
		
		});
		// reportExternal = new Button(container, SWT.RADIO);
		// reportExternal.setText("External browser");
		// reportExternal.setSelection(getPreferenceStore().getBoolean(MylarTaskListPlugin.REPORT_OPEN_EXTERNAL));
		// reportExternal.setEnabled(false);
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_EDITOR, reportEditor.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL, reportInternal.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL, disableInternal.getSelection());

		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP,
				synchQueries.getSelection());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED,
				enableBackgroundSynch.getSelection());
		long miliseconds = 60000 * Long.parseLong(synchScheduleTime.getText());
		getPreferenceStore().setValue(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS,
				"" + miliseconds);
		return super.performOk();
	}

	@Override
	public boolean performCancel() {
		reportEditor.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		disableInternal.setSelection(getPreferenceStore().getBoolean(TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL));
		synchQueries.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		enableBackgroundSynch.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		synchScheduleTime.setText(getMinutesString());
		return super.performCancel();
	}

	@Override
	protected void performDefaults() {
		reportEditor.setSelection(getPreferenceStore()
				.getDefaultBoolean(TaskListPreferenceConstants.REPORT_OPEN_EDITOR));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPORT_OPEN_INTERNAL));
		reportInternal.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPORT_DISABLE_INTERNAL));

		synchQueries.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));
		enableBackgroundSynch.setSelection(getPreferenceStore().getDefaultBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
		userRefreshOnly.setSelection(!enableBackgroundSynch.getSelection());
		long miliseconds = getPreferenceStore().getDefaultLong(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		synchScheduleTime.setText("" + minutes);
		super.performDefaults();
	}

	// reference:
	// org.eclipse.team.internal.ui.synchronize.ConfigureSynchronizeScheduleComposite
	private void createTaskRefreshScheduleGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Repository Synchronization");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		{
			userRefreshOnly = new Button(group, SWT.RADIO);
			final GridData gridData = new GridData();
			gridData.horizontalSpan = 2;
			userRefreshOnly.setLayoutData(gridData);
			userRefreshOnly.setText("No background synchronization");
			userRefreshOnly.setSelection(!getPreferenceStore().getBoolean(
					TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
			userRefreshOnly.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					updateRefreshGroupEnablements();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

		}
		{
			enableBackgroundSynch = new Button(group, SWT.RADIO);
			final GridData gridData = new GridData();
			gridData.horizontalSpan = 2;
			enableBackgroundSynch.setLayoutData(gridData);
			enableBackgroundSynch.setText("Use the following synchronization schedule");
			enableBackgroundSynch.setSelection(getPreferenceStore().getBoolean(
					TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
			enableBackgroundSynch.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					updateRefreshGroupEnablements();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

		}
		final Composite composite = new Composite(group, SWT.NONE);
		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		composite.setLayoutData(gridData);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 3;
		composite.setLayout(gridLayout_1);
		{
			final Label label = new Label(composite, SWT.NONE);
			label.setText("Every: ");
		}
		{
			synchScheduleTime = new Text(composite, SWT.BORDER | SWT.RIGHT);
			final GridData gridData_1 = new GridData();
			gridData_1.widthHint = 35;
			synchScheduleTime.setLayoutData(gridData_1);

			synchScheduleTime.setText(getMinutesString());
			synchScheduleTime.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					updateRefreshGroupEnablements();
				}
			});

		}
		{
			final Label label = new Label(composite, SWT.NONE);
			label.setText("minutes");
		}

		synchQueries = new Button(group, SWT.CHECK);
		synchQueries.setText("Automatically perform a synchronization on startup");
		synchQueries.setSelection(getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP));

	}

	public void updateRefreshGroupEnablements() {
		if (enableBackgroundSynch.getSelection()) {
			try {
				long number = Long.parseLong(synchScheduleTime.getText());
				if (number <= 0) {
					this.setErrorMessage("Refresh schedule time must be > 0");
					this.setValid(false);
				} else {
					this.setErrorMessage(null);
					this.setValid(true);
				}
			} catch (NumberFormatException e) {
				this.setErrorMessage("Refresh schedule time must be valid integer");
				this.setValid(false);
			}
		} else {
			this.setValid(true);
			this.setErrorMessage(null);
		}
		synchScheduleTime.setEnabled(enableBackgroundSynch.getSelection());
	}

	private String getMinutesString() {
		long miliseconds = getPreferenceStore().getLong(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
		long minutes = miliseconds / 60000;
		return "" + minutes;
	}

}
