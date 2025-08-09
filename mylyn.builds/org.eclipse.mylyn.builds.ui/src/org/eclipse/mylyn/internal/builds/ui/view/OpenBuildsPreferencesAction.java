/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Steffen Pingel
 */
public class OpenBuildsPreferencesAction extends Action {

	public OpenBuildsPreferencesAction() {
		setText(Messages.OpenBuildsPreferencesAction_preferences);
		setToolTipText(Messages.OpenBuildsPreferencesAction_buildPreferences);
	}

	@Override
	public void run() {
		PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(WorkbenchUtil.getShell(),
				BuildsUiInternal.ID_PREFERENCE_PAGE_BUILDS, new String[] { BuildsUiInternal.ID_PREFERENCE_PAGE_BUILDS },
				null);
		dlg.open();
	}

}
