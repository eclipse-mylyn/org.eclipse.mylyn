/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskSelectionDialog;
import org.eclipse.mylyn.internal.tasks.ui.commands.AbstractTaskHandler;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class CopyContextHandler extends AbstractTaskHandler {

	private static final String TITLE_DIALOG = "Copy Context";

	@Override
	protected void execute(ExecutionEvent event, ITask sourceTask) throws ExecutionException {
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

		if (result instanceof ITask) {
			ITask targetTask = (ITask) result;
			TasksUi.getTaskActivityManager().deactivateActiveTask();
			if (targetTask.equals(sourceTask)) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						TITLE_DIALOG, "Target task can not be the same as source task.");
			} else {
				IInteractionContext context = ContextCore.getContextStore().cloneContext(
						sourceTask.getHandleIdentifier(), targetTask.getHandleIdentifier());
				if (context == null) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							TITLE_DIALOG, "Source task does not have a context.");
				} else {
					TasksUi.getTaskActivityManager().activateTask(targetTask);
				}
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TITLE_DIALOG, "No target task selected.");
		}
	}

}
