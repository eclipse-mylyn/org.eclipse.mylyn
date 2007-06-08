/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
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

	private static final String ACTIVATE_TASK_ACTION_DIALOG_SETTINGS = "activate.task.action.dialog.settings";

	@Override
	public void run(IAction action) {
		TaskSelectionDialog dlg = new TaskSelectionDialog(window.getShell());
		dlg.setTitle("Activate Task");

		IDialogSettings settings = TasksUiPlugin.getDefault().getDialogSettings();
		IDialogSettings dlgSettings = settings.getSection(ACTIVATE_TASK_ACTION_DIALOG_SETTINGS);
		if (dlgSettings == null) {
			dlgSettings = settings.addNewSection(ACTIVATE_TASK_ACTION_DIALOG_SETTINGS);
		}
		dlg.setDialogBoundsSettings(dlgSettings, Dialog.DIALOG_PERSISTLOCATION | Dialog.DIALOG_PERSISTSIZE);

		if (dlg.open() != Window.OK) {
			return;
		}

		Object result = dlg.getFirstResult();
		TaskListManager manager = TasksUiPlugin.getTaskListManager();
		if (result instanceof ITask) {
			ITask task = (ITask) result;
			manager.activateTask(task);
			manager.getTaskActivationHistory().addTask(task);
		}
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().refreshAndFocus(false);
		}
	}

}
