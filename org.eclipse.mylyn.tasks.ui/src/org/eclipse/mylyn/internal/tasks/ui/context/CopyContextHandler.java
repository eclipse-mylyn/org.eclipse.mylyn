/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.context;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
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

		TaskSelectionDialog dialog = new TaskSelectionDialog(WorkbenchUtil.getShell());
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
				if (TasksUiPlugin.getContextStore().hasContext(targetTask)) {
					MessageDialog dialog2 = new MessageDialog(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getShell(), TITLE_DIALOG, null,
							Messages.CopyContextHandler_SELECTED_TASK_ALREADY_HAS_CONTEXT, MessageDialog.QUESTION,
							new String[] { Messages.CopyContextHandler_Replace, Messages.CopyContextHandler_Merge,
									IDialogConstants.CANCEL_LABEL }, 1);
					action = dialog2.open();
				}

				switch (action) {
				case REPLACE:
					IAdaptable context = TasksUiPlugin.getContextStore().copyContext(sourceTask, targetTask);
					if (context == null) {
						MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								TITLE_DIALOG, Messages.CopyContextHandler_SOURCE_TASK_DOES_HAVE_A_CONTEXT);
						return;
					}
					break;
				case MERGE:
					TasksUiPlugin.getContextStore().mergeContext(sourceTask, targetTask);
					break;
				case CANCEL:
					return;
				}

				TasksUiInternal.activateTaskThroughCommand(targetTask);
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TITLE_DIALOG, Messages.CopyContextHandler_No_target_task_selected);
		}
	}

}
