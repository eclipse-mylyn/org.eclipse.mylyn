/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;

/**
 * @author Mik Kersten
 */
public class SynchronizeAutomaticallyAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.actions.synchronize.background";

	private static final String LABEL = "Synchronize Automatically";

	public SynchronizeAutomaticallyAction() {
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setChecked(TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED));
	}

	@Override
	public void run() {
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, isChecked());
	}
}
