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
import org.eclipse.mylyn.tasks.core.AbstractTask;
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
		dlg.setTitle("Open Task");
		dlg.setMessage("&Select a task to open (? = any character, * = any String):");
		dlg.setShowExtendedOpeningOptions(true);

		if (dlg.open() != Window.OK) {
			return;
		}

		Object result = dlg.getFirstResult();
		if (result instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) result;
			if (dlg.getOpenInBrowser()) {
				if (task.hasValidUrl()) {
					TasksUiUtil.openUrl(task.getUrl());
				}
			} else {
				TasksUiUtil.refreshAndOpenTaskListElement(task);
			}
		}
	}

}
