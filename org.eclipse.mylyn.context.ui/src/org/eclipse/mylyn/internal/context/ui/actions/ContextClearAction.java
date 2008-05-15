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
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.ui.commands.ClearContextHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
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
@Deprecated
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

	public boolean run(ITask task) {
		boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), "Confirm clear context",
				"Clear the context for the selected task?  This cannot be undone.");
		if (!deleteConfirmed) {
			return false;
		}

		ContextCore.getContextManager().deleteContext(task.getHandleIdentifier());
		TasksUiInternal.getTaskList().notifyElementChanged(task);
		return true;
	}

}
