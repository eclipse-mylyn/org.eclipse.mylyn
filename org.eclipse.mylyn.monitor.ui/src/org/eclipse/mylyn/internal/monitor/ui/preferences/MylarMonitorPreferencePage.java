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

package org.eclipse.mylar.internal.monitor.ui.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylar.internal.core.MylarPreferenceContstants;
import org.eclipse.mylar.internal.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.internal.monitor.MylarMonitorPreferenceConstants;
import org.eclipse.mylar.internal.monitor.HandleObfuscator;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class MylarMonitorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String DESCRIPTION = "If enabled the Mylar monitor logs selections, edits, commands, and preference changes. "
			+ "If you would like to help improve the user experience by anonymously sharing non-private "
			+ "parts of this data, run the Usage Feedback Wizard.";

	private IntegerFieldEditor userStudyId;

	private Button enableMonitoring;
	private Button enableObfuscation;

	private Text logFileText;

	private Text uploadUrl;

	public MylarMonitorPreferencePage() {
		super();
		setPreferenceStore(MylarMonitorPlugin.getPrefs());
		setDescription(DESCRIPTION);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		if (MylarMonitorPlugin.getDefault().getCustomizingPlugin() != null) {
			Label label = new Label(parent, SWT.NULL);
			label.setText(MylarMonitorPlugin.getDefault().getCustomizedByMessage());
			label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		}

		createLogFileSection(container);
		createUsageSection(container);
		updateEnablement();
		return container;
	}

	public void init(IWorkbench workbench) {
		// Nothing to init
	}

	private void updateEnablement() {
		if (!enableMonitoring.getSelection()) {
			logFileText.setEnabled(false);
		} else {
			logFileText.setEnabled(true);
		}
	}

	private void createLogFileSection(Composite parent) {
		final Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Monitoring");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		enableMonitoring = new Button(group, SWT.CHECK);
		enableMonitoring.setText("Enable logging to: ");
		enableMonitoring.setSelection(getPreferenceStore().getBoolean(MylarMonitorPreferenceConstants.PREF_MONITORING_ENABLED));
		enableMonitoring.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});

		// Label label = new Label(group, SWT.NULL);
		// label.setText("");

		String logFilePath = MylarMonitorPlugin.getDefault().getMonitorLogFile().getPath();
		logFilePath = logFilePath.replaceAll("\\\\", "/");
		logFileText = new Text(group, SWT.BORDER);
		logFileText.setText(logFilePath);
		logFileText.setEditable(false);
		logFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Button browse = createButton(group, "Browse...");
		// browse.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// FileDialog dialog = new FileDialog(getShell());
		// dialog.setText("Folder Selection");
		// // dialog.setMessage("Specify the monitor log file");
		// String dir = logFileText.getText();
		// dir = dir.replaceAll("\\\\", "/");
		// dialog.setFilterPath(dir);
		//
		// dir = dialog.open();
		// if(dir == null || dir.equals(""))
		// return;
		// logFileText.setText(dir);
		// }
		// });

		enableObfuscation = new Button(group, SWT.CHECK);
		enableObfuscation.setText("Obfuscate elements using: ");
		enableObfuscation.setSelection(getPreferenceStore().getBoolean(MylarMonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE));
		Label obfuscationLablel = new Label(group, SWT.NULL);
		obfuscationLablel.setText(HandleObfuscator.ENCRYPTION_ALGORITHM + " message digest one-way hash");
	}

	private void createUsageSection(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Usage Feedback");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		userStudyId = new IntegerFieldEditor("", " Feedback User ID:", group); // HACK
		userStudyId.setErrorMessage("Your user id must be an integer");
		int uidNum = MylarPlugin.getDefault().getPreferenceStore().getInt(MylarPreferenceContstants.USER_ID);
		if (uidNum > 0) {
			userStudyId.setStringValue(uidNum + "");
			userStudyId.setEmptyStringAllowed(false);
		}

		Label label = new Label(group, SWT.NULL);
		label.setText(" Upload URL: ");
		uploadUrl = new Text(group, SWT.BORDER);
		uploadUrl.setEditable(false);
		uploadUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		uploadUrl.setText(MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUrl());

		Label events = new Label(group, SWT.NULL);
		events.setText(" Events since upload:");
		Label logged = new Label(group, SWT.NULL);
		logged.setText("" + getPreferenceStore().getInt(MylarMonitorPreferenceConstants.PREF_NUM_USER_EVENTS));

		if (uidNum <= 0) {
			userStudyId.setEnabled(false, group);
			uploadUrl.setEnabled(false);
			label.setEnabled(false);
			logged.setEnabled(false);
			events.setEnabled(false);
		}
	}

	public void performDefaults() {
		super.performDefaults();
		logFileText.setText(MylarMonitorPlugin.getDefault().getMonitorLogFile().getPath());
		// logFileText.setText(getPreferenceStore().getDefaultString(MylarMonitorPlugin.PREF_LOG_FILE));
	}

	@Override
	public boolean performOk() {
		MylarMonitorPlugin.getPrefs()
			.setValue(MylarMonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE, enableObfuscation.getSelection());
		if (enableMonitoring.getSelection()) {
			MylarMonitorPlugin.getDefault().startMonitoring();
		} else {
			MylarMonitorPlugin.getDefault().stopMonitoring();
		}

		// String taskDirectory = logFileText.getText();
		// taskDirectory = taskDirectory.replaceAll("\\\\", "/");
		// getPreferenceStore().setValue(MylarPlugin.PREF_DATA_DIR,
		// taskDirectory);

		int uidNum = -1;
		try {
			if (userStudyId.getStringValue() == null || userStudyId.getStringValue().equals("")) {
				uidNum = -1;
				userStudyId.setStringValue(uidNum + "");
			} else {
				uidNum = userStudyId.getIntValue();
			}

			if (uidNum <= 0 && uidNum != -1) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "User ID Incorrect",
						"The user study id must be a posative integer");
				return false;
			}
			if (uidNum != -1 && uidNum % 17 != 1) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "User ID Incorrect",
						"Your user study id is not valid, please make sure it is correct or get a new id");
				return false;
			}
		} catch (NumberFormatException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "User ID Incorrect",
					"The user study id must be a posative integer");
			return false;
		}
		MylarPlugin.getDefault().getPreferenceStore().setValue(MylarPreferenceContstants.USER_ID, uidNum);
		return true;
	}

	@Override
	public boolean performCancel() {
		enableMonitoring.setSelection(getPreferenceStore().getBoolean(MylarMonitorPreferenceConstants.PREF_MONITORING_ENABLED));
		enableObfuscation.setSelection(getPreferenceStore().getBoolean(MylarMonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE));
		userStudyId.setStringValue(MylarPlugin.getDefault().getPreferenceStore().getInt(MylarPreferenceContstants.USER_ID)
				+ "");
		return true;
	}

	// private Button createButton(Composite parent, String text) {
	// Button button = new Button(parent, SWT.TRAIL);
	// button.setText(text);
	// button.setVisible(true);
	// return button;
	// }
}
