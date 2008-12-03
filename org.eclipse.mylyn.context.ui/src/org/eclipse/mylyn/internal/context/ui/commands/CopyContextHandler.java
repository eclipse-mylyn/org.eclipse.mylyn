/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class CopyContextHandler extends AbstractTaskHandler {

	private static final String TITLE_DIALOG = Messages.CopyContextHandler_Copy_Context;

	@Override
	protected void execute(ExecutionEvent event, ITask sourceTask) throws ExecutionException {
		if (sourceTask == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TITLE_DIALOG, Messages.CopyContextHandler_No_source_task_selected);
			return;
		}

		TaskSelectionDialog dialog = new TaskSelectionDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell());
		dialog.setNeedsCreateTask(false);
		dialog.setTitle(Messages.CopyContextHandler_Select_Target_Task);
		dialog.setMessage(Messages.CopyContextHandler_Select_the_target_task__);

		if (dialog.open() != Window.OK) {
			return;
		}

		Object result = dialog.getFirstResult();

		if (result instanceof ITask) {
			ITask targetTask = (ITask) result;
			TasksUi.getTaskActivityManager().deactivateActiveTask();
			if (targetTask.equals(sourceTask)) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						TITLE_DIALOG, Messages.CopyContextHandler_TARGET_TASK_CON_NOT_BE_THE_SAME_AS_SOURCE_TASK);
			} else {
				IInteractionContext context = ContextCore.getContextStore().cloneContext(
						sourceTask.getHandleIdentifier(), targetTask.getHandleIdentifier());
				if (context == null) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							TITLE_DIALOG, Messages.CopyContextHandler_SOURCE_TASK_DOES_HAVE_A_CONTEXT);
				} else {
					TasksUi.getTaskActivityManager().activateTask(targetTask);
				}
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TITLE_DIALOG, Messages.CopyContextHandler_No_target_task_selected);
		}
	}

}
