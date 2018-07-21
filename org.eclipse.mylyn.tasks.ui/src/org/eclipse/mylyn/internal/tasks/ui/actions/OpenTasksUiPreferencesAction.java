/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.mylyn.internal.tasks.ui.preferences.TasksUiPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Mik Kersten
 */
public class OpenTasksUiPreferencesAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.actions.preferences.open"; //$NON-NLS-1$

	public OpenTasksUiPreferencesAction() {
		setText(Messages.OpenTasksUiPreferencesAction_Preferences_);
		setToolTipText(Messages.OpenTasksUiPreferencesAction_Preferences_);
		setId(ID);
	}

	@Override
	public void run() {
		PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), TasksUiPreferencePage.ID, new String[] { TasksUiPreferencePage.ID }, null);
		dlg.open();
	}
}
