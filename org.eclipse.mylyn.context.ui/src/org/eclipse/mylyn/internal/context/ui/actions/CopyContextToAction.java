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

package org.eclipse.mylar.internal.context.ui.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskSelectionDialog;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class CopyContextToAction implements IViewActionDelegate {

	private static final String OPEN_TASK_ACTION_DIALOG_SETTINGS = "open.task.action.dialog.settings";
	
	private ISelection selection;
	
	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		ITask sourceTask = null;
		if (selection instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection)selection).getFirstElement();
			if (selectedObject instanceof ITask) {
				sourceTask = (ITask)selectedObject;
			} else if (selectedObject instanceof AbstractQueryHit) {
				sourceTask = ((AbstractQueryHit)selectedObject).getCorrespondingTask();
			}
		}
		if (sourceTask == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TasksUiPlugin.TITLE_DIALOG, 
					"No source task selected");
		}
		
		TaskSelectionDialog dialog = new TaskSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
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
		if (result instanceof AbstractQueryHit) {
			AbstractQueryHit hit = (AbstractQueryHit) result;
			targetTask = hit.getOrCreateCorrespondingTask();
		} else if (result instanceof ITask) {
			targetTask = (ITask)result;
		}
		
		if (targetTask != null) {
			TasksUiPlugin.getTaskListManager().deactivateAllTasks();
			File contextFile = ContextCorePlugin.getContextManager().getFileForContext(sourceTask.getHandleIdentifier());
			ContextCorePlugin.getContextManager().transferContextAndActivate(targetTask.getHandleIdentifier(), contextFile);
			TasksUiPlugin.getTaskListManager().activateTask(targetTask);
			TaskListView view = TaskListView.getFromActivePerspective();
			if (view != null) {
				view.refreshAndFocus(false);
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TasksUiPlugin.TITLE_DIALOG, 
					"No target task selected");
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
