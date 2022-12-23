/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Frank Becker
 */
public class NotificationsLinkPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	public NotificationsLinkPreferencesPage() {
		super(Messages.NotificationsLinkPreferencesPage_Mylyn_Notifications);
		noDefaultAndApplyButton();
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		final Shell shell = parent.getShell();
		String text = Messages.NotificationsLinkPreferencesPage_LinkText;
		Link link = new Link(parent, SWT.PUSH);
		link.setText(text);
		link.setEnabled(true);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(shell,
						"org.eclipse.mylyn.commons.notifications.preferencePages.Notifications", null, null); //$NON-NLS-1$
			}
		});
		link.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

		return parent;
	}
}
