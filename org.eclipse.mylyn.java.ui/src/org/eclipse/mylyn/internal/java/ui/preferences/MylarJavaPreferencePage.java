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

package org.eclipse.mylar.internal.java.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.internal.java.MylarJavaPlugin;
import org.eclipse.mylar.internal.java.MylarJavaPrefConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Ken Sueda and Mik Kersten
 */
public class MylarJavaPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button autoEnableExplorerFilter = null;

	private Button enableErrorInterest = null;

	public MylarJavaPreferencePage() {
		super();
		setPreferenceStore(MylarJavaPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createUserbooleanControl(container);
		return container;
	}

	public void init(IWorkbench workbench) {
		// ignore
	}

	private void createUserbooleanControl(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Package Explorer");
		GridLayout gl = new GridLayout(1, false);
		group.setLayout(gl);

		autoEnableExplorerFilter = new Button(group, SWT.CHECK);
		autoEnableExplorerFilter.setText("Automatically toggle interest filter on task activation/deactivation.");
		autoEnableExplorerFilter.setSelection(getPreferenceStore().getBoolean(
				MylarJavaPrefConstants.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE));

		enableErrorInterest = new Button(group, SWT.CHECK);
		enableErrorInterest.setText("Enable predicted interest of errors (significantly increases view refresh).");
		enableErrorInterest.setSelection(getPreferenceStore().getBoolean(
				MylarJavaPrefConstants.PREDICTED_INTEREST_ERRORS));
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(MylarJavaPrefConstants.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE,
				autoEnableExplorerFilter.getSelection());
		getPreferenceStore().setValue(MylarJavaPrefConstants.PREDICTED_INTEREST_ERRORS,
				enableErrorInterest.getSelection());
		return true;
	}

	@Override
	public boolean performCancel() {
		autoEnableExplorerFilter.setSelection(getPreferenceStore().getBoolean(
				MylarJavaPrefConstants.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE));
		enableErrorInterest.setSelection(getPreferenceStore().getBoolean(
				MylarJavaPrefConstants.PREDICTED_INTEREST_ERRORS));
		return true;
	}

	public void performDefaults() {
		super.performDefaults();
		enableErrorInterest.setSelection(getPreferenceStore().getDefaultBoolean(
				MylarJavaPrefConstants.PREDICTED_INTEREST_ERRORS));
	}
}
