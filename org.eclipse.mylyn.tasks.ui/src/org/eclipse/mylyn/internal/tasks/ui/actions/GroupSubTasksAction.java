/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class GroupSubTasksAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.filter.subtasks";

	private static final String LABEL = "Group Subtasks";

	private final TaskListView view;

	public GroupSubTasksAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		// setImageDescriptor(TasksUiImages.FILTER_COMPLETE);
		setChecked(TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ITasksUiPreferenceConstants.GROUP_SUBTASKS));
	}

	@Override
	public void run() {
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(ITasksUiPreferenceConstants.GROUP_SUBTASKS,
				isChecked());
		// TODO: refresh not getting rid of subtasks

		view.refresh(true);
//		try {
//			view.getViewer().getControl().setRedraw(false);
//			view.getViewer().collapseAll();
//			if (view.isFocusedMode()) {
//				view.getViewer().expandAll();
//			}
//			view.getViewer().refresh();
//		} finally {
//			view.getViewer().getControl().setRedraw(true);
//		}
	}
}
