/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten
 */
public class FilterCompletedTasksAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.filter.completed"; //$NON-NLS-1$

	private final TaskListView view;

	public FilterCompletedTasksAction(TaskListView view) {
		this.view = view;
		setText(Messages.FilterCompletedTasksAction_Filter_Completed_Tasks);
		setToolTipText(Messages.FilterCompletedTasksAction_Filter_Completed_Tasks);
		setId(ID);
		setImageDescriptor(CommonImages.FILTER_COMPLETE);
		setChecked(TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.contains(ITasksUiPreferenceConstants.FILTER_COMPLETE_MODE));
	}

	@Override
	public void run() {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.FILTER_COMPLETE_MODE, isChecked());
		if (isChecked()) {
			view.addFilter(view.getCompleteFilter());
		} else {
			view.removeFilter(view.getCompleteFilter());
		}
		view.refresh();
	}
}
