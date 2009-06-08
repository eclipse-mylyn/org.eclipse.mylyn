/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * @author Willian Mitsuda
 */
public class OpenTaskAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(IAction action) {
		TaskSelectionDialog dlg = new TaskSelectionDialog(window.getShell());
		dlg.setTitle(Messages.OpenTaskAction_Open_Task);
		dlg.setMessage(Messages.OpenTaskAction_Select_a_task_to_open__);
		dlg.setShowExtendedOpeningOptions(true);

		if (dlg.open() != Window.OK) {
			return;
		}

		Object result = dlg.getFirstResult();
		if (result instanceof ITask) {
			AbstractTask task = (AbstractTask) result;
			if (dlg.getOpenInBrowser()) {
				if (TasksUiInternal.isValidUrl(task.getUrl())) {
					TasksUiUtil.openUrl(task.getUrl());
				}
			} else {
				TasksUiInternal.refreshAndOpenTaskListElement(task);
			}
		}
	}

}
