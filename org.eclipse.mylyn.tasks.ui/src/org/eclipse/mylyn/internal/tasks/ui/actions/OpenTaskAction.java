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
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
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

	private static final String OPEN_TASK_ACTION_DIALOG_SETTINGS = "open.task.action.dialog.settings";

	private static final String SHOW_IN_BROWSER_SETTING = "show.in.browser.setting";

	@Override
	public void run(IAction action) {
		TaskSelectionDialog dlg = new TaskSelectionDialog(window.getShell());
		dlg.setTitle("Open Task");
		dlg.setShowOpenInBrowserOption(true);

		IDialogSettings settings = TasksUiPlugin.getDefault().getDialogSettings();
		IDialogSettings dlgSettings = settings.getSection(OPEN_TASK_ACTION_DIALOG_SETTINGS);
		if (dlgSettings == null) {
			dlgSettings = settings.addNewSection(OPEN_TASK_ACTION_DIALOG_SETTINGS);
		}
		dlg.setDialogBoundsSettings(dlgSettings, Dialog.DIALOG_PERSISTLOCATION | Dialog.DIALOG_PERSISTSIZE);
		dlg.setOpenInBrowser(dlgSettings.getBoolean(SHOW_IN_BROWSER_SETTING));

		int ret = dlg.open();
		dlgSettings.put(SHOW_IN_BROWSER_SETTING, dlg.getOpenInBrowser());
		if (ret != Window.OK) {
			return;
		}

		Object result = dlg.getFirstResult();
		if (result instanceof ITask) {
			ITask task = (ITask) result;
			if (dlg.getOpenInBrowser()) {
				if (task.hasValidUrl()) {
					TasksUiUtil.openUrl(task.getTaskUrl(), false);
					TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(task);
				}
			} else {
				TasksUiUtil.refreshAndOpenTaskListElement(task);
				TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(task);
			}
		}
	}

}
