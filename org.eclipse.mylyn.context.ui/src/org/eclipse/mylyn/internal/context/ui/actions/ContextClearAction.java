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
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ContextClearAction extends TaskContextAction {

	public static final String ID = "org.eclipse.mylyn.ui.clear.context";

	private static final String ID_ACTION = "org.eclipse.mylyn.context.ui.task.clear";

	public ContextClearAction() {
		setText("Clear");
		setToolTipText("Clear");
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
		AbstractTask task = TaskListView.getFromActivePerspective().getSelectedTask();
		if (task != null) {
			run(task);
		}
	}

	public boolean run(AbstractTask task) {
		boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), "Confirm clear context",
				"Clear the context for the selected task?  This cannot be undone.");
		if (!deleteConfirmed)
			return false;

		ContextCorePlugin.getContextManager().deleteContext(task.getHandleIdentifier());
		TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task, false);
		return true;
	}

}
