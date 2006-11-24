/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.internal.tasks.ui.AddExistingTaskJob;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Willian Mitsuda
 */
public class OpenRemoteTaskHandler extends AbstractHandler {

	private static final String OPEN_REMOTE_TASK_DIALOG_DIALOG_SETTINGS = "open.remote.task.dialog.settings";

	@Override
	public Object execute(ExecutionEvent evt) throws ExecutionException {
		RemoteTaskSelectionDialog dlg = new RemoteTaskSelectionDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		dlg.setTitle("Open Remote Task");

		IDialogSettings settings = TasksUiPlugin.getDefault().getDialogSettings();
		IDialogSettings dlgSettings = settings.getSection(OPEN_REMOTE_TASK_DIALOG_DIALOG_SETTINGS);
		if (dlgSettings == null) {
			dlgSettings = settings.addNewSection(OPEN_REMOTE_TASK_DIALOG_DIALOG_SETTINGS);
		}
		dlg.setDialogBoundsSettings(dlgSettings, Dialog.DIALOG_PERSISTLOCATION | Dialog.DIALOG_PERSISTSIZE);

		if (dlg.open() == Window.OK) {
			if (dlg.getSelectedTask() != null) {
				openExistingTask(dlg);
			} else {
				openRemoteTask(dlg);
			}
		}
		return null;
	}

	/**
	 * Selected a existing task; handle category move, if needed
	 */
	private void openExistingTask(RemoteTaskSelectionDialog dlg) {
		if (dlg.shouldAddToTaskList()) {
			TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(dlg.getSelectedCategory(),
					dlg.getSelectedTask());
		}
		TaskUiUtil.refreshAndOpenTaskListElement(dlg.getSelectedTask());
	}

	/**
	 * Selected a repository, so try to obtain the task using id
	 */
	private void openRemoteTask(RemoteTaskSelectionDialog dlg) {
		if (dlg.shouldAddToTaskList()) {
			final IProgressService svc = PlatformUI.getWorkbench().getProgressService();
			final AddExistingTaskJob job = new AddExistingTaskJob(dlg.getSelectedTaskRepository(), dlg.getSelectedId(),
					dlg.getSelectedCategory());
			job.schedule();
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				public void run() {
					svc.showInDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), job);
				}

			});
		} else {
			TaskUiUtil.openRepositoryTask(dlg.getSelectedTaskRepository(), dlg.getSelectedId());
		}
	}
}
