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

package org.eclipse.mylar.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;

/**
 * @author Mik Kersten
 */
public class ToggleDecorateInterestLevelAction extends Action {

	public static final String PREF_ID = "org.eclipse.mylar.ui.decorators.interest";

	public ToggleDecorateInterestLevelAction() {
		super();
		setImageDescriptor(MylarImages.DECORATE_INTEREST);
		setToolTipText("Toggle Interest Level Decorator");

		boolean checked = MylarPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID);
		valueChanged(checked, false);
	}

	@Override
	public void run() {
		valueChanged(isChecked(), true);
	}

	private void valueChanged(final boolean on, boolean store) {
		setChecked(on);
		if (store)
			MylarPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on);
		MylarUiPlugin.getDefault().setDecorateInterestMode(on);
		MylarPlugin.getContextManager().notifyActivePresentationSettingsChange(IMylarContextListener.UpdateKind.UPDATE);
	}
}
