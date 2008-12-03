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

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.ui.commands.CopyContextHandler;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskSelectionDialog;
import org.eclipse.mylyn.tasks.core.ITask;
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

	private static final String ID_ACTION = "org.eclipse.mylyn.context.ui.task.copy.context.to"; //$NON-NLS-1$

	public ContextCopyAction() {
		setText(Messages.ContextCopyAction_Copy_to_);
		setToolTipText(Messages.ContextCopyAction_Copy_Task_Context_to_);
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
					Messages.ContextCopyAction_Copy_Context, Messages.ContextCopyAction_No_source_task_selected);
			return;
		}

		TaskSelectionDialog dialog = new TaskSelectionDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell());
		dialog.setNeedsCreateTask(false);
		dialog.setTitle(Messages.ContextCopyAction_Select_Target_Task);
		dialog.setMessage(Messages.ContextCopyAction_Select_the_target_task__);

		if (dialog.open() != Window.OK) {
			return;
		}

		Object result = dialog.getFirstResult();

		if (result instanceof ITask) {
			ITask targetTask = (ITask) result;
			TasksUi.getTaskActivityManager().deactivateActiveTask();
			if (targetTask.equals(sourceTask)) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.ContextCopyAction_Copy_Context, Messages.ContextCopyAction_Target_task_can_not_be_the_same_as_source_task);
			} else {
				IInteractionContext context = ContextCore.getContextStore().cloneContext(
						sourceTask.getHandleIdentifier(), targetTask.getHandleIdentifier());
				if (context == null) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.ContextCopyAction_Copy_Context, Messages.ContextCopyAction_Source_task_does_not_have_a_context);
				} else {
					TasksUi.getTaskActivityManager().activateTask(targetTask);
				}
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.ContextCopyAction_Copy_Context, Messages.ContextCopyAction_No_target_task_selected);
		}
	}
}
