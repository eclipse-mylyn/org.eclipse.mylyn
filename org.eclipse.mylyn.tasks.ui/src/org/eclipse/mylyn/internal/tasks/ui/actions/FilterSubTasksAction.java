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

package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class FilterSubTasksAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.filter.subtasks";

	private static final String LABEL = "Filter SubTasks";

	private final TaskListView view;

	public FilterSubTasksAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		// setImageDescriptor(TasksUiImages.FILTER_COMPLETE);
		setChecked(TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.FILTER_SUBTASKS));
	}

	@Override
	public void run() {
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TaskListPreferenceConstants.FILTER_SUBTASKS,
				isChecked());
		// TODO: refresh not getting rid of subtasks
		try {
			view.getViewer().getControl().setRedraw(false);
			view.getViewer().collapseAll();
			if (view.isFocusedMode()) {
				view.getViewer().expandAll();
			} 
			view.getViewer().refresh();
		} finally {
			view.getViewer().getControl().setRedraw(true);
		}
	}
}
