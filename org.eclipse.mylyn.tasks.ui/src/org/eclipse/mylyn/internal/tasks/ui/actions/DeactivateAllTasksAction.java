/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * @author wmitsuda
 */
public class DeactivateAllTasksAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

	public void init(IWorkbenchWindow window) {
		// ignore
	}

	@Override
	public void run(IAction action) {
		TasksUiPlugin.getTaskListManager().deactivateAllTasks();
	}

}
