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
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * @author Willian Mitsuda
 * @author Mik Kersten
 */
public class ActivateTaskDialogAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(IAction action) {
		TaskSelectionDialogWithRandom dialog = new TaskSelectionDialogWithRandom(window.getShell());
		dialog.setActivateTask(true);
		dialog.setTitle(Messages.ActivateTaskDialogAction_Activate_Task);
		dialog.setMessage(Messages.ActivateTaskDialogAction_Select_a_task_to_activate__);

		if (dialog.open() != Window.OK) {
			return;
		}

		Object result = dialog.getFirstResult();
		if (result instanceof ITask) {
			AbstractTask task = (AbstractTask) result;
			TasksUiInternal.activateTaskThroughCommand(task);
		}
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().refresh();
		}
	}
}
