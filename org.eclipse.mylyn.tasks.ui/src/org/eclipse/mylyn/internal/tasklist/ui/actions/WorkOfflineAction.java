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
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPrefConstants;

public class WorkOfflineAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.work.offline";
	
	public WorkOfflineAction() {
		setId(ID);
		setText("Work Offline");
		setToolTipText("Work Offline");
		setChecked(MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.WORK_OFFLINE));
	}
	
	@Override
	public void run() {
		boolean on = !MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.WORK_OFFLINE);
		MylarTaskListPlugin.getPrefs().setValue(MylarTaskListPrefConstants.WORK_OFFLINE, on);
	}
}
