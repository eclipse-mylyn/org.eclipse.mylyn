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
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.ui.commands.ClearContextHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @deprecated use {@link ClearContextHandler} instead
 */
@SuppressWarnings("restriction")
@Deprecated
public class ContextClearAction extends TaskContextAction {

	public static final String ID = "org.eclipse.mylyn.ui.clear.context"; //$NON-NLS-1$

	private static final String ID_ACTION = "org.eclipse.mylyn.context.ui.task.clear"; //$NON-NLS-1$

	public ContextClearAction() {
		setText(Messages.ContextClearAction_Clear);
		setToolTipText(Messages.ContextClearAction_Clear);
		setId(ID_ACTION);
		setImageDescriptor(TasksUiImages.CONTEXT_CLEAR);
	}

	public void init(IViewPart view) {

	}

	@Override
	public void run() {
		run(this);
	}

	public void run(IAction action) {
		ITask task = TaskListView.getFromActivePerspective().getSelectedTask();
		if (task != null) {
			run(task);
		}
	}

	public boolean run(ITask task) {
		boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), Messages.ContextClearAction_Confirm_clear_context,
				Messages.ContextClearAction_Clear_the_context_for_the_selected_task);
		if (!deleteConfirmed) {
			return false;
		}

		ContextCore.getContextManager().deleteContext(task.getHandleIdentifier());
		TasksUiInternal.getTaskList().notifyElementChanged(task);
		return true;
	}

}
