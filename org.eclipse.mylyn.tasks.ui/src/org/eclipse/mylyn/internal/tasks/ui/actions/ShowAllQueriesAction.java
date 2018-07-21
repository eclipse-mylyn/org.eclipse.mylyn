/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * @author Steffen Pingel
 */
public class ShowAllQueriesAction extends Action {

	public ShowAllQueriesAction() {
		setText(Messages.ShowAllQueriesAction_Show_All_Queries);
		setToolTipText(Messages.ShowAllQueriesAction_Show_All_Queries_Including_Hidden_Queries);
		setChecked(!TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.FILTER_HIDDEN));
	}

	@Override
	public void run() {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.FILTER_HIDDEN, !isChecked());
	}

}
