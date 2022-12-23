/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class BreakpointsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button manageBreakpointsButton;

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);

		Label infoImage = new Label(composite, SWT.NONE);
		infoImage.setImage(CommonImages.getImage(CommonImages.INFORMATION));
		Link bugLink = new Link(composite, SWT.NONE);
		bugLink.setText(Messages.BreakpointsPreferencePage_bug_link);
		bugLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BrowserUtil.openUrl("https://bugs.eclipse.org/bugs/show_bug.cgi?id=428378"); //$NON-NLS-1$
			}
		});

		manageBreakpointsButton = new Button(composite, SWT.CHECK);
		manageBreakpointsButton.setText(Messages.BreakpointsPreferencePage_Manage_breakpoints);
		manageBreakpointsButton.setSelection(getPreferenceStore().getBoolean(
				BreakpointsContextContributor.AUTO_MANAGE_BREAKPOINTS));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(manageBreakpointsButton);

		Group warningGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		warningGroup.setLayout(new GridLayout(1, false));
		warningGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		warningGroup.setText(Messages.BreakpointsPreferencePage_Known_Issues);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(warningGroup);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(warningGroup);

		createWarning(warningGroup, Messages.Breakpoints_closed_projects_warning);
		createWarning(warningGroup, Messages.Breakpoints_locations_warning);
		createWarning(warningGroup, Messages.BreakpointsPreferencePage_unchecking_will_remove);
		return composite;
	}

	private void createWarning(Composite parent, String message) {
		Label warningImage = new Label(parent, SWT.NONE);
		warningImage.setImage(CommonImages.getImage(CommonImages.WARNING));
		Label warningMessage = new Label(parent, SWT.NONE);
		warningMessage.setText(message);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(warningImage);
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(BreakpointsContextContributor.AUTO_MANAGE_BREAKPOINTS,
				manageBreakpointsButton.getSelection());
		return true;
	}

	@Override
	protected void performDefaults() {
		manageBreakpointsButton.setSelection(false);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return DebugUiPlugin.getDefault().getPreferenceStore();
	}
}
