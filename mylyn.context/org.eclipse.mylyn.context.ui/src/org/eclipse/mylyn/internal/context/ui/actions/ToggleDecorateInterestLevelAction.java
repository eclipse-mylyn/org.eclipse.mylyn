/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;

/**
 * @author Mik Kersten
 */
public class ToggleDecorateInterestLevelAction extends Action {

	public static final String PREF_ID = "org.eclipse.mylyn.ui.decorators.interest"; //$NON-NLS-1$

	public static final String PREF_INTERSECTION_MODE = "org.eclipse.mylyn.ui.interest.intersection"; //$NON-NLS-1$

	public ToggleDecorateInterestLevelAction() {
		setText(Messages.ToggleDecorateInterestLevelAction_Decorate_Interest);
		setToolTipText(Messages.ToggleDecorateInterestLevelAction_Toggle_Interest_Level_Decorator);

		boolean checked = ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID);
		valueChanged(checked, false);
	}

	@Override
	public void run() {
		valueChanged(isChecked(), true);
	}

	private void valueChanged(final boolean on, boolean store) {
		setChecked(on);
		if (store) {
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on);
		}
		ContextUiPlugin.getDefault().getPreferenceStore().setValue(PREF_INTERSECTION_MODE, true);
	}
}
