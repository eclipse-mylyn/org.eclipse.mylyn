/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class ManageEditorsAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.auto.close";

	private static final String LABEL = "Manage Editors with Activation";

	public ManageEditorsAction() {
		setText(LABEL);
		setId(ID);
		setChecked(MylarTasklistPlugin.getPrefs().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE));
	}

	@Override
	public void run() {
		boolean on = !MylarTasklistPlugin.getPrefs().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE);
		MylarTasklistPlugin.getPrefs().setValue(MylarPlugin.TASKLIST_EDITORS_CLOSE, on);

		if (on) {
			boolean previousValue = WorkbenchPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
			MylarTasklistPlugin.getPrefs().setValue(
					MylarPlugin.TASKLIST_EDITORS_CLOSE, 
					previousValue);
			WorkbenchPlugin.getDefault().getPreferenceStore().setValue(
					IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
					false);
		} else {
			boolean previousValue = MylarTasklistPlugin.getPrefs().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
			WorkbenchPlugin.getDefault().getPreferenceStore().setValue(
					IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
					previousValue);			
		}
	}
}