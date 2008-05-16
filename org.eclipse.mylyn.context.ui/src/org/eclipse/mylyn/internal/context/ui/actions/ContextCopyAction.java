/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.ui.commands.CopyContextHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskSelectionDialog;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @deprecated use {@link CopyContextHandler} instead
 */
@SuppressWarnings("restriction")
@Deprecated
public class ContextCopyAction extends TaskContextAction {

	private static final String ID_ACTION = "org.eclipse.mylyn.context.ui.task.copy.context.to";

	public ContextCopyAction() {
		setText("Copy to...");
		setToolTipText("Copy Task Context to...");
		setId(ID_ACTION);
		setImageDescriptor(TasksUiImages.CONTEXT_TRANSFER);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		run(getSelectedTask(selection));
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

		TaskSelectionDialog dialog = new TaskSelectionDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell());
		dialog.setNeedsCreateTask(false);
		dialog.setTitle("Select Target Task");
		dialog.setMessage("&Select the target task (? = any character, * = any String):");

		if (dialog.open() != Window.OK) {
			return;
		}

		Object result = dialog.getFirstResult();

		AbstractTask targetTask = null;
		if (result instanceof ITask) {
			targetTask = (AbstractTask) result;
		}

		if (targetTask != null) {
			TasksUi.getTaskActivityManager().deactivateActiveTask();
			IInteractionContext source = ContextCore.getContextManager().loadContext(sourceTask.getHandleIdentifier());

			if (targetTask.equals(sourceTask)) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ITasksUiConstants.TITLE_DIALOG, "Target task can not be the same as source task.");
			} else if (source == null /*!contextFile.exists()*/) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ITasksUiConstants.TITLE_DIALOG, "Source task does not have a context.");
			} else {

				ContextCore.getContextManager().cloneContext(sourceTask.getHandleIdentifier(),
						targetTask.getHandleIdentifier());

				TasksUi.getTaskActivityManager().activateTask(targetTask);
				TaskListView view = TaskListView.getFromActivePerspective();
				if (view != null) {
					view.refresh();
				}
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					ITasksUiConstants.TITLE_DIALOG, "No target task selected.");
		}
	}
}
