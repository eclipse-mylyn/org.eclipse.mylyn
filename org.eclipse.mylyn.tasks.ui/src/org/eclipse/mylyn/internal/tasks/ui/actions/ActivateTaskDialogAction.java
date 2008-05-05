/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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
		TaskSelectionDialog dlg = new TaskSelectionDialog(window.getShell());
		dlg.setTitle("Activate Task");
		dlg.setMessage("&Select a task to activate (? = any character, * = any String):");

		if (dlg.open() != Window.OK) {
			return;
		}

		Object result = dlg.getFirstResult();
		if (result instanceof ITask) {
			AbstractTask task = (AbstractTask) result;
			TasksUi.getTaskActivityManager().activateTask(task);
//			manager.getTaskActivationHistory().addTask(task);
		}
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().refresh();
		}
	}

}
