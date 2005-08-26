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
package org.eclipse.mylar.java.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.java.MylarJavaPlugin;
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
 * @author Ken Sueda and Mik Kersten
 */
public class MylarJavaPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Button autoEnableExplorerFilter = null;

	public MylarJavaPreferencePage() {
		super();
		setPreferenceStore(MylarJavaPlugin.getDefault().getPreferenceStore());	
	}
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout (layout);
		
		createUserbooleanControl(container);
		return container;
	}

	public void init(IWorkbench workbench) {
		// ignore
	}

	private void createUserbooleanControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		container.setLayoutData(gridData);
		GridLayout gl = new GridLayout(1, false);
		container.setLayout(gl);		

		Group group = new Group(container, SWT.SHADOW_ETCHED_IN);		
		group.setLayout(new RowLayout());
		group.setText("Package Explorer");
		
		autoEnableExplorerFilter = new Button(group, SWT.CHECK);
		autoEnableExplorerFilter.setText("Automatically toggle interest filter on task activation/deactivation.");
		autoEnableExplorerFilter.setSelection(getPreferenceStore().getBoolean(MylarJavaPlugin.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE));
	}
	
	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(MylarJavaPlugin.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE, autoEnableExplorerFilter.getSelection());
		return true;
	}
	@Override
	public boolean performCancel() {
		autoEnableExplorerFilter.setSelection(getPreferenceStore().getBoolean(MylarJavaPlugin.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE));
		return true;
	}
	
	public void performDefaults() {
		super.performDefaults();
		autoEnableExplorerFilter.setSelection(getPreferenceStore().getDefaultBoolean(MylarJavaPlugin.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE));
	}
}
