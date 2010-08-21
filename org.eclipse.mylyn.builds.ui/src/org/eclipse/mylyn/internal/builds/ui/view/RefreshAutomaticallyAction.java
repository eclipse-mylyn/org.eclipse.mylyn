/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
		setText("Refresh Automatically");
		updateState();
	}

	protected void updateState() {
		setChecked(BuildsUiPlugin.getDefault().getPreferenceStore().getBoolean(
				BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED));
	}

	@Override
	public void run() {
		BuildsUiPlugin.getDefault().getPreferenceStore().setValue(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED,
				isChecked());
	}

}
