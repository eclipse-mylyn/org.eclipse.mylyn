/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten
 */
public class FilterCompletedTasksAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.filter.completed";

	private static final String LABEL = "Filter Completed Tasks";

	private final TaskListView view;

	public FilterCompletedTasksAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setImageDescriptor(CommonImages.FILTER_COMPLETE);
		setChecked(TasksUiPlugin.getDefault().getPreferenceStore().contains(
				ITasksUiPreferenceConstants.FILTER_COMPLETE_MODE));
	}

	@Override
	public void run() {
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(ITasksUiPreferenceConstants.FILTER_COMPLETE_MODE,
				isChecked());
		if (isChecked()) {
			view.addFilter(view.getCompleteFilter());
		} else {
			view.removeFilter(view.getCompleteFilter());
		}
		this.view.refresh();
	}
}
