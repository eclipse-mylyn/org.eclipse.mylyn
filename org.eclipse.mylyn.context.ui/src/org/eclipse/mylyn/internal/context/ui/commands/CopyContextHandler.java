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

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskSelectionDialog;
import org.eclipse.mylyn.internal.tasks.ui.commands.AbstractTaskHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
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
		run(sourceTask);
	}

	public static void run(ITask sourceTask) {
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
				final int REPLACE = 0;
				final int MERGE = 1;
				final int CANCEL = 2;
				int action = REPLACE;
				if (ContextCorePlugin.getContextStore().hasContext(targetTask.getHandleIdentifier())) {
					MessageDialog dialog2 = new MessageDialog(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getShell(), TITLE_DIALOG, null,
							Messages.CopyContextHandler_SELECTED_TASK_ALREADY_HAS_CONTEXT, MessageDialog.QUESTION,
							new String[] { Messages.CopyContextHandler_Replace, Messages.CopyContextHandler_Merge,
									IDialogConstants.CANCEL_LABEL }, 1);
					action = dialog2.open();
				}

				boolean shouldCopyEditorMemento = true;
				switch (action) {
				case REPLACE:
					IInteractionContext context = ContextCore.getContextStore().cloneContext(
							sourceTask.getHandleIdentifier(), targetTask.getHandleIdentifier());
					if (context == null) {
						MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								TITLE_DIALOG, Messages.CopyContextHandler_SOURCE_TASK_DOES_HAVE_A_CONTEXT);
						return;
					}
					break;
				case MERGE:
					ContextCorePlugin.getContextStore().merge(sourceTask.getHandleIdentifier(),
							targetTask.getHandleIdentifier());
					shouldCopyEditorMemento = !ContextUiPlugin.getEditorManager().hasEditorMemento(
							targetTask.getHandleIdentifier());
					break;
				case CANCEL:
					return;
				}

				if (shouldCopyEditorMemento) {
					ContextUiPlugin.getEditorManager().copyEditorMemento(sourceTask.getHandleIdentifier(),
							targetTask.getHandleIdentifier());
				}
				TasksUiInternal.activateTaskThroughCommand(targetTask);
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TITLE_DIALOG, Messages.CopyContextHandler_No_target_task_selected);
		}
	}

}
