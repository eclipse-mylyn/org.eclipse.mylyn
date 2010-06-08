/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.commands.RemoteTaskSelectionDialog;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskOpenEvent;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskOpenListener;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class OpenRepositoryTaskAction extends Action implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

	private static final String OPEN_REMOTE_TASK_DIALOG_DIALOG_SETTINGS = "org.eclipse.mylyn.tasks.ui.open.remote"; //$NON-NLS-1$

	public void run(IAction action) {
		RemoteTaskSelectionDialog dlg = new RemoteTaskSelectionDialog(WorkbenchUtil.getShell());
		dlg.setTitle(Messages.OpenRepositoryTask_Open_Repository_Task);

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
	}

	/**
	 * Selected a existing task; handle category move, if needed
	 */
	private void openExistingTask(RemoteTaskSelectionDialog dlg) {
		if (dlg.shouldAddToTaskList()) {
			TasksUiInternal.getTaskList().addTask(dlg.getSelectedTask(), dlg.getSelectedCategory());
		}
		TasksUiInternal.refreshAndOpenTaskListElement(dlg.getSelectedTask());
	}

	/**
	 * Selected a repository, so try to obtain the task using taskId
	 */
	private void openRemoteTask(RemoteTaskSelectionDialog dlg) {
		final AbstractTaskCategory finalCategory;
		if (dlg.shouldAddToTaskList()) {
			AbstractTaskCategory category = dlg.getSelectedCategory();
			TaskListView taskListView = TaskListView.getFromActivePerspective();
			if (category == null) {
				Object selectedObject = ((IStructuredSelection) taskListView.getViewer().getSelection()).getFirstElement();
				if (selectedObject instanceof TaskCategory) {
					category = (TaskCategory) selectedObject;
				}
			}
			finalCategory = category;
		} else {
			finalCategory = null;
		}

		String[] selectedIds = dlg.getSelectedIds();
		boolean openSuccessful = false;
		for (String id : selectedIds) {
			boolean opened = TasksUiInternal.openTask(dlg.getSelectedTaskRepository(), id, new TaskOpenListener() {
				@Override
				public void taskOpened(TaskOpenEvent event) {
					if (finalCategory != null && event.getTask() != null) {
						TasksUiInternal.getTaskList().addTask(event.getTask(), finalCategory);
					}
				}
			});
			if (opened) {
				openSuccessful = true;
			}
			if (!openSuccessful) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.OpenRepositoryTask_Open_Task,
						Messages.OpenRepositoryTask_Could_not_find_matching_repository_task);
			}
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void init(IViewPart view) {
	}

}
