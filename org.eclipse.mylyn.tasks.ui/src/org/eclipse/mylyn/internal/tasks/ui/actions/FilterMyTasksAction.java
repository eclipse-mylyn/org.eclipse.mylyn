/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

public class FilterMyTasksAction extends Action {
	public static final String ID = "org.eclipse.mylyn.tasklist.actions.filter.myTasks"; //$NON-NLS-1$

	private final TaskListView view;

	public FilterMyTasksAction(TaskListView view) {
		this.view = view;
		setText(Messages.FilterMyTasksAction_My_Tasks);
		setToolTipText(Messages.FilterMyTasksAction_My_Tasks);
		setId(ID);
		setImageDescriptor(CommonImages.FILTER_MY_TASKS);
		setChecked(TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.contains(ITasksUiPreferenceConstants.FILTER_MY_TASKS_MODE));
	}

	@Override
	public void run() {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.FILTER_MY_TASKS_MODE, isChecked());
		if (isChecked()) {
			view.addFilter(view.getMyTasksFilter());
		} else {
			view.removeFilter(view.getMyTasksFilter());
		}
		this.view.refresh();
	}
}
