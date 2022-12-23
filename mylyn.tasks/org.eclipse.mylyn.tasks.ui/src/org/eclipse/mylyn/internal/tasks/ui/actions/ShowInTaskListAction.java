/*******************************************************************************
 * Copyright (c) 2004, 2009 Willian Mitsuda and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Willian Mitsuda
 */
public class ShowInTaskListAction extends BaseSelectionListenerAction {

	public ShowInTaskListAction() {
		super(Messages.ShowInTaskListAction_Show_In_Task_List);
	}

	@Override
	public void run() {
		IStructuredSelection selection = getStructuredSelection();
		if (!selection.isEmpty()) {
			Object element = selection.getFirstElement();
			if (element instanceof ITask) {
				TasksUiUtil.openTasksViewInActivePerspective();
				TaskListView.getFromActivePerspective().selectedAndFocusTask((ITask) element);
			}
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection.size() == 1 && selection.getFirstElement() instanceof ITask;
	}

}
