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
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;

/**
 * @author Steffen Pingel
 */
public class RefreshAutomaticallyAction extends Action {

	public RefreshAutomaticallyAction() {
		setText(Messages.RefreshAutomaticallyAction_refreshAutomatically);
		updateState();
	}

	protected void updateState() {
		setChecked(BuildsUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED));
	}

	@Override
	public void run() {
		BuildsUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED, isChecked());
	}

}
