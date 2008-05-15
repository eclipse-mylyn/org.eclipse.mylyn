/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;

/**
 * @author Mik Kersten
 */
public class ToggleDecorateInterestLevelAction extends Action {

	public static final String PREF_ID = "org.eclipse.mylyn.ui.decorators.interest";

	public static final String PREF_INTERSECTION_MODE = "org.eclipse.mylyn.ui.interest.intersection";

	public ToggleDecorateInterestLevelAction() {
		super();
		setText("Decorate Interest");
		setToolTipText("Toggle Interest Level Decorator");

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
