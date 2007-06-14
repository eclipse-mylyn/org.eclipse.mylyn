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

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
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

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		AbstractTask task = TaskListView.getFromActivePerspective().getSelectedTask();
		if (task instanceof AbstractTask) {
			boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell(), "Confirm clear context", "Clear context for the selected task?");
			if (!deleteConfirmed)
				return;
			
			if (task.isActive()) {
//				TasksUiPlugin.getTaskListManager().deactivateTask(task);
				ContextCorePlugin.getContextManager().deleteContext((task).getHandleIdentifier());
//				TasksUiPlugin.getTaskListManager().activateTask(task);
			} else {
				ContextCorePlugin.getContextManager().deleteContext((task).getHandleIdentifier());
			}
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task);
		}
	}

}
