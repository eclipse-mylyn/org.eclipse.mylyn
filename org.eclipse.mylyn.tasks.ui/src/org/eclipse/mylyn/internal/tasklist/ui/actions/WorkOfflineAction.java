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
package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

public class WorkOfflineAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.work.offline";

	public WorkOfflineAction() {
		setId(ID);
		setText("Work Offline");
		setToolTipText("Work Offline");
		setChecked(MylarTaskListPlugin.getMylarCorePrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE));
	}

	@Override
	public void run() {
		boolean on = !MylarTaskListPlugin.getMylarCorePrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.WORK_OFFLINE, on);
	}
}
