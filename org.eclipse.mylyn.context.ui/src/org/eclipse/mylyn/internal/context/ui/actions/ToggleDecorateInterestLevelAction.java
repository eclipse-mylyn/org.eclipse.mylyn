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

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;

/**
 * @author Mik Kersten
 */
public class ToggleDecorateInterestLevelAction extends Action {

	public static final String PREF_ID = "org.eclipse.mylyn.ui.decorators.interest";

	public ToggleDecorateInterestLevelAction() {
		super();
		setImageDescriptor(ContextUiImages.DECORATE_INTEREST);
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
		if (store)
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on);
		ContextUiPlugin.getDefault().getPreferenceStore().setValue(ContextUiPrefContstants.INTERSECTION_MODE, true);
//		ContextCorePlugin.getContextManager().notifyActivePresentationSettingsChange(IMylarContextListener.UpdateKind.UPDATE);
	}
}
