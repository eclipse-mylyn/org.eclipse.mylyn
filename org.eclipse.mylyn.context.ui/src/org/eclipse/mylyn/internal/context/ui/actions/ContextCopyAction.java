/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskSelectionDialog;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ContextCopyAction extends TaskContextAction {

	private static final String OPEN_TASK_ACTION_DIALOG_SETTINGS = "open.task.action.dialog.settings";

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		run(getSelectedTask(selection));
	}

	public void run(ITask sourceTask) {
		if (sourceTask == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					ITasksUiConstants.TITLE_DIALOG, "No source task selected.");
			return;
		}

		TaskSelectionDialog dialog = new TaskSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell());
		dialog.setTitle("Select Target Task");

		IDialogSettings settings = TasksUiPlugin.getDefault().getDialogSettings();
		IDialogSettings dlgSettings = settings.getSection(OPEN_TASK_ACTION_DIALOG_SETTINGS);
		if (dlgSettings == null) {
			dlgSettings = settings.addNewSection(OPEN_TASK_ACTION_DIALOG_SETTINGS);
		}
		dialog.setDialogBoundsSettings(dlgSettings, Dialog.DIALOG_PERSISTLOCATION | Dialog.DIALOG_PERSISTSIZE);

		int ret = dialog.open();
		if (ret != Window.OK) {
			return;
		}

		Object result = dialog.getFirstResult();

		ITask targetTask = null;
		if (result instanceof ITask) {
			targetTask = (ITask) result;
		}

		if (targetTask != null) {
			TasksUiPlugin.getTaskListManager().deactivateAllTasks();
			File contextFile = ContextCorePlugin.getContextManager()
					.getFileForContext(sourceTask.getHandleIdentifier());

			if (targetTask.equals(sourceTask)) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ITasksUiConstants.TITLE_DIALOG, "Target task can not be the same as source task.");				
			} else if (!contextFile.exists()) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ITasksUiConstants.TITLE_DIALOG, "Source task does not have a context.");
			} else {
				ContextCorePlugin.getContextManager().transferContextAndActivate(targetTask.getHandleIdentifier(),
						contextFile);
				TasksUiPlugin.getTaskListManager().activateTask(targetTask);
				TaskListView view = TaskListView.getFromActivePerspective();
				if (view != null) {
					view.refreshAndFocus(false);
				}
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					ITasksUiConstants.TITLE_DIALOG, "No target task selected.");
		}
	}
}
