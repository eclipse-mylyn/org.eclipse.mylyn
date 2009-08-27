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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * @author Mik Kersten
 */
public class TaskDeactivateAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.context.deactivate"; //$NON-NLS-1$

	public TaskDeactivateAction() {
		setId(ID);
		setActionDefinitionId("org.eclipse.mylyn.tasks.ui.command.deactivateSelectedTask"); //$NON-NLS-1$
		setText(Messages.TaskDeactivateAction_Deactivate);
		setImageDescriptor(TasksUiImages.CONTEXT_INACTIVE_EMPTY);
	}

	@Deprecated
	public void run(ITask task) {
		TasksUi.getTaskActivityManager().deactivateTask(task);
	}

	@Override
	public void run() {
		TasksUi.getTaskActivityManager().deactivateActiveTask();
	}

}
