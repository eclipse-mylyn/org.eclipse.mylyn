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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPrefConstants;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * @author Mik Kersten
 */
public class ManageEditorsAction extends Action {

	public static final String ID = "org.eclipse.mylar.ui.editors.auto.manage";
	
	public ManageEditorsAction() {
		super("Manage Editors with Context", IAction.AS_CHECK_BOX);
		setId(ID);
		update(MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.AUTO_MANAGE_EDITORS));
	}
	
	@Override
	public void run() {
		update(isChecked());
	} 
    
    public void update(boolean on) {
    	setChecked(on);
    	MylarTaskListPlugin.getPrefs().setValue(MylarTaskListPrefConstants.AUTO_MANAGE_EDITORS, on);
		
    	if (on) {
			boolean previousValue = WorkbenchPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
			MylarTaskListPlugin.getPrefs().setValue(
					IPreferenceConstants.REUSE_EDITORS_BOOLEAN, 
					previousValue);
			WorkbenchPlugin.getDefault().getPreferenceStore().setValue(
					IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
					false);
		} else {
			boolean previousValue = MylarTaskListPlugin.getPrefs().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
			WorkbenchPlugin.getDefault().getPreferenceStore().setValue(
					IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
					previousValue);			
		}	
	}
}