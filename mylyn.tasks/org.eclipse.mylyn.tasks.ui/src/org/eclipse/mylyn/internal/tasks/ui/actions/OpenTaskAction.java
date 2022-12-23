/*******************************************************************************
 * Copyright (c) 2004, 2011 Willian Mitsuda and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *     Abner Ballardo - fixes for bug 349003
 *     Tasktop Technologies - improvements
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
 * @author Abner Ballardo
 * @author Steffen Pingel
 */
public class OpenTaskAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(IAction action) {
		TaskSelectionDialog dlg = new TaskSelectionDialog(window.getShell(), true);
		dlg.setTitle(Messages.OpenTaskAction_Open_Task);
		dlg.setMessage(Messages.OpenTaskAction_Select_a_task_to_open__);
		dlg.setShowExtendedOpeningOptions(true);
		dlg.setHelpAvailable(false);

		if (dlg.open() != Window.OK) {
			return;
		}
		if (dlg.getResult() != null) {
			for (Object result : dlg.getResult()) {
				if (result instanceof ITask) {
					AbstractTask task = (AbstractTask) result;
					if (dlg.getOpenInBrowser()) {
						TasksUiUtil.openWithBrowser(task);
					} else {
						TasksUiInternal.refreshAndOpenTaskListElement(task);
					}
				}
			}
		}
	}

}
